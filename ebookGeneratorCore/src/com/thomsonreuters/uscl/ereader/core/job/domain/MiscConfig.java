package com.thomsonreuters.uscl.ereader.core.job.domain;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.log4j.Level;

/**
 * Application and root log4j loggers log level configuration.
 */
public class MiscConfig {

	/** Typesafe representation of the keys used to represent the throttling configuration */
	public static enum Key { appLogLevel, rootLogLevel };
	
	public static final Level DEFAULT_APP_LOG_LEVEL = Level.INFO;
	public static final Level DEFAULT_ROOT_LOG_LEVEL = Level.ERROR;
	
	/** Current application log4j logging level, String in order to serialize */
	private String appLogLevel = DEFAULT_APP_LOG_LEVEL.toString();
	/** Current root log4j logging level, String in order to serialize */
	private String rootLogLevel = DEFAULT_ROOT_LOG_LEVEL.toString();
	
	public MiscConfig() {
		super();
	}
	/**
	 * Full constructor.
	 */
	public MiscConfig(Level appLogLevel, Level rootLogLevel) {
		setAllProperties(appLogLevel, rootLogLevel);
	}

	/**
	 * Copy the property values from one object to this one.
	 * @param config the source object to copy property values from.
	 */
	public void copy(MiscConfig config) {
		setAllProperties(config.getAppLogLevel(), config.getRootLogLevel());
	}

	private synchronized void setAllProperties(Level appLogLevel, Level rootLogLevel) {
		setAppLogLevel(appLogLevel);
		setRootLogLevel(rootLogLevel);
	}

	public Level getAppLogLevel() {
		return Level.toLevel(appLogLevel);
	}
	public Level getRootLogLevel() {
		return Level.toLevel(rootLogLevel);
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
		result = prime * result
				+ ((rootLogLevel == null) ? 0 : rootLogLevel.hashCode());
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
		MiscConfig other = (MiscConfig) obj;
		if (appLogLevel == null) {
			if (other.appLogLevel != null)
				return false;
		} else if (!appLogLevel.equals(other.appLogLevel))
			return false;
		if (rootLogLevel == null) {
			if (other.rootLogLevel != null)
				return false;
		} else if (!rootLogLevel.equals(other.rootLogLevel))
			return false;
		return true;
	}
}
