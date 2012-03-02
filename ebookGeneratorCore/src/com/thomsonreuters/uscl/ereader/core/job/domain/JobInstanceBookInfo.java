package com.thomsonreuters.uscl.ereader.core.job.domain;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Properties of the book that are needed for presentation when presenting job instance and job execution data.
 * These are derived from the publishing history of the job.
 */
public class JobInstanceBookInfo {

	private String bookName;
	private String titleId;
	
	public JobInstanceBookInfo(String bookName, String titleId) {
		this.bookName = bookName;
		this.titleId = titleId;
	}
	public String getBookName() {
		return bookName;
	}
	public String getTitleId() {
		return titleId;
	}
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
