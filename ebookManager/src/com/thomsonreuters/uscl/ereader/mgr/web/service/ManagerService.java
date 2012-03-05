package com.thomsonreuters.uscl.ereader.mgr.web.service;

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

}
