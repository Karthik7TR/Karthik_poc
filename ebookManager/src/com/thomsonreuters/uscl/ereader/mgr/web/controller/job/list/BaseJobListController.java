/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.job.list;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.displaytag.pagination.PaginatedList;
import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import com.thomsonreuters.uscl.ereader.core.job.domain.JobFilter;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobSort;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobSort.SortProperty;
import com.thomsonreuters.uscl.ereader.core.job.service.JobService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.job.list.PageAndSort.DisplayTagSortProperty;

/**
 * Methods common to, and needed by both the JobListController and the FilterFormController.
 */
@Controller
public abstract class BaseJobListController {
	//private static final Logger log = Logger.getLogger(BaseJobListController.class);
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
			jobExecutionIds = jobService.findJobExecutions(new JobFilter(), new JobSort());
		}
		return jobExecutionIds;
	}
	
	protected PageAndSort fetchSavedPageAndSort(HttpSession httpSession) {
		PageAndSort form = (PageAndSort) httpSession.getAttribute(PageAndSort.class.getName());
		if (form == null) {
			form = PageAndSort.createDefault();
		}
		return form;
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
	protected void setUpModel(List<Long> jobExecutionIds, FilterForm filterForm, PageAndSort pageAndSortForm,
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
	 * Map the sort property name returned by display tag to the busines object property name
	 * for sort used in the service.
	 * I.e. map a PageAndSortForm.DisplayTagSortProperty to a JobSort.SortProperty
	 * @param dtSortProperty display tag sort property key from the JSP
	 * @param ascendingSort true to sort in ascending order
	 * @return a job sort business object used by the service to fetch the job execution entities.
	 */
	protected static JobSort createJobSort(DisplayTagSortProperty dtSortProperty, boolean ascendingSort) {
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
}
