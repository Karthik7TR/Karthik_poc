/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.orchestrate.engine.queue;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.thomsonreuters.uscl.ereader.core.job.domain.JobRequest;
import com.thomsonreuters.uscl.ereader.core.job.service.JobNameProvider;
import com.thomsonreuters.uscl.ereader.core.job.service.JobRequestService;
import com.thomsonreuters.uscl.ereader.core.outage.domain.PlannedOutage;
import com.thomsonreuters.uscl.ereader.core.outage.service.OutageProcessor;
import com.thomsonreuters.uscl.ereader.orchestrate.engine.service.EngineService;
import com.thomsonreuters.uscl.ereader.orchestrate.engine.service.JobStartupThrottleService;

/**
 * A regularly scheduled task to check the batch job run request queue(s) for
 * new job run request messages. If a message is present, and we can run the job
 * because less than the maximum number of concurrent batch jobs is running (not
 * throttled) then the specified job will be run.
 */
public class JobRunQueuePoller {
	private static final Logger LOG = LogManager.getLogger(JobRunQueuePoller.class);

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
			PlannedOutage outage = outageProcessor.processPlannedOutages();
			if (outage == null) {
				// Core pool size is the task executor pool size, effectively
				// the maximum number of concurrent jobs.
				// This is dynamic and can be changed through the administrative
				// UI.
				int coreThreadPoolSize = springBatchTaskExecutor.getCorePoolSize();
				int activeThreads = springBatchTaskExecutor.getActiveCount();
				if (activeThreads < coreThreadPoolSize) {
					if (jobStartupThrottleService.checkIfnewJobCanbeLaunched()) {
						jobRequest = jobRequestService.getNextJobToExecute();
						if (jobRequest != null) {
							// Create the dynamic set of launch parameters,
							// things like
							// user name, user email, and a unique serial number
							JobParameters dynamicJobParameters = engineService.createDynamicJobParameters(jobRequest);
							// Put the dynamic job parameters in the map.
							Map<String, JobParameter> allJobParametersMap = new HashMap<>(
									dynamicJobParameters.getParameters());
							JobParameters allJobParameters = new JobParameters(allJobParametersMap);
							// Start the job that builds the ebook
							LOG.debug("Starting Job: " + jobRequest);
							engineService.runJob(jobNameProvider.getJobName(jobRequest), allJobParameters);
							if (LOG.isDebugEnabled()) {
								LOG.debug(String.format(
										"THREAD POOL: activeCount=%d,poolSize=%d,corePoolSize=%d,maxPoolSize=%d",
										springBatchTaskExecutor.getActiveCount(), springBatchTaskExecutor.getPoolSize(),
										springBatchTaskExecutor.getCorePoolSize(),
										springBatchTaskExecutor.getMaxPoolSize()));
								LOG.debug(String.format("DB CONN POOL: numActive=%d,numIdle=%d,maxActive=%d,maxWait=%d",
										basicDataSource.getNumActive(), basicDataSource.getNumIdle(),
										basicDataSource.getMaxActive(), basicDataSource.getMaxWait()));
							}
						}
					} else {
						LOG.debug("Application step throttling is not allowing any new jobs to run.");
					}
				} else {
					LOG.debug(String.format(
							"There are %d active job threads running, the maximum allowed is %d.  No new jobs will be started until the active is less than the maximum.",
							activeThreads, coreThreadPoolSize));
				}
			} else {
				LOG.debug(String.format("A planned outage is in effect until %s", outage.getEndTime().toString()));
			}
		} catch (Exception e) {
			LOG.error("Failed run job request: " + jobRequest, e);
		}
	}

	@Required
	public void setEngineService(EngineService engineService) {
		this.engineService = engineService;
	}

	@Required
	public void setJobStartupThrottleService(JobStartupThrottleService jobStartupThrottleService) {
		this.jobStartupThrottleService = jobStartupThrottleService;
	}

	@Required
	public void setOutageProcessor(OutageProcessor processor) {
		this.outageProcessor = processor;
	}

	@Required
	public void setJobRequestService(JobRequestService jobRequestService) {
		this.jobRequestService = jobRequestService;
	}

	@Required
	public void setSpringBatchTaskExecutor(ThreadPoolTaskExecutor taskExecutor) {
		this.springBatchTaskExecutor = taskExecutor;
	}

	@Required
	public void setJobNameProvider(JobNameProvider jobNameProvider) {
		this.jobNameProvider = jobNameProvider;
	}
}
