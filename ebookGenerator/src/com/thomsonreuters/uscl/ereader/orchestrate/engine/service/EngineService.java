package com.thomsonreuters.uscl.ereader.orchestrate.engine.service;

import com.thomsonreuters.uscl.ereader.core.job.domain.JobRequest;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;

/**
 * Utilities for interacting with the Spring Batch engine used in starting, restarting, and stopping jobs.
 */
public interface EngineService
{
    /**
     * Start a new e-book generating job as specified by properties of the JobRunRequest.
     * @param jobName the id of the job to run
     * @param JobParameters the launch key/value pair set
     * @return the Spring Batch execution entity
     * @throws Exception if unable to start the job within the Spring Batch engine.
     */
    JobExecution runJob(String jobName, JobParameters jobParameters) throws Exception;

    /**
     * Resume a stopped batch job. Requires that it already be in a STOPPED or FAILED status,
     * but makes no attempt to verify this before attempting to restart it.
     * @param jobExecutionId of the job to be resumed
     * @return the job execution ID of the restarted job
     * @throws Exception on restart errors, like its already completed, or it cannot be resumed because it is in an incorrect state.
     */
    Long restartJob(long jobExecutionId) throws Exception;

    /**
     * Perform a graceful stop of a job by stopping it after its current step has finished executing.
     * @param jobExecutionId identifies the job to be stopped.
     * @throws Exception on any stop failure
     */
    void stopJob(long jobExecutionId) throws Exception;

    /**
     * Create a set of Job parameters at runtime from dynamic data.
     * These parameters will include the user who ran the job, their email address, and a job timestamp.
     * @param jobRequest request used to start the book generating job
     * @return a set of Spring Batch job parameters used in launching a job.
     */
    JobParameters createDynamicJobParameters(JobRequest jobRequest);

    void setTaskExecutorCoreThreadPoolSize(int coreThreadPoolSize);
}
