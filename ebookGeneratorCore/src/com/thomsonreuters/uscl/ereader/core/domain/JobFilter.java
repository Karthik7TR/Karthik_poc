/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.core.domain;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.springframework.batch.core.BatchStatus;

/**
 * The filter criteria used when searching for jobs to display in the job summary list.
 * A null property value indicates that it will be ignored.
 */
public class JobFilter {
	
	// Job Execution properties
	private Date from;
	private Date to;
	private BatchStatus batchStatus;
	
	// Job Parameters
	private String titleId;
	private String bookName;
	
	public JobFilter() {
		super();
	}
	public JobFilter(Date from, Date to, BatchStatus batchStatus, String titleId, String bookName) {
		this.from = from;
		this.to = to;
		this.batchStatus = batchStatus;
		this.titleId = titleId;
		this.bookName = bookName;
	}
	
	/**
	 * Returns true if a filter to be applied is against a Job Parameter.
	 * Needed because we need to join on the JOB_PARAMS table if there is any job parameter
	 * being filtered on and the DAO needs to know if it should add the join clause to the query.
	 */
	public boolean hasAnyJobParameters() {
		return (StringUtils.isNotBlank(titleId)) || (StringUtils.isNotBlank(bookName));
	}
	
	public Date getFrom() {
		return from;
	}
	public Date getTo() {
		return to;
	}
	public BatchStatus getBatchStatus() {
		return batchStatus;
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
	public void setBatchStatus(BatchStatus status) {
		this.batchStatus = status;
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
