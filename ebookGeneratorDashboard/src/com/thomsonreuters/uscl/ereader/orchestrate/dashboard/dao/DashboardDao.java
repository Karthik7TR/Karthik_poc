package com.thomsonreuters.uscl.ereader.orchestrate.dashboard.dao;

import java.util.Date;
import java.util.List;

import org.springframework.batch.core.JobExecution;

public interface DashboardDao {
	
	/**
	 * Find list of job execution ID's that match the job name and example criteria.
	 * @return a list of job execution ID's
	 */
	public List<Long> findJobExecutionIds(String jobName, JobExecution filter);
	
	/**
	 * Delete all job meta-data before the specified date.
	 * @param jobsBefore job data before this date will be removed.
	 */
	public void deleteJobsBefore(Date jobsBefore);

}
