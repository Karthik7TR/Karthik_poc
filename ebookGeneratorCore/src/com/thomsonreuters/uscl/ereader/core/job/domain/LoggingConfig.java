package com.thomsonreuters.uscl.ereader.core.job.domain;

import org.apache.log4j.Level;

/**
 * Application and root log4j loggers log level configuration.
 */
public interface LoggingConfig {

	public Level getAppLogLevel();
	public Level getRootLogLevel();
	
	public void setAppLogLevel(Level level);
	public void setRootLogLevel(Level level);

}
