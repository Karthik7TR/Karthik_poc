package com.thomsonreuters.uscl.ereader.orchestrate.engine.service;

import java.util.List;
import java.util.Set;

import com.thomsonreuters.uscl.ereader.core.job.domain.JobThrottleConfig;
import com.thomsonreuters.uscl.ereader.core.job.service.AppConfigService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class covers Throttle behavior for spring batch where before starting new job , jobRepository is queried to find
 * how many jobs are currently running and what is current throttle limit. if number of jobs running are more or
 * equal to throttle Limit. Each running job is verified if they have crossed throttle step, total number of such jobs
 * which have not crossed throttle limit is considered to decide if new job should be allowed to launch.
 */
public class JobStartupThrottleServiceImpl implements JobStartupThrottleService {
    private static final Logger log = LogManager.getLogger(JobStartupThrottleServiceImpl.class);

    private JobExplorer jobExplorer;
    private JobRepository jobRepository;
    private AppConfigService appConfigService;

    /** Note that is is mutable and can be changed on the fly */
    private JobThrottleConfig jobThrottleConfig;

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

        boolean jobPullFlag = false;
        int jobNotDoneWithKeyStep = 0;
        Set<JobExecution> runningJobExecutions = null;

        final List<String> jobNames = jobExplorer.getJobNames();

        if (jobNames != null && jobNames.size() > 0) {
            // spring batch is capable of running multiple jobs but in current implementation we have one job with multiple instances.
            runningJobExecutions = jobExplorer.findRunningJobExecutions(jobNames.get(0));
        }
        if ((runningJobExecutions != null)
            && (runningJobExecutions.size() >= jobThrottleConfig.getThrottleStepMaxJobs())) {
            /**
             * we found that for some reason jobExplorer.findRunningJobExecutions
             * can return even failed jobs . so to get actual number of jobs running we are iterating
             * over return jobs are verifying if they are not failed.
             */
            for (final JobExecution jobExecution : runningJobExecutions) {
                if (jobExecution.isRunning()) {
                    final String currentExitCode = jobExecution.getExitStatus().getExitCode();
                    if (currentExitCode.equalsIgnoreCase(ExitStatus.FAILED.toString())) {
                        log.debug(
                            "jobExplorer.findRunningJobExecutions () returned filed job with jobId="
                                + jobExecution.getJobId());
                    } else {
                        final JobInstance jobInstance = jobExecution.getJobInstance();
                        // retrieve last step of execution
                        final StepExecution stepExecution =
                            jobRepository.getLastStepExecution(jobInstance, jobThrottleConfig.getThrottleStepName());

                        if (stepExecution == null) {
                            // job is not done with key step
                            jobNotDoneWithKeyStep++;
                        }
                    }
                }
            }
            if (jobNotDoneWithKeyStep < jobThrottleConfig.getThrottleStepMaxJobs()) {
                jobPullFlag = true;
            } else {
                jobPullFlag = false;
            }
        } else {
            jobPullFlag = true;
        }

        return jobPullFlag;
    }

    /**
     * Used to update the configuration which can be changed on the fly.
     * The SyncJobThrottleConfigSerice will invoke this method.
     */
    @Override
    public void setJobThrottleConfig(final JobThrottleConfig jobThrottleConfig) {
        this.jobThrottleConfig = jobThrottleConfig;
    }
}
