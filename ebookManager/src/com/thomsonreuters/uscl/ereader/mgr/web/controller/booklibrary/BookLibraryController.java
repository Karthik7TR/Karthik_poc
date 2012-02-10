package com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.displaytag.tags.TableTagParameters;
import org.displaytag.util.ParamEncoder;
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

import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.orchestrate.core.BookDefinitionKey;

@Controller
public class BookLibraryController {
	// private static final Logger log =
	// Logger.getLogger(BookLibraryController.class);

	private BookLibraryService bookLibraryService;
	private Validator validator;

	@InitBinder(BookLibrarySelectionForm.FORM_NAME)
	protected void initDataBinder(WebDataBinder binder) {
		binder.setValidator(validator);
	}

	@RequestMapping(value = WebConstants.MVC_BOOK_LIBRARY_LIST, method = RequestMethod.GET)
	public ModelAndView bookList(
			HttpSession httpSession,
			@ModelAttribute(BookLibrarySelectionForm.FORM_NAME) BookLibrarySelectionForm form,
			@ModelAttribute(BookLibraryFilterForm.FORM_NAME) BookLibraryFilterForm bookLibraryFilterForm,
			BindingResult bindingResult, Model model) throws Exception {

		List<BookDefinitionVdo> paginatedList = bookLibraryService
				.getBooksOnPage("bookName", true, 1, 20);
		Integer resultSize = (int) bookLibraryService.getTotalBookCount();
		model.addAttribute(WebConstants.KEY_PAGINATED_LIST, paginatedList);
		model.addAttribute(WebConstants.KEY_TOTAL_BOOK_SIZE, resultSize);

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
	public ModelAndView pagingAndSorting(
			HttpServletRequest request,
			HttpSession httpSession,
			@ModelAttribute(BookLibrarySelectionForm.FORM_NAME) BookLibrarySelectionForm form,
			@ModelAttribute(BookLibraryFilterForm.FORM_NAME) BookLibraryFilterForm bookLibraryForm,
			BindingResult bindingResult, Model model) throws Exception {
		// log.debug(">>> " + form);
		// Fetch the current object list from the session
		String sort = request.getParameter(new ParamEncoder("vdo")
				.encodeParameterName(TableTagParameters.PARAMETER_SORT));
		if (sort == null) {
			sort = "bookName";
		}

		String order = request.getParameter(new ParamEncoder("vdo")
				.encodeParameterName(TableTagParameters.PARAMETER_ORDER));
		boolean isAscending = order != null && order.equals("2") ? false : true;

		int page = Integer
				.parseInt(request.getParameter(new ParamEncoder("vdo")
						.encodeParameterName(TableTagParameters.PARAMETER_PAGE)));

		List<BookDefinitionVdo> paginatedList = bookLibraryService
				.getBooksOnPage(sort, isAscending, page, 20);
		Integer resultSize = (int) bookLibraryService.getTotalBookCount();
		model.addAttribute(WebConstants.KEY_PAGINATED_LIST, paginatedList);
		model.addAttribute(WebConstants.KEY_TOTAL_BOOK_SIZE, resultSize);

		return new ModelAndView(WebConstants.VIEW_BOOK_LIBRARY_LIST);
	}

	@RequestMapping(value = WebConstants.MVC_BOOK_SINGLE_GENERATE_PREVIEW, method = RequestMethod.POST)
	public ModelAndView generateEbookPreview(
			@ModelAttribute(BookLibrarySelectionForm.FORM_NAME) @Valid BookLibrarySelectionForm form,
			BindingResult bindingResult, Model model) throws Exception {

		String[] bookKeys = form.getSelectedEbookKeys();
		BookDefinitionKey key = new BookDefinitionKey(bookKeys[0]);
		BookDefinitionVdo bookDefinition = bookLibraryService
				.getSingleBook(key);

		model.addAttribute(WebConstants.KEY_BOOK_DEFINITION, bookDefinition);

		return new ModelAndView(WebConstants.VIEW_BOOK_GENERATE_PREVIEW);
	}

	@RequestMapping(value = WebConstants.MVC_BOOK_BULK_GENERATE_PREVIEW, method = RequestMethod.POST)
	public ModelAndView generateBulkEbookPreview(
			@ModelAttribute(BookLibrarySelectionForm.FORM_NAME) @Valid BookLibrarySelectionForm form,
			BindingResult bindingResult, Model model) throws Exception {

		return new ModelAndView(WebConstants.VIEW_BOOK_GENERATE_BULK_PREVIEW);
	}

	@RequestMapping(value = WebConstants.MVC_BOOK_DEFINITION_PROMOTION, method = RequestMethod.POST)
	public ModelAndView bookDefinitionPromotion(
			@ModelAttribute(BookLibrarySelectionForm.FORM_NAME) @Valid BookLibrarySelectionForm form,
			BindingResult bindingResult, Model model) throws Exception {

		return new ModelAndView(WebConstants.VIEW_BOOK_DEFINITION_PROMOTION);
	}

	@RequestMapping(value = WebConstants.MVC_BOOK_LIBRARY_THUMBNAILS, method = RequestMethod.GET)
	public ModelAndView bookThumbnails(Model model) throws Exception {
		// TODO: implement this
		return new ModelAndView(WebConstants.VIEW_BOOK_LIBRARY_THUMBNAILS);
	}

	@RequestMapping(value = WebConstants.MVC_BOOK_LIBRARY_ICONS, method = RequestMethod.GET)
	public ModelAndView bookIcons(Model model) throws Exception {
		// TODO: implement this
		return new ModelAndView(WebConstants.VIEW_BOOK_LIBRARY_ICONS);
	}

	public BookLibraryService getBookLibraryService() {
		return bookLibraryService;
	}

	@Required
	public void setBookLibraryService(BookLibraryService bookLibraryService) {
		this.bookLibraryService = bookLibraryService;
	}

	@Required
	public void setValidator(Validator validator) {
		this.validator = validator;
	}

}
