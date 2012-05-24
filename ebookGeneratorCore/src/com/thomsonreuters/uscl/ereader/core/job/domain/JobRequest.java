/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.core.job.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;

@Entity
@Table(name = "JOB_REQUEST_DONG")
public class JobRequest implements Serializable {

	@Deprecated
	// Currently supporting only QUEUED jobs
	public enum JobStatus {
		QUEUED, SCHEDULED
	}

	private static final long serialVersionUID = 5207493496108658705L;
	public static final String JOB_NAME_CREATE_EBOOK = "ebookGeneratorJob";

	private Long jobRequestId;
	private BookDefinition bookDefinition;
	private String bookVersion;
	private int priority;
	private Date scheduledAt;
	private String submittedBy;
	private Date submittedAt;
	@Deprecated
	private JobStatus jobStatus = JobStatus.QUEUED;

	public JobRequest() {
		super();
	}

	private JobRequest(Long jobRequestId, String version,
			BookDefinition bookDefinition, int priority, Date scheduledAt,
			String submittedBy, Date submitDate) {
		setPrimaryKey(jobRequestId);
		setBookVersion(version);
		setBookDefinition(bookDefinition);
		setPriority(priority);
		setScheduledAt(scheduledAt);
		setSubmittedBy(submittedBy);
		setSubmittedAt(submitDate);
	}

	public static JobRequest createQueuedJobRequest(
			BookDefinition bookDefinition, String version, int priority,
			String submittedBy) {
		return new JobRequest(null, version, bookDefinition, priority, null,
				submittedBy, null);
	}

	@Column(name = "BOOK_VERISON_SUBMITTED", nullable = false)
	public String getBookVersion() {
		return bookVersion;
	}

	@OneToOne
	@Fetch(FetchMode.SELECT)
	@JoinColumn(name = "EBOOK_DEFINITION_ID")
	public BookDefinition getBookDefinition() {
		return bookDefinition;
	}

	@Column(name = "JOB_SCHEDULE_TIMESTAMP", nullable = true)
	public Date getScheduledAt() {
		return scheduledAt;
	}

	@Deprecated
	@Column(name = "JOB_STATUS", nullable = false)
	public String getJobStatusString() {
		return jobStatus.toString();
	}

	@Deprecated
	@Transient
	public JobStatus getJobStatus() {
		return jobStatus;
	}

	@Column(name = "JOB_SUBMITTER_NAME", nullable = false)
	public String getSubmittedBy() {
		return submittedBy;
	}

	@Column(name = "JOB_SUBMIT_TIMESTAMP", nullable = false)
	public Date getSubmittedAt() {
		return submittedAt;
	}

	@Id
	@Column(name = "JOB_REQUEST_ID", nullable = false)
	@GeneratedValue(generator = "JobRequestIdSeq")
	@SequenceGenerator(name = "JobRequestIdSeq", sequenceName = "JOB_REQUEST_ID_SEQ")
	public Long getPrimaryKey() {
		return jobRequestId;
	}

	@Column(name = "JOB_PRIORITY", nullable = false)
	public int getPriority() {
		return priority;
	}

	@Transient
	public boolean isQueuedRequest() {
		return (scheduledAt == null);
	}

	@Transient
	public boolean isScheduledRequest() {
		return !isQueuedRequest();
	}

	public void setBookVersion(String bookVersionSubmited) {
		this.bookVersion = bookVersionSubmited;
	}

	public void setBookDefinition(BookDefinition definition) {
		this.bookDefinition = definition;
	}

	public void setScheduledAt(Date jobScheduleTime) {
		this.scheduledAt = jobScheduleTime;
	}

	@Deprecated
	public void setJobStatus(JobStatus status) {
		this.jobStatus = status;
	}

	@Deprecated
	public void setJobStatusString(String status) {
		setJobStatus(JobStatus.valueOf(status));
	}

	public void setSubmittedBy(String jobSubmittersName) {
		this.submittedBy = jobSubmittersName;
	}

	public void setSubmittedAt(Date submitTime) {
		this.submittedAt = submitTime;
	}

	public void setPrimaryKey(Long jobRequestId) {
		this.jobRequestId = jobRequestId;
	}

	public void setPriority(int jobPriority) {
		this.priority = jobPriority;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this,
				ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
