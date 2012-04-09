/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary;

import java.util.List;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
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

import com.thomsonreuters.uscl.ereader.core.job.domain.JobFilter;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobSort;
import com.thomsonreuters.uscl.ereader.core.job.service.JobService;
import com.thomsonreuters.uscl.ereader.core.library.service.LibraryListService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.PageAndSort;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.job.summary.FilterForm;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.job.summary.FilterForm.FilterCommand;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.job.summary.JobSummaryForm.DisplayTagSortProperty;

@Controller
public class BookLibraryFilterController {
	private static final Logger log = Logger
			.getLogger(BookLibraryFilterController.class);
	private LibraryListService libraryListService;

	
	/**
	 * Handle submit/post of a new set of filter criteria.
	 */
	@RequestMapping(value = WebConstants.MVC_BOOK_LIBRARY_FILTERED_POST, method = RequestMethod.POST)
	public ModelAndView bookListFiltered(
			HttpSession httpSession,
			@ModelAttribute(BookLibraryFilterForm.FORM_NAME) BookLibraryFilterForm bookLibraryFilterForm,
			BindingResult bindingResult, Model model) throws Exception {
		System.out.println("Inside filter tile");

		return new ModelAndView(WebConstants.VIEW_BOOK_LIBRARY_LIST);
	}

	public LibraryListService getLibraryListService() {
		return libraryListService;
	}

	public void setLibraryListService(LibraryListService libraryListService) {
		this.libraryListService = libraryListService;
	}

}
