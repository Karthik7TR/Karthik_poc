/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewaudit;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.PageAndSort;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewaudit.ProviewAuditForm.DisplayTagSortProperty;


@Controller
public class ProviewAuditController extends BaseProviewAuditController {

	/**
	 * Handle initial in-bound HTTP get request to the page.
	 * No query string parameters are expected.
	 */
	@RequestMapping(value=WebConstants.MVC_PROVIEW_AUDIT_LIST, method = RequestMethod.GET)
	public ModelAndView auditList(HttpSession httpSession, Model model) {
		ProviewAuditFilterForm filterForm = fetchSavedFilterForm(httpSession);
		
		return setupInitialView(model, filterForm, httpSession);
	}
	
	/**
	 * Setup of Form and sorting shared by two different incoming HTTP get request
	 */
	private ModelAndView setupInitialView(Model model, ProviewAuditFilterForm filterForm, HttpSession httpSession) {
		PageAndSort<DisplayTagSortProperty> savedPageAndSort = fetchSavedPageAndSort(httpSession);
				
		ProviewAuditForm proviewAuditForm = new ProviewAuditForm();
		proviewAuditForm.setObjectsPerPage(savedPageAndSort.getObjectsPerPage());

		setUpModel(filterForm, savedPageAndSort, httpSession, model);
		model.addAttribute(ProviewAuditForm.FORM_NAME, proviewAuditForm);
	
		return new ModelAndView(WebConstants.VIEW_PROVIEW_AUDIT_LIST);
	}

	/**
	 * Handle paging and sorting of audit list.
	 * Handles clicking of column headers to sort, or use of page number navigation links, like prev/next.
	 */
	@RequestMapping(value=WebConstants.MVC_PROVIEW_AUDIT_LIST_PAGE_AND_SORT, method = RequestMethod.GET)
	public ModelAndView auditListPagingAndSorting(HttpSession httpSession, 
								@ModelAttribute(ProviewAuditForm.FORM_NAME) ProviewAuditForm form,
								Model model) {
		ProviewAuditFilterForm filterForm = fetchSavedFilterForm(httpSession);
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
		
		return new ModelAndView(WebConstants.VIEW_PROVIEW_AUDIT_LIST);
	}
	
}