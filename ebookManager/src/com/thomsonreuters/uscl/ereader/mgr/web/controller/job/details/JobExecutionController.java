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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.ModelAndView;

import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobOperationResponse;
import com.thomsonreuters.uscl.ereader.core.job.service.JobService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.InfoMessage;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.job.summary.JobExecutionVdo;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.job.summary.JobSummaryController;
import com.thomsonreuters.uscl.ereader.mgr.web.service.ManagerService;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;

/**
 * Controller for the Job Execution Details page.
 */
@Controller
public class JobExecutionController {
	private static final Logger log = Logger.getLogger(JobExecutionController.class);
	private static final String CODE_JOB_OPERATION_PRIVILEGE = "job.operation.privilege";
	private static final String CODE_JOB_RESTART_SUCCESS = "job.restart.success";
	private static final String CODE_JOB_RESTART_FAIL = "job.restart.fail";
	private static final String CODE_JOB_STOP_SUCCESS = "job.stop.success";
	private static final String CODE_JOB_STOP_FAIL = "job.stop.fail";
	
	private JobService jobService;
	private ManagerService managerService;
	private PublishingStatsService publishingStatsService;
	private Validator validator;
	private MessageSourceAccessor messageSourceAccessor;
	private JobSummaryController jobSummaryController;
	
	@InitBinder(JobExecutionForm.FORM_NAME)
	protected void initDataBinder(WebDataBinder binder) {
		binder.setValidator(validator);
	}
	
	/**
	 * Inbound GET to initially display the page.
	 */
	@RequestMapping(value=WebConstants.MVC_JOB_EXECUTION_DETAILS, method = RequestMethod.GET)
	public ModelAndView inboundGet(HttpServletRequest request,
							  @RequestParam Long jobExecutionId,
							  Model model) throws Exception {
		log.debug(">>> jobExecutionId="+jobExecutionId);
		JobExecutionVdo vdo = createJobExecutionVdo(jobExecutionId);
		populateModel(model, vdo);
		model.addAttribute(JobExecutionForm.FORM_NAME, new JobExecutionForm());
		return new ModelAndView(WebConstants.VIEW_JOB_EXECUTION_DETAILS);
	}
	
	/**
	 * Handle the submit/post of a new job execution ID whose details are to be viewed.
	 */
	@RequestMapping(value=WebConstants.MVC_JOB_EXECUTION_DETAILS_POST, method = RequestMethod.POST)
	public ModelAndView handlePost(@ModelAttribute(JobExecutionForm.FORM_NAME) @Valid JobExecutionForm form,
							   BindingResult bindingResult,
							   Model model) {
		log.debug(form);
		JobExecutionVdo vdo = null;
		if (!bindingResult.hasErrors()) {
			vdo = createJobExecutionVdo(form.getJobExecutionId());
			JobExecution jobExecution = vdo.getJobExecution();
			if (jobExecution == null) {
				String[] args = { form.getJobExecutionId().toString() };
				bindingResult.reject("executionId.not.found", args, "Job execution not found");
			}
		} else {
			vdo = new JobExecutionVdo();
		}
		populateModel(model, vdo);
		return new ModelAndView(WebConstants.VIEW_JOB_EXECUTION_DETAILS);
	}
	
	/**
	 * Attempts to restart a stopped or failed Spring Batch job execution via invoking the REST service
	 * restart operation built into the ebook generator web application.
	 */
	@RequestMapping(value=WebConstants.MVC_JOB_EXECUTION_JOB_RESTART, method = RequestMethod.GET)
	public ModelAndView restartJob(HttpSession httpSession,
								   @RequestParam Long jobExecutionId, Model model) throws Exception {
		log.debug(">>> jobExecutionId="+jobExecutionId);
		List<InfoMessage> messages = new ArrayList<InfoMessage>();
		if (authorizedForJobOperation(jobExecutionId, "RESTART", messages)) {
			try {
				JobOperationResponse jobOperationResponse = managerService.restartJob(jobExecutionId);
				handleRestartJobOperationResponse(messages, jobExecutionId, jobOperationResponse, messageSourceAccessor);
				Thread.sleep(1);
			} catch (HttpClientErrorException e) {
				log.error("REST error restarting job: " + jobExecutionId, e);
				messages.add(JobSummaryController.createRestExceptionMessage(e, messageSourceAccessor));
			}
		}
		model.addAttribute(WebConstants.KEY_INFO_MESSAGES, messages);
		// Forward to to the job summary controller
		return jobSummaryController.inboundGet(httpSession, model);
	}
	
