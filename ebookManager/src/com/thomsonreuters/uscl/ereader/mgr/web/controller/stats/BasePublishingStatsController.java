/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.stats;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.displaytag.pagination.PaginatedList;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.ui.Model;


import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.PageAndSort;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.stats.PublishingStatsForm.DisplayTagSortProperty;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStatsFilter;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStatsSort;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStatsSort.SortProperty;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;

/**
 * Methods common to, and needed by both the PublishingStatsController and the PublishingStatsFilterController.
 */
public abstract class BasePublishingStatsController {
	//private static final Logger log = Logger.getLogger(BasePublishingStatsController.class);
	protected static final String PAGE_AND_SORT_NAME = "publishingStatsPageAndSort";
	protected PublishingStatsService publishingStatsService;
	
	/**
	 * Fetch object containing the current page number, sort column, and sort direction as saved on the session.
	 */
	@SuppressWarnings("unchecked")
	protected PageAndSort<DisplayTagSortProperty> fetchSavedPageAndSort(HttpSession httpSession) {
		PageAndSort<DisplayTagSortProperty> pageAndSort = (PageAndSort<DisplayTagSortProperty>) httpSession.getAttribute(PAGE_AND_SORT_NAME);
		if (pageAndSort == null) {
			pageAndSort = new PageAndSort<DisplayTagSortProperty>(1, DisplayTagSortProperty.JOB_SUBMIT_TIMESTAMP, false);
		}
		return pageAndSort;
	}
	protected PublishingStatsFilterForm fetchSavedFilterForm(HttpSession httpSession) {
		PublishingStatsFilterForm form = (PublishingStatsFilterForm) httpSession.getAttribute(PublishingStatsFilterForm.FORM_NAME);
		if (form == null) {
			form = new PublishingStatsFilterForm();
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
	protected void setUpModel(PublishingStatsFilterForm filterForm, PageAndSort<DisplayTagSortProperty> pageAndSort,
							  HttpSession httpSession, Model model) {
		
		// Save filter and paging state in the session
		httpSession.setAttribute(PublishingStatsFilterForm.FORM_NAME, filterForm);
		httpSession.setAttribute(PAGE_AND_SORT_NAME, pageAndSort);
		
		model.addAttribute(PublishingStatsFilterForm.FORM_NAME, filterForm);
		
		// Create the DisplayTag VDO object - the PaginatedList which wrappers the job execution partial list
		PaginatedList paginatedList = createPaginatedList(pageAndSort, filterForm);
		model.addAttribute(WebConstants.KEY_PAGINATED_LIST, paginatedList);
	}
	
	/**
	 * Map the sort property name returned by display tag to the business object property name
	 * for sort used in the service.
	 * I.e. map a PageAndSortForm.DisplayTagSortProperty to a PublishingStatsSort.SortProperty
	 * @param dtSortProperty display tag sort property key from the JSP
	 * @param ascendingSort true to sort in ascending order
	 * @return a ebookAudit sort business object used by the service to fetch the audit entities.
	 */
	protected static PublishingStatsSort createStatsSort(PageAndSort<DisplayTagSortProperty> pageAndSort) {
		
		return new PublishingStatsSort(SortProperty.valueOf(pageAndSort.getSortProperty().toString()), 
				pageAndSort.isAscendingSort(), pageAndSort.getPageNumber(), pageAndSort.getObjectsPerPage());
	}
	
	protected static PublishingStatsFilter createStatsFilter(PublishingStatsFilterForm filterForm) {
		
		return new PublishingStatsFilter(filterForm.getFromDate(), filterForm.getToDate(),
			 	filterForm.getTitleId(), filterForm.getProviewDisplayName(), filterForm.getBookDefinitionId());
	}
	
	/**
	 * Create the partial paginated list used by DisplayTag to render to current page number of 
	 * list list of objects.
	 * @param pageAndSort current page number, sort column, and sort direction (asc/desc).
	 * @return an implemented DisplayTag paginated list interface
	 */
    private PaginatedList createPaginatedList(PageAndSort<DisplayTagSortProperty> pageAndSort, PublishingStatsFilterForm filterForm) {
		PublishingStatsFilter publishingStatsFilter = createStatsFilter(filterForm);
		PublishingStatsSort publishingStatsSort = createStatsSort(pageAndSort);

		// Lookup all the EbookAudit objects by their primary key
		List<PublishingStats> stats = publishingStatsService.findPublishingStats(publishingStatsFilter, publishingStatsSort);
		int numberOfStats = publishingStatsService.numberOfPublishingStats(publishingStatsFilter);

		// Instantiate the object used by DisplayTag to render a partial list
		PublishingStatsPaginatedList paginatedList = new PublishingStatsPaginatedList(stats,
								numberOfStats,
								pageAndSort.getPageNumber(), pageAndSort.getObjectsPerPage(),
								(DisplayTagSortProperty) pageAndSort.getSortProperty(),
								pageAndSort.isAscendingSort());
		return paginatedList;
    }

	@Required
	public void setPublishingStatsService(PublishingStatsService service) {
		this.publishingStatsService = service;
	}
}
