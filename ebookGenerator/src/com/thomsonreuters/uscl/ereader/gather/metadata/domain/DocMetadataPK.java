/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.gather.metadata.domain;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.Id;

/**
 */
public class DocMetadataPK implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 */
	public DocMetadataPK() {
	}

	/**
	 */

	@Column(name = "TITLE_ID", length = 64, nullable = false)
	@Basic(fetch = FetchType.EAGER)
	@Id
	public String titleId;
	/**
	 */

	@Column(name = "JOB_INSTANCE_ID", nullable = false)
	@Basic(fetch = FetchType.EAGER)
	@Id
	public Integer jobInstanceId;
	/**
	 */

	@Column(name = "DOC_UUID", length = 36, nullable = false)
	@Basic(fetch = FetchType.EAGER)
	@Id
	public String docUuid;

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
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (int) (prime * result + ((titleId == null) ? 0 : titleId.hashCode()));
		result = (int) (prime * result + ((jobInstanceId == null) ? 0 : jobInstanceId.hashCode()));
		result = (int) (prime * result + ((docUuid == null) ? 0 : docUuid.hashCode()));
		return result;
	}

	/**
	 */
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (!(obj instanceof DocMetadataPK))
			return false;
		DocMetadataPK equalCheck = (DocMetadataPK) obj;
		if ((titleId == null && equalCheck.titleId != null) || (titleId != null && equalCheck.titleId == null))
			return false;
		if (titleId != null && !titleId.equals(equalCheck.titleId))
			return false;
		if ((jobInstanceId == null && equalCheck.jobInstanceId != null) || (jobInstanceId != null && equalCheck.jobInstanceId == null))
			return false;
		if (jobInstanceId != null && !jobInstanceId.equals(equalCheck.jobInstanceId))
			return false;
		if ((docUuid == null && equalCheck.docUuid != null) || (docUuid != null && equalCheck.docUuid == null))
			return false;
		if (docUuid != null && !docUuid.equals(equalCheck.docUuid))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("DocMetadataPK");
		sb.append(" titleId: ").append(getTitleId());
		sb.append(" jobInstanceId: ").append(getJobInstanceId());
		sb.append(" docUuid: ").append(getDocUuid());
		return sb.toString();
	}
}
