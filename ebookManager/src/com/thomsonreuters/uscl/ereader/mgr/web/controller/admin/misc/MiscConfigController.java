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
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
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
public class MiscConfigController
{
    private static final Logger log = LogManager.getLogger(MiscConfigController.class);
    /** Hosts to push new configuration to, assume a listening REST service to receive the new configuration. */
    private List<InetSocketAddress> gathererSocketAddrs;
    private List<InetSocketAddress> generatorSocketAddrs;
    private List<InetSocketAddress> managerSocketAddrs;
    private String gathererContextName;
    private String generatorContextName;
    private String managerContextName;
    private int gathererPort;
    private int generatorPort;
    private int managerPort;
    private ManagerService managerService;
    private AppConfigService appConfigService;
    private MiscConfigSyncService miscConfigSyncService;
    private Validator validator;

    public MiscConfigController(final int gathererPort, final int generatorPort, final int managerPort)
    {
        this.gathererPort = gathererPort;
        this.generatorPort = generatorPort;
        this.managerPort = managerPort;
    }

    @InitBinder(MiscConfigForm.FORM_NAME)
    protected void initDataBinder(final WebDataBinder binder)
    {
        binder.setValidator(validator);
    }

    @RequestMapping(value = WebConstants.MVC_ADMIN_MISC, method = RequestMethod.GET)
    public ModelAndView inboundGet(
        @ModelAttribute(MiscConfigForm.FORM_NAME) final MiscConfigForm form,
        final Model model)
    {
        final MiscConfig miscConfig = miscConfigSyncService.getMiscConfig();
        form.initialize(miscConfig);
        return new ModelAndView(WebConstants.VIEW_ADMIN_MISC);
    }

    @RequestMapping(value = WebConstants.MVC_ADMIN_MISC, method = RequestMethod.POST)
    public ModelAndView submitMiscConfigForm(
        @ModelAttribute(MiscConfigForm.FORM_NAME) @Valid final MiscConfigForm form,
        final BindingResult errors,
        final Model model)
    {
        log.debug(form);
        final List<InfoMessage> infoMessages = new ArrayList<>();
        final boolean anyFormValidationErrors = errors.hasErrors();

        if (!anyFormValidationErrors)
        {
            // Check if the Proview host has changed.  If it has, then reject the change if there are any started or queued jobs.
            final MiscConfig originalMiscConfig = miscConfigSyncService.getMiscConfig();
            if (!originalMiscConfig.getProviewHost().equals(form.getProviewHost()))
            { // If the Proview hostname has changed
                if (managerService.isAnyJobsStartedOrQueued())
                {
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
            try
            {
                appConfigService.saveMiscConfig(newMiscConfig); // Persist the changed configuration
                infoMessages.add(new InfoMessage(InfoMessage.Type.SUCCESS, "Successfully saved misc configuration."));
            }
            catch (final Exception e)
            {
                anySaveErrors = true;
                final String errorMessage = String.format("Failed to save new misc configuration - %s", e.getMessage());
                log.error(errorMessage, e);
                infoMessages.add(new InfoMessage(InfoMessage.Type.FAIL, errorMessage));
            }

            // If no data persistence errors, then
            // Push/synchronize the new configuration out to all listening ebookGenerator hosts who care about the change.
            if (!anySaveErrors)
            {
                // Push the config to all generator, gatherer applications (which are assumed to be on the same host)
                for (int i = 0; i < generatorSocketAddrs.size(); i++)
                {
                    final InetSocketAddress generatorSocketAddr = generatorSocketAddrs.get(i);
                    final InetSocketAddress gathererSocketAddr = gathererSocketAddrs.get(i);

                    pushMiscConfiguration(newMiscConfig, generatorSocketAddr, generatorContextName, infoMessages);
                    pushMiscConfiguration(newMiscConfig, gathererSocketAddr, gathererContextName, infoMessages);
                }

                // Push the config out to the manager applications
                for (final InetSocketAddress managerSocketAddr : managerSocketAddrs)
                {
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
        final List<InfoMessage> infoMessages)
    {
        final String errorMessageTemplate = "Failed to push new misc configuration to %s on host %s - %s";
        try
        {
            final SimpleRestServiceResponse opResponse =
                managerService.pushMiscConfiguration(miscConfig, contextName, socketAddr);
            if (opResponse.isSuccess())
            {
                infoMessages.add(
                    new InfoMessage(
                        InfoMessage.Type.SUCCESS,
                        String.format(
                            "Successfully pushed misc configuration to %s on host %s",
                            contextName,
                            socketAddr)));
            }
            else
            {
                final String errorMessage =
                    String.format(errorMessageTemplate, contextName, socketAddr, opResponse.getMessage());
                log.error("Response failure: " + errorMessage);
                infoMessages.add(new InfoMessage(InfoMessage.Type.FAIL, errorMessage));
            }
        }
        catch (final Exception e)
        {
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
        throws UnknownHostException
    {
        final List<InetSocketAddress> socketAddrs = new ArrayList<>();
        final StringTokenizer hostTokenizer = new StringTokenizer(commaSeparatedHostNames, ", ");
        while (hostTokenizer.hasMoreTokens())
        {
            final String hostName = hostTokenizer.nextToken();
            final InetSocketAddress socketAddr = new InetSocketAddress(hostName, port);
            if (socketAddr.isUnresolved())
            {
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

    /**
     * Generator and Gatherer hosts that receive the REST service push notification when a configuration has changed.
     * It is assumed that the generator and gatherer apps are running on the same host.
     * @param commaSeparatedHostNames a CSV list of valid host names
     */
    @Required
    public void setGeneratorHosts(final String commaSeparatedHostNames) throws UnknownHostException
    {
        generatorSocketAddrs = createSocketAddressList(commaSeparatedHostNames, generatorPort);
        gathererSocketAddrs = createSocketAddressList(commaSeparatedHostNames, gathererPort);
    }

    /**
     * Manager hosts that receive the REST service push notification when a configuration has changed.
     * It is assumed that this is NOT the same host as the generator and gatherer apps.
     * @param commaSeparatedHostNames a CSV list of valid host names
     */
    @Required
    public void setManagerHosts(final String commaSeparatedHostNames) throws UnknownHostException
    {
        managerSocketAddrs = createSocketAddressList(commaSeparatedHostNames, managerPort);
    }

    @Required
    public void setAppConfigService(final AppConfigService service)
    {
        appConfigService = service;
    }

    @Required
    public void setMiscConfigSyncService(final MiscConfigSyncService service)
    {
        miscConfigSyncService = service;
    }

    @Required
    public void setManagerService(final ManagerService service)
    {
        managerService = service;
    }

    @Required
    public void setGeneratorContextName(final String name)
    {
        generatorContextName = name;
    }

    @Required
    public void setGathererContextName(final String name)
    {
        gathererContextName = name;
    }

    @Required
    public void setManagerContextName(final String name)
    {
        managerContextName = name;
    }

    @Required
    public void setValidator(final Validator validator)
    {
        this.validator = validator;
    }
}
