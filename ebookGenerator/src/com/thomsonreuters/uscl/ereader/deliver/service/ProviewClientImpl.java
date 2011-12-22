/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.deliver.service;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.http.entity.FileEntity;
import org.apache.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import com.thomsonreuters.uscl.ereader.deliver.rest.ProviewMessageConverter;

/**
 * This class is responsible for interacting with ProView via their REST interface.
 * @author u0081674
 *
 */
public class ProviewClientImpl implements ProviewClient {

	private static final Logger LOG = Logger.getLogger(ProviewClientImpl.class);
	private static final String USCL_PREFIX = "uscl/";
	private RestTemplate restTemplate;
	private String validationUriTemplate;
	private String publishingUriTemplate;
	private String getTitlesUriTemplate;
	private String publishingStatusUriTemplate;
	
	/* (non-Javadoc)
	 * @see com.thomsonreuters.uscl.ereader.deliver.service.ProviewClient#publishTitle(java.lang.String, java.lang.String, java.io.File)
	 */
	@Override
	public String publishTitle(final String contentTypeAbbreviation, final String titleId, final String eBookVersionNumber, final File eBook) throws ProviewException {
		if (StringUtils.isBlank(contentTypeAbbreviation)){
			throw new IllegalArgumentException("contentTypeAbbreviation must not be null or empty, but was [" + contentTypeAbbreviation + "].");
		}
		if (StringUtils.isBlank(titleId)){
			throw new IllegalArgumentException("titleId cannot be null or empty, but was [" + titleId + "].");
		}
		if (StringUtils.isBlank(eBookVersionNumber)){
			throw new IllegalArgumentException("eBookVersionNumber must not be null or empty, but was [" + eBookVersionNumber + "].");
		}
		
		Map<String, String> urlParameters = new HashMap<String, String>();
		urlParameters.put("contentTypeAbbreviation", contentTypeAbbreviation);
		urlParameters.put("titleId", titleId);
		urlParameters.put("eBookVersionNumber", eBookVersionNumber);
		
		//ResponseEntity<String> response = restTemplate.exchange(publishingUriTemplate,
		//  HttpMethod.PUT, new HttpEntity<File>(eBook), String.class, urlParameters);
		
		restTemplate.put(publishingUriTemplate, eBook, urlParameters);
		
		//logResponse(response);
		
		return null;
	}
	
	@Override
	public String getAllPublishedTitles() throws ProviewException {
		Map<String, String> urlVariables = new HashMap<String, String>();
		
		ResponseEntity responseEntity = restTemplate.getForEntity(getTitlesUriTemplate, String.class, urlVariables);
		logResponse(responseEntity);
		
		return responseEntity.getBody().toString();
	}	

	@Override
	public String getPublishingStatus(String contentTypeAbbreviation, String titleId) throws ProviewException {
		if (StringUtils.isBlank(titleId)) {
			throw new IllegalArgumentException("Cannot get publishing status for titleId: " + titleId + ". The titleId must not be null or empty.");
		}
		
		if (StringUtils.isBlank(contentTypeAbbreviation)) {
			throw new IllegalArgumentException("Cannot get publishing status for titleId: " + titleId + ". The contentTypeAbbreviation [cr, sc, an, ...] must not be null or empty.");
		}
		Map<String, String> urlParameters = new HashMap<String, String>();
		urlParameters.put("titleId", USCL_PREFIX + contentTypeAbbreviation + "/" + titleId);
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

	public void setPublishingUriTemplate(String publishingUriTemplate) {
		this.publishingUriTemplate = publishingUriTemplate;
	}

	public void setGetTitlesUriTemplate(String getTitlesUriTemplate) {
		this.getTitlesUriTemplate = getTitlesUriTemplate;
	}

	public void setPublishingStatusUriTemplate(String publishingStatusUriTemplate) {
		this.publishingStatusUriTemplate = publishingStatusUriTemplate;
	}
}
