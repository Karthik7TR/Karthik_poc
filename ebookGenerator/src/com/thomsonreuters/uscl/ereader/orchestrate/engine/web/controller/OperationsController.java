/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.orchestrate.engine.web.controller;

import java.net.URL;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.thomsonreuters.uscl.ereader.orchestrate.engine.service.EngineService;
import com.thomsonreuters.uscl.ereader.orchestrate.engine.service.EngineServiceImpl;
import com.thomsonreuters.uscl.ereader.orchestrate.engine.web.WebConstants;

/**
 * URL based Spring Batch job control operations for RESTART and STOP.
 */
@Controller
public class OperationsController {
	private static final Logger log = Logger.getLogger(OperationsController.class);
	
	private EngineService engineService;
	private URL dashboardContextUrl;
	private MessageSourceAccessor messageSourceAccessor;

	/**
	 * Attempt to restart a currently stopped job execution.
	 * Makes no attempt to verify that the specified jobExecutionId is in a state that allows it to be restarted.
	 * If the job cannot be restarted due to an improper state, the user is directed to an error page describing the reason for the failure.
	 * @param jobExecutionId id of the job execution to restart
	 */
	@RequestMapping(value=WebConstants.URL_JOB_RESTART, method = RequestMethod.GET)
	public ModelAndView restartJobExecution(@RequestParam Long jobExecutionId, Model model) throws Exception {
		Long jobExecutionIdToRestart = jobExecutionId;
log.debug("jobExecutionIdToRestart="+jobExecutionIdToRestart);		
		try {
			Long restartedJobExecutionId = engineService.restartJob(jobExecutionIdToRestart);
log.debug("restartedJobExecutionId="+restartedJobExecutionId);

			// Redirect back to the Dashboard Job Execution Details page to view the details of the restarted job
			String dashboardDetailsUrl = getDashboardJobExecutionDetailsUrl(restartedJobExecutionId);
			return new ModelAndView(new RedirectView(dashboardDetailsUrl));
		} catch (Exception e) {
			log.error("Failed to restart job with execution ID=" + jobExecutionIdToRestart, e);
			populateModel(model, jobExecutionIdToRestart, e, messageSourceAccessor.getMessage("label.restart"));
			return new ModelAndView(WebConstants.VIEW_JOB_OPERATION_FAILURE);
		}
	}
	
	/**
	 * Attempt to Stop a currently started job execution.
	 * Makes no attempt to verify that the specified jobExecutionId is in a state that allows it to be stopped.
	 * If the job cannot be stopped due to an improper state, the user is directed to an error page describing the reason for the failure.
	 * @param jobExecutionId id of the job execution to stop
	 */
	@RequestMapping(value=WebConstants.URL_JOB_STOP, method = RequestMethod.GET)
	public ModelAndView stopJobExecution(@RequestParam Long jobExecutionId, Model model) {
		Long jobExecutionIdToStop = jobExecutionId;
log.debug("jobExecutionIdToStop="+jobExecutionIdToStop);
		try {
			engineService.stopJob(jobExecutionIdToStop);
log.debug("Stopped Job: " + jobExecutionIdToStop);

			// Redirect back to the Dashboard Job Execution Details page to view the details of the stopped job
			String dashboardDetailsUrl = getDashboardJobExecutionDetailsUrl(jobExecutionIdToStop);
			return new ModelAndView(new RedirectView(dashboardDetailsUrl));
		} catch (Exception e) {
			log.error("Failed to stop job with execution ID=" + jobExecutionIdToStop, e);
			populateModel(model, jobExecutionIdToStop, e, messageSourceAccessor.getMessage("label.stop"));
			return new ModelAndView(WebConstants.VIEW_JOB_OPERATION_FAILURE);
		}
	}
	
	private void populateModel(Model model, Long jobExecutionId, Exception e, String action) {
		model.addAttribute("dashboardDetailsUrl", getDashboardJobExecutionDetailsUrl(jobExecutionId));
		model.addAttribute(WebConstants.KEY_JOB_EXECUTION_ID, jobExecutionId);
		model.addAttribute(WebConstants.KEY_ERROR_MESSAGE, e.getMessage());
		model.addAttribute(WebConstants.KEY_STACK_TRACE, EngineServiceImpl.getStackTrace(e));
		model.addAttribute(WebConstants.KEY_ACTION, action);
	}
	
	/**
	 * Fetch the complete URL for the Job Execution Details page of the dashboard web application, less the required query string.
	 * @return the Dashboard Job execution details page url with no query string.
	 */
	private String getDashboardJobExecutionDetailsUrl(long jobExecutionId) {
		return dashboardContextUrl.toString()+"/jobExecutionDetails.mvc?"+WebConstants.KEY_JOB_EXECUTION_ID+"="+jobExecutionId;
	}
	@Required
	public void setEngineService(EngineService engineService) {
		this.engineService = engineService;
	}
	@Required
	public void setDashboardContextUrl(URL dashboardContextUrl) {
		this.dashboardContextUrl = dashboardContextUrl;
	}
	@Required
	public void setMessageSourceAccessor(MessageSourceAccessor messageSourceAccessor) {
		this.messageSourceAccessor = messageSourceAccessor;
	}
}
