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
	private static final Logger log = Logger.getLogger(JobListController.class);
	private JobService jobService;
	
	/**
	 * Handle initial in-bound HTTP get request to the page.
	 * No query string parameters are expected.
	 */
	@RequestMapping(value=WebConstants.MVC_JOB_LIST_GET, method = RequestMethod.GET)
	public ModelAndView doGet(HttpSession httpSession, 
							  @ModelAttribute(JobListForm.FORM_NAME) JobListForm jobListForm,
							  Model model) throws Exception {
		PageAndSortForm pageAndSortForm = new PageAndSortForm(1, PageAndSortForm.DEFAULT_ITEMS_PER_PAGE,
															  DisplayTagSortProperty.START_TIME, false);
		JobFilter filter = new JobFilter(); // TODO: define the filter
		JobSort jobSort = createJobSort(DisplayTagSortProperty.START_TIME, false);
		List<Long> jobExecutionIds = jobService.findJobExecutions(filter, jobSort);
		
		setUpModel(jobExecutionIds, pageAndSortForm, httpSession, model);
		model.addAttribute(PageAndSortForm.FORM_NAME, pageAndSortForm);
	
		return new ModelAndView(WebConstants.VIEW_JOB_LIST);
	}
	
	/**
	 * Handle paging and sorting of job list.
	 * Handles clicking of column headers to sort, or use of page number navigation links, like prev/next.
	 */
	@RequestMapping(value=WebConstants.MVC_JOB_LIST_PAGE_AND_SORT, method = RequestMethod.GET)
	public ModelAndView doPagingAndSorting(HttpSession httpSession, 
			 				  @ModelAttribute(JobListForm.FORM_NAME) JobListForm jobListForm,
							  @ModelAttribute(PageAndSortForm.FORM_NAME) PageAndSortForm pageAndSortForm,
							  Model model) throws Exception {
		List<Long> jobExecutionIds = null;
		PageAndSortForm savedPageAndSortForm = fetchSavedPageAndSortForm(httpSession);

		Integer nextPageNumber = pageAndSortForm.getPage();
		// If there was a page=n query string parameter, then we assume we are paging since this
		// parameter is not present on the query string when display tag sorting.
		if (nextPageNumber != null) {
			pageAndSortForm.copy(savedPageAndSortForm);
			pageAndSortForm.setPage(nextPageNumber);
			jobExecutionIds = fetchCurrentJobExecutionIdList(httpSession);
		} else {  // SORTING
			pageAndSortForm.setPage(1);
			pageAndSortForm.setItemsPerPage(savedPageAndSortForm.getItemsPerPage());
			// Fetch the job list model
			JobFilter filter = new JobFilter();  // TODO: map the filter form to this business object
			JobSort jobSort = createJobSort(pageAndSortForm.getSort(), pageAndSortForm.isAscendingSort());
			jobExecutionIds = jobService.findJobExecutions(filter, jobSort);
		}
		
		setUpModel(jobExecutionIds, pageAndSortForm, httpSession, model);
		
		return new ModelAndView(WebConstants.VIEW_JOB_LIST);
	}
	
	@RequestMapping(value=WebConstants.MVC_JOB_LIST_POST, method = RequestMethod.POST)
	public ModelAndView doPost(HttpSession httpSession) throws Exception {
// TODO: implement this
		return null;
	}
	
	@RequestMapping(value=WebConstants.MVC_JOB_LIST_ITEMS, method = RequestMethod.POST)
	public ModelAndView doChangeItemsPerPage(HttpSession httpSession, 
			@ModelAttribute(JobListForm.FORM_NAME) JobListForm jobListForm,
			@ModelAttribute(PageAndSortForm.FORM_NAME) PageAndSortForm pageAndSortForm,
			Model model) throws Exception {
		Integer itemsPerPage = pageAndSortForm.getItemsPerPage();
		
		// Fetch the current paging/sorting state from the session
		PageAndSortForm sessionPageAndSortForm = fetchSavedPageAndSortForm(httpSession);
		pageAndSortForm.copy(sessionPageAndSortForm);
		pageAndSortForm.setItemsPerPage(itemsPerPage);	// Update the new number of items to be shown at one time
		
		List<Long> jobExecutionIds = fetchCurrentJobExecutionIdList(httpSession);
		
		setUpModel(jobExecutionIds, pageAndSortForm, httpSession, model);
		
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
	private void setUpModel(List<Long> jobExecutionIds, PageAndSortForm pageAndSortForm,
							HttpSession httpSession, Model model) {
		// Create the object used by the DisplayTag custom tag to render the table
		PaginatedList paginatedList = createPaginatedList(jobExecutionIds, pageAndSortForm);
log.debug("session saving: " + pageAndSortForm);		
		httpSession.setAttribute(PageAndSortForm.FORM_NAME, pageAndSortForm);
		httpSession.setAttribute(WebConstants.KEY_JOB_EXECUTION_IDS, jobExecutionIds);
		model.addAttribute(WebConstants.KEY_PAGINATED_LIST, paginatedList);
	}
	
	/**
	 * Get the current list of job execution ID's saved on the session, if not present then fail-safe to fetching
	 * a new current list from the service.
	 * @return a list of job execution ID's
	 */
	@SuppressWarnings("unchecked")
	private List<Long> fetchCurrentJobExecutionIdList(HttpSession httpSession) {
		List<Long> jobExecutionIds = (List<Long>) httpSession.getAttribute(WebConstants.KEY_JOB_EXECUTION_IDS);
		if (jobExecutionIds == null) {
			JobFilter filter = new JobFilter(); // TODO: deal with current filter form
			JobSort jobSort = new JobSort(SortProperty.startTime, false);
			jobExecutionIds = jobService.findJobExecutions(filter, jobSort);
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

	private static JobSort createJobSort(DisplayTagSortProperty dtSortProperty, boolean ascendingSort) {
		switch (dtSortProperty) {
			case BATCH_STATUS:
				return new JobSort(SortProperty.batchStatus, ascendingSort);
			case JOB_INSTANCE_ID:
				return new JobSort(SortProperty.jobInstanceId, ascendingSort);
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

	public static void initializeForm(JobListForm form, HttpSession httpSession) {
	
//	PageAndSortForm pageAndSortForm = (PageAndSortForm) httpSession.getAttribute(PageAndSortForm.FORM_NAME);
//	form.setItemsPerPage(JobSummaryForm.DEFAULT_ITEMS_PER_PAGE);
//	if (sessionForm == null) { 
//		Calendar cal = Calendar.getInstance();
//		cal.add(Calendar.DAY_OF_MONTH, -1);
//		form.setStartDate(WebConstants.DATE_FORMAT.format(cal.getTime()));  // mm/dd/yyyy
//	} else {
//		form.copyUserFields(sessionForm);
//	}
//}
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
}
