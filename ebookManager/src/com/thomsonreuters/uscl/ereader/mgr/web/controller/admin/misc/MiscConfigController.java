package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.misc;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.validation.Valid;

import com.thomsonreuters.uscl.ereader.core.job.domain.MiscConfig;
import com.thomsonreuters.uscl.ereader.core.job.domain.SimpleRestServiceResponse;
import com.thomsonreuters.uscl.ereader.core.job.service.AppConfigService;
import com.thomsonreuters.uscl.ereader.core.service.MiscConfigSyncService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.InfoMessage;
import com.thomsonreuters.uscl.ereader.mgr.web.service.ManagerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@Slf4j
public class MiscConfigController {

    /** Hosts to push new configuration to, assume a listening REST service to receive the new configuration. */
    private final List<InetSocketAddress> gathererSocketAddrs;
    private final List<InetSocketAddress> generatorSocketAddrs;
    private final List<InetSocketAddress> managerSocketAddrs;

    @Value("${gatherer.context.name}")
    private String gathererContextName;
    @Value("${generator.context.name}")
    private String generatorContextName;
    @Value("${manager.context.name}")
    private String managerContextName;

    @Autowired
    private ManagerService managerService;
    @Autowired
    private AppConfigService appConfigService;
    @Autowired
    private MiscConfigSyncService miscConfigSyncService;
    @Autowired
    @Qualifier("miscConfigFormValidator")
    private Validator validator;

    @Autowired
    public MiscConfigController(
        @Value("${generator.hosts}") final String commaSeparatedGeneratorHostNames,
        @Value("${gatherer.port}") final int gathererPort,
        @Value("${generator.port}") final int generatorPort,
        @Value("${manager.hosts}") final String commaSeparatedManagerHostNames,
        @Value("${manager.port}") final int managerPort)
        throws UnknownHostException {
        generatorSocketAddrs = createSocketAddressList(commaSeparatedGeneratorHostNames, generatorPort);
        gathererSocketAddrs = createSocketAddressList(commaSeparatedGeneratorHostNames, gathererPort);
        managerSocketAddrs = createSocketAddressList(commaSeparatedManagerHostNames, managerPort);
    }

    @InitBinder(MiscConfigForm.FORM_NAME)
    protected void initDataBinder(final WebDataBinder binder) {
        binder.setValidator(validator);
    }

    @RequestMapping(value = WebConstants.MVC_ADMIN_MISC, method = RequestMethod.GET)
    public ModelAndView inboundGet(
        @ModelAttribute(MiscConfigForm.FORM_NAME) final MiscConfigForm form,
        final Model model) {
        final MiscConfig miscConfig = miscConfigSyncService.getMiscConfig();
        form.initialize(miscConfig);
        return new ModelAndView(WebConstants.VIEW_ADMIN_MISC);
    }

