/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.job.list;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpSession;

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
import com.thomsonreuters.uscl.ereader.mgr.web.controller.job.list.PageAndSortForm.DisplayTagSortProperty;


@Controller
public class JobListController {
	//private static final Logger log = Logger.getLogger(JobListController.class);
	private JobService jobService;
	private FilterFormValidator filterFormValidator;
	
//	@InitBinder(FilterForm.FORM_NAME)
//	protected void initDataBinder(WebDataBinder binder) {
//		binder.setValidator(filterFormValidator);
//	}

	/**
	 * Handle initial in-bound HTTP get request to the page.
	 * No query string parameters are expected.
	 */
	@RequestMapping(value=WebConstants.MVC_JOB_LIST, method = RequestMethod.GET)
	public ModelAndView doGet(HttpSession httpSession, 
							  @ModelAttribute(FilterForm.FORM_NAME) FilterForm filterForm,
							  @ModelAttribute(JobListForm.FORM_NAME) JobListForm jobListForm,
							  @ModelAttribute(PageAndSortForm.FORM_NAME) PageAndSortForm pageAndSortForm,
							  Model model) throws Exception {
		
		pageAndSortForm.initialize(1, PageAndSortForm.DEFAULT_ITEMS_PER_PAGE,
								   DisplayTagSortProperty.START_TIME, false);
		FilterForm savedFilterForm = fetchSavedFilterForm(httpSession);
		filterForm.copy(savedFilterForm);
		JobFilter jobFilter = new JobFilter(filterForm.getFromDate(), filterForm.getToDate(), filterForm.getBatchStatus(),
										 filterForm.getTitleId(), filterForm.getBookName());
		JobSort jobSort = new JobSort();
		List<Long> jobExecutionIds = jobService.findJobExecutions(jobFilter, jobSort);
		
		setUpModel(jobExecutionIds, filterForm, pageAndSortForm, httpSession, model);
	
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
								@ModelAttribute(PageAndSortForm.FORM_NAME) PageAndSortForm pageAndSortForm,
								Model model) throws Exception {
		List<Long> jobExecutionIds = null;
		
		// Restore the state of the search filter form
		FilterForm savedFilterForm = fetchSavedFilterForm(httpSession);
		filterForm.copy(savedFilterForm);
		
		// Set up the sorting/paging depending upon what the user selected
		PageAndSortForm savedPageAndSortForm = fetchSavedPageAndSortForm(httpSession);
		Integer nextPageNumber = pageAndSortForm.getPage();
		// If there was a page=n query string parameter, then we assume we are paging since this
		// parameter is not present on the query string when display tag sorting.
		if (nextPageNumber != null) {
			pageAndSortForm.copy(savedPageAndSortForm);
			pageAndSortForm.setPage(nextPageNumber);
			jobExecutionIds = fetchSavedJobExecutionIdList(httpSession);
		} else {  // SORTING
			pageAndSortForm.setPage(1);
			pageAndSortForm.setItemsPerPage(savedPageAndSortForm.getItemsPerPage());
			// Fetch the job list model
			JobFilter jobFilter = new JobFilter(filterForm.getFromDate(), filterForm.getToDate(), filterForm.getBatchStatus(),
					 filterForm.getTitleId(), filterForm.getBookName());
			JobSort jobSort = createJobSort(pageAndSortForm.getSort(), pageAndSortForm.isAscendingSort());
			jobExecutionIds = jobService.findJobExecutions(jobFilter, jobSort);
		}
		
		setUpModel(jobExecutionIds, filterForm, pageAndSortForm, httpSession, model);
		
		return new ModelAndView(WebConstants.VIEW_JOB_LIST);
	}

	@RequestMapping(value=WebConstants.MVC_JOB_LIST_POST, method = RequestMethod.POST)
	public ModelAndView doPost(HttpSession httpSession) throws Exception {
// TODO: implement this
		return null;
	}

