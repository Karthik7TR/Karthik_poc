package com.thomsonreuters.uscl.ereader.mgr.web.controller.job.list;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Maps the query string parameters sent by DisplayTag paging and sorting into properites.
 */
public class PageAndSort {
	public static final String FORM_NAME = "pageAndSort";
	public enum DisplayTagSortProperty { TITLE_ID, BOOK_NAME, START_TIME, BATCH_STATUS, JOB_INSTANCE_ID };
	
	// Paging and sorting query string parameters sent by DisplayTag
	private int pageNumber;	// page number user wants to see (integer)
	private DisplayTagSortProperty sortProperty;	// one of SortProperty enum values
	private String sortDirection;		// "asc" for a ascending sort direction
	
	public int getPage() {
		return pageNumber;
	}
	public DisplayTagSortProperty getSort() {
		return sortProperty;
	}
	public String getDir() {
		return sortDirection;
	}
	public boolean isAscendingSort() {
		return "asc".equals(sortDirection);
	}
	public void setPage(int page) {
		this.pageNumber = page;
	}
	public void setSort(DisplayTagSortProperty sortProperty) {
		this.sortProperty = sortProperty;
	}
	public void setDir(String direction) {
		this.sortDirection = direction;
	}

	public String toString() {
		return ReflectionToStringBuilder.toString(this,
				ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
