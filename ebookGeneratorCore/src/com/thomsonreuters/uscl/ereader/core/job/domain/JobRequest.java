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

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

@Entity
@Table(schema="EBOOK", name="JOB_REQUEST")
@NamedQueries({
//	@NamedQuery(name = "findAllJobRequests", query = "Select myJobRequest from JobRequest myJobRequest"),
	
				@NamedQuery(name = "findJobRequestBookDefinitionId", query = "select myJobRequest from JobRequest myJobRequest where myJobRequest.ebookDefinitionId = :ebookDefinitionId"),
				@NamedQuery(name = "findJobRequestByGivenCriteria", query="select myJobRequest from JobRequest myJobRequest"),
				@NamedQuery(name = "findNextJobRequestToRun", query = "select myJobRequest from JobRequest myJobRequest order by jobSubmitTime asc, priority desc")
				})

public class JobRequest implements Serializable{
	
	private static final long serialVersionUID = 5207493496108658705L;
	
	public enum JobStatus { QUEUED, SCHEDULED };
	
	@Id
	@Column (name = "JOB_REQUEST_ID" ,nullable = false)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "JobRequestIdSeq")
	@SequenceGenerator (schema="EBOOK", name = "JobRequestIdSeq",sequenceName="JOB_REQUEST_ID_SEQ")
	private Long jobRequestId;

	@Column (name = "EBOOK_DEFINITION_ID", nullable = false)
	private Long ebookDefinitionId;

	@Column (name = "BOOK_VERISON_SUBMITTED" ,nullable = false)
	private String bookVersionSubmited;
	
	@Deprecated  // Unneeded, to be removed
	@Column (name = "JOB_STATUS", nullable = false )
	private String jobStatus;
	
	@Column (name = "JOB_PRIORITY" ,nullable = false)
	private int priority;

	@Column (name = "JOB_SCHEDULE_TIMESTAMP" ,nullable = true)
	private Date jobScheduleTime;
	
	@Column (name = "JOB_SUBMITTER_NAME" ,nullable = false)
	private String jobSubmittersName;
	
	@Column (name = "JOB_SUBMIT_TIMESTAMP" ,nullable = false)
	private Date jobSubmitTime;

	public static JobRequest createQueuedJobRequest(long ebookDefinitionId, String version,
													int priority, String submittedBy) {
		return new JobRequest(null, version, ebookDefinitionId, priority, null, submittedBy, null);
	}
	
	public JobRequest() {
		super();
	}
	
	private JobRequest(Long jobRequestId, String version, long ebookDefinitionId, int priority,
					  Date scheduledAt, String submittedBy, Date submitDate) {
		setPrimaryKey(jobRequestId);
		setBookVersionSubmited(version);
		setEbookDefinitionId(ebookDefinitionId);
		setPriority(priority);
		setJobScheduleTime(scheduledAt);
		setJobSubmittersName(submittedBy);
		setJobSubmitTime(submitDate);
		setJobStatus(JobStatus.QUEUED.toString()); // OBSOLETE, to be deleted as unneeded
	}
	
	public boolean isQueuedRequest() {
		return (jobScheduleTime == null);
	}
	public boolean isScheduledRequest() {
		return !isQueuedRequest();
	}

	public Date getJobScheduleTime() {
		return jobScheduleTime;
	}

	public void setJobScheduleTime(Date jobScheduleTime) {
		this.jobScheduleTime = jobScheduleTime;
	}

	public String getJobSubmittersName() {
		return jobSubmittersName;
	}

	public void setJobSubmittersName(String jobSubmittersName) {
		this.jobSubmittersName = jobSubmittersName;
	}

	public Date getJobSubmitTime() {
		return jobSubmitTime;
	}

	public void setJobSubmitTime(Date submitTime) {
		this.jobSubmitTime = submitTime;
	}

	public Long getPrimaryKey() {
		return jobRequestId;
	}

	public void setPrimaryKey(Long jobRequestId) {
		this.jobRequestId = jobRequestId;
	}

	public Long getEbookDefinitionId() {
		return ebookDefinitionId;
	}

	public void setEbookDefinitionId(Long ebookDefinitionId) {
		this.ebookDefinitionId = ebookDefinitionId;
	}

	public String getBookVersionSubmited() {
		return bookVersionSubmited;
	}

	public void setBookVersionSubmited(String bookVersionSubmited) {
		this.bookVersionSubmited = bookVersionSubmited;
	}
	
	
	public String getJobStatus() {
		return jobStatus;
	}
	public void setJobStatus(String status) {
		this.jobStatus = status;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int jobPriority) {
		this.priority = jobPriority;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((bookVersionSubmited == null) ? 0 : bookVersionSubmited
						.hashCode());
		result = prime * result
				+ (int) (ebookDefinitionId ^ (ebookDefinitionId >>> 32));
		result = prime * result + priority;
		result = prime * result + (int) (jobRequestId ^ (jobRequestId >>> 32));
		result = prime
				* result
				+ ((jobScheduleTime == null) ? 0 : jobScheduleTime
						.hashCode());
		result = prime
				* result
				+ ((jobSubmitTime == null) ? 0 : jobSubmitTime
						.hashCode());
		result = prime
				* result
				+ ((jobSubmittersName == null) ? 0 : jobSubmittersName.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JobRequest other = (JobRequest) obj;
		if (bookVersionSubmited == null) {
			if (other.bookVersionSubmited != null)
				return false;
		} else if (!bookVersionSubmited.equals(other.bookVersionSubmited))
			return false;
		if (ebookDefinitionId != other.ebookDefinitionId)
			return false;
		if (priority != other.priority)
			return false;
		if (jobRequestId != other.jobRequestId)
			return false;
		if (jobScheduleTime == null) {
			if (other.jobScheduleTime != null)
				return false;
		} else if (!jobScheduleTime.equals(other.jobScheduleTime))
			return false;
		if (jobSubmitTime == null) {
			if (other.jobSubmitTime != null)
				return false;
		} else if (!jobSubmitTime.equals(other.jobSubmitTime))
			return false;
		if (jobSubmittersName == null) {
			if (other.jobSubmittersName != null)
				return false;
		} else if (!jobSubmittersName.equals(other.jobSubmittersName))
			return false;
		return true;
	}

	public String toString(){
		return ReflectionToStringBuilder.toString(this,ToStringStyle.SHORT_PREFIX_STYLE);
	}
}

//class RunOrderComparator implements Comparator<JobRequest> {
//	 public  int compare(JobRequest paramT1, JobRequest paramT2);
//		
//	}
//}
