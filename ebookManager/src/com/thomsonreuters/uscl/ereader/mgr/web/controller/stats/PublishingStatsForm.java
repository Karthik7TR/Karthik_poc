package com.thomsonreuters.uscl.ereader.mgr.web.controller.stats;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.thomsonreuters.uscl.ereader.mgr.web.controller.PageAndSort;

public class PublishingStatsForm {
	
	public static final String FORM_NAME = "publishingStatsForm";
	public enum DisplayTagSortProperty { JOB_INSTANCE_ID, AUDIT_ID, EBOOK_DEFINITION_ID, JOB_SUBMITTER, JOB_SUBMIT_TIMESTAMP, 
		BOOK_VERSION, PUBLISH_STATUS, BOOK_SIZE, LARGEST_DOC_SIZE, LARGEST_IMAGE_SIZE, LARGEST_PDF_SIZE, PROVIEW_DISPLAY_NAME, TITLE_ID }
	
	private PageAndSort<DisplayTagSortProperty> pageAndSort = new PageAndSort<DisplayTagSortProperty>();	// sort, page, dir, objectsPerPage
	
	public PublishingStatsForm() {
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
