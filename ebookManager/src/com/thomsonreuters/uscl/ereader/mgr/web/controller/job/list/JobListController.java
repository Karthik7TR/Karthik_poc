/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.job.list;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.displaytag.pagination.PaginatedList;
import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.thomsonreuters.uscl.ereader.core.domain.JobFilter;
import com.thomsonreuters.uscl.ereader.core.domain.JobSort;
import com.thomsonreuters.uscl.ereader.core.domain.JobSort.SortProperty;
import com.thomsonreuters.uscl.ereader.core.service.JobService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.job.list.FilterForm.FilterCommand;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.job.list.JobListForm.JobCommand;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.job.list.PageAndSort.DisplayTagSortProperty;


@Controller
public class JobListController {
	private static final Logger log = Logger.getLogger(JobListController.class);
	private JobService jobService;
	private FilterFormValidator filterFormValidator;

	/**
	 * Handle initial in-bound HTTP get request to the page.
	 * No query string parameters are expected.
	 */
	@RequestMapping(value=WebConstants.MVC_JOB_LIST, method = RequestMethod.GET)
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
	
		return new ModelAndView(WebConstants.VIEW_JOB_LIST);
	}

	/**
	 * Handle paging and sorting of job list.
	 * Handles clicking of column headers to sort, or use of page number navigation links, like prev/next.
	 */
	@RequestMapping(value=WebConstants.MVC_JOB_LIST_PAGE_AND_SORT, method = RequestMethod.GET)
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
		
		return new ModelAndView(WebConstants.VIEW_JOB_LIST);
	}

	/**
	 * Handle operational buttons that submit a form of selected rows, or when the user changes the number of
	 * rows displayed at one time.
	 */
	@RequestMapping(value=WebConstants.MVC_JOB_LIST_POST, method = RequestMethod.POST)
	public ModelAndView doPost(HttpSession httpSession,
							   @ModelAttribute(JobListForm.FORM_NAME) JobListForm jobListForm,
							   @ModelAttribute(FilterForm.FORM_NAME) FilterForm filterForm,
							   Model model) throws Exception {
		log.debug(jobListForm);
		
		// Restore state of paging and sorting
		PageAndSort pageAndSort = jobListForm.getPageAndSort();
		PageAndSort savedPageAndSort = fetchSavedPageAndSort(httpSession);
		Integer newObjectsPerPage = pageAndSort.getObjectsPerPage();	// possibly changed by user via select menu
		pageAndSort.copyProperties(savedPageAndSort);
		
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
		
		// Restore the state of the search filter
		FilterForm savedFilterForm = fetchSavedFilterForm(httpSession);
		filterForm.copyProperties(savedFilterForm);
		
		// Fetch the existing session saved list of job execution ID's
		List<Long> jobExecutionIds = fetchSavedJobExecutionIdList(httpSession);
		
		setUpModel(jobExecutionIds, filterForm, pageAndSort, httpSession, model);
		
		return new ModelAndView(WebConstants.VIEW_JOB_LIST);
	}

	/**
	 * Handle submit/post of a new set of filter criteria.
	 */
	@RequestMapping(value=WebConstants.MVC_JOB_LIST_FILTER_POST, method = RequestMethod.POST)
	public ModelAndView doFilterPost(HttpSession httpSession,
						@ModelAttribute(FilterForm.FORM_NAME) FilterForm filterForm,
						@ModelAttribute(JobListForm.FORM_NAME) JobListForm jobListForm,
						BindingResult errors,
						Model model) throws Exception {
log.debug(filterForm);
		// Fetch the existing saved list of job execution ID's from the last successful query
		List<Long> jobExecutionIds = fetchSavedJobExecutionIdList(httpSession);
		
		// Restore state of paging and sorting
		PageAndSort savedPageAndSort = fetchSavedPageAndSort(httpSession);
		PageAndSort pageAndSort = jobListForm.getPageAndSort();
		pageAndSort.copyProperties(savedPageAndSort);
		
		if (FilterCommand.RESET.equals(filterForm.getFilterCommand())){
			filterForm.initialize();
		} else {
			filterFormValidator.validate(filterForm, errors);
		}
		pageAndSort.setPage(1);
		if (!errors.hasErrors()) {
			JobFilter filter = new JobFilter(filterForm.getFromDate(), filterForm.getToDate(), filterForm.getBatchStatus(),
											 filterForm.getTitleId(), filterForm.getBookName());
			JobSort jobSort = createJobSort(savedPageAndSort.getSort(), savedPageAndSort.isAscendingSort());
			jobExecutionIds = jobService.findJobExecutions(filter, jobSort);
		}
		setUpModel(jobExecutionIds, filterForm, pageAndSort, httpSession, model);
		return new ModelAndView(WebConstants.VIEW_JOB_LIST);
	}
	
	/**
	 * Handles the current paging and sorting state and creates the DisplayTag PaginatedList object
	 * for use by the DisplayTag custom tag in the JSP.
	 * @param pageAndSortForm paging/sorting/direction/display count
	 * @param jobExecutionIds list of 
	 * @param httpSession
	 * @param model
	 */
	private void setUpModel(List<Long> jobExecutionIds, FilterForm filterForm, PageAndSort pageAndSortForm,
							HttpSession httpSession, Model model) {
		
		// Save filter and paging state in the session
		httpSession.setAttribute(FilterForm.FORM_NAME, filterForm);
		httpSession.setAttribute(PageAndSort.class.getName(), pageAndSortForm);
		httpSession.setAttribute(WebConstants.KEY_JOB_EXECUTION_IDS, jobExecutionIds);
		
		// Create the DisplayTag VDO object - the PaginatedList which wrappers the job execution partial list
		PaginatedList paginatedList = createPaginatedList(jobExecutionIds, pageAndSortForm);
		model.addAttribute(WebConstants.KEY_PAGINATED_LIST, paginatedList);
	}
	
	/**
	 * Get the current list of job execution ID's saved on the session, if not present then fail-safe to fetching
	 * a new current list from the service.
	 * @return a list of job execution ID's
	 */
	@SuppressWarnings("unchecked")
	private List<Long> fetchSavedJobExecutionIdList(HttpSession httpSession) {
		List<Long> jobExecutionIds = (List<Long>) httpSession.getAttribute(WebConstants.KEY_JOB_EXECUTION_IDS);
		if (jobExecutionIds == null) {
			jobExecutionIds = jobService.findJobExecutions(new JobFilter(), new JobSort());
		}
		return jobExecutionIds;
	}
	
	private PageAndSort fetchSavedPageAndSort(HttpSession httpSession) {
		PageAndSort form = (PageAndSort) httpSession.getAttribute(PageAndSort.class.getName());
		if (form == null) {
			form = PageAndSort.createDefault();
		}
		return form;
	}
	private FilterForm fetchSavedFilterForm(HttpSession httpSession) {
		FilterForm form = (FilterForm) httpSession.getAttribute(FilterForm.FORM_NAME);
		if (form == null) {
			form = new FilterForm();
		}
		return form;
	}

	/**
	 * Map the sort property name returned by display tag to the busines object property name
	 * for sort used in the service.
	 * I.e. map a PageAndSortForm.DisplayTagSortProperty to a JobSort.SortProperty
	 * @param dtSortProperty display tag sort property key from the JSP
	 * @param ascendingSort true to sort in ascending order
	 * @return a job sort business object used by the service to fetch the job execution entities.
	 */
	private static JobSort createJobSort(DisplayTagSortProperty dtSortProperty, boolean ascendingSort) {
		switch (dtSortProperty) {
			case JOB_EXECUTION_ID:
				return new JobSort(SortProperty.jobExecutionId, ascendingSort);
			case JOB_INSTANCE_ID:
				return new JobSort(SortProperty.jobInstanceId, ascendingSort);
			case BATCH_STATUS:
				return new JobSort(SortProperty.batchStatus, ascendingSort);
			case START_TIME:
				return new JobSort(SortProperty.startTime, ascendingSort);
			case BOOK_NAME:
				return new JobSort(JobSort.SortParmeterKeyName.bookName, ascendingSort);
			case TITLE_ID:
				return new JobSort(JobSort.SortParmeterKeyName.titleIdFullyQualified, ascendingSort);
			default:
				throw new IllegalArgumentException("Unexpected DisplayTag sort property: " + dtSortProperty);
		}
	}

    private JobPaginatedList createPaginatedList(List<Long> jobExecutionIds, PageAndSort pageAndSort) { 
		int fullListSize = jobExecutionIds.size();
		// Calculate begin and end index for the current page number
		int fromIndex = (pageAndSort.getPage() - 1) * pageAndSort.getObjectsPerPage();
		int toIndex = fromIndex + pageAndSort.getObjectsPerPage();
		toIndex = (toIndex < jobExecutionIds.size()) ? toIndex : jobExecutionIds.size();
		
		// Get the subset of jobExecutionIds that will be displayed on the current page
		List<Long> jobExecutionIdSubList = jobExecutionIds.subList(fromIndex, toIndex);
		// Lookup all the JobExecution objects by their primary key
		List<JobExecution> jobExecutions = jobService.findJobExecutions(jobExecutionIdSubList);
		
		// Create the paginated list of View Data Objects (wrapping JobExecution) for use by DisplayTag table on the JSP.
		List<JobExecutionVdo> jobExecutionVdos = new ArrayList<JobExecutionVdo>();
		for (JobExecution je : jobExecutions) {
			jobExecutionVdos.add(new JobExecutionVdo(je));
		}
		
		JobPaginatedList paginatedList = new JobPaginatedList(jobExecutionVdos, fullListSize,
						pageAndSort.getPage(), pageAndSort.getObjectsPerPage(),
						pageAndSort.getSort(), pageAndSort.isAscendingSort());
		return paginatedList;
    }	

	@Required
	public void setJobService(JobService service) {
		this.jobService = service;
	}
	@Required
	public void setFilterFormValidator(FilterFormValidator validator) {
		this.filterFormValidator = validator;
	}
}
