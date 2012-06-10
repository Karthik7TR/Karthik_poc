/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewaudit;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.displaytag.pagination.PaginatedList;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.ui.Model;

import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.PageAndSort;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewaudit.ProviewAuditForm.DisplayTagSortProperty;
import com.thomsonreuters.uscl.ereader.proviewaudit.domain.ProviewAudit;
import com.thomsonreuters.uscl.ereader.proviewaudit.domain.ProviewAuditFilter;
import com.thomsonreuters.uscl.ereader.proviewaudit.domain.ProviewAuditSort;
import com.thomsonreuters.uscl.ereader.proviewaudit.domain.ProviewAuditSort.SortProperty;
import com.thomsonreuters.uscl.ereader.proviewaudit.service.ProviewAuditService;

/**
 * Methods common to, and needed by both the ProviewAuditController and the ProviewAuditFilterController.
 */
public abstract class BaseProviewAuditController {
	//private static final Logger log = Logger.getLogger(BaseProviewAuditController.class);
	protected static final String PAGE_AND_SORT_NAME = "proviewAuditPageAndSort";
	protected ProviewAuditService auditService;
	
	/**
	 * Fetch object containing the current page number, sort column, and sort direction as saved on the session.
	 */
	@SuppressWarnings("unchecked")
	protected PageAndSort<DisplayTagSortProperty> fetchSavedPageAndSort(HttpSession httpSession) {
		PageAndSort<DisplayTagSortProperty> pageAndSort = (PageAndSort<DisplayTagSortProperty>) httpSession.getAttribute(PAGE_AND_SORT_NAME);
		if (pageAndSort == null) {
			pageAndSort = new PageAndSort<DisplayTagSortProperty>(1, DisplayTagSortProperty.REQUEST_DATE, false);
		}
		return pageAndSort;
	}
	protected ProviewAuditFilterForm fetchSavedFilterForm(HttpSession httpSession) {
		ProviewAuditFilterForm form = (ProviewAuditFilterForm) httpSession.getAttribute(ProviewAuditFilterForm.FORM_NAME);
		if (form == null) {
			form = new ProviewAuditFilterForm();
		}
		return form;
	}
	
	/**
	 * Handles the current paging and sorting state and creates the DisplayTag PaginatedList object
	 * for use by the DisplayTag custom tag in the JSP.
	 * @param pageAndSortForm paging/sorting/direction/display count
	 * @param httpSession
	 * @param model
	 */
	protected void setUpModel(ProviewAuditFilterForm filterForm, PageAndSort<DisplayTagSortProperty> pageAndSort,
							  HttpSession httpSession, Model model) {
		
		// Save filter and paging state in the session
		httpSession.setAttribute(ProviewAuditFilterForm.FORM_NAME, filterForm);
		httpSession.setAttribute(PAGE_AND_SORT_NAME, pageAndSort);
		
		model.addAttribute(ProviewAuditFilterForm.FORM_NAME, filterForm);
		
		// Create the DisplayTag VDO object - the PaginatedList which wrappers the job execution partial list
		PaginatedList paginatedList = createPaginatedList(pageAndSort, filterForm);
		model.addAttribute(WebConstants.KEY_PAGINATED_LIST, paginatedList);
	}
	
	/**
	 * Map the sort property name returned by display tag to the business object property name
	 * for sort used in the service.
	 * I.e. map a PageAndSortForm.DisplayTagSortProperty to a ProviewAuditSort.SortProperty
	 * @param dtSortProperty display tag sort property key from the JSP
	 * @param ascendingSort true to sort in ascending order
	 * @return a ebookAudit sort business object used by the service to fetch the audit entities.
	 */
	protected static ProviewAuditSort createBookAuditSort(PageAndSort<DisplayTagSortProperty> pageAndSort) {
		
		return new ProviewAuditSort(SortProperty.valueOf(pageAndSort.getSortProperty().toString()), 
				pageAndSort.isAscendingSort(), pageAndSort.getPageNumber(), pageAndSort.getObjectsPerPage());
	}
	
	/**
	 * Create the partial paginated list used by DisplayTag to render to current page number of 
	 * list list of objects.
	 * @param pageAndSort current page number, sort column, and sort direction (asc/desc).
	 * @return an implemented DisplayTag paginated list interface
	 */
    private PaginatedList createPaginatedList(PageAndSort<DisplayTagSortProperty> pageAndSort, ProviewAuditFilterForm filterForm) {
    	String action = filterForm.getAction() != null ? filterForm.getAction().toString() : null; 
		ProviewAuditFilter auditFilter = new ProviewAuditFilter(filterForm.getRequestFromDate(), filterForm.getRequestToDate(), action,
		 	filterForm.getTitleId(), filterForm.getUsername());
		ProviewAuditSort auditSort = createBookAuditSort(pageAndSort);

		// Lookup all the ProviewAudit objects that match the filter criteria
		List<ProviewAudit> audits = auditService.findProviewAudits(auditFilter, auditSort);
		int numberOfAudits = auditService.numberProviewAudits(auditFilter);

		// Instantiate the object used by DisplayTag to render a partial list
		ProviewAuditPaginatedList paginatedList = new ProviewAuditPaginatedList(audits,
								numberOfAudits,
								pageAndSort.getPageNumber(), pageAndSort.getObjectsPerPage(),
								(DisplayTagSortProperty) pageAndSort.getSortProperty(),
								pageAndSort.isAscendingSort());
		return paginatedList;
    }

	@Required
	public void setAuditService(ProviewAuditService service) {
		this.auditService = service;
	}
}
