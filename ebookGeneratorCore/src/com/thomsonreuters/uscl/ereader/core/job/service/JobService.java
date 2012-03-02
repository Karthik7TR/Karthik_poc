package com.thomsonreuters.uscl.ereader.core.job.service;

import java.util.List;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.StepExecution;

import com.thomsonreuters.uscl.ereader.core.job.domain.JobFilter;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobInstanceBookInfo;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobSort;

public interface JobService {
	
	/**
	 * Find a job execution by its primary key.
	 * @param jobExecutionId primary key
	 * @return the found execution, or null if not found
	 */
	public JobExecution findJobExecution(long jobExecutionId);
	
	public List<JobExecution> findJobExecutions(List<Long> jobExecutionIds);
	
	public List<Long> findJobExecutions(JobFilter filter, JobSort sort);
	
	public List<JobExecution> findJobExecutions(JobInstance jobInstance);
	
	public JobInstance findJobInstance(long jobInstanceId);
	/**
	 * Get the specific set of book information that is required when presenting a job instance or execution.
	 * This includes the book name an its title ID.
	 * @param jobInstanceId primary key for the job instance.
	 * @return subset of the book definition properties used for a specific job instance.
	 */
	public JobInstanceBookInfo findJobInstanceBookInfo(long jobInstanceId);
	
	public StepExecution findStepExecution(long jobExecutionId, long stepExecutionId);
	
	/**
	 * Returns the a job with the given title id is currently running.
	 * @param titleId the fully qualified title ID
	 * @return true if there is a job running with this titleId
	 */
	public boolean isJobRunning(String titleId);
	
}
