/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.deliver.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import com.thomsonreuters.uscl.ereader.deliver.rest.ProviewRequestCallback;
import com.thomsonreuters.uscl.ereader.deliver.rest.ProviewRequestCallbackFactory;
import com.thomsonreuters.uscl.ereader.deliver.rest.ProviewResponseExtractorFactory;

/**
 * This class is responsible for interacting with ProView via their REST
 * interface.
 * 
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
	private String deleteTitleUriTemplate;
	private String removeTitleUriTemplate;
	private String promoteTitleUriTemplate;

	private ProviewRequestCallbackFactory proviewRequestCallbackFactory;
	private ProviewResponseExtractorFactory proviewResponseExtractorFactory;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.thomsonreuters.uscl.ereader.deliver.service.ProviewClient#publishTitle
	 * (java.lang.String, java.lang.String, java.io.File)
	 */
	@Override
	public String publishTitle(final String fullyQualifiedTitleId,
			final String eBookVersionNumber, final File eBook)
			throws ProviewException {
		if (StringUtils.isBlank(fullyQualifiedTitleId)) {
			throw new IllegalArgumentException(
					"fullyQualifiedTitleId cannot be null or empty, but was ["
							+ fullyQualifiedTitleId + "].");
		}
		if (StringUtils.isBlank(eBookVersionNumber)) {
			throw new IllegalArgumentException(
					"eBookVersionNumber must not be null or empty, but was ["
							+ eBookVersionNumber + "].");
		}
		FileInputStream ebookInputStream;

		try {
			ebookInputStream = new FileInputStream(eBook);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(
					"Cannot send a null or empty File to ProView.", e);
		}

		Map<String, String> urlParameters = new HashMap<String, String>();
		urlParameters.put("titleId", fullyQualifiedTitleId);
		urlParameters.put("eBookVersionNumber", eBookVersionNumber);

		ProviewRequestCallback proviewRequestCallback = proviewRequestCallbackFactory
				.getRequestCallback();
		proviewRequestCallback.setEbookInputStream(ebookInputStream);

		String proviewResponse = restTemplate.execute(publishTitleUriTemplate,
				HttpMethod.PUT, proviewRequestCallback,
				proviewResponseExtractorFactory.getResponseExtractor(),
				urlParameters);

		// restTemplate.put(publishTitleUriTemplate, eBook, urlParameters);

		// logResponse(response);

		return proviewResponse;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.thomsonreuters.uscl.ereader.deliver.service.ProviewClient#deleteTitle
	 * (java.lang.String, java.lang.String)
	 */
	public String deleteTitle(final String fullyQualifiedTitleId,
			final String eBookVersionNumber) throws ProviewException {
		if (StringUtils.isBlank(fullyQualifiedTitleId)) {
			throw new IllegalArgumentException(
					"fullyQualifiedTitleId cannot be null or empty, but was ["
							+ fullyQualifiedTitleId + "].");
		}
		if (StringUtils.isBlank(eBookVersionNumber)) {
			throw new IllegalArgumentException(
					"eBookVersionNumber must not be null or empty, but was ["
							+ eBookVersionNumber + "].");
		}

		Map<String, String> urlParameters = new HashMap<String, String>();
		urlParameters.put("titleId", fullyQualifiedTitleId);
		urlParameters.put("eBookVersionNumber", eBookVersionNumber);

		ProviewRequestCallback proviewRequestCallback = proviewRequestCallbackFactory
				.getRequestCallback();

		String proviewResponse = restTemplate.execute(deleteTitleUriTemplate,
				HttpMethod.DELETE, proviewRequestCallback,
				proviewResponseExtractorFactory.getResponseExtractor(),
				urlParameters);

		return proviewResponse;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.thomsonreuters.uscl.ereader.deliver.service.ProviewClient#removeTitle
	 * (java.lang.String, java.lang.String)
	 */
	public String removeTitle(final String fullyQualifiedTitleId,
			final String eBookVersionNumber) throws ProviewException {
		if (StringUtils.isBlank(fullyQualifiedTitleId)) {
			throw new IllegalArgumentException(
					"fullyQualifiedTitleId cannot be null or empty, but was ["
							+ fullyQualifiedTitleId + "].");
		}
		if (StringUtils.isBlank(eBookVersionNumber)) {
			throw new IllegalArgumentException(
					"eBookVersionNumber must not be null or empty, but was ["
							+ eBookVersionNumber + "].");
		}

		Map<String, String> urlParameters = new HashMap<String, String>();
		urlParameters.put("titleId", fullyQualifiedTitleId);
		urlParameters.put("eBookVersionNumber", eBookVersionNumber);

		ProviewRequestCallback proviewRequestCallback = proviewRequestCallbackFactory
				.getRequestCallback();

		String proviewResponse = restTemplate.execute(removeTitleUriTemplate,
				HttpMethod.PUT, proviewRequestCallback,
				proviewResponseExtractorFactory.getResponseExtractor(),
				urlParameters);

		return proviewResponse;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.thomsonreuters.uscl.ereader.deliver.service.ProviewClient#promoteTitle
	 * (java.lang.String, java.lang.String)
	 */
	public String promoteTitle(final String fullyQualifiedTitleId,
			final String eBookVersionNumber) throws ProviewException {
		if (StringUtils.isBlank(fullyQualifiedTitleId)) {
			throw new IllegalArgumentException(
					"fullyQualifiedTitleId cannot be null or empty, but was ["
							+ fullyQualifiedTitleId + "].");
		}
		if (StringUtils.isBlank(eBookVersionNumber)) {
			throw new IllegalArgumentException(
					"eBookVersionNumber must not be null or empty, but was ["
							+ eBookVersionNumber + "].");
		}

		Map<String, String> urlParameters = new HashMap<String, String>();
		urlParameters.put("titleId", fullyQualifiedTitleId);
		urlParameters.put("eBookVersionNumber", eBookVersionNumber);

		ProviewRequestCallback proviewRequestCallback = proviewRequestCallbackFactory
				.getRequestCallback();

		String proviewResponse = restTemplate.execute(promoteTitleUriTemplate,
				HttpMethod.PUT, proviewRequestCallback,
				proviewResponseExtractorFactory.getResponseExtractor(),
				urlParameters);

		return proviewResponse;
	}

	@Override
	public String getAllPublishedTitles() throws ProviewException {
		Map<String, String> urlVariables = new HashMap<String, String>();

		String response = null;
		try {
			response = restTemplate.execute(getTitlesUriTemplate,
					HttpMethod.GET,
					proviewRequestCallbackFactory.getRequestCallback(),
					proviewResponseExtractorFactory.getResponseExtractor());
		} catch (Exception e) {
			LOG.debug(e);
			throw new ProviewException(e.getMessage());
		}

		return response;
	}

	@Override
	public String getPublishingStatus(String fullyQualifiedTitleId)
			throws ProviewException {
		if (StringUtils.isBlank(fullyQualifiedTitleId)) {
			throw new IllegalArgumentException(
					"Cannot get publishing status for titleId: "
							+ fullyQualifiedTitleId
							+ ". The titleId must not be null or empty.");
		}

		Map<String, String> urlParameters = new HashMap<String, String>();
		urlParameters.put("titleId", fullyQualifiedTitleId);
		ResponseEntity responseEntity = restTemplate.getForEntity(
				publishingStatusUriTemplate, String.class, urlParameters);
		logResponse(responseEntity);

		return responseEntity.getBody().toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.thomsonreuters.uscl.ereader.deliver.service.ProviewClient#
	 * getCurrentProviewTitleInfo(java.lang.String)
	 */
	@Override
	public ProviewTitleInfo getLatestProviewTitleInfo(
			final String fullyQualifiedTitleId) throws ProviewException {

		ProviewTitleInfo latestProviewVersion = null;

		String allPublishedTitleResponse = getAllPublishedTitles();

		PublishedTitleParser parser = new PublishedTitleParser();
		Map<String, ProviewTitleContainer> titleMap = parser
				.process(allPublishedTitleResponse);

		ProviewTitleContainer proviewTitleContainer = titleMap
				.get(fullyQualifiedTitleId);

		if (proviewTitleContainer != null) {
			latestProviewVersion = proviewTitleContainer.getLatestVersion();
		}

		return latestProviewVersion;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.thomsonreuters.uscl.ereader.deliver.service.ProviewClient#
	 * getProviewTitleContainer(java.lang.String)
	 */
	@Override
	public ProviewTitleContainer getProviewTitleContainer(
			final String fullyQualifiedTitleId) throws ProviewException {

		String allPublishedTitleResponse = getAllPublishedTitles();

		PublishedTitleParser parser = new PublishedTitleParser();
		Map<String, ProviewTitleContainer> titleMap = parser
				.process(allPublishedTitleResponse);

		return titleMap.get(fullyQualifiedTitleId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.thomsonreuters.uscl.ereader.deliver.service.ProviewClient#
	 * getAllProviewTitleInfo()
	 */
	@Override
	public Map<String, ProviewTitleContainer> getAllProviewTitleInfo()
			throws ProviewException {

		String allPublishedTitleResponse = getAllPublishedTitles();

		PublishedTitleParser parser = new PublishedTitleParser();

		return parser.process(allPublishedTitleResponse);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.thomsonreuters.uscl.ereader.deliver.service.ProviewClient#
	 * getAllLatestProviewTitleInfo()
	 */
	@Override
	public ArrayList<ProviewTitleInfo> getAllLatestProviewTitleInfo()
			throws ProviewException {

		ArrayList<ProviewTitleInfo> allLatestProviewTitles = new ArrayList<ProviewTitleInfo>();

		Map<String, ProviewTitleContainer> titleMap = getAllProviewTitleInfo();

		for (String bookId : titleMap.keySet()) {
			ProviewTitleContainer titleContainer = titleMap.get(bookId);
			ProviewTitleInfo latestVersion = titleContainer.getLatestVersion();
			latestVersion.setTotalNumberOfVersions(titleContainer
					.getProviewTitleInfos().size());
			allLatestProviewTitles.add(latestVersion);
		}

		return allLatestProviewTitles;

	}

	@Override
	public ArrayList<ProviewTitleInfo> getAllLatestProviewTitleInfo(
			Map<String, ProviewTitleContainer> titleMap)
			throws ProviewException {

		ArrayList<ProviewTitleInfo> allLatestProviewTitles = new ArrayList<ProviewTitleInfo>();

		for (String bookId : titleMap.keySet()) {
			ProviewTitleContainer titleContainer = titleMap.get(bookId);
			ProviewTitleInfo latestVersion = titleContainer.getLatestVersion();
			latestVersion.setTotalNumberOfVersions(titleContainer
					.getProviewTitleInfos().size());
			allLatestProviewTitles.add(latestVersion);
		}

		return allLatestProviewTitles;

	}

	@Override
	public boolean hasTitleIdBeenPublished(final String fullyQualifiedTitleId)
			throws ProviewException {

		String allPublishedTitleResponse = getAllPublishedTitles();

		PublishedTitleParser parser = new PublishedTitleParser();
		Map<String, ProviewTitleContainer> titleMap = parser
				.process(allPublishedTitleResponse);

		ProviewTitleContainer proviewTitleContainer = titleMap
				.get(fullyQualifiedTitleId);

		if (proviewTitleContainer != null) {
			return proviewTitleContainer.hasBeenPublished();
		} else {
			return false;
		}
	}

	private void logResponse(final ResponseEntity responseEntity) {
		LOG.debug("Response Headers: "
				+ responseEntity.getHeaders().toSingleValueMap());
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

	public void setPublishingStatusUriTemplate(
			String publishingStatusUriTemplate) {
		this.publishingStatusUriTemplate = publishingStatusUriTemplate;
	}

	public void setProviewRequestCallbackFactory(
			ProviewRequestCallbackFactory proviewRequestCallbackFactory) {
		this.proviewRequestCallbackFactory = proviewRequestCallbackFactory;
	}

	public void setProviewResponseExtractorFactory(
			ProviewResponseExtractorFactory proviewResponseExtractorFactory) {
		this.proviewResponseExtractorFactory = proviewResponseExtractorFactory;
	}

	public String getDeleteTitleUriTemplate() {
		return deleteTitleUriTemplate;
	}

	public void setDeleteTitleUriTemplate(String deleteTitleUriTemplate) {
		this.deleteTitleUriTemplate = deleteTitleUriTemplate;
	}

	public String getRemoveTitleUriTemplate() {
		return removeTitleUriTemplate;
	}

	public void setRemoveTitleUriTemplate(String removeTitleUriTemplate) {
		this.removeTitleUriTemplate = removeTitleUriTemplate;
	}

	public String getPromoteTitleUriTemplate() {
		return promoteTitleUriTemplate;
	}

	public void setPromoteTitleUriTemplate(String promoteTitleUriTemplate) {
		this.promoteTitleUriTemplate = promoteTitleUriTemplate;
	}
}
