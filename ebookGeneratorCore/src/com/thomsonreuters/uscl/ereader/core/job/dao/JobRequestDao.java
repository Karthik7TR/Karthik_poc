/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.core.job.dao;

import java.util.List;

import com.thomsonreuters.uscl.ereader.core.job.domain.JobRequest;

/**
 * 
 * @author Mahendra Survase U0105927
 * 
 */
public interface JobRequestDao {
	
	/**
	 * gets next job to execute , order by job priority ,job subSubmited date 
	 * @return
	 */
	public List<JobRequest> findAllJobRequests();
	public JobRequest findByPrimaryKey(long jobRequestId);
	
	public JobRequest getNextJobToExecute();
	public void deleteJobByJobId(long jobRequestId);
	public void updateJobPriority(long jobRequestId, int jobPriority); 
	public Long saveJobRequest(JobRequest jobRequest);
	
	public JobRequest getJobRequestByBookDefinationId(long ebookDefinitionId);
//	public List<JobRequest> getAllJobRequestsBy(String jobStatus,
//			int jobPriority, Date jobScheduledTime, String jobSubmittersName);

}
