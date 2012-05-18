package com.thomsonreuters.uscl.ereader.core.web.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.thomsonreuters.uscl.ereader.core.CoreConstants;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobThrottleConfig;
import com.thomsonreuters.uscl.ereader.core.job.domain.MiscConfig;
import com.thomsonreuters.uscl.ereader.core.job.domain.SimpleRestServiceResponse;
import com.thomsonreuters.uscl.ereader.core.service.JobThrottleConfigSyncService;
import com.thomsonreuters.uscl.ereader.core.service.MiscConfigSyncService;

/**
 * REST service provider embeded into all the web applications to receive new application configuration.
 * The configurations are POST'ed as the body of the HTTP request.
 */
@Controller
public class SyncConfigRestController {
	private static final Logger log = Logger.getLogger(SyncConfigRestController.class);
	private MiscConfigSyncService miscConfigSyncService;
	/** May be null if this is not the generator web app application */
	private JobThrottleConfigSyncService jobThrottleConfigSyncService;
	
	/**
	 * Read the current job throttle configuration from the database, and update in-memory state.
	 * This allows for the configuration to be changed on-the-fly in the manager and then pushed out 
	 * to all ebookGenerator instances.
	 * @param newConfiguration the updated configuration as changed on the ebookManager administration page.
	 */
	@RequestMapping(value=CoreConstants.URI_SYNC_MISC_CONFIG, method = RequestMethod.POST)
	public ModelAndView synchronizeMiscConfiguration(@RequestBody MiscConfig config, Model model) throws Exception {
		//log.debug(config);
		SimpleRestServiceResponse opResponse = null;
		try {
			String message = "Successfully synchronized application misc configuration";
			miscConfigSyncService.syncMiscConfig(config);
			opResponse = new SimpleRestServiceResponse(null, true, message);
		} catch (Exception e) {
			String message = "Exception performing misc config data sync - " + e.getMessage();
			opResponse = new SimpleRestServiceResponse(null, false, message);	
			log.error(message, e);
		}
		model.addAttribute(CoreConstants.KEY_SIMPLE_REST_RESPONSE, opResponse);
		return new ModelAndView(CoreConstants.VIEW_SIMPLE_REST_RESPONSE);
	}

	/**
	 * Used only in the ebookGenerator to update the job throttle configuration with the changes
	 * made from the manager web app admin page.
	 */
	@RequestMapping(value=CoreConstants.URI_SYNC_JOB_THROTTLE_CONFIG, method = RequestMethod.POST)
	public ModelAndView synchronizeJobThrottleConfiguration(@RequestBody JobThrottleConfig config, Model model) throws Exception {
		SimpleRestServiceResponse opResponse = null;
		try {
			String message = "Successfully synchronized application job throttle configuration";
			jobThrottleConfigSyncService.syncJobThrottleConfig(config);
			opResponse = new SimpleRestServiceResponse(null, true, message);
		} catch (Exception e) {
			String message = "Exception performing app config data sync - " + e.getMessage();
			opResponse = new SimpleRestServiceResponse(null, false, message);	
			log.error(message, e);
		}
		model.addAttribute(CoreConstants.KEY_SIMPLE_REST_RESPONSE, opResponse);
		return new ModelAndView(CoreConstants.VIEW_SIMPLE_REST_RESPONSE);
	}
	
	/**
	 * Debugging echo service that makes sure the service is up
	 */
	@RequestMapping(value="service/echo/{message}", method = RequestMethod.GET)
	public ModelAndView synchronizeMiscConfiguration(@PathVariable String message, Model model) throws Exception {
		log.debug(">>> " + message);
		SimpleRestServiceResponse opResponse = new SimpleRestServiceResponse(null, true, message);
		model.addAttribute(CoreConstants.KEY_SIMPLE_REST_RESPONSE, opResponse);
		return new ModelAndView(CoreConstants.VIEW_SIMPLE_REST_RESPONSE);
	}	
	
	@Required
	public void setMiscConfigSyncService(MiscConfigSyncService syncService) {
		this.miscConfigSyncService = syncService;
	}
	/**
	 * Note that this is NOT a required service, if it is missing, then no attempt is made to sync the throttle config.
	 * This would be the case for the manager and the gatherer who only care about the misc configurations.
	 * @param syncService
	 */
	public void setJobThrottleConfigSyncService(JobThrottleConfigSyncService syncService) {
		this.jobThrottleConfigSyncService = syncService;
	}
}