/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.core.job.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

@Entity
@Table(schema = "EBOOK", name = "JOB_REQUEST")
@NamedQueries({
		@NamedQuery(name = "findJobRequestBookDefinitionId", query = "select myJobRequest from JobRequest myJobRequest where myJobRequest.ebookDefinitionId = :ebookDefinitionId"),
		@NamedQuery(name = "findJobRequestByGivenCriteria", query = "select myJobRequest from JobRequest myJobRequest") })
//		@NamedQuery(name = "findNextJobRequestToRun", query = "select myJobRequest from JobRequest myJobRequest order by jobSubmitTime asc, priority desc") })
public class JobRequest implements Serializable {

	@Deprecated
	public enum JobStatus {
		QUEUED, SCHEDULED
	}

	private static final long serialVersionUID = 5207493496108658705L;;

	public static JobRequest createQueuedJobRequest(long ebookDefinitionId,
			String version, int priority, String submittedBy) {
		return new JobRequest(null, version, ebookDefinitionId, priority, null,
				submittedBy, null);
	}

	private Long jobRequestId;
	private Long ebookDefinitionId;
	private String bookVersion;
	@Deprecated
	private String jobStatus;// Unneeded, to be removed
	private int priority;
	private Date scheduledAt;
	private String submittedBy;
	private Date submittedAt;

	public JobRequest() {
		super();
	}

	private JobRequest(Long jobRequestId, String version,
			long ebookDefinitionId, int priority, Date scheduledAt,
			String submittedBy, Date submitDate) {
		setPrimaryKey(jobRequestId);
		setBookVersion(version);
		setEbookDefinitionId(ebookDefinitionId);
		setPriority(priority);
		setScheduledAt(scheduledAt);
		setSubmittedBy(submittedBy);
		setSubmittedAt(submitDate);
		setJobStatus(JobStatus.QUEUED.toString()); // OBSOLETE, to be deleted since unneeded
	}

	@Column(name = "BOOK_VERISON_SUBMITTED", nullable = false)
	public String getBookVersion() {
		return bookVersion;
	}
	@Column(name = "EBOOK_DEFINITION_ID", nullable = false)
	public Long getEbookDefinitionId() {
		return ebookDefinitionId;
	}
	@Column(name = "JOB_SCHEDULE_TIMESTAMP", nullable = true)
	public Date getScheduledAt() {
		return scheduledAt;
	}
	@Deprecated
	@Column(name = "JOB_STATUS", nullable = false)
	public String getJobStatus() {
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
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "JobRequestIdSeq")
	@SequenceGenerator(schema = "EBOOK", name = "JobRequestIdSeq", sequenceName = "JOB_REQUEST_ID_SEQ")
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

	public void setEbookDefinitionId(Long ebookDefinitionId) {
		this.ebookDefinitionId = ebookDefinitionId;
	}

	public void setScheduledAt(Date jobScheduleTime) {
		this.scheduledAt = jobScheduleTime;
	}
	@Deprecated
	public void setJobStatus(String status) {
		this.jobStatus = status;
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
