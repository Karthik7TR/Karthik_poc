/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.core.job.service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.thomsonreuters.uscl.ereader.core.job.dao.JobRequestDao;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobRequest;

/**
 * 
 * @author U0105927
 *
 */
public class JobRequestServiceImpl implements JobRequestService{

	private static final Logger log = Logger.getLogger(JobRequestServiceImpl.class);

	public JobRequestDao jobRequestDao;
	
	
	@Override
	public JobRequest getNextJobToExecute() {

		JobRequest jobRequest = jobRequestDao.getNextJobToExecute();
		
		return jobRequest;
	}

	@Override
	public void deleteJobByJobId(long jobRequestId) {
		
		jobRequestDao.deleteJobByJobId(jobRequestId);
		
	}

	@Override
	public void updateJobPriority(long jobRequestId, String jobPriority) {
		
		jobRequestDao.updateJobPriority(jobRequestId, convertJobPriorityToInt(jobPriority));
		
	}

	@Override
	public boolean isBookInJobRequest(long ebookDefinaitionId) {
		boolean isBookInRequest = false; 
		JobRequest jobRequest = jobRequestDao.getJobRequestByBookDefinationId(ebookDefinaitionId);
		if(jobRequest != null ){
			isBookInRequest = true;
		}
		return isBookInRequest;
	}


	@Override
	public JobRequest getJobRequestByBookDefinationId(long ebookDefinitionId) {
		return jobRequestDao.getJobRequestByBookDefinationId(ebookDefinitionId);
	}

	@Override
	public void saveJobRequest(long ebookDefinitionId,
			String ebookVersionSubmitted, String jobStatus, String jobPriority,
			Timestamp jobScheduledTime, String jobSubmittersName) {
		
		jobStatus = jobStatusSetup(jobScheduledTime);
		int jobPrioriyInt = convertJobPriorityToInt(jobPriority);
		JobRequest jobRequest = new JobRequest();
		jobRequest.setBookVersionSubmited(ebookVersionSubmitted);
		jobRequest.setEbookDefinitionId(ebookDefinitionId);
		jobRequest.setJobStatus(jobStatus);
		jobRequest.setJobPriority(jobPrioriyInt);
		jobRequest.setJobSubmittersName(jobSubmittersName);

		java.util.Date date= new java.util.Date();

		jobRequest.setJobSubmitTimestamp(new Timestamp(date.getTime()));
		jobRequest.setJobScheduleTimeStamp(jobScheduledTime);
		jobRequestDao.saveJobRequest(jobRequest);
		
	}
	

	@Override
	public List<JobRequest> getAllJobRequests() {
		
		List<JobRequest> jobRequestList = null;
		jobRequestList = jobRequestDao.getAllJobRequests();
		return jobRequestList;
		
	}

	@Override
	public List<JobRequest> getAllJobRequestsBy(String jobStatus,
			String jobPriority, Date jobScheduledTime, String jobSubmittersName) {
		
		List<JobRequest> jobRequestList = null;
		int jobPriorityInt = convertJobPriorityToInt(jobPriority);
		
		jobRequestList = jobRequestDao.getAllJobRequestsBy(jobStatus,
				jobPriorityInt,  jobScheduledTime, jobSubmittersName);
		
		return jobRequestList;
		
	}

	
	/**
	 * Temporary method need to replace this method with enum.
	 * @param jobPriority
	 * @return
	 */
	private int convertJobPriorityToInt(String jobPriority){
		int jobPriorityInt = 2; // default priority.
		
		if(jobPriority.equalsIgnoreCase("high")){
			jobPriorityInt = 1;
		}
		
		if(jobPriority.equalsIgnoreCase("normal")){
			jobPriorityInt = 2;
		}
		
		return jobPriorityInt;
	}
	
	private String jobStatusSetup(Date scheduledDate){
		String status = "Queue";
		if(scheduledDate == null ){
			status = "Queue";
		}
		if(scheduledDate  != null ){
			status = "Scheduled";
		}
		
		return status;
	}
	
	@Required
	public void setJobRequestDao(JobRequestDao jobRequestDao) {
		this.jobRequestDao = jobRequestDao;
	}
	
	
	
	public enum Priority {
		High, Normal ;

		int eval(String jobPriority){
		switch(this) {
		case High: return 1;
		case Normal: return 2;
		}
		throw new AssertionError("Unknown job priority value " + this);
		}
	}









}
