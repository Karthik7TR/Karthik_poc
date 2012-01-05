/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.gather.image.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Persisted metadata for images downloaded from the Image Vertical REST web service.
 */
@Table(schema="EBOOK_AUTHORITY", name="IMAGE_METADATA")
public class ImageMetadataEntity implements Serializable {
	
	private static final long serialVersionUID = -3714413519050210417L;
	
	private Long	jobInstanceId;	// From the Spring Batch job
	private String	guid;	// image GUID
	private String 	titleId;	// Indetifies the book that this is for
	private Long	width;
	private Long 	height;
	private Long 	size;	// image size in bytes
	private Long	dpi;	// dots per inch
	private String  dimUnits;	// unit for dimensions, like "px" (pixels)

	public ImageMetadataEntity() {
		super();
	}

	public ImageMetadataEntity(Long jobInstanceId, String guid, String titleId, 
							   Long width, Long height, Long size, Long dpi, String dimUnits) {
		this.jobInstanceId = jobInstanceId;
		this.guid = guid;
		this.titleId = titleId;
		this.width = width;
		this.height = height;
		this.size = size;
		this.dpi = dpi;
		this.dimUnits = dimUnits;
	}
	
	@Column(name = "TITLE_ID", length=64, nullable=false)
	public String getTitleId() {
		return titleId;
	}
	@Column(name = "JOB_INSTANCE_ID")
	public Long getJobInstanceId() {
		return jobInstanceId;
	}
	@Column(name = "IMAGE_GUID", length=64)
	public String getGuid() {
		return guid;
	}
	@Column(name = "IMAGE_WIDTH")
	public Long getWidth() {
		return width;
	}
	@Column(name = "IMAGE_HEIGHT")
	public Long getHeight() {
		return height;
	}
	@Column(name = "IMAGE_SIZE")
	public Long getSize() {
		return size;
	}
	@Column(name = "IMAGE_DPI")
	public Long getDpi() {
		return dpi;
	}
	@Column(name = "IMAGE_DIM_UNITS", length=32)
	public String getDimUnits() {
		return dimUnits;
	}

	public void setTitleId(String titleId) {
		this.titleId = titleId;
	}
	public void setJobInstanceId(Long jobInstanceId) {
		this.jobInstanceId = jobInstanceId;
	}
	public void setGuid(String guid) {
		this.guid = guid;
	}
	public void setWidth(Long width) {
		this.width = width;
	}
	public void setHeight(Long height) {
		this.height = height;
	}
	public void setSize(Long size) {
		this.size = size;
	}
	public void setDpi(Long dpi) {
		this.dpi = dpi;
	}
	public void setDimUnits(String dimUnits) {
		this.dimUnits = dimUnits;
	}
	
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
