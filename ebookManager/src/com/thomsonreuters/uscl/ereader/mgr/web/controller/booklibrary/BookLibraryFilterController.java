/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary;

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
import com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary.BookLibraryFilterForm.FilterCommand;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary.BookLibrarySelectionForm.DisplayTagSortProperty;

@Controller
public class BookLibraryFilterController extends BaseBookLibraryController {
	//private static final Logger log = LogManager.getLogger(BookLibraryFilterController.class);

	private Validator validator;

	@InitBinder(BookLibraryFilterForm.FORM_NAME)
	protected void initDataBinder(WebDataBinder binder) {
		binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
		binder.setValidator(validator);
	}
	
	/**
	 * Handle submit/post of a new set of filter criteria.
	 */
	@RequestMapping(value=WebConstants.MVC_BOOK_LIBRARY_FILTERED_POST, method = RequestMethod.POST)
	public ModelAndView doFilterPost(HttpSession httpSession,
						@ModelAttribute(BookLibraryFilterForm.FORM_NAME) @Valid BookLibraryFilterForm filterForm,
						BindingResult errors,
						Model model) throws Exception {
		// Restore state of paging and sorting
		PageAndSort<DisplayTagSortProperty> pageAndSort = fetchSavedPageAndSort(httpSession);
		BookLibrarySelectionForm librarySelectionForm = new BookLibrarySelectionForm();
		librarySelectionForm.setObjectsPerPage(pageAndSort.getObjectsPerPage());
		
		if (FilterCommand.RESET.equals(filterForm.getFilterCommand())) {
			filterForm.initialize();
		}
		
		pageAndSort.setPageNumber(1);

		setUpModel(filterForm, pageAndSort, httpSession, model);
		model.addAttribute(BookLibrarySelectionForm.FORM_NAME, librarySelectionForm);

		return new ModelAndView(WebConstants.VIEW_BOOK_LIBRARY_LIST);
	}
	
	@Required
	public void setValidator(Validator validator) {
		this.validator = validator;
	}

}
