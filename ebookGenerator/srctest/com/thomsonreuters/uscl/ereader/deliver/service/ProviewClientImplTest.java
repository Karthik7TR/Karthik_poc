/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.deliver.service;

import static org.junit.Assert.assertTrue;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.thomsonreuters.uscl.ereader.deliver.rest.ProviewRequestCallback;
import com.thomsonreuters.uscl.ereader.deliver.rest.ProviewRequestCallbackFactory;
import com.thomsonreuters.uscl.ereader.deliver.rest.ProviewResponseExtractor;
import com.thomsonreuters.uscl.ereader.deliver.rest.ProviewResponseExtractorFactory;

/**
 * Component tests for ProviewClientImpl.
 * 
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 */
public class ProviewClientImplTest 
{
	//private static final Logger LOG = Logger.getLogger(ProviewClientImplTest.class);
	
	private static final String PROVIEW_DOMAIN_PREFIX = "proviewpublishing.int.qed.thomsonreuters.com";
	private static InetAddress PROVIEW_HOST;
	private String getTitlesUriTemplate = "/v1/titles/uscl/all";
	
	private ProviewClientImpl proviewClient;
	private RestTemplate mockRestTemplate;
	@SuppressWarnings("rawtypes")
	private ResponseEntity mockResponseEntity;
	private HttpHeaders mockHeaders;
	private ProviewRequestCallbackFactory mockRequestCallbackFactory;
	private ProviewResponseExtractorFactory mockResponseExtractorFactory;
	private ProviewRequestCallback mockRequestCallback;
	private ProviewResponseExtractor mockResponseExtractor;
	
	
	@Before
	public void setUp() throws Exception
	{
		PROVIEW_HOST = InetAddress.getLocalHost();
		this.proviewClient = new ProviewClientImpl();
		mockRequestCallback = new ProviewRequestCallback();
		mockResponseExtractor = new ProviewResponseExtractor();
		mockRestTemplate = EasyMock.createMock(RestTemplate.class);
		mockRequestCallbackFactory = EasyMock.createMock(ProviewRequestCallbackFactory.class);
		mockResponseExtractorFactory = EasyMock.createMock(ProviewResponseExtractorFactory.class);
		proviewClient.setRestTemplate(mockRestTemplate);
		proviewClient.setProviewRequestCallbackFactory(mockRequestCallbackFactory);
		proviewClient.setProviewResponseExtractorFactory(mockResponseExtractorFactory);
		proviewClient.setProviewHost(PROVIEW_HOST);
		mockResponseEntity = EasyMock.createMock(ResponseEntity.class);
		mockHeaders = EasyMock.createMock(HttpHeaders.class);
	}

	@After
	public void tearDown() throws Exception
	{
		
	}
	
	@Test
	public void testGetAllTitlesHappyPath() throws Exception {
		proviewClient.setGetTitlesUriTemplate("http://" + PROVIEW_DOMAIN_PREFIX + getTitlesUriTemplate);
		String expectedResponse = "YARR!";
		
		EasyMock.expect(mockRequestCallbackFactory.getRequestCallback()).andReturn(mockRequestCallback);
		EasyMock.expect(mockResponseExtractorFactory.getResponseExtractor()).andReturn(mockResponseExtractor);
		Map<String, String> urlParameters = new HashMap<String, String>();
		urlParameters.put(ProviewClientImpl.PROVIEW_HOST_PARAM, PROVIEW_HOST.getHostName());
		EasyMock.expect(mockRestTemplate.execute("http://" + PROVIEW_DOMAIN_PREFIX + getTitlesUriTemplate, HttpMethod.GET, mockRequestCallback, mockResponseExtractor, urlParameters)).andReturn("YARR!");
		
		replayAll();
		String response = proviewClient.getAllPublishedTitles();
		verifyAll();
		
		assertTrue("Response did not match expected result!", response.equals(expectedResponse));
	}
	
	private void verifyAll() {
		EasyMock.verify(mockRestTemplate);
		EasyMock.verify(mockResponseEntity);
		EasyMock.verify(mockHeaders);
		EasyMock.verify(mockRequestCallbackFactory);
		EasyMock.verify(mockResponseExtractorFactory);
	}

	private void replayAll() {
		EasyMock.replay(mockHeaders);
		EasyMock.replay(mockResponseEntity);
		EasyMock.replay(mockRestTemplate);
		EasyMock.replay(mockRequestCallbackFactory);
		EasyMock.replay(mockResponseExtractorFactory);
		
	}
	
}
