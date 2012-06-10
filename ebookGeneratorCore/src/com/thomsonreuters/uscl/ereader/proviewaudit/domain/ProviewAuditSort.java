/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.proviewaudit.domain;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.springframework.util.Assert;

public class ProviewAuditSort {
	
	/**
	 * The property names in the entities proview audit table that are sorted on for presentation.  
	 */
	public enum SortProperty { TITLE_ID, BOOK_VERSION, BOOK_LAST_UPDATED, USERNAME, PROVIEW_REQUEST, REQUEST_DATE };  // properties that can be sorted on
	
	/** Java bean property on which sorting should occur - entity maps this to the physical database column. */
	private SortProperty sortProperty;
	/** true if ascending sort, false if descending sort */
	private boolean ascending;
	
	private int pageNumber;
	private int itemsPerPage;
	
	/**
	 * Default sort is by audit creation time, descending order.
	 */
	public ProviewAuditSort() {
		this(SortProperty.REQUEST_DATE, false, 1, 20);
	}
	
	/**
	 * Used to indicate that we are sorting on a property.
	 * @param sortProperty which property to sort on, not null. 
	 * @param ascending true for an ascending direction sort
	 */
	public ProviewAuditSort(SortProperty sortProperty, boolean ascending, int pageNumber, int itemsPerPage) {
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
