/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.gather.image.domain;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.codehaus.jackson.annotate.JsonProperty;
import org.springframework.http.MediaType;

/**
 * The meta-data JSON object when fetching Image Vertical REST service image meta-data for a single image.
 * This object is part of the larger containing object: SingleImageMetadataResponse.
 */
public class SingleImageMetadata {
	
	private String 	ttype;
	private String 	guid;		// image GUID to retrieve
	private MediaType	mediaType;	// image mime-type
	private Long 	size;		// image size in bytes
	private String 	dimUnit;	// dimension units, like "px"
	private Long 	height;		// image height in the dimensional units
	private Long 	width;		// image width in the dimensional units
	private Long 	dpi;		// dots per inch
	private String contentDatabase;
	private String pageCount;
	private String royaltyCode;

	@JsonProperty("ContentDatabase")
	public String getContentDatabase() {
		return contentDatabase;
	}
	@JsonProperty("DimensionUnit")
	public String getDimUnit() {
		return dimUnit;
	}
	@JsonProperty("DPI")
	public Long getDpi() {
		return dpi;
	}
	@JsonProperty("ImageGuid")
	public String getGuid() {
		return guid;
	}
	@JsonProperty("Height")
	public Long getHeight() {
		return height;
	}
	@JsonProperty("MimeType")
	public MediaType getMediaType() {
		return mediaType;
	}
	@JsonProperty("PageCount")
	public String getPageCount() {
		return pageCount;
	}
	@JsonProperty("RoyaltyCode")
	public String getRoyaltyCode() {
		return royaltyCode;
	}
	@JsonProperty("ByteCount")
	public Long getSize() {
		return size;
	}
	@JsonProperty("TType")
	public String getTtype() {
		return ttype;
	}
	@JsonProperty("Width")
	public Long getWidth() {
		return width;
	}
	
	public void setContentDatabase(String contentDatabase) {
		this.contentDatabase = contentDatabase;
	}
	public void setDimUnit(String dimUnit) {
		this.dimUnit = dimUnit;
	}
	public void setDpi(Long dpi) {
		this.dpi = dpi;
	}
	public void setGuid(String guid) {
		this.guid = guid;
	}
	public void setHeight(Long height) {
		this.height = height;
	}
	public void setMediaType(String mimeType) {
		this.mediaType = MediaType.valueOf(mimeType);
	}
	public void setPageCount(String pageCount) {
		this.pageCount = pageCount;
	}
	public void setRoyaltyCode(String royaltyCode) {
		this.royaltyCode = royaltyCode;
	}
	public void setSize(Long size) {
		this.size = size;
	}
	public void setTtype(String ttype) {
		this.ttype = ttype;
	}
	public void setWidth(Long width) {
		this.width = width;
	}
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
