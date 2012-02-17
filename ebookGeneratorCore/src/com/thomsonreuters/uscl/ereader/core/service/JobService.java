package com.thomsonreuters.uscl.ereader.core.service;

import java.util.List;

import org.springframework.batch.core.JobExecution;

import com.thomsonreuters.uscl.ereader.core.domain.JobExecutionEntity;
import com.thomsonreuters.uscl.ereader.core.domain.JobFilter;
import com.thomsonreuters.uscl.ereader.core.domain.JobParameterFilter;
import com.thomsonreuters.uscl.ereader.core.domain.JobSort;

public interface JobService {
	
	/**
	 * Find a job execution by its primary key.
	 * @param jobExecutionId primary key
	 * @return the found execution, or null if not found
	 */
	public JobExecution findJobExecution(Long jobExecutionId);
	
	public List<JobExecution> findJobExecutions(List<Long> jobExecutionIds);
	
	public List<JobExecution> findJobExecutions(JobFilter jobFilter, JobParameterFilter paramFilter);
	
	/**
	 * Returns the a job with the given title id is currently running.
	 * @param titleId the fully qualified title ID
	 * @return true if there is a job running with this titleId
	 */
	public boolean isJobRunning(String titleId);
	

	/**
	 * Get the job execution ID's that match the specified criteria.
	 * filter search criteria
	 * @return a list of JobExecutionEntity matching the filter/sort/paging criteria 
	 */
	public List<Long> findJobExecutionIds(JobFilter jobFilter);
	

}
