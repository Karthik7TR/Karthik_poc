/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.gather.image.domain;

import java.io.Serializable;

import javax.persistence.Embeddable;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * The primary key for image meta-data entity read from database.
 */
@Embeddable
public class ImageMetadataEntityKey implements Serializable {

	private static final long serialVersionUID = 964134678526913067L;
	private Long jobInstanceId;
	private String imageGuid;
	private String docUuid;

	public ImageMetadataEntityKey() {
		super();
	}
	
	public ImageMetadataEntityKey(Long jobInstanceId, String guid, String docUuid) {
		this.jobInstanceId = jobInstanceId;
		this.imageGuid = guid;
		this.docUuid = docUuid;
	}

	public Long getJobInstanceId() {
		return jobInstanceId;
	}
	public String getImageGuid() {
		return imageGuid;
	}
	public void setJobInstanceId(Long jobInstanceId) {
		this.jobInstanceId = jobInstanceId;
	}
	public void setImageGuid(String imageGuid) {
		this.imageGuid = imageGuid;
	}
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
	public String getDocUuid() {
		return docUuid;
	}

	public void setDocUuid(String docUuid) {
		this.docUuid = docUuid;
	}
	
	@Override 
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (int) (prime * result + ((jobInstanceId == null) ? 0 : jobInstanceId.hashCode()));
		result = (int) (prime * result + ((imageGuid == null) ? 0 : imageGuid.hashCode()));
		result = (int) (prime * result + ((docUuid == null) ? 0 : docUuid.hashCode()));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (!(obj instanceof ImageMetadataEntityKey)) {
			return false;
		}
		
		ImageMetadataEntityKey equalCheck = (ImageMetadataEntityKey) obj;
		
		if ((jobInstanceId == null && equalCheck.jobInstanceId != null) || (jobInstanceId != null && equalCheck.jobInstanceId == null))
			return false;
		if (jobInstanceId != null && !jobInstanceId.equals(equalCheck.jobInstanceId))
			return false;
		if ((docUuid == null && equalCheck.docUuid != null) || (docUuid != null && equalCheck.docUuid == null))
			return false;
		if (docUuid != null && !docUuid.equals(equalCheck.docUuid))
			return false;
		if ((imageGuid == null && equalCheck.imageGuid != null) || (imageGuid != null && equalCheck.imageGuid == null))
			return false;
		if (imageGuid != null && !imageGuid.equals(equalCheck.imageGuid))
			return false;
		return true;
	}
}
