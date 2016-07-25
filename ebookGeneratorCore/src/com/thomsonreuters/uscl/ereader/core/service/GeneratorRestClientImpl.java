/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.core.service;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.client.RestTemplate;

import com.thomsonreuters.uscl.ereader.core.CoreConstants;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobThrottleConfig;
import com.thomsonreuters.uscl.ereader.core.job.domain.MiscConfig;
import com.thomsonreuters.uscl.ereader.core.job.domain.SimpleRestServiceResponse;

public class GeneratorRestClientImpl implements GeneratorRestClient {
	//private static final Logger log = LogManager.getLogger(GeneratorRestServiceClientImpl.class);
	
	private static final String GENERATOR_REST_STOP_JOB_URL_PATTERN =
							"{context}/service/stop/job/{jobExecutionId}";
	private static final String GENERATOR_REST_RESTART_JOB_URL_PATTERN =
							"{context}/service/restart/job/{jobExecutionId}";
	private static final String GENERATOR_REST_GET_STEP_NAMES_PATTERN =
							"{context}/service/get/step/names";	
	
	private static final String GENERATOR_GET_JOB_THROTTLE_CONFIG =
						"{context}/"+CoreConstants.URI_GET_JOB_THROTTLE_CONFIG;
	private static final String GENERATOR_GET_MISC_CONFIG =
						"{context}/"+CoreConstants.URI_GET_MISC_CONFIG;

	/** Used to invoke the REST  job stop and restart operations on the ebookGenerator. */
	private RestTemplate restTemplate;
	/** The root web application context URL for the ebook generator. */
	private URL generatorContextUrl;
	
	@Override
	public SimpleRestServiceResponse restartJob(long jobExecutionId) {
		
		SimpleRestServiceResponse response = (SimpleRestServiceResponse) 
					restTemplate.getForObject(GENERATOR_REST_RESTART_JOB_URL_PATTERN,
					SimpleRestServiceResponse.class,
					generatorContextUrl.toString(), jobExecutionId);
		return response;
	}
	
	@Override
	public SimpleRestServiceResponse stopJob(long jobExecutionId) {
		SimpleRestServiceResponse response = (SimpleRestServiceResponse)
					restTemplate.getForObject(GENERATOR_REST_STOP_JOB_URL_PATTERN,
					SimpleRestServiceResponse.class,
					generatorContextUrl.toString(), jobExecutionId);
		return response;
	}
	
	@Override
	public JobThrottleConfig getJobThrottleConfig() {
		JobThrottleConfig config = (JobThrottleConfig)
					restTemplate.getForObject(GENERATOR_GET_JOB_THROTTLE_CONFIG,
					JobThrottleConfig.class,
					generatorContextUrl.toString());
		return config;
	}
	
	@Override
	public MiscConfig getMiscConfig() {
		MiscConfig config = (MiscConfig)
					restTemplate.getForObject(GENERATOR_GET_MISC_CONFIG,
					MiscConfig.class,
					generatorContextUrl.toString());
		return config;
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
	public URL getGeneratorContextUrl() {
		return generatorContextUrl;
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
