package com.thomsonreuters.uscl.ereader.core.job.dao;

import java.util.Date;
import java.util.List;

import com.thomsonreuters.uscl.ereader.core.job.domain.JobRequest;

public interface JobRequestDao {
	
	/**
	 * gets next job to execute , order by job priority ,job subSubmited date 
	 * @return
	 */
	public JobRequest getNextJobToExecute();
	public void deleteJobByJobId(long jobRequestId);
	public void updateJobPriority(long jobRequestId ,int jobPriority); 
	public void saveJobRequest(JobRequest jobRequest);
	public JobRequest findJobRequestByRequestId(long jobRequestId);
	
	public List<JobRequest> getAllJobRequests();
	public JobRequest getJobRequestByBookDefinationId(long ebookDefinitionId);
	public List<JobRequest> getAllJobRequestsBy(String jobStatus,
			int jobPriority, Date jobScheduledTime, String jobSubmittersName);

}
