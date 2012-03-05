/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.job.details;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.thomsonreuters.uscl.ereader.core.job.domain.JobInstanceBookInfo;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobOperationResponse;
import com.thomsonreuters.uscl.ereader.core.job.service.JobService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.InfoMessage;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.job.list.JobExecutionVdo;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.job.list.JobListController;
import com.thomsonreuters.uscl.ereader.mgr.web.service.ManagerService;

/**
 * Controller for the Job Execution Details page.
 */
@Controller
public class JobExecutionController {
	private static final Logger log = Logger.getLogger(JobExecutionController.class);
	
	private JobService jobService;
	private ManagerService managerService;
	private Validator validator;
	private MessageSourceAccessor messageSourceAccessor;
	private JobListController jobSummaryController;
	
	@InitBinder(JobExecutionForm.FORM_NAME)
	protected void initDataBinder(WebDataBinder binder) {
		binder.setValidator(validator);
	}
	
	@RequestMapping(value=WebConstants.MVC_JOB_EXECUTION_DETAILS, method = RequestMethod.GET)
	public ModelAndView doDisplayJobExecutionDetails(HttpServletRequest request,
							  @RequestParam Long jobExecutionId,
							  @ModelAttribute(JobExecutionForm.FORM_NAME) JobExecutionForm form,
							  Model model) throws Exception {
		JobExecution jobExecution = (jobExecutionId != null) ? jobService.findJobExecution(jobExecutionId) : null;
		populateModel(model, jobExecution);
		return new ModelAndView(WebConstants.VIEW_JOB_EXECUTION_DETAILS);
	}
	
	@RequestMapping(value=WebConstants.MVC_JOB_EXECUTION_DETAILS_POST, method = RequestMethod.POST)
	public ModelAndView doPost(@ModelAttribute(JobExecutionForm.FORM_NAME) @Valid JobExecutionForm form,
							   BindingResult bindingResult,
							   Model model) throws Exception {
		JobExecution jobExecution = null;
		if (!bindingResult.hasErrors()) {
			jobExecution = jobService.findJobExecution(form.getJobExecutionId());
			if (jobExecution == null) {
				String[] args = { form.getJobExecutionId().toString() };
				bindingResult.reject("executionId.not.found", args, "Job execution not found");
			}
		}
		populateModel(model, jobExecution);
		return new ModelAndView(WebConstants.VIEW_JOB_EXECUTION_DETAILS);
	}
	
	/**
	 * Attempts to restart a stopped or failed Spring Batch job execution via invoking the REST service
	 * restart operation built into the ebook generator web application.
	 */
	@RequestMapping(value=WebConstants.MVC_JOB_RESTART, method = RequestMethod.GET)
	public ModelAndView restartJob(HttpSession httpSession, @RequestParam Long jobExecutionId, Model model) throws Exception {
log.debug(">>> RESTART jobExecutionId="+jobExecutionId);
		List<InfoMessage> messages = new ArrayList<InfoMessage>();
		JobOperationResponse jobOperationResponse = managerService.restartJob(jobExecutionId);
		handleJobOperationResponse(jobOperationResponse, messages, "job.restart.success", "job.restart.fail");
		model.addAttribute(WebConstants.KEY_INFO_MESSAGES, messages);
		// Forward to to the job summary controller
		return jobSummaryController.doGet(httpSession, model);
	}
	
	/**
	 * Attempts to stop a running Spring Batch job execution via invoking the REST service
	 * stop operation built into the ebook generator web application.
	 */
	@RequestMapping(value=WebConstants.MVC_JOB_STOP, method = RequestMethod.GET)
	public ModelAndView stopJob(HttpSession httpSession, @RequestParam Long jobExecutionId, Model model) throws Exception {
log.debug(">>> STOP jobExecutionId="+jobExecutionId);
		List<InfoMessage> messages = new ArrayList<InfoMessage>();
		JobOperationResponse jobOperationResponse = managerService.stopJob(jobExecutionId);
		handleJobOperationResponse(jobOperationResponse, messages, "job.stop.success", "job.stop.fail");
		model.addAttribute(WebConstants.KEY_INFO_MESSAGES, messages);
		// Forward to to the job summary controller
		return jobSummaryController.doGet(httpSession, model);
	}
	
	private void handleJobOperationResponse(JobOperationResponse jobOperationResponse, List<InfoMessage> messages,
											String successCode, String failCode) {
		String execId = jobOperationResponse.getJobExecutionId().toString();
		if (jobOperationResponse.isSuccess()) {
			Object[] args = { execId };
			messages.add(new InfoMessage(InfoMessage.Type.SUCCESS,
						 messageSourceAccessor.getMessage(successCode, args)));
		} else {
			Object[] args = { execId, jobOperationResponse.getMessage() };
			messages.add(new InfoMessage(InfoMessage.Type.FAIL,
					 messageSourceAccessor.getMessage(failCode, args)));
		}
	}
	
	private void populateModel(Model model, JobExecution jobExecution) {
		JobInstanceBookInfo bookInfo = (jobExecution != null) ? 
				jobService.findJobInstanceBookInfo(jobExecution.getJobId()) : null;
		JobExecutionVdo vdo = new JobExecutionVdo(jobExecution, bookInfo);
		model.addAttribute(WebConstants.KEY_JOB_EXECUTION, jobExecution);
		model.addAttribute(WebConstants.KEY_VDO, vdo);
	}

	@Required
	public void setJobService(JobService jobService) {
		this.jobService = jobService;
	}
	@Required
	public void setManagerService(ManagerService service) {
		this.managerService = service;
	}
	@Required
	public void setValidator(Validator validator) {
		this.validator = validator;
	}
	@Required
	public void setMessageSourceAccessor(MessageSourceAccessor accessor) {
		this.messageSourceAccessor = accessor;
	}
	@Required
	public void setJobSummaryController(JobListController controller) {
		this.jobSummaryController = controller;
	}
}
