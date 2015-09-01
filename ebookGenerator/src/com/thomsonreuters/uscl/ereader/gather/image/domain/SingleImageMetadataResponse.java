/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.gather.image.domain;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Unmarshalled HTTP body response for REST requests to the Image Vertical REST service.
 * The HttpMessageConverter for this class is a MappingJacksonHttpMessageConverter.
 * HTTP request is in the form:
 * https://{host}/image/{version}/images/ttype/null/guid/{guid}/meta
 * The response is a set of JSON object comprised of the objects defined as fields of this class.
 */
public class SingleImageMetadataResponse {
	
	private String data;
	private String exception;
	private Boolean isSuccessful;
	private SingleImageMetadata imageMetadata;
	
	@JsonProperty("Data")
	public String getData() {
		return data;
	}
	@JsonProperty("Exception")
	public String getException() {
		return exception;
	}
	@JsonProperty("IsSuccessful")
	public Boolean getIsSuccessful() {
		return isSuccessful;
	}
	@JsonProperty("SingleImageMetadata")
	public SingleImageMetadata getImageMetadata() {
		return imageMetadata;
	}

	public void setData(String data) {
		this.data = data;
	}
	public void setException(String exception) {
		this.exception = exception;
	}
	public void setIsSuccessful(Boolean isSuccessful) {
		this.isSuccessful = isSuccessful;
	}
	public void setImageMetadata(SingleImageMetadata imageMetadata) {
		this.imageMetadata = imageMetadata;
	}

	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
