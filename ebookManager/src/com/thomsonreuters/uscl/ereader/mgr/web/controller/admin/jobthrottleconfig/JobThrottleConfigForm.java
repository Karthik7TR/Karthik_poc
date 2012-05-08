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
		setThrottleStepActive(config.isThrottleStepActive());
		setThrottleStepName(config.getThrottleStepName());
		setThrotttleStepMaxJobs(config.getThrotttleStepMaxJobs());
	}
	
	public JobThrottleConfig getJobThrottleConfig() {
		return jobThrottleConfig;
	}
	public int getCoreThreadPoolSize() {
		return jobThrottleConfig.getCoreThreadPoolSize();
	}
	public boolean isThrottleStepActive() {
		return jobThrottleConfig.isThrottleStepActive();
	}
	public String getThrottleStepName() {
		return jobThrottleConfig.getThrottleStepName();
	}
	public int getThrotttleStepMaxJobs() {
		return jobThrottleConfig.getThrotttleStepMaxJobs();
	}
	public void setCoreThreadPoolSize(int coreThreadPoolSize) {
		jobThrottleConfig.setCoreThreadPoolSize(coreThreadPoolSize);
	}
	public void setThrottleStepActive(boolean throttleStepActive) {
		jobThrottleConfig.setThrottleStepActive(throttleStepActive);
	}
	public void setThrottleStepName(String throttleStepName) {
		jobThrottleConfig.setThrottleStepName(throttleStepName);
	}
	public void setThrotttleStepMaxJobs(int throtttleStepMaxJobs) {
		jobThrottleConfig.setThrotttleStepMaxJobs(throtttleStepMaxJobs);
	}
}
