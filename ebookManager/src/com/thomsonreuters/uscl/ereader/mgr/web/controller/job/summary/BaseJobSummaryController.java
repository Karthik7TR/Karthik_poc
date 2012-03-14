/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.job.summary;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.displaytag.pagination.PaginatedList;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.ui.Model;

import com.thomsonreuters.uscl.ereader.core.job.domain.JobSort;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobSort.SortProperty;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobSummary;
import com.thomsonreuters.uscl.ereader.core.job.service.JobService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.PageAndSort;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.job.summary.JobSummaryForm.DisplayTagSortProperty;

/**
 * Methods common to, and needed by both the JobSummaryController and the FilterFormController.
 */
public abstract class BaseJobSummaryController {
	//private static final Logger log = Logger.getLogger(BaseJobSummaryController.class);
	protected JobService jobService;
	
	/**
	 * Get the current list of job execution ID's saved on the session, if not present then fail-safe to fetching
	 * a new current list from the service.
	 * @return a list of job execution ID's
	 */
	@SuppressWarnings("unchecked")
	protected List<Long> fetchSavedJobExecutionIdList(HttpSession httpSession) {
		List<Long> jobExecutionIds = (List<Long>) httpSession.getAttribute(WebConstants.KEY_JOB_EXECUTION_IDS);
		if (jobExecutionIds == null) {
			jobExecutionIds = Collections.EMPTY_LIST;
		}
		return jobExecutionIds;
	}
	
	/**
	 * Fetch object containing the current page number, sort column, and sort direction as saved on the session.
	 */
	protected PageAndSort<DisplayTagSortProperty> fetchSavedPageAndSort(HttpSession httpSession) {
		PageAndSort<DisplayTagSortProperty> pageAndSort = (PageAndSort<DisplayTagSortProperty>) httpSession.getAttribute(PageAndSort.class.getName());
		if (pageAndSort == null) {
			pageAndSort = new PageAndSort<DisplayTagSortProperty>(1, PageAndSort.DEFAULT_ITEMS_PER_PAGE, DisplayTagSortProperty.START_TIME, false);
		}
		return pageAndSort;
	}
	protected FilterForm fetchSavedFilterForm(HttpSession httpSession) {
		FilterForm form = (FilterForm) httpSession.getAttribute(FilterForm.FORM_NAME);
		if (form == null) {
			form = new FilterForm();
		}
		return form;
	}
	
	/**
	 * Handles the current paging and sorting state and creates the DisplayTag PaginatedList object
	 * for use by the DisplayTag custom tag in the JSP.
	 * @param pageAndSortForm paging/sorting/direction/display count
	 * @param jobExecutionIds list of 
	 * @param httpSession
	 * @param model
	 */
	protected void setUpModel(List<Long> jobExecutionIds, FilterForm filterForm, PageAndSort pageAndSort,
							  HttpSession httpSession, Model model) {
		
		// Save filter and paging state in the session
		httpSession.setAttribute(FilterForm.FORM_NAME, filterForm);
		httpSession.setAttribute(PageAndSort.class.getName(), pageAndSort);
		httpSession.setAttribute(WebConstants.KEY_JOB_EXECUTION_IDS, jobExecutionIds);
		
		model.addAttribute(FilterForm.FORM_NAME, filterForm);
		
		// Create the DisplayTag VDO object - the PaginatedList which wrappers the job execution partial list
		PaginatedList paginatedList = createPaginatedList(jobExecutionIds, pageAndSort);
		model.addAttribute(WebConstants.KEY_PAGINATED_LIST, paginatedList);
	}
	
	/**
	 * Map the sort property name returned by display tag to the business object property name
	 * for sort used in the service.
	 * I.e. map a PageAndSortForm.DisplayTagSortProperty to a JobSort.SortProperty
	 * @param dtSortProperty display tag sort property key from the JSP
	 * @param ascendingSort true to sort in ascending order
	 * @return a job sort business object used by the service to fetch the job execution entities.
	 */
	protected static JobSort createJobSort(DisplayTagSortProperty dtSortProperty, boolean ascendingSort) {
		
		return new JobSort(SortProperty.valueOf(dtSortProperty.toString()), ascendingSort);
	}
	
	/**
	 * Create the partial paginated list used by DisplayTag to render to current page number of 
	 * list list of objects.
	 * @param jobExecutionIds primary key for the set of job executions to fetch/display.
	 * @param pageAndSort current page number, sort column, and sort direction (asc/desc).
	 * @return an implemented DisplayTag paginated list interface
	 */
    private PaginatedList createPaginatedList(List<Long> jobExecutionIds, PageAndSort pageAndSort) {
		// Calculate begin and end index for the current page number
		int fromIndex = (pageAndSort.getPageNumber() - 1) * pageAndSort.getObjectsPerPage();
		int toIndex = fromIndex + pageAndSort.getObjectsPerPage();
		toIndex = (toIndex < jobExecutionIds.size()) ? toIndex : jobExecutionIds.size();
		
		// Get the subset of jobExecutionIds that will be displayed on the current page
		List<Long> jobExecutionIdSubList = jobExecutionIds.subList(fromIndex, toIndex);
		// Lookup all the JobExecution objects by their primary key
		List<JobSummary> jobs = jobService.findJobSummary(jobExecutionIdSubList);
		//List<JobExecution> jobExecutions = jobService.findJobExecutions(jobExecutionIdSubList);
		
		// Create the paginated list of View Data Objects (wrapping JobExecution) for use by DisplayTag table on the JSP.
//		List<JobExecutionVdo> jobExecutionVdos = new ArrayList<JobExecutionVdo>();
//		for (JobExecution je : jobExecutions) {
//			JobInstanceBookInfo bookInfo = jobService.findJobInstanceBookInfo(je.getJobId());
//			jobExecutionVdos.add(new JobExecutionVdo(je, bookInfo));
//		}

		// Instantiate the object used by DisplayTag to render a partial list
		JobPaginatedList paginatedList = new JobPaginatedList(jobs,
								jobExecutionIds.size(),
								pageAndSort.getPageNumber(), pageAndSort.getObjectsPerPage(),
								(DisplayTagSortProperty) pageAndSort.getSortProperty(),
								pageAndSort.isAscendingSort());
		return paginatedList;
    }

	@Required
	public void setJobService(JobService service) {
		this.jobService = service;
	}
}
