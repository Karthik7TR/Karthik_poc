package com.thomsonreuters.uscl.ereader.mgr.web.controller.job.queue;

import java.util.List;

import com.thomsonreuters.uscl.ereader.core.job.domain.JobRequest;
import org.displaytag.pagination.PaginatedList;
import org.displaytag.properties.SortOrderEnum;

/**
 * A DisplayTag PaginatedList implementation for paging through a list of job run requests.
 */
public class QueuePaginatedList<SortProperty> implements PaginatedList {
    //private static final Logger log = LogManager.getLogger(JobPaginatedList.class);

    private List<JobRequest> partialList;
    private int fullListSize;
    private int pageNumber; // Which page number of data is this
    private int objectsPerPage; // How many rows are to be shown on each page
    private SortProperty sortProperty; // Indicates the column that we want to sort by
    private boolean ascending; // True if the list is sorted in the ascending direction

    /**
     * Create the PaginatedList used for paging and sorting operations by DisplayTag.
     * None the parameters may be null.
     */
    public QueuePaginatedList(
        final List<JobRequest> partialList,
        final int fullListSize,
        final int pageNumber,
        final int objectsPerPage,
        final SortProperty sortProperty,
        final boolean ascending) {
        this.partialList = partialList;
        this.fullListSize = fullListSize;
        this.pageNumber = pageNumber;
        this.objectsPerPage = objectsPerPage;
        this.sortProperty = sortProperty;
        this.ascending = ascending;
    }

    @Override
    public int getFullListSize() {
        return fullListSize;
    }

    @Override
    public List<JobRequest> getList() {
        return partialList;
    }

    @Override
    public int getObjectsPerPage() {
        return objectsPerPage;
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
    public String getSortCriterion() {
        return sortProperty.toString();
    }

    @Override
    public SortOrderEnum getSortDirection() {
        return (ascending) ? SortOrderEnum.ASCENDING : SortOrderEnum.DESCENDING;
    }

    public boolean isAscendingSort() {
        return ascending;
    }
}
