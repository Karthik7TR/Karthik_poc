package com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.controller.book;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class CreateBookForm {
	public static final String FORM_NAME = "createBookForm";
	
	private String bookCode;		// Book code/id to be generated
	private int threadPriority;	// 1..10, 1=MIN, 5=NORMAL, 10=MAX
	private boolean highPriorityJob;	// if true, job request will be placed on the high priority run queue

	public String getBookCode() {
		return bookCode;
	}
	public int getThreadPriority() {
		return threadPriority;
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
	public void setThreadPriority(int threadPriority) {
		this.threadPriority = threadPriority;
	}
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
