/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.orchestrate.engine.queue;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Logger;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.thomsonreuters.uscl.ereader.core.job.domain.JobRequest;
import com.thomsonreuters.uscl.ereader.core.job.service.JobRequestService;
import com.thomsonreuters.uscl.ereader.orchestrate.engine.service.EngineService;
import com.thomsonreuters.uscl.ereader.orchestrate.engine.service.JobStartupThrottleService;

/**
 * A regularly scheduled task to check the batch job run request queue(s) for
 * new job run request messages. If a message is present, and we can run the job
 * because less than the maximum number of concurrent batch jobs is running (not
 * throttled) then the specified job will be run.
 */
public class JobRunQueuePoller {
	private static final Logger log = Logger.getLogger(JobRunQueuePoller.class);

	private EngineService engineService;
	private JobStartupThrottleService jobStartupThrottleService;
	private JobRequestService jobRequestService;
	private ThreadPoolTaskExecutor springBatchTaskExecutor;
	/** Maximum number of jobs allowed to run concurrently */
	private int maximumConcurrentJobs;
	@Resource(name = "dataSource")
	private BasicDataSource basicDataSource;

	@Scheduled(fixedDelay = 15000)
	public void pollJobQueue() {
//		log.debug("----- CHECKING FOR A JOB TO RUN ------");
		JobRequest jobRequest = null;
		try {
			/**
			 * Do not consume a JOB_REQUEST record if:
			 * 1) The current number of concurrent job threads equals the configured pool size
			 *    in the ThreadPoolTaskExecutor.
			 * 2) The application specific rules will prevent it from running.
			 */
			if (springBatchTaskExecutor.getActiveCount() < maximumConcurrentJobs) {
				if (jobStartupThrottleService.checkIfnewJobCanbeLaunched()) {
					jobRequest = jobRequestService.getNextJobToExecute();
					if (jobRequest != null) {
						// Create the dynamic set of launch parameters, things like
						// user name, user email, and a unique serial number
						JobParameters dynamicJobParameters = engineService
											.createDynamicJobParameters(jobRequest);
						// Put the dynamic job parameters in the map.
						Map<String, JobParameter> allJobParametersMap = new HashMap<String, JobParameter>(
														dynamicJobParameters.getParameters());
						JobParameters allJobParameters = new JobParameters(allJobParametersMap);
						// Start the job that builds the ebook
						log.debug("Starting Job: " + jobRequest);
						engineService.runJob(JobRequest.JOB_NAME_CREATE_EBOOK, allJobParameters);
						if (log.isDebugEnabled()) {
							log.debug(String.format("THREAD POOL: activeCount=%d,poolSize=%d,corePoolSize=%d,maxPoolSize=%d",
									springBatchTaskExecutor.getActiveCount(), springBatchTaskExecutor.getPoolSize(),
									springBatchTaskExecutor.getCorePoolSize(), springBatchTaskExecutor.getMaxPoolSize()));
							log.debug(String.format("DB CONN POOL: numActive=%d,numIdle=%d,maxActive=%d,maxWait=%d",
									basicDataSource.getNumActive(), basicDataSource.getNumIdle(),
									basicDataSource.getMaxActive(), basicDataSource.getMaxWait()));
						}
					} else {
//						log.debug("No job request was found in the job queue.");
					}
				} else {
					log.debug("Application throttling does not allow any new jobs to be run.");
				}
			} else {
				log.debug(String.format("The maximum allowed number of concurrent jobs (%d) are running.  No new jobs may be started.", maximumConcurrentJobs));
			}
		} catch (Exception e) {
			log.error("Failed run job request: " + jobRequest, e);
		}
	}

	@Required
	public void setEngineService(EngineService engineService) {
		this.engineService = engineService;
	}

	@Required
	public void setJobStartupThrottleService(
			JobStartupThrottleService jobStartupThrottleService) {
		this.jobStartupThrottleService = jobStartupThrottleService;
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
	public void setMaximumConcurrentJobs(int maxJobs) {
		this.maximumConcurrentJobs = maxJobs;
	}
}
