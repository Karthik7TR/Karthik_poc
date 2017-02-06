/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.mgr.web.controller.job.queue;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

 import org.apache.log4j.LogManager; import org.apache.log4j.Logger;
import org.displaytag.pagination.PaginatedList;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.thomsonreuters.uscl.ereader.core.job.domain.JobRequest;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobRequestComparators;
import com.thomsonreuters.uscl.ereader.core.job.service.JobRequestService;
import com.thomsonreuters.uscl.ereader.core.outage.service.OutageService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.PageAndSort;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.job.queue.QueueForm.DisplayTagSortProperty;

/**
 * Creates the model of book generating jobs that are queued up to run, or that are scheduled to run
 * at some point in the future.
 */
@Controller
public class QueueController {
	private static final Logger log = LogManager.getLogger(QueueController.class);
	private JobRequestService jobRequestService;
	private OutageService outageService;
	private Validator validator;
	private static Map<DisplayTagSortProperty, Comparator<JobRequest>> comparators = new HashMap<DisplayTagSortProperty, Comparator<JobRequest>>();
	static {
		comparators.put(DisplayTagSortProperty.BOOK_NAME, new JobRequestComparators.BookNameComparator());
		comparators.put(DisplayTagSortProperty.BOOK_VERSION, new JobRequestComparators.BookVersionComparator());
		comparators.put(DisplayTagSortProperty.PRIORITY, new JobRequestComparators.PriorityComparator());
		comparators.put(DisplayTagSortProperty.SUBMITTED_AT, new JobRequestComparators.SubmittedAtComparator());
		comparators.put(DisplayTagSortProperty.SUBMITTED_BY, new JobRequestComparators.SubmittedByComparator());
		comparators.put(DisplayTagSortProperty.TITLE_ID, new JobRequestComparators.TitleIdComparator());
		comparators.put(DisplayTagSortProperty.SOURCE_TYPE, new JobRequestComparators.SourceTypeComparator());
	}

	@InitBinder(QueueForm.FORM_NAME)
	protected void initDataBinder(WebDataBinder binder) {
		binder.setValidator(validator);
	}

	/**
	 * Handle initial in-bound HTTP get request to the page.
	 * No query string parameters are expected.
	 */
	@RequestMapping(value=WebConstants.MVC_JOB_QUEUE, method = RequestMethod.GET)
	public ModelAndView inboundGet(HttpSession httpSession, Model model) {
		log.debug(">>>");
		PageAndSort<DisplayTagSortProperty> queuedPageAndSort = fetchSavedQueuedPageAndSort(httpSession);
		List<JobRequest> allQueuedJobs = jobRequestService.findAllJobRequests();

		setUpModel(allQueuedJobs, queuedPageAndSort, httpSession, model);
		return new ModelAndView(WebConstants.VIEW_JOB_QUEUE);
	}
	
	@RequestMapping(value=WebConstants.MVC_JOB_QUEUE_PAGE_AND_SORT, method = RequestMethod.GET)
	public ModelAndView doPagingAndSorting(HttpSession httpSession, 
								@ModelAttribute(QueueForm.FORM_NAME) QueueForm form,
								Model model) {
		log.debug(form);
		PageAndSort<DisplayTagSortProperty> pageAndSort = fetchSavedQueuedPageAndSort(httpSession);
		List<JobRequest> allQueuedJobs = jobRequestService.findAllJobRequests();
		
		Integer nextPageNumber = form.getPage();
		// If there was a page=n query string parameter, then we assume we are paging since this
		// parameter is not present on the query string when display tag sorting.
		if (nextPageNumber != null) {  // PAGING (1..n)
			// Guard against a shrinking list and user selecting a forward page that no longer exists because the count of jobs has dropped
			int startIndex = (nextPageNumber-1) * pageAndSort.getObjectsPerPage();
			if (startIndex >= allQueuedJobs.size()) {
				nextPageNumber = 1;
			}
			pageAndSort.setPageNumber(nextPageNumber);
		} else {  // SORTING
			pageAndSort.setPageNumber(1);
			pageAndSort.setSortProperty(form.getSortProperty());
			pageAndSort.setAscendingSort(form.isAscendingSort());
			
		}
		setUpModel(allQueuedJobs, pageAndSort, httpSession, model);
		return new ModelAndView(WebConstants.VIEW_JOB_QUEUE);
	}
	
