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
	@Resource(name = "dataSource")
	private BasicDataSource basicDataSource;
	@Resource(name = "springBatchTaskExecutor")
	private ThreadPoolTaskExecutor springBatchTaskExecutor;

	@Scheduled(fixedRate = 15000)
	public void pollJobQueue() {
//log.debug("----- POLLING FOR NEW JOB ------");
		try {
			// Check if number of jobs running are below throttle limit.
			if (jobStartupThrottleService.checkIfnewJobCanbeLaunched()) {
				JobRequest jobRequest = jobRequestService.getNextJobToExecute();
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
					jobRequestService.deleteJobRequest(jobRequest.getPrimaryKey());
					if (log.isDebugEnabled()) {
						log.debug(String.format("DB CONN POOL: numActive=%d,numIdle=%d,maxActive=%d,maxWait=%d",
								basicDataSource.getNumActive(), basicDataSource.getNumIdle(),
								basicDataSource.getMaxActive(), basicDataSource.getMaxWait()));
						log.debug(String.format("THREAD POOL: activeCount=%d,poolSize=%d,corePoolSize=%d",
								springBatchTaskExecutor.getActiveCount(), springBatchTaskExecutor.getPoolSize(),
								springBatchTaskExecutor.getCorePoolSize()));
					}
				}
			}
		} catch (Exception e) {
			log.error("Failed to fetch job run request from launch input queue", e);
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
}
