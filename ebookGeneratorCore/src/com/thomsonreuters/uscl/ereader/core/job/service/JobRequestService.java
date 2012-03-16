/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.core.job.service;

import java.util.List;

import com.thomsonreuters.uscl.ereader.core.job.domain.JobRequest;

public interface JobRequestService {
	
	/**
	 * Fetch all the current job requests order by run order.
	 * @return a list of job request sorted into the order in which they will run.
	 */
	public List<JobRequest> findAllJobRequests();

	/**
	 * Fetch a job request by its primary key.
	 * @param jobReqeustId the primary key of the object.
	 * @return the job request with the specified key, or null if not found.
	 */
	public JobRequest findByPrimaryKey(long jobReqeustId);
	
	/**
	 * Returns the next queued job that is ready to run.
	 * @return a job request, or null if there are no jobs to run.
	 */
	public JobRequest getNextJobToExecute();
	public void deleteJobByJobId(long jobRequestId);
	public void updateJobPriority(long jobRequestId, int priority);
	public Long saveQueuedJobRequest(long ebookDefinitionId, String version, int priority, String submittedBy);
	public boolean isBookInJobRequest(long ebookDefinaitionId);
	
	public JobRequest getJobRequestByBookDefinationId(long ebookDefinitionId);
	
//    public List<JobRequest> getAllJobRequestsBy(String jobStatus, int jobPriority, Date jobScheduledTime, String jobSubmittersName);

    
	
	
	

}

