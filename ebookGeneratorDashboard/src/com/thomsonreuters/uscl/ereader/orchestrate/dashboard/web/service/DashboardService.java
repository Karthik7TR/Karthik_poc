package com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

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
	
	/**
	 * Get the set of books for which a job is launched.
	 * Key=the title ID, like "FL_2011_LOCAL" the value is the proper name
	 * Value=The natural language book name, like "Flordia Rule of Court 2011 local volume 2".
	 * @return a map of book name strings.
	 */
	public Map<String,String> getBooks();
	
	/**
	 * Returns the natural languange title of the book for a give book id.
	 * @param bookId the title identifier
	 * @return the descriptive title of the book or null if not found
	 */
	public String getBookTitle(String bookId);
}
