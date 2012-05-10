package com.thomsonreuters.uscl.ereader.core.job.domain;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class JobThrottleConfig {
	
	/** Typesafe representation of the keys used to represent the throttling configuration */
	public static enum Key { coreThreadPoolSize, stepThrottleEnabled, throttleStepName, throtttleStepMaxJobs };
	
	/** The task executor core thread pool size */
	private int coreThreadPoolSize = 2;
	/** is step-level throttling active */
	private boolean stepThrottleEnabled = false;
	/** The step name at which the throttle is applied */
	private String throttleStepName = null;
	/** The limit of jobs up to the specified throttle step name */
	private int throtttleStepMaxJobs = 2;

	public JobThrottleConfig() {
		super();
	}
	public JobThrottleConfig(int coreThreadPoolSize, boolean stepThrottleEnabled,
							 String throttleStepName, int throtttleStepMaxJobs) {
		setAllProperties(coreThreadPoolSize, stepThrottleEnabled, throttleStepName, throtttleStepMaxJobs);
	}

	/**
	 * Copy the property values from one object to this one.
	 * @param config the source object to copy property values from.
	 */
	public void sync(JobThrottleConfig config) {
		setAllProperties(config.getCoreThreadPoolSize(), config.isStepThrottleEnabled(),
						 config.getThrottleStepName(), config.getThrotttleStepMaxJobs());
	}

	private synchronized void setAllProperties(int coreThreadPoolSize, boolean stepThrottleEnabled,
								 String throttleStepName, int throtttleStepMaxJobs) {
		this.coreThreadPoolSize = coreThreadPoolSize;
		this.stepThrottleEnabled = stepThrottleEnabled;
		this.throttleStepName = throttleStepName;
		this.throtttleStepMaxJobs = throtttleStepMaxJobs;
	}

	public int getCoreThreadPoolSize() {
		return coreThreadPoolSize;
	}
	public boolean isStepThrottleEnabled() {
		return stepThrottleEnabled;
	}
	public String getThrottleStepName() {
		return throttleStepName;
	}
	public int getThrotttleStepMaxJobs() {
		return throtttleStepMaxJobs;
	}
	public void setCoreThreadPoolSize(int coreThreadPoolSize) {
		this.coreThreadPoolSize = coreThreadPoolSize;
	}
	public void setStepThrottleEnabled(boolean stepThrottleEnabled) {
		this.stepThrottleEnabled = stepThrottleEnabled;
	}
	public void setThrottleStepName(String throttleStepName) {
		this.throttleStepName = throttleStepName;
	}
	public void setThrotttleStepMaxJobs(int throtttleStepMaxJobs) {
		this.throtttleStepMaxJobs = throtttleStepMaxJobs;
	}

	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
