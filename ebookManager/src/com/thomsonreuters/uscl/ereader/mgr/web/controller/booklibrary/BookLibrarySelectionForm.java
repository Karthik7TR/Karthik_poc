/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class BookLibrarySelectionForm {

	public static final String FORM_NAME = "bookLibrarySelectionForm";
	public enum Command { IMPORT, EXPORT, GENERATE, PROMOTE };
	
	public int page;
	public String sort;
	public boolean isAscending;
	private String [] selectedEbookKeys;
	private Command command;
	
	public void initialize(int page, String sortBy, boolean isAscending) {
		this.page = page;
		this.sort = sortBy;
		this.isAscending = isAscending;
	}

	public String[] getSelectedEbookKeys() {
		return selectedEbookKeys;
	}
	public void setSelectedEbookKeys(String[] selectedEbookKeys) {
		this.selectedEbookKeys = selectedEbookKeys;
	}
	
	public Command getCommand() {
		return command;
	}
	
	public void setCommand(Command cmd) {
		this.command = cmd;
	}

	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public String getSort() {
		return sort;
	}
	public void setSort(String sort) {
		this.sort = sort;
	}
	public boolean getIsAscending() {
		return isAscending;
	}
	public void setIsAscending(boolean isAscending) {
		this.isAscending = isAscending;
	}
	
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
