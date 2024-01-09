package com.thomsonreuters.uscl.ereader.gather.image.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * The ServiceStatus JSON object that comes back from a Image Vertical HTTP REST web service request.
 * This is a JSON object embedded in the HTTP response body (as part of a larger containing JSON object).
 */
public class ServiceStatus {
    private long elapsedTime; // Total amount of time taken processing the request
    private String startTime; // Time request processing started
    private int statusCode; // The status of the response
    private String description; // Descriptive text of what happened

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

    public void setElapsedTime(final long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public void setStartTime(final String startTime) {
        this.startTime = startTime;
    }

    public void setStatusCode(final int statusCode) {
        this.statusCode = statusCode;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
