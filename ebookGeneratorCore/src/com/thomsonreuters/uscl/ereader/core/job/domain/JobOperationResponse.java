package com.thomsonreuters.uscl.ereader.core.job.domain;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Encapsulates the response from the generator REST service that carries job stop and restart requests.
 * This object is marshalled and becomes the body of the REST response back to the manager client.
 */
public class JobOperationResponse {
	
	private Long jobExecutionId;
	private boolean success;
	private String message;
	
	public JobOperationResponse() {
		super();
	}
	
	/**
	 * The nominal success response object.
	 * @param jobExecutionId job execution id of job stopped or restarted.
	 */
	public JobOperationResponse(Long jobExecutionId) {
		this(jobExecutionId, true, null);
	}

	/**
	 * The full constructor for a response.
	 * @param jobExecutionId job execution id of job stopped or restarted.
	 * @param success true if the operation was successful.
	 * @param message informational message as to what went wrong.
	 */
	public JobOperationResponse(Long jobExecutionId, boolean success, String message) {
		this.jobExecutionId = jobExecutionId;
		this.success = success;
		this.message = message;
	}
	
	public Long getJobExecutionId() {
		return jobExecutionId;
	}
	public boolean isSuccess() {
		return success;
	}
	public String getMessage() {
		return message;
	}
	public void setJobExecutionId(Long jobExecutionId) {
		this.jobExecutionId = jobExecutionId;
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
