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
 * The ServiceStatus JSON object that comes back from a Image Vertical HTTP REST web service request.
 * This is a JSON object embedded in the HTTP response body (as part of a larger containing JSON object).
 */
public class ServiceStatus {

	private long elapsedTime;	// Total amount of time taken processing the request
	private String startTime;		// Time request processing started
	private int statusCode;		// The status of the response
	private String description;	// Descriptive text of what happened
	
	@JsonProperty("ElapsedTime")
	public long getElapsedTime() {
		return elapsedTime;
	}
	@JsonProperty("StartTime")
	public String getStartTime() {
		return startTime;
	}
	@JsonProperty("StatusCode")
	public int getStatusCode() {
		return statusCode;
	}
	@JsonProperty("StatusDescription")
	public String getDescription() {
		return description;
	}
	public void setElapsedTime(long elapsedTime) {
		this.elapsedTime = elapsedTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
