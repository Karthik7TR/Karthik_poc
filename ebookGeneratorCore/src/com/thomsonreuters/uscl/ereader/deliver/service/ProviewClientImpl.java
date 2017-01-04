/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.deliver.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import com.thomsonreuters.uscl.ereader.deliver.rest.ProviewRequestCallback;
import com.thomsonreuters.uscl.ereader.deliver.rest.ProviewRequestCallbackFactory;
import com.thomsonreuters.uscl.ereader.deliver.rest.ProviewResponseExtractorFactory;
import com.thomsonreuters.uscl.ereader.deliver.rest.ProviewXMLRequestCallback;

/**
 * This class is responsible for interacting with ProView via their REST interface.
 * 
 * @author u0081674
 * 
 */
public class ProviewClientImpl implements ProviewClient {

	private static final Logger LOG = LogManager.getLogger(ProviewClientImpl.class);
	
	public static final String PROVIEW_HOST_PARAM = "proviewHost";
	private RestTemplate restTemplate;
	private InetAddress proviewHost;

	private String allGroupsUriTemplate;
	private String singleGroupUriTemplate;
	private String getGroupUriTemplate;
	private String createGroupUriTemplate;
	private String promoteGroupStatusUriTemplate;
	private String removeGroupStatusUriTemplate;
	private String deleteGroupUriTemplate;
	
	private String getTitlesUriTemplate;
	private String singleTitleTemplate;
	private String singleTitleByVersionUriTemplate;
	private String publishTitleUriTemplate;
	private String promoteTitleUriTemplate;
	private String removeTitleUriTemplate;
	private String deleteTitleUriTemplate;

	private ProviewRequestCallbackFactory proviewRequestCallbackFactory;
	private ProviewResponseExtractorFactory proviewResponseExtractorFactory;

	/*----------------------ProviewGroup operations--------------------------*/

	/**
	 * Request will get group definition
	 * 
	 * @param groupDefinition
	 * @return
	 * @throws ProviewException
	 */
	@Override
	public String getAllProviewGroups() throws ProviewException {

		String proviewResponse = null;
		try {

			Map<String, String> urlParameters = new HashMap<String, String>();
			urlParameters.put(PROVIEW_HOST_PARAM, proviewHost.getHostName());

			proviewResponse = restTemplate.execute(allGroupsUriTemplate, HttpMethod.GET, proviewRequestCallbackFactory
					.getStreamRequestCallback(), proviewResponseExtractorFactory.getResponseExtractor(), urlParameters);

		} catch (Exception e) {
			LOG.debug(e);
			throw new ProviewException(e.getMessage());
		}

		return proviewResponse;
	}

	/**
	 * Request will get all versions of group definition by Id
	 * 
	 * @param groupDefinition
	 * @return
	 * @throws ProviewException
	 */
	@Override
	public String getProviewGroupById(final String groupId) throws ProviewException {
		Map<String, String> urlParameters = new HashMap<String, String>();
		urlParameters.put(PROVIEW_HOST_PARAM, proviewHost.getHostName());
		urlParameters.put("groupId", groupId);

		ProviewXMLRequestCallback proviewXMLRequestCallback = proviewRequestCallbackFactory.getXMLRequestCallback();

		String proviewResponse = restTemplate.execute(singleGroupUriTemplate, HttpMethod.GET, proviewXMLRequestCallback,
				proviewResponseExtractorFactory.getResponseExtractor(), urlParameters);

		return proviewResponse;
	}

	/**
	 * Request will get group definition by version
	 * 
	 * @param groupDefinition
	 * @return
	 * @throws ProviewException
	 */
	@Override
	public String getProviewGroupInfo(final String groupId, final String groupVersion) throws ProviewException {

		Map<String, String> urlParameters = new HashMap<String, String>();
		urlParameters.put(PROVIEW_HOST_PARAM, proviewHost.getHostName());
		urlParameters.put("groupId", groupId);
		urlParameters.put("groupVersionNumber", groupVersion);

		ProviewXMLRequestCallback proviewXMLRequestCallback = proviewRequestCallbackFactory.getXMLRequestCallback();

		String proviewResponse = restTemplate.execute(getGroupUriTemplate, HttpMethod.GET, proviewXMLRequestCallback,
				proviewResponseExtractorFactory.getResponseExtractor(), urlParameters);

		return proviewResponse;
	}

