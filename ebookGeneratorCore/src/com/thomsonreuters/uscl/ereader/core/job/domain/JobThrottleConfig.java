/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.core.job.domain;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

@XmlRootElement(name="jobThrottleConfig")
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
	private int throttleStepMaxJobs = 2;

	public JobThrottleConfig() {
		super();
	}
	
	/**
	 * Full constructor.
	 */
	public JobThrottleConfig(int coreThreadPoolSize, boolean stepThrottleEnabled,
					 	     String throttleStepName, int throtttleStepMaxJobs) {
		setAllProperties(coreThreadPoolSize, stepThrottleEnabled, throttleStepName, throtttleStepMaxJobs);
	}

	/**
	 * Copy the property values from one object to this one.
	 * @param config the source object to copy property values from.
	 */
	public void copy(JobThrottleConfig config) {
		setAllProperties(config.getCoreThreadPoolSize(), config.isStepThrottleEnabled(),
						 config.getThrottleStepName(), config.getThrottleStepMaxJobs());
	}

	private synchronized void setAllProperties(int coreThreadPoolSize, boolean stepThrottleEnabled,
											   String throttleStepName, int throtttleStepMaxJobs) {
		setCoreThreadPoolSize(coreThreadPoolSize);
		setStepThrottleEnabled(stepThrottleEnabled);
		setThrottleStepName(throttleStepName);
		setThrottleStepMaxJobs(throtttleStepMaxJobs);
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
	public int getThrottleStepMaxJobs() {
		return throttleStepMaxJobs;
	}
	@XmlElement(name="coreThreadPoolSize", required=true)
	public void setCoreThreadPoolSize(int coreThreadPoolSize) {
		this.coreThreadPoolSize = coreThreadPoolSize;
	}
	@XmlElement(name="stepThrottleEnabled", required=true)
	public void setStepThrottleEnabled(boolean stepThrottleEnabled) {
		this.stepThrottleEnabled = stepThrottleEnabled;
	}
	@XmlElement(name="throttleStepName", required=true)
	public void setThrottleStepName(String throttleStepName) {
		this.throttleStepName = throttleStepName;
	}
	@XmlElement(name="throttleStepMaxJobs", required=true)
	public void setThrottleStepMaxJobs(int throttleStepMaxJobs) {
		this.throttleStepMaxJobs = throttleStepMaxJobs;
	}

	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + coreThreadPoolSize;
		result = prime * result + (stepThrottleEnabled ? 1231 : 1237);
		result = prime
				* result
				+ ((throttleStepName == null) ? 0 : throttleStepName.hashCode());
		result = prime * result + throttleStepMaxJobs;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JobThrottleConfig other = (JobThrottleConfig) obj;
		if (coreThreadPoolSize != other.coreThreadPoolSize)
			return false;
		if (stepThrottleEnabled != other.stepThrottleEnabled)
			return false;
		if (throttleStepName == null) {
			if (other.throttleStepName != null)
				return false;
		} else if (!throttleStepName.equals(other.throttleStepName))
			return false;
		if (throttleStepMaxJobs != other.throttleStepMaxJobs)
			return false;
		return true;
	}
}
