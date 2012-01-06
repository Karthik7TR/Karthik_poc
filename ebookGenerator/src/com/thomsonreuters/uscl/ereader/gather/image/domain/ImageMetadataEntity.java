/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.gather.image.domain;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Persisted metadata for images downloaded from the Image Vertical REST web service.
 */
@Entity
@Table(schema="EBOOK_AUTHORITY", name="IMAGE_METADATA")
public class ImageMetadataEntity implements Serializable {
	
	private static final long serialVersionUID = -3714413519050210417L;
	
	private ImageMetadataEntityKey primaryKey;  // jobInstanceId & imageGuid
	private String 	titleId;	// Identifies the book that this is for
	private Long	width;
	private Long 	height;
	private Long 	size;	// image size in bytes
	private Long	dpi;	// dots per inch
	private String  dimUnits;	// unit for dimensions, like "px" (pixels)

	public ImageMetadataEntity() {
		super();
	}

	public ImageMetadataEntity(ImageMetadataEntityKey key, String titleId,
							   Long width, Long height, Long size, Long dpi, String dimUnits) {
		this.primaryKey = key;
		this.titleId = titleId;
		this.width = width;
		this.height = height;
		this.size = size;
		this.dpi = dpi;
		this.dimUnits = dimUnits;
	}
	
	/**
	 * Primary key
	 */
	@EmbeddedId
	@AttributeOverrides({
		@AttributeOverride(name = "jobInstanceId", column = @Column(name="JOB_INSTANCE_ID", nullable=false)),
		@AttributeOverride(name = "imageGuid", column = @Column(name="IMAGE_GUID", nullable=false))
	})
	public ImageMetadataEntityKey getPrimaryKey() {
		return primaryKey;
	}
	
	@Column(name = "TITLE_ID", length=64, nullable=false)
	public String getTitleId() {
		return titleId;
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

	public void setPrimaryKey(ImageMetadataEntityKey pk) {
		this.primaryKey = pk;
	}
	public void setTitleId(String titleId) {
		this.titleId = titleId;
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