	/**
	 * Handle submit/post of a new set of filter criteria.
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value=WebConstants.MVC_JOB_LIST_FILTER_POST, method = RequestMethod.POST)
	public ModelAndView doFilterPost(HttpSession httpSession,
						@ModelAttribute(FilterForm.FORM_NAME) FilterForm filterForm,
						@ModelAttribute(JobListForm.FORM_NAME) JobListForm jobListForm,
						@ModelAttribute(PageAndSortForm.FORM_NAME) PageAndSortForm pageAndSortForm,
						BindingResult errors,
						Model model) throws Exception {

		// Configure to sort on the same column and sort direction as user has already chose 
		PageAndSortForm savedPageAndSortForm = fetchSavedPageAndSortForm(httpSession);
		pageAndSortForm.copy(savedPageAndSortForm);
		pageAndSortForm.setPage(1);
		
		filterFormValidator.validate(filterForm, errors);
		List<Long> jobExecutionIds = null;
		if (!errors.hasErrors()) {
			JobFilter filter = new JobFilter(filterForm.getFromDate(), filterForm.getToDate(), filterForm.getBatchStatus(),
											 filterForm.getTitleId(), filterForm.getBookName());
			JobSort jobSort = createJobSort(savedPageAndSortForm.getSort(), savedPageAndSortForm.isAscendingSort());
			jobExecutionIds = jobService.findJobExecutions(filter, jobSort);
		} else {
			jobExecutionIds = Collections.EMPTY_LIST;
		}
		setUpModel(jobExecutionIds, filterForm, pageAndSortForm, httpSession, model);
		return new ModelAndView(WebConstants.VIEW_JOB_LIST);
	}

	/**
	 * Change the number of rows displayed in the table.
	 */
	@RequestMapping(value=WebConstants.MVC_JOB_LIST_ITEMS_PER_PAGE, method = RequestMethod.POST)
	public ModelAndView doChangeItemsPerPage(HttpSession httpSession, 
			@ModelAttribute(FilterForm.FORM_NAME) FilterForm filterForm,
			@ModelAttribute(JobListForm.FORM_NAME) JobListForm jobListForm,
			@ModelAttribute(PageAndSortForm.FORM_NAME) PageAndSortForm pageAndSortForm,
			Model model) throws Exception {
		Integer itemsPerPage = pageAndSortForm.getItemsPerPage();
		
		// Restore the state of the search filter
		FilterForm savedFilterForm = fetchSavedFilterForm(httpSession);
		filterForm.copy(savedFilterForm);
		
		// Fetch the current paging/sorting state from the session
		PageAndSortForm sessionPageAndSortForm = fetchSavedPageAndSortForm(httpSession);
		pageAndSortForm.copy(sessionPageAndSortForm);
		pageAndSortForm.setItemsPerPage(itemsPerPage);	// Update the new number of items to be shown at one time
		
		List<Long> jobExecutionIds = fetchSavedJobExecutionIdList(httpSession);
		
		setUpModel(jobExecutionIds, filterForm, pageAndSortForm, httpSession, model);
		
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
	private void setUpModel(List<Long> jobExecutionIds, FilterForm filterForm, PageAndSortForm pageAndSortForm,
							HttpSession httpSession, Model model) {
		
		// Save filter and paging state in the session
		httpSession.setAttribute(FilterForm.FORM_NAME, filterForm);
		httpSession.setAttribute(PageAndSortForm.FORM_NAME, pageAndSortForm);
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
	
	private PageAndSortForm fetchSavedPageAndSortForm(HttpSession httpSession) {
		PageAndSortForm form = (PageAndSortForm) httpSession.getAttribute(PageAndSortForm.FORM_NAME);
		if (form == null) {
			form = PageAndSortForm.createDefault();
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

    private JobPaginatedList createPaginatedList(List<Long> jobExecutionIds, PageAndSortForm form) { 
		int fullListSize = jobExecutionIds.size();
		// Calculate begin and end index for the current page number
		int fromIndex = (form.getPage() - 1) * form.getItemsPerPage();
		int toIndex = fromIndex + form.getItemsPerPage();
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
						form.getPage(), form.getItemsPerPage(),
						form.getSort(), form.isAscendingSort());
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
