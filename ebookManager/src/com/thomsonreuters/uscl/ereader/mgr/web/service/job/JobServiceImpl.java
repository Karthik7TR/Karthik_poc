package com.thomsonreuters.uscl.ereader.mgr.web.service.job;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.thomsonreuters.uscl.ereader.core.job.dao.JobDao;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobFilter;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobSort;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobSummary;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service("jobService")
public class JobServiceImpl implements JobService {
    private final JobDao dao;
    private final JobExplorer jobExplorer;

    @Autowired
    public JobServiceImpl(final JobDao dao, final JobExplorer jobExplorer) {
        this.dao = dao;
        this.jobExplorer = jobExplorer;
    }

    @Override
    public List<JobSummary> findJobSummary(final List<Long> jobExecutionIds) {
        return dao.findJobSummary(jobExecutionIds);
    }

    @Override
    @Transactional(readOnly = true)
    public JobExecution findJobExecution(final long jobExecutionId) {
        try {
            return jobExplorer.getJobExecution(jobExecutionId);
        } catch (Exception e) {
            log.warn("unable to get JobExecution {}", jobExecutionId, e);
            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobExecution> findJobExecutions(final List<Long> jobExecutionIds) {
        final List<JobExecution> jobExecutions = new ArrayList<>();
        for (final Long jobExecutionId : jobExecutionIds) {
            final JobExecution jobExecution = jobExplorer.getJobExecution(jobExecutionId);
            if (jobExecution != null) {
                jobExecutions.add(jobExecution);
            }
        }
        return jobExecutions;
    }

    @Override
    public List<JobExecution> findJobExecutions(final JobInstance jobInstance) {
        try {
            return jobExplorer.getJobExecutions(jobInstance);
        } catch (Exception e) {
            log.warn("unable to get JobInstance {}", jobInstance, e);
            return Collections.emptyList();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> findJobExecutions(final JobFilter filter, final JobSort sort) {
        return dao.findJobExecutions(filter, sort);
    }

    @Override
    public JobInstance findJobInstance(final long jobInstanceId) {
        return jobExplorer.getJobInstance(jobInstanceId);
    }

    @Override
    public StepExecution findStepExecution(final long jobExecutionId, final long stepExecutionId) {
        final StepExecution stepExecution = jobExplorer.getStepExecution(jobExecutionId, stepExecutionId);
        return stepExecution;
    }

    @Override
    @Transactional(readOnly = true)
    public int getStartedJobCount() {
        return dao.getStartedJobCount();
    }
}
