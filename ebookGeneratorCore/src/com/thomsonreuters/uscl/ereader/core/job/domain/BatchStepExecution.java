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
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * @author <a href="mailto:Mahendra.Survase@thomsonreuters.com">Mahendra Survase</a> u0105927
 */
@Entity
@Table(name = "BATCH_STEP_EXECUTION")
public class BatchStepExecution implements Serializable {


	private static final long serialVersionUID = 5207493496108658705L;
	
	private Long stepExecutionId;
	private Long version;
	private String stepName;
	private Long jobExecutionId;
	private Date startTime;
	private Date endTime;
	private String status;
	private Long commitCount;
	private Long readCount;
	private Long filterCount;
	private Long writeCount;
	private Long readSkipCount;
	private Long writeSkipCount;
	private Long processSkipCount;
	private Long rollbackCount;
	private String exitCode;
	private String exitMessage;
	private Date lastUpdated;

	public BatchStepExecution() {
		super();
	}


	@Id
	@Column(name = "STEP_EXECUTION_ID", nullable=false)
	public Long getStepExecutionId() {
		return stepExecutionId;
	}

	public void setStepExecutionId(Long stepExecutionId) {
		this.stepExecutionId = stepExecutionId;
	}
	
	@Column(name ="VERSION", nullable=false)
	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}
	@Column(name = "STEP_NAME",nullable=false)
	public String getStepName() {
		return stepName;
	}

	public void setStepName(String stepName) {
		this.stepName = stepName;
	}
	
	@Column(name = "JOB_EXECUTION_ID", nullable=false)
	public Long getJobExecutionId() {
		return jobExecutionId;
	}
	
	public void setJobExecutionId(Long jobExecutionId) {
		this.jobExecutionId = jobExecutionId;
	}

	@Column(name = "START_TIME", nullable=false)
	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	
	@Column(name = "END_TIME", nullable=true)
	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	
	@Column(name = "STATUS", nullable=true)	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Column(name = "COMMIT_COUNT", nullable=true)	
	public Long getCommitCount() {
		return commitCount;
	}

	public void setCommitCount(Long commitCount) {
		this.commitCount = commitCount;
	}

	@Column(name = "READ_COUNT", nullable=true)	
	public Long getReadCount() {
		return readCount;
	}

	public void setReadCount(Long readCount) {
		this.readCount = readCount;
	}

	@Column(name = "FILTER_COUNT", nullable=true)	
	public Long getFilterCount() {
		return filterCount;
	}

	public void setFilterCount(Long filterCount) {
		this.filterCount = filterCount;
	}

	@Column(name = "WRITE_COUNT", nullable=true)	
	public Long getWriteCount() {
		return writeCount;
	}

	public void setWriteCount(Long writeCount) {
		this.writeCount = writeCount;
	}

	@Column(name = "READ_SKIP_COUNT", nullable=true)	
	public Long getReadSkipCount() {
		return readSkipCount;
	}

	public void setReadSkipCount(Long readSkipCount) {
		this.readSkipCount = readSkipCount;
	}

	@Column(name = "WRITE_SKIP_COUNT", nullable=true)	
	public Long getWriteSkipCount() {
		return writeSkipCount;
	}

	public void setWriteSkipCount(Long writeSkipCount) {
		this.writeSkipCount = writeSkipCount;
	}

	@Column(name = "PROCESS_SKIP_COUNT", nullable=true)	
	public Long getProcessSkipCount() {
		return processSkipCount;
	}

	public void setProcessSkipCount(Long processSkipCount) {
		this.processSkipCount = processSkipCount;
	}
	
	@Column(name = "ROLLBACK_COUNT", nullable=true)	
	public Long getRollbackCount() {
		return rollbackCount;
	}

	public void setRollbackCount(Long rollbackCount) {
		this.rollbackCount = rollbackCount;
	}

	@Column(name = "EXIT_CODE", nullable=true)	
	public String getExitCode() {
		return exitCode;
	}

	public void setExitCode(String exitCode) {
		this.exitCode = exitCode;
	}

	@Column(name = "EXIT_MESSAGE", nullable=true)	
	public String getExitMessage() {
		return exitMessage;
	}

	public void setExitMessage(String exitMessage) {
		this.exitMessage = exitMessage;
	}

	@Column(name = "LAST_UPDATED", nullable=true)	
	public Date getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this,
				ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
