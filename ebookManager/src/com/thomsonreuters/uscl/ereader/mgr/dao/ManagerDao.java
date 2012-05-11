package com.thomsonreuters.uscl.ereader.mgr.dao;

import java.util.Date;

public interface ManagerDao {
	
	/**
	 * Archive step data to JOB_HISTORY table and delete all old Spring Batch job data before the specified date.
	 * @param jobsBefore job data before this date will be removed.
	 * @return the number of job step executions that were archived/deleted.
	 */
	public int archiveAndDeleteSpringBatchJobRecordsBefore(Date deleteBefore);

}
