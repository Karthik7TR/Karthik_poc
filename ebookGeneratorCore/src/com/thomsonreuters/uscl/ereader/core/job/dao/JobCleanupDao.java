/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.core.job.dao;

/**
 * @author <a href="mailto:Mahendra.Survase@thomsonreuters.com">Mahendra Survase</a> u0105927
 */
import java.util.List;

/**
 * Queries for fetching and update Spring Batch job information.
 *   
 * @author <a href="mailto:Mahendra.Survase@thomsonreuters.com">Mahendra Survase</a> u0105927
 */
public interface JobCleanupDao {

	/**
	 * Update dead job exit status to "failed".  
	 * @return
	 */
	public int updateBatchJobExecution();

	/**
	 * Update dead steps exit status to "failed".  
	 * @return
	 */
	public int updateBatchStepExecution();

	/**
	 * Gets list of dead jobs , so that the job owners could be notified to resubmit these jobs.   
	 * @return
	 */
	public List<String> findListOfDeadJobs();

	/**
	 * Update dead step exit status to 'failed' for given server name
	 * @param serverName
	 * @return
	 */
	public int updateBatchStepExecutionForGivenServer(String serverName);
	
	/**
	 * Update dead job exit status to 'failed' for given server name
	 * @param serverName
	 * @return
	 */
	public int updateBatchJobExecutionForGivenServer(String serverName);

	/**
	 * Gets list of dead jobs for given serverName, so that the job owners could be notified to resubmit these jobs.   
	 * @return
	 */
	public List<String> findListOfDeadJobsByServerName(String serverName);

}
