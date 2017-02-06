/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.core.job.service;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.job.dao.JobRequestDao;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobRequest;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobRequestRunOrderComparator;

public class JobRequestServiceImpl implements JobRequestService {

	//private static final Logger log = LogManager.getLogger(JobRequestServiceImpl.class);
	private static final Comparator<JobRequest> RUN_ORDER_COMPARATOR = new JobRequestRunOrderComparator();

	public JobRequestDao jobRequestDao;
	
	@Override
	@Transactional(readOnly = true)
	public List<JobRequest> findAllJobRequests() {
		List<JobRequest> jobRequestList = jobRequestDao.findAllJobRequests();
		Collections.sort(jobRequestList, RUN_ORDER_COMPARATOR);
		return jobRequestList;
	}
	
	@Override
	@Transactional(readOnly = true)
	public JobRequest findByPrimaryKey(long id) {
		JobRequest jobRequest = jobRequestDao.findByPrimaryKey(id);
		return jobRequest;
	}

	@Override
	@Transactional(isolation=Isolation.SERIALIZABLE)
	public JobRequest getNextJobToExecute() {
		List<JobRequest> jobs = jobRequestDao.findAllJobRequestsOrderByPriorityAndSubmitedtime();//findAllJobRequests();
		// It is assumed that the findAllJobRequests() returns the job requests in the order in which they will run.
		// So take advantage of this and return the first one, which should be the next job to be launched.
		JobRequest jobRequest = (jobs.size() > 0) ? jobs.get(0) : null; 
		// Delete the job just picked up otherwise we can have two different pollers pick up the same job from the table
		// causing the same job to be launched twice.
		if (jobRequest != null) {
			jobRequestDao.deleteJobRequest(jobRequest.getJobRequestId());
		}
		return jobRequest;
	}

	@Override
	@Transactional
	public void updateJobPriority(long jobRequestId, int jobPriority) {
		jobRequestDao.updateJobPriority(jobRequestId, jobPriority);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean isBookInJobRequest(long bookDefinitionId) {
		return (findJobRequestByBookDefinitionId(bookDefinitionId) != null);
	}

	@Override
	@Transactional(readOnly = true)
	public JobRequest findJobRequestByBookDefinitionId(long bookDefinitionId) {
		return jobRequestDao.findJobRequestByBookDefinitionId(bookDefinitionId);
	}

	@Override
	@Transactional
	public Long saveQueuedJobRequest(BookDefinition bookDefinition, String version, int priority, String submittedBy) {
		JobRequest jobRequest = JobRequest.createQueuedJobRequest(bookDefinition, version, priority, submittedBy);
		jobRequest.setSubmittedAt(new Date());
		return jobRequestDao.saveJobRequest(jobRequest);
	}

	@Required
	public void setJobRequestDao(JobRequestDao jobRequestDao) {
		this.jobRequestDao = jobRequestDao;
	}
}
