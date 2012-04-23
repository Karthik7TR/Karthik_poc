/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.core.job.service;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.thomsonreuters.uscl.ereader.core.job.dao.JobCleanupDao;

/**
 * Updates dead jobs exit status to "failed" status. 
 * @author <a href="mailto:Mahendra.Survase@thomsonreuters.com">Mahendra Survase</a> u0105927
 */
public class JobCleanupServiceImpl implements JobCleanupService {

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
		
		LOG.debug("Number of steps updated ="+stepCleanup +" and number of jobs updated ="+jobCleanup );
		
	}

	/**
	 * Gets list of dead jobs so that jobs owner could be notified to resubmit these jobs.  
	 */
	@Override
	public ArrayList<String> findListOfDeadJobs(){
		return jobCleanupDao.findListOfDeadJobs();
		
	}
	
	@Required
	public void setJobCleanupDao(JobCleanupDao jobCleanupDao) {
		this.jobCleanupDao = jobCleanupDao;
	}

	
}
