/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.gather.image.domain;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Unmarshalled HTTP body response for REST requests to the Image Vertical REST service.
 * The HttpMessageConverter for this class is a MappingJacksonHttpMessageConverter.
 * HTTP request is in the form:
 * https://{host}/image/{version}/images/ttype/null/guid/{guid}/meta
 * The response is a set of JSON object comprised of the objects defined as fields of this class.
 */
public class SingleImageMetadataResponse {
	
	private Header header;
	private TraceInformation trace;
	private ServiceStatus serviceStatus;
	private SingleImageMetadata imageMetadata;
	
	@JsonProperty("Header")
	public Header getHeader() {
		return header;
	}
	@JsonProperty("Trace")
	public TraceInformation getTrace() {
		return trace;
	}
	@JsonProperty("ServiceStatus")
	public ServiceStatus getServiceStatus() {
		return serviceStatus;
	}
	@JsonProperty("SingleImageMetadata")
	public SingleImageMetadata getImageMetadata() {
		return imageMetadata;
	}

	public void setHeader(Header header) {
		this.header = header;
	}
	public void setTrace(TraceInformation trace) {
		this.trace = trace;
	}
	public void setServiceStatus(ServiceStatus serviceStatus) {
		this.serviceStatus = serviceStatus;
	}
	public void setImageMetadata(SingleImageMetadata imageMetadata) {
		this.imageMetadata = imageMetadata;
	}

	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
