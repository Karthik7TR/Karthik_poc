package com.thomsonreuters.uscl.ereader.core.service;

import java.net.URL;
import java.util.List;

import com.thomsonreuters.uscl.ereader.core.job.domain.JobThrottleConfig;
import com.thomsonreuters.uscl.ereader.core.job.domain.MiscConfig;
import com.thomsonreuters.uscl.ereader.core.job.domain.SimpleRestServiceResponse;

/**
 * REST service operations offered by the generator web application.
 *
 */
public interface GeneratorRestClient {

	public SimpleRestServiceResponse restartJob(long jobExecutionId);
		
	public SimpleRestServiceResponse stopJob(long jobExecutionId);
	
	public List<String> getStepNames();
	
	public JobThrottleConfig getJobThrottleConfig();
	
	public MiscConfig getMiscConfig();
	
	public URL getGeneratorContextUrl();
	
}
