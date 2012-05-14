/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

/**
 * 
 * @author <a href="mailto:Mahendra.Survase@thomsonreuters.com">Mahendra Survase</a> u0105927
 */
package com.thomsonreuters.uscl.ereader.core.job.service;

import java.util.List;

public interface JobCleanupService {

	/**
	 * Clean up dead jobs from both BatchStepExecution and BatchJobExecution tables 
	 * dead jobs are those jobs which were in "UNKNOWN" exit status due to server shutdown. 
	 */
	public void cleanUpDeadJobs();

	/**
	 * Gets list of dead jobs caused by all the server instances,  so that jobs owner could be notified to resubmit these jobs.  
	 */
	public List<String> findListOfDeadJobs();

	/**
	 * Clean up dead jobs from both BatchStepExecution and BatchJobExecution tables for given server name 
	 * 
	 * @see com.thomsonreuters.uscl.ereader.core.job.service.JobCleanupService#cleanUpDeadJobsForGivenServer(java.lang.String)
	 */
	public void cleanUpDeadJobsForGivenServer(String serverName);
	/**
	 * Gets list of dead jobs caused by passed in the server instances,  so that jobs owner could be notified to resubmit these jobs.  
	 */
	public List<String> findListOfDeadJobsByServerName(String serverName);

}
