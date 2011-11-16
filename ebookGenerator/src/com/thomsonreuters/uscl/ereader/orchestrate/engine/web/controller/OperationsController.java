package com.thomsonreuters.uscl.ereader.orchestrate.engine.web.controller;

import java.net.URL;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.thomsonreuters.uscl.ereader.orchestrate.engine.EngineManagerImpl;
import com.thomsonreuters.uscl.ereader.orchestrate.engine.web.WebConstants;

/**
 * URL based Spring Batch job control operations for RESTART and STOP.
 */
@Controller
public class OperationsController {
	private static final Logger log = Logger.getLogger(OperationsController.class);
	
	@Autowired
	private EngineManagerImpl engineUtils;
	@Resource(name="dashboardContextUrl")
	private URL dashboardContextUrl;

	// RESTART ===========================================================================================================
	@RequestMapping(value=WebConstants.URL_JOB_RESTART, method = RequestMethod.GET)
	public ModelAndView restartJobExecution(@RequestParam Long jobExecutionId, Model model) throws Exception {
		Long jobExecutionIdToRestart = jobExecutionId;
log.debug("jobExecutionIdToRestart="+jobExecutionIdToRestart);		
		try {
			Long restartedJobExecutionId = engineUtils.restartJob(jobExecutionIdToRestart);
log.debug("restartedJobExecutionId="+restartedJobExecutionId);

			// Redirect back to the Dashboard Job Execution Details page to view the details of the restarted job
			String dashboardDetailsUrl = getDashboardJobExecutionDetailsUrl()+"?"+WebConstants.KEY_JOB_EXECUTION_ID+"="+restartedJobExecutionId;
			return new ModelAndView(new RedirectView(dashboardDetailsUrl));
		} catch (Exception e) {
			log.error("Failed to restart job with execution ID=" + jobExecutionIdToRestart, e);
			populateModel(model, jobExecutionIdToRestart, e, "restart");
			return new ModelAndView(WebConstants.VIEW_JOB_OPERATION_FAILURE);
		}
	}
	
	// STOP ===========================================================================================================
	@RequestMapping(value=WebConstants.URL_JOB_STOP, method = RequestMethod.GET)
	public ModelAndView stopJobExecution(@RequestParam Long jobExecutionId, Model model) throws Exception {
		Long jobExecutionIdToStop = jobExecutionId;
log.debug("jobExecutionIdToStop="+jobExecutionIdToStop);
		try {
			engineUtils.stopJob(jobExecutionIdToStop);
log.debug("Stopped Job: " + jobExecutionIdToStop);

			// Redirect back to the Dashboard Job Execution Details page to view the details of the stopped job
			String dashboardDetailsUrl = getDashboardJobExecutionDetailsUrl()+"?"+WebConstants.KEY_JOB_EXECUTION_ID+"="+jobExecutionIdToStop;
			return new ModelAndView(new RedirectView(dashboardDetailsUrl));
		} catch (Exception e) {
			log.error("Failed to stop job with execution ID=" + jobExecutionIdToStop, e);
			populateModel(model, jobExecutionIdToStop, e, "stop");
			return new ModelAndView(WebConstants.VIEW_JOB_OPERATION_FAILURE);
		}
	}
	
	private void populateModel(Model model, Long jobExecutionId, Exception e, String action) {
		String dashboardDetailsUrl = getDashboardJobExecutionDetailsUrl()+"?"+WebConstants.KEY_JOB_EXECUTION_ID+"=";
		model.addAttribute("dashboardDetailsUrl", dashboardDetailsUrl+jobExecutionId);
		model.addAttribute(WebConstants.KEY_JOB_EXECUTION_ID, jobExecutionId);
		model.addAttribute(WebConstants.KEY_ERROR_MESSAGE, e.getMessage());
		model.addAttribute(WebConstants.KEY_STACK_TRACE, EngineManagerImpl.getStackTrace(e));
		model.addAttribute(WebConstants.KEY_ACTION, action);
	}
	
	private String getDashboardJobExecutionDetailsUrl() {
		return dashboardContextUrl.toString()+"/jobExecutionDetails.mvc";
	}
}
