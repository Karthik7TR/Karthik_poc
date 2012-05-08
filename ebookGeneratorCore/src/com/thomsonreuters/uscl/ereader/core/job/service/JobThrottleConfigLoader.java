package com.thomsonreuters.uscl.ereader.core.job.service;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;

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
	
	public JobThrottleConfigLoader(JobThrottleConfigService service, JobThrottleConfig config) {
		this.jobThrottleConfigService = service;
		this.jobThrottleConfig = config;
	}
	
	@PostConstruct
	public void loadJobThrottleConfiguration() throws Exception {
		JobThrottleConfig configurationReadFromDatabase = jobThrottleConfigService.getThrottleConfig();
		jobThrottleConfig.copy(configurationReadFromDatabase);
		log.info("Successfully completed the startup load of the job throttle configuration: " + jobThrottleConfig);
	}
}
