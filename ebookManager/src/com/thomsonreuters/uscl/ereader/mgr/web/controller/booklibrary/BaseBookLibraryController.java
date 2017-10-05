package com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary;

import java.util.List;

import javax.servlet.http.HttpSession;

import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.service.CodeService;
import com.thomsonreuters.uscl.ereader.core.outage.service.OutageService;
import com.thomsonreuters.uscl.ereader.mgr.library.service.LibraryListService;
import com.thomsonreuters.uscl.ereader.mgr.library.vdo.LibraryList;
import com.thomsonreuters.uscl.ereader.mgr.library.vdo.LibraryListFilter;
import com.thomsonreuters.uscl.ereader.mgr.library.vdo.LibraryListSort;
import com.thomsonreuters.uscl.ereader.mgr.library.vdo.LibraryListSort.SortProperty;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.PageAndSort;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary.BookLibrarySelectionForm.DisplayTagSortProperty;
import org.displaytag.pagination.PaginatedList;
import org.springframework.ui.Model;

/**
 * Methods common to, and needed by both the BookLibraryController and the BookLibraryFilterController.
 */
public abstract class BaseBookLibraryController {
    public static final String PAGE_AND_SORT_NAME = "bookLibraryPageAndSort";

    protected final LibraryListService libraryService;
    protected final CodeService codeService;
    protected final OutageService outageService;

    protected BaseBookLibraryController(
        final LibraryListService libraryService,
        final CodeService codeService,
        final OutageService outageService) {
        this.libraryService = libraryService;
        this.codeService = codeService;
        this.outageService = outageService;
    }

    /**
     * Fetch object containing the current page number, sort column, and sort direction as saved on the session.
     */
    protected PageAndSort<DisplayTagSortProperty> fetchSavedPageAndSort(final HttpSession httpSession) {
        PageAndSort<DisplayTagSortProperty> pageAndSort =
            (PageAndSort<DisplayTagSortProperty>) httpSession.getAttribute(PAGE_AND_SORT_NAME);
        if (pageAndSort == null) {
            pageAndSort = new PageAndSort<>(1, DisplayTagSortProperty.PROVIEW_DISPLAY_NAME, true);
        }
        return pageAndSort;
    }

    protected BookLibraryFilterForm fetchSavedFilterForm(final HttpSession httpSession) {
        BookLibraryFilterForm form = (BookLibraryFilterForm) httpSession.getAttribute(BookLibraryFilterForm.FORM_NAME);
        if (form == null) {
            form = new BookLibraryFilterForm();
        }
        return form;
    }

    /**
     * Handles the current paging and sorting state and creates the DisplayTag PaginatedList object
     * for use by the DisplayTag custom tag in the JSP.
     */
    protected void setUpModel(
        final BookLibraryFilterForm filterForm,
        final PageAndSort<DisplayTagSortProperty> pageAndSort,
        final HttpSession httpSession,
        final Model model) {
        // Save filter and paging state in the session
        httpSession.setAttribute(BookLibraryFilterForm.FORM_NAME, filterForm);
        httpSession.setAttribute(PAGE_AND_SORT_NAME, pageAndSort);

        model.addAttribute(BookLibraryFilterForm.FORM_NAME, filterForm);

        // Create the DisplayTag VDO object - the PaginatedList which wrappers the Book Definition partial list
        final PaginatedList paginatedList = createPaginatedList(pageAndSort, filterForm);
        model.addAttribute(WebConstants.KEY_PAGINATED_LIST, paginatedList);

        final List<KeywordTypeCode> codes = codeService.getAllKeywordTypeCodes();
        // Add keywords
        model.addAttribute(WebConstants.KEY_KEYWORD_TYPE_CODE, codes);
        model.addAttribute(WebConstants.KEY_DISPLAY_OUTAGE, outageService.getAllPlannedOutagesToDisplay());
    }

    /**
     * Map the sort property name returned by display tag to the business object property name
     * for sort used in the service.
     * I.e. map a PageAndSortForm.DisplayTagSortProperty to a LibraryListSort.SortProperty
     */
    protected static LibraryListSort createLibraryListSort(final PageAndSort<DisplayTagSortProperty> pageAndSort) {
        return new LibraryListSort(
            SortProperty.valueOf(pageAndSort.getSortProperty().toString()),
            pageAndSort.isAscendingSort(),
            pageAndSort.getPageNumber(),
            pageAndSort.getObjectsPerPage());
    }

    /**
     * Create the partial paginated list used by DisplayTag to render to current page number of
     * list list of objects.
     */
    private PaginatedList createPaginatedList(
        final PageAndSort<DisplayTagSortProperty> pageAndSort,
        final BookLibraryFilterForm filterForm) {
        final String action = filterForm.getAction() != null ? filterForm.getAction().toString() : null;
        final LibraryListFilter libraryListFilter = new LibraryListFilter(
            filterForm.getFrom(),
            filterForm.getTo(),
            action,
            filterForm.getTitleId(),
            filterForm.getProviewDisplayName(),
            filterForm.getIsbn(),
            filterForm.getMaterialId(),
            filterForm.getProviewKeyword());
        final LibraryListSort libraryListSort = createLibraryListSort(pageAndSort);

        // Lookup all the EbookAudit objects by their primary key
        final List<LibraryList> bookDefinitions =
            libraryService.findBookDefinitions(libraryListFilter, libraryListSort);
        final Integer numberOfBooks = libraryService.numberOfBookDefinitions(libraryListFilter);

        // Instantiate the object used by DisplayTag to render a partial list
        final BookLibraryPaginatedList paginatedList = new BookLibraryPaginatedList(
            bookDefinitions,
            numberOfBooks,
            pageAndSort.getPageNumber(),
            pageAndSort.getObjectsPerPage(),
            pageAndSort.getSortProperty(),
            pageAndSort.isAscendingSort());

        return paginatedList;
    }
}
