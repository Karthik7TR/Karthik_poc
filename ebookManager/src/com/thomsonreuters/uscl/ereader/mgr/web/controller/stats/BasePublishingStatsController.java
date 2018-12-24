package com.thomsonreuters.uscl.ereader.mgr.web.controller.stats;

import java.util.List;

import javax.servlet.http.HttpSession;

import com.thomsonreuters.uscl.ereader.core.outage.service.OutageService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.PageAndSort;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.stats.PublishingStatsForm.DisplayTagSortProperty;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStatsFilter;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStatsSort;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStatsSort.SortProperty;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;
import org.displaytag.pagination.PaginatedList;
import org.springframework.ui.Model;

/**
 * Methods common to, and needed by both the PublishingStatsController and the PublishingStatsFilterController.
 */
public abstract class BasePublishingStatsController {
    protected static final String PAGE_AND_SORT_NAME = "publishingStatsPageAndSort";
    protected final PublishingStatsService publishingStatsService;
    protected final OutageService outageService;

    protected BasePublishingStatsController(final PublishingStatsService publishingStatsService,
        final OutageService outageService) {
        this.publishingStatsService = publishingStatsService;
        this.outageService = outageService;
    }

    /**
     * Fetch object containing the current page number, sort column, and sort direction as saved on the session.
     */
    protected PageAndSort<DisplayTagSortProperty> fetchSavedPageAndSort(final HttpSession httpSession) {
        PageAndSort<DisplayTagSortProperty> pageAndSort =
            (PageAndSort<DisplayTagSortProperty>) httpSession.getAttribute(PAGE_AND_SORT_NAME);
        if (pageAndSort == null) {
            pageAndSort = new PageAndSort<>(1, DisplayTagSortProperty.JOB_SUBMIT_TIMESTAMP, false);
        }
        return pageAndSort;
    }

    protected PublishingStatsFilterForm fetchSavedFilterForm(final HttpSession httpSession) {
        PublishingStatsFilterForm form =
            (PublishingStatsFilterForm) httpSession.getAttribute(PublishingStatsFilterForm.FORM_NAME);
        if (form == null) {
            form = new PublishingStatsFilterForm();
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
        final PublishingStatsFilterForm filterForm,
        final PageAndSort<DisplayTagSortProperty> pageAndSort,
        final HttpSession httpSession,
        final Model model) {
        // Save filter and paging state in the session
        httpSession.setAttribute(PublishingStatsFilterForm.FORM_NAME, filterForm);
        httpSession.setAttribute(PAGE_AND_SORT_NAME, pageAndSort);

        model.addAttribute(PublishingStatsFilterForm.FORM_NAME, filterForm);

        // Create the DisplayTag VDO object - the PaginatedList which wrappers the job execution partial list
        final PaginatedList paginatedList = createPaginatedList(pageAndSort, filterForm);
        model.addAttribute(WebConstants.KEY_PAGINATED_LIST, paginatedList);
        model.addAttribute(WebConstants.KEY_DISPLAY_OUTAGE, outageService.getAllPlannedOutagesToDisplay());
        httpSession.setAttribute(WebConstants.KEY_PAGINATED_LIST, paginatedList);
    }

    /**
     * Map the sort property name returned by display tag to the business object property name
     * for sort used in the service.
     * I.e. map a PageAndSortForm.DisplayTagSortProperty to a PublishingStatsSort.SortProperty
     * @param dtSortProperty display tag sort property key from the JSP
     * @param ascendingSort true to sort in ascending order
     * @return a ebookAudit sort business object used by the service to fetch the audit entities.
     */
    protected static PublishingStatsSort createStatsSort(final PageAndSort<DisplayTagSortProperty> pageAndSort) {
        return new PublishingStatsSort(
            SortProperty.valueOf(pageAndSort.getSortProperty().toString()),
            pageAndSort.isAscendingSort(),
            pageAndSort.getPageNumber(),
            pageAndSort.getObjectsPerPage());
    }

    protected static PublishingStatsFilter createStatsFilter(final PublishingStatsFilterForm filterForm) {
        return new PublishingStatsFilter(
            filterForm.getFromDate(),
            filterForm.getToDate(),
            filterForm.getTitleId(),
            filterForm.getProviewDisplayName(),
            filterForm.getBookDefinitionId());
    }

    /**
     * Create the partial paginated list used by DisplayTag to render to current page number of
     * list list of objects.
     * @param pageAndSort current page number, sort column, and sort direction (asc/desc).
     * @return an implemented DisplayTag paginated list interface
     */
    private PaginatedList createPaginatedList(
        final PageAndSort<DisplayTagSortProperty> pageAndSort,
        final PublishingStatsFilterForm filterForm) {
        final PublishingStatsFilter publishingStatsFilter = createStatsFilter(filterForm);
        final PublishingStatsSort publishingStatsSort = createStatsSort(pageAndSort);

        // Lookup all the EbookAudit objects by their primary key
        final List<PublishingStats> stats =
            publishingStatsService.findPublishingStats(publishingStatsFilter, publishingStatsSort);
        final int numberOfStats = publishingStatsService.numberOfPublishingStats(publishingStatsFilter);

        // Instantiate the object used by DisplayTag to render a partial list
        final PublishingStatsPaginatedList paginatedList = new PublishingStatsPaginatedList(
            stats,
            numberOfStats,
            pageAndSort.getPageNumber(),
            pageAndSort.getObjectsPerPage(),
            pageAndSort.getSortProperty(),
            pageAndSort.isAscendingSort());
        return paginatedList;
    }

    protected List<PublishingStats> fetch(final PublishingStatsFilterForm filterForm) {
        final PublishingStatsFilter publishingStatsFilter = createStatsFilter(filterForm);
        return publishingStatsService.findPublishingStats(publishingStatsFilter);
    }
}
