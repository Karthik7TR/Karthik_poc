/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.deliver.service;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.net.InetAddress;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewRuntimeException;
import com.thomsonreuters.uscl.ereader.deliver.rest.ProviewHttpResponseErrorHandler;
import com.thomsonreuters.uscl.ereader.deliver.rest.ProviewMessageConverter;
import com.thomsonreuters.uscl.ereader.deliver.rest.ProviewRequestCallbackFactory;
import com.thomsonreuters.uscl.ereader.deliver.rest.ProviewResponseExtractorFactory;

/**
 * Integration tests for ProviewClientImpl. These do not get run during CI builds.
 * 
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 */
public class ProviewClientImplIntegrationTest 
{
	private static final Logger LOG = Logger.getLogger(ProviewClientImplIntegrationTest.class);
	
	private static final String PROVIEW_DOMAIN_PREFIX = "trp0002-14:9008";
	private String getTitlesUriTemplate = "/v1/titles/uscl/all";
	private String publishTitleUriTemplate = "/v1/title/{titleId}/{eBookVersionNumber}";
	private String validateTitleUriTemplate = "";

	private static final String PROVIEW_USERNAME = "publisher";
	private static final String PROVIEW_PASSWORD = "f9R_zBq37a";
	
	private static final String PROVIEW_INVALID_USERNAME = "YARR";
	private static final String PROVIEW_INVALID_PASSWORD = "PIRATES!";
	
	private ProviewClientImpl proviewClient;
	private DefaultHttpClient defaultHttpClient;
	private ProviewRequestCallbackFactory proviewRequestCallbackFactory;
	private ProviewResponseExtractorFactory proviewResponseExtractorFactory;
	
	@Before
	public void setUp() throws Exception
	{
		this.proviewClient = new ProviewClientImpl();
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		
		defaultHttpClient = new DefaultHttpClient();
		requestFactory.setHttpClient(defaultHttpClient);

		RestTemplate restTemplate = new RestTemplate(requestFactory);
		restTemplate.getMessageConverters().add(new ProviewMessageConverter<File>());
		restTemplate.setErrorHandler(new ProviewHttpResponseErrorHandler());
		proviewClient.setRestTemplate(restTemplate);
		proviewRequestCallbackFactory = new ProviewRequestCallbackFactory();
		proviewResponseExtractorFactory = new ProviewResponseExtractorFactory();
		proviewClient.setProviewRequestCallbackFactory(proviewRequestCallbackFactory);
		proviewClient.setProviewResponseExtractorFactory(proviewResponseExtractorFactory);
	}

	@After
	public void tearDown() throws Exception
	{
		
	}
	
	@Test
	public void testGetAllTitlesHappyPath() throws Exception {
		proviewClient.setProviewHost(InetAddress.getLocalHost());
		proviewClient.setGetTitlesUriTemplate("http://" + PROVIEW_DOMAIN_PREFIX + getTitlesUriTemplate);
		String publisherInformation = proviewClient.getAllPublishedTitles();
		System.out.println(publisherInformation);
		assertTrue(publisherInformation.startsWith("<titles apiversion=\"v1\" publisher=\"uscl\""));
	}
	
	@Test
	public void testPublishBookFailsBecauseItAlreadyExistsOnProview() throws Exception {
		proviewClient.setProviewHost(InetAddress.getLocalHost());
		proviewClient.setPublishTitleUriTemplate("http://" + PROVIEW_DOMAIN_PREFIX + publishTitleUriTemplate);
		String integrationTestTitleId = "uscl/cr/generator_integration_test";
		String eBookVersionNumber = "v2";

		File eBookDirectory = new File("/apps/ebookbuilder/staticContent");
		File eBook = new File(eBookDirectory, "UrlsAndPaths.dtd");
		try {
			proviewClient.publishTitle(integrationTestTitleId, eBookVersionNumber, eBook);
			fail("Expected an exception related to the title already existing on ProView!");
		}
		catch (ProviewRuntimeException e) {
			//expected
		}
	}
	
	@Test
	public void getSingleTitleInfoByVersion() throws Exception{		
		try{
		String singleTitleByVersionUriTemplate =  "/v1/titles/{titleId}/{eBookVersionNumber}";
		proviewClient.setProviewHost(InetAddress.getLocalHost());
		proviewClient.setSingleTitleByVersionUriTemplate("http://" + PROVIEW_DOMAIN_PREFIX + singleTitleByVersionUriTemplate);
		String publisherInformation = proviewClient.getSingleTitleInfoByVersion("uscl/an/coi", "v1.0");
		System.out.println("publisherInformation : "+publisherInformation);
		}
		catch (Exception e) {
			assertTrue(e.toString().contains("uscl/an/coi/v1.0 does not exist"));
		}
	}
	
	
	@Test
	public void testGetAllTitlesFailsDueToInvalidCredetials() throws Exception {
		
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		
		defaultHttpClient = new DefaultHttpClient();
		defaultHttpClient.getCredentialsProvider().setCredentials(
				new AuthScope(PROVIEW_DOMAIN_PREFIX, AuthScope.ANY_PORT),
				new UsernamePasswordCredentials(PROVIEW_INVALID_USERNAME, PROVIEW_INVALID_PASSWORD));
		requestFactory.setHttpClient(defaultHttpClient);
		
		RestTemplate restTemplate = new RestTemplate(requestFactory);
		proviewClient.setRestTemplate(restTemplate);
		
		proviewClient.setGetTitlesUriTemplate("http://" + PROVIEW_DOMAIN_PREFIX + getTitlesUriTemplate);
		
		try {
			proviewClient.getAllPublishedTitles();
			
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
			//expected
		}
	}
}
