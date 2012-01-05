package com.thomsonreuters.uscl.ereader.gather.image.service.play;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * A response object for fetching images from the Image Vertical RESTful web service.
 */
public class ImageResponse {
	
	private String tag;	// client-specified string that follows the image from request to response.
	private String uri;	// Image resource URI relative to the version of the API.
	
	@JsonProperty("Tag")
	public String getTag() {
		return tag;
	}
	@JsonProperty("ImageResourceUri")
	public String getUri() {
		return uri;
	}
	
	public void setTag(String tag) {
		this.tag = tag;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
