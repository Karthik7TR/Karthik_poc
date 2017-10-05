package com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import com.thomsonreuters.uscl.ereader.core.book.service.CodeService;
import com.thomsonreuters.uscl.ereader.core.outage.service.OutageService;
import com.thomsonreuters.uscl.ereader.mgr.library.service.LibraryListService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.PageAndSort;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary.BookLibrarySelectionForm.Command;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary.BookLibrarySelectionForm.DisplayTagSortProperty;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

@Controller
public class BookLibraryController extends BaseBookLibraryController
{
    private static final Logger log = LogManager.getLogger(BookLibraryController.class);

    private final Validator validator;

    @Autowired
    public BookLibraryController(final LibraryListService libraryService,
                                 final CodeService codeService,
                                 final OutageService outageService,
                                 @Qualifier("bookLibrarySelectionFormValidator") final Validator validator)
    {
        super(libraryService, codeService, outageService);
        this.validator = validator;
    }

    @InitBinder(BookLibrarySelectionForm.FORM_NAME)
    protected void initDataBinder(final WebDataBinder binder)
    {
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
    public ModelAndView inboundGet(final HttpSession httpSession, final Model model)
    {
        log.debug(">>>");
        final BookLibraryFilterForm filterForm = fetchSavedFilterForm(httpSession); // from session
        final PageAndSort<DisplayTagSortProperty> savedPageAndSort = fetchSavedPageAndSort(httpSession); // from session

        final BookLibrarySelectionForm librarySelectionForm = new BookLibrarySelectionForm();
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
    public ModelAndView pagingAndSorting(
        final HttpSession httpSession,
        @ModelAttribute(BookLibrarySelectionForm.FORM_NAME) final BookLibrarySelectionForm form,
        final Model model)
    {
        final BookLibraryFilterForm filterForm = fetchSavedFilterForm(httpSession);
        final PageAndSort<DisplayTagSortProperty> pageAndSort = fetchSavedPageAndSort(httpSession);
        form.setObjectsPerPage(pageAndSort.getObjectsPerPage());
        final Integer nextPageNumber = form.getPage();

        // If there was a page=n query string parameter, then we assume we are paging since this
        // parameter is not present on the query string when display tag sorting.
        if (nextPageNumber != null)
        { // PAGING
            pageAndSort.setPageNumber(nextPageNumber);
        }
        else
        { // SORTING
            pageAndSort.setPageNumber(1);
            pageAndSort.setSortProperty(form.getSort());
            pageAndSort.setAscendingSort(form.isAscendingSort());
        }
        setUpModel(filterForm, pageAndSort, httpSession, model);

        return new ModelAndView(WebConstants.VIEW_BOOK_LIBRARY_LIST);
    }

    @RequestMapping(value = WebConstants.MVC_BOOK_LIBRARY_LIST_SELECTION_POST, method = RequestMethod.POST)
    public ModelAndView postBookDefinitionSelections(
        final HttpSession httpSession,
        final HttpServletRequest request,
        @ModelAttribute(BookLibrarySelectionForm.FORM_NAME) @Valid final BookLibrarySelectionForm form,
        final BindingResult bindingResult,
        final Model model) throws Exception
    {
        if (!bindingResult.hasErrors())
        {
            ModelAndView mav = null;
            final String[] bookKeys = form.getSelectedEbookKeys();
            final StringBuilder parameters = new StringBuilder();
            parameters.append("?");
            for (final String key : bookKeys)
            {
                parameters.append("id=" + key + "&");
            }
            parameters.deleteCharAt(parameters.length() - 1);

            final Command command = form.getCommand();
            switch (command)
            {
            case GENERATE:
                if (bookKeys.length > 1)
                {
                    mav = new ModelAndView(
                        new RedirectView(WebConstants.MVC_BOOK_BULK_GENERATE_PREVIEW + parameters.toString()));
                }
                else
                {
                    mav = new ModelAndView(
                        new RedirectView(WebConstants.MVC_BOOK_SINGLE_GENERATE_PREVIEW + parameters.toString()));
                }
                break;
            default:
                throw new RuntimeException("Unexpected form command: " + command);
            }

            return mav;
        }

        final BookLibraryFilterForm filterForm = fetchSavedFilterForm(httpSession);
        final PageAndSort<DisplayTagSortProperty> pageAndSort = fetchSavedPageAndSort(httpSession);
        form.setObjectsPerPage(pageAndSort.getObjectsPerPage());

        setUpModel(filterForm, pageAndSort, httpSession, model);

        return new ModelAndView(WebConstants.VIEW_BOOK_LIBRARY_LIST);
    }

    /**
     * Handle URL request that the number of rows displayed in table be changed.
     */
    @RequestMapping(value = WebConstants.MVC_BOOK_LIBRARY_CHANGE_ROW_COUNT, method = RequestMethod.POST)
    public ModelAndView handleChangeInItemsToDisplay(
        final HttpSession httpSession,
        @ModelAttribute(BookLibrarySelectionForm.FORM_NAME) @Valid final BookLibrarySelectionForm form,
        final Model model)
    {
        log.debug(form);
        final PageAndSort<DisplayTagSortProperty> pageAndSort = fetchSavedPageAndSort(httpSession);
        pageAndSort.setPageNumber(1); // Always start from first page again once changing row count to avoid index out of bounds
        pageAndSort.setObjectsPerPage(form.getObjectsPerPage()); // Update the new number of items to be shown at one time
        // Restore the state of the search filter
        final BookLibraryFilterForm filterForm = fetchSavedFilterForm(httpSession);
        setUpModel(filterForm, pageAndSort, httpSession, model);
        return new ModelAndView(WebConstants.VIEW_BOOK_LIBRARY_LIST);
    }
}
