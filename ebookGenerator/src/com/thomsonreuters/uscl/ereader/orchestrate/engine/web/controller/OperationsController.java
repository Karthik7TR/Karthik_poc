/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.orchestrate.engine.web.controller;

import java.io.IOException;
import java.util.Collection;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.thomsonreuters.uscl.ereader.core.job.domain.JobOperationResponse;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobThrottleConfig;
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
	private JobThrottleConfig currentJobThrottleConfig;
	private FlowJob job;
	
	public OperationsController(JobThrottleConfig config, FlowJob job) {
		this.currentJobThrottleConfig = config;
		this.job = job;
	}
	
	/** Maximum number of jobs allowed to run concurrently */

	/**
	 * Handle REST request to restart an currently stopped or failed job.
	 * Only a superuser, or the user who started the job in the first place is allowed to perform this operation.
	 * @param jobExecutionId the job execution ID of the job to restart (required).
	 * @return the view name which will marshal and return the JobOperationResponse object.
	 */
	@RequestMapping(value=WebConstants.URI_JOB_RESTART, method = RequestMethod.GET)
	public ModelAndView restartJob(@PathVariable Long jobExecutionId, Model model) throws Exception {
		Long jobExecutionIdToRestart = jobExecutionId;
//		log.debug("jobExecutionIdToRestart="+jobExecutionIdToRestart);
		
		JobOperationResponse opResponse = null;
		try {
			Long restartedJobExecutionId = engineService.restartJob(jobExecutionIdToRestart);
			opResponse = new JobOperationResponse(restartedJobExecutionId);
		} catch (JobInstanceAlreadyCompleteException e) {  // Cannot restart a job that is already finished
			Object[] args = { jobExecutionIdToRestart.toString() };
			String errorMessage = messageSourceAccessor.getMessage("err.job.instance.already.complete", args);
			log.debug(errorMessage);
			opResponse = new JobOperationResponse(jobExecutionIdToRestart, false, errorMessage);
		} catch (Exception e) {
			log.debug("Job RESTART exception: " + e);
			opResponse = new JobOperationResponse(jobExecutionIdToRestart, false, e.getMessage());
		}
		model.addAttribute(WebConstants.KEY_JOB_OPERATION_RESPONSE, opResponse);
		return new ModelAndView(WebConstants.VIEW_JOB_OPERATION_RESPONSE);
	}
	
	/**
	 * Handle REST request to stop an currently execution job.
	 * Only a superuser, or the user who started the job in the first place is allowed to perform this operation.
	 * @param jobExecutionId the job execution ID of the job to stop (required).
	 * @return the view name which will marshal and return the JobOperationResponse object.
	 */
	@RequestMapping(value=WebConstants.URI_JOB_STOP, method = RequestMethod.GET)
	public ModelAndView stopJob(@PathVariable Long jobExecutionId, Model model) {
		Long jobExecutionIdToStop = jobExecutionId;
//		log.debug("jobExecutionIdToStop="+jobExecutionIdToStop);

		JobOperationResponse opResponse = null;
		try {
			engineService.stopJob(jobExecutionIdToStop);
			opResponse = new JobOperationResponse(jobExecutionIdToStop);
		} catch (JobExecutionNotRunningException e) {  // Cannot stop a job that is not running
			Object[] args = { jobExecutionIdToStop.toString() };
			String errorMessage = messageSourceAccessor.getMessage("err.job.execution.not.running", args);
			log.debug(errorMessage);
			opResponse = new JobOperationResponse(jobExecutionIdToStop, false, errorMessage);
		} catch (Exception e) {
			log.debug("Job STOP exception: " + e);
			opResponse = new JobOperationResponse(jobExecutionIdToStop, false, e.getMessage());
		}
		model.addAttribute(WebConstants.KEY_JOB_OPERATION_RESPONSE, opResponse);
		return new ModelAndView(WebConstants.VIEW_JOB_OPERATION_RESPONSE);
	}
	
	/**
	 * Receive a new job throttle configuration from the ebookManager.
	 * This allows for the configuration to be changed on-the-fly in the manager and then pushed out 
	 * to all ebookGenerator instances.
	 * @param newConfiguration the updated configuration as changed on the ebookManager administration page.
	 */
	@RequestMapping(value=WebConstants.URI_UPDATE_JOB_THROTTLE_CONFIG, method = RequestMethod.POST)
	public ModelAndView synchronizeJobThrottleConfiguration(@RequestBody JobThrottleConfig newConfiguration, Model model) {
		log.info("Received: " + newConfiguration);
		JobOperationResponse opResponse = null;
		try {
			currentJobThrottleConfig.sync(newConfiguration);
			// Update the task executor with the new core thread pool size which limits the number of concurrent jobs that can run
			engineService.setTaskExecutorCoreThreadPoolSize(currentJobThrottleConfig.getCoreThreadPoolSize());
			opResponse = new JobOperationResponse(null, true, "Successfully synchronized job throttle configuration.");
		} catch (Exception e) {
			log.debug("Exception performing data sync: " + e);
			opResponse = new JobOperationResponse(null, false, "Error performing configuration sync - " + e.getMessage());	
		}		
		model.addAttribute(WebConstants.KEY_JOB_OPERATION_RESPONSE, opResponse);
		return new ModelAndView(WebConstants.VIEW_JOB_OPERATION_RESPONSE);
	}

	
	@RequestMapping(value=WebConstants.URI_GET_STEP_NAMES, method = RequestMethod.GET)
	public void getStepNames(HttpServletResponse response, Model model) throws Exception {
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
	public void setEngineService(EngineService engineService) {
		this.engineService = engineService;
	}
	@Required
	public void setMessageSourceAccessor(MessageSourceAccessor accessor) {
		this.messageSourceAccessor = accessor;
	}
}
