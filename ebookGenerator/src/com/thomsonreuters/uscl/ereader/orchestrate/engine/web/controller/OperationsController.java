/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.orchestrate.engine.web.controller;

import org.apache.log4j.Logger;
import org.springframework.batch.core.launch.JobExecutionNotRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.thomsonreuters.uscl.ereader.core.job.domain.JobOperationResponse;
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
	private ThreadPoolTaskExecutor springBatchTaskExecutor;
	/** Maximum number of jobs allowed to run concurrently */
	private int maximumConcurrentJobs;

	/**
	 * Handle REST request to restart an currently stopped or failed job.
	 * Only a superuser, or the user who started the job in the first place is allowed to perform this operation.
	 * @param jobExecutionId the job execution ID of the job to restart (required).
	 * @return the view name which will marshal and return the JobOperationResponse object.
	 */
	@RequestMapping(value=WebConstants.URI_JOB_RESTART, method = RequestMethod.GET)
	public ModelAndView restartJob(@PathVariable Long jobExecutionId, Model model) throws Exception {
		Long jobExecutionIdToRestart = jobExecutionId;
		log.debug("jobExecutionIdToRestart="+jobExecutionIdToRestart);
		JobOperationResponse opResponse = null;

		// Do not restart the job if the max number of concurrent jobs are already running
		if (springBatchTaskExecutor.getActiveCount() < maximumConcurrentJobs) {
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
		} else {
			Object[] args = { jobExecutionIdToRestart.toString(), maximumConcurrentJobs }; 
			String errorMessage = messageSourceAccessor.getMessage("err.restart.max.concurrent", args);
			log.warn(errorMessage);
			opResponse = new JobOperationResponse(jobExecutionIdToRestart, false, errorMessage);
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
		log.debug("jobExecutionIdToStop="+jobExecutionIdToStop);
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

	@Required
	public void setEngineService(EngineService engineService) {
		this.engineService = engineService;
	}
	@Required
	public void setMessageSourceAccessor(MessageSourceAccessor accessor) {
		this.messageSourceAccessor = accessor;
	}
	@Required
	public void setSpringBatchTaskExecutor(ThreadPoolTaskExecutor taskExecutor) {
		this.springBatchTaskExecutor = taskExecutor;
	}
	@Required
	public void setMaximumConcurrentJobs(int maxJobs) {
		this.maximumConcurrentJobs = maxJobs;
	}
}
