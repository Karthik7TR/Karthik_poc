/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.core.job.domain;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * 
 * @author U0105927
 * 
 */
@Entity
@Table(schema="EBOOK", name="JOB_REQUEST")
@NamedQueries({ @NamedQuery(name = "findAllJobRequests", query = "Select myJobRequest from JobRequest myJobRequest"),
				@NamedQuery(name = "findJobRequestBookDefinitionId", query = "select myJobRequest from JobRequest myJobRequest where myJobRequest.ebookDefinitionId = :ebookDefinitionId"),
				@NamedQuery(name = "findJobRequestByGivenCriteria", query="select myJobRequest from JobRequest myJobRequest"),
				@NamedQuery(name = "findNextJobRequestToRun", query = "select myJobRequest from JobRequest myJobRequest order by jobPriority asc,jobSubmitTimestamp asc")

				})
public class JobRequest implements Serializable{
	

	private static final long serialVersionUID = 5207493496108658705L;
	
	@Id
	@Column (name = "JOB_REQUEST_ID" ,nullable = false)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "JobRequestIdSeq")
	@SequenceGenerator (schema="EBOOK", name = "JobRequestIdSeq",sequenceName="JOB_REQUEST_ID_SEQ")
	private long jobRequestId;

	@Column (name = "EBOOK_DEFINITION_ID", nullable = false)
	private long ebookDefinitionId;

	@Column (name = "BOOK_VERISON_SUBMITTED" ,nullable = false)
	private String bookVersionSubmited;
	
	@Column (name = "JOB_STATUS", nullable = false )
	private String jobStatus;
	
	@Column (name = "JOB_PRIORITY" ,nullable = false)
	private int jobPriority;

	@Column (name = "JOB_SCHEDULE_TIMESTAMP" ,nullable = true)
	private Timestamp jobScheduleTimeStamp;
	
	@Column (name = "JOB_SUBMITTER_NAME" ,nullable = false)
	private String jobSubmittersName;
	
	@Column (name = "JOB_SUBMIT_TIMESTAMP" ,nullable = false)
	private Timestamp jobSubmitTimestamp;


	
	public enum JobStatus {
		Queue, Scheduled
	}
	
	public enum JobPriority{
		High,Normal
	}
	
	public JobRequest() {
		super();
	}

	


	public Timestamp getJobScheduleTimeStamp() {
		return jobScheduleTimeStamp;
	}


	public void setJobScheduleTimeStamp(Timestamp jobScheduleTimeStamp) {
		this.jobScheduleTimeStamp = jobScheduleTimeStamp;
	}


	public String getJobSubmittersName() {
		return jobSubmittersName;
	}


	public void setJobSubmittersName(String jobSubmittersName) {
		this.jobSubmittersName = jobSubmittersName;
	}


	public Timestamp getJobSubmitTimestamp() {
		return jobSubmitTimestamp;
	}


	public void setJobSubmitTimestamp(Timestamp jobSubmitTimestamp) {
		this.jobSubmitTimestamp = jobSubmitTimestamp;
	}


	public long getJobRequestId() {
		return jobRequestId;
	}


	public void setJobRequestId(long jobRequestId) {
		this.jobRequestId = jobRequestId;
	}


	public long getEbookDefinitionId() {
		return ebookDefinitionId;
	}


	public void setEbookDefinitionId(long ebookDefinitionId) {
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


	public void setJobStatus(String jobStatus) {
		this.jobStatus = jobStatus;
	}

	public int getJobPriority() {
		return jobPriority;
	}


	public void setJobPriority(int jobPriority) {
		this.jobPriority = jobPriority;
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
		result = prime * result + jobPriority;
		result = prime * result + (int) (jobRequestId ^ (jobRequestId >>> 32));
		result = prime
				* result
				+ ((jobScheduleTimeStamp == null) ? 0 : jobScheduleTimeStamp
						.hashCode());
		result = prime * result
				+ ((jobStatus == null) ? 0 : jobStatus.hashCode());
		result = prime
				* result
				+ ((jobSubmitTimestamp == null) ? 0 : jobSubmitTimestamp
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
		if (jobPriority != other.jobPriority)
			return false;
		if (jobRequestId != other.jobRequestId)
			return false;
		if (jobScheduleTimeStamp == null) {
			if (other.jobScheduleTimeStamp != null)
				return false;
		} else if (!jobScheduleTimeStamp.equals(other.jobScheduleTimeStamp))
			return false;
		if (jobStatus != other.jobStatus)
			return false;
		if (jobSubmitTimestamp == null) {
			if (other.jobSubmitTimestamp != null)
				return false;
		} else if (!jobSubmitTimestamp.equals(other.jobSubmitTimestamp))
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
