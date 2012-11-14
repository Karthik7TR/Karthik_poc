/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.bookaudit;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.PageAndSort;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.bookaudit.BookAuditForm.DisplayTagSortProperty;


@Controller
public class BookAuditController extends BaseBookAuditController {

	/**
	 * Handle initial in-bound HTTP get request to the page.
	 * No query string parameters are expected.
	 */
	@RequestMapping(value=WebConstants.MVC_BOOK_AUDIT_LIST, method = RequestMethod.GET)
	public ModelAndView auditList(HttpSession httpSession, Model model) {
		BookAuditFilterForm filterForm = fetchSavedFilterForm(httpSession);
		
		return setupInitialView(model, filterForm, httpSession);
	}
	
	/**
	 * Handle initial in-bound HTTP get request for specific book definition audit list.
	 * Used from the View Book Definition page.
	 */
	@RequestMapping(value=WebConstants.MVC_BOOK_AUDIT_SPECIFIC, method = RequestMethod.GET)
	public ModelAndView specificBookAuditList(HttpSession httpSession, @RequestParam("id") Long id, Model model) {
		BookAuditFilterForm filterForm = new BookAuditFilterForm(id);	// from session
		
		return setupInitialView(model, filterForm, httpSession);
	}
	
	/**
	 * Setup of Form and sorting shared by two different incoming HTTP get request
	 */
	private ModelAndView setupInitialView(Model model, BookAuditFilterForm filterForm, HttpSession httpSession) {
		PageAndSort<DisplayTagSortProperty> savedPageAndSort = fetchSavedPageAndSort(httpSession);
		
		BookAuditForm ebookAuditForm = new BookAuditForm();
		ebookAuditForm.setObjectsPerPage(savedPageAndSort.getObjectsPerPage());

		setUpModel(filterForm, savedPageAndSort, httpSession, model);
		model.addAttribute(BookAuditForm.FORM_NAME, ebookAuditForm);
	
		return new ModelAndView(WebConstants.VIEW_BOOK_AUDIT_LIST);
	}

	/**
	 * Handle paging and sorting of audit list.
	 * Handles clicking of column headers to sort, or use of page number navigation links, like prev/next.
	 */
	@RequestMapping(value=WebConstants.MVC_BOOK_AUDIT_LIST_PAGE_AND_SORT, method = RequestMethod.GET)
	public ModelAndView auditListPagingAndSorting(HttpSession httpSession, 
								@ModelAttribute(BookAuditForm.FORM_NAME) BookAuditForm form,
								Model model) {
		BookAuditFilterForm filterForm = fetchSavedFilterForm(httpSession);
		PageAndSort<DisplayTagSortProperty> pageAndSort = fetchSavedPageAndSort(httpSession);
		form.setObjectsPerPage(pageAndSort.getObjectsPerPage());
		Integer nextPageNumber = form.getPage();

		// If there was a page=n query string parameter, then we assume we are paging since this
		// parameter is not present on the query string when display tag sorting.
		if (nextPageNumber != null) {  // PAGING
			pageAndSort.setPageNumber(nextPageNumber);
		} else {  // SORTING
			pageAndSort.setPageNumber(1);
			pageAndSort.setSortProperty(form.getSort());
			pageAndSort.setAscendingSort(form.isAscendingSort());
		}
		setUpModel(filterForm, pageAndSort, httpSession, model);
		
		return new ModelAndView(WebConstants.VIEW_BOOK_AUDIT_LIST);
	}
	
	/**
	 * Handle URL request that the number of rows displayed in table be changed.
	 */
	@RequestMapping(value=WebConstants.MVC_BOOK_AUDIT_CHANGE_ROW_COUNT, method = RequestMethod.POST)
	public ModelAndView handleChangeInItemsToDisplay(HttpSession httpSession,
							   @ModelAttribute(BookAuditForm.FORM_NAME) @Valid BookAuditForm form,
							   Model model) {
		PageAndSort<DisplayTagSortProperty> pageAndSort = fetchSavedPageAndSort(httpSession);
		pageAndSort.setPageNumber(1); // Always start from first page again once changing row count to avoid index out of bounds
		pageAndSort.setObjectsPerPage(form.getObjectsPerPage());	// Update the new number of items to be shown at one time
		// Restore the state of the search filter
		BookAuditFilterForm filterForm = fetchSavedFilterForm(httpSession);
		setUpModel(filterForm, pageAndSort, httpSession, model);
		return new ModelAndView(WebConstants.VIEW_BOOK_AUDIT_LIST);
	}
	
	/**
	 * Handle initial in-bound HTTP get request for specific book audit detail.
	 */
	@RequestMapping(value=WebConstants.MVC_BOOK_AUDIT_DETAIL, method = RequestMethod.GET)
	public ModelAndView auditDetail(HttpSession httpSession, @RequestParam("id") Long id, Model model) {
		EbookAudit audit = auditService.findEBookAuditByPrimaryKey(id);
		model.addAttribute(WebConstants.KEY_BOOK_AUDIT_DETAIL, audit);
		
		return new ModelAndView(WebConstants.VIEW_BOOK_AUDIT_DETAIL);
	}
	
}
