/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.core.job.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

import com.thomsonreuters.uscl.ereader.core.job.dao.JobRequestDao;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobRequest;

/**
 * 
 * @author U0105927
 *
 */
public class JobRequestServiceImpl implements JobRequestService {

	//private static final Logger log = Logger.getLogger(JobRequestServiceImpl.class);

	public JobRequestDao jobRequestDao;
	
	@Override
	@Transactional(readOnly = true)
	public List<JobRequest> findAllJobRequests() {
		List<JobRequest> jobRequestList = jobRequestDao.findAllJobRequests();
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

		JobRequest jobRequest = jobRequestDao.getNextJobToExecute();
		
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
	public boolean isBookInJobRequest(long ebookDefinaitionId) {
		boolean isBookInRequest = false; 
		JobRequest jobRequest = jobRequestDao.getJobRequestByBookDefinationId(ebookDefinaitionId);
		if(jobRequest != null ){
			isBookInRequest = true;
		}
		return isBookInRequest;
	}


	@Override
	@Transactional(readOnly = true)
	public JobRequest getJobRequestByBookDefinationId(long ebookDefinitionId) {
		return jobRequestDao.getJobRequestByBookDefinationId(ebookDefinitionId);
	}

	@Override
	@Transactional
	public Long saveQueuedJobRequest(long ebookDefinitionId, String version, int priority, String submittedBy) {
		JobRequest jobRequest = JobRequest.createQueuedJobRequest(ebookDefinitionId, version, priority, submittedBy);
		jobRequest.setSubmittedAt(new Date());
		return jobRequestDao.saveJobRequest(jobRequest);
	}
	



//	@Override
//	@Transactional(readOnly = true)
//	public List<JobRequest> getAllJobRequestsBy(int jobPriority, Date jobScheduledTime, String jobSubmittersName) {
//		
//		List<JobRequest> jobRequestList = null;
//		int jobPriorityInt = convertJobPriorityToInt(jobPriority);
//		
//		jobRequestList = jobRequestDao.getAllJobRequestsBy(jobStatus,
//				jobPriorityInt,  jobScheduledTime, jobSubmittersName);
//		
//		return jobRequestList;
//		
//	}

	
	/**
	 * Temporary method need to replace this method with enum.
	 * @param jobPriority
	 * @return
	 */
//	private int convertJobPriorityToInt(String jobPriority){
//		int jobPriorityInt = 2; // default priority.
//		
//		if(jobPriority.equalsIgnoreCase("high")){
//			jobPriorityInt = 1;
//		}
//		
//		if(jobPriority.equalsIgnoreCase("normal")){
//			jobPriorityInt = 2;
//		}
//		
//		return jobPriorityInt;
//	}
	
	@Required
	public void setJobRequestDao(JobRequestDao jobRequestDao) {
		this.jobRequestDao = jobRequestDao;
	}
	
	
	
//	public enum Priority {
//		High, Normal ;
//
//		int eval(String jobPriority){
//		switch(this) {
//		case High: return 1;
//		case Normal: return 2;
//		}
//		throw new AssertionError("Unknown job priority value " + this);
//		}
//	}

}
