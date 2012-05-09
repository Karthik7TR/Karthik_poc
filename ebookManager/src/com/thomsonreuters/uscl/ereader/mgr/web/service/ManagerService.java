package com.thomsonreuters.uscl.ereader.mgr.web.service;

import java.net.InetSocketAddress;
import java.util.List;

import com.thomsonreuters.uscl.ereader.core.job.domain.JobOperationResponse;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobThrottleConfig;

public interface ManagerService {
	
	/**
	 * The client to the ebook Generator operations REST service to restart a stopped or failed Spring Batch job.
	 * @param jobExecutionId id of the execution to be restarted
	 * @return response that encapsulates success or failure, along with a description of any problem.
	 */
	public JobOperationResponse restartJob(long jobExecutionId);

	/**
	 * The client to the ebook Generator operations REST service to stop an active Spring Batch job.
	 * @param jobExecutionId id of the execution to be restarted
	 * @return response that encapsulates success or failure, along with a description of any problem.
	 */
	public JobOperationResponse stopJob(long jobExecutionId);
	
	/**
	 * Get the book publishing job step names.
	 * @return a comma separated list of job step names
	 */
	public List<String> getStepNames();
	
	/**
	 * 
	 * @param socketAddr Container for the hostname and port number for the push REST operation that will accept the new throttle config.
	 * @param newJobThrottleConfig the new throttle configuration
	 * @return the response object indicating success or failure
	 */
	public JobOperationResponse pushJobThrottleConfiguration(InetSocketAddress socketAddr, JobThrottleConfig newJobThrottleConfig);

}
