package com.thomsonreuters.uscl.ereader.mgr.web.controller.job.list;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class JobListForm {
	
	public static final String FORM_NAME = "jobListForm";

	private Long[] 	jobId;
	
	/** Selected Job instance ID for multi-select */
	public Long[] getJobId() {
		return jobId;
	}

	public void setJobId(Long[] jobId) {
		this.jobId = jobId;
	}

	public String toString() {
		return ReflectionToStringBuilder.toString(this,
				ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
