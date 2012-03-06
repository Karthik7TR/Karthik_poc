/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.job.summary;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.log4j.Logger;
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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.ModelAndView;

import com.thomsonreuters.uscl.ereader.core.job.domain.JobFilter;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobOperationResponse;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobSort;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.InfoMessage;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.InfoMessage.Type;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.job.details.JobExecutionController;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.job.summary.JobSummaryForm.JobCommand;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.job.summary.PageAndSort.DisplayTagSortProperty;
import com.thomsonreuters.uscl.ereader.mgr.web.service.ManagerService;


@Controller
public class JobSummaryController extends BaseJobSummaryController {
	private static final Logger log = Logger.getLogger(JobSummaryController.class);
	private ManagerService managerService;
	private Validator validator;
	private MessageSourceAccessor messageSourceAccessor;

	@InitBinder(JobSummaryForm.FORM_NAME)
	protected void initDataBinder(WebDataBinder binder) {
		binder.setValidator(validator);
	}

	/**
	 * Handle initial in-bound HTTP get request to the page.
	 * No query string parameters are expected.
	 */
	@RequestMapping(value=WebConstants.MVC_JOB_SUMMARY, method = RequestMethod.GET)
	public ModelAndView doInboundGet(HttpSession httpSession, 
							  			Model model) {
		
		FilterForm filterForm = new FilterForm();
		JobSummaryForm jobListForm = new JobSummaryForm();
		PageAndSort pageAndSort = jobListForm.getPageAndSort();
		pageAndSort.initialize(1, PageAndSort.DEFAULT_ITEMS_PER_PAGE,
							   DisplayTagSortProperty.START_TIME, false);
		FilterForm savedFilterForm = fetchSavedFilterForm(httpSession);
		filterForm.copyProperties(savedFilterForm);
		JobFilter jobFilter = new JobFilter(filterForm.getFromDate(), filterForm.getToDate(), filterForm.getBatchStatus(),
										 filterForm.getTitleId(), filterForm.getBookName());
		JobSort jobSort = new JobSort();
		List<Long> jobExecutionIds = jobService.findJobExecutions(jobFilter, jobSort);
		
		setUpModel(jobExecutionIds, filterForm, pageAndSort, httpSession, model);
		model.addAttribute(JobSummaryForm.FORM_NAME, jobListForm);
	
		return new ModelAndView(WebConstants.VIEW_JOB_SUMMARY);
	}

	/**
	 * Handle paging and sorting of job list.
	 * Handles clicking of column headers to sort, or use of page number navigation links, like prev/next.
	 */
	@RequestMapping(value=WebConstants.MVC_JOB_SUMMARY_PAGE_AND_SORT, method = RequestMethod.GET)
	public ModelAndView doPagingAndSorting(HttpSession httpSession, 
								@ModelAttribute(JobSummaryForm.FORM_NAME) JobSummaryForm jobListForm,
								Model model) {
		List<Long> jobExecutionIds = null;
		
		// Restore the state of the search filter form
		FilterForm filterForm = fetchSavedFilterForm(httpSession);
		
		// Set up the sorting/paging depending upon what the user selected
		PageAndSort savedPageAndSort = fetchSavedPageAndSort(httpSession);
		PageAndSort pageAndSort = jobListForm.getPageAndSort();
		Integer nextPageNumber = pageAndSort.getPage();
		// If there was a page=n query string parameter, then we assume we are paging since this
		// parameter is not present on the query string when display tag sorting.
		if (nextPageNumber != null) {
			pageAndSort.copyProperties(savedPageAndSort);
			pageAndSort.setPage(nextPageNumber);
			jobExecutionIds = fetchSavedJobExecutionIdList(httpSession);
		} else {  // SORTING
			pageAndSort.setPage(1);
			pageAndSort.setObjectsPerPage(savedPageAndSort.getObjectsPerPage());
			// Fetch the job list model
			JobFilter jobFilter = new JobFilter(filterForm.getFromDate(), filterForm.getToDate(), filterForm.getBatchStatus(),
					 filterForm.getTitleId(), filterForm.getBookName());
			JobSort jobSort = createJobSort(pageAndSort.getSort(), pageAndSort.isAscendingSort());
			jobExecutionIds = jobService.findJobExecutions(jobFilter, jobSort);
		}
		
		setUpModel(jobExecutionIds, filterForm, pageAndSort, httpSession, model);
		
		return new ModelAndView(WebConstants.VIEW_JOB_SUMMARY);
	}

