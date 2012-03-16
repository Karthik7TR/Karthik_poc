package com.thomsonreuters.uscl.ereader.mgr.web.controller.job.queue;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.thomsonreuters.uscl.ereader.mgr.web.controller.PageAndSort;

/**
 * Form backing object for the table of jobs that are queued to run
 * (not scheduled, which is a separate table and form).  
 */
public class QueueForm {
	
	public static final String FORM_NAME = "queueForm";
	public enum Command { TODO_AAA, TODO_BBB };
	/** Sortable columns on the Job Queue page */
	public enum DisplayTagSortProperty { SEQUENCE, BOOK_NAME, TITLE_ID, BOOK_VERSION, PRIORITY, SUBMITTED_BY, SUBMITTED_AT };
	
	private Command command;
	private Long[] 	ids;
	private PageAndSort<DisplayTagSortProperty> pageAndSort = new PageAndSort<DisplayTagSortProperty>();	// sort, page, dir, objectsPerPage

	public boolean isAscendingSort() {
		return pageAndSort.isAscendingSort();
	}
	public Command getCommand() {
		return command;
	}
	public String getDir() {
		return (pageAndSort.isAscendingSort()) ? "asc" : "desc";
	}
	public Long[] getIds() {
		return ids;
	}
	public Integer getPage() {
		return pageAndSort.getPageNumber();
	}
	public DisplayTagSortProperty getSort() {
		return getSortProperty();
	}
	public DisplayTagSortProperty getSortProperty() {
		return pageAndSort.getSortProperty();
	}
	public void setDir(String direction) {
		setAscendingSort("asc".equals(direction));
	}
	public void setAscendingSort(boolean ascending) {
		pageAndSort.setAscendingSort(ascending);
	}
	public void setIds(Long[] ids) {
		this.ids = ids;
	}
	public void setPage(Integer pageNumber) {
		pageAndSort.setPageNumber(pageNumber);
	}
	public void setSort(DisplayTagSortProperty sortProperty) {
		pageAndSort.setSortProperty(sortProperty);
	}
	public void setCommand(Command cmd) {
	 	this.command = cmd;
	}
	
	public String toString() {
		return ReflectionToStringBuilder.toString(this,
				ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
