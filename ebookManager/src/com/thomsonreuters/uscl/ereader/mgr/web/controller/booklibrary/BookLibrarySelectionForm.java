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

	private String [] selectedEbookKeys;

	public String[] getSelectedEbookKeys() {
		return selectedEbookKeys;
	}
	public void setSelectedEbookKeys(String[] selectedEbookKeys) {
		this.selectedEbookKeys = selectedEbookKeys;
	}

	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
