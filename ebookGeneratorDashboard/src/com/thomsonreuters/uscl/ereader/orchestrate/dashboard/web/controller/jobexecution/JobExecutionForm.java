package com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.controller.jobexecution;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Form backing object for the Job Summary/Executions page.
 */
public class JobExecutionForm {
	
	public static final String FORM_NAME = "jobExecutionForm";

	private Long executionId;

	public Long getExecutionId() {
		return executionId;
	}

	public void setExecutionId(Long executionId) {
		this.executionId = executionId;
	}
	
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