	/**
	 * Attempts to stop a running Spring Batch job execution via invoking the REST service
	 * stop operation built into the ebook generator web application.
	 */
	@RequestMapping(value=WebConstants.MVC_JOB_EXECUTION_JOB_STOP, method = RequestMethod.GET)
	public ModelAndView stopJob(HttpSession httpSession,
								@RequestParam Long jobExecutionId, Model model) throws Exception {
		log.debug(">>> jobExecutionId="+jobExecutionId);
		List<InfoMessage> messages = new ArrayList<InfoMessage>();
		if (authorizedForJobOperation(jobExecutionId, "STOP", messages)) {
			try {
				JobOperationResponse jobOperationResponse = managerService.stopJob(jobExecutionId);
				handleStopJobOperationResponse(messages, jobOperationResponse, messageSourceAccessor);
				Thread.sleep(1);
			} catch (HttpClientErrorException e) {
				log.error("REST error stopping job: " + jobExecutionId, e);
				messages.add(JobSummaryController.createRestExceptionMessage(e, messageSourceAccessor));
			}
		}
		model.addAttribute(WebConstants.KEY_INFO_MESSAGES, messages);
		// Forward to to the job summary controller
		return jobSummaryController.inboundGet(httpSession, model);
	}


	public static void handleRestartJobOperationResponse(List<InfoMessage> messages,
														 Long jobExecutionIdToRestart,
														 JobOperationResponse jobOperationResponse,
														 MessageSourceAccessor messageSourceAccessor) {
		String execId = jobOperationResponse.getJobExecutionId().toString();
		if (jobOperationResponse.isSuccess()) {
			Object[] args = { jobExecutionIdToRestart.toString(), jobOperationResponse.getJobExecutionId().toString() };
			messages.add(new InfoMessage(InfoMessage.Type.SUCCESS,
						 messageSourceAccessor.getMessage(CODE_JOB_RESTART_SUCCESS, args)));
		} else {
			Object[] args = { execId, jobOperationResponse.getMessage() };
			messages.add(new InfoMessage(InfoMessage.Type.FAIL,
							messageSourceAccessor.getMessage(CODE_JOB_RESTART_FAIL, args)));
		}
	}


	public static void handleStopJobOperationResponse(List<InfoMessage> messages,
													  JobOperationResponse jobOperationResponse,
													  MessageSourceAccessor messageSourceAccessor) {
		String execId = jobOperationResponse.getJobExecutionId().toString();
		if (jobOperationResponse.isSuccess()) {
			Object[] args = { execId };
			messages.add(new InfoMessage(InfoMessage.Type.SUCCESS,
						 messageSourceAccessor.getMessage(CODE_JOB_STOP_SUCCESS, args)));
		} else {
			Object[] args = { execId, jobOperationResponse.getMessage() };
			messages.add(new InfoMessage(InfoMessage.Type.FAIL,
							messageSourceAccessor.getMessage(CODE_JOB_STOP_FAIL, args)));
		}
	}
	
	private JobExecutionVdo createJobExecutionVdo(Long jobExecutionId) {
		EbookAudit bookInfo = null;
		PublishingStats stats = null;
		JobExecution jobExecution = (jobExecutionId != null) ? jobService.findJobExecution(jobExecutionId) : null;
		if (jobExecution != null) {
			bookInfo = publishingStatsService.findAuditInfoByJobId(jobExecution.getJobId());
			Long jobInstanceId = jobExecution.getJobId();
			stats = publishingStatsService.findPublishingStatsByJobId(jobInstanceId);
		}
		JobExecutionVdo vdo = new JobExecutionVdo(jobExecution, bookInfo, stats);
		return vdo;
	}
	
	private boolean authorizedForJobOperation(Long jobExecutionId, String operation, List<InfoMessage> messages) {
		JobExecutionVdo vdo = createJobExecutionVdo(jobExecutionId);
		if (vdo.getJobExecution() == null) {
			Object[] args = { jobExecutionId.toString()};
			messages.add(new InfoMessage(InfoMessage.Type.ERROR, messageSourceAccessor.getMessage("job.execution.does.not.exist", args)));
			return false;
		}
		if (!vdo.isUserAllowedToStopAndRestartJob()) {
			Object[] args = { operation, jobExecutionId.toString() };
			messages.add(new InfoMessage(InfoMessage.Type.ERROR, messageSourceAccessor.getMessage(CODE_JOB_OPERATION_PRIVILEGE, args)));
			return false;
		}
		return true;
	}
	
	private void populateModel(Model model, JobExecutionVdo vdo) {
		model.addAttribute(WebConstants.KEY_JOB, vdo);
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
	public void setJobSummaryController(JobSummaryController controller) {
		this.jobSummaryController = controller;
	}
	@Required
	public void setPublishingStatsService(PublishingStatsService service) {
		this.publishingStatsService = service;
	}
}
