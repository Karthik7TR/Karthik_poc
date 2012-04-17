/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.bookaudit;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.displaytag.pagination.PaginatedList;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.ui.Model;

import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAuditFilter;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAuditSort;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAuditSort.SortProperty;
import com.thomsonreuters.uscl.ereader.core.book.service.EBookAuditService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.PageAndSort;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.bookaudit.BookAuditForm.DisplayTagSortProperty;

/**
 * Methods common to, and needed by both the BookAuditController and the FilterFormController.
 */
public abstract class BaseBookAuditController {
	//private static final Logger log = Logger.getLogger(BaseBookAuditController.class);
	protected static final String PAGE_AND_SORT_NAME = "auditPageAndSort";
	protected EBookAuditService auditService;
	
	/**
	 * Fetch object containing the current page number, sort column, and sort direction as saved on the session.
	 */
	@SuppressWarnings("unchecked")
	protected PageAndSort<DisplayTagSortProperty> fetchSavedPageAndSort(HttpSession httpSession) {
		PageAndSort<DisplayTagSortProperty> pageAndSort = (PageAndSort<DisplayTagSortProperty>) httpSession.getAttribute(PAGE_AND_SORT_NAME);
		if (pageAndSort == null) {
			pageAndSort = new PageAndSort<DisplayTagSortProperty>(1, DisplayTagSortProperty.SUBMITTED_DATE, false);
		}
		return pageAndSort;
	}
	protected BookAuditFilterForm fetchSavedFilterForm(HttpSession httpSession) {
		BookAuditFilterForm form = (BookAuditFilterForm) httpSession.getAttribute(BookAuditFilterForm.FORM_NAME);
		if (form == null) {
			form = new BookAuditFilterForm();
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
	protected void setUpModel(BookAuditFilterForm filterForm, PageAndSort<DisplayTagSortProperty> pageAndSort,
							  HttpSession httpSession, Model model) {
		
		// Save filter and paging state in the session
		httpSession.setAttribute(BookAuditFilterForm.FORM_NAME, filterForm);
		httpSession.setAttribute(PAGE_AND_SORT_NAME, pageAndSort);
		
		model.addAttribute(BookAuditFilterForm.FORM_NAME, filterForm);
		
		// Create the DisplayTag VDO object - the PaginatedList which wrappers the job execution partial list
		PaginatedList paginatedList = createPaginatedList(pageAndSort, filterForm);
		model.addAttribute(WebConstants.KEY_PAGINATED_LIST, paginatedList);
	}
	
	/**
	 * Map the sort property name returned by display tag to the business object property name
	 * for sort used in the service.
	 * I.e. map a PageAndSortForm.DisplayTagSortProperty to a EbookAuditSort.SortProperty
	 * @param dtSortProperty display tag sort property key from the JSP
	 * @param ascendingSort true to sort in ascending order
	 * @return a ebookAudit sort business object used by the service to fetch the audit entities.
	 */
	protected static EbookAuditSort createBookAuditSort(PageAndSort<DisplayTagSortProperty> pageAndSort) {
		
		return new EbookAuditSort(SortProperty.valueOf(pageAndSort.getSortProperty().toString()), 
				pageAndSort.isAscendingSort(), pageAndSort.getPageNumber(), pageAndSort.getObjectsPerPage());
	}
	
	/**
	 * Create the partial paginated list used by DisplayTag to render to current page number of 
	 * list list of objects.
	 * @param pageAndSort current page number, sort column, and sort direction (asc/desc).
	 * @return an implemented DisplayTag paginated list interface
	 */
    private PaginatedList createPaginatedList(PageAndSort<DisplayTagSortProperty> pageAndSort, BookAuditFilterForm filterForm) {
    	String action = filterForm.getAction() != null ? filterForm.getAction().toString() : null; 
		EbookAuditFilter bookAuditFilter = new EbookAuditFilter(filterForm.getFromDate(), filterForm.getToDate(), action,
		 	filterForm.getTitleId(), filterForm.getBookName(), filterForm.getSubmittedBy(), filterForm.getBookDefinitionId());
		EbookAuditSort bookAuditSort = createBookAuditSort(pageAndSort);

		// Lookup all the EbookAudit objects by their primary key
		List<EbookAudit> audits = auditService.findEbookAudits(bookAuditFilter, bookAuditSort);
		int numberOfAudits = auditService.numberEbookAudits(bookAuditFilter);

		// Instantiate the object used by DisplayTag to render a partial list
		BookAuditPaginatedList paginatedList = new BookAuditPaginatedList(audits,
								numberOfAudits,
								pageAndSort.getPageNumber(), pageAndSort.getObjectsPerPage(),
								(DisplayTagSortProperty) pageAndSort.getSortProperty(),
								pageAndSort.isAscendingSort());
		return paginatedList;
    }

	@Required
	public void setAuditService(EBookAuditService service) {
		this.auditService = service;
	}
}
