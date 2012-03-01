package com.thomsonreuters.uscl.ereader.mgr.web.controller.job.details;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Form backing object for the Job Summary/Executions page.
 */
public class JobExecutionForm {
	
	public static final String FORM_NAME = "jobExecutionForm";

	private Long jobExecutionId;

	public Long getJobExecutionId() {
		return jobExecutionId;
	}

	public void setJobExecutionId(Long executionId) {
		this.jobExecutionId = executionId;
	}
	
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
