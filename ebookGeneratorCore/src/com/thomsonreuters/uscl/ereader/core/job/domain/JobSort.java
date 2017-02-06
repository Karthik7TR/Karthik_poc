/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.core.job.domain;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.springframework.util.Assert;

/**
 * Holds sorting data used in Spring Batch job queries.
 */
public class JobSort {
	
	/**
	 * The property names in the entities job execution and book audit tables that are sorted on for presentation.  
	 */
	public enum SortProperty { JOB_INSTANCE_ID, JOB_EXECUTION_ID, START_TIME, BATCH_STATUS,	// Job properties so sort on
							   TITLE_ID, BOOK_NAME, SUBMITTED_BY, SOURCE_TYPE };  // Book properties that can be sorted on
	
	/** Java bean property on which sorting should occur - entity maps this to the physical database column. */
	private SortProperty sortProperty;
	/** true if ascending sort, false if descending sort */
	private boolean ascending;
	
	/**
	 * Default Job sort is by job start time, descending order.
	 */
	public JobSort() {
		this(SortProperty.START_TIME, false);
	}
	
	/**
	 * Used to indicate that we are sorting on a job execution property.
	 * @param sortProperty which job/book property to sort on, not null. 
	 * @param ascending true for an ascending direction sort
	 */
	public JobSort(SortProperty sortProperty, boolean ascending) {
		Assert.notNull(sortProperty);
		this.sortProperty = sortProperty;
		this.ascending = ascending;
	}
	
	/**
	 * Returns true if we are sorting the job data by either the titleId or the bookName properties.
	 * Returning false implies that we are sorting on some column in the Spring Batch job execution table.
	 */
	public boolean isSortingOnBookProperty() {
		return (SortProperty.TITLE_ID.equals(sortProperty) ||
				SortProperty.BOOK_NAME.equals(sortProperty) ||
				SortProperty.SUBMITTED_BY.equals(sortProperty)||
				SortProperty.SOURCE_TYPE.equals(sortProperty));
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
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
