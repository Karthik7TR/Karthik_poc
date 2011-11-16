package com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.controller.jobexecution;

import java.net.URL;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.servlet.view.RedirectView;

import com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.WebConstants;
import com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.controller.JobExecutionVdo;

@Controller
public class JobExecutionController {
	private static final Logger log = Logger.getLogger(JobExecutionController.class);
	
	@Resource(name="environmentName")
	private String environmentName;
	@Autowired
	private JobExplorer jobExplorer;
	@Resource(name="engineContextUrl")
	private URL engineContextUrl;
	@Resource(name="jobExecutionFormValidator")
	private Validator validator;
	
	@InitBinder(JobExecutionForm.FORM_NAME)
	protected void initDataBinder(WebDataBinder binder) {
		binder.setValidator(validator);
	}
	
	@RequestMapping(value=WebConstants.URL_JOB_EXECUTION_DETAILS_GET, method = RequestMethod.GET)
	public ModelAndView doGetJobExecutionDetails(HttpServletRequest request,
							  @RequestParam Long jobExecutionId,
							  @ModelAttribute(JobExecutionForm.FORM_NAME) JobExecutionForm form,
							  Model model) throws Exception {
//log.debug(">>> jobExecutionId="+jobExecutionId);
		JobExecution jobExecution = jobExplorer.getJobExecution(jobExecutionId);
		populateModel(model, jobExecution);
		return new ModelAndView(WebConstants.VIEW_JOB_EXECUTION_DETAILS);
	}
	
	@RequestMapping(value=WebConstants.URL_JOB_EXECUTION_DETAILS_POST, method = RequestMethod.POST)
	public ModelAndView doPostJobExecutionDetails(@ModelAttribute(JobExecutionForm.FORM_NAME) @Valid JobExecutionForm form,
							   BindingResult bindingResult,
							   Model model) throws Exception {
log.debug(form);
		JobExecution jobExecution = null;
		if (!bindingResult.hasErrors()) {
			jobExecution = jobExplorer.getJobExecution(form.getExecutionId());
			if (jobExecution == null) {
				String[] args = { form.getExecutionId().toString() };
				bindingResult.reject("executionId.not.found", args, "Job execution not found");
			}
		}
		populateModel(model, jobExecution);
		return new ModelAndView(WebConstants.VIEW_JOB_EXECUTION_DETAILS);
	}
	
	/**
	 * Attempts to restart a job by requesting a job restart at the engine restart URL.
	 * The engine will redirect back to the Dashboard Job Details page for the restarted job execution.
	 */
	@RequestMapping(value=WebConstants.URL_JOB_RESTART, method = RequestMethod.GET)
	public View restartJob(@RequestParam Long jobExecutionId) throws Exception {
log.debug(">>> jobExecutionId="+jobExecutionId);
		return new RedirectView(engineContextUrl.toString()+"/"+WebConstants.URL_JOB_RESTART+"?"+WebConstants.KEY_JOB_EXECUTION_ID+"="+jobExecutionId);
	}
	
	/**
	 * Attempts to stop a job by requesting a job stop at the engine stop URL.
	 * The engine will redirect back to the Dashboard Job Details page for the stopped job execution.
	 */
	@RequestMapping(value=WebConstants.URL_JOB_STOP, method = RequestMethod.GET)
	public View stopJob(@RequestParam Long jobExecutionId) throws Exception {
log.debug(">>> jobExecutionId="+jobExecutionId);
		return new RedirectView(engineContextUrl.toString()+"/"+WebConstants.URL_JOB_STOP+"?"+WebConstants.KEY_JOB_EXECUTION_ID+"="+jobExecutionId);
	}
	
	private void populateModel(Model model, JobExecution jobExecution) {
		JobExecutionVdo vdo = new JobExecutionVdo(jobExecution);
		model.addAttribute(WebConstants.KEY_ENVIRONMENT, environmentName);
		model.addAttribute(WebConstants.KEY_JOB_EXECUTION, jobExecution);
		model.addAttribute(WebConstants.KEY_VDO, vdo);
	}
}
