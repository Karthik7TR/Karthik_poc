/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.job.details;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.core.job.service.JobService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;


/**
 * Controller for the Step Execution Details page.
 */
@Controller
public class StepExecutionController {
//	private static final Logger log = Logger.getLogger(StepExecutionController.class);
	
	private JobService jobService;
	private PublishingStatsService publishingStatsService;
	
	/**
	 * Set up model for viewing a specific job step.
	 * @param jobInstanceId job instance ID  (job defn + when + job params)
	 * @param jobExecutionId job execution ID  (represents a single run of a job instance)
	 * @param stepExecutionId step execution ID
	 */
	@RequestMapping(value=WebConstants.MVC_JOB_STEP_EXECUTION_DETAILS, method = RequestMethod.GET)
	public ModelAndView inboundGet(HttpServletRequest request,
							  @RequestParam Long jobInstanceId,  	// job instance ID
							  @RequestParam Long jobExecutionId,  	// job execution ID
							  @RequestParam Long stepExecutionId,	// step execution ID
							  Model model) throws Exception {
//		log.debug(">>> jobInstanceId="+jobInstanceId + ",jobExecutionId="+jobExecutionId + ",stepExecutionId="+stepExecutionId);
		JobInstance jobInstance = jobService.findJobInstance(jobInstanceId);
		EbookAudit bookInfo = publishingStatsService.findAuditInfoByJobId(jobInstance.getId());
		StepExecution stepExecution = jobService.findStepExecution(jobExecutionId, stepExecutionId);
		populateModel(model, jobInstance, bookInfo, stepExecution);
		return new ModelAndView(WebConstants.VIEW_JOB_STEP_EXECUTION_DETAILS);
	}
	
	private void populateModel(Model model, final JobInstance jobInstance,
							   final EbookAudit bookInfo, final StepExecution stepExecution) {
		List<Map.Entry<String,Object>> mapEntryList = createStepExecutionContextMapEntryList(stepExecution);
		model.addAttribute(WebConstants.KEY_JOB_INSTANCE, jobInstance);
		model.addAttribute(WebConstants.KEY_JOB_BOOK_INFO, bookInfo);
		model.addAttribute(WebConstants.KEY_JOB_STEP_EXECUTION, stepExecution);
		model.addAttribute(WebConstants.KEY_JOB_STEP_EXECUTION_CONTEXT_MAP_ENTRIES, mapEntryList);
	}
	
	/**
	 * Create a list of map entries that represents all the key/value pairs inside the specified step execution.
	 * @param stepExecution the step for whom we want the step execution context map entries.
	 * @return a list of map entries inside the step execution context.
	 */
	private static List<Map.Entry<String,Object>> createStepExecutionContextMapEntryList(final StepExecution stepExecution) {
		List<Map.Entry<String,Object>> list = new ArrayList<Map.Entry<String,Object>>();
		if (stepExecution != null) {
			ExecutionContext execContext = stepExecution.getExecutionContext();
			Set<Map.Entry<String,Object>> entrySet = execContext.entrySet();
			Iterator<Map.Entry<String,Object>> entryIterator = entrySet.iterator();
			while(entryIterator.hasNext()) {
				list.add(entryIterator.next());
			}
		}
		return list;
	}
	@Required
	public void setJobService(JobService jobService) {
		this.jobService = jobService;
	}
	@Required
	public void setPublishingStatsService(PublishingStatsService service) {
		this.publishingStatsService = service;
	}
}
