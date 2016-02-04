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
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.thomsonreuters.uscl.ereader.GroupDefinition;
import com.thomsonreuters.uscl.ereader.GroupDefinition.SubGroupInfo;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewRuntimeException;
import com.thomsonreuters.uscl.ereader.deliver.rest.CloseableAuthenticationHttpClientFactory;
import com.thomsonreuters.uscl.ereader.deliver.rest.ProviewHttpResponseErrorHandler;
import com.thomsonreuters.uscl.ereader.deliver.rest.ProviewMessageConverter;
import com.thomsonreuters.uscl.ereader.deliver.rest.ProviewRequestCallbackFactory;
import com.thomsonreuters.uscl.ereader.deliver.rest.ProviewResponseExtractorFactory;

/**
 * Integration tests for ProviewClientImpl. These do not get run during CI
 * builds.
 * 
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris
 *         Schwartz</a> u0081674
 */
public class ProviewClientImplIntegrationTest {
	private static final Logger LOG = Logger
			.getLogger(ProviewClientImplIntegrationTest.class);

	private static final String PROVIEW_DOMAIN_PREFIX = "proviewpublishing.int.ci.thomsonreuters.com";
	private String getTitlesUriTemplate = "/v1/titles/uscl/all";
	private String publishTitleUriTemplate = "/v1/title/{titleId}/{eBookVersionNumber}";
	private String removeTitleUriTemplate = "/v1/title/{titleId}/{eBookVersionNumber}/status/removed";
	private String promoteTitleUriTemplate = "/v1/title/{titleId}/{eBookVersionNumber}/status/final";
	private String deleteTitleUriTemplate = "/v1/title/{titleId}/{eBookVersionNumber}";
	private String validateTitleUriTemplate = "";

	private static final String PROVIEW_USERNAME = "publisher";
	private static final String PROVIEW_PASSWORD = "f9R_zBq37a";

	private static final String PROVIEW_INVALID_USERNAME = "YARR";
	private static final String PROVIEW_INVALID_PASSWORD = "PIRATES!";

	private ProviewClientImpl proviewClient;
	private CloseableAuthenticationHttpClientFactory defaultHttpClient;
	private ProviewRequestCallbackFactory proviewRequestCallbackFactory;
	private ProviewResponseExtractorFactory proviewResponseExtractorFactory;

	@Before
	public void setUp() throws Exception {
		this.proviewClient = new ProviewClientImpl();
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		requestFactory.setBufferRequestBody(false);

		defaultHttpClient = new CloseableAuthenticationHttpClientFactory("proviewpublishing.int.ci.thomsonreuters.com",PROVIEW_USERNAME,PROVIEW_PASSWORD);
		/*defaultHttpClient = new DefaultHttpClient();
		defaultHttpClient.getCredentialsProvider().setCredentials(
				new AuthScope(PROVIEW_DOMAIN_PREFIX, AuthScope.ANY_PORT),
				new UsernamePasswordCredentials(PROVIEW_USERNAME,
						PROVIEW_PASSWORD));
		requestFactory.setHttpClient(defaultHttpClient);*/
		requestFactory.setHttpClient(defaultHttpClient.getCloseableAuthenticationHttpClient());

		RestTemplate restTemplate = new RestTemplate(requestFactory);
		restTemplate.getMessageConverters().add(
				new ProviewMessageConverter<File>());
		restTemplate.setErrorHandler(new ProviewHttpResponseErrorHandler());
		proviewClient.setRestTemplate(restTemplate);
		proviewRequestCallbackFactory = new ProviewRequestCallbackFactory();
		proviewResponseExtractorFactory = new ProviewResponseExtractorFactory();
		proviewClient
				.setProviewRequestCallbackFactory(proviewRequestCallbackFactory);
		proviewClient
				.setProviewResponseExtractorFactory(proviewResponseExtractorFactory);
	}

	@After
	public void tearDown() throws Exception {

	}

