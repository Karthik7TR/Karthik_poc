package com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.service;

import java.util.Date;
import java.util.List;

import org.springframework.batch.core.JobExecution;

public interface DashboardService {
	
	/**
	 * Return all executions associated with a job
	 * @param jobName name of job to find
	 * @return a list of job executions, possibly empty if jobName is not found or blank, never null
	 */
	public List<JobExecution> findAllJobExecutions(String jobName);
	
	
	/**
	 * Returns a list of the job execution ID's of a given job that are ((job start >= startTime) && (job start < endTime)).
	 * @param startTime returns ID's that are greater than or equal to this time
	 * @param filter, the template object for a query by example
	 * @return list of job execution ID
	 */
	public List<Long> findJobExecutionIds(String jobName, JobExecution filter);
	
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
