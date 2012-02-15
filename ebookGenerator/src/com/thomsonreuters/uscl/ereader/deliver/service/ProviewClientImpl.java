/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.deliver.service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;

/**
 * This class is responsible for interacting with ProView via their REST interface.
 * @author u0081674
 *
 */
public class ProviewClientImpl implements ProviewClient {

	private static final Logger LOG = Logger.getLogger(ProviewClientImpl.class);
	private RestTemplate restTemplate;
	private String validationUriTemplate;
	private String publishTitleUriTemplate;
	private String getTitlesUriTemplate;
	private String publishingStatusUriTemplate;
	
	/* (non-Javadoc)
	 * @see com.thomsonreuters.uscl.ereader.deliver.service.ProviewClient#publishTitle(java.lang.String, java.lang.String, java.io.File)
	 */
	@Override
	public String publishTitle(final String fullyQualifiedTitleId, final String eBookVersionNumber, final File eBook) throws ProviewException {
		if (StringUtils.isBlank(fullyQualifiedTitleId)){
			throw new IllegalArgumentException("fullyQualifiedTitleId cannot be null or empty, but was [" + fullyQualifiedTitleId + "].");
		}
		if (StringUtils.isBlank(eBookVersionNumber)){
			throw new IllegalArgumentException("eBookVersionNumber must not be null or empty, but was [" + eBookVersionNumber + "].");
		}
		
		Map<String, String> urlParameters = new HashMap<String, String>();
		urlParameters.put("titleId", fullyQualifiedTitleId);
		urlParameters.put("eBookVersionNumber", eBookVersionNumber);
		
		restTemplate.put(publishTitleUriTemplate, eBook, urlParameters);
		
		//logResponse(response);
		
		return null;
	}
	
	@Override
	public String getAllPublishedTitles() throws ProviewException {
		Map<String, String> urlVariables = new HashMap<String, String>();
		
		//ResponseEntity responseEntity
		String response = restTemplate.execute(getTitlesUriTemplate, HttpMethod.GET, new ProviewRequestCallback() , new ProviewResponseExtractor());
		//logResponse(responseEntity);
		
		return response;
	}	

	@Override
	public String getPublishingStatus(String fullyQualifiedTitleId) throws ProviewException {
		if (StringUtils.isBlank(fullyQualifiedTitleId)) {
			throw new IllegalArgumentException("Cannot get publishing status for titleId: " + fullyQualifiedTitleId + ". The titleId must not be null or empty.");
		}
		
		Map<String, String> urlParameters = new HashMap<String, String>();
		urlParameters.put("titleId", fullyQualifiedTitleId);
		ResponseEntity responseEntity = restTemplate.getForEntity(publishingStatusUriTemplate, String.class, urlParameters);
		logResponse(responseEntity);
		
		return responseEntity.getBody().toString();
	}
	
	private void logResponse(final ResponseEntity responseEntity){
		LOG.debug("Response Headers: " + responseEntity.getHeaders().toSingleValueMap());
		LOG.debug("Response Body:" + responseEntity.getBody().toString());
	}
	
	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public void setValidationUriTemplate(String validationUriTemplate) {
		this.validationUriTemplate = validationUriTemplate;
	}

	public void setPublishTitleUriTemplate(String publishTitleUriTemplate) {
		this.publishTitleUriTemplate = publishTitleUriTemplate;
	}

	public void setGetTitlesUriTemplate(String getTitlesUriTemplate) {
		this.getTitlesUriTemplate = getTitlesUriTemplate;
	}

	public void setPublishingStatusUriTemplate(String publishingStatusUriTemplate) {
		this.publishingStatusUriTemplate = publishingStatusUriTemplate;
	}
	
	private class ProviewResponseExtractor implements ResponseExtractor<String> {
		@Override
		public String extractData(ClientHttpResponse response)
				throws IOException {
			return IOUtils.toString(response.getBody(), "UTF-8");
		}
	}
	
	private class ProviewRequestCallback implements RequestCallback {
		@Override
		public void doWithRequest(ClientHttpRequest clientHttpRequest) throws IOException {
			clientHttpRequest.getHeaders().add("Accept", "application/xml");
		}
	}
}
