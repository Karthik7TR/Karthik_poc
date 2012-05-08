package com.thomsonreuters.uscl.ereader.core.job.domain;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class JobThrottleConfig {
	
	/** Typesafe representation of the keys used to represent the throttling configuration */
	public static enum Key { coreThreadPoolSize, throttleStepActive, throttleStepName, throtttleStepMaxJobs };
	
	/** The task executor core thread pool size */
	private int coreThreadPoolSize = 4;
	/** is step-level throttling active */
	private boolean throttleStepActive = false;
	/** The step name at which the throttle is applied */
	private String throttleStepName = null;
	/** The limit of jobs up to the specified throttle step name */
	private int throtttleStepMaxJobs = 4;

	public JobThrottleConfig() {
		super();
	}
	public JobThrottleConfig(int coreThreadPoolSize, boolean throttleStepActive,
							 String throttleStepName, int throtttleStepMaxJobs) {
		setAllProperties(coreThreadPoolSize, throttleStepActive, throttleStepName, throtttleStepMaxJobs);
	}

	/**
	 * Copy the property values from one object to this one.
	 * @param config the source object to copy property values from.
	 */
	public void sync(JobThrottleConfig config) {
		setAllProperties(config.getCoreThreadPoolSize(), config.isThrottleStepActive(),
						 config.getThrottleStepName(), config.getThrotttleStepMaxJobs());
	}

	private synchronized void setAllProperties(int coreThreadPoolSize, boolean throttleStepActive,
								 String throttleStepName, int throtttleStepMaxJobs) {
		this.coreThreadPoolSize = coreThreadPoolSize;
		this.throttleStepActive = throttleStepActive;
		this.throttleStepName = throttleStepName;
		this.throtttleStepMaxJobs = throtttleStepMaxJobs;
	}

	public int getCoreThreadPoolSize() {
		return coreThreadPoolSize;
	}
	public boolean isThrottleStepActive() {
		return throttleStepActive;
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
	public void setThrottleStepActive(boolean throttleStepActive) {
		this.throttleStepActive = throttleStepActive;
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
