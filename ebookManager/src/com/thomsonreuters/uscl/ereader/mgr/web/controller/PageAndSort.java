package com.thomsonreuters.uscl.ereader.mgr.web.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Base class that holds the sorting, paging, and displayed row count presentation information.
 * The SortProperty parameterized type is an enumeration that identifies the column to be sorted on.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageAndSort<SortProperty> {
    public static final int DEFAULT_ITEMS_PER_PAGE = 20;

    private Integer pageNumber; // page number user wants to see (integer)
    private Integer objectsPerPage; // number of table rows displayed at one time on a page
    private SortProperty sortProperty; // identifier for the table column the user wants to sort on
    private boolean ascendingSort; // true for an ascending order sort on the selected column

    /**
     * 3-arg constructor, omits the items per page
     */
    public PageAndSort(final Integer pageNumber, final SortProperty sortProperty, final boolean ascendingSort) {
        this(pageNumber, DEFAULT_ITEMS_PER_PAGE, sortProperty, ascendingSort);
    }

    public void setSortAndAscendingProperties(final SortProperty sort, final boolean ascending) {
        if (sort != null) {
            setSortProperty(sort);
            setAscendingSort(ascending);
        }
    }

}