    @RequestMapping(value = WebConstants.MVC_ADMIN_MISC, method = RequestMethod.POST)
    public ModelAndView submitMiscConfigForm(
        @ModelAttribute(MiscConfigForm.FORM_NAME) @Valid final MiscConfigForm form,
        final BindingResult errors,
        final Model model) {
        log.debug(form.toString());
        final List<InfoMessage> infoMessages = new ArrayList<>();
        final boolean anyFormValidationErrors = errors.hasErrors();

        if (!anyFormValidationErrors) {
            // Check if the Proview host has changed.  If it has, then reject the change if there are any started or queued jobs.
            final MiscConfig originalMiscConfig = miscConfigSyncService.getMiscConfig();
            if (!originalMiscConfig.getProviewHost().equals(form.getProviewHost())) { // If the Proview hostname has changed
                if (managerService.isAnyJobsStartedOrQueued()) {
                    // Restore the original host name value
                    form.setProviewHost(originalMiscConfig.getProviewHost());
                    infoMessages.add(
                        new InfoMessage(
                            InfoMessage.Type.ERROR,
                            "ProView host name was not changed because there are running or queued jobs."));
                }
            }

            final MiscConfig newMiscConfig = form.createMiscConfig();

            // Persist the changed configuration
            boolean anySaveErrors = false;
            try {
                appConfigService.saveMiscConfig(newMiscConfig); // Persist the changed configuration
                infoMessages.add(new InfoMessage(InfoMessage.Type.SUCCESS, "Successfully saved misc configuration."));
            } catch (final Exception e) {
                anySaveErrors = true;
                final String errorMessage = String.format("Failed to save new misc configuration - %s", e.getMessage());
                log.error(errorMessage, e);
                infoMessages.add(new InfoMessage(InfoMessage.Type.FAIL, errorMessage));
            }

            // If no data persistence errors, then
            // Push/synchronize the new configuration out to all listening ebookGenerator hosts who care about the change.
            if (!anySaveErrors) {
                // Push the config to all generator, gatherer applications (which are assumed to be on the same host)
                for (int i = 0; i < generatorSocketAddrs.size(); i++) {
                    final InetSocketAddress generatorSocketAddr = generatorSocketAddrs.get(i);
                    final InetSocketAddress gathererSocketAddr = gathererSocketAddrs.get(i);

                    pushMiscConfiguration(newMiscConfig, generatorSocketAddr, generatorContextName, infoMessages);
                    pushMiscConfiguration(newMiscConfig, gathererSocketAddr, gathererContextName, infoMessages);
                }

                // Push the config out to the manager applications
                for (final InetSocketAddress managerSocketAddr : managerSocketAddrs) {
                    pushMiscConfiguration(newMiscConfig, managerSocketAddr, managerContextName, infoMessages);
                }
            }
        }
        model.addAttribute(WebConstants.KEY_INFO_MESSAGES, infoMessages);
        return new ModelAndView(WebConstants.VIEW_ADMIN_MISC);
    }

    private void pushMiscConfiguration(
        final MiscConfig miscConfig,
        final InetSocketAddress socketAddr,
        final String contextName,
        final List<InfoMessage> infoMessages) {
        final String errorMessageTemplate = "Failed to push new misc configuration to %s on host %s - %s";
        try {
            final SimpleRestServiceResponse opResponse =
                managerService.pushMiscConfiguration(miscConfig, contextName, socketAddr);
            if (opResponse.isSuccess()) {
                infoMessages.add(
                    new InfoMessage(
                        InfoMessage.Type.SUCCESS,
                        String.format(
                            "Successfully pushed misc configuration to %s on host %s",
                            contextName,
                            socketAddr)));
            } else {
                final String errorMessage =
                    String.format(errorMessageTemplate, contextName, socketAddr, opResponse.getMessage());
                log.error("Response failure: " + errorMessage);
                infoMessages.add(new InfoMessage(InfoMessage.Type.FAIL, errorMessage));
            }
        } catch (final Exception e) {
            final String errorMessage = String.format(errorMessageTemplate, contextName, socketAddr, e.getMessage());
            log.error(errorMessage, e);
            infoMessages.add(new InfoMessage(InfoMessage.Type.FAIL, errorMessage));
        }
    }

    /**
     * Create a list of sockets based on a list of hosts, and a single port number.
     * Note that this is used by more than just this controller as other controllers also create a socket list to push configurations out to.
     * @param commaSeparatedHostNames hosts names that should resolve
     * @param port the application listen port number
     * @return a list of InetSocketAddress
     * @throws UnknownHostException if a host name cannot resolve
     */
    public static List<InetSocketAddress> createSocketAddressList(final String commaSeparatedHostNames, final int port)
        throws UnknownHostException {
        final List<InetSocketAddress> socketAddrs = new ArrayList<>();
        final StringTokenizer hostTokenizer = new StringTokenizer(commaSeparatedHostNames, ", ");
        while (hostTokenizer.hasMoreTokens()) {
            final String hostName = hostTokenizer.nextToken();
            final InetSocketAddress socketAddr = new InetSocketAddress(hostName, port);
            if (socketAddr.isUnresolved()) {
                final String errorMessage = String.format(
                    "Unresolved host socket address <%s>.  Check the environment specific property file and ensure that the CSV generator host names are complete and correct.",
                    socketAddr);
                log.error(errorMessage);
                throw new UnknownHostException(errorMessage);
            }
            socketAddrs.add(socketAddr);
        }
        return socketAddrs;
    }
}
