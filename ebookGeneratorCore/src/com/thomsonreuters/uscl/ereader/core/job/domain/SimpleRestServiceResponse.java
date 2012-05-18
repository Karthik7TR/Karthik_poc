package com.thomsonreuters.uscl.ereader.core.job.domain;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Encapsulates a generic response from a REST service provider.
 * This object is marshalled and becomes the body of the REST response back to the REST client.
 */
public class SimpleRestServiceResponse {
	
	private Long id;
	private boolean success;
	private String message;
	
	public SimpleRestServiceResponse() {
		super();
	}
	
	/**
	 * The nominal success response object.
	 * @param jobExecutionId job execution id of job stopped or restarted.
	 */
	public SimpleRestServiceResponse(Long id) {
		this(id, true, null);
	}

	/**
	 * The full constructor for a response.
	 * @param id
	 * @param success true if the operation was successful.
	 * @param message informational message as to what went wrong.
	 */
	public SimpleRestServiceResponse(Long id, boolean success, String message) {
		this.id = id;
		this.success = success;
		this.message = message;
	}
	
	public Long getId() {
		return id;
	}
	public boolean isSuccess() {
		return success;
	}
	public String getMessage() {
		return message;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
