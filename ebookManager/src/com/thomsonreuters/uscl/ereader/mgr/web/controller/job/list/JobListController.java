/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.job.list;

import java.util.List;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.log4j.Logger;
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
import org.springframework.web.servlet.ModelAndView;

import com.thomsonreuters.uscl.ereader.core.job.domain.JobFilter;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobSort;
import com.thomsonreuters.uscl.ereader.core.job.service.JobService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.job.list.JobListForm.JobCommand;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.job.list.PageAndSort.DisplayTagSortProperty;


@Controller
public class JobListController extends BaseJobListController {
	private static final Logger log = Logger.getLogger(JobListController.class);
	private Validator validator;

	@InitBinder(JobListForm.FORM_NAME)
	protected void initDataBinder(WebDataBinder binder) {
		binder.setValidator(validator);
	}

	/**
	 * Handle initial in-bound HTTP get request to the page.
	 * No query string parameters are expected.
	 */
	@RequestMapping(value=WebConstants.MVC_JOB_SUMMARY, method = RequestMethod.GET)
	public ModelAndView doGet(HttpSession httpSession, 
							  @ModelAttribute(FilterForm.FORM_NAME) FilterForm filterForm,
							  @ModelAttribute(JobListForm.FORM_NAME) JobListForm jobListForm,
							  Model model) throws Exception {
		
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
	
		return new ModelAndView(WebConstants.VIEW_JOB_SUMMARY);
	}

	/**
	 * Handle paging and sorting of job list.
	 * Handles clicking of column headers to sort, or use of page number navigation links, like prev/next.
	 */
	@RequestMapping(value=WebConstants.MVC_JOB_SUMMARY_PAGE_AND_SORT, method = RequestMethod.GET)
	public ModelAndView doPagingAndSorting(HttpSession httpSession, 
								@ModelAttribute(FilterForm.FORM_NAME) FilterForm filterForm,
								@ModelAttribute(JobListForm.FORM_NAME) JobListForm jobListForm,
								Model model) throws Exception {
		List<Long> jobExecutionIds = null;
		
		// Restore the state of the search filter form
		FilterForm savedFilterForm = fetchSavedFilterForm(httpSession);
		filterForm.copyProperties(savedFilterForm);
		
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
	public ModelAndView doPost(HttpSession httpSession,
							   @ModelAttribute(JobListForm.FORM_NAME) @Valid JobListForm jobListForm,
							   BindingResult errors,
							   Model model) throws Exception {
		log.debug(jobListForm);
		
		// Restore state of paging and sorting
		PageAndSort pageAndSort = jobListForm.getPageAndSort();
		PageAndSort savedPageAndSort = fetchSavedPageAndSort(httpSession);
		Integer newObjectsPerPage = pageAndSort.getObjectsPerPage();	// possibly changed by user via select menu
		pageAndSort.copyProperties(savedPageAndSort);
		
		if (!errors.hasErrors()) {
			JobCommand command = jobListForm.getJobCommand();
			switch (command) {
				case CHANGE_OBJECTS_PER_PAGE:
					pageAndSort.setObjectsPerPage(newObjectsPerPage);	// Update the new number of items to be shown at one time
					break;
				case RESTART_JOB:
	// TODO: implement this
					log.debug("TODO: implement RESTART job: " + jobListForm);	// TODO
					jobListForm.setJobExecutionIds(null);	// uncheck all rows
					break;
				case STOP_JOB:
	// TODO: implement this				
					log.debug("TODO: implement STOP job: " + jobListForm);	// TODO
					jobListForm.setJobExecutionIds(null);	// uncheck all rows
					break;
				default:
					throw new IllegalArgumentException("Unexpected job list command: " + command);
			}
		}
		
		// Restore the state of the search filter
		FilterForm filterForm = new FilterForm();
		FilterForm savedFilterForm = fetchSavedFilterForm(httpSession);
		filterForm.copyProperties(savedFilterForm);
		
		// Fetch the existing session saved list of job execution ID's
		List<Long> jobExecutionIds = fetchSavedJobExecutionIdList(httpSession);
		
		setUpModel(jobExecutionIds, filterForm, pageAndSort, httpSession, model);
		model.addAttribute(FilterForm.FORM_NAME, filterForm);		
		
		return new ModelAndView(WebConstants.VIEW_JOB_SUMMARY);
	}

	@Required
	public void setJobService(JobService service) {
		this.jobService = service;
	}
	@Required
	public void setValidator(JobListValidator validator) {
		this.validator = validator;
	}
}
