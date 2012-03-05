/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.job.details;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.StepExecution;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.thomsonreuters.uscl.ereader.core.job.domain.JobInstanceBookInfo;
import com.thomsonreuters.uscl.ereader.core.job.service.JobService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.job.summary.StepStartTimeComparator;

/**
 * Controller for the Job Instance Details page.
 */
@Controller
public class JobInstanceController {
	private static final Logger log = Logger.getLogger(JobInstanceController.class);
	private static final StepStartTimeComparator stepStartTimeComparator = new StepStartTimeComparator();
	
	private JobService jobService;
	
	/**
	 * Create a aggregated list of StepExecution's from all JobInstance's specified by id.
	 * @param jobInstanceId
	 * @param model
	 * @return the view to the Job Instance Steps page
	 * @throws Exception
	 */
	@RequestMapping(value=WebConstants.MVC_JOB_INSTANCE_DETAILS, method = RequestMethod.GET)
	public ModelAndView doGet(@RequestParam Long jobInstanceId,
							  Model model) throws Exception {
		log.debug(">>> jobInstanceId="+jobInstanceId);
		JobInstance jobInstance = (jobInstanceId != null) ? jobService.findJobInstance(jobInstanceId) : null;
		if (jobInstance != null) {
			JobInstanceBookInfo bookInfo = jobService.findJobInstanceBookInfo(jobInstance.getId());
			List<JobExecution> jobExecutions = jobService.findJobExecutions(jobInstance);
			List<StepExecution> allJobInstanceSteps = new ArrayList<StepExecution>();
			for (JobExecution je : jobExecutions) {
				Collection<StepExecution> stepExecutions = je.getStepExecutions();
				allJobInstanceSteps.addAll(stepExecutions);
			}
			Collections.sort(allJobInstanceSteps, stepStartTimeComparator);  // Descending sort
			populateModel(model, jobInstance, bookInfo, allJobInstanceSteps);
		}
		return new ModelAndView(WebConstants.VIEW_JOB_INSTANCE_DETAILS);
	}

	private void populateModel(Model model, final JobInstance jobInstance,
							   final JobInstanceBookInfo bookInfo,
							   final List<StepExecution> allJobInstanceSteps) {
		model.addAttribute(WebConstants.KEY_JOB_INSTANCE, jobInstance);
		model.addAttribute(WebConstants.KEY_JOB_INSTANCE_BOOK_INFO, bookInfo);
		model.addAttribute(WebConstants.KEY_JOB_STEP_EXECUTIONS, allJobInstanceSteps);
	}
	@Required
	public void setJobService(JobService jobService) {
		this.jobService = jobService;
	}
}
