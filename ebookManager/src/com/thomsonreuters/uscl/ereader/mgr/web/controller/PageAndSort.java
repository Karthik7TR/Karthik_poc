package com.thomsonreuters.uscl.ereader.mgr.web.controller;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Base class that holds the sorting, paging, and displayed row count presentation information.
 * The SortProperty parameterized type is an enumeration that identifies the column to be sorted on.
 */
public class PageAndSort<SortProperty>
{
    public static final int DEFAULT_ITEMS_PER_PAGE = 20;

    private Integer pageNumber; // page number user wants to see (integer)
    private Integer objectsPerPage; // number of table rows displayed at one time on a page
    private SortProperty sortProperty; // identifier for the table column the user wants to sort on
    private boolean ascendingSort; // true for an ascending order sort on the selected column

    public PageAndSort()
    {
        super();
    }

    /**
     * 3-arg constructor, omits the items per page
     */
    public PageAndSort(final Integer pageNumber, final SortProperty sortProperty, final boolean ascendingSort)
    {
        this(pageNumber, DEFAULT_ITEMS_PER_PAGE, sortProperty, ascendingSort);
    }

    /**
     * 4-arg full constructor.
     */
    public PageAndSort(final Integer pageNumber, final Integer itemsPerPage, final SortProperty sortProperty, final boolean ascendingSort)
    {
        this.pageNumber = pageNumber;
        this.objectsPerPage = itemsPerPage;
        this.sortProperty = sortProperty;
        this.ascendingSort = ascendingSort;
    }

    /**
     * Returns the number of table rows displayed at one time on a page
     */
    public Integer getObjectsPerPage()
    {
        return objectsPerPage;
    }

    /**
     * Returns the number of table rows displayed at one time on a page.
     */
    public Integer getPageNumber()
    {
        return pageNumber;
    }

    /**
     * Returns the identifier for the table column the user wants to sort on
     */
    public SortProperty getSortProperty()
    {
        return sortProperty;
    }

    /**
     * Returns true to indicate ascending order sort on the selected column, false for descending sort order.
     */
    public boolean isAscendingSort()
    {
        return ascendingSort;
    }

    public void setAscendingSort(final boolean tf)
    {
        this.ascendingSort = tf;
    }

    public void setObjectsPerPage(final Integer itemsPerPage)
    {
        this.objectsPerPage = itemsPerPage;
    }

    public void setPageNumber(final Integer page)
    {
        this.pageNumber = page;
    }

    public void setSortProperty(final SortProperty columnId)
    {
        this.sortProperty = columnId;
    }

    @Override
    public String toString()
    {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
