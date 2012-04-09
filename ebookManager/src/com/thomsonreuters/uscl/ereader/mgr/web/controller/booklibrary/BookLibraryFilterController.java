/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.thomsonreuters.uscl.ereader.core.library.domain.LibraryList;
import com.thomsonreuters.uscl.ereader.core.library.service.LibraryListService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary.BookLibraryFilterForm.FilterCommand;

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

		FilterCommand c = bookLibraryFilterForm.getFilterCommand();
		List<LibraryList> paginatedList = null;
		switch (c) {

		case SEARCH:
			paginatedList = libraryListService.findBookDefinitions(
					"proviewDisplayName", true, 1,
					WebConstants.NUMBER_BOOK_DEF_SHOWN,
					bookLibraryFilterForm.getProviewDisplayName(),
					bookLibraryFilterForm.getTitleId(),
					bookLibraryFilterForm.getIsbn(),
					bookLibraryFilterForm.getMaterialId(),
					bookLibraryFilterForm.getTo(),
					bookLibraryFilterForm.getFrom(),
					bookLibraryFilterForm.geteBookDefStatus());
			break;

		case RESET:
			bookLibraryFilterForm = new BookLibraryFilterForm();
			paginatedList = libraryListService.findBookDefinitions(
					"proviewDisplayName", true, 1,
					WebConstants.NUMBER_BOOK_DEF_SHOWN, null, null, null, null,
					null, null, null);
			break;

		}

		initializeFormAndModel(model, paginatedList, httpSession);
		saveSessionValues(httpSession, bookLibraryFilterForm, paginatedList);

		ModelAndView mav = new ModelAndView(new RedirectView(
				WebConstants.MVC_BOOK_LIBRARY_LIST));

		return mav;
	}

	private void saveSessionValues(HttpSession httpSession,
			BookLibraryFilterForm bookLibraryFilterForm,
			List<LibraryList> paginatedList) {

		httpSession.setAttribute(BookLibraryFilterForm.FORM_NAME,
				bookLibraryFilterForm);
		httpSession
				.setAttribute(WebConstants.KEY_PAGINATED_LIST, paginatedList);

	}

	/**
	 * Populates the model to display the book definitions and hidden properties
	 * 
	 * @param model
	 * @param sortBy
	 * @param isAscending
	 * @param pageNumber
	 * @return
	 */
	private void initializeFormAndModel(Model model,
			List<LibraryList> paginatedList, HttpSession httpSession) {

		Long resultSize = (long) paginatedList.size();

		model.addAttribute(WebConstants.KEY_PAGINATED_LIST, paginatedList);
		model.addAttribute(WebConstants.KEY_TOTAL_BOOK_SIZE,
				resultSize.intValue());

	}

	public LibraryListService getLibraryListService() {
		return libraryListService;
	}

	public void setLibraryListService(LibraryListService libraryListService) {
		this.libraryListService = libraryListService;
	}

}
