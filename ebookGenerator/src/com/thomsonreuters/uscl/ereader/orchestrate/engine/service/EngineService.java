/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.orchestrate.engine.service;

import org.springframework.batch.core.JobExecution;

import com.thomsonreuters.uscl.ereader.orchestrate.core.JobRunRequest;

/**
 * Utilities for interacting with the Spring Batch engine used in starting, restarting, and stopping jobs.
 */
public interface EngineService {
	
	/**
	 * Start a new e-book generating job as specified by properties of the JobRunRequest.
	 * @param request identifies which e-book to create
	 * @return the Spring Batch execution entity
	 * @throws Exception if unable to start the job within the Spring Batch engine.
	 */
	public JobExecution runJob(JobRunRequest request) throws Exception;
	
	/**
	 * Resume a stopped batch job. Requires that it already be in a STOPPED or FAILED status,
	 * but makes no attempt to verify this before attempting to restart it.
	 * @param jobExecutionId of the job to be resumed
	 * @return the job execution ID of the restarted job
	 * @throws Exception on restart errors, like its already completed, or it cannot be resumed because it is in an incorrect state.
	 */
	public Long restartJob(long jobExecutionId) throws Exception;
	
	
	/**
	 * Perform a graceful stop of a job by stopping it after its current step has finished executing.
	 * @param jobExecutionId identifies the job to be stopped.
	 * @throws Exception on any stop failure
	 */
	public void stopJob(long jobExecutionId) throws Exception;

}
