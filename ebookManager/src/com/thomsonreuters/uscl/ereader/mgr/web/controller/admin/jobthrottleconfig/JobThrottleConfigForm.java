/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.jobthrottleconfig;

import com.thomsonreuters.uscl.ereader.core.job.domain.JobThrottleConfig;

public class JobThrottleConfigForm {
	
	private JobThrottleConfig jobThrottleConfig = new JobThrottleConfig();
	
	public static final String FORM_NAME = "jobThrottleForm";
	
	public JobThrottleConfigForm() {
		super();
	}
	
	/**
	 * Populate the form from the corresponding domain object.
	 */
	public void initialize(JobThrottleConfig config) {
		setCoreThreadPoolSize(config.getCoreThreadPoolSize());
		setStepThrottleEnabled(config.isStepThrottleEnabled());
		setThrottleStepName(config.getThrottleStepName());
		setThrotttleStepMaxJobs(config.getThrottleStepMaxJobs());
	}
	
	public JobThrottleConfig getJobThrottleConfig() {
		return jobThrottleConfig;
	}
	public int getCoreThreadPoolSize() {
		return jobThrottleConfig.getCoreThreadPoolSize();
	}
	public boolean isStepThrottleEnabled() {
		return jobThrottleConfig.isStepThrottleEnabled();
	}
	public String getThrottleStepName() {
		return jobThrottleConfig.getThrottleStepName();
	}
	public int getThrotttleStepMaxJobs() {
		return jobThrottleConfig.getThrottleStepMaxJobs();
	}
	public void setCoreThreadPoolSize(int coreThreadPoolSize) {
		jobThrottleConfig.setCoreThreadPoolSize(coreThreadPoolSize);
	}
	public void setStepThrottleEnabled(boolean enabled) {
		jobThrottleConfig.setStepThrottleEnabled(enabled);
	}
	public void setThrottleStepName(String throttleStepName) {
		jobThrottleConfig.setThrottleStepName(throttleStepName);
	}
	public void setThrotttleStepMaxJobs(int throtttleStepMaxJobs) {
		jobThrottleConfig.setThrottleStepMaxJobs(throtttleStepMaxJobs);
	}
}
