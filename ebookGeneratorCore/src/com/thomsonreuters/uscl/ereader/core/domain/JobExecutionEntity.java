/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.core.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.springframework.batch.core.BatchStatus;

@Entity
@Table(name="BATCH_JOB_EXECUTION", schema="EBOOK_SPRINGBATCH")
public class JobExecutionEntity implements Serializable {

	private static final long serialVersionUID = -7060462755147125706L;
	private Long jobExecutionId;
	private Long jobInstanceId;
	private Date startTime;
	private Date endTime;
	private BatchStatus batchStatus;
//	private Set<JobParameterEntity> jobParameterEntities;
	
	// Book definition
//	private String titleId;
//	private String bookname;
	
	@Id
	@Column(name="JOB_EXECUTION_ID")
	public Long getJobExecutionId() {
		return jobExecutionId;
	}
	@Column(name="JOB_INSTANCE_ID")
	public Long getJobInstanceId() {
		return jobInstanceId;
	}
	@Column(name="START_TIME")
	public Date getStartTime() {
		return startTime;
	}
	@Column(name="END_TIME")
	public Date getEndTime() {
		return endTime;
	}
	@Column(name="STATUS")
	public BatchStatus getBatchStatus() {
		return batchStatus;
	}
//	/** Title ID from the job parameters. */
//	@Transient
//	public String getTitleId() {
//		return titleId;
//	}
//	/** Book name (title) from the job parameters. */
//	@Transient
//	public String getBookname() {
//		return bookname;
//	}
	@Transient
	public Long getExecutionDuration() {
		if (endTime == null) {
			return null;
		}
		return endTime.getTime() - startTime.getTime();
	}
//	@OneToMany(mappedBy="jobInstanceId")
//	public Set<JobParameterEntity> getJobParameterEntities() {
//		return jobParameterEntities;
//	}
	public void setJobExecutionId(Long jobExecutionId) {
		this.jobExecutionId = jobExecutionId;
	}
	public void setJobInstanceId(Long jobInstanceId) {
		this.jobInstanceId = jobInstanceId;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public void setBatchStatus(BatchStatus batchStatus) {
		this.batchStatus = batchStatus;
	}
	public void setBatchStatus(String statusString) {
		this.batchStatus = (statusString != null) ? BatchStatus.valueOf(statusString) : null;
	}
	
//	public void setTitleId(String titleId) {
//		this.titleId = titleId;
//	}
//	public void setBookname(String bookname) {
//		this.bookname = bookname;
//	}

//	public void setJobParameterEntities(Set<JobParameterEntity> entities) {
//		this.jobParameterEntities = entities;
//	}
	
	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
