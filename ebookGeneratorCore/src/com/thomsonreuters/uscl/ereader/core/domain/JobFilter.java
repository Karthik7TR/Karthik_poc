package com.thomsonreuters.uscl.ereader.core.domain;

import java.util.Date;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.springframework.batch.core.BatchStatus;

/**
 * The filter criteria used when searching for jobs to display in the job summary list.
 * A null property value indicates that it will be ignored.
 */
public class JobFilter {
	
	// Execution stats
	private Date from;
	private Date to;
	private BatchStatus status;
	
	// Job Parameters
	private String titleId;
	private String bookName;
	
	public boolean hasAnyJobParameters() {
		return (titleId != null) || (bookName != null);
	}
	
	public Date getFrom() {
		return from;
	}
	public Date getTo() {
		return to;
	}
	public BatchStatus getStatus() {
		return status;
	}	
	public String getTitleId() {
		return titleId;
	}
	public String getBookName() {
		return bookName;
	}
	public void setFrom(Date from) {
		this.from = from;
	}
	public void setTo(Date to) {
		this.to = to;
	}
	public void setStatus(BatchStatus status) {
		this.status = status;
	}
	public void setTitleId(String titleId) {
		this.titleId = titleId;
	}
	public void setBookName(String bookName) {
		this.bookName = bookName;
	}
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
