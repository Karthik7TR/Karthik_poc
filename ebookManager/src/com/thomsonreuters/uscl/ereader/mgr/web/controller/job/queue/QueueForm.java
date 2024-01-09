package com.thomsonreuters.uscl.ereader.mgr.web.controller.job.queue;

import com.thomsonreuters.uscl.ereader.mgr.web.controller.PageAndSort;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Form backing object for the table of jobs that are queued to run
 * (not scheduled, which is a separate table and form).
 */
public class QueueForm {
    public static final String FORM_NAME = "queueForm";

    /** Sortable columns on the Job Queue page */
    public enum DisplayTagSortProperty {
        BOOK_NAME,
        TITLE_ID,
        SOURCE_TYPE,
        BOOK_VERSION,
        PRIORITY,
        SUBMITTED_BY,
        SUBMITTED_AT
    };

    private Long[] ids;
    private PageAndSort<DisplayTagSortProperty> pageAndSort = new PageAndSort<>(); // sort, page, dir, objectsPerPage

    public boolean isAscendingSort() {
        return pageAndSort.isAscendingSort();
    }

    public String getDir() {
        return (pageAndSort.isAscendingSort()) ? "asc" : "desc";
    }

    public Long[] getIds() {
        return ids;
    }

    public Integer getPage() {
        return pageAndSort.getPageNumber();
    }

    public DisplayTagSortProperty getSort() {
        return getSortProperty();
    }

    public DisplayTagSortProperty getSortProperty() {
        return pageAndSort.getSortProperty();
    }

    public void setDir(final String direction) {
        setAscendingSort("asc".equals(direction));
    }

    public void setAscendingSort(final boolean ascending) {
        pageAndSort.setAscendingSort(ascending);
    }

    public void setIds(final Long[] ids) {
        this.ids = ids;
    }

    public void setPage(final Integer pageNumber) {
        pageAndSort.setPageNumber(pageNumber);
    }

    public void setSort(final DisplayTagSortProperty sortProperty) {
        pageAndSort.setSortProperty(sortProperty);
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