	@Override
	public String createGroup(String groupId, String groupVersion, String requestBody) throws ProviewException,
			UnsupportedEncodingException {

		InputStream requestBodyStream = new ByteArrayInputStream(requestBody.getBytes("UTF-8"));
		Map<String, String> urlParameters = new HashMap<String, String>();
		urlParameters.put(PROVIEW_HOST_PARAM, proviewHost.getHostName());
		urlParameters.put("groupId", groupId);
		urlParameters.put("groupVersionNumber", groupVersion);

		ProviewXMLRequestCallback proviewXMLRequestCallback = proviewRequestCallbackFactory.getXMLRequestCallback();
		proviewXMLRequestCallback.setRequestInputStream(requestBodyStream);

		String proviewResponse = restTemplate.execute(createGroupUriTemplate, HttpMethod.PUT, proviewXMLRequestCallback,
				proviewResponseExtractorFactory.getResponseExtractor(), urlParameters);

		return proviewResponse;
	}

	/**
	 * This request will update Group status to promote
	 * 
	 * @param groupDefinition
	 * @return
	 * @throws ProviewException
	 */
	@Override
	public String promoteGroup(final String groupId, final String groupVersion) throws ProviewException {

		Map<String, String> urlParameters = new HashMap<String, String>();
		urlParameters.put(PROVIEW_HOST_PARAM, proviewHost.getHostName());
		urlParameters.put("groupId", groupId);
		urlParameters.put("groupVersionNumber", groupVersion);

		ProviewXMLRequestCallback proviewXMLRequestCallback = proviewRequestCallbackFactory.getXMLRequestCallback();

		String proviewResponse = restTemplate.execute(promoteGroupStatusUriTemplate, HttpMethod.PUT, proviewXMLRequestCallback,
				proviewResponseExtractorFactory.getResponseExtractor(), urlParameters);

		return proviewResponse;
	}

	/**
	 * This request will update Group status to removed
	 * 
	 * @param groupDefinition
	 * @return
	 * @throws ProviewException
	 */
	@Override
	public String removeGroup(final String groupId, final String groupVersion) throws ProviewException {

		Map<String, String> urlParameters = new HashMap<String, String>();
		urlParameters.put(PROVIEW_HOST_PARAM, proviewHost.getHostName());
		urlParameters.put("groupId", groupId);
		urlParameters.put("groupVersionNumber", groupVersion);

		ProviewXMLRequestCallback proviewXMLRequestCallback = proviewRequestCallbackFactory.getXMLRequestCallback();

		String proviewResponse = restTemplate.execute(removeGroupStatusUriTemplate, HttpMethod.PUT, proviewXMLRequestCallback,
				proviewResponseExtractorFactory.getResponseExtractor(), urlParameters);

		return proviewResponse;
	}

	/**
	 * Request will delete group
	 * 
	 * @param groupDefinition
	 * @return
	 * @throws ProviewException
	 */
	@Override
	public String deleteGroup(final String groupId, final String groupVersion) throws ProviewException {

		Map<String, String> urlParameters = new HashMap<String, String>();
		urlParameters.put(PROVIEW_HOST_PARAM, proviewHost.getHostName());
		urlParameters.put("groupId", groupId);
		urlParameters.put("groupVersionNumber", groupVersion);

		ProviewXMLRequestCallback proviewXMLRequestCallback = proviewRequestCallbackFactory.getXMLRequestCallback();

		String proviewResponse = restTemplate.execute(deleteGroupUriTemplate, HttpMethod.DELETE, proviewXMLRequestCallback,
				proviewResponseExtractorFactory.getResponseExtractor(), urlParameters);

		// to-do response could be "status cannot be changed from removed to removed"
		return proviewResponse;
	}

	/*-----------------------Proview Title operations-----------------------------*/

	@Override
	public String getAllPublishedTitles() throws ProviewException {
		String response = null;
		try {
			Map<String, String> urlParameters = new HashMap<String, String>();
			LOG.debug("Proview host: " + proviewHost.getHostName());
			urlParameters.put(PROVIEW_HOST_PARAM, proviewHost.getHostName());
			response = restTemplate.execute(getTitlesUriTemplate, HttpMethod.GET, proviewRequestCallbackFactory.getStreamRequestCallback(),
					proviewResponseExtractorFactory.getResponseExtractor(), urlParameters);
		} catch (Exception e) {
			LOG.debug(e);
			throw new ProviewException(e.getMessage());
		}

		return response;
	}

