package com.thomsonreuters.uscl.ereader.core.job.service;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.thomsonreuters.uscl.ereader.common.retry.Retry;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.CombinedBookDefinition;
import com.thomsonreuters.uscl.ereader.core.job.dao.JobRequestDao;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobRequest;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobRequestRunOrderComparator;
import org.hibernate.exception.GenericJDBCException;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

public class JobRequestServiceImpl implements JobRequestService {
    private static final Comparator<JobRequest> RUN_ORDER_COMPARATOR = new JobRequestRunOrderComparator();

    private JobRequestDao jobRequestDao;

    @Override
    @Transactional(readOnly = true)
    public List<JobRequest> findAllJobRequests() {
        final List<JobRequest> jobRequestList = jobRequestDao.findAllJobRequests();
        Collections.sort(jobRequestList, RUN_ORDER_COMPARATOR);
        return jobRequestList;
    }

    @Override
    @Transactional(readOnly = true)
    public JobRequest findByPrimaryKey(final long id) {
        final JobRequest jobRequest = jobRequestDao.findByPrimaryKey(id);
        return jobRequest;
    }

    @Override
    @Retry(propertyValue = "transactions.retry.count",
        exceptions = GenericJDBCException.class,
        delayProperty = "transactions.retry.delay.ms"
    )
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public JobRequest getNextJobToExecute() {
        final List<JobRequest> jobs = jobRequestDao.findAllJobRequestsOrderByPriorityAndSubmitedtime();
        // findAllJobRequests();
        // It is assumed that the findAllJobRequests() returns the job requests in the order in which they will run.
        // So take advantage of this and return the first one, which should be the next job to be launched.
        final JobRequest jobRequest = (jobs.size() > 0) ? jobs.get(0) : null;
        // Delete the job just picked up otherwise we can have two different pollers pick up the same job from the table
        // causing the same job to be launched twice.
        if (jobRequest != null) {
            jobRequestDao.deleteJobRequest(jobRequest.getJobRequestId());
        }
        return jobRequest;
    }

    @Override
    @Transactional
    public void updateJobPriority(final long jobRequestId, final int jobPriority) {
        jobRequestDao.updateJobPriority(jobRequestId, jobPriority);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isBookInJobRequest(final long bookDefinitionId) {
        return (findJobRequestByBookDefinitionId(bookDefinitionId) != null);
    }

    @Override
    @Transactional(readOnly = true)
    public JobRequest findJobRequestByBookDefinitionId(final long bookDefinitionId) {
        return jobRequestDao.findJobRequestByBookDefinitionId(bookDefinitionId);
    }

    @Override
    @Transactional
    public Long saveQueuedJobRequest(
        final BookDefinition bookDefinition,
        final String version,
        final int priority,
        final String submittedBy) {
        final JobRequest jobRequest = JobRequest.createQueuedJobRequest(bookDefinition, version, priority, submittedBy);
        jobRequest.setSubmittedAt(new Date());
        return jobRequestDao.saveJobRequest(jobRequest);
    }

    @Override
    @Transactional
    public Long saveQueuedJobRequest(
            final CombinedBookDefinition combinedBookDefinition,
            final String version,
            final int priority,
            final String submittedBy) {
        final JobRequest jobRequest = JobRequest.createQueuedJobRequest(combinedBookDefinition, version, priority, submittedBy);
        jobRequest.setSubmittedAt(new Date());
        return jobRequestDao.saveJobRequest(jobRequest);
    }

    @Required
    public void setJobRequestDao(final JobRequestDao jobRequestDao) {
        this.jobRequestDao = jobRequestDao;
    }

    @Override
    @Transactional
    public void cleanupQueue() {
        jobRequestDao.deleteAllJobRequests();
    }
}
