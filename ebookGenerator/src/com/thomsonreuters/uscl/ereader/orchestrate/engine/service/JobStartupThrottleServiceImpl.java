package com.thomsonreuters.uscl.ereader.orchestrate.engine.service;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.thomsonreuters.uscl.ereader.core.job.domain.JobThrottleConfig;
import lombok.Setter;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class covers Throttle behavior for spring batch where before starting new job , jobRepository is queried to find
 * how many jobs are currently running and what is current throttle limit. if number of jobs running are more or
 * equal to throttle Limit. Each running job is verified if they have crossed throttle step, total number of such jobs
 * which have not crossed throttle limit is considered to decide if new job should be allowed to launch.
 */
@Service("jobStartupThrottleService")
public class JobStartupThrottleServiceImpl implements JobStartupThrottleService {
    private final JobExplorer jobExplorer;
    private final JobRepository jobRepository;

    /** Note that is is mutable and can be changed on the fly
      * Used to update the configuration which can be changed on the fly.
      * The SyncJobThrottleConfigSerice will invoke this method.**/
    @Setter(onMethod_ = {@Override})
    private JobThrottleConfig jobThrottleConfig;

    @Autowired
    public JobStartupThrottleServiceImpl(final JobExplorer jobExplorer, final JobRepository jobRepository) {
        this.jobExplorer = jobExplorer;
        this.jobRepository = jobRepository;
    }

    /**
     * Before starting new job check with Spring Batch Job Repository to find if new job can be
     * launched with out breaking throttle limit.
     */
    @Override
    @Transactional
    public boolean checkIfnewJobCanbeLaunched() {
        // Return true immediately if this step check is not enabled
        if (!jobThrottleConfig.isStepThrottleEnabled()) {
            return true;
        }

        final long runningJobsCount = Optional.ofNullable(jobExplorer.getJobNames())
            .map(Collection::stream)
            .orElseGet(Stream::empty)
            .map(this::getRunningJobExecutions)
            .flatMap(Collection::stream)
            .count();

        return runningJobsCount < jobThrottleConfig.getThrottleStepMaxJobs();
    }

    private Collection<JobExecution> getRunningJobExecutions(final String jobName) {
        /**
         * we found that for some reason jobExplorer.findRunningJobExecutions
         * can return even failed jobs . so to get actual number of jobs running we are iterating
         * over return jobs are verifying if they are not failed.
         */
        return Optional.ofNullable(jobExplorer.findRunningJobExecutions(jobName))
            .map(Collection::stream)
            .orElseGet(Stream::empty)
            .filter(JobExecution::isRunning)
            .filter(jobExecution -> !ExitStatus.FAILED.getExitCode().equalsIgnoreCase(jobExecution.getExitStatus().getExitCode()))
            .filter(jobExecution -> !isThrottleStepCompleted(jobName, jobExecution))
            .collect(Collectors.toList());
    }

    private boolean isThrottleStepCompleted(final String jobName, final JobExecution jobExecution) {
        final String throttleStepName = jobThrottleConfig.getThrottleStepName(jobName);
        final JobInstance jobInstance = jobExecution.getJobInstance();
        return Objects.nonNull(jobRepository.getLastStepExecution(jobInstance, throttleStepName));
    }
}
