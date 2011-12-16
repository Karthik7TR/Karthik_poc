/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.service;

import java.util.Date;
import java.util.List;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;

public interface DashboardService {
	
	/**
	 * Return all executions associated with a job
	 * @param jobName name of job to find
	 * @return a list of job executions, possibly empty if jobName is not found or blank, never null
	 */
	public List<JobExecution> findAllJobExecutions(String jobName);
	
	/**
	 * Find list of job execution ID's that match the supplied criteria. 
	 * @param jobName the name of the job
	 * @param startTime job executions on and after this time are returned
	 * @param batchStatus the job status for the execution
	 * @return a list of job execution ID's
	 */
	public List<Long> findJobExecutionIds(String jobName, Date startTime, BatchStatus batchStatus);
	
	/**
	 * Get the job executions with the specified list of primary keys, quietly skips entry if no record found for a key.
	 * @param executionIds array of execution ID primary keys, may be empty, but not null
	 * @return the list of job executions for the specified keys.
	 */
	public List<JobExecution> findJobExecutionByPrimaryKey(List<Long> executionIds);
	
	/**
	 * Delete job metadata from the repository that is older than (before) the specified date.
	 * @param jobsBefore delete all jobs before this date.
	 */
	public void jobCleaner(Date jobsBefore);
}
