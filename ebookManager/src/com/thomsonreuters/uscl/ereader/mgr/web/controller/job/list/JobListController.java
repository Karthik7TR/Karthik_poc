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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.thomsonreuters.uscl.ereader.core.domain.JobFilter;
import com.thomsonreuters.uscl.ereader.core.domain.JobSort;
import com.thomsonreuters.uscl.ereader.core.domain.JobSort.SortProperty;
import com.thomsonreuters.uscl.ereader.core.service.JobService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.job.list.PageAndSort.DisplayTagSortProperty;

@Controller
public class JobListController {
	
	private JobService jobService;
	
	@RequestMapping(value=WebConstants.MVC_JOB_LIST_GET, method = RequestMethod.GET)
	public ModelAndView doGet(HttpSession httpSession, 
							  @ModelAttribute(JobListForm.FORM_NAME) JobListForm form,
							  Model model) throws Exception {
		//initializeForm(form, httpSession);
		
		JobFilter filter = new JobFilter();
		JobSort sort = new JobSort(SortProperty.startTime, false);
		
		List<Long> jobExecutionIds = jobService.findJobExecutions(filter, sort);
		PaginatedList paginatedList = createPaginatedList(jobExecutionIds, 1, JobListForm.DEFAULT_ITEMS_PER_PAGE, DisplayTagSortProperty.START_TIME, false);
		model.addAttribute(WebConstants.KEY_PAGINATED_LIST, paginatedList);
		
		return new ModelAndView(WebConstants.VIEW_JOB_LIST);
	}

//	TODO
//	public static void initializeFilterForm(FilterForm form, HttpSession httpSession) {
//		FilterForm sessionForm = (FilterForm) httpSession.getAttribute(WebConstants.KEY_SESSION_xxx_FORM);
//		form.setItemsPerPage(JobSummaryForm.DEFAULT_ITEMS_PER_PAGE);
//		if (sessionForm == null) { 
//			Calendar cal = Calendar.getInstance();
//			cal.add(Calendar.DAY_OF_MONTH, -1);
//			form.setStartDate(WebConstants.DATE_FORMAT.format(cal.getTime()));  // mm/dd/yyyy
//		} else {
//			form.copyUserFields(sessionForm);
//		}
//	}

// TODO
//	@RequestMapping(value=WebConstants.MVC_JOB_LIST_PAGE_SORT, method = RequestMethod.GET)
//	public ModelAndView doPagingAndSorting(HttpSession httpSession, 
//							  @ModelAttribute(PageAndSortQueryString.FORM_NAME) PageAndSortQueryString pageAndSortForm,
//							  Model model) throws Exception {
//		initializeForm(pageAndSortForm, httpSession);
//		JobFilter jobFilter = new JobFilter();
////	how do I determine if i am sorting on a execution vs job parameter property?
//		JobSort jobSortInfo = new JobSort(pageAndSortForm.getSortProperty(), pageAndSortForm.isSortAscending());
//		List<Long> jobExecutionIds = jobService.findJobExecutions(jobFilter, paramFilter, jobSortInfo);
//		PaginatedList paginatedList = createPaginatedList(jobExecutionIds, pageAndSortForm.getPage(), ITEMS_PER_PAGE, jobSortInfo);
//		//populateModel(model, httpSession, paginatedList);
//		
//// TODO: implement this
//		return null; // new ModelAndView(WebConstants.VIEW_JOB_LIST);
//	}
	
	public static void initializeForm(PageAndSort form, HttpSession httpSession) {
// TODO: sample from Job Summary
//		FilterForm sessionForm = (FilterForm) httpSession.getAttribute(FilterForm.FORM_NAME);
//		if (sessionForm == null) { 
//			Calendar cal = Calendar.getInstance();
//			cal.add(Calendar.DAY_OF_MONTH, -1);
//			form.setStartDate(WebConstants.DATE_FORMAT.format(cal.getTime()));  // mm/dd/yyyy
//		} else {
//			form.copyUserFields(sessionForm);
//		}
	}

    private JobPaginatedList createPaginatedList(List<Long> jobExecutionIds, int pageNumber, int itemsPerPage, 
    											 DisplayTagSortProperty sortProperty, boolean ascending) {
		int fullListSize = jobExecutionIds.size();
		// Calculate begin and end index for the current page number
		int fromIndex = (pageNumber - 1) * itemsPerPage;
		int toIndex = fromIndex + itemsPerPage;
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
		
		JobPaginatedList paginatedList = new JobPaginatedList(jobExecutionVdos, fullListSize, pageNumber, itemsPerPage, sortProperty, ascending);
		return paginatedList;
    }	

	
	@RequestMapping(value=WebConstants.MVC_JOB_LIST_POST, method = RequestMethod.POST)
	public ModelAndView doPost(HttpSession httpSession) throws Exception {
// TODO: implement this
		return null;
	}
	
	@Required
	public void setJobService(JobService service) {
		this.jobService = service;
	}
}
