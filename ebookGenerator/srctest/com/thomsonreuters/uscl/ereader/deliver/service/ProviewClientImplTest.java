/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.deliver.service;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * Component tests for ProviewClientImpl.
 * 
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 */
public class ProviewClientImplTest 
{
	private static final Logger LOG = Logger.getLogger(ProviewClientImplTest.class);
	
	private static final String PROVIEW_DOMAIN_PREFIX = "proviewpublishing.int.qed.thomsonreuters.com";
	private String getTitlesUriTemplate = "/v1/titles/uscl/all";
	private String publishTitleUriTemplate = "/v1/title/uscl/{contentTypeAbbreviation}/{titleId}/{eBookVersionNumber}";
	private String validateTitleUriTemplate = "";
	
	private ProviewClientImpl proviewClient;
	private RestTemplate mockRestTemplate;
	private ResponseEntity mockResponseEntity;
	private HttpHeaders mockHeaders;
	
	@Before
	public void setUp() throws Exception
	{
		this.proviewClient = new ProviewClientImpl();
		mockRestTemplate = EasyMock.createMock(RestTemplate.class);
		proviewClient.setRestTemplate(mockRestTemplate);
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
		
		EasyMock.expect(mockRestTemplate.execute("http://" + PROVIEW_DOMAIN_PREFIX + getTitlesUriTemplate, HttpMethod.GET, null, null)).andReturn("YARR!");
		
		replayAll();
		String response = proviewClient.getAllPublishedTitles();
		verifyAll();
		
		assertTrue("Response did not match expected result!", response.equals(expectedResponse));
	}
	
	private void verifyAll() {
		EasyMock.verify(mockRestTemplate);
		EasyMock.verify(mockResponseEntity);
		EasyMock.verify(mockHeaders);
	}

	private void replayAll() {
		EasyMock.replay(mockHeaders);
		EasyMock.replay(mockResponseEntity);
		EasyMock.replay(mockRestTemplate);
		
	}
	
}
