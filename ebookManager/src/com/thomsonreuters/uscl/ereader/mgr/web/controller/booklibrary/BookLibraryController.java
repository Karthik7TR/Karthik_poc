package com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

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
import org.springframework.web.servlet.view.RedirectView;

import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.PageAndSort;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary.BookLibrarySelectionForm.Command;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary.BookLibrarySelectionForm.DisplayTagSortProperty;

@Controller
public class BookLibraryController extends BaseBookLibraryController {
	// private static final Logger log = Logger.getLogger(BookLibraryController.class);

	private Validator validator;

	@InitBinder(BookLibrarySelectionForm.FORM_NAME)
	protected void initDataBinder(WebDataBinder binder) {
		binder.setValidator(validator);
	}

	/**
	 * Handles the initial loading of the Book Definition List page
	 * 
	 * @param httpSession
	 * @param form
	 * @param bindingResult
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = WebConstants.MVC_BOOK_LIBRARY_LIST, method = RequestMethod.GET)
	public ModelAndView inboundGet(HttpSession httpSession, Model model) {
//		log.debug(">>>");
		BookLibraryFilterForm filterForm = fetchSavedFilterForm(httpSession);	// from session
		PageAndSort<DisplayTagSortProperty> savedPageAndSort = fetchSavedPageAndSort(httpSession);	// from session
		
		BookLibrarySelectionForm librarySelectionForm = new BookLibrarySelectionForm();
		librarySelectionForm.setObjectsPerPage(savedPageAndSort.getObjectsPerPage());

		setUpModel(filterForm, savedPageAndSort, httpSession, model);
		model.addAttribute(BookLibrarySelectionForm.FORM_NAME, librarySelectionForm);
	
		return new ModelAndView(WebConstants.VIEW_BOOK_LIBRARY_LIST);
	}

	// ====================================================================================
	/**
	 * Handles the DisplayTag table external paging and sorting operations.
	 * Assumes a current list of execution ID's on the session that is reused
	 * with each paging/sorting operation. To get a completely up-to-date result
	 * set, the page must be refreshed (a new GET) or a new filtered search form
	 * must again be submitted (a POST).
	 */
	@RequestMapping(value = WebConstants.MVC_BOOK_LIBRARY_LIST_PAGING, method = RequestMethod.GET)
	public ModelAndView pagingAndSorting(HttpSession httpSession, 
			@ModelAttribute(BookLibrarySelectionForm.FORM_NAME) BookLibrarySelectionForm form,
			Model model) {
		BookLibraryFilterForm filterForm = fetchSavedFilterForm(httpSession);
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
		
		return new ModelAndView(WebConstants.VIEW_BOOK_LIBRARY_LIST);
	}

	@RequestMapping(value = WebConstants.MVC_BOOK_LIBRARY_LIST_SELECTION_POST, method = RequestMethod.POST)
	public ModelAndView postBookDefinitionSelections(HttpSession httpSession, HttpServletRequest request, 
			@ModelAttribute(BookLibrarySelectionForm.FORM_NAME) @Valid BookLibrarySelectionForm form,
			BindingResult bindingResult, Model model) throws Exception {

		if (!bindingResult.hasErrors()) {
			ModelAndView mav = null;
			String[] bookKeys = form.getSelectedEbookKeys();
			StringBuilder parameters = new StringBuilder();
			parameters.append("?");
			for (String key : bookKeys) {
				parameters.append("id=" + key + "&");
			}
			parameters.deleteCharAt(parameters.length() - 1);

			Command command = form.getCommand();
			switch (command) {
			case GENERATE:
				if (bookKeys.length > 1)
					mav = new ModelAndView(new RedirectView(
							WebConstants.MVC_BOOK_BULK_GENERATE_PREVIEW
									+ parameters.toString()));
				else
					mav = new ModelAndView(new RedirectView(
							WebConstants.MVC_BOOK_SINGLE_GENERATE_PREVIEW
									+ parameters.toString()));
				break;
			case IMPORT:
				// TODO:
				// mav = new ModelAndView(new
				// RedirectView(WebConstants.MVC_GENERATE+queryString));
				break;
			case EXPORT:
				// TODO:
				// mav = new ModelAndView(new
				// RedirectView(WebConstants.MVC_GENERATE+queryString));
				break;
			case PROMOTE:
				if (bookKeys.length > 1)
					mav = new ModelAndView(new RedirectView(
							WebConstants.MVC_BOOK_DEFINITION_BULK_PROMOTION
									+ parameters.toString()));
				else
					mav = new ModelAndView(new RedirectView(
							WebConstants.MVC_BOOK_DEFINITION_PROMOTION
									+ parameters.toString()));
				break;
			default:
				throw new RuntimeException("Unexpected form command: "
						+ command);
			}

			return mav;
		}
		
		BookLibraryFilterForm filterForm = fetchSavedFilterForm(httpSession);
		PageAndSort<DisplayTagSortProperty> pageAndSort = fetchSavedPageAndSort(httpSession);
		form.setObjectsPerPage(pageAndSort.getObjectsPerPage());
		
		setUpModel(filterForm, pageAndSort, httpSession, model);

		return new ModelAndView(WebConstants.VIEW_BOOK_LIBRARY_LIST);
	}


	@Required
	public void setValidator(Validator validator) {
		this.validator = validator;
	}

}
