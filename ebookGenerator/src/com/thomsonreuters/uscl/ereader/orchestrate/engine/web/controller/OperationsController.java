/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.orchestrate.engine.web.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
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
		try {
			Long restartedJobExecutionId = engineService.restartJob(jobExecutionIdToRestart);
			opResponse = new JobOperationResponse(restartedJobExecutionId);
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
		log.debug("jobExecutionIdToStop="+jobExecutionIdToStop);
		JobOperationResponse opResponse = null;
		try {
			engineService.stopJob(jobExecutionIdToStop);
			opResponse = new JobOperationResponse(jobExecutionIdToStop);
		} catch (Exception e) {
			log.debug("Job STOP exception: " + e);
			opResponse = new JobOperationResponse(jobExecutionIdToStop, false, e.getMessage());
		}
		model.addAttribute(WebConstants.KEY_JOB_OPERATION_RESPONSE, opResponse);
		return new ModelAndView(WebConstants.VIEW_JOB_OPERATION_RESPONSE);
	}
	
	/**
	 * Verify the requesting user is authorized to stop the job.
	 * This is programmatic security logic because we need to check the job starting user as well as a role.
	 * @param jobExecutionId job execution to be stopped or restarted.
	 * @return
	 */
//	private JobOperationResponse performUserSecurityCheck(Long jobExecutionId) {
//		JobExecution jobExecution = jobExplorer.getJobExecution(jobExecutionId);
//		if (jobExecution == null) {
//			String mesg = "No job execution was found for ID: " + jobExecutionId;
//			log.debug(mesg);
//			return new JobOperationResponse(jobExecutionId, false, mesg);
//		}
//		String usernameThatStartedTheJob = null;
//		PublishingStats stats = publishingStatsService.findPublishingStatsByJobId(jobExecution.getJobId());
//		if (stats != null) {
//			usernameThatStartedTheJob = stats.getJobSubmitterName();
//		} else {
//			String mesg = "Unable to determine the user who started the job";
//			log.warn(mesg);
//		}
//		
///* TODO: uncomment this if block once CAS REST security integration is worked out...
//		if (!Security.isUserAuthorizedToStopOrRestartBatchJob(usernameThatStartedTheJob)) {
//			return new JobOperationResponse(jobExecutionId, false, 
//			String.format("You must be either the user who started the job (%s), or a SUPERUSER in order to stop or restart a job",
//							usernameThatStartedTheJob));
//		}
//*/
//		
//		return null;
//	}

	@Required
	public void setEngineService(EngineService engineService) {
		this.engineService = engineService;
	}
}
