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
 * A request object for fetching image meta-data.
 */
public class ImageMetadataRequest {
	
	private String ttype;
	private String guid;		// image GUID to retrieve

	@JsonProperty("ImageGuid")
	public String getGuid() {
		return guid;
	}
	@JsonProperty("TType")
	public String getTtype() {
		return ttype;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}
	public void setTtype(String ttype) {
		this.ttype = ttype;
	}
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
