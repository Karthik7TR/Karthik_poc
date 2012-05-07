package com.thomsonreuters.uscl.ereader.core.job.service;

import java.net.InetAddress;

import com.thomsonreuters.uscl.ereader.core.job.domain.JobThrottleConfig;

public interface JobThrottleConfigService {
	
	/**
	 * Return the value for the specified key from the APP_PARAMETER table.
	 * @param key the lookup primary key
	 * @return the corresponding value
	 */
	public String getConfigValue(JobThrottleConfig.Key key);
	
	
	/**
	 * Lookup the set of application parameters that comprise the Throttle configuration.
	 * @return
	 */
	public JobThrottleConfig getThrottleConfig();

	public void saveJobThrottleConfig(JobThrottleConfig config);
	public void deleteJobThrottleConfig(JobThrottleConfig config);
	
	/**
	 * Push the new throttle configuration out to a listenting (a REST service) host that cares about receiving the new config.\
	 * It is assumed that the configuration listening service is enabled.
	 */
	public void pushConfiguration(InetAddress host, JobThrottleConfig config) throws Exception;

}
