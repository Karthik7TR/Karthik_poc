package com.thomsonreuters.uscl.ereader.core.web.controller;

import javax.servlet.http.HttpSession;

import com.thomsonreuters.uscl.ereader.core.CoreConstants;
import com.thomsonreuters.uscl.ereader.core.job.domain.MiscConfig;
import com.thomsonreuters.uscl.ereader.core.job.domain.SimpleRestServiceResponse;
import com.thomsonreuters.uscl.ereader.core.service.MiscConfigSyncService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * A common REST service provider embedded into all the web applications to receive new application configuration.
 * The configurations are POST'ed as the body of the HTTP request.
 */
@Controller
public class MiscConfigRestController
{
    private static final Logger log = LogManager.getLogger(MiscConfigRestController.class);
    private MiscConfigSyncService miscConfigSyncService;

    /**
     * Update logging log levels from the configuration received in the body of the request.
     * This allows for the configuration to be changed on-the-fly in the manager and then pushed out
     * to all ebookGenerator instances.
     * @param newConfiguration the updated configuration as changed on the ebookManager administration page.
     */
    @RequestMapping(value = CoreConstants.URI_SYNC_MISC_CONFIG, method = RequestMethod.POST)
    public ModelAndView synchronizeMiscConfiguration(
        final HttpSession httpSession,
        @RequestBody final MiscConfig config,
        final Model model)
    {
        //log.debug(">>> " + config);
        SimpleRestServiceResponse opResponse = null;
        try
        {
            final String message = "Successfully synchronized misc configuration";
            miscConfigSyncService.sync(config);
            httpSession.setAttribute(CoreConstants.KEY_PROVIEW_HOST, config.getProviewHost().getHostName());
            log.debug(String.format("%s: %s", message, config.toString()));
            opResponse = new SimpleRestServiceResponse(null, true, message);
        }
        catch (final Exception e)
        {
            final String message = "Failed to sync misc config data - " + e.getMessage();
            opResponse = new SimpleRestServiceResponse(null, false, message);
            log.error(message, e);
        }
        model.addAttribute(CoreConstants.KEY_SIMPLE_REST_RESPONSE, opResponse);
        return new ModelAndView(CoreConstants.VIEW_SIMPLE_REST_RESPONSE);
    }

    /**
     * Debugging echo service that makes sure the service is up
     */
    @RequestMapping(value = "service/{message}/echo.mvc", method = RequestMethod.GET)
    public ModelAndView echo(@PathVariable final String message, final Model model)
    {
        log.debug(">>> " + message);
        final SimpleRestServiceResponse opResponse = new SimpleRestServiceResponse(null, true, message);
        model.addAttribute(CoreConstants.KEY_SIMPLE_REST_RESPONSE, opResponse);
        return new ModelAndView(CoreConstants.VIEW_SIMPLE_REST_RESPONSE);
    }

    @Required
    public void setMiscConfigSyncService(final MiscConfigSyncService syncService)
    {
        miscConfigSyncService = syncService;
    }
}
