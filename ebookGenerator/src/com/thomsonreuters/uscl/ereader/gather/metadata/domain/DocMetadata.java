/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.gather.metadata.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 */
@IdClass(com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadataPK.class)
@Entity
@NamedQueries({ @NamedQuery(name = "findDocMetadataMapByDocUuid", query = "select myDocMetadata.docFamilyUuid from DocMetadata myDocMetadata where myDocMetadata.docUuid = :doc_uuid") })
@Table(schema = "EBOOK_AUTHORITY", name = "DOCUMENT_METADATA")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "ebookGenerator/com/thomsonreuters/uscl/ereader/gather/metadata/domain", name = "DocMetadata")
public class DocMetadata implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 */

	@Column(name = "TITLE_ID", length = 64, nullable = false)
	@Basic(fetch = FetchType.EAGER)
	@Id
	@XmlElement
	String titleId;
	/**
	 */

	@Column(name = "JOB_INSTANCE_ID", nullable = false)
	@Basic(fetch = FetchType.EAGER)
	@Id
	@XmlElement
	Integer jobInstanceId;
	/**
	 */

	@Column(name = "DOC_UUID", length = 36, nullable = false)
	@Basic(fetch = FetchType.EAGER)
	@Id
	@XmlElement
	String docUuid;
	/**
	 */

	@Column(name = "DOC_FAMILY_UUID", length = 36, nullable = false)
	@Basic(fetch = FetchType.EAGER)
	@XmlElement
	String docFamilyUuid;
	/**
	 */

	@Column(name = "DOC_TYPE", length = 10)
	@Basic(fetch = FetchType.EAGER)
	@XmlElement
	String docType;
	/**
	 */

	@Column(name = "NORMALIZED_FIRSTLINE_CITE", length = 100)
	@Basic(fetch = FetchType.EAGER)
	@XmlElement
	String normalizedFirstlineCite;
	/**
	 */

	@Column(name = "FIND_ORIG", length = 80)
	@Basic(fetch = FetchType.EAGER)
	@XmlElement
	String findOrig;
	/**
	 */

	@Column(name = "SERIAL_NUMBER")
	@Basic(fetch = FetchType.EAGER)
	@XmlElement
	Integer serialNumber;
	/**
	 */

	@Column(name = "COLLECTION_NAME", length = 36, nullable = false)
	@Basic(fetch = FetchType.EAGER)
	@XmlElement
	String collectionName;
	/**
	 */

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LAST_UPDATED", nullable = false)
	@Basic(fetch = FetchType.EAGER)
	@XmlElement
	Date lastUpdated;

	/**
	 */
	public void setTitleId(String titleId) {
		this.titleId = titleId;
	}

	/**
	 */
	public String getTitleId() {
		return this.titleId;
	}

	/**
	 */
	public void setJobInstanceId(Integer jobInstanceId) {
		this.jobInstanceId = jobInstanceId;
	}

	/**
	 */
	public Integer getJobInstanceId() {
		return this.jobInstanceId;
	}

	/**
	 */
	public void setDocUuid(String docUuid) {
		this.docUuid = docUuid;
	}

	/**
	 */
	public String getDocUuid() {
		return this.docUuid;
	}

	/**
	 */
	public void setDocFamilyUuid(String docFamilyUuid) {
		this.docFamilyUuid = docFamilyUuid;
	}

	/**
	 */
	public String getDocFamilyUuid() {
		return this.docFamilyUuid;
	}

	/**
	 */
	public void setDocType(String docType) {
		this.docType = docType;
	}

	/**
	 */
	public String getDocType() {
		return this.docType;
	}

	/**
	 */
	public void setNormalizedFirstlineCite(String normalizedFirstlineCite) {
		this.normalizedFirstlineCite = normalizedFirstlineCite;
	}

	/**
	 */
	public String getNormalizedFirstlineCite() {
		return this.normalizedFirstlineCite;
	}

	/**
	 */
	public void setFindOrig(String findOrig) {
		this.findOrig = findOrig;
	}

	/**
	 */
	public String getFindOrig() {
		return this.findOrig;
	}

	/**
	 */
	public void setSerialNumber(Integer serialNumber) {
		this.serialNumber = serialNumber;
	}

	/**
	 */
	public Integer getSerialNumber() {
		return this.serialNumber;
	}

	/**
	 */
	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}

	/**
	 */
	public String getCollectionName() {
		return this.collectionName;
	}

	/**
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
	public DocMetadata() {
	}

	/**
	 * Returns a textual representation of a bean.
	 * 
	 */
	public String toString() {

		StringBuilder buffer = new StringBuilder();

		buffer.append("titleId=[").append(titleId).append("] ");
		buffer.append("jobInstanceId=[").append(jobInstanceId).append("] ");
		buffer.append("docUuid=[").append(docUuid).append("] ");
		buffer.append("docFamilyUuid=[").append(docFamilyUuid).append("] ");
		buffer.append("docType=[").append(docType).append("] ");
		buffer.append("normalizedFirstlineCite=[")
				.append(normalizedFirstlineCite).append("] ");
		buffer.append("findOrig=[").append(findOrig).append("] ");
		buffer.append("serialNumber=[").append(serialNumber).append("] ");
		buffer.append("collectionName=[").append(collectionName).append("] ");
		buffer.append("lastUpdated=[").append(lastUpdated).append("] ");

		return buffer.toString();
	}

	/**
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (int) (prime * result + ((titleId == null) ? 0 : titleId
				.hashCode()));
		result = (int) (prime * result + ((jobInstanceId == null) ? 0
				: jobInstanceId.hashCode()));
		result = (int) (prime * result + ((docUuid == null) ? 0 : docUuid
				.hashCode()));
		return result;
	}

	/**
	 */
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (!(obj instanceof DocMetadata))
			return false;
		DocMetadata equalCheck = (DocMetadata) obj;
		if ((titleId == null && equalCheck.titleId != null)
				|| (titleId != null && equalCheck.titleId == null))
			return false;
		if (titleId != null && !titleId.equals(equalCheck.titleId))
			return false;
		if ((jobInstanceId == null && equalCheck.jobInstanceId != null)
				|| (jobInstanceId != null && equalCheck.jobInstanceId == null))
			return false;
		if (jobInstanceId != null
				&& !jobInstanceId.equals(equalCheck.jobInstanceId))
			return false;
		if ((docUuid == null && equalCheck.docUuid != null)
				|| (docUuid != null && equalCheck.docUuid == null))
			return false;
		if (docUuid != null && !docUuid.equals(equalCheck.docUuid))
			return false;
		return true;
	}
}
