/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.core.job.service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import com.thomsonreuters.uscl.ereader.core.job.domain.JobRequest;

/**
 * 
 * @author U0105927
 *
 */
public interface JobRequestService {
	
	public JobRequest getNextJobToExecute();
	public void deleteJobByJobId(long jobRequestId);
	public void updateJobPriority(long jobRequestId, String jobPriority) ;
	public void saveJobRequest(long ebookDefinitionId,String ebookVersionSubmitted,String jobStatus, String jobPriority,Timestamp jobScheduledTime,String jobSubmittersName);
	public boolean isBookInJobRequest(long ebookDefinaitionId);
	
	public JobRequest getJobRequestByBookDefinationId(long ebookDefinitionId);
	public List<JobRequest> getAllJobRequests();
    public List<JobRequest> getAllJobRequestsBy(String jobStatus, String jobPriority, Date jobScheduledTime, String jobSubmittersName);

    
	
	
	

}

