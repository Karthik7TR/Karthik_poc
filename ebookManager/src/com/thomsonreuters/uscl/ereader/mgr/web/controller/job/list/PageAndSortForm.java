package com.thomsonreuters.uscl.ereader.mgr.web.controller.job.list;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Maps the query string parameters sent by DisplayTag paging and sorting into properites.
 */

public class PageAndSortForm {
	public static final String FORM_NAME = "pageAndSortForm";
	public static final int DEFAULT_ITEMS_PER_PAGE = 20;
	
	public enum DisplayTagSortProperty { TITLE_ID, BOOK_NAME, START_TIME, BATCH_STATUS, JOB_INSTANCE_ID }

	// Paging and sorting query string parameters sent by DisplayTag
	private Integer pageNumber;	// page number user wants to see (integer)
	private Integer itemsPerPage;
	private DisplayTagSortProperty sortProperty;
	private boolean ascendingSort;		// "asc" for a ascending sort direction

	public PageAndSortForm() {
		super();
	}

	public PageAndSortForm(Integer pageNumber, Integer itemsPerPage,
						   DisplayTagSortProperty sortProperty, boolean ascendingSort) {
		this.pageNumber = pageNumber;
		this.itemsPerPage = itemsPerPage;
		this.sortProperty = sortProperty;
		this.ascendingSort = ascendingSort;
	}
	
	public void copy(PageAndSortForm copy) {
		setPage(copy.getPage());
		setItemsPerPage(copy.getItemsPerPage());
		setSort(copy.getSort());
		setAscendingSort(copy.isAscendingSort());
	}
	
	public static PageAndSortForm createDefault() {
		return new PageAndSortForm(1, DEFAULT_ITEMS_PER_PAGE, DisplayTagSortProperty.START_TIME, false);
	}
	
	public String getDir() {
		return (ascendingSort) ? "asc" : "desc";
	}
	public Integer getItemsPerPage() {
		return itemsPerPage;
	}
	public Integer getPage() {
		return pageNumber;
	}
	public DisplayTagSortProperty getSort() {
		return sortProperty;
	}
	public boolean isAscendingSort() {
		return ascendingSort;
	}
	public boolean isSortingOperation() {
		return (sortProperty != null);
	}
	public void setAscendingSort(boolean tf) {
		this.ascendingSort = tf;
	}
	public void setDir(String direction) {
		this.ascendingSort = "asc".equals(direction);
	}
	public void setItemsPerPage(Integer itemsPerPage) {
		this.itemsPerPage = itemsPerPage;
	}
	public void setPage(Integer page) {
		this.pageNumber = page;
	}
	public void setSort(DisplayTagSortProperty sortProperty) {
		this.sortProperty = sortProperty;
	}

	public String toString() {
		return ReflectionToStringBuilder.toString(this,
				ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
