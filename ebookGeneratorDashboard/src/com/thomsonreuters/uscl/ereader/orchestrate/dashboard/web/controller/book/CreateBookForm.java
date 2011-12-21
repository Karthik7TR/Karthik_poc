/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.controller.book;

import java.util.StringTokenizer;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.thomsonreuters.uscl.ereader.orchestrate.core.BookDefinitionKey;

public class CreateBookForm {
	public static final String FORM_NAME = "createBookForm";
	
	private BookDefinitionKey bookDefinitionKey;	// A function of the bookKeyString in form: "<titleId>,<majorVersion>"
	private boolean highPriorityJob;	// if true, job request will be placed on the high priority run queue
	
	private String bookKeyString;	// in form <fullyQualifiedTitleId>,<majorVersion>

	public BookDefinitionKey getBookDefinitionKey() {
		return bookDefinitionKey;
	}
	public String getBookKeyString() {
		return bookKeyString;
	}
	public boolean isHighPriorityJob() {
		return highPriorityJob;
	}
	public void setHighPriorityJob(boolean high) {
		this.highPriorityJob = high;
	}
	public void setBookKeyString(String csvKeyString) {
		this.bookKeyString = csvKeyString;
		StringTokenizer tokenizer = new StringTokenizer(csvKeyString, ",");
		String fullyQualifiedTitleId = tokenizer.nextToken();
		Long majorVersion = Long.valueOf(tokenizer.nextToken());
		this.bookDefinitionKey = new BookDefinitionKey(fullyQualifiedTitleId, majorVersion);
	}
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
