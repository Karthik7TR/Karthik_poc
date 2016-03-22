/*
* Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.deliver.service;

import static org.junit.Assert.assertTrue;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.thomsonreuters.uscl.ereader.GroupDefinition;
import com.thomsonreuters.uscl.ereader.GroupDefinition.SubGroupInfo;
import com.thomsonreuters.uscl.ereader.deliver.rest.ProviewRequestCallback;
import com.thomsonreuters.uscl.ereader.deliver.rest.ProviewRequestCallbackFactory;
import com.thomsonreuters.uscl.ereader.deliver.rest.ProviewResponseExtractor;
import com.thomsonreuters.uscl.ereader.deliver.rest.ProviewResponseExtractorFactory;
import com.thomsonreuters.uscl.ereader.deliver.rest.ProviewXMLRequestCallback;

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
	private static Map<String, String> urlParameters = new HashMap<String, String>();
	private String getTitlesUriTemplate = "/v1/titles/uscl/all";
	private ProviewClientImpl proviewClient;
	private RestTemplate mockRestTemplate;
	@SuppressWarnings("rawtypes")
	private ResponseEntity mockResponseEntity;
	private HttpHeaders mockHeaders;
	private ProviewRequestCallbackFactory mockRequestCallbackFactory;
	private ProviewResponseExtractorFactory mockResponseExtractorFactory;
	private ProviewRequestCallback mockRequestCallback;
	private ProviewXMLRequestCallback mockXMLRequestCallback;
	private ProviewResponseExtractor mockResponseExtractor;
	private GroupDefinition mockGroupDefinition;
	
	@Before
	public void setUp() throws Exception
	{
		this.proviewClient = new ProviewClientImpl();
		PROVIEW_HOST = InetAddress.getLocalHost();
		
		urlParameters.put(ProviewClientImpl.PROVIEW_HOST_PARAM, PROVIEW_HOST.getHostName());
		mockRequestCallback = new ProviewRequestCallback();
		mockXMLRequestCallback = new ProviewXMLRequestCallback();
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
		mockGroupDefinition = createGroupDef();
	}

	@After
	public void tearDown() throws Exception
	{
		
	}
	
	@Test
	public void getSingleTitleInfoByVersion() throws Exception{
		String singleTitleByVersionUriTemplate =  "/v1/titles/titleId/eBookVersionNumber";
		
		proviewClient.setSingleTitleByVersionUriTemplate("http://"
				+ PROVIEW_DOMAIN_PREFIX + singleTitleByVersionUriTemplate);
		
		Map<String, String> urlParameters = new HashMap<String, String>();
		urlParameters.put(ProviewClientImpl.PROVIEW_HOST_PARAM, PROVIEW_HOST.getHostName());
		urlParameters.put("titleId", "uscl/an/coi");
		urlParameters.put("eBookVersionNumber", "v1.0");
		
		EasyMock.expect(mockRequestCallbackFactory.getXMLRequestCallback()).andReturn(mockXMLRequestCallback);
		EasyMock.expect(mockResponseExtractorFactory.getResponseExtractor()).andReturn(mockResponseExtractor);
		EasyMock.expect(mockRestTemplate.execute("http://" + PROVIEW_DOMAIN_PREFIX + singleTitleByVersionUriTemplate, HttpMethod.GET, mockXMLRequestCallback, mockResponseExtractor, urlParameters)).andReturn("");
		
		replayAll();
		String response = proviewClient.getSingleTitleInfoByVersion("uscl/an/coi", "v1.0");
		System.out.println("response "+response);
		verifyAll();
			
		Assert.assertEquals("", response);
	}
	
	@Test
	public void testRequestBody()
			throws Exception {
				
		String expectedBody = "<group id=\"uscl/groupTest\"><name>Group Test</name><type>standard</type>"
				+ "<headtitle>uscl/sc/ca_evid</headtitle><members><subgroup heading=\"2014\"><title>uscl/sc/ca_evid</title><title>uscl/an/ilcv</title></subgroup></members></group>";
		Assert.assertEquals(expectedBody,proviewClient.buildRequestBody(mockGroupDefinition));		
		
	}
	
	
	@Test
	public void testGetAllTitlesHappyPath() throws Exception {
		proviewClient.setGetTitlesUriTemplate("http://" + PROVIEW_DOMAIN_PREFIX + getTitlesUriTemplate);
				
		String expectedResponse = "YARR!";
		
		EasyMock.expect(mockRequestCallbackFactory.getStreamRequestCallback()).andReturn(mockRequestCallback);
		EasyMock.expect(mockResponseExtractorFactory.getResponseExtractor()).andReturn(mockResponseExtractor);
		EasyMock.expect(mockRestTemplate.execute("http://" + PROVIEW_DOMAIN_PREFIX + getTitlesUriTemplate, HttpMethod.GET, mockRequestCallback, mockResponseExtractor, urlParameters)).andReturn("YARR!");
		
		replayAll();
		String response = proviewClient.getAllPublishedTitles();
		verifyAll();
		
		assertTrue("Response did not match expected result!", response.equals(expectedResponse));
	}
	
	@Test
	public void testHasTitleIdBeenPublished() throws Exception {
		proviewClient.setGetTitlesUriTemplate("http://" + PROVIEW_DOMAIN_PREFIX + getTitlesUriTemplate);
		String expectedResponse = "<title id='example' version='v1' publisher='uscl' lastupdate='20100205' status='Final'>Title</title>";
		
		EasyMock.expect(mockRequestCallbackFactory.getStreamRequestCallback()).andReturn(mockRequestCallback);
		EasyMock.expect(mockResponseExtractorFactory.getResponseExtractor()).andReturn(mockResponseExtractor);
		EasyMock.expect(mockRestTemplate.execute("http://" + PROVIEW_DOMAIN_PREFIX + getTitlesUriTemplate, HttpMethod.GET, mockRequestCallback, mockResponseExtractor, urlParameters)).andReturn(expectedResponse);
		replayAll();
		
		boolean reponse = proviewClient.hasTitleIdBeenPublished("example");
		Assert.assertEquals(true, reponse);
		verifyAll();
	}
	
	@Test
	public void testCreateGroup() throws Exception {
			
		getTitlesUriTemplate =  "/v1/group/groupId/groupVersionNumber";
		
		proviewClient.setCreateGroupUriTemplate("http://"
				+ PROVIEW_DOMAIN_PREFIX + getTitlesUriTemplate);
		
		EasyMock.expect(mockRequestCallbackFactory.getXMLRequestCallback()).andReturn(mockXMLRequestCallback);
		EasyMock.expect(mockResponseExtractorFactory.getResponseExtractor()).andReturn(mockResponseExtractor);
		EasyMock.expect(mockRestTemplate.execute("http://" + PROVIEW_DOMAIN_PREFIX + getTitlesUriTemplate, HttpMethod.PUT, mockXMLRequestCallback, mockResponseExtractor, createURLParameters())).andReturn("");
		
		replayAll();
		String response = proviewClient.createGroup(mockGroupDefinition);
		System.out.println("response "+response);
		verifyAll();
			
		Assert.assertEquals("", response);
	}
	
	@Test
	public void testGetGroup() throws Exception {
			
		getTitlesUriTemplate =  "/v1/group/groupId/groupVersionNumber/info";
		
		proviewClient.setGetGroupUriTemplate("http://"
				+ PROVIEW_DOMAIN_PREFIX + getTitlesUriTemplate);
		
		EasyMock.expect(mockRequestCallbackFactory.getXMLRequestCallback()).andReturn(mockXMLRequestCallback);
		EasyMock.expect(mockResponseExtractorFactory.getResponseExtractor()).andReturn(mockResponseExtractor);
		EasyMock.expect(mockRestTemplate.execute("http://" + PROVIEW_DOMAIN_PREFIX + getTitlesUriTemplate, HttpMethod.GET, mockXMLRequestCallback, mockResponseExtractor, createURLParameters())).andReturn("");
		
		replayAll();
		String response = proviewClient.getProviewGroupInfo("uscl/groupTest","v1");
		System.out.println("response "+response);
		verifyAll();
		Assert.assertEquals("", response);
	}
	
	@Test
	public void testDeleteGroup() throws Exception {
			
		getTitlesUriTemplate =  "/v1/group/groupId/groupVersionNumber";
				
		proviewClient.setCreateGroupUriTemplate("http://"
				+ PROVIEW_DOMAIN_PREFIX + getTitlesUriTemplate);
		proviewClient.setDeleteGroupUriTemplate("http://"
				+ PROVIEW_DOMAIN_PREFIX + getTitlesUriTemplate);
		
		
		EasyMock.expect(mockRequestCallbackFactory.getXMLRequestCallback()).andReturn(mockXMLRequestCallback);
		EasyMock.expect(mockResponseExtractorFactory.getResponseExtractor()).andReturn(mockResponseExtractor);
		EasyMock.expect(mockRestTemplate.execute("http://" + PROVIEW_DOMAIN_PREFIX + getTitlesUriTemplate, HttpMethod.DELETE, mockXMLRequestCallback, mockResponseExtractor, createURLParameters())).andReturn("");
		
		replayAll();
		String response = proviewClient.deleteGroup(mockGroupDefinition.getGroupId(),mockGroupDefinition.getProviewGroupVersionString());
		System.out.println("response "+response);
		verifyAll();
			
		Assert.assertEquals("", response);
	}
	
	@Test
	public void testUpdateGroupStatus() throws Exception {
			
		getTitlesUriTemplate =  "/v1/group/groupId/groupVersionNumber/status/removed";
		
		proviewClient.setRemoveGroupStatusUriTemplate("http://"
				+ PROVIEW_DOMAIN_PREFIX + getTitlesUriTemplate);		
		
		EasyMock.expect(mockRequestCallbackFactory.getXMLRequestCallback()).andReturn(mockXMLRequestCallback);
		EasyMock.expect(mockResponseExtractorFactory.getResponseExtractor()).andReturn(mockResponseExtractor);
		EasyMock.expect(mockRestTemplate.execute("http://" + PROVIEW_DOMAIN_PREFIX + getTitlesUriTemplate, HttpMethod.PUT, mockXMLRequestCallback, mockResponseExtractor, createURLParameters())).andReturn("");
		
		replayAll();
		String response = proviewClient.removeGroup(mockGroupDefinition.getGroupId(),mockGroupDefinition.getProviewGroupVersionString());
		System.out.println("response "+response);
		verifyAll();
			
		Assert.assertEquals("", response);
	}
	
	@Test
	public void testAllGroupStatus() throws Exception {
			
		getTitlesUriTemplate =  "/v1/group/groupId/groupVersionNumber/status/removed";
		
		proviewClient.setRemoveGroupStatusUriTemplate("http://"
				+ PROVIEW_DOMAIN_PREFIX + getTitlesUriTemplate);		
		
		EasyMock.expect(mockRequestCallbackFactory.getXMLRequestCallback()).andReturn(mockXMLRequestCallback);
		EasyMock.expect(mockResponseExtractorFactory.getResponseExtractor()).andReturn(mockResponseExtractor);
		EasyMock.expect(mockRestTemplate.execute("http://" + PROVIEW_DOMAIN_PREFIX + getTitlesUriTemplate, HttpMethod.PUT, mockXMLRequestCallback, mockResponseExtractor, createURLParameters())).andReturn("");
		
		replayAll();
		String response = proviewClient.removeGroup(mockGroupDefinition.getGroupId(),mockGroupDefinition.getProviewGroupVersionString());
		//System.out.println("response "+response);
		verifyAll();
			
		Assert.assertEquals("", response);
	}
	
	@Test
	public void testGetAllGroups() throws Exception {
		String allGroupsUriTemplate="/v1/group/uscl";
		
		proviewClient.setAllGroupsUriTemplate("http://"
				+ PROVIEW_DOMAIN_PREFIX + allGroupsUriTemplate);
		
		String expectedResponse = "YARR!";
		
		EasyMock.expect(mockRequestCallbackFactory.getStreamRequestCallback()).andReturn(mockRequestCallback);
		EasyMock.expect(mockResponseExtractorFactory.getResponseExtractor()).andReturn(mockResponseExtractor);
		EasyMock.expect(mockRestTemplate.execute("http://" + PROVIEW_DOMAIN_PREFIX + allGroupsUriTemplate, HttpMethod.GET, mockRequestCallback, mockResponseExtractor, urlParameters)).andReturn("YARR!");
		
		replayAll();
		String response = proviewClient.getAllProviewGroups();
		verifyAll();		
		assertTrue("Response did not match expected result!", response.equals(expectedResponse));

	}
	
	@Test
	public void testSinglePublishedTitle() throws Exception {
		String singleTitleTemplate = "/v1/titles/titleId";
		
		proviewClient.setSingleTitleTemplate("http://"
				+ PROVIEW_DOMAIN_PREFIX + singleTitleTemplate);
		
		Map<String, String> urlParameters = new HashMap<String, String>();
		urlParameters.put(ProviewClientImpl.PROVIEW_HOST_PARAM, PROVIEW_HOST.getHostName());
		urlParameters.put("titleId", "uscl/sc/ca_evid");
		
		String expectedResponse = "YARR!";
		EasyMock.expect(mockRequestCallbackFactory.getStreamRequestCallback()).andReturn(mockRequestCallback);
		EasyMock.expect(mockResponseExtractorFactory.getResponseExtractor()).andReturn(mockResponseExtractor);
		EasyMock.expect(mockRestTemplate.execute("http://" + PROVIEW_DOMAIN_PREFIX + singleTitleTemplate, HttpMethod.GET, mockRequestCallback, mockResponseExtractor, urlParameters)).andReturn("YARR!");		
		
		replayAll();
		String response = proviewClient.getSinglePublishedTitle("uscl/sc/ca_evid");
		verifyAll();		
		Assert.assertEquals(expectedResponse, response);

	}
	
	private Map<String, String> createURLParameters(){
		Map<String, String> urlParameters = new HashMap<String, String>();
		urlParameters.put(ProviewClientImpl.PROVIEW_HOST_PARAM, PROVIEW_HOST.getHostName());
		urlParameters.put("groupId", "uscl/groupTest");
		urlParameters.put("groupVersionNumber", "v1");
		return urlParameters;
	}
	
	private GroupDefinition createGroupDef(){
		
		GroupDefinition groupDefinition = new GroupDefinition();
		
		groupDefinition.setGroupId("uscl/groupTest");
		groupDefinition.setProviewGroupVersionString("v1");
		groupDefinition.setName("Group Test");
		groupDefinition.setType("standard");
		groupDefinition.setOrder("newerfirst");
		groupDefinition.setHeadTitle("uscl/sc/ca_evid");
		
		List<SubGroupInfo> subGroupInfoList = new ArrayList<SubGroupInfo>();
		SubGroupInfo subGroupInfo = new SubGroupInfo();
		subGroupInfo.setHeading("2014");
		List<String> titleList = new ArrayList<String>();
		titleList.add("uscl/sc/ca_evid");
		titleList.add("uscl/an/ilcv");
		subGroupInfo.setTitles(titleList);
		subGroupInfoList.add(subGroupInfo);
		groupDefinition.setSubGroupInfoList(subGroupInfoList);
		
		return groupDefinition;
		
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
