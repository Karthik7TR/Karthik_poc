package com.thomsonreuters.uscl.ereader.orchestrate.engine.web.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.thomsonreuters.uscl.ereader.core.CoreConstants;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobThrottleConfig;
import com.thomsonreuters.uscl.ereader.core.job.domain.SimpleRestServiceResponse;
import com.thomsonreuters.uscl.ereader.core.outage.domain.PlannedOutage;
import com.thomsonreuters.uscl.ereader.core.outage.domain.PlannedOutageContainer;
import com.thomsonreuters.uscl.ereader.core.outage.service.OutageService;
import com.thomsonreuters.uscl.ereader.core.service.JobThrottleConfigSyncService;

/**
 * A common REST service provider embedded into all the web applications to receive new application configuration.
 * The configurations are POST'ed as the body of the HTTP request.
 */
@Controller
public class GeneratorSyncRestController {
	private static final Logger log = Logger.getLogger(GeneratorSyncRestController.class);
	/** May be null if this is not the generator web app application */
	private JobThrottleConfigSyncService jobThrottleConfigSyncService;
	private OutageService outageService;

	/**
	 * Used only in the ebookGenerator to update the job throttle configuration with the changes
	 * made from the manager web app admin page.
	 */
	@RequestMapping(value=CoreConstants.URI_SYNC_JOB_THROTTLE_CONFIG, method = RequestMethod.POST)
	public ModelAndView synchronizeJobThrottleConfiguration(@RequestBody JobThrottleConfig config,
															Model model) throws Exception {
		log.debug(">>> " + config);
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
	 * Used only in the ebookGenerator to receive and process pushed outage event notifications.
	 */
	@RequestMapping(value=CoreConstants.URI_SYNC_PLANNED_OUTAGE, method = RequestMethod.POST)
	public ModelAndView synchronizePlannedOutage(@RequestBody PlannedOutage outage, Model model) throws Exception {
		log.debug(">>> " + outage);
		SimpleRestServiceResponse opResponse = null;
		String message = null;
		PlannedOutageContainer plannedOutageContainer = outageService.getPlannedOutageContainer();
		switch (outage.getOperation()) {
			case SAVE:
				plannedOutageContainer.save(outage);
				message = String.format("Successfully saved planned outage with ID %d", outage.getId());
				break;
			case REMOVE:
				boolean wasRemoved = plannedOutageContainer.remove(outage);
				
				message = (wasRemoved) ? String.format("Successfully removed planned outage with ID %d", outage.getId()) :
										 String.format("There was no outage with ID %d, nothing removed" + outage.getId());
				break;
			default: // programming error
				throw new IllegalArgumentException("Unexpected outage operation: " + outage.getOperation());
		}
		opResponse = new SimpleRestServiceResponse(null, true, message);
		model.addAttribute(CoreConstants.KEY_SIMPLE_REST_RESPONSE, opResponse);
		return new ModelAndView(CoreConstants.VIEW_SIMPLE_REST_RESPONSE);
	}
	
	/**
	 * Note that this is NOT a required service, if it is missing, then no attempt is made to sync the throttle config.
	 * This would be the case for the manager and the gatherer who only care about the misc configurations.
	 * @param syncService
	 */
	@Required
	public void setJobThrottleConfigSyncService(JobThrottleConfigSyncService syncService) {
		this.jobThrottleConfigSyncService = syncService;
	}
	@Required
	public void setOutageService(OutageService service) {
		this.outageService = service;
	}
}
