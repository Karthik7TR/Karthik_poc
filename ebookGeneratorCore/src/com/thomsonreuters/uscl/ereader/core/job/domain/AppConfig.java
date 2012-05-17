package com.thomsonreuters.uscl.ereader.core.job.domain;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.log4j.Level;

/**
 * General application configuration parameters.
 */
public class AppConfig implements JobThrottleConfig, LoggingConfig {

	/** Typesafe representation of the keys used to represent the throttling configuration */
	public static enum Key { // 2 log4j logging levels ("com.thomsonretuers..." and root loggers)
							 appLogLevel, rootLogLevel, 
							 // 4 properties for job throttle configuration
							 coreThreadPoolSize, stepThrottleEnabled, throttleStepName, throtttleStepMaxJobs };
	
	public static final Level DEFAULT_APP_LOG_LEVEL = Level.INFO;
	public static final Level DEFAULT_ROOT_LOG_LEVEL = Level.ERROR;
	
	/** The task executor core thread pool size */
	private int coreThreadPoolSize = 2;
	/** is step-level throttling active */
	private boolean stepThrottleEnabled = false;
	/** The step name at which the throttle is applied */
	private String throttleStepName = null;
	/** The limit of jobs up to the specified throttle step name */
	private int throtttleStepMaxJobs = 2;
	/** Current application log4j logging level, String in order to serialize */
	private String appLogLevel = DEFAULT_APP_LOG_LEVEL.toString();
	/** Current root log4j logging level, String in order to serialize */
	private String rootLogLevel = DEFAULT_ROOT_LOG_LEVEL.toString();

	public AppConfig() {
		super();
	}

	public static JobThrottleConfig createJobThrottleConfig(int coreThreadPoolSize, boolean stepThrottleEnabled,
			String throttleStepName, int throtttleStepMaxJobs) {
		return new AppConfig(DEFAULT_APP_LOG_LEVEL, DEFAULT_ROOT_LOG_LEVEL,
							 coreThreadPoolSize, stepThrottleEnabled, throttleStepName, throtttleStepMaxJobs);
	}
	public static LoggingConfig createLoggingConfig(Level appLogLevel, Level rootLogLevel) {
		LoggingConfig config = new AppConfig();
		config.setAppLogLevel(appLogLevel);
		config.setRootLogLevel(rootLogLevel);
		return config;
	}

	/**
	 * Full constructor.
	 */
	public AppConfig(Level appLogLevel, Level rootLogLevel,
					 int coreThreadPoolSize, boolean stepThrottleEnabled,
					 String throttleStepName, int throtttleStepMaxJobs) {
		setAllProperties(appLogLevel, rootLogLevel, coreThreadPoolSize, stepThrottleEnabled, throttleStepName, throtttleStepMaxJobs);
	}

	/**
	 * Copy the property values from one object to this one.
	 * @param config the source object to copy property values from.
	 */
	public void copy(AppConfig config) {
		setAllProperties(config.getAppLogLevel(), config.getRootLogLevel(),
						 config.getCoreThreadPoolSize(), config.isStepThrottleEnabled(),
						 config.getThrottleStepName(), config.getThrotttleStepMaxJobs());
	}

	private synchronized void setAllProperties(Level appLogLevel, Level rootLogLevel,
								int coreThreadPoolSize, boolean stepThrottleEnabled,
								String throttleStepName, int throtttleStepMaxJobs) {
		setAppLogLevel(appLogLevel);
		setRootLogLevel(rootLogLevel);
		setCoreThreadPoolSize(coreThreadPoolSize);
		setStepThrottleEnabled(stepThrottleEnabled);
		setThrottleStepName(throttleStepName);
		setThrotttleStepMaxJobs(throtttleStepMaxJobs);
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
	public Level getAppLogLevel() {
		return Level.toLevel(appLogLevel);
	}
	public Level getRootLogLevel() {
		return Level.toLevel(rootLogLevel);
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
	public void setAppLogLevel(Level logLevel) {
		this.appLogLevel = (logLevel != null) ? logLevel.toString() : DEFAULT_APP_LOG_LEVEL.toString();
	}
	public void setRootLogLevel(Level logLevel) {
		this.rootLogLevel = (logLevel != null) ? logLevel.toString() : DEFAULT_ROOT_LOG_LEVEL.toString();
	}

	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((appLogLevel == null) ? 0 : appLogLevel.hashCode());
		result = prime * result + coreThreadPoolSize;
		result = prime * result
				+ ((rootLogLevel == null) ? 0 : rootLogLevel.hashCode());
		result = prime * result + (stepThrottleEnabled ? 1231 : 1237);
		result = prime
				* result
				+ ((throttleStepName == null) ? 0 : throttleStepName.hashCode());
		result = prime * result + throtttleStepMaxJobs;
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
		AppConfig other = (AppConfig) obj;
		if (appLogLevel == null) {
			if (other.appLogLevel != null)
				return false;
		} else if (!appLogLevel.equals(other.appLogLevel))
			return false;
		if (coreThreadPoolSize != other.coreThreadPoolSize)
			return false;
		if (rootLogLevel == null) {
			if (other.rootLogLevel != null)
				return false;
		} else if (!rootLogLevel.equals(other.rootLogLevel))
			return false;
		if (stepThrottleEnabled != other.stepThrottleEnabled)
			return false;
		if (throttleStepName == null) {
			if (other.throttleStepName != null)
				return false;
		} else if (!throttleStepName.equals(other.throttleStepName))
			return false;
		if (throtttleStepMaxJobs != other.throtttleStepMaxJobs)
			return false;
		return true;
	}
}
