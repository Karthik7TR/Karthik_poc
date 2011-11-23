package com.thomsonreuters.uscl.ereader.orchestrate.engine.service;

import org.springframework.batch.core.JobParameters;

public interface EngineService {
	
	/**
	 * Load job launch parameters from a database table.
	 * @param jobName job to load launch parameters for, lookup key in table
	 * @return a map of the job parameters
	 */
	public JobParameters loadJobParameters(String jobName);

}
