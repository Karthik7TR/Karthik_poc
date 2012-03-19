/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.job.queue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
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

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobRequest;
import com.thomsonreuters.uscl.ereader.core.job.service.JobRequestService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.PageAndSort;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.job.queue.QueueForm.DisplayTagSortProperty;

/**
 * Creates the model of book generating jobs that are queued up to run, or that are scheduled to run
 * at some point in the future.
 */
@Controller
public class QueueController {
	private static final Logger log = Logger.getLogger(QueueController.class);
	private BookDefinitionService bookDefinitionService;
	private JobRequestService jobRequestService;
	private Validator validator;
	private static Map<DisplayTagSortProperty, Comparator<JobRequestRow>> comparators = new HashMap<DisplayTagSortProperty, Comparator<JobRequestRow>>();
	static {
		comparators.put(DisplayTagSortProperty.BOOK_NAME, new JobRequestRowComparators.BookNameComparator());
		comparators.put(DisplayTagSortProperty.BOOK_VERSION, new JobRequestRowComparators.BookVersionComparator());
		comparators.put(DisplayTagSortProperty.PRIORITY, new JobRequestRowComparators.PriorityComparator());
		comparators.put(DisplayTagSortProperty.SUBMITTED_AT, new JobRequestRowComparators.SubmittedAtComparator());
		comparators.put(DisplayTagSortProperty.SUBMITTED_BY, new JobRequestRowComparators.SubmittedByComparator());
		comparators.put(DisplayTagSortProperty.TITLE_ID, new JobRequestRowComparators.TitleIdComparator());
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
		PageAndSort<DisplayTagSortProperty> queuedPageAndSort = fetchSavedQueuedPageAndSort(httpSession);
		List<JobRequest> allQueuedJobs = jobRequestService.findAllJobRequests();
log.debug(allQueuedJobs); // DEBUG		
		setUpModel(allQueuedJobs, queuedPageAndSort, httpSession, model);
		return new ModelAndView(WebConstants.VIEW_JOB_QUEUE);
	}
	
	@RequestMapping(value=WebConstants.MVC_JOB_QUEUE_PAGE_AND_SORT, method = RequestMethod.GET)
	public ModelAndView doPagingAndSorting(HttpSession httpSession, 
								@ModelAttribute(QueueForm.FORM_NAME) QueueForm form,
								Model model) {
		log.debug(form);
		PageAndSort<DisplayTagSortProperty> pageAndSort = fetchSavedQueuedPageAndSort(httpSession);
		List<JobRequest> allQueuedJobs = null;
		
		Integer nextPageNumber = form.getPage();
		// If there was a page=n query string parameter, then we assume we are paging since this
		// parameter is not present on the query string when display tag sorting.
		if (nextPageNumber != null) {  // PAGING
			allQueuedJobs = fetchSavedQueuedJobs(httpSession);
			pageAndSort.setPageNumber(nextPageNumber);
		} else {  // SORTING
			pageAndSort.setPageNumber(1);
			pageAndSort.setSortProperty(form.getSortProperty());
			pageAndSort.setAscendingSort(form.isAscendingSort());
			allQueuedJobs = jobRequestService.findAllJobRequests();
		}
		setUpModel(allQueuedJobs, pageAndSort, httpSession, model);
		return new ModelAndView(WebConstants.VIEW_JOB_QUEUE);
	}
	
	@SuppressWarnings("unchecked")
	private PageAndSort<DisplayTagSortProperty> fetchSavedQueuedPageAndSort(HttpSession httpSession) {
		PageAndSort<DisplayTagSortProperty> pageAndSort = (PageAndSort<DisplayTagSortProperty>) httpSession.getAttribute(WebConstants.KEY_JOB_QUEUED_PAGE_AND_SORT);
		if (pageAndSort == null) {
			pageAndSort = new PageAndSort<DisplayTagSortProperty>(1, DisplayTagSortProperty.PRIORITY, true);
		}
		return pageAndSort;
	}
	
	@SuppressWarnings("unchecked")
	private List<JobRequest> fetchSavedQueuedJobs(HttpSession httpSession) {
		List<JobRequest> queuedJobs = (List<JobRequest>) httpSession.getAttribute(WebConstants.KEY_JOB_REQUESTS_QUEUED);
		if (queuedJobs == null) {
			queuedJobs = Collections.EMPTY_LIST;
		}
		return queuedJobs;
	}

	private void setUpModel(List<JobRequest> allQueuedJobs, PageAndSort<DisplayTagSortProperty> queuedPageAndSort, 
							HttpSession httpSession, Model model) {
		httpSession.setAttribute(WebConstants.KEY_JOB_REQUESTS_QUEUED, allQueuedJobs);
		httpSession.setAttribute(WebConstants.KEY_JOB_QUEUED_PAGE_AND_SORT, queuedPageAndSort);
		
		// Create the DisplayTag VDO object - the PaginatedList which wrappers list
		PaginatedList queuedPaginatedList = createPaginatedList(allQueuedJobs, queuedPageAndSort);
		model.addAttribute(WebConstants.KEY_PAGINATED_LIST, queuedPaginatedList);
	}

	/**
	 * Wrapper one page of tabular data rows into a DisplayTag (view) presentation container PaginatedList
	 * that is used by the DisplayTag custom tag in the JSP to render the data in tabular form.
	 */
	private PaginatedList createPaginatedList(List<JobRequest> allQueuedJobs,
											  PageAndSort<DisplayTagSortProperty> queuedPageAndSort) {

		List<JobRequestRow> onePageOfRows = createOnePageOfRows(allQueuedJobs, queuedPageAndSort);
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
	private List<JobRequestRow> createOnePageOfRows(List<JobRequest> allQueuedJobs,
			  								PageAndSort<DisplayTagSortProperty> queuedPageAndSort) {

		// Create a presentation row VDO for each JobRequest and assign the run sequence number into it
		List<JobRequestRow> rows = new ArrayList<JobRequestRow>();
		for (JobRequest job : allQueuedJobs) {
			BookDefinition book = bookDefinitionService.findBookDefinitionByEbookDefId(job.getEbookDefinitionId());
			rows.add(new JobRequestRow(job, book));
		}

		// Sort into the order/sequence that the jobs will actually appear on the page
		Comparator<JobRequestRow> rowComparator = comparators.get(queuedPageAndSort.getSortProperty());
		Collections.sort(rows, rowComparator);
		if (!queuedPageAndSort.isAscendingSort()) {
			Collections.reverse(rows);
		}
		
		// Get the subset of objects that will be displayed on the current page.
		int fromIndex = (queuedPageAndSort.getPageNumber() - 1) * queuedPageAndSort.getObjectsPerPage();
		int toIndex = fromIndex + queuedPageAndSort.getObjectsPerPage();
		toIndex = (toIndex < rows.size()) ? toIndex : rows.size();
		List<JobRequestRow> onePageOfRows = rows.subList(fromIndex, toIndex);
	
		return onePageOfRows;
	}
	
	@Required
	public void setJobRequestService(JobRequestService service) {
		this.jobRequestService = service;
	}
	@Required
	public void setBookDefinitionService(BookDefinitionService service) {
		this.bookDefinitionService = service;
	}
	@Required
	public void setValidator(QueueFormValidator validator) {
		this.validator = validator;
	}
}
