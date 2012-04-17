package com.thomsonreuters.uscl.ereader.mgr.web.controller.bookaudit;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.thomsonreuters.uscl.ereader.mgr.web.controller.PageAndSort;

public class BookAuditForm {
	
	public static final String FORM_NAME = "ebookAuditForm";
	public enum DisplayTagSortProperty { TITLE_ID, BOOK_NAME, BOOK_DEFINITION_ID, SUBMITTED_DATE, ACTION, SUBMITTED_BY, COMMENT }
	
	private PageAndSort<DisplayTagSortProperty> pageAndSort = new PageAndSort<DisplayTagSortProperty>();	// sort, page, dir, objectsPerPage
	
	public BookAuditForm() {
		super();
	}
	public String getDir() {
		return (pageAndSort.isAscendingSort()) ? "asc" : "desc";
	}
	public Integer getPage() {
		return pageAndSort.getPageNumber();
	}
	public DisplayTagSortProperty getSort() {
		return pageAndSort.getSortProperty();
	}
	public Integer getObjectsPerPage() {
		return pageAndSort.getObjectsPerPage();
	}
	public boolean isAscendingSort() {
		return pageAndSort.isAscendingSort();
	}
	public void setDir(String direction) {
		pageAndSort.setAscendingSort("asc".equals(direction));
	}
	public void setObjectsPerPage(Integer objectsPerPage) {
		pageAndSort.setObjectsPerPage(objectsPerPage);
	}
	public void setPage(Integer pageNumber) {
		pageAndSort.setPageNumber(pageNumber);
	}
	public void setSort(DisplayTagSortProperty sortProperty) {
		pageAndSort.setSortProperty(sortProperty);
	}
	public String toString() {
		return ReflectionToStringBuilder.toString(this,
				ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
