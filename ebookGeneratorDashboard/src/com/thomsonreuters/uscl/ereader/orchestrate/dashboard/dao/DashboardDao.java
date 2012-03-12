package com.thomsonreuters.uscl.ereader.orchestrate.dashboard.dao;

import java.util.Date;
import java.util.List;

import org.springframework.batch.core.BatchStatus;

import com.thomsonreuters.uscl.ereader.orchestrate.core.BookDefinition;

public interface DashboardDao {
	
	public BookDefinition findBookDefinition(String titleID);
	
	/**
	 * Returns all the current book definitions.
	 * @return a list of BookDefinition
	 */
	public List<BookDefinition> findAllBookDefinitions();
	
	/**
	 * Find list of job execution ID's that match the supplied criteria. 
	 * @param jobName the name of the job
	 * @param startTime job executions on and after this time are returned
	 * @param batchStatus the job status for the execution
	 * @return a list of job execution ID's
	 */
	public List<Long> findJobExecutionIds(String jobName, Date startTime, BatchStatus batchStatus);
	
	/**
	 * Delete all job meta-data before the specified date.
	 * @param jobsBefore job data before this date will be removed.
	 */
	public void deleteJobsBefore(Date jobsBefore);
	


}
