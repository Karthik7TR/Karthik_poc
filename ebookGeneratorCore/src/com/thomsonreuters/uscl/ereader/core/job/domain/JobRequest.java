/*
 * Copyright 2017: Thomson Reuters Global Resources. All Rights Reserved.
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

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.jetbrains.annotations.NotNull;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;

@Entity
@Table(name = "JOB_REQUEST")
public class JobRequest implements Serializable {

	private static final long serialVersionUID = 5207493496108658705L;

	@Id
	@Column(name = "JOB_REQUEST_ID", nullable = false)
	@GeneratedValue(generator = "JobRequestIdSeq")
	@SequenceGenerator(name = "JobRequestIdSeq", sequenceName = "JOB_REQUEST_ID_SEQ")
	private Long jobRequestId;

	@OneToOne
	@Fetch(FetchMode.SELECT)
	@JoinColumn(name = "EBOOK_DEFINITION_ID")
	private BookDefinition bookDefinition;

	@Column(name = "BOOK_VERISON_SUBMITTED", nullable = false)
	private String bookVersion;

	@Column(name = "JOB_PRIORITY", nullable = false)
	private int priority;

	@Column(name = "JOB_SUBMITTER_NAME", nullable = false)
	private String submittedBy;

	@Column(name = "JOB_SUBMIT_TIMESTAMP", nullable = false)
	private Date submittedAt;

	public static JobRequest createQueuedJobRequest(@NotNull BookDefinition bookDefinition, @NotNull String version,
			int priority, @NotNull String submittedBy) {
		JobRequest jobRequest = new JobRequest();
		jobRequest.setBookDefinition(bookDefinition);
		jobRequest.setBookVersion(version);
		jobRequest.setPriority(priority);
		jobRequest.setSubmittedBy(submittedBy);
		return jobRequest;
	}

	public Long getJobRequestId() {
		return jobRequestId;
	}

	public void setJobRequestId(Long jobRequestId) {
		this.jobRequestId = jobRequestId;
	}

	public BookDefinition getBookDefinition() {
		return bookDefinition;
	}

	public void setBookDefinition(BookDefinition definition) {
		this.bookDefinition = definition;
	}

	public String getBookVersion() {
		return bookVersion;
	}

	public void setBookVersion(String bookVersionSubmited) {
		this.bookVersion = bookVersionSubmited;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int jobPriority) {
		this.priority = jobPriority;
	}

	public String getSubmittedBy() {
		return submittedBy;
	}

	public void setSubmittedBy(String jobSubmittersName) {
		this.submittedBy = jobSubmittersName;
	}

	public Date getSubmittedAt() {
		return submittedAt;
	}

	public void setSubmittedAt(Date submitTime) {
		this.submittedAt = submitTime;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
