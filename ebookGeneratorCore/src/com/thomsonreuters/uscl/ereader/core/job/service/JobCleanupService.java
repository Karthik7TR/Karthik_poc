package com.thomsonreuters.uscl.ereader.core.job.service;

import java.util.List;

import com.thomsonreuters.uscl.ereader.core.job.domain.JobUserInfo;
import com.thomsonreuters.uscl.ereader.util.EBookServerException;

public interface JobCleanupService {
    /**
     * Clean up dead jobs from both BatchStepExecution and BatchJobExecution tables
     * dead jobs are those jobs which were in "UNKNOWN" exit status due to server shutdown.
     * @throws EBookServerException
     */
    void cleanUpDeadJobs() throws EBookServerException;

    /**
     * Gets list of dead jobs caused by all the server instances,  so that jobs owner could be notified to resubmit these jobs.
     * @throws EBookServerException
     */
    List<JobUserInfo> findListOfDeadJobs() throws EBookServerException;

    /**
     * Clean up dead jobs from both BatchStepExecution and BatchJobExecution tables for given server name
     * @throws EBookServerException
     *
     * @see com.thomsonreuters.uscl.ereader.core.job.service.JobCleanupService#cleanUpDeadJobsForGivenServer(java.lang.String)
     */
    void cleanUpDeadJobsForGivenServer(String serverName) throws EBookServerException;

    /**
     * Gets list of dead jobs caused by passed in the server instances,  so that jobs owner could be notified to resubmit these jobs.
     * @throws EBookServerException
     */
    List<JobUserInfo> findListOfDeadJobsByServerName(String serverName) throws EBookServerException;
}
