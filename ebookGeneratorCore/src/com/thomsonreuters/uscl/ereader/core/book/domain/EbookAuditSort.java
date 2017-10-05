package com.thomsonreuters.uscl.ereader.core.book.domain;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.util.Assert;

/**
 * Holds sorting data used in Spring Batch job queries.
 */
public class EbookAuditSort {
    /**
     * The property names in the entities job execution and book audit tables that are sorted on for presentation.
     */
    public enum SortProperty {
        TITLE_ID,
        BOOK_NAME,
        BOOK_DEFINITION_ID,
        SUBMITTED_DATE,
        ACTION,
        SUBMITTED_BY
    }; // Book properties that can be sorted on

    /** Java bean property on which sorting should occur - entity maps this to the physical database column. */
    private SortProperty sortProperty;
    /** true if ascending sort, false if descending sort */
    private boolean ascending;

    private int pageNumber;
    private int itemsPerPage;

    /**
     * Default Job sort is by job start time, descending order.
     */
    public EbookAuditSort() {
        this(SortProperty.SUBMITTED_DATE, false, 1, 20);
    }

    /**
     * Used to indicate that we are sorting on a job execution property.
     * @param sortProperty which job/book property to sort on, not null.
     * @param ascending true for an ascending direction sort
     */
    public EbookAuditSort(
        final SortProperty sortProperty,
        final boolean ascending,
        final int pageNumber,
        final int itemsPerPage) {
        Assert.notNull(sortProperty);
        this.sortProperty = sortProperty;
        this.ascending = ascending;
        this.pageNumber = pageNumber;
        this.itemsPerPage = itemsPerPage;
    }

    public SortProperty getSortProperty() {
        return sortProperty;
    }

    public boolean isAscending() {
        return ascending;
    }

    public String getSortDirection() {
        return getSortDirection(ascending);
    }

    public static String getSortDirection(final boolean anAscendingSort) {
        return (anAscendingSort) ? "asc" : "desc";
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public int getItemsPerPage() {
        return itemsPerPage;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
