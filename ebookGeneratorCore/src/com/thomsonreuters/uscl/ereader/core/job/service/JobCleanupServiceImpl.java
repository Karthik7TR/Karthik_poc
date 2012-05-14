/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.core.job.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.thomsonreuters.uscl.ereader.core.job.dao.JobCleanupDao;

/**
 * Updates dead jobs exit status to "failed" status. 
 * @author <a href="mailto:Mahendra.Survase@thomsonreuters.com">Mahendra Survase</a> u0105927
 */
public  class JobCleanupServiceImpl implements JobCleanupService {

	private static final Logger LOG = Logger.getLogger(JobCleanupServiceImpl.class);

	public JobCleanupDao jobCleanupDao;

	/**
	 * Clean up dead jobs from both BatchStepExecution and BatchJobExecution tables 
	 * dead jobs are those jobs which were in "UNKNOWN" exit status due to server shutdown. 
	 */
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void cleanUpDeadJobs(){
		
		int stepCleanup = jobCleanupDao.updateBatchStepExecution();
		int jobCleanup = jobCleanupDao.updateBatchJobExecution();
		
		LOG.debug(String.format("Updated %d steps and %d jobs.", stepCleanup, jobCleanup));
		
	}

	/**
	 * Gets list of dead jobs so that jobs owner could be notified to resubmit these jobs.  
	 */
	@Override
	public List<String> findListOfDeadJobs(){
		return jobCleanupDao.findListOfDeadJobs();
		
	}
	
	@Required
	public void setJobCleanupDao(JobCleanupDao jobCleanupDao) {
		this.jobCleanupDao = jobCleanupDao;
	}

	/**
	 * Clean up dead jobs from both BatchStepExecution and BatchJobExecution tables for given server name 
	 * 
	 * @see com.thomsonreuters.uscl.ereader.core.job.service.JobCleanupService#cleanUpDeadJobsForGivenServer(java.lang.String)
	 */
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void cleanUpDeadJobsForGivenServer(String serverName){
		
		int stepCleanup = jobCleanupDao.updateBatchStepExecutionForGivenServer(serverName);
		int jobCleanup = jobCleanupDao.updateBatchJobExecutionForGivenServer(serverName);
		LOG.debug(String.format("Updated %d steps and %d jobs.", stepCleanup, jobCleanup));
	}

	@Override
	public List<String> findListOfDeadJobsByServerName(String serverName) 
	{
		return jobCleanupDao.findListOfDeadJobsByServerName(serverName);

		
	}
	
	
}
