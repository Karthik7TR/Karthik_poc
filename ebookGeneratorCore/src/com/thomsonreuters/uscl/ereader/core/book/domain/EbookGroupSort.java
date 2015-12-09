package com.thomsonreuters.uscl.ereader.core.book.domain;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.springframework.util.Assert;

public class EbookGroupSort {

	/**
	 * The property names in the entities job execution and book audit tables that are sorted on for presentation.  
	 */
	public enum SortProperty { TITLE_ID, PROVIEW_DISPLAY_NAME, GROUP_NAME };  // Book properties that can be sorted on
	
	/** Java bean property on which sorting should occur - entity maps this to the physical database column. */
	private SortProperty sortProperty;
	/** true if ascending sort, false if descending sort */
	private boolean ascending;
	
	private int pageNumber;
	private int itemsPerPage;
	
	/**
	 * Default Job sort is by job start time, descending order.
	 */
	public EbookGroupSort() {
		this(SortProperty.TITLE_ID, false, 1, 20);
	}
	
	/**
	 * Used to indicate that we are sorting on a job execution property.
	 * @param sortProperty which job/book property to sort on, not null. 
	 * @param ascending true for an ascending direction sort
	 */
	public EbookGroupSort(SortProperty sortProperty, boolean ascending, int pageNumber, int itemsPerPage) {
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
	public static String getSortDirection(boolean anAscendingSort) {
		return (anAscendingSort) ? "asc" : "desc";
	}
	public int getPageNumber() {
		return pageNumber;
	}
	public int getItemsPerPage() {
		return itemsPerPage;
	}
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
