package com.thomsonreuters.uscl.ereader.core.job.service;

import java.util.List;

import org.springframework.batch.core.JobExecution;

import com.thomsonreuters.uscl.ereader.core.job.domain.JobFilter;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobSort;

public interface JobService {
	
	/**
	 * Find a job execution by its primary key.
	 * @param jobExecutionId primary key
	 * @return the found execution, or null if not found
	 */
	public JobExecution findJobExecution(Long jobExecutionId);
	
	public List<JobExecution> findJobExecutions(List<Long> jobExecutionIds);
	
	
	public List<Long> findJobExecutions(JobFilter filter, JobSort sort);
	
	/**
	 * Returns the a job with the given title id is currently running.
	 * @param titleId the fully qualified title ID
	 * @return true if there is a job running with this titleId
	 */
	public boolean isJobRunning(String titleId);
	
}
