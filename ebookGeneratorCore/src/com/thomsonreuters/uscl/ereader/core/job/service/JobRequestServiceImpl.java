/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.core.job.service;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

import com.thomsonreuters.uscl.ereader.core.job.dao.JobRequestDao;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobRequest;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobRequestRunOrderComparator;

public class JobRequestServiceImpl implements JobRequestService {

	//private static final Logger log = Logger.getLogger(JobRequestServiceImpl.class);
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
	@Transactional(readOnly = true)
	public JobRequest getNextJobToExecute() {
		List<JobRequest> jobs = findAllJobRequests();
		// It is assumed that the findAll...() returns the job requests is ascending run order.
		JobRequest jobRequest = (jobs.size() > 0) ? jobs.get(0) : null; 
		return jobRequest;
	}

	@Override
	@Transactional
	public void deleteJobByJobId(long jobRequestId) {
		
		jobRequestDao.deleteJobByJobId(jobRequestId);
		
	}

	@Override
	@Transactional
	public void updateJobPriority(long jobRequestId, int jobPriority) {
		jobRequestDao.updateJobPriority(jobRequestId, jobPriority);
		
	}

	@Override
	@Transactional(readOnly = true)
	public boolean isBookInJobRequest(long ebookDefinitionId) {
		boolean isBookInRequest = false; 
		JobRequest jobRequest = jobRequestDao.getJobRequestByBookDefinitionId(ebookDefinitionId);
		if(jobRequest != null ){
			isBookInRequest = true;
		}
		return isBookInRequest;
	}

	@Override
	@Transactional(readOnly = true)
	public JobRequest getJobRequestByBookDefinationId(long ebookDefinitionId) {
		return jobRequestDao.getJobRequestByBookDefinitionId(ebookDefinitionId);
	}

	@Override
	@Transactional
	public Long saveQueuedJobRequest(long ebookDefinitionId, String version, int priority, String submittedBy) {
		JobRequest jobRequest = JobRequest.createQueuedJobRequest(ebookDefinitionId, version, priority, submittedBy);
		jobRequest.setSubmittedAt(new Date());
		return jobRequestDao.saveJobRequest(jobRequest);
	}

	@Required
	public void setJobRequestDao(JobRequestDao jobRequestDao) {
		this.jobRequestDao = jobRequestDao;
	}
}
