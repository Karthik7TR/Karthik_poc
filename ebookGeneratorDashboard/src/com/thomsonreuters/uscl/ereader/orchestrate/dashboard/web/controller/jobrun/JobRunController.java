package com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.controller.jobrun;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.thomsonreuters.uscl.ereader.orchestrate.core.JobControl;
import com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.SelectOption;
import com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.WebConstants;

@Controller
public class JobRunController {
	private static final Logger log = Logger.getLogger(JobRunController.class);
	
	@Resource(name="environmentName")
	private String environmentName;
	@Autowired
	private JobExplorer jobExplorer;
	@Autowired
	private JobControl jobControl;
	
	/**
	 * Handle in-bound GET request to display the job launching page.
	 */
	@RequestMapping(value=WebConstants.URL_JOB_RUN, method = RequestMethod.GET)
	public ModelAndView doGet(@ModelAttribute JobRunForm form,
							  Model model) throws Exception {
		form.setThreadPriority(Thread.NORM_PRIORITY);
		populateModel(model);
		return new ModelAndView(WebConstants.VIEW_JOB_RUN);
	}
	
	/**
	 * Handle submit (POST) of the form that indicates which job is to be run.
	 */
	@RequestMapping(value=WebConstants.URL_JOB_RUN, method = RequestMethod.POST)
	public ModelAndView doPost(@ModelAttribute JobRunForm form,
								Model model) throws Exception {
		log.debug(form);
		if (form.isHighPriorityJob()) {
			jobControl.enqueueHighPriorityJobRunRequest(form.getJobName(), form.getThreadPriority());
		} else {
			jobControl.enqueueNormalPriorityJobRunRequest(form.getJobName(), form.getThreadPriority());
		}
		populateModel(model);
		return new ModelAndView(WebConstants.VIEW_JOB_RUN);
	}

	private void populateModel(Model model) {
		/* Get all the unique job names */
		List<String> uniqueJobNames = jobExplorer.getJobNames();		
		List<SelectOption> jobNameOptions = new ArrayList<SelectOption>();
		for (String jobName : uniqueJobNames) {
			jobNameOptions.add(new SelectOption(jobName, jobName));
		}
		model.addAttribute(WebConstants.KEY_JOB_NAMES, jobNameOptions);
		model.addAttribute(WebConstants.KEY_ENVIRONMENT, environmentName);
	}
}
