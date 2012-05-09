package com.thomsonreuters.uscl.ereader.core.job.service;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.thomsonreuters.uscl.ereader.core.job.domain.JobThrottleConfig;

/**
 * Perform the system startup boot load of the job throttle configuration.
 * Its properties are then subsequently modifiable via the Manager administration page.
 *
 */
public class JobThrottleConfigLoader {
	private static Logger log = Logger.getLogger(JobThrottleConfigLoader.class);
	private JobThrottleConfigService jobThrottleConfigService;
	private JobThrottleConfig jobThrottleConfig;
	private ThreadPoolTaskExecutor springBatchTaskExecutor;
	
	public JobThrottleConfigLoader(JobThrottleConfigService service, JobThrottleConfig config,
								   ThreadPoolTaskExecutor springBatchTaskExecutor) {
		this.jobThrottleConfigService = service;
		this.jobThrottleConfig = config;
		this.springBatchTaskExecutor = springBatchTaskExecutor;
	}
	
	@PostConstruct
	public void loadJobThrottleConfiguration() throws Exception {
		JobThrottleConfig configurationReadFromDatabase = jobThrottleConfigService.getThrottleConfig();
		jobThrottleConfig.sync(configurationReadFromDatabase);
		log.info("Successfully loaded: " + jobThrottleConfig);
		// Reset the Spring Batch task executor with the core thread pool size read from the database as part of the job throttle config.
		springBatchTaskExecutor.setCorePoolSize(jobThrottleConfig.getCoreThreadPoolSize());
	}
}
