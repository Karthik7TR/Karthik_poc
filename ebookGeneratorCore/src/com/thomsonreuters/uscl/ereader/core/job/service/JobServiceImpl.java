package com.thomsonreuters.uscl.ereader.core.job.service;

import java.util.ArrayList;
import java.util.List;

import com.thomsonreuters.uscl.ereader.core.job.dao.JobDao;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobFilter;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobSort;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobSummary;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

public class JobServiceImpl implements JobService
{
    //private static final Logger log = LogManager.getLogger(JobServiceImpl.class);
    private JobDao dao;
    private JobExplorer jobExplorer;

    @Override
    public List<JobSummary> findJobSummary(final List<Long> jobExecutionIds)
    {
        return dao.findJobSummary(jobExecutionIds);
    }

    @Override
    @Transactional(readOnly = true)
    public JobExecution findJobExecution(final long jobExecutionId)
    {
        return jobExplorer.getJobExecution(jobExecutionId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobExecution> findJobExecutions(final List<Long> jobExecutionIds)
    {
        final List<JobExecution> jobExecutions = new ArrayList<>();
        for (final Long jobExecutionId : jobExecutionIds)
        {
            final JobExecution jobExecution = jobExplorer.getJobExecution(jobExecutionId);
            if (jobExecution != null)
            {
                jobExecutions.add(jobExecution);
            }
        }
        return jobExecutions;
    }

    @Override
    public List<JobExecution> findJobExecutions(final JobInstance jobInstance)
    {
        final List<JobExecution> jobExecutions = jobExplorer.getJobExecutions(jobInstance);
        return jobExecutions;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> findJobExecutions(final JobFilter filter, final JobSort sort)
    {
        return dao.findJobExecutions(filter, sort);
    }

    @Override
    public JobInstance findJobInstance(final long jobInstanceId)
    {
        return jobExplorer.getJobInstance(jobInstanceId);
    }

    @Override
    public StepExecution findStepExecution(final long jobExecutionId, final long stepExecutionId)
    {
        final StepExecution stepExecution = jobExplorer.getStepExecution(jobExecutionId, stepExecutionId);
        return stepExecution;
    }

    @Override
    @Transactional(readOnly = true)
    public int getStartedJobCount()
    {
        return dao.getStartedJobCount();
    }

    @Required
    public void setJobDao(final JobDao dao)
    {
        this.dao = dao;
    }

    @Required
    public void setJobExplorer(final JobExplorer explorer)
    {
        jobExplorer = explorer;
    }
}
