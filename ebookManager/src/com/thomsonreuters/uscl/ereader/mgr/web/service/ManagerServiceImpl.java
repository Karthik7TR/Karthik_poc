package com.thomsonreuters.uscl.ereader.mgr.web.service;

import java.net.InetSocketAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.client.RestTemplate;

import com.thomsonreuters.uscl.ereader.core.job.domain.JobOperationResponse;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobThrottleConfig;

public class ManagerServiceImpl implements ManagerService {
	//private static final Logger log = Logger.getLogger(ManagerServiceImpl.class);
	private static String GENERATOR_REST_STOP_JOB_URL_PATTERN =
							"{context}/service/stop/job/{jobExecutionId}";
	private static String GENERATOR_REST_RESTART_JOB_URL_PATTERN =
							"{context}/service/restart/job/{jobExecutionId}";
	private static String GENERATOR_REST_GET_STEP_NAMES_PATTERN =
							"{context}/service/get/step/names";	

	/** Used to invoke the REST  job stop and restart operations on the ebookGenerator. */
	private RestTemplate restTemplate;
	/** The root web application context URL for the ebook generator. */
	private URL generatorContextUrl;
	
	@Override
	public JobOperationResponse restartJob(long jobExecutionId) {
		
		JobOperationResponse response = (JobOperationResponse) 
					restTemplate.getForObject(GENERATOR_REST_RESTART_JOB_URL_PATTERN,
					JobOperationResponse.class,
					generatorContextUrl.toString(), jobExecutionId);
		return response;
	}
	
	@Override
	public JobOperationResponse stopJob(long jobExecutionId) {
		JobOperationResponse response = (JobOperationResponse)
					restTemplate.getForObject(GENERATOR_REST_STOP_JOB_URL_PATTERN,
					JobOperationResponse.class,
					generatorContextUrl.toString(), jobExecutionId);
		return response;
	}
	
	@Override
	public List<String> getStepNames() {
		String csvStepNames = (String)
					restTemplate.getForObject(GENERATOR_REST_GET_STEP_NAMES_PATTERN,
					String.class, generatorContextUrl.toString());
		ArrayList<String> stepNames = new ArrayList<String>();
		if (csvStepNames != null) {
			StringTokenizer tokenizer = new StringTokenizer(csvStepNames, ",");
			while (tokenizer.hasMoreTokens()) {
				String stepName = tokenizer.nextToken();
				stepNames.add(stepName);
			}
		}
		Collections.sort(stepNames);
		return stepNames;
	}
	
	@Override
	public JobOperationResponse pushJobThrottleConfiguration(InetSocketAddress socketAddr, JobThrottleConfig newJobThrottleConfig) {
		String url = String.format("http://%s:%d/ebookGenerator/service/update/job/throttle/config",
								   socketAddr.getHostName(), socketAddr.getPort());		
		JobOperationResponse response = (JobOperationResponse)
				restTemplate.postForObject(url, newJobThrottleConfig, JobOperationResponse.class);
		return response;
	}
	
	@Required
	public void setRestTemplate(RestTemplate template) {
		this.restTemplate = template;
	}
	@Required
	public void setGeneratorContextUrl(URL url) {
		this.generatorContextUrl = url;
	}
}
