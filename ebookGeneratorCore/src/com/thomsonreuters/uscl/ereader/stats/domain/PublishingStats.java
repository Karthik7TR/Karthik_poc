/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.stats.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 */
@IdClass(com.thomsonreuters.uscl.ereader.stats.domain.PublishingStatsPK.class)
@Entity
@Table(schema = "EBOOK", name = "PUBLISHING_STATS")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "ebookGenerator/com/thomsonreuters/uscl/ereader/core/job/domain", name = "PublishingStats")
public class PublishingStats implements Serializable {
	private static final long serialVersionUID = 1L;


	/**
	 */
	@Column(name = "JOB_INSTANCE_ID", nullable = false)
	@Basic(fetch = FetchType.EAGER)
	@Id
	@XmlElement
	Long jobInstanceId;

	/**
	 */
	
	@Column(name = "AUDIT_ID", nullable = false)
	@Basic(fetch = FetchType.EAGER)
	@XmlElement
	Long auditId;

	/**
	 */
	@Column(name = "EBOOK_DEFINITION_ID", nullable = false)
	@Basic(fetch = FetchType.EAGER)
	@XmlElement
	Long ebookDefId;
	
	/**
	 */

	@Column(name = "JOB_SUBMITTER_NAME", length = 1024)
	@Basic(fetch = FetchType.EAGER)
	@XmlElement
	String jobSubmitterName;
	/**
	 */

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "JOB_SUBMIT_TIMESTAMP", nullable = false)
	@Basic(fetch = FetchType.EAGER)
	@XmlElement
	Date jobSubmitTimestamp;

	@Column(name = "BOOK_VERSION_SUBMITTED", length = 10)
	@Basic(fetch = FetchType.EAGER)
	@XmlElement
	String bookVersionSubmitted;
	
	@Column(name = "JOB_HOST_NAME", length = 1024)
	@Basic(fetch = FetchType.EAGER)
	@XmlElement
	String jobHostName;


	/**
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "PUBLISH_START_TIMESTAMP", nullable = false)
	@Basic(fetch = FetchType.EAGER)
	@XmlElement
	Date publishStartTimestamp;
	
	/**
	 */
	@Column(name = "GATHER_TOC_NODE_COUNT")
	@Basic(fetch = FetchType.EAGER)
	@XmlElement
	Integer gatherTocNodeCount;


	/**
	 */
	@Column(name = "GATHER_TOC_SKIPPED_COUNT")
	@Basic(fetch = FetchType.EAGER)
	@XmlElement
	Integer gatherTocSkippedCount;

	/**
	 */
	@Column(name = "GATHER_TOC_DOC_COUNT")
	@Basic(fetch = FetchType.EAGER)
	@XmlElement
	Integer gatherTocDocCount;
	/**
	 */
	@Column(name = "GATHER_TOC_RETRY_COUNT")
	@Basic(fetch = FetchType.EAGER)
	@XmlElement
	Integer gatherTocRetryCount;
	/**
	 */
	@Column(name = "GATHER_DOC_EXPECTED_COUNT")
	@Basic(fetch = FetchType.EAGER)
	@XmlElement
	Integer gatherDocExpectedCount;
	/**
	 */
	@Column(name = "GATHER_DOC_RETRY_COUNT")
	@Basic(fetch = FetchType.EAGER)
	@XmlElement
	Integer gatherDocRetryCount;
	/**
	 */
	@Column(name = "GATHER_DOC_RETRIEVED_COUNT")
	@Basic(fetch = FetchType.EAGER)
	@XmlElement
	Integer gatherDocRetrievedCount;
	/**
	 */
	@Column(name = "GATHER_META_EXPECTED_COUNT")
	@Basic(fetch = FetchType.EAGER)
	@XmlElement
	Integer gatherMetaExpectedCount;
	/**
	 */
	@Column(name = "GATHER_META_RETRY_COUNT")
	@Basic(fetch = FetchType.EAGER)
	@XmlElement
	Integer gatherMetaRetryCount;
	/**
	 */
	@Column(name = "GATHER_META_RETRIEVED_COUNT")
	@Basic(fetch = FetchType.EAGER)
	@XmlElement
	Integer gatherMetaRetrievedCount;
	/**
	 */
	@Column(name = "GATHER_IMAGE_EXPECTED_COUNT")
	@Basic(fetch = FetchType.EAGER)
	@XmlElement
	Integer gatherImageExpectedCount;
	/**
	 */
	@Column(name = "GATHER_IMAGE_RETRY_COUNT")
	@Basic(fetch = FetchType.EAGER)
	@XmlElement
	Integer gatherImageRetryCount;
	/**
	 */
	@Column(name = "GATHER_IMAGE_RETRIEVED_COUNT")
	@Basic(fetch = FetchType.EAGER)
	@XmlElement
	Integer gatherImageRetrievedCount;
	/**
	 */
	@Column(name = "FORMAT_DOC_COUNT")
	@Basic(fetch = FetchType.EAGER)
	@XmlElement
	Integer formatDocCount;
	/**
	 */
	@Column(name = "ASSEMBLE_DOC_COUNT")
	@Basic(fetch = FetchType.EAGER)
	@XmlElement
	Integer assembleDocCount;
	/**
	 */
	@Column(name = "TITLE_DOC_COUNT")
	@Basic(fetch = FetchType.EAGER)
	@XmlElement
	Integer titleDocCount;
	/**
	 */
	@Column(name = "TITLE_DUP_DOC_COUNT")
	@Basic(fetch = FetchType.EAGER)
	@XmlElement
	Integer titleDupDocCount;
	/**
	 */
	@Column(name = "PUBLISH_STATUS", length = 1024)
	@Basic(fetch = FetchType.EAGER)
	@XmlElement
	String publishStatus;
	/**
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "PUBLISH_END_TIMESTAMP", nullable = false)
	@Basic(fetch = FetchType.EAGER)
	@XmlElement
	Date publishEndTimestamp;
	/**
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LAST_UPDATED", nullable = false)
	@Basic(fetch = FetchType.EAGER)
	@XmlElement
	Date lastUpdated;



	public Date getPublishStartTimestamp() {
		return publishStartTimestamp;
	}

	public void setPublishStartTimestamp(Date publishStartTimestamp) {
		this.publishStartTimestamp = publishStartTimestamp;
	}

	public Integer getGatherTocNodeCount() {
		return gatherTocNodeCount;
	}

	public void setGatherTocNodeCount(Integer gatherTocNodeCount) {
		this.gatherTocNodeCount = gatherTocNodeCount;
	}

	public Integer getGatherTocSkippedCount() {
		return gatherTocSkippedCount;
	}

	public void setGatherTocSkippedCount(Integer gatherTocSkippedCount) {
		this.gatherTocSkippedCount = gatherTocSkippedCount;
	}

	public Integer getGatherTocDocCount() {
		return gatherTocDocCount;
	}

	public void setGatherTocDocCount(Integer gatherTocDocCount) {
		this.gatherTocDocCount = gatherTocDocCount;
	}

	public Integer getGatherTocRetryCount() {
		return gatherTocRetryCount;
	}

	public void setGatherTocRetryCount(Integer gatherTocRetryCount) {
		this.gatherTocRetryCount = gatherTocRetryCount;
	}

	public Integer getGatherDocExpectedCount() {
		return gatherDocExpectedCount;
	}

	public void setGatherDocExpectedCount(Integer gatherDocExpectedCount) {
		this.gatherDocExpectedCount = gatherDocExpectedCount;
	}

	public Integer getGatherDocRetryCount() {
		return gatherDocRetryCount;
	}

	public void setGatherDocRetryCount(Integer gatherDocRetryCount) {
		this.gatherDocRetryCount = gatherDocRetryCount;
	}

	public Integer getGatherDocRetrievedCount() {
		return gatherDocRetrievedCount;
	}

	public void setGatherDocRetrievedCount(Integer gatherDocRetrievedCount) {
		this.gatherDocRetrievedCount = gatherDocRetrievedCount;
	}

	public Integer getGatherMetaExpectedCount() {
		return gatherMetaExpectedCount;
	}

	public void setGatherMetaExpectedCount(Integer gatherMetaExpectedCount) {
		this.gatherMetaExpectedCount = gatherMetaExpectedCount;
	}

	public Integer getGatherMetaRetryCount() {
		return gatherMetaRetryCount;
	}

	public void setGatherMetaRetryCount(Integer gatherMetaRetryCount) {
		this.gatherMetaRetryCount = gatherMetaRetryCount;
	}

	public Integer getGatherMetaRetrievedCount() {
		return gatherMetaRetrievedCount;
	}

	public void setGatherMetaRetrievedCount(Integer gatherMetaRetrievedCount) {
		this.gatherMetaRetrievedCount = gatherMetaRetrievedCount;
	}

	public Integer getGatherImageExpectedCount() {
		return gatherImageExpectedCount;
	}

	public void setGatherImageExpectedCount(Integer gatherImageExpectedCount) {
		this.gatherImageExpectedCount = gatherImageExpectedCount;
	}

	public Integer getGatherImageRetryCount() {
		return gatherImageRetryCount;
	}

	public void setGatherImageRetryCount(Integer gatherImageRetryCount) {
		this.gatherImageRetryCount = gatherImageRetryCount;
	}

	public Integer getGatherImageRetrievedCount() {
		return gatherImageRetrievedCount;
	}

	public void setGatherImageRetrievedCount(Integer gatherImageRetrievedCount) {
		this.gatherImageRetrievedCount = gatherImageRetrievedCount;
	}

	public Integer getFormatDocCount() {
		return formatDocCount;
	}

	public void setFormatDocCount(Integer formatDocCount) {
		this.formatDocCount = formatDocCount;
	}

	public Integer getAssembleDocCount() {
		return assembleDocCount;
	}

	public void setAssembleDocCount(Integer assembleDocCount) {
		this.assembleDocCount = assembleDocCount;
	}

	public Integer getTitleDocCount() {
		return titleDocCount;
	}

	public void setTitleDocCount(Integer titleDocCount) {
		this.titleDocCount = titleDocCount;
	}

	public Integer getTitleDupDocCount() {
		return titleDupDocCount;
	}

	public void setTitleDupDocCount(Integer titleDupDocCount) {
		this.titleDupDocCount = titleDupDocCount;
	}

	public String getPublishStatus() {
		return publishStatus;
	}

	/** Automatically set to sysdate
	 * 	
	 * */
	public void setPublishStatus(String publishStatus) {
	 
		this.publishStatus = publishStatus;
	}

	public Date getPublishEndTimestamp() {
		return publishEndTimestamp;
	}

	public void setPublishEndTimestamp(Date publishEndTimestamp) {
		this.publishEndTimestamp = publishEndTimestamp;
	}

	/**
	 */
	public void setEbookDefId(Long ebookDefId) {
		this.ebookDefId = ebookDefId;
	}

	/**
	 */
	public Long getEbookDefId() {
		return this.ebookDefId;
	}

	/**
	 */
	public void setJobInstanceId(Long jobInstanceId) {
		this.jobInstanceId = jobInstanceId;
	}

	/**
	 */
	public Long getJobInstanceId() {
		return this.jobInstanceId;
	}

	/**
	 */
	public void setAuditId(Long auditId) {
		this.auditId = auditId;
	}

	/**
	 */
	public Long getAuditId() {
		return this.auditId;
	}

	
	/**
	 */
	public void setJobSubmitterName(String jobSubmitterName) {
		this.jobSubmitterName = jobSubmitterName;
	}

	/**
	 */
	public String getJobSubmitterName() {
		return this.jobSubmitterName;
	}

	/**
	 */
	public void setBookVersionSubmitted(String bookVersionSubmitted) {
		this.bookVersionSubmitted = bookVersionSubmitted;
	}

	/**
	 */
	public String getBookVersionSubmitted() {
		return this.bookVersionSubmitted;
	}

	/**
	 */
	public void setJobHostName(String jobHostName) {
		this.jobHostName = jobHostName;
	}

	/**
	 */
	public String getJobHostName() {
		return this.jobHostName;
	}


	/**
	 */
	public void setJobSubmitTimestamp(Date jobSubmitTimestamp) {
		this.jobSubmitTimestamp = jobSubmitTimestamp;
	}

	/**
	 */
	public Date getJobSubmitTimestamp() {
		return this.jobSubmitTimestamp;
	}

	/**  Automatically set to sysdate
	 * 	
	 */
	  public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	  }

	/**
	 */
	public Date getLastUpdated() {
		return this.lastUpdated;
	}
	/**
	 */
	
	
	/**
	 */
	public PublishingStats() {
	}

	/**
	 * Returns a textual representation of a bean.
	 * 
	 */
	public String toString() {

		StringBuilder buffer = new StringBuilder();

		buffer.append("jobInstanceId=[").append(jobInstanceId).append("] ");
		buffer.append("ebookDefId=[").append(ebookDefId).append("] ");
		buffer.append("auditId=[").append(auditId).append("] ");
		buffer.append("bookVersionSubmitted=[").append(bookVersionSubmitted).append("] ");
		buffer.append("jobHostName=[").append(jobHostName).append("] ");
		buffer.append("jobSubmitterName=[").append(jobSubmitterName).append("] ");
		buffer.append("jobSubmitTimestamp=[").append(jobSubmitTimestamp).append("] ");
		buffer.append("publishStartTimestamp=[").append(publishStartTimestamp).append("] ");
		buffer.append("gatherTocNodeCount=[").append(gatherTocNodeCount).append("] ");
		buffer.append("gatherTocSkippedCount=[").append(gatherTocSkippedCount).append("] ");
		buffer.append("gatherTocDocCount=[").append(gatherTocDocCount).append("] ");
		buffer.append("gatherTocRetryCount=[").append(gatherTocRetryCount).append("] ");
		buffer.append("gatherDocExpectedCount=[").append(gatherDocExpectedCount).append("] ");
		buffer.append("gatherDocRetrievedCount=[").append(gatherDocRetrievedCount).append("] ");
		buffer.append("gatherDocRetryCount=[").append(gatherDocRetryCount).append("] ");
		buffer.append("gatherMetaExpectedCount=[").append(gatherMetaExpectedCount).append("] ");
		buffer.append("gatherMetaRetrievedCount=[").append(gatherMetaRetrievedCount).append("] ");
		buffer.append("gatherMetaRetryCount=[").append(gatherMetaRetryCount).append("] ");
		buffer.append("gatherImageExpectedCount=[").append(gatherImageExpectedCount).append("] ");
		buffer.append("gatherImageRetrievedCount=[").append(gatherImageRetrievedCount).append("] ");
		buffer.append("gatherImageRetryCount=[").append(gatherImageRetryCount).append("] ");
		buffer.append("formatDocCount=[").append(formatDocCount).append("] ");
		buffer.append("assembleDocCount=[").append(assembleDocCount).append("] ");
		buffer.append("titleDocCount=[").append(titleDocCount).append("] ");
		buffer.append("titleDupDocCount=[").append(titleDupDocCount).append("] ");
		buffer.append("publishStatus=[").append(publishStatus).append("] ");
		buffer.append("publishEndTimestamp=[").append(publishEndTimestamp).append("] ");
		buffer.append("lastUpdated=[").append(lastUpdated).append("] ");

		return buffer.toString();
	}

	/**
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (int) (prime * result + ((jobInstanceId == null) ? 0
				: jobInstanceId.hashCode()));
		return result;
	}

	/**
	 */
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (!(obj instanceof PublishingStats))
			return false;
		PublishingStats equalCheck = (PublishingStats) obj;
		if ((jobInstanceId == null && equalCheck.jobInstanceId != null)
				|| (jobInstanceId != null && equalCheck.jobInstanceId == null))
			return false;
		if (jobInstanceId != null
				&& !jobInstanceId.equals(equalCheck.jobInstanceId))
			return false;
		return true;
	}
}
