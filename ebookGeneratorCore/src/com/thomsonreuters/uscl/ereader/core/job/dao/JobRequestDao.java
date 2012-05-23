/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.core.job.dao;

import java.util.List;

import com.thomsonreuters.uscl.ereader.core.job.domain.JobRequest;

public interface JobRequestDao {

	public void deleteJobRequest(long jobRequestId);

	/**
	 * Returns all rows of the job request table, in no particular order.
	 */
	public List<JobRequest> findAllJobRequests();

	/**
	 * Find a job request object by its primary key
	 * @param jobRequestId the primary key
	 * @return the object found or null if not found
	 */
	public JobRequest findByPrimaryKey(long jobRequestId);
	
	public JobRequest findJobRequestByBookDefinitionId(long ebookDefinitionId); 
	
	public Long saveJobRequest(JobRequest jobRequest);
	
	public void updateJobPriority(long jobRequestId, int jobPriority);

	public List<JobRequest> findAllJobRequestsOrderByPriorityAndSubmitedtime();
}
