package com.thomsonreuters.uscl.ereader.mgr.web.service;

import java.net.InetSocketAddress;
import java.util.List;

import com.thomsonreuters.uscl.ereader.core.job.domain.JobOperationResponse;

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
	 * @param socketAddr Container for the hostname and port number for the push REST operation that will accept
	 *  the new app config.
	 * @param newAppConfig the new configuration properties to disseminate
	 * @return the response object indicating success or failure
	 */
	public JobOperationResponse syncApplicationConfiguration(InetSocketAddress socketAddr);
	/**
	 * Delete old database table and filesystem job data.
	 * @param daysBack jobs more than this many days old will be deleted. 
	 */
	public void cleanupOldSpringBatchJobs(int daysBack);

}
