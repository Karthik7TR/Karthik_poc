package com.thomsonreuters.uscl.ereader.core.job.service;

import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobRequest;

public interface JobRequestService
{
    /**
     * Fetch all the current job requests order by run order.
     * @return a list of job request sorted into the order in which they will run.
     */
    List<JobRequest> findAllJobRequests();

    /**
     * Fetch a job request by its primary key.
     * @param jobReqeustId the primary key of the object.
     * @return the job request with the specified key, or null if not found.
     */
    JobRequest findByPrimaryKey(long jobReqeustId);

    /**
     * Returns the next queued job (if any) that is ready to run, removing it from the job request queue table.
     * @return a job request, or null if there are no jobs to run.
     */
    JobRequest getNextJobToExecute();

    void updateJobPriority(long jobRequestId, int priority);

    Long saveQueuedJobRequest(BookDefinition bookDefinition, String version, int priority, String submittedBy);

    /**
     * Returns true if there is any job run request that is for the specified book definition.
     * @param bookDefinitionId primary key of a book definition
     * @return true if there is a queued publishing job that is for the specified book definition, false otherwise.
     */
    boolean isBookInJobRequest(long bookDefinitionId);

    JobRequest findJobRequestByBookDefinitionId(long ebookDefinitionId);
}
