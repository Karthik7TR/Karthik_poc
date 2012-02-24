package com.thomsonreuters.uscl.ereader.core.job.dao;

import java.util.Date;
import java.util.List;

import com.thomsonreuters.uscl.ereader.core.job.domain.JobFilter;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobSort;

public interface JobDao {
	
	/**
	 * Get the job execution ID's from the JOB_EXECUTION table that match the specified
	 * filter criteria
	 * @return a list of job execution IDs from the JOB_EXECUTION table.
	 */
	public List<Long> findJobExecutions(JobFilter filter, JobSort sort);
	
	
	/**
	 * Delete all job meta-data before the specified date.
	 * @param jobsBefore job data before this date will be removed.
	 */
	public void deleteJobsBefore(Date jobsBefore);

}