	/**
	 * Handle operational buttons that submit a form of selected rows, or when the user changes the number of
	 * rows displayed at one time.
	 */
	@RequestMapping(value=WebConstants.MVC_JOB_SUMMARY_POST, method = RequestMethod.POST)
	public ModelAndView doStopOrRestartJob(HttpSession httpSession,
							   @ModelAttribute(JobSummaryForm.FORM_NAME) @Valid JobSummaryForm jobListForm,
							   BindingResult errors,
							   Model model) {
		log.debug(jobListForm);
		List<InfoMessage> messages = new ArrayList<InfoMessage>();
		
		// Restore state of paging and sorting
		PageAndSort pageAndSort = jobListForm.getPageAndSort();
		PageAndSort savedPageAndSort = fetchSavedPageAndSort(httpSession);
		Integer newObjectsPerPage = pageAndSort.getObjectsPerPage();	// possibly changed by user via select menu
		pageAndSort.copyProperties(savedPageAndSort);
		
		if (!errors.hasErrors()) {
			JobCommand command = jobListForm.getJobCommand();
			try {
			switch (command) {
				case CHANGE_OBJECTS_PER_PAGE:
					pageAndSort.setObjectsPerPage(newObjectsPerPage);	// Update the new number of items to be shown at one time
					break;
				case RESTART_JOB:
					for (Long jobExecutionId : jobListForm.getJobExecutionIds()) {
						JobOperationResponse jobOperationResponse = managerService.restartJob(jobExecutionId);
						JobExecutionController.handleRestartJobOperationResponse(messages, jobExecutionId, jobOperationResponse, messageSourceAccessor);
					}
					break;
				case STOP_JOB:
					for (Long jobExecutionId : jobListForm.getJobExecutionIds()) {
						JobOperationResponse jobOperationResponse = managerService.stopJob(jobExecutionId);
						JobExecutionController.handleStopJobOperationResponse(messages, jobOperationResponse, messageSourceAccessor);
					}
					break;
				default:
					throw new IllegalArgumentException("Unexpected command: " + command);
			}
			} catch (HttpClientErrorException e) {
				messages.add(createRestExceptionMessage(e));
				log.error("Could not complete ebookGenerator REST request", e);
			}
		}
		
		// Restore the state of the search filter
		FilterForm filterForm = fetchSavedFilterForm(httpSession);
		
		// Fetch the existing session saved list of job execution ID's
		List<Long> jobExecutionIds = fetchSavedJobExecutionIdList(httpSession);
		// Uncheck all of the form checkboxes
		jobListForm.setJobExecutionIds(null);
		
		setUpModel(jobExecutionIds, filterForm, pageAndSort, httpSession, model);
		model.addAttribute(WebConstants.KEY_INFO_MESSAGES, messages);	// Informational messages related to success/fail of job stop or restart
		
		return new ModelAndView(WebConstants.VIEW_JOB_SUMMARY);
	}
	
	public static InfoMessage createRestExceptionMessage(Throwable t) {
		return new InfoMessage(Type.ERROR, String.format(
		"Job operation(s) failed. %s - This is probably because the ebookGenerator web application is not running.",
					 t.getMessage()));
	}

	@Required
	public void setManagerService(ManagerService service) {
		this.managerService = service;
	}
	@Required
	public void setValidator(JobSummaryValidator validator) {
		this.validator = validator;
	}
	@Required
	public void setMessageSourceAccessor(MessageSourceAccessor accessor) {
		this.messageSourceAccessor = accessor;
	}
}
