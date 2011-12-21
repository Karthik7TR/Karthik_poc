/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.controller.book;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.thomsonreuters.uscl.ereader.orchestrate.core.BookDefinitionKey;

public class CreateBookForm {
	public static final String FORM_NAME = "createBookForm";
	
	private BookDefinitionKey bookDefinitionKey = new BookDefinitionKey();
	private boolean highPriorityJob;	// if true, job request will be placed on the high priority run queue

	public BookDefinitionKey getBookDefinitionKey() {
		return bookDefinitionKey;
	}
	public String getTitleId() {
		return bookDefinitionKey.getFullyQualifiedTitleId();
	}
	public boolean isHighPriorityJob() {
		return highPriorityJob;
	}
	public void setHighPriorityJob(boolean high) {
		this.highPriorityJob = high;
	}
	public void setTitleId(String titleId) {
		this.bookDefinitionKey = new BookDefinitionKey(titleId);
	}
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
