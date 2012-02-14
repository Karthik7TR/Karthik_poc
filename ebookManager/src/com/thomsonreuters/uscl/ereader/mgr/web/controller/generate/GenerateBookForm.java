/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.generate;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.thomsonreuters.uscl.ereader.orchestrate.core.BookDefinitionKey;

public class GenerateBookForm {
	public static final String FORM_NAME = "generateBookForm";
	
	private BookDefinitionKey bookDefinitionKey = new BookDefinitionKey();
	private boolean highPriorityJob;
	private boolean majorVersion;
	private boolean regenerate;

	public boolean isRegenerate() {
		return regenerate;
	}
	public void setRegenerate(boolean regenerate) {
		this.regenerate = regenerate;
	}
	public boolean isMajorVersion() {
		return majorVersion;
	}
	public void setMajorVersion(boolean majorVersion) {
		this.majorVersion = majorVersion;
	}
	public BookDefinitionKey getBookDefinitionKey() {
		return bookDefinitionKey;
	}
	public String getFullyQualifiedTitleId() {
		return bookDefinitionKey.getFullyQualifiedTitleId();
	}
	public boolean isHighPriorityJob() {
		return highPriorityJob;
	}
	public void setHighPriorityJob(boolean high) {
		this.highPriorityJob = high;
	}
	public void setFullyQualifiedTitleId(String fullTitleId) {
		this.bookDefinitionKey = new BookDefinitionKey(fullTitleId);
	}
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
