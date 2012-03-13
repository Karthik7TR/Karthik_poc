package com.thomsonreuters.uscl.ereader.core.job.dao;

import java.util.Date;
import java.util.List;

import com.thomsonreuters.uscl.ereader.core.job.domain.JobFilter;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobSort;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobSummary;

public interface JobDao {
	
	
	public List<JobSummary> findJobSummary(List<Long> jobExecutionIds);
	
	/**
	 * Get the job execution ID's from the JOB_EXECUTION table that match the specified
	 * filter criteria and sorted as specified.
	 * @return a list of job execution IDs from the JOB_EXECUTION table.
	 */
	public List<Long> findJobExecutions(JobFilter filter, JobSort sort);
	
	/**
	 * Delete all Spring Batch job data before the specified date.
	 * @param deleteJobDataBefore job data before this point in time will be removed.
	 */
	public void deleteJobsBefore(Date deleteJobDataBefore);
}
