package com.thomsonreuters.uscl.ereader.core.job.dao;

/**
 * @author <a href="mailto:Mahendra.Survase@thomsonreuters.com">Mahendra Survase</a> u0105927
 */
import java.util.List;

import com.thomsonreuters.uscl.ereader.core.job.domain.JobUserInfo;
import com.thomsonreuters.uscl.ereader.util.EBookServerException;

/**
 * Queries for fetching and update Spring Batch job information.
 *
 * @author <a href="mailto:Mahendra.Survase@thomsonreuters.com">Mahendra Survase</a> u0105927
 */
public interface JobCleanupDao {
    /**
     * Update dead job exit status to "failed".
     * @return
     * @throws EBookServerException
     */
    int updateBatchJobExecution() throws EBookServerException;

    /**
     * Update dead steps exit status to "failed".
     * @return
     * @throws EBookServerException
     */
    int updateBatchStepExecution() throws EBookServerException;

    /**
     * Gets list of dead jobs , so that the job owners could be notified to resubmit these jobs.
     * @return
     */
    List<JobUserInfo> findListOfDeadJobs() throws EBookServerException;

    /**
     * Update dead step exit status to 'failed' for given server name
     * @param serverName
     * @return
     * @throws EBookServerException
     */
    int updateBatchStepExecutionForGivenServer(String serverName) throws EBookServerException;

    /**
     * Update dead job exit status to 'failed' for given server name
     * @param serverName
     * @return
     * @throws EBookServerException
     */
    int updateBatchJobExecutionForGivenServer(String serverName) throws EBookServerException;

    /**
     * Gets list of dead jobs for given serverName, so that the job owners could be notified to resubmit these jobs.
     * @return
     * @throws EBookServerException
     */
    List<JobUserInfo> findListOfDeadJobsByServerName(String serverName) throws EBookServerException;
}
