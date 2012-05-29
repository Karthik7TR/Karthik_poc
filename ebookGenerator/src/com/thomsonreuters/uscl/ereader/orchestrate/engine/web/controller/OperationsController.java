/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.orchestrate.engine.web.controller;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.batch.core.job.flow.FlowJob;
import org.springframework.batch.core.launch.JobExecutionNotRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.thomsonreuters.uscl.ereader.core.CoreConstants;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobThrottleConfig;
import com.thomsonreuters.uscl.ereader.core.job.domain.MiscConfig;
import com.thomsonreuters.uscl.ereader.core.job.domain.SimpleRestServiceResponse;
import com.thomsonreuters.uscl.ereader.core.job.service.AppConfigService;
import com.thomsonreuters.uscl.ereader.core.outage.domain.PlannedOutage;
import com.thomsonreuters.uscl.ereader.orchestrate.engine.domain.PlannedOutageContainer;
import com.thomsonreuters.uscl.ereader.orchestrate.engine.service.EngineService;
import com.thomsonreuters.uscl.ereader.orchestrate.engine.web.WebConstants;


/**
 * URL based Spring Batch job control operations for RESTART and STOP.
 */
@Controller
public class OperationsController {
	private static final Logger log = Logger.getLogger(OperationsController.class);
	private EngineService engineService;
	private MessageSourceAccessor messageSourceAccessor;
	private AppConfigService appConfigService;
	private PlannedOutageContainer plannedOutages;
	private FlowJob job;
	
	public OperationsController(FlowJob job) {
		this.job = job;
	}
	
	/** Maximum number of jobs allowed to run concurrently */

	/**
	 * Handle REST request to restart an currently stopped or failed job.
	 * Only a superuser, or the user who started the job in the first place is allowed to perform this operation.
	 * @param jobExecutionId the job execution ID of the job to restart (required).
	 * @return the view name which will marshal and return the SimpleRestServiceResponse object.
	 */
	@RequestMapping(value=WebConstants.URI_JOB_RESTART, method = RequestMethod.GET)
	public ModelAndView restartJob(@PathVariable Long jobExecutionId, Model model) throws Exception {
		Long jobExecutionIdToRestart = jobExecutionId;
		log.debug("jobExecutionIdToRestart="+jobExecutionIdToRestart);
		
		SimpleRestServiceResponse opResponse = null;
		try {
			PlannedOutage outage = plannedOutages.findOutage(new Date());
			if (outage != null) {
				String message = String.format("Cannot restart job because we are in a service outage until %s", outage.getEndTime().toString()); 
				opResponse = new SimpleRestServiceResponse(jobExecutionIdToRestart, false, message);
			} else {
				Long restartedJobExecutionId = engineService.restartJob(jobExecutionIdToRestart);
				opResponse = new SimpleRestServiceResponse(restartedJobExecutionId);
			}
		} catch (JobInstanceAlreadyCompleteException e) {  // Cannot restart a job that is already finished
			Object[] args = { jobExecutionIdToRestart.toString() };
			String message = messageSourceAccessor.getMessage("err.job.instance.already.complete", args);
			opResponse = new SimpleRestServiceResponse(jobExecutionIdToRestart, false, message);
			log.debug(message);
		} catch (Exception e) {
			opResponse = new SimpleRestServiceResponse(jobExecutionIdToRestart, false, e.getMessage());
			log.debug("Job RESTART exception: " + e);
		}
		model.addAttribute(CoreConstants.KEY_SIMPLE_REST_RESPONSE, opResponse);
		return new ModelAndView(CoreConstants.VIEW_SIMPLE_REST_RESPONSE);
	}
	
	/**
	 * Handle REST request to stop an currently execution job.
	 * Only a superuser, or the user who started the job in the first place is allowed to perform this operation.
	 * @param jobExecutionId the job execution ID of the job to stop (required).
	 * @return the view name which will marshal and return the SimpleRestServiceResponse object.
	 */
	@RequestMapping(value=WebConstants.URI_JOB_STOP, method = RequestMethod.GET)
	public ModelAndView stopJob(@PathVariable Long jobExecutionId, Model model) {
		Long jobExecutionIdToStop = jobExecutionId;
		log.debug("jobExecutionIdToStop="+jobExecutionIdToStop);

		SimpleRestServiceResponse opResponse = null;
		try {
			engineService.stopJob(jobExecutionIdToStop);
			opResponse = new SimpleRestServiceResponse(jobExecutionIdToStop);
		} catch (JobExecutionNotRunningException e) {  // Cannot stop a job that is not running
			Object[] args = { jobExecutionIdToStop.toString() };
			String message = messageSourceAccessor.getMessage("err.job.execution.not.running", args);
			opResponse = new SimpleRestServiceResponse(jobExecutionIdToStop, false, message);
			log.debug(message);
		} catch (Exception e) {
			opResponse = new SimpleRestServiceResponse(jobExecutionIdToStop, false, e.getMessage());
			log.debug("Job STOP exception: " + e);
		}
		model.addAttribute(CoreConstants.KEY_SIMPLE_REST_RESPONSE, opResponse);
		return new ModelAndView(CoreConstants.VIEW_SIMPLE_REST_RESPONSE);
	}

	@RequestMapping(value=CoreConstants.URI_GET_JOB_THROTTLE_CONFIG, method = RequestMethod.GET)
	public ModelAndView getJobThrottleConfig(HttpServletResponse response, Model model) throws Exception {
		log.debug(">>>");
		JobThrottleConfig config = appConfigService.loadJobThrottleConfig();
		model.addAttribute(WebConstants.KEY_JOB_THROTTLE_CONFIG, config);
		return new ModelAndView(WebConstants.VIEW_JOB_THROTTLE_CONFIG_RESPONSE);
	}
	
	@RequestMapping(value=CoreConstants.URI_GET_MISC_CONFIG, method = RequestMethod.GET)
	public ModelAndView getMiscConfig(HttpServletResponse response, Model model) throws Exception {
		log.debug(">>>");
		MiscConfig config = appConfigService.loadMiscConfig();
		model.addAttribute(WebConstants.KEY_MISC_CONFIG, config);
		return new ModelAndView(WebConstants.VIEW_MISC_CONFIG_RESPONSE);
	}
	
	@RequestMapping(value=WebConstants.URI_GET_STEP_NAMES, method = RequestMethod.GET)
	public void getStepNames(HttpServletResponse response, Model model) throws Exception {
		log.debug(">>>");
		ServletOutputStream out = null;
		try {
			Collection<String> stepNames = job.getStepNames();
			StringBuffer csv = new StringBuffer();
			boolean first = true;
			for (String stepName : stepNames) {
				if (!first) {
					csv.append(",");
				}
				first = false;
				csv.append(stepName);
			}
			out = response.getOutputStream();
			out.print(csv.toString());
		} catch (IOException e) {
			log.error(e);
			out.print("Error getting step names");
		}
	}

	@Required
	public void setAppConfigService(AppConfigService service) {
		this.appConfigService = service;
	}
	@Required
	public void setEngineService(EngineService service) {
		this.engineService = service;
	}
	@Required
	public void setMessageSourceAccessor(MessageSourceAccessor accessor) {
		this.messageSourceAccessor = accessor;
	}
	@Required
	public void setPlannedOutages(PlannedOutageContainer container) {
		this.plannedOutages = container;
	}
}
