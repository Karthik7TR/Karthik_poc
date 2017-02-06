package com.thomsonreuters.uscl.ereader.core.job.domain;

import java.util.Date;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.joda.time.Period;
import org.springframework.batch.core.BatchStatus;

/**
 * A subset of book and job execution data used to present the Job Summary view.
 */
public class JobSummary {
	
	private Long bookDefinitionId;
	private String bookName;
	private String titleId;
	private String sourceType;
	private Long jobInstanceId;
	private Long jobExecutionId;
	private BatchStatus batchStatus;
	private String submittedBy;
	private Date startTime;
	private Date endTime;

	public JobSummary(Long bookDefinitionId, String bookName, String titleId, String sourceType, Long jobInstanceId,
					  Long jobExecutionId, BatchStatus batchStatus, String submittedBy, Date startTime, Date endTime) {
		this.bookDefinitionId = bookDefinitionId;
		this.bookName = bookName;
		this.titleId = titleId;
		this.sourceType = sourceType;
		this.jobInstanceId = jobInstanceId;
		this.jobExecutionId = jobExecutionId;
		this.batchStatus = batchStatus;
		this.submittedBy = submittedBy;
		this.startTime = startTime;
		this.endTime = endTime;
	}
	public Long getBookDefinitionId() {
		return bookDefinitionId;
	}
	public String getBookName() {
		return bookName;
	}
	public String getTitleId() {
		return titleId;
	}
	public Long getJobInstanceId() {
		return jobInstanceId;
	}
	public Long getJobExecutionId() {
		return jobExecutionId;
	}
	public BatchStatus getBatchStatus() {
		return batchStatus;
	}
	public Date getStartTime() {
		return startTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	
	public String getSourceType() {
		return sourceType;
	}
	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}
	
	public String getDuration() {
		return JobSummary.getExecutionDuration(getExecutionDuration());
	}

	private Long getExecutionDuration() {
		return getExecutionDuration(startTime, endTime);
	}
	
	public static long getExecutionDuration(Date start, Date end) {		
		long duration = 0;
		if (start != null) {
			if (end == null) {
				end = new Date();
			}
			duration = end.getTime() - start.getTime();
		}
		return duration;
	}
	
	public static String getExecutionDuration(Long durationMilliseconds) {
		if (durationMilliseconds == null) {
			return null;
		}
		long durationMs = durationMilliseconds.longValue();
		StringBuffer periodString = new StringBuffer();
		if (durationMs > -1) {
			Period period = new Period(durationMs);
			periodString.append((period.getHours() < 10) ? "0" : "");
			periodString.append(period.getHours());
			periodString.append(":");
			periodString.append((period.getMinutes() < 10) ? "0" : "");
			periodString.append(period.getMinutes());
			periodString.append(":");
			periodString.append((period.getSeconds() < 10) ? "0" : "");
			periodString.append(period.getSeconds());
			periodString.append(".");
			periodString.append((period.getMillis() < 10) ? "0" : "");
			periodString.append((period.getMillis() < 100) ? "0" : "");
			periodString.append(period.getMillis());
		}
		return periodString.toString();
	}
	public String getSubmittedBy() {
		return submittedBy;
	}
	
	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
