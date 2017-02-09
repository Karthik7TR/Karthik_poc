package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.jobthrottleconfig;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import com.thomsonreuters.uscl.ereader.core.job.domain.JobThrottleConfig;
import com.thomsonreuters.uscl.ereader.core.job.domain.SimpleRestServiceResponse;
import com.thomsonreuters.uscl.ereader.core.job.service.AppConfigService;
import com.thomsonreuters.uscl.ereader.core.service.GeneratorRestClient;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.InfoMessage;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.misc.MiscConfigController;
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
public class JobThrottleConfigController
{
    private static final Logger log = LogManager.getLogger(JobThrottleConfigController.class);
    public static final String KEY_STEP_NAMES = "stepNames";
    /** Hosts to push new configuration to, assume a listening REST service to receive the new configuration. */
    private List<InetSocketAddress> socketAddrs;
    private int generatorPort;
    private AppConfigService appConfigService;
    private ManagerService managerService;
    private GeneratorRestClient generatorRestClient;
    private Validator validator;

    public JobThrottleConfigController(final int generatorPort)
    {
        this.generatorPort = generatorPort;
    }

    @InitBinder(JobThrottleConfigForm.FORM_NAME)
    protected void initDataBinder(final WebDataBinder binder)
    {
        binder.setValidator(validator);
    }

    @RequestMapping(value = WebConstants.MVC_ADMIN_JOB_THROTTLE_CONFIG, method = RequestMethod.GET)
    public ModelAndView inboundGet(
        @ModelAttribute(JobThrottleConfigForm.FORM_NAME) final JobThrottleConfigForm form,
        final Model model)
    {
        final JobThrottleConfig databaseJobThrottleConfig = appConfigService.loadJobThrottleConfig();
        form.initialize(databaseJobThrottleConfig);
        setUpModel(model);
        return new ModelAndView(WebConstants.VIEW_ADMIN_JOB_THROTTLE_CONFIG);
    }

    @RequestMapping(value = WebConstants.MVC_ADMIN_JOB_THROTTLE_CONFIG, method = RequestMethod.POST)
    public ModelAndView submitForm(
        @ModelAttribute(JobThrottleConfigForm.FORM_NAME) @Valid final JobThrottleConfigForm form,
        final BindingResult errors,
        final Model model)
    {
        final List<InfoMessage> infoMessages = new ArrayList<>();
        if (!errors.hasErrors())
        {
            boolean anySaveErrors = false;
            // Persist the changed Throttle configuration
            try
            {
                final JobThrottleConfig jobThrottleConfig = form.getJobThrottleConfig();
                appConfigService.saveJobThrottleConfig(jobThrottleConfig);
                infoMessages
                    .add(new InfoMessage(InfoMessage.Type.SUCCESS, "Successfully saved throttle configuration."));
            }
            catch (final Exception e)
            {
                anySaveErrors = true;
                final String errorMessage =
                    String.format("Failed to save new throttle configuration - %s", e.getMessage());
                log.error(errorMessage, e);
                infoMessages.add(new InfoMessage(InfoMessage.Type.FAIL, errorMessage));
            }

            // If no data persistence errors, then
            // Sync the new configuration out to all listening ebookGenerator hosts who care about the change.
            if (!anySaveErrors)
            {
                InetSocketAddress currentSocketAddr = null;
                final String errorMessageTemplate =
                    "Failed to push new job throttle configuration to host socket %s - %s";
                // Fetch the complete current state of the application configuration
                for (final InetSocketAddress socketAddr : socketAddrs)
                {
                    try
                    {
                        currentSocketAddr = socketAddr;
                        // Notify the generator app to pick up the changes
                        final JobThrottleConfig jobThrottleConfig = appConfigService.loadJobThrottleConfig();
                        final SimpleRestServiceResponse opResponse =
                            managerService.pushJobThrottleConfiguration(jobThrottleConfig, socketAddr);
                        if (opResponse.isSuccess())
                        {
                            infoMessages.add(
                                new InfoMessage(
                                    InfoMessage.Type.SUCCESS,
                                    String.format(
                                        "Successfully pushed new throttle configuration to host %s",
                                        socketAddr)));
                        }
                        else
                        {
                            final String errorMessage =
                                String.format(errorMessageTemplate, socketAddr, opResponse.getMessage());
                            log.error("REST response failure: " + errorMessage);
                            infoMessages.add(new InfoMessage(InfoMessage.Type.FAIL, errorMessage));
                        }
                    }
                    catch (final Exception e)
                    {
                        final String errorMessage =
                            String.format(errorMessageTemplate, currentSocketAddr, e.getMessage());
                        log.error("Exception occurred: " + errorMessage, e);
                        infoMessages.add(new InfoMessage(InfoMessage.Type.FAIL, errorMessage));
                    }
                }
            }
        }
        model.addAttribute(WebConstants.KEY_INFO_MESSAGES, infoMessages);
        setUpModel(model);
        return new ModelAndView(WebConstants.VIEW_ADMIN_JOB_THROTTLE_CONFIG);
    }

    private void setUpModel(final Model model)
    {
        final List<String> stepNames = generatorRestClient.getStepNames();
        model.addAttribute(KEY_STEP_NAMES, stepNames);
    }

    /**
     * Hosts that receive the REST service push notification that the job throttle configuration has changed.
     * @param commaSeparatedHostNames a CSV list of valid host names
     */
    @Required
    public void setGeneratorHosts(final String commaSeparatedHostNames) throws UnknownHostException
    {
        socketAddrs = MiscConfigController.createSocketAddressList(commaSeparatedHostNames, generatorPort);
    }

    @Required
    public void setAppConfigService(final AppConfigService service)
    {
        appConfigService = service;
    }

    @Required
    public void setManagerService(final ManagerService service)
    {
        managerService = service;
    }

    @Required
    public void setGeneratorRestClient(final GeneratorRestClient client)
    {
        generatorRestClient = client;
    }

    @Required
    public void setValidator(final Validator validator)
    {
        this.validator = validator;
    }
}
