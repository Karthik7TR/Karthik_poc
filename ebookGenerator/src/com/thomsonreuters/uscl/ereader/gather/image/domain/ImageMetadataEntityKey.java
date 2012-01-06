/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.gather.image.domain;

import java.io.Serializable;

import javax.persistence.Embeddable;

import org.apache.commons.lang.StringUtils;
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
	
	public ImageMetadataEntityKey() {
		super();
	}
	
	public ImageMetadataEntityKey(Long jobInstanceId, String guid) {
		this.jobInstanceId = jobInstanceId;
		this.imageGuid = guid;
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
	
	@Override 
	public int hashCode() {
		int hashCode = jobInstanceId.intValue();
		if (StringUtils.isNotBlank(imageGuid)) {
			hashCode += imageGuid.hashCode();
		}
		return hashCode;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ImageMetadataEntityKey)) {
			return false;
		}
		ImageMetadataEntityKey that = (ImageMetadataEntityKey) obj;
		return (this.jobInstanceId.equals(that.jobInstanceId) &&
				this.imageGuid.equals(that.imageGuid));
	}
}
