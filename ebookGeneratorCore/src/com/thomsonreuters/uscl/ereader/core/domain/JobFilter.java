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
	
	private Date from;
	private Date to;
	private BatchStatus status;
	
	public Date getFrom() {
		return from;
	}
	public Date getTo() {
		return to;
	}
	public BatchStatus getStatus() {
		return status;
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
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
