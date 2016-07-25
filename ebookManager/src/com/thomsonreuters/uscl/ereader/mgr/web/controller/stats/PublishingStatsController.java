/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.mgr.web.controller.stats;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

 import org.apache.log4j.LogManager; import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.PageAndSort;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.stats.PublishingStatsForm.DisplayTagSortProperty;


@Controller
public class PublishingStatsController extends BasePublishingStatsController {
	
	private static final Logger log = LogManager.getLogger(PublishingStatsController.class);
	
	/**
	 * Handle initial in-bound HTTP get request to the page.
	 * No query string parameters are expected.
	 */
	@RequestMapping(value=WebConstants.MVC_STATS, method = RequestMethod.GET)
	public ModelAndView stats(HttpSession httpSession, Model model) {
		PublishingStatsFilterForm filterForm = fetchSavedFilterForm(httpSession);
		
		return setupInitialView(model, filterForm, httpSession);
	}
	
	/**
	 * Handle initial in-bound HTTP get request for specific book definition publishing stats.
	 * Used from the View Book Definition page.
	 */
	@RequestMapping(value=WebConstants.MVC_STATS_SPECIFIC_BOOK, method = RequestMethod.GET)
	public ModelAndView specificBookStat(HttpSession httpSession, @RequestParam("id") Long id, Model model) {
		PublishingStatsFilterForm filterForm = new PublishingStatsFilterForm(id);	// from session
		
		return setupInitialView(model, filterForm, httpSession);
	}
	
	/**
	 * Setup of Form and sorting shared by two different incoming HTTP get request
	 */
	private ModelAndView setupInitialView(Model model, PublishingStatsFilterForm filterForm, HttpSession httpSession) {
		PageAndSort<DisplayTagSortProperty> savedPageAndSort = fetchSavedPageAndSort(httpSession);
		
		PublishingStatsForm publishingStatsForm = new PublishingStatsForm();
		publishingStatsForm.setObjectsPerPage(savedPageAndSort.getObjectsPerPage());

		setUpModel(filterForm, savedPageAndSort, httpSession, model);
		model.addAttribute(PublishingStatsForm.FORM_NAME, publishingStatsForm);
	
		return new ModelAndView(WebConstants.VIEW_STATS);
	}

	/**
	 * Handle paging and sorting of audit list.
	 * Handles clicking of column headers to sort, or use of page number navigation links, like prev/next.
	 */
	@RequestMapping(value=WebConstants.MVC_STATS_PAGE_AND_SORT, method = RequestMethod.GET)
	public ModelAndView publishingStatsPagingAndSorting(HttpSession httpSession, 
								@ModelAttribute(PublishingStatsForm.FORM_NAME) PublishingStatsForm form,
								Model model) {
		PublishingStatsFilterForm filterForm = fetchSavedFilterForm(httpSession);
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
		
		return new ModelAndView(WebConstants.VIEW_STATS);
	}
	
	/**
	 * Handle URL request that the number of rows displayed in table be changed.
	 */
	@RequestMapping(value=WebConstants.MVC_STATS_CHANGE_ROW_COUNT, method = RequestMethod.POST)
	public ModelAndView handleChangeInItemsToDisplay(HttpSession httpSession,
							   @ModelAttribute(PublishingStatsForm.FORM_NAME) @Valid PublishingStatsForm form, Model model) {
		PageAndSort<DisplayTagSortProperty> pageAndSort = fetchSavedPageAndSort(httpSession);
		pageAndSort.setPageNumber(1); // Always start from first page again once changing row count to avoid index out of bounds
		pageAndSort.setObjectsPerPage(form.getObjectsPerPage());	// Update the new number of items to be shown at one time
		// Restore the state of the search filter
		PublishingStatsFilterForm filterForm = fetchSavedFilterForm(httpSession);
		setUpModel(filterForm, pageAndSort, httpSession, model);
		return new ModelAndView(WebConstants.VIEW_STATS);
	}
	
	@RequestMapping(value=WebConstants.MVC_STATS_DOWNLOAD, method = RequestMethod.GET)
	public void downloadPublishingStatsExcel(HttpSession httpSession, HttpServletRequest request, HttpServletResponse response) {
		
		PublishingStatsExcelExportService excelExportService = new PublishingStatsExcelExportService();
		
		try {
			Workbook wb = excelExportService.createExcelDocument(httpSession);
			Date date = new Date();
			SimpleDateFormat s = new SimpleDateFormat("yyyyMMdd");
			String stringDate = s.format(date);
			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-Disposition", "attachment; filename=publishingStats_"+ stringDate +".xls");
			ServletOutputStream out = response.getOutputStream();
			wb.write(out);
			out.flush();
		} catch(Exception e) {
			log.error(e.getMessage());
		}
	}
	
}