	@Override
	public String getSinglePublishedTitle(String fullyQualifiedTitleId) throws ProviewException {
		String response = null;
		try {
			Map<String, String> urlParameters = new HashMap<String, String>();
			LOG.debug("Proview host: " + proviewHost.getHostName());
			urlParameters.put(PROVIEW_HOST_PARAM, proviewHost.getHostName());
			urlParameters.put("titleId", fullyQualifiedTitleId);
			response = restTemplate.execute(singleTitleTemplate, HttpMethod.GET, proviewRequestCallbackFactory.getStreamRequestCallback(),
					proviewResponseExtractorFactory.getResponseExtractor(), urlParameters);
		} catch (Exception e) {
			LOG.debug(e);
			throw new ProviewException(e.getMessage());
		}

		return response;
	}

	@Override
	public String getSingleTitleInfoByVersion(String fullyQualifiedTitleId, String version) throws ProviewException {
		String response = null;
		try {
			if (StringUtils.isBlank(fullyQualifiedTitleId) && StringUtils.isBlank(version)) {
				throw new IllegalArgumentException("Cannot get publishing status for titleId: " + fullyQualifiedTitleId + " and version "
						+ version + ". Both titleId and version should be provided.");
			}

			Map<String, String> urlParameters = new HashMap<String, String>();
			urlParameters.put("titleId", fullyQualifiedTitleId);
			urlParameters.put("eBookVersionNumber", version);
			urlParameters.put(PROVIEW_HOST_PARAM, proviewHost.getHostName());
			response = restTemplate.execute(singleTitleByVersionUriTemplate, HttpMethod.GET, proviewRequestCallbackFactory
					.getXMLRequestCallback(), proviewResponseExtractorFactory.getResponseExtractor(), urlParameters);

		} catch (Exception e) {
			LOG.debug(e);
			throw new ProviewException(e.getMessage());
		}
		return response;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.thomsonreuters.uscl.ereader.deliver.service.ProviewClient#publishTitle (java.lang.String, java.lang.String,
	 * java.io.File)
	 */
	@Override
	public String publishTitle(final String fullyQualifiedTitleId, final String eBookVersionNumber, final File eBook)
			throws ProviewException {
		if (StringUtils.isBlank(fullyQualifiedTitleId)) {
			throw new IllegalArgumentException("fullyQualifiedTitleId cannot be null or empty, but was [" + fullyQualifiedTitleId + "].");
		}
		if (StringUtils.isBlank(eBookVersionNumber)) {
			throw new IllegalArgumentException("eBookVersionNumber must not be null or empty, but was [" + eBookVersionNumber + "].");
		}
		FileInputStream ebookInputStream;

		try {
			ebookInputStream = new FileInputStream(eBook);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Cannot send a null or empty File to ProView.", e);
		}

		Map<String, String> urlParameters = new HashMap<String, String>();
		urlParameters.put(PROVIEW_HOST_PARAM, proviewHost.getHostName());
		urlParameters.put("titleId", fullyQualifiedTitleId);
		urlParameters.put("eBookVersionNumber", eBookVersionNumber);

		ProviewRequestCallback proviewRequestCallback = proviewRequestCallbackFactory.getStreamRequestCallback();
		proviewRequestCallback.setEbookInputStream(ebookInputStream);

		String proviewResponse = restTemplate.execute(publishTitleUriTemplate, HttpMethod.PUT, proviewRequestCallback,
				proviewResponseExtractorFactory.getResponseExtractor(), urlParameters);

		// restTemplate.put(publishTitleUriTemplate, eBook, urlParameters);

		// logResponse(response);

		return proviewResponse;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.thomsonreuters.uscl.ereader.deliver.service.ProviewClient#promoteTitle (java.lang.String, java.lang.String)
	 */
	@Override
	public String promoteTitle(final String fullyQualifiedTitleId, final String eBookVersionNumber) throws ProviewException {
		if (StringUtils.isBlank(fullyQualifiedTitleId)) {
			throw new IllegalArgumentException("fullyQualifiedTitleId cannot be null or empty, but was [" + fullyQualifiedTitleId + "].");
		}
		if (StringUtils.isBlank(eBookVersionNumber)) {
			throw new IllegalArgumentException("eBookVersionNumber must not be null or empty, but was [" + eBookVersionNumber + "].");
		}

		Map<String, String> urlParameters = new HashMap<String, String>();
		urlParameters.put(PROVIEW_HOST_PARAM, proviewHost.getHostName());
		urlParameters.put("titleId", fullyQualifiedTitleId);
		urlParameters.put("eBookVersionNumber", eBookVersionNumber);

		ProviewRequestCallback proviewRequestCallback = proviewRequestCallbackFactory.getStreamRequestCallback();

		String proviewResponse = restTemplate.execute(promoteTitleUriTemplate, HttpMethod.PUT, proviewRequestCallback,
				proviewResponseExtractorFactory.getResponseExtractor(), urlParameters);

		return proviewResponse;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.thomsonreuters.uscl.ereader.deliver.service.ProviewClient#removeTitle (java.lang.String, java.lang.String)
	 */
	@Override
	public String removeTitle(final String fullyQualifiedTitleId, final String eBookVersionNumber) throws ProviewException {
		if (StringUtils.isBlank(fullyQualifiedTitleId)) {
			throw new IllegalArgumentException("fullyQualifiedTitleId cannot be null or empty, but was [" + fullyQualifiedTitleId + "].");
		}
		if (StringUtils.isBlank(eBookVersionNumber)) {
			throw new IllegalArgumentException("eBookVersionNumber must not be null or empty, but was [" + eBookVersionNumber + "].");
		}

		Map<String, String> urlParameters = new HashMap<String, String>();
		urlParameters.put(PROVIEW_HOST_PARAM, proviewHost.getHostName());
		urlParameters.put("titleId", fullyQualifiedTitleId);
		urlParameters.put("eBookVersionNumber", eBookVersionNumber);

		ProviewRequestCallback proviewRequestCallback = proviewRequestCallbackFactory.getStreamRequestCallback();

		String proviewResponse = restTemplate.execute(removeTitleUriTemplate, HttpMethod.PUT, proviewRequestCallback,
				proviewResponseExtractorFactory.getResponseExtractor(), urlParameters);

		return proviewResponse;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.thomsonreuters.uscl.ereader.deliver.service.ProviewClient#deleteTitle (java.lang.String, java.lang.String)
	 */
	@Override
	public String deleteTitle(final String fullyQualifiedTitleId, final String eBookVersionNumber) throws ProviewException {
		if (StringUtils.isBlank(fullyQualifiedTitleId)) {
			throw new IllegalArgumentException("fullyQualifiedTitleId cannot be null or empty, but was [" + fullyQualifiedTitleId + "].");
		}
		if (StringUtils.isBlank(eBookVersionNumber)) {
			throw new IllegalArgumentException("eBookVersionNumber must not be null or empty, but was [" + eBookVersionNumber + "].");
		}

		Map<String, String> urlParameters = new HashMap<String, String>();
		urlParameters.put(PROVIEW_HOST_PARAM, proviewHost.getHostName());
		urlParameters.put("titleId", fullyQualifiedTitleId);
		urlParameters.put("eBookVersionNumber", eBookVersionNumber);

		ProviewRequestCallback proviewRequestCallback = proviewRequestCallbackFactory.getStreamRequestCallback();

		String proviewResponse = restTemplate.execute(deleteTitleUriTemplate, HttpMethod.DELETE, proviewRequestCallback,
				proviewResponseExtractorFactory.getResponseExtractor(), urlParameters);

		return proviewResponse;
	}

	/**
	 * Compose the search request body.
	 * 
	 * @param writer
	 * @param name
	 * @param value
	 * @throws XMLStreamException
	 */
	protected void writeElement(XMLStreamWriter writer, String name, Object value) throws XMLStreamException {
		if (value != null) {
			writer.writeStartElement(name);
			writer.writeCharacters(value.toString().trim());
			writer.writeEndElement();
		}
	}

	/**
	 * Allows for the dynamic setting of this host name "on the fly".
	 * 
	 * @param host the new host name to use. For example a production or test host.
	 */
	@Override
	public void setProviewHostname(String hostname) throws UnknownHostException {
		setProviewHost(InetAddress.getByName(hostname));
	}

	public void setProviewHost(InetAddress host) {
		this.proviewHost = host;
	}

	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	@Required
	public void setPublishTitleUriTemplate(String publishTitleUriTemplate) {
		this.publishTitleUriTemplate = publishTitleUriTemplate;
	}

	@Required
	public void setGetTitlesUriTemplate(String getTitlesUriTemplate) {
		this.getTitlesUriTemplate = getTitlesUriTemplate;
	}

	@Required
	public String getCreateGroupUriTemplate() {
		return createGroupUriTemplate;
	}

	@Required
	public void setCreateGroupUriTemplate(String createGroupUriTemplate) {
		this.createGroupUriTemplate = createGroupUriTemplate;
	}

	@Required
	public void setProviewRequestCallbackFactory(ProviewRequestCallbackFactory proviewRequestCallbackFactory) {
		this.proviewRequestCallbackFactory = proviewRequestCallbackFactory;
	}

	@Required
	public void setProviewResponseExtractorFactory(ProviewResponseExtractorFactory proviewResponseExtractorFactory) {
		this.proviewResponseExtractorFactory = proviewResponseExtractorFactory;
	}

	public String getDeleteTitleUriTemplate() {
		return deleteTitleUriTemplate;
	}

	@Required
	public void setDeleteTitleUriTemplate(String deleteTitleUriTemplate) {
		this.deleteTitleUriTemplate = deleteTitleUriTemplate;
	}

	public String getRemoveTitleUriTemplate() {
		return removeTitleUriTemplate;
	}

	@Required
	public void setRemoveTitleUriTemplate(String removeTitleUriTemplate) {
		this.removeTitleUriTemplate = removeTitleUriTemplate;
	}

	public String getPromoteTitleUriTemplate() {
		return promoteTitleUriTemplate;
	}

	@Required
	public void setPromoteTitleUriTemplate(String promoteTitleUriTemplate) {
		this.promoteTitleUriTemplate = promoteTitleUriTemplate;
	}

	public String getRemoveGroupStatusUriTemplate() {
		return removeGroupStatusUriTemplate;
	}

	@Required
	public void setRemoveGroupStatusUriTemplate(String updateGroupStatusUriTemplate) {
		this.removeGroupStatusUriTemplate = updateGroupStatusUriTemplate;
	}

	public String getGetGroupUriTemplate() {
		return getGroupUriTemplate;
	}

	@Required
	public void setGetGroupUriTemplate(String getGroupUriTemplate) {
		this.getGroupUriTemplate = getGroupUriTemplate;
	}

	public String getPromoteGroupStatusUriTemplate() {
		return promoteGroupStatusUriTemplate;
	}

	@Required
	public void setPromoteGroupStatusUriTemplate(String promoteGroupStatusUriTemplate) {
		this.promoteGroupStatusUriTemplate = promoteGroupStatusUriTemplate;
	}

	public String getDeleteGroupUriTemplate() {
		return deleteGroupUriTemplate;
	}

	@Required
	public void setDeleteGroupUriTemplate(String deleteGroupUriTemplate) {
		this.deleteGroupUriTemplate = deleteGroupUriTemplate;
	}

	public String getAllGroupsUriTemplate() {
		return allGroupsUriTemplate;
	}

	@Required
	public void setAllGroupsUriTemplate(String allGroupsUriTemplate) {
		this.allGroupsUriTemplate = allGroupsUriTemplate;
	}

	public String getSingleTitleTemplate() {
		return singleTitleTemplate;
	}

	@Required
	public void setSingleTitleTemplate(String singleTitleTemplate) {
		this.singleTitleTemplate = singleTitleTemplate;
	}

	public String getSingleTitleByVersionUriTemplate() {
		return singleTitleByVersionUriTemplate;
	}

	@Required
	public void setSingleTitleByVersionUriTemplate(String singleTitleByVersionUriTemplate) {
		this.singleTitleByVersionUriTemplate = singleTitleByVersionUriTemplate;
	}

	public String getSingleGroupUriTemplate() {
		return singleGroupUriTemplate;
	}

	@Required
	public void setSingleGroupUriTemplate(String singleGroupUriTemplate) {
		this.singleGroupUriTemplate = singleGroupUriTemplate;
	}
}
