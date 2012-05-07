package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.jobthrottleconfig;

import com.thomsonreuters.uscl.ereader.core.job.domain.JobThrottleConfig;

public class JobThrottleConfigForm extends JobThrottleConfig {
	
	public static final String FORM_NAME = "jobThrottleForm";
	
	public JobThrottleConfigForm() {
		setCoreThreadPoolSize(8);
		setThrottleStepActive(false);
		setThrottleStepName("none");
		setThrotttleStepMaxJobs(6);
	}
	
	/**
	 * Populate the form from the corresponding domain object.
	 */
	public void initialize(JobThrottleConfig config) {
		setCoreThreadPoolSize(config.getCoreThreadPoolSize());
		setThrottleStepActive(config.isThrottleStepActive());
		setThrottleStepName(config.getThrottleStepName());
		setThrotttleStepMaxJobs(config.getThrotttleStepMaxJobs());
	}
	
	public JobThrottleConfig createJobThrottleConfig() {
		return this;
	}
}
