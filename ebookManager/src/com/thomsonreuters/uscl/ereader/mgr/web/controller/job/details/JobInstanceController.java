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

import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobSummary;
import com.thomsonreuters.uscl.ereader.core.job.service.JobService;
import com.thomsonreuters.uscl.ereader.core.outage.service.OutageService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.job.summary.JobExecutionVdo;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.job.summary.StepStartTimeComparator;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;

/**
 * Controller for the Job Instance Details page.
 */
@Controller
public class JobInstanceController {
//	private static final Logger log = Logger.getLogger(JobInstanceController.class);
	private static final StepStartTimeComparator stepStartTimeComparator = new StepStartTimeComparator();
	
	private JobService jobService;
	private PublishingStatsService publishingStatsService;
	private OutageService outageService;
	
	/**
	 * Create a aggregated list of StepExecution's from all JobInstance's specified by id.
	 * @param jobInstanceId
	 * @param model
	 * @return the view to the Job Instance Steps page
	 * @throws Exception
	 */
	@RequestMapping(value=WebConstants.MVC_JOB_INSTANCE_DETAILS, method = RequestMethod.GET)
	public ModelAndView inboundGet(@RequestParam("jobInstanceId") Long jobInstanceId,
							  Model model) throws Exception {
//		log.debug(">>> jobInstanceId="+jobInstanceId);
		JobInstance jobInstance = (jobInstanceId != null) ? jobService.findJobInstance(jobInstanceId) : null;
		if (jobInstance != null) {
			long totalDurationOfAllExecutions = 0;
			PublishingStats publishingStats = publishingStatsService.findPublishingStatsByJobId(jobInstance.getId());
			EbookAudit bookInfo = publishingStats.getAudit();
			List<JobExecution> jobExecutions = jobService.findJobExecutions(jobInstance);
			List<StepExecution> allJobInstanceSteps = new ArrayList<StepExecution>();
			for (JobExecution je : jobExecutions) {
				totalDurationOfAllExecutions += JobSummary.getExecutionDuration(je.getStartTime(), je.getEndTime());
				Collection<StepExecution> stepExecutions = je.getStepExecutions();
				allJobInstanceSteps.addAll(stepExecutions);
			}
			
			// Get the job execution for the last step run, used to determine if we can restart the job here		
			if (allJobInstanceSteps.size() > 0) {
				Collections.sort(allJobInstanceSteps, stepStartTimeComparator);  // Descending sort
				StepExecution lastStepExecution = allJobInstanceSteps.get(0);
				JobExecution lastJobExecution = lastStepExecution.getJobExecution();
				JobExecutionVdo vdo = new JobExecutionVdo(lastJobExecution, bookInfo, publishingStats);
				model.addAttribute(WebConstants.KEY_JOB, vdo);
			}

			model.addAttribute(WebConstants.KEY_JOB_INSTANCE_DURATION,
							JobSummary.getExecutionDuration(totalDurationOfAllExecutions));
			populateModel(model, jobInstance, bookInfo, allJobInstanceSteps);
		}
		return new ModelAndView(WebConstants.VIEW_JOB_INSTANCE_DETAILS);
	}

	private void populateModel(Model model, final JobInstance jobInstance,
							   final EbookAudit bookInfo,
							   final List<StepExecution> allJobInstanceSteps) {
		model.addAttribute(WebConstants.KEY_JOB_INSTANCE, jobInstance);
		model.addAttribute(WebConstants.KEY_JOB_BOOK_INFO, bookInfo);
		model.addAttribute(WebConstants.KEY_JOB_STEP_EXECUTIONS, allJobInstanceSteps);
		model.addAttribute(WebConstants.KEY_DISPLAY_OUTAGE, outageService.getAllPlannedOutagesToDisplay());
	}
	@Required
	public void setJobService(JobService jobService) {
		this.jobService = jobService;
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
