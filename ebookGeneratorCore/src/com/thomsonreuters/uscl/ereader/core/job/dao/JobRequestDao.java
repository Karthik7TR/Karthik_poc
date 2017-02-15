package com.thomsonreuters.uscl.ereader.core.job.dao;

import java.util.List;

import com.thomsonreuters.uscl.ereader.core.job.domain.JobRequest;

public interface JobRequestDao
{
    void deleteJobRequest(long jobRequestId);

    /**
     * Returns all rows of the job request table, in no particular order.
     */
    List<JobRequest> findAllJobRequests();

    /**
     * Find a job request object by its primary key
     * @param jobRequestId the primary key
     * @return the object found or null if not found
     */
    JobRequest findByPrimaryKey(long jobRequestId);

    JobRequest findJobRequestByBookDefinitionId(long ebookDefinitionId);

    Long saveJobRequest(JobRequest jobRequest);

    void updateJobPriority(long jobRequestId, int jobPriority);

    List<JobRequest> findAllJobRequestsOrderByPriorityAndSubmitedtime();
}
