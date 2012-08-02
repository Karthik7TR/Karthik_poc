/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.stats;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.PageAndSort;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.stats.PublishingStatsFilterForm.FilterCommand;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.stats.PublishingStatsForm.DisplayTagSortProperty;

@Controller
public class PublishingStatsFilterController extends BasePublishingStatsController {
	private Validator validator;

	@InitBinder(PublishingStatsFilterForm.FORM_NAME)
	protected void initDataBinder(WebDataBinder binder) {
		binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
		binder.setValidator(validator);
	}
	
	/**
	 * Handle submit/post of a new set of filter criteria.
	 */
	@RequestMapping(value=WebConstants.MVC_STATS_FILTER, method = RequestMethod.POST)
	public ModelAndView doFilterPost(HttpSession httpSession,
						@ModelAttribute(PublishingStatsFilterForm.FORM_NAME) @Valid PublishingStatsFilterForm filterForm,
						BindingResult errors,
						Model model) throws Exception {
		// Restore state of paging and sorting
		PageAndSort<DisplayTagSortProperty> pageAndSort = fetchSavedPageAndSort(httpSession);
		PublishingStatsForm jobSummaryForm = new PublishingStatsForm();
		jobSummaryForm.setObjectsPerPage(pageAndSort.getObjectsPerPage());
		
		if (FilterCommand.RESET.equals(filterForm.getFilterCommand())) {
			filterForm.initialize();
		}
		
		pageAndSort.setPageNumber(1);

		setUpModel(filterForm, pageAndSort, httpSession, model);
		model.addAttribute(PublishingStatsForm.FORM_NAME, jobSummaryForm);

		return new ModelAndView(WebConstants.VIEW_STATS);
	}

	@Required
	public void setValidator(PublishingStatsFormValidator validator) {
		this.validator = validator;
	}
}
