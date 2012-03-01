/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.job.details;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.beans.factory.annotation.Required;
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
import org.springframework.web.servlet.View;

import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.job.list.JobExecutionVdo;

/**
 * Controller for the Job Execution Details page.
 */
@Controller
public class JobExecutionController {
	private static final Logger log = Logger.getLogger(JobExecutionController.class);
	
	private JobExplorer jobExplorer;
	private Validator validator;
	
	@InitBinder(JobExecutionForm.FORM_NAME)
	protected void initDataBinder(WebDataBinder binder) {
		binder.setValidator(validator);
	}
	
	@RequestMapping(value=WebConstants.MVC_JOB_EXECUTION_DETAILS, method = RequestMethod.GET)
	public ModelAndView doDisplayJobExecutionDetails(HttpServletRequest request,
							  @RequestParam Long jobExecutionId,
							  @ModelAttribute(JobExecutionForm.FORM_NAME) JobExecutionForm form,
							  Model model) throws Exception {
		JobExecution jobExecution = jobExplorer.getJobExecution(jobExecutionId);
		populateModel(model, jobExecution);
		return new ModelAndView(WebConstants.VIEW_JOB_EXECUTION_DETAILS);
	}
	
	@RequestMapping(value=WebConstants.MVC_JOB_EXECUTION_DETAILS_POST, method = RequestMethod.POST)
	public ModelAndView doPost(@ModelAttribute(JobExecutionForm.FORM_NAME) @Valid JobExecutionForm form,
							   BindingResult bindingResult,
							   Model model) throws Exception {
log.debug(form);
		JobExecution jobExecution = null;
		if (!bindingResult.hasErrors()) {
			jobExecution = jobExplorer.getJobExecution(form.getJobExecutionId());
			if (jobExecution == null) {
				String[] args = { form.getJobExecutionId().toString() };
				bindingResult.reject("executionId.not.found", args, "Job execution not found");
			}
		}
		populateModel(model, jobExecution);
		return new ModelAndView(WebConstants.VIEW_JOB_EXECUTION_DETAILS);
	}
	
	/**
	 * Attempts to restart a job.
	 */
	@RequestMapping(value=WebConstants.MVC_JOB_RESTART, method = RequestMethod.GET)
	public ModelAndView restartJob(@RequestParam Long jobExecutionId) throws Exception {
log.debug(">>> RESTART jobExecutionId="+jobExecutionId);
// TODO: implement this
		return null;
	}
	
	/**
	 * Attempts to stop a job.
	 */
	@RequestMapping(value=WebConstants.MVC_JOB_STOP, method = RequestMethod.GET)
	public View stopJob(@RequestParam Long jobExecutionId) throws Exception {
log.debug(">>> STOP jobExecutionId="+jobExecutionId);
		return null;
	}
	
	private void populateModel(Model model, JobExecution jobExecution) {
		JobExecutionVdo vdo = new JobExecutionVdo(jobExecution);
		model.addAttribute(WebConstants.KEY_JOB_EXECUTION, jobExecution);
		model.addAttribute(WebConstants.KEY_VDO, vdo);
	}
	@Required
	public void setJobExplorer(JobExplorer jobExplorer) {
		this.jobExplorer = jobExplorer;
	}
	@Required
	public void setValidator(Validator validator) {
		this.validator = validator;
	}
}
