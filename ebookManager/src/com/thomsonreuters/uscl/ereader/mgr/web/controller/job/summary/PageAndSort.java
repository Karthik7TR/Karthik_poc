package com.thomsonreuters.uscl.ereader.mgr.web.controller.job.summary;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Holds the sorting, paging, and displayed row count presentation information.
 */
public class PageAndSort {
	public static final int DEFAULT_ITEMS_PER_PAGE = 20;
	
	public enum DisplayTagSortProperty { TITLE_ID, BOOK_NAME, START_TIME, BATCH_STATUS, JOB_INSTANCE_ID, JOB_EXECUTION_ID }

	private Integer pageNumber;	// page number user wants to see (integer)
	private Integer objectsPerPage;		// number of rows dislayed at one time
	private DisplayTagSortProperty sortProperty;	// which column the user wants to sort on
	private boolean ascendingSort;	// true for an ascending order sort

	public PageAndSort() {
		super();
	}

	public PageAndSort(Integer pageNumber, Integer itemsPerPage,
					   DisplayTagSortProperty sortProperty, boolean ascendingSort) {
		this.pageNumber = pageNumber;
		this.objectsPerPage = itemsPerPage;
		this.sortProperty = sortProperty;
		this.ascendingSort = ascendingSort;
	}

	public Integer getObjectsPerPage() {
		return objectsPerPage;
	}
	public Integer getPageNumber() {
		return pageNumber;
	}
	public DisplayTagSortProperty getSortProperty() {
		return sortProperty;
	}
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
	public void setSortProperty(DisplayTagSortProperty sortProperty) {
		this.sortProperty = sortProperty;
	}

	public String toString() {
		return ReflectionToStringBuilder.toString(this,
				ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
