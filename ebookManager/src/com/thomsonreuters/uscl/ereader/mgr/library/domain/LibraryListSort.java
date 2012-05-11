/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.library.domain;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.springframework.util.Assert;

/**
 * Holds sorting data used in Library List
 */
public class LibraryListSort {
	
	public enum SortProperty  { PROVIEW_DISPLAY_NAME, TITLE_ID, LAST_GENERATED_DATE, DEFINITION_STATUS, LAST_EDIT_DATE };
	
	/** Java bean property on which sorting should occur - entity maps this to the physical database column. */
	private SortProperty sortProperty;
	/** true if ascending sort, false if descending sort */
	private boolean ascending;
	
	private int pageNumber;
	private int itemsPerPage;
	
	/**
	 * Default Library List sort order is on ProView Display Name, ascending order.
	 */
	public LibraryListSort() {
		this(SortProperty.PROVIEW_DISPLAY_NAME, true, 1, 20);
	}
	
	/**
	 * Used to indicate that we are sorting on a library list property.
	 * @param sortProperty
	 * @param ascending true for an ascending direction sort
	 */
	public LibraryListSort(SortProperty sortProperty, boolean ascending, int pageNumber, int itemsPerPage) {
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
