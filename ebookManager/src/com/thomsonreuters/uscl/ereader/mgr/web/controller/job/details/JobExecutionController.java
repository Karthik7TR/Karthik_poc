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
import com.thomsonreuters.uscl.ereader.core.job.domain.SimpleRestServiceResponse;
import com.thomsonreuters.uscl.ereader.core.job.service.JobService;
import com.thomsonreuters.uscl.ereader.core.outage.service.OutageService;
import com.thomsonreuters.uscl.ereader.core.service.GeneratorRestClient;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.InfoMessage;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.job.summary.JobExecutionVdo;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.job.summary.JobSummaryController;
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
	private static final String CODE_JOB_OPERATION_NO_RESPONSE = "job.operation.no.response";
	public static final String LABEL_RESTART = "restart";
	public static final String LABEL_STOP = "stop";
	
	private JobService jobService;
	private GeneratorRestClient generatorRestClient;
	private PublishingStatsService publishingStatsService;
	private Validator validator;
	private MessageSourceAccessor messageSourceAccessor;
	private JobSummaryController jobSummaryController;
	private OutageService outageService;
	
	@InitBinder(JobExecutionForm.FORM_NAME)
	protected void initDataBinder(WebDataBinder binder) {
		binder.setValidator(validator);
	}
	
	/**
	 * Inbound GET to initially display the page.
	 */
	@RequestMapping(value=WebConstants.MVC_JOB_EXECUTION_DETAILS, method = RequestMethod.GET)
	public ModelAndView inboundGet(HttpServletRequest request,
							  @RequestParam("jobExecutionId") Long jobExecutionId,
							  Model model) throws Exception {
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
								   @RequestParam("jobExecutionId") Long jobExecutionId, Model model) throws Exception {
		List<InfoMessage> messages = new ArrayList<InfoMessage>();
		if (authorizedForJobOperation(jobExecutionId, LABEL_RESTART, messages)) {
			try {
				SimpleRestServiceResponse restResponse = generatorRestClient.restartJob(jobExecutionId);
				handleRestartJobOperationResponse(messages, jobExecutionId, restResponse, messageSourceAccessor);
				Thread.sleep(1);
			} catch (HttpClientErrorException e) {
				InfoMessage errorMessage = JobSummaryController.createRestExceptionMessage("job.restart.fail", jobExecutionId, e, messageSourceAccessor);
				messages.add(errorMessage);
				log.warn(errorMessage.getText());
			}
		}
		model.addAttribute(WebConstants.KEY_INFO_MESSAGES, messages);
		model.addAttribute(WebConstants.KEY_DISPLAY_OUTAGE, outageService.getAllPlannedOutagesToDisplay());
		// Forward to to the job summary controller
		return jobSummaryController.inboundGet(httpSession, model);
	}
	
	/**
	 * Attempts to stop a running Spring Batch job execution via invoking the REST service
	 * stop operation built into the ebook generator web application.
	 */
	@RequestMapping(value=WebConstants.MVC_JOB_EXECUTION_JOB_STOP, method = RequestMethod.GET)
	public ModelAndView stopJob(HttpSession httpSession,
								@RequestParam("jobExecutionId") Long jobExecutionId, Model model) throws Exception {
		List<InfoMessage> messages = new ArrayList<InfoMessage>();
		if (authorizedForJobOperation(jobExecutionId, LABEL_STOP, messages)) {
			try {
				SimpleRestServiceResponse restResponse = generatorRestClient.stopJob(jobExecutionId);
				handleStopJobOperationResponse(messages, jobExecutionId, restResponse, messageSourceAccessor);
				Thread.sleep(1);
			} catch (HttpClientErrorException e) {
				InfoMessage errorMessage = JobSummaryController.createRestExceptionMessage("job.stop.fail", jobExecutionId, e, messageSourceAccessor);
				messages.add(errorMessage);
				log.warn(errorMessage.getText());
			}
		}
		model.addAttribute(WebConstants.KEY_INFO_MESSAGES, messages);
		model.addAttribute(WebConstants.KEY_DISPLAY_OUTAGE, outageService.getAllPlannedOutagesToDisplay());
		// Forward to to the job summary controller
		return jobSummaryController.inboundGet(httpSession, model);
	}


	public static void handleRestartJobOperationResponse(List<InfoMessage> messages,
														 Long jobExecutionIdToRestart,
														 SimpleRestServiceResponse restResponse,
														 MessageSourceAccessor messageSourceAccessor) {
		if (restResponse == null) {
			String errorMessage = messageSourceAccessor.getMessage(CODE_JOB_OPERATION_NO_RESPONSE );
			Object[] args = { jobExecutionIdToRestart.toString(), errorMessage};
			messages.add(new InfoMessage(InfoMessage.Type.FAIL,
							messageSourceAccessor.getMessage(CODE_JOB_RESTART_FAIL, args)));
			return;
		}
		String execId = restResponse.getId().toString();
		if (restResponse.isSuccess()) {
			Object[] args = { jobExecutionIdToRestart.toString(), restResponse.getId().toString() };
			messages.add(new InfoMessage(InfoMessage.Type.SUCCESS,
						 messageSourceAccessor.getMessage(CODE_JOB_RESTART_SUCCESS, args)));
		} else {
			Object[] args = { execId, restResponse.getMessage() };
			messages.add(new InfoMessage(InfoMessage.Type.FAIL,
							messageSourceAccessor.getMessage(CODE_JOB_RESTART_FAIL, args)));
		}
	}


	public static void handleStopJobOperationResponse(List<InfoMessage> messages,
			 										  Long jobExecutionIdToStop,
			 										 SimpleRestServiceResponse restResponse,
													  MessageSourceAccessor messageSourceAccessor) {
		if (restResponse == null) {
			String errorMessage = messageSourceAccessor.getMessage(CODE_JOB_OPERATION_NO_RESPONSE );
			Object[] args = { jobExecutionIdToStop.toString(), errorMessage};
			messages.add(new InfoMessage(InfoMessage.Type.FAIL,
							messageSourceAccessor.getMessage(CODE_JOB_STOP_FAIL, args)));
			return;
		}
		String execId = restResponse.getId().toString();
		if (restResponse.isSuccess()) {
			Object[] args = { execId };
			messages.add(new InfoMessage(InfoMessage.Type.SUCCESS,
						 messageSourceAccessor.getMessage(CODE_JOB_STOP_SUCCESS, args)));
		} else {
			Object[] args = { execId, restResponse.getMessage() };
			messages.add(new InfoMessage(InfoMessage.Type.FAIL,
							messageSourceAccessor.getMessage(CODE_JOB_STOP_FAIL, args)));
		}
	}
	
	private JobExecutionVdo createJobExecutionVdo(Long jobExecutionId) {
		EbookAudit bookInfo = null;
		PublishingStats stats = null;
		JobExecution jobExecution = (jobExecutionId != null) ? jobService.findJobExecution(jobExecutionId) : null;
		if (jobExecution != null) {
			Long jobInstanceId = jobExecution.getJobId();
			stats = publishingStatsService.findPublishingStatsByJobId(jobInstanceId);
			bookInfo = stats.getAudit();
		}
		JobExecutionVdo vdo = new JobExecutionVdo(jobExecution, bookInfo, stats);
		return vdo;
	}
	
	public boolean authorizedForJobOperation(Long jobExecutionId, String operation, List<InfoMessage> messages) {
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
		model.addAttribute(WebConstants.KEY_DISPLAY_OUTAGE, outageService.getAllPlannedOutagesToDisplay());
	}

	@Required
	public void setJobService(JobService jobService) {
		this.jobService = jobService;
	}
	@Required
	public void setGeneratorRestClient(GeneratorRestClient client) {
		this.generatorRestClient = client;
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
	@Required
	public void setOutageService(OutageService service) {
		this.outageService = service;
	}
}
