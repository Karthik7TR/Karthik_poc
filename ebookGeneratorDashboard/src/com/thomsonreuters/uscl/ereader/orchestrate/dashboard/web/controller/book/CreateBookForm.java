package com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.controller.book;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class CreateBookForm {
	public static final String FORM_NAME = "createBookForm";
	
	private String bookCode;		// Book code/id to be generated
	private String bookVersion;
	private boolean highPriorityJob;	// if true, job request will be placed on the high priority run queue

	public String getBookCode() {
		return bookCode;
	}
	public String getBookVersion() {
		return bookVersion;
	}
	public boolean isHighPriorityJob() {
		return highPriorityJob;
	}
	public void setHighPriorityJob(boolean high) {
		this.highPriorityJob = high;
	}
	public void setBookCode(String bookCode) {
		this.bookCode = bookCode;
	}
	public void setBookVersion(String bookVersion) {
		this.bookVersion = bookVersion;
	}
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
