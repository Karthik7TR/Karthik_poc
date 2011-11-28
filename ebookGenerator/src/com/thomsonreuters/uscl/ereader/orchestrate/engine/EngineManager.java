package com.thomsonreuters.uscl.ereader.orchestrate.engine;

import org.springframework.batch.core.JobExecution;

import com.thomsonreuters.uscl.ereader.orchestrate.core.JobRunRequest;


public interface EngineManager {
	
	public JobExecution runJob(JobRunRequest request) throws Exception;
	
	/**
	 * Resume a stopped batch job. Requires that it already be in a STOPPED or FAILED status,
	 * but makes no attempt to verify this before attempting to restart it.
	 * @param jobExecutionId of the job to be resumed
	 * @return the job execution ID of the restarted job
	 * @throws Exception on restart errors, like its already completed, or it cannot be resumed.
	 */
	public Long restartJob(long jobExecutionId) throws Exception;
	
	
	/**
	 * Perform a graceful stop of a job by stopping it after its current step has finished executing.
	 * @param jobExecutionId identifies the job to be stopped.
	 * @throws Exception on any stop failure
	 */
	public void stopJob(long jobExecutionId) throws Exception;
		
}
