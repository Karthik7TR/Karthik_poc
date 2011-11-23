package com.thomsonreuters.uscl.ereader.orchestrate.engine;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;


public interface EngineManager {
	
	/**
	 * Immediately run a batch job at the specified thread execution priority.
	 * @param jobName the name of the job as defined in Spring bean definition file(s).
	 * @param threadPriority 1..10, but mapped to LOW (1..4), NORMAL (5), HIGH (6..10).
	 * @param jobParameters user provided key/value pairs defined prior to job launch, but used within the job.
	 * The database provided set will be combined with this set prior to the job being run.
	 * @return the job execution object
	 * @throws Exception on unable to find job name, or launching the job
	 */
	public JobExecution runJob(String jobName, Integer threadPriority, JobParameters jobParameters) throws Exception;
	public JobExecution runJob(String jobName, Integer threadPriority) throws Exception;
	public JobExecution runJob(String jobName) throws Exception;
	
	
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
