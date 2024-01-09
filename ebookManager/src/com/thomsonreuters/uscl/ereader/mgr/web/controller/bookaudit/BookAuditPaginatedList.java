package com.thomsonreuters.uscl.ereader.mgr.web.controller.bookaudit;

import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.bookaudit.BookAuditFilterForm.DisplayTagSortProperty;
import org.displaytag.pagination.PaginatedList;
import org.displaytag.properties.SortOrderEnum;

/**
 * A DisplayTag PaginatedList implementation for paging through the part lists of ebook audit.
 */
public class BookAuditPaginatedList implements PaginatedList {
    private List<EbookAudit> partialList;
    private int pageNumber; // Which page number of data is this
    private int fullListSize; // The size of the entire population of elements that are to be displayed in a paginated fashion
    private int itemsPerPage; // How many rows are to be shown on each page
    private DisplayTagSortProperty sortProperty; // Indicates the property that we want to sort by
    private boolean ascending; // True if the list is sorted in the ascending direction

    /**
     * Create the PaginatedList used for paging and sorting operations by DisplayTag.
     * None the parameters may be null.
     */
    public BookAuditPaginatedList(
        final List<EbookAudit> partialList,
        final int fullListSize,
        final int pageNumber,
        final int itemsPerPage,
        final DisplayTagSortProperty sortProperty,
        final boolean ascending) {
        this.partialList = partialList;
        this.fullListSize = fullListSize;
        this.pageNumber = pageNumber;
        this.itemsPerPage = itemsPerPage;
        this.sortProperty = sortProperty;
        this.ascending = ascending;
    }

    @Override
    public int getFullListSize() {
        return fullListSize;
    }

    @Override
    public List<EbookAudit> getList() {
        return partialList;
    }

    @Override
    public int getObjectsPerPage() {
        return itemsPerPage;
    }

    @Override
    public int getPageNumber() {
        return pageNumber;
    }

    @Override
    public String getSearchId() {
        return null;
    }

    @Override
    /** Returns of of the SortProperty values */
    public String getSortCriterion() {
        return sortProperty.toString();
    }

    @Override
    public SortOrderEnum getSortDirection() {
        return ascending ? SortOrderEnum.ASCENDING : SortOrderEnum.DESCENDING;
    }

    public boolean isAscendingSort() {
        return ascending;
    }
}