	@SuppressWarnings("unchecked")
	private PageAndSort<DisplayTagSortProperty> fetchSavedQueuedPageAndSort(HttpSession httpSession) {
		PageAndSort<DisplayTagSortProperty> pageAndSort = (PageAndSort<DisplayTagSortProperty>) httpSession.getAttribute(WebConstants.KEY_JOB_QUEUED_PAGE_AND_SORT);
		if (pageAndSort == null) {
			pageAndSort = new PageAndSort<DisplayTagSortProperty>(1, DisplayTagSortProperty.PRIORITY, false);
		}
		return pageAndSort;
	}

	private void setUpModel(List<JobRequest> allQueuedJobs, PageAndSort<DisplayTagSortProperty> queuedPageAndSort, 
							HttpSession httpSession, Model model) {
		httpSession.setAttribute(WebConstants.KEY_JOB_QUEUED_PAGE_AND_SORT, queuedPageAndSort);
		
		// Create the DisplayTag VDO object - the PaginatedList which wrappers list
		PaginatedList queuedPaginatedList = createPaginatedList(allQueuedJobs, queuedPageAndSort);
		model.addAttribute(WebConstants.KEY_PAGINATED_LIST, queuedPaginatedList);
		model.addAttribute(WebConstants.KEY_DISPLAY_OUTAGE, outageService.getAllPlannedOutagesToDisplay());
	}

	/**
	 * Wrapper one page of tabular data rows into a DisplayTag (view) presentation container PaginatedList
	 * that is used by the DisplayTag custom tag in the JSP to render the data in tabular form.
	 */
	private PaginatedList createPaginatedList(List<JobRequest> allQueuedJobs,
											  PageAndSort<DisplayTagSortProperty> queuedPageAndSort) {

		List<JobRequest> onePageOfRows = createOnePageOfRows(allQueuedJobs, queuedPageAndSort);
		// Instantiate the object used by DisplayTag to render a partial list
		QueuePaginatedList<DisplayTagSortProperty> paginatedList =
					new QueuePaginatedList<DisplayTagSortProperty>(onePageOfRows, allQueuedJobs.size(),
					queuedPageAndSort.getPageNumber(), queuedPageAndSort.getObjectsPerPage(),
					queuedPageAndSort.getSortProperty(), queuedPageAndSort.isAscendingSort());
		return paginatedList;
	}
	
	/**
	 * Create a list of view data object (VDO) row job objects that hold the data for the current page of 
	 * data to be displayed in the table.  This is done because we have both job and book information,
	 * along with the run sequence number to display in tabular form, so we wrapper all the objects in a VDO so that
	 * they are accessible as properties of a single list object when used in the view/JSP.
	 * @param allQueuedJobs the queued jobs, does not assume any existing sort order 
	 */
	private List<JobRequest> createOnePageOfRows(List<JobRequest> allQueuedJobs,
			  								PageAndSort<DisplayTagSortProperty> queuedPageAndSort) {

		// Sort into the order/sequence that the jobs will actually appear on the page
		Comparator<JobRequest> rowComparator = comparators.get(queuedPageAndSort.getSortProperty());
		Collections.sort(allQueuedJobs, rowComparator);
		if (!queuedPageAndSort.isAscendingSort()) {
			Collections.reverse(allQueuedJobs);
		}
		
		// Get the subset of objects that will be displayed on the current page.
		int fromIndex = (queuedPageAndSort.getPageNumber() - 1) * queuedPageAndSort.getObjectsPerPage();
		int toIndex = fromIndex + queuedPageAndSort.getObjectsPerPage();
		toIndex = (toIndex < allQueuedJobs.size()) ? toIndex : allQueuedJobs.size();
		List<JobRequest> onePageOfRows = allQueuedJobs.subList(fromIndex, toIndex);

		return onePageOfRows;
	}
	
	@Required
	public void setJobRequestService(JobRequestService service) {
		this.jobRequestService = service;
	}
	@Required
	public void setValidator(QueueFormValidator validator) {
		this.validator = validator;
	}
	@Required
	public void setOutageService(OutageService service) {
		this.outageService = service;
	}
}
