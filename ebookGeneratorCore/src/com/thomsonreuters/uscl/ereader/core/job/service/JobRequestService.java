/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.core.job.service;

import java.util.List;

import com.thomsonreuters.uscl.ereader.core.job.domain.JobRequest;

public interface JobRequestService {
	
	public List<JobRequest> findAllJobRequests();
	public JobRequest findByPrimaryKey(long jobReqeustId);
	
	public JobRequest getNextJobToExecute();
	public void deleteJobByJobId(long jobRequestId);
	public void updateJobPriority(long jobRequestId, int priority);
	public Long saveQueuedJobRequest(long ebookDefinitionId, String version, int priority, String submittedBy);
	public boolean isBookInJobRequest(long ebookDefinaitionId);
	
	public JobRequest getJobRequestByBookDefinationId(long ebookDefinitionId);
	
//    public List<JobRequest> getAllJobRequestsBy(String jobStatus, int jobPriority, Date jobScheduledTime, String jobSubmittersName);

    
	
	
	

}

