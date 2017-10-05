package com.thomsonreuters.uscl.ereader.mgr.web.controller.bookaudit;

import java.util.List;

import javax.servlet.http.HttpSession;

import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAuditFilter;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAuditSort;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAuditSort.SortProperty;
import com.thomsonreuters.uscl.ereader.core.book.service.EBookAuditService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.PageAndSort;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.bookaudit.BookAuditForm.DisplayTagSortProperty;
import org.displaytag.pagination.PaginatedList;
import org.springframework.ui.Model;

/**
 * Methods common to, and needed by both the BookAuditController and the FilterFormController.
 */
public abstract class BaseBookAuditController {
    protected static final String PAGE_AND_SORT_NAME = "auditPageAndSort";
    protected final EBookAuditService auditService;

    protected BaseBookAuditController(final EBookAuditService auditService) {
        this.auditService = auditService;
    }

    /**
     * Fetch object containing the current page number, sort column, and sort direction as saved on the session.
     */
    protected PageAndSort<DisplayTagSortProperty> fetchSavedPageAndSort(final HttpSession httpSession) {
        PageAndSort<DisplayTagSortProperty> pageAndSort =
            (PageAndSort<DisplayTagSortProperty>) httpSession.getAttribute(PAGE_AND_SORT_NAME);
        if (pageAndSort == null) {
            pageAndSort = new PageAndSort<>(1, DisplayTagSortProperty.SUBMITTED_DATE, false);
        }
        return pageAndSort;
    }

    protected BookAuditFilterForm fetchSavedFilterForm(final HttpSession httpSession) {
        BookAuditFilterForm form = (BookAuditFilterForm) httpSession.getAttribute(BookAuditFilterForm.FORM_NAME);
        if (form == null) {
            form = new BookAuditFilterForm();
        }
        return form;
    }

    /**
     * Handles the current paging and sorting state and creates the DisplayTag PaginatedList object
     * for use by the DisplayTag custom tag in the JSP.
     * @param pageAndSortForm paging/sorting/direction/display count
     * @param jobExecutionIds list of
     * @param httpSession
     * @param model
     */
    protected void setUpModel(
        final BookAuditFilterForm filterForm,
        final PageAndSort<DisplayTagSortProperty> pageAndSort,
        final HttpSession httpSession,
        final Model model) {
        // Save filter and paging state in the session
        httpSession.setAttribute(BookAuditFilterForm.FORM_NAME, filterForm);
        httpSession.setAttribute(PAGE_AND_SORT_NAME, pageAndSort);

        model.addAttribute(BookAuditFilterForm.FORM_NAME, filterForm);

        // Create the DisplayTag VDO object - the PaginatedList which wrappers the job execution partial list
        final PaginatedList paginatedList = createPaginatedList(pageAndSort, filterForm);
        model.addAttribute(WebConstants.KEY_PAGINATED_LIST, paginatedList);
    }

    /**
     * Map the sort property name returned by display tag to the business object property name
     * for sort used in the service.
     * I.e. map a PageAndSortForm.DisplayTagSortProperty to a EbookAuditSort.SortProperty
     * @param dtSortProperty display tag sort property key from the JSP
     * @param ascendingSort true to sort in ascending order
     * @return a ebookAudit sort business object used by the service to fetch the audit entities.
     */
    protected static EbookAuditSort createBookAuditSort(final PageAndSort<DisplayTagSortProperty> pageAndSort) {
        return new EbookAuditSort(
            SortProperty.valueOf(pageAndSort.getSortProperty().toString()),
            pageAndSort.isAscendingSort(),
            pageAndSort.getPageNumber(),
            pageAndSort.getObjectsPerPage());
    }

    /**
     * Create the partial paginated list used by DisplayTag to render to current page number of
     * list list of objects.
     * @param pageAndSort current page number, sort column, and sort direction (asc/desc).
     * @return an implemented DisplayTag paginated list interface
     */
    private PaginatedList createPaginatedList(
        final PageAndSort<DisplayTagSortProperty> pageAndSort,
        final BookAuditFilterForm filterForm) {
        final String action = filterForm.getAction() != null ? filterForm.getAction().toString() : null;
        final EbookAuditFilter bookAuditFilter = new EbookAuditFilter(
            filterForm.getFromDate(),
            filterForm.getToDate(),
            action,
            filterForm.getTitleId(),
            filterForm.getProviewDisplayName(),
            filterForm.getSubmittedBy(),
            filterForm.getBookDefinitionId());
        final EbookAuditSort bookAuditSort = createBookAuditSort(pageAndSort);

        // Lookup all the EbookAudit objects by their primary key
        final List<EbookAudit> audits = auditService.findEbookAudits(bookAuditFilter, bookAuditSort);
        final int numberOfAudits = auditService.numberEbookAudits(bookAuditFilter);

        // Instantiate the object used by DisplayTag to render a partial list
        final BookAuditPaginatedList paginatedList = new BookAuditPaginatedList(
            audits,
            numberOfAudits,
            pageAndSort.getPageNumber(),
            pageAndSort.getObjectsPerPage(),
            pageAndSort.getSortProperty(),
            pageAndSort.isAscendingSort());
        return paginatedList;
    }
}
