package com.thomsonreuters.uscl.ereader.mgr.web.service;

import java.net.InetSocketAddress;

import com.thomsonreuters.uscl.ereader.core.job.domain.JobThrottleConfig;
import com.thomsonreuters.uscl.ereader.core.job.domain.MiscConfig;
import com.thomsonreuters.uscl.ereader.core.job.domain.SimpleRestServiceResponse;

public interface ManagerService {
	
	/**
	 * 
	 * @param socketAddr Container for the hostname and port number for the push REST operation that will accept
	 *  the new app config.
	 * @param webAppContextName the name of the web application, like "ebookGenerator"
	 * @return the response object indicating success or failure
	 */
	public SimpleRestServiceResponse pushMiscConfiguration(MiscConfig config, String webAppContextName, InetSocketAddress socketAddr);

	public SimpleRestServiceResponse pushJobThrottleConfiguration(JobThrottleConfig config, InetSocketAddress socketAddr);
	
	/**
	 * Delete old database table and filesystem job data.
	 * @param daysBack jobs more than this many days old will be deleted. 
	 */
	public void cleanupOldSpringBatchJobs(int daysBack);

}
