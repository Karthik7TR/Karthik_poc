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
import org.springframework.web.servlet.view.RedirectView;

import com.thomsonreuters.uscl.ereader.core.library.domain.LibraryList;
import com.thomsonreuters.uscl.ereader.core.library.service.LibraryListService;
import com.thomsonreuters.uscl.ereader.mgr.web.UserUtils;
import com.thomsonreuters.uscl.ereader.mgr.web.UserUtils.SecurityRole;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary.BookLibrarySelectionForm.Command;

@Controller
public class BookLibraryController {
	// private static final Logger log =
	// Logger.getLogger(BookLibraryController.class);

	private LibraryListService libraryListService;
	private Validator validator;

	@InitBinder(BookLibrarySelectionForm.FORM_NAME)
	protected void initDataBinder(WebDataBinder binder) {
		binder.setValidator(validator);
	}

	private BookLibraryFilterForm fetchSavedFilterForm(HttpSession httpSession) {
		BookLibraryFilterForm form = (BookLibraryFilterForm) httpSession
				.getAttribute(BookLibraryFilterForm.FORM_NAME);
		if (form == null) {
			form = new BookLibraryFilterForm();
		}
		return form;
	}

	private List<LibraryList> fetchSavedPaginatedList(HttpSession httpSession) {
		List<LibraryList> paginatedList = (List<LibraryList>) httpSession
				.getAttribute(WebConstants.KEY_PAGINATED_LIST);
		return paginatedList;
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
	public ModelAndView bookList(
			HttpSession httpSession,
			@ModelAttribute(BookLibrarySelectionForm.FORM_NAME) BookLibrarySelectionForm form,
			BindingResult bindingResult, Model model) throws Exception {

		BookLibraryFilterForm bookLibraryFilterForm = fetchSavedFilterForm(httpSession);
		model.addAttribute(BookLibraryFilterForm.FORM_NAME,
				bookLibraryFilterForm);

		List<LibraryList> paginatedList = fetchSavedPaginatedList(httpSession);

		if (paginatedList == null) {
			paginatedList = libraryListService.findBookDefinitions(
					"proviewDisplayName", true, 1,
					WebConstants.NUMBER_BOOK_DEF_SHOWN, null, null, null, null,
					null, null, null, httpSession);
			saveSessionValues(httpSession, bookLibraryFilterForm, paginatedList);
		}

		initializeFormAndModel(model, form, "proviewDisplayName", true, 1,
				paginatedList);

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

			BindingResult bindingResult, Model model) throws Exception {

		BookLibraryFilterForm bookLibraryFilterForm = fetchSavedFilterForm(httpSession);
		model.addAttribute(BookLibraryFilterForm.FORM_NAME,
				bookLibraryFilterForm);

		String sort = request.getParameter(new ParamEncoder(
				WebConstants.KEY_VDO)
				.encodeParameterName(TableTagParameters.PARAMETER_SORT));

		String order = request.getParameter(new ParamEncoder(
				WebConstants.KEY_VDO)
				.encodeParameterName(TableTagParameters.PARAMETER_ORDER));
		boolean isAscending = order != null && order.equals("2") ? false : true;

		int page = Integer.parseInt(request.getParameter(new ParamEncoder(
				WebConstants.KEY_VDO)
				.encodeParameterName(TableTagParameters.PARAMETER_PAGE)));

		List<LibraryList> paginatedList = fetchSavedPaginatedList(httpSession);

		if (paginatedList == null || sort != null) {
			paginatedList = libraryListService.findBookDefinitions(sort,
					isAscending, page, WebConstants.NUMBER_BOOK_DEF_SHOWN,
					bookLibraryFilterForm.getProviewDisplayName(),
					bookLibraryFilterForm.getTitleId(),
					bookLibraryFilterForm.getIsbn(),
					bookLibraryFilterForm.getMaterialId(),
					bookLibraryFilterForm.getTo(),
					bookLibraryFilterForm.getFrom(),
					bookLibraryFilterForm.geteBookDefStatus(), httpSession);
			saveSessionValues(httpSession, bookLibraryFilterForm, paginatedList);
		}
		initializeFormAndModel(model, form, sort, isAscending, page,
				paginatedList);

		return new ModelAndView(WebConstants.VIEW_BOOK_LIBRARY_LIST);
	}

	@RequestMapping(value = WebConstants.MVC_BOOK_LIBRARY_LIST, method = RequestMethod.POST)
	public ModelAndView postBookDefinitionSelections(
			HttpServletRequest request,
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

		return new ModelAndView(WebConstants.VIEW_BOOK_LIBRARY_LIST);
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
			BookLibrarySelectionForm form, String sortBy, boolean isAscending,
			int pageNumber, List<LibraryList> paginatedList) {

		Long resultSize = (long) paginatedList.size();

		model.addAttribute(WebConstants.KEY_PAGINATED_LIST, paginatedList);
		model.addAttribute(WebConstants.KEY_TOTAL_BOOK_SIZE,
				resultSize.intValue());

		SecurityRole[] roles = { SecurityRole.ROLE_PUBLISHER,
				SecurityRole.ROLE_SUPERUSER, SecurityRole.ROLE_PUBLISHER_PLUS };
		model.addAttribute(WebConstants.KEY_SUPER_PUBLISHER_PUBLISHERPLUS,
				UserUtils.isUserInRole(roles) ? "" : "disabled=\"disabled\"");
		form.setIsAscending(isAscending);
		form.setPage(pageNumber);
		form.setSort(sortBy);
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

	@Required
	public void setLibraryListService(LibraryListService service) {
		this.libraryListService = service;
	}

	@Required
	public void setValidator(Validator validator) {
		this.validator = validator;
	}

}
