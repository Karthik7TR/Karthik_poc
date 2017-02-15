package com.thomsonreuters.uscl.ereader.stats.domain;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.util.Assert;

/**
 * Holds sorting data used in Spring Batch job queries.
 */
public class PublishingStatsSort
{
    /**
     * The property names in the entities job execution and book audit tables that are sorted on for presentation.
     */
    public enum SortProperty
    {
        JOB_INSTANCE_ID,
        AUDIT_ID,
        EBOOK_DEFINITION_ID,
        JOB_SUBMITTER,
        JOB_SUBMIT_TIMESTAMP,
        BOOK_VERSION,
        PUBLISH_STATUS,
        BOOK_SIZE,
        LARGEST_DOC_SIZE,
        LARGEST_IMAGE_SIZE,
        LARGEST_PDF_SIZE,
        PROVIEW_DISPLAY_NAME,
        TITLE_ID
    } // publishing stats properties that can be sorted on

    /** Java bean property on which sorting should occur - entity maps this to the physical database column. */
    private SortProperty sortProperty;
    /** true if ascending sort, false if descending sort */
    private boolean ascending;

    private int pageNumber;
    private int itemsPerPage;

    /**
     * Default publish stat sort is by start time, descending order.
     */
    public PublishingStatsSort()
    {
        this(SortProperty.JOB_SUBMIT_TIMESTAMP, false, 1, 20);
    }

    /**
     * Used to indicate that we are sorting on a publishing stats property.
     * @param sortProperty which job/book property to sort on, not null.
     * @param ascending true for an ascending direction sort
     */
    public PublishingStatsSort(final SortProperty sortProperty, final boolean ascending, final int pageNumber, final int itemsPerPage)
    {
        Assert.notNull(sortProperty);
        this.sortProperty = sortProperty;
        this.ascending = ascending;
        this.pageNumber = pageNumber;
        this.itemsPerPage = itemsPerPage;
    }

    public SortProperty getSortProperty()
    {
        return sortProperty;
    }

    public boolean isAscending()
    {
        return ascending;
    }

    public String getSortDirection()
    {
        return getSortDirection(ascending);
    }

    public static String getSortDirection(final boolean anAscendingSort)
    {
        return (anAscendingSort) ? "asc" : "desc";
    }

    public int getPageNumber()
    {
        return pageNumber;
    }

    public int getItemsPerPage()
    {
        return itemsPerPage;
    }

    @Override
    public String toString()
    {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
