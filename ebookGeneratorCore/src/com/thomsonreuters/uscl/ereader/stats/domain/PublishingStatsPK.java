/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.stats.domain;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.Id;

/**
 */
public class PublishingStatsPK implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 */
	public PublishingStatsPK() {
	}


	/**
	 */

	@Column(name = "JOB_INSTANCE_ID", nullable = false)
	@Basic(fetch = FetchType.EAGER)
	@Id
	public Long jobInstanceId;
	/**
	 */



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
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (int) (prime * result + ((jobInstanceId == null) ? 0 : jobInstanceId.hashCode()));
		return result;
	}

	/**
	 */
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (!(obj instanceof PublishingStatsPK))
			return false;
		PublishingStatsPK equalCheck = (PublishingStatsPK) obj;
		if ((jobInstanceId == null && equalCheck.jobInstanceId != null) || (jobInstanceId != null && equalCheck.jobInstanceId == null))
			return false;
		if (jobInstanceId != null && !jobInstanceId.equals(equalCheck.jobInstanceId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("PublishingStatsPK");
		sb.append(" jobInstanceId: ").append(getJobInstanceId());
		return sb.toString();
	}
}
