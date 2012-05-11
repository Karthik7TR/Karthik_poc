/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.thomsonreuters.uscl.ereader.mgr.web.controller.PageAndSort;

public class BookLibrarySelectionForm {

	public enum Command { IMPORT, EXPORT, GENERATE, PROMOTE }
	public enum DisplayTagSortProperty { PROVIEW_DISPLAY_NAME, TITLE_ID, LAST_GENERATED_DATE, DEFINITION_STATUS, LAST_EDIT_DATE };
	public static final String FORM_NAME = "bookLibrarySelectionForm";;
	
	private String [] selectedEbookKeys;
	private Command command;
	private PageAndSort<DisplayTagSortProperty> pageAndSort = new PageAndSort<DisplayTagSortProperty>();	// sort, page, dir, objectsPerPage
	
	public Command getCommand() {
		return command;
	}
	public String getDir() {
		return (pageAndSort.isAscendingSort()) ? "asc" : "desc";
	}
	public Integer getObjectsPerPage() {
		return pageAndSort.getObjectsPerPage();
	}
	public Integer getPage() {
		return pageAndSort.getPageNumber();
	}
	public String[] getSelectedEbookKeys() {
		return selectedEbookKeys;
	}

	public DisplayTagSortProperty getSort() {
		return pageAndSort.getSortProperty();
	}
	public boolean isAscendingSort() {
		return pageAndSort.isAscendingSort();
	}
	
	public void setCommand(Command cmd) {
		this.command = cmd;
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
	public void setSelectedEbookKeys(String[] selectedEbookKeys) {
		this.selectedEbookKeys = selectedEbookKeys;
	}
	
	public void setSort(DisplayTagSortProperty sortProperty) {
		pageAndSort.setSortProperty(sortProperty);
	}

	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
