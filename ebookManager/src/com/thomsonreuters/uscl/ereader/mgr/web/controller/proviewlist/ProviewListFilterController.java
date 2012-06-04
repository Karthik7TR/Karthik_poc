/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist;

import javax.servlet.http.HttpSession;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist.ProviewListFilterForm.FilterCommand;

@Controller
public class ProviewListFilterController {

	@InitBinder(ProviewListFilterForm.FORM_NAME)
	protected void initDataBinder(WebDataBinder binder) {
		binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));

	}

	/**
	 * Handle submit/post of a new set of filter criteria.
	 */
	@RequestMapping(value = WebConstants.MVC_PROVIEW_LIST_FILTERED_POST, method = RequestMethod.POST)
	public ModelAndView doFilterPost(
			HttpSession httpSession,
			@ModelAttribute(ProviewListFilterForm.FORM_NAME) ProviewListFilterForm filterForm,
			BindingResult errors, Model model) throws Exception {

		ProviewListFilterForm librarySelectionForm = new ProviewListFilterForm();

		if (FilterCommand.RESET.equals(filterForm.getFilterCommand())) {
			filterForm.initNull();
		}

		model.addAttribute(ProviewListFilterForm.FORM_NAME,
				librarySelectionForm);

		return new ModelAndView(WebConstants.VIEW_PROVIEW_TITLES);
	}

}
