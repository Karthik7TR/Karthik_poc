package com.thomsonreuters.uscl.ereader.mgr.web.service;

import java.net.URL;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.client.RestTemplate;

import com.thomsonreuters.uscl.ereader.core.job.domain.JobOperationResponse;

public class ManagerServiceImpl implements ManagerService {
	
	public static String GENERATOR_REST_STOP_JOB_URL_PATTERN = "{context}/service/stop/job/{jobInstanceId}";
	public static String GENERATOR_REST_RESTART_JOB_URL_PATTERN = "{context}/service/restart/job/{jobInstanceId}";
	
	private RestTemplate restTemplate;
	/** The root web application context URL for the ebook generator. */
	private URL generatorContextUrl;
	
	@Override
	public JobOperationResponse restartJob(long jobExecutionId) {
		JobOperationResponse response = restTemplate.getForObject(GENERATOR_REST_RESTART_JOB_URL_PATTERN,
																  JobOperationResponse.class,
																  generatorContextUrl.toString(), jobExecutionId);
		return response;
	}
	
	@Override
	public JobOperationResponse stopJob(long jobExecutionId) {
		JobOperationResponse response = restTemplate.getForObject(GENERATOR_REST_STOP_JOB_URL_PATTERN,
																  JobOperationResponse.class,
																  generatorContextUrl.toString(), jobExecutionId);
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
