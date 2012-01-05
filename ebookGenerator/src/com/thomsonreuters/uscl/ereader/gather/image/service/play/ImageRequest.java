/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.gather.image.service.play;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * A request object for fetching an image from the Image Vertical RESTful web service.
 */
public class ImageRequest {
	
	private String ttype;
	private String guid;		// image GUID to retrieve
	private String mimeType;	// format to convert the image to
	private String tag;			// client-specified string that follows the image from request to response
	private String basename;	// name to apply to the file within a merged container (like a ZIP file), or individual files if a non-merged image package is requested.

	@JsonProperty("TType")
	public String getTtype() {
		return ttype;
	}
	@JsonProperty("ImageGuid")
	public String getGuid() {
		return guid;
	}
	@JsonProperty("RequestedMimeType")
	public String getMimeType() {
		return mimeType;
	}
	@JsonProperty("Tag")
	public String getTag() {
		return tag;
	}
	@JsonProperty("Filename")
	public String getBasename() {
		return basename;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public void setBasename(String basename) {
		this.basename = basename;
	}
	public void setTtype(String ttype) {
		this.ttype = ttype;
	}
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
