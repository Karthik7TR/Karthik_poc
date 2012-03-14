package com.thomsonreuters.uscl.ereader.mgr.web.controller;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Base class that holds the sorting, paging, and displayed row count presentation information.
 */
public class PageAndSort<SortProperty> {
	public static final int DEFAULT_ITEMS_PER_PAGE = 20;
	
	private Integer pageNumber;		// page number user wants to see (integer)
	private Integer objectsPerPage;	// number of table rows displayed at one time on a page
	private SortProperty sortProperty;		// identifier for the table column the user wants to sort on
	private boolean ascendingSort;	// true for an ascending order sort on the selected column

	public PageAndSort() {
		super();
	}

	public PageAndSort(Integer pageNumber, Integer itemsPerPage,
					   SortProperty sortProperty, boolean ascendingSort) {
		this.pageNumber = pageNumber;
		this.objectsPerPage = itemsPerPage;
		this.sortProperty = sortProperty;
		this.ascendingSort = ascendingSort;
	}
	/**
	 * Returns the number of table rows displayed at one time on a page
	 */
	public Integer getObjectsPerPage() {
		return objectsPerPage;
	}
	/**
	 * Returns the number of table rows displayed at one time on a page.
	 */
	public Integer getPageNumber() {
		return pageNumber;
	}
	/**
	 * Returns the identifier for the table column the user wants to sort on
	 */
	public SortProperty getSortProperty() {
		return sortProperty;
	}
	/**
	 * Returns true to indicate ascending order sort on the selected column, false for descending sort order.
	 */
	public boolean isAscendingSort() {
		return ascendingSort;
	}
	
	public void setAscendingSort(boolean tf) {
		this.ascendingSort = tf;
	}
	public void setObjectsPerPage(Integer itemsPerPage) {
		this.objectsPerPage = itemsPerPage;
	}
	public void setPageNumber(Integer page) {
		this.pageNumber = page;
	}
	public void setSortProperty(SortProperty columnId) {
		this.sortProperty = columnId;
	}

	public String toString() {
		return ReflectionToStringBuilder.toString(this,
				ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
