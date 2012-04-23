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
import javax.persistence.GenerationType;
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

/**
 * @author <a href="mailto:Mahendra.Survase@thomsonreuters.com">Mahendra Survase</a> u0105927
 */
@Entity
@Table(name = "BATCH_JOB_EXECUTION")
public class BatchJobExecution implements Serializable {


	private static final long serialVersionUID = 1572082615837622969L;
	
	private Long jobExecutionId;
	private String version;
	private Long jobInstanceId;
	private Date createTime;
	private Date startTime;
	private Date endtime;
	private String status;
	private String exitCode;
	private String exitMessage;
	private Date lastUpdated;
	
	
	public BatchJobExecution() {
		super();
	}
	
	@Id
	@Column(name = "JOB_EXECUTION_ID", nullable=false)
	public Long getJobExecutionId() {
		return jobExecutionId;
	}


	@Column(name = "VERSION", nullable=true)
	public String getVersion() {
		return version;
	}


	@Column(name = "JOB_INSTANCE_ID", nullable=false)
	public Long getJobInstanceId() {
		return jobInstanceId;
	}


	@Column(name = "CREATE_TIME", nullable =false)
	public Date getCreateTime() {
		return createTime;
	}


	@Column(name = "START_TIME",nullable = true)
	public Date getStartTime() {
		return startTime;
	}


	@Column(name = "END_TIME",nullable = true)
	public Date getEndtime() {
		return endtime;
	}


	@Column(name = "STATUS",nullable= true)
	public String getStatus() {
		return status;
	}


	@Column(name = "EXITCODE",nullable=true)
	public String getExitCode() {
		return exitCode;
	}


	@Column(name="EXIT_MESSAGE",nullable=true)
	public String getExitMessage() {
		return exitMessage;
	}


	@Column(name="LAST_UPDATED",nullable=true)
	public Date getLastUpdated() {
		return lastUpdated;
	}
	
	
	public void setJobExecutionId(Long jobExecutionId) {
		this.jobExecutionId = jobExecutionId;
	}



	public void setVersion(String version) {
		this.version = version;
	}



	public void setJobInstanceId(Long jobInstanceId) {
		this.jobInstanceId = jobInstanceId;
	}



	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}



	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}



	public void setEndtime(Date endtime) {
		this.endtime = endtime;
	}



	public void setStatus(String status) {
		this.status = status;
	}



	public void setExitCode(String exitCode) {
		this.exitCode = exitCode;
	}



	public void setExitMessage(String exitMessage) {
		this.exitMessage = exitMessage;
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