	@Test
	public void testGetAllTitlesHappyPath() throws Exception {
		proviewClient.setGetTitlesUriTemplate("http://" + PROVIEW_DOMAIN_PREFIX
				+ getTitlesUriTemplate);
		String publisherInformation = proviewClient.getAllPublishedTitles();
		System.out.println(publisherInformation);
		assertTrue(publisherInformation
				.startsWith("<titles apiversion=\"v1\" publisher=\"uscl\""));
	}

	@Test
	public void testRemoveTitle() {
		proviewClient.setRemoveTitleUriTemplate("http://"
				+ PROVIEW_DOMAIN_PREFIX + removeTitleUriTemplate);
		String publisherInformation = null;
		try {
			publisherInformation = proviewClient.removeTitle("uscl/an/blm",
					"v1.1");

		} catch (Exception e) {
			if (e.getMessage().contains("Collection doesn't exist")) {
				fail("Expected an exception as title doesn't exist on ProView!");
			}
		}

		System.out.println(publisherInformation);
	}
	
	@Test
	public void testPromoteTitle() {
		proviewClient.setPromoteTitleUriTemplate("http://"
				+ PROVIEW_DOMAIN_PREFIX + promoteTitleUriTemplate);
		String publisherInformation = null;
		try {
			publisherInformation = proviewClient.promoteTitle("uscl/an/an_frcpbkeenantest2",
					"v2");

		} catch (Exception e) {
			if (e.getMessage().contains("Collection doesn't exist")) {
				fail("Expected an exception as title doesn't exist on ProView!");
			}
		}

		System.out.println(publisherInformation);
	}


	@Test
	public void testDeleteTitle() {
		String publisherInformation = null;
		proviewClient.setDeleteTitleUriTemplate("http://"
				+ PROVIEW_DOMAIN_PREFIX + deleteTitleUriTemplate);
		try {
			publisherInformation = proviewClient.deleteTitle("uscl/an/blm",
					"v1.0");
		} catch (Exception e) {
			if (e.getMessage().contains("Collection doesn't exist")) {
				fail("Expected an exception as title doesn't exist on ProView!");
			}

		}

		System.out.println(publisherInformation);
	}

	@Test
	public void testPublishBookFailsBecauseItAlreadyExistsOnProview()
			throws Exception {
		proviewClient.setPublishTitleUriTemplate("http://"
				+ PROVIEW_DOMAIN_PREFIX + publishTitleUriTemplate);
		String integrationTestTitleId = "uscl/cr/generator_integration_test";
		String eBookVersionNumber = "v2";

		File eBookDirectory = new File("/nas/ebookbuilder/data/");
		File eBook = new File(eBookDirectory,
				"proview_client_integration_test.gz");
		try {
			proviewClient.publishTitle(integrationTestTitleId,
					eBookVersionNumber, eBook);
			fail("Expected an exception related to the title already existing on ProView!");
		} catch (ProviewRuntimeException e) {
			// expected
		}
	}
	
	@Test
	public void testGetAllGroupsHappyPath() throws Exception {
		proviewClient.setProviewHost(InetAddress.getLocalHost());
		proviewClient.setAllGroupsUriTemplate("http://"
				+ "proviewpublishing.int.demo.thomsonreuters.com" + "/v1/group/uscl");
		boolean thrown = false;
		try {
		  proviewClient.getAllProviewGroups();
		} catch (ProviewRuntimeException e) {
			thrown = true;
		}
		
		Assert.assertEquals(false,thrown);
		
	}
	
