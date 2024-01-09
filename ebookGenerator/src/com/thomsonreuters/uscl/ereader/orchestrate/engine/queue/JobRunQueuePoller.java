package com.thomsonreuters.uscl.ereader.orchestrate.engine.queue;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.core.job.domain.JobRequest;
import com.thomsonreuters.uscl.ereader.core.job.service.JobNameProvider;
import com.thomsonreuters.uscl.ereader.core.job.service.JobRequestService;
import com.thomsonreuters.uscl.ereader.core.outage.domain.PlannedOutage;
import com.thomsonreuters.uscl.ereader.core.outage.service.OutageProcessor;
import com.thomsonreuters.uscl.ereader.orchestrate.engine.service.EngineService;
import com.thomsonreuters.uscl.ereader.orchestrate.engine.service.JobStartupThrottleService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * A regularly scheduled task to check the batch job run request queue(s) for
 * new job run request messages. If a message is present, and we can run the job
 * because less than the maximum number of concurrent batch jobs is running (not
 * throttled) then the specified job will be run.
 */
@Slf4j
public class JobRunQueuePoller {

    private EngineService engineService;
    private JobStartupThrottleService jobStartupThrottleService;
    private JobRequestService jobRequestService;
    private OutageProcessor outageProcessor;
    private ThreadPoolTaskExecutor springBatchTaskExecutor;
    private JobNameProvider jobNameProvider;
    @Resource(name = "dataSource")
    private BasicDataSource basicDataSource;

    @Scheduled(fixedDelay = 15000)
    public void pollJobQueue() {
        JobRequest jobRequest = null;
        try {
            /**
             * Do not consume a JOB_REQUEST record if:
             * 1) A planned outage is active, no jobs started until outage is over
             * 2) The current number of concurrent job threads equals the configured pool size in the ThreadPoolTaskExecutor.
             * 3) The application step specific rules prevent it from running.
             */
            final PlannedOutage outage = outageProcessor.processPlannedOutages();
            if (outage == null) {
                // Core pool size is the task executor pool size, effectively
                // the maximum number of concurrent jobs.
                // This is dynamic and can be changed through the administrative
                // UI.
                final int coreThreadPoolSize = springBatchTaskExecutor.getCorePoolSize();
                final int activeThreads = springBatchTaskExecutor.getActiveCount();
                if (activeThreads < coreThreadPoolSize) {
                    if (jobStartupThrottleService.checkIfnewJobCanbeLaunched()) {
                        jobRequest = jobRequestService.getNextJobToExecute();
                        if (jobRequest != null) {
                            // Create the dynamic set of launch parameters,
                            // things like
                            // user name, user email, and a unique serial number
                            final JobParametersBuilder builder = engineService.createDynamicJobParameters(jobRequest);
                            // Put the dynamic job parameters in the map.
                            final JobParameters allJobParameters = builder.toJobParameters();
                            // Start the job that builds the ebook
                            log.debug("Starting Job: " + jobRequest);
                            engineService.runJob(jobNameProvider.getJobName(jobRequest), allJobParameters);
                            if (log.isDebugEnabled()) {
                                log.debug(
                                    String.format(
                                        "THREAD POOL: activeCount=%d,poolSize=%d,corePoolSize=%d,maxPoolSize=%d",
                                        springBatchTaskExecutor.getActiveCount(),
                                        springBatchTaskExecutor.getPoolSize(),
                                        springBatchTaskExecutor.getCorePoolSize(),
                                        springBatchTaskExecutor.getMaxPoolSize()));
                                log.debug(
                                    String.format(
                                        "DB CONN POOL: numActive=%d,numIdle=%d,maxActive=%d,maxWait=%d",
                                        basicDataSource.getNumActive(),
                                        basicDataSource.getNumIdle(),
                                        basicDataSource.getMaxActive(),
                                        basicDataSource.getMaxWait()));
                            }
                        }
                    } else {
                        log.debug("Application step throttling is not allowing any new jobs to run.");
                    }
                } else {
                    log.debug(
                        String.format(
                            "There are %d active job threads running, the maximum allowed is %d.  No new jobs will be started until the active is less than the maximum.",
                            activeThreads,
                            coreThreadPoolSize));
                }
            } else {
                log.debug(String.format("A planned outage is in effect until %s", outage.getEndTime().toString()));
            }
        } catch (final Exception e) {
            log.error("Failed run job request: " + jobRequest, e);
        }
    }

    @Required
    public void setEngineService(final EngineService engineService) {
        this.engineService = engineService;
    }

    @Required
    public void setJobStartupThrottleService(final JobStartupThrottleService jobStartupThrottleService) {
        this.jobStartupThrottleService = jobStartupThrottleService;
    }

    @Required
    public void setOutageProcessor(final OutageProcessor processor) {
        outageProcessor = processor;
    }

    @Required
    public void setJobRequestService(final JobRequestService jobRequestService) {
        this.jobRequestService = jobRequestService;
    }

    @Required
    public void setSpringBatchTaskExecutor(final ThreadPoolTaskExecutor taskExecutor) {
        springBatchTaskExecutor = taskExecutor;
    }

    @Required
    public void setJobNameProvider(final JobNameProvider jobNameProvider) {
        this.jobNameProvider = jobNameProvider;
    }
}
