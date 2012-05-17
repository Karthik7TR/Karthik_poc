package com.thomsonreuters.uscl.ereader.core.job.service;

import com.thomsonreuters.uscl.ereader.core.job.domain.AppConfig;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobThrottleConfig;
import com.thomsonreuters.uscl.ereader.core.job.domain.LoggingConfig;

public interface AppConfigService {
	
	public AppConfig getAppConfig();
	
	public JobThrottleConfig getJobThrottleConfig();

	public LoggingConfig getLoggingConfig();
	
	/**
	 * Return the value for the specified key from the APP_PARAMETER table.
	 * @param key the lookup primary key
	 * @return the corresponding value
	 */
	public String getConfigValue(AppConfig.Key key);
	
	public void saveJobThrottleConfig(JobThrottleConfig config);
	public void saveLoggingConfig(LoggingConfig config);
	
	/**
	 * Update the in-memory log4j logging levels.
	 */
	public void setLogLevel(LoggingConfig config);
	
//	public void deleteAppConfig(AppConfig config);
	
	
}
