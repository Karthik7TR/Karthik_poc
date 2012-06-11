package com.thomsonreuters.uscl.ereader.core.job.dao;

import java.util.List;

import com.thomsonreuters.uscl.ereader.core.job.domain.JobFilter;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobSort;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobSummary;

/**
 * Queries for fetching Spring Batch job information.
 */
public interface JobDao {
	
	public List<JobSummary> findJobSummary(List<Long> jobExecutionIds);
	
	/**
	 * Get the job execution ID's from the JOB_EXECUTION table that match the specified
	 * filter criteria and sorted as specified.
	 * @return a list of job execution IDs from the JOB_EXECUTION table.
	 */
	public List<Long> findJobExecutions(JobFilter filter, JobSort sort);
	
}
