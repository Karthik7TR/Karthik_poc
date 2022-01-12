package com.thomsonreuters.uscl.ereader.core.job.service;

import java.util.List;

import com.thomsonreuters.uscl.ereader.core.job.dao.JobCleanupDao;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobUserInfo;
import com.thomsonreuters.uscl.ereader.util.EBookServerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Updates dead jobs exit status to "failed" status.
 * @author <a href="mailto:Mahendra.Survase@thomsonreuters.com">Mahendra Survase</a> u0105927
 */
@Service("jobCleanupService")
@Slf4j
public class JobCleanupServiceImpl implements JobCleanupService {

    private final JobCleanupDao jobCleanupDao;

    @Autowired
    public JobCleanupServiceImpl(final JobCleanupDao jobCleanupDao) {
        this.jobCleanupDao = jobCleanupDao;
    }

    /**
     * Clean up dead jobs from both BatchStepExecution and BatchJobExecution tables
     * dead jobs are those jobs which were in "UNKNOWN" exit status due to server shutdown.
     * @throws EBookServerException
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public void cleanUpDeadJobs() throws EBookServerException {
        final int stepCleanup = jobCleanupDao.updateBatchStepExecution();
        final int jobCleanup = jobCleanupDao.updateBatchJobExecution();
        log.debug(String.format("Updated %d steps and %d jobs.", stepCleanup, jobCleanup));
    }

    /**
     * Gets list of dead jobs so that jobs owner could be notified to resubmit these jobs.
     * @throws EBookServerException
     */
    @Override
    public List<JobUserInfo> findListOfDeadJobs() throws EBookServerException {
        return jobCleanupDao.findListOfDeadJobs();
    }

    /**
     * Clean up dead jobs from both BatchStepExecution and BatchJobExecution tables for given server name
     * @throws EBookServerException
     *
     * @see com.thomsonreuters.uscl.ereader.core.job.service.JobCleanupService#cleanUpDeadJobsForGivenServer(java.lang.String)
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public void cleanUpDeadJobsForGivenServer(final String serverName) throws EBookServerException {
        final int stepCleanup = jobCleanupDao.updateBatchStepExecutionForGivenServer(serverName);
        final int jobCleanup = jobCleanupDao.updateBatchJobExecutionForGivenServer(serverName);
        log.debug(String.format("Updated %d steps and %d jobs for host %s.", stepCleanup, jobCleanup, serverName));
    }

    @Override
    public List<JobUserInfo> findListOfDeadJobsByServerName(final String serverName) throws EBookServerException {
        return jobCleanupDao.findListOfDeadJobsByServerName(serverName);
    }
}
