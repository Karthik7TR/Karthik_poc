package com.thomsonreuters.uscl.ereader.orchestrate.engine.web.controller.admin;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class AdminForm {
	
	public static final String FORM_NAME = "adminForm";
	
	private Integer maxConcurrentJobs;

	public Integer getMaxConcurrentJobs() {
		return maxConcurrentJobs;
	}
	public void setMaxConcurrentJobs(Integer maxConcurrentJobs) {
		this.maxConcurrentJobs = maxConcurrentJobs;
	}
	
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
