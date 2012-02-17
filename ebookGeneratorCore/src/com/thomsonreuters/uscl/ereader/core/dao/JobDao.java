package com.thomsonreuters.uscl.ereader.core.dao;

import java.util.Date;
import java.util.List;

import com.thomsonreuters.uscl.ereader.core.domain.JobExecutionEntity;
import com.thomsonreuters.uscl.ereader.core.domain.JobFilter;

public interface JobDao {
	
	/**
	 * Get the job execution ID's that match the specified criteria.
	 * filter search criteria
	 * @return a list of JobExecutionEntity matching the filter/sort/paging criteria 
	 */
	public List<Long> findJobExecutionIds(JobFilter jobFilter);
	
//	public List<JobExecutionEntity> findJobExecutions(JobFilter jobFilter);
	
	/**
	 * Delete all job meta-data before the specified date.
	 * @param jobsBefore job data before this date will be removed.
	 */
	public void deleteJobsBefore(Date jobsBefore);

}