	@Test
	public void testGetSinglePublishedTitle() throws Exception {
		proviewClient.setProviewHost(InetAddress.getLocalHost());
		proviewClient.setSingleTitleTemplate("http://"
				+ "proviewpublishing.int.demo.thomsonreuters.com" + "/v1/titles/{titleId}");		
		try{
		 proviewClient.getSinglePublishedTitle("uscl/an/book_lohisplitnodeinfo");
		} catch (Exception e) {
			if (!e.getMessage().contains("uscl/an/book_lohisplitnodeinfo does not exist")) {
				fail("Expected an exception as title doesn't exist on ProView!");
			}
			
		}
		
	}
	
	
	@Test
	public void testCreateGroup()
			throws Exception {
		proviewClient.setCreateGroupUriTemplate("http://"
				+ "proviewpublishing.int.demo.thomsonreuters.com" + "/v1/group/{groupId}/{groupVersionNumber}");
		proviewClient.setProviewHostname("proviewpublishing.int.demo.thomsonreuters.com");
		GroupDefinition groupDefinition = new GroupDefinition();
		groupDefinition.setGroupId("uscl/groupFinalTest");
		groupDefinition.setGroupVersion("v1");
		groupDefinition.setName("Group Test");
		groupDefinition.setType("standard");
		groupDefinition.setOrder("newerfirst");
		groupDefinition.setHeadTitle("uscl/an/book_lohisplitnodeinfo");
		
		List<SubGroupInfo> subGroupInfoList = new ArrayList<SubGroupInfo>();
		SubGroupInfo subGroupInfo = new SubGroupInfo();
		subGroupInfo.setHeading("2014");
		List<String> titleList = new ArrayList<String>();
		titleList.add("uscl/an/book_lohisplitnodeinfo/v1");
		titleList.add("uscl/an/book_lohisplitnodeinfo_pt2/v1");
		subGroupInfo.setTitles(titleList);
		subGroupInfoList.add(subGroupInfo);
		groupDefinition.setSubGroupInfoList(subGroupInfoList);

		
		try {
			String response =  proviewClient.createGroup(groupDefinition);
			Assert.assertEquals(response.length(),0);
			proviewClient.setGetGroupUriTemplate("http://"
					+ PROVIEW_DOMAIN_PREFIX + "/v1/group/{groupId}/{groupVersionNumber}/info");
			response = proviewClient.getProviewGroupInfo("uscl/groupTest","v1");
			Assert.assertEquals(response.length(),325);
			updateStatusAndDelete(groupDefinition);
			
		} catch (ProviewRuntimeException e) {
			e.printStackTrace();
		}
	}
	
	private void updateStatusAndDelete(GroupDefinition groupDefinition) throws Exception{
		proviewClient.setRemoveGroupStatusUriTemplate("http://"
		+ "proviewpublishing.int.demo.thomsonreuters.com" + "/v1/group/{groupId}/{groupVersionNumber}/status/Removed");
		proviewClient.setProviewHostname("proviewpublishing.int.demo.thomsonreuters.com");
		groupDefinition.setGroupId("uscl/groupTest");
		groupDefinition.setGroupVersion("v1");
		proviewClient.setDeleteGroupUriTemplate("http://"
				+ "proviewpublishing.int.demo.thomsonreuters.com" + "/v1/group/{groupId}/{groupVersionNumber}");

		
		try {
			String response = proviewClient.removeGroup(groupDefinition.getGroupId(),groupDefinition.getGroupVersion());
			Assert.assertEquals(response.contains("Group status changed to Removed"),true);
			response = proviewClient.deleteGroup(groupDefinition.getGroupId(),groupDefinition.getGroupVersion());
			Assert.assertEquals(response.length(),0);
			
		} catch (ProviewRuntimeException e) {
			e.printStackTrace();
			// expected
		}
		
	}
	
	
	

/*	@Test
	public void testGetAllTitlesFailsDueToInvalidCredetials() throws Exception {

		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();

		defaultHttpClient = new DefaultHttpClient();
		defaultHttpClient.getCredentialsProvider().setCredentials(
				new AuthScope(PROVIEW_DOMAIN_PREFIX, AuthScope.ANY_PORT),
				new UsernamePasswordCredentials(PROVIEW_INVALID_USERNAME,
						PROVIEW_INVALID_PASSWORD));
		requestFactory.setHttpClient(defaultHttpClient);

		RestTemplate restTemplate = new RestTemplate(requestFactory);
		proviewClient.setRestTemplate(restTemplate);

		proviewClient.setGetTitlesUriTemplate("http://" + PROVIEW_DOMAIN_PREFIX
				+ getTitlesUriTemplate);

		try {
			proviewClient.getAllPublishedTitles();

		} catch (Exception e) {
			System.out.println(e.getMessage());
			// expected
		}
	}*/
}
