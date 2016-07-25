/*
* Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.deliver.service;

import java.io.File;
import java.io.FileOutputStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
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
import com.thomsonreuters.uscl.ereader.deliver.rest.ProviewXMLRequestCallback;
import com.thomsonreuters.uscl.ereader.deliver.service.GroupDefinition.SubGroupInfo;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewGroup.GroupDetails;

/**
 * Component tests for ProviewClientImpl.
 * 
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris
 *         Schwartz</a> u0081674
 * @author <a href="mailto:zack.farrell@thomsonreuters.com">Zack Farrell</a>
 *         uc209819
 */
public class ProviewClientImplTest {
	// private static final Logger LOG =
	// LogManager.getLogger(ProviewClientImplTest.class);

	private static final String PROVIEW_DOMAIN_PREFIX = "proviewpublishing.int.qed.thomsonreuters.com";
	private static InetAddress PROVIEW_HOST;
	private static Map<String, String> urlParameters = new HashMap<String, String>();
	private String allGroupsUriTemplate = "http://{proviewHost}/v1/group/{groupId}/{groupVersionNumber}";
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
	public void setUp() throws Exception {
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
	public void tearDown() throws Exception {

	}

	@Test
	public void testGetAllLatestProviewGroupInfo() throws Exception {

		proviewClient.setAllGroupsUriTemplate(allGroupsUriTemplate);
		Map<String, String> urlParameters = new HashMap<String, String>();
		urlParameters.put(ProviewClientImpl.PROVIEW_HOST_PARAM, PROVIEW_HOST.getHostName());

		EasyMock.expect(mockRequestCallbackFactory.getStreamRequestCallback()).andReturn(mockRequestCallback);
		EasyMock.expect(mockResponseExtractorFactory.getResponseExtractor()).andReturn(mockResponseExtractor);
		EasyMock.expect(mockRestTemplate.execute(allGroupsUriTemplate, HttpMethod.GET, mockRequestCallback,
				mockResponseExtractor, urlParameters))
				.andReturn("<groups><group id=\"uscl/abook_testgroup\" status=\"Review\" version=\"v2\">"
						+ "<name>Group1</name><type>standard</type><headtitle>uscl/an/abook_testgroup/v1</headtitle>"
						+ "<members><subgroup heading=\"2010\"><title>uscl/an/abook_testgroup/v1</title>"
						+ "<title>uscl/an/abook_testgroup_pt2/v1</title></subgroup></members></group>"
						+ "<group id=\"uscl/abook_testgroup\" status=\"Final\" version=\"v1\"><name>Group1</name>"
						+ "<type>standard</type><headtitle>uscl/an/abook_testgroup</headtitle><members><subgroup>"
						+ "<title>uscl/an/abook_testgroup</title></subgroup></members></group></groups>");

		replayAll();
		List<ProviewGroup> proviewGroups = proviewClient.getAllLatestProviewGroupInfo();
		Assert.assertEquals(1, proviewGroups.size());
		Assert.assertEquals((Integer) 2, proviewGroups.get(0).getTotalNumberOfVersions());
	}

	@Test
	public void testGetProviewGroupContainerById() throws Exception {
		String groupId = "testGroupId";
		String response = "<groups><group id=\"" + groupId + "\" status=\"Test\" version=\"v1\">"
				+ "<name>Test Group</name><type>standard</type><headtitle>testTitleId</headtitle>"
				+ "<members><subgroup><title>test1</title><title>test2</title></subgroup></members>"
				+ "</group></groups>";

		String singleGroupUriTemplate = "";
		proviewClient.setSingleGroupUriTemplate(singleGroupUriTemplate);
		Map<String, String> urlParameters = new HashMap<String, String>();
		urlParameters.put(ProviewClientImpl.PROVIEW_HOST_PARAM, PROVIEW_HOST.getHostName());
		urlParameters.put("groupId", groupId);

		EasyMock.expect(mockRequestCallbackFactory.getXMLRequestCallback()).andReturn(mockXMLRequestCallback);
		EasyMock.expect(mockResponseExtractorFactory.getResponseExtractor()).andReturn(mockResponseExtractor);
		EasyMock.expect(mockRestTemplate.execute(singleGroupUriTemplate, HttpMethod.GET, mockXMLRequestCallback,
				mockResponseExtractor, urlParameters)).andReturn(response);
		replayAll();

		ProviewGroupContainer groupContainer = proviewClient.getProviewGroupContainerById(groupId);

		Assert.assertNotNull(groupContainer);
		Assert.assertNotNull(groupContainer.getProviewGroups());
		Assert.assertNotNull(groupContainer.getProviewGroups().get(0));
	}

	@Test
	public void testGetProviewGroupInfo() throws Exception {

		getTitlesUriTemplate = "/v1/group/groupId/groupVersionNumber/info";

		proviewClient.setGetGroupUriTemplate("http://" + PROVIEW_DOMAIN_PREFIX + getTitlesUriTemplate);

		EasyMock.expect(mockRequestCallbackFactory.getXMLRequestCallback()).andReturn(mockXMLRequestCallback);
		EasyMock.expect(mockResponseExtractorFactory.getResponseExtractor()).andReturn(mockResponseExtractor);
		EasyMock.expect(mockRestTemplate.execute("http://" + PROVIEW_DOMAIN_PREFIX + getTitlesUriTemplate,
				HttpMethod.GET, mockXMLRequestCallback, mockResponseExtractor, createURLParameters())).andReturn("");

		replayAll();
		String response = proviewClient.getProviewGroupInfo("uscl/groupTest", "v1");
		System.out.println("response " + response);
		verifyAll();
		Assert.assertEquals("", response);
	}

	@Test
	public void testGetAllLatestProviewGroupInfoByMap() throws Exception {
		Map<String, ProviewGroupContainer> map = new HashMap<String, ProviewGroupContainer>();
		ProviewGroupContainer groupContainer = new ProviewGroupContainer();
		ProviewGroup group = new ProviewGroup();
		group.setGroupVersion("v2");
		List<ProviewGroup> groupList = new ArrayList<ProviewGroup>();
		groupList.add(group);
		groupContainer.setProviewGroups(groupList);
		map.put("testGroupId", groupContainer);

		List<ProviewGroup> proviewGroups = proviewClient.getAllLatestProviewGroupInfo(map);
		Assert.assertEquals(1, proviewGroups.size());
	}

	@Test
	public void testCreateGroup() throws Exception {

		getTitlesUriTemplate = "/v1/group/groupId/groupVersionNumber";

		proviewClient.setCreateGroupUriTemplate("http://" + PROVIEW_DOMAIN_PREFIX + getTitlesUriTemplate);

		EasyMock.expect(mockRequestCallbackFactory.getXMLRequestCallback()).andReturn(mockXMLRequestCallback);
		EasyMock.expect(mockResponseExtractorFactory.getResponseExtractor()).andReturn(mockResponseExtractor);
		EasyMock.expect(mockRestTemplate.execute("http://" + PROVIEW_DOMAIN_PREFIX + getTitlesUriTemplate,
				HttpMethod.PUT, mockXMLRequestCallback, mockResponseExtractor, createURLParameters())).andReturn("");

		replayAll();
		String response = proviewClient.createGroup(mockGroupDefinition);
		System.out.println("response " + response);
		verifyAll();

		Assert.assertEquals("", response);
	}

	@Test
	public void testPromoteGroup() throws Exception {

		getTitlesUriTemplate = "/v1/group/groupId/groupVersionNumber";

		proviewClient.setCreateGroupUriTemplate("http://" + PROVIEW_DOMAIN_PREFIX + getTitlesUriTemplate);
		proviewClient.setPromoteGroupStatusUriTemplate("http://" + PROVIEW_DOMAIN_PREFIX + getTitlesUriTemplate);

		EasyMock.expect(mockRequestCallbackFactory.getXMLRequestCallback()).andReturn(mockXMLRequestCallback);
		EasyMock.expect(mockResponseExtractorFactory.getResponseExtractor()).andReturn(mockResponseExtractor);
		EasyMock.expect(mockRestTemplate.execute("http://" + PROVIEW_DOMAIN_PREFIX + getTitlesUriTemplate,
				HttpMethod.PUT, mockXMLRequestCallback, mockResponseExtractor, createURLParameters())).andReturn("");

		replayAll();
		String response = proviewClient.promoteGroup(mockGroupDefinition.getGroupId(),
				mockGroupDefinition.getProviewGroupVersionString());
		System.out.println("response " + response);
		verifyAll();

		Assert.assertEquals("", response);
	}

	@Test
	public void testRemoveGroup() throws Exception {

		getTitlesUriTemplate = "/v1/group/groupId/groupVersionNumber/status/removed";

		proviewClient.setRemoveGroupStatusUriTemplate("http://" + PROVIEW_DOMAIN_PREFIX + getTitlesUriTemplate);

		EasyMock.expect(mockRequestCallbackFactory.getXMLRequestCallback()).andReturn(mockXMLRequestCallback);
		EasyMock.expect(mockResponseExtractorFactory.getResponseExtractor()).andReturn(mockResponseExtractor);
		EasyMock.expect(mockRestTemplate.execute("http://" + PROVIEW_DOMAIN_PREFIX + getTitlesUriTemplate,
				HttpMethod.PUT, mockXMLRequestCallback, mockResponseExtractor, createURLParameters())).andReturn("");

		replayAll();
		String response = proviewClient.removeGroup(mockGroupDefinition.getGroupId(),
				mockGroupDefinition.getProviewGroupVersionString());
		System.out.println("response " + response);
		verifyAll();

		Assert.assertEquals("", response);
	}

	@Test
	public void testDeleteGroup() throws Exception {

		getTitlesUriTemplate = "/v1/group/groupId/groupVersionNumber";

		proviewClient.setCreateGroupUriTemplate("http://" + PROVIEW_DOMAIN_PREFIX + getTitlesUriTemplate);
		proviewClient.setDeleteGroupUriTemplate("http://" + PROVIEW_DOMAIN_PREFIX + getTitlesUriTemplate);

		EasyMock.expect(mockRequestCallbackFactory.getXMLRequestCallback()).andReturn(mockXMLRequestCallback);
		EasyMock.expect(mockResponseExtractorFactory.getResponseExtractor()).andReturn(mockResponseExtractor);
		EasyMock.expect(mockRestTemplate.execute("http://" + PROVIEW_DOMAIN_PREFIX + getTitlesUriTemplate,
				HttpMethod.DELETE, mockXMLRequestCallback, mockResponseExtractor, createURLParameters())).andReturn("");

		replayAll();
		String response = proviewClient.deleteGroup(mockGroupDefinition.getGroupId(),
				mockGroupDefinition.getProviewGroupVersionString());
		System.out.println("response " + response);
		verifyAll();

		Assert.assertEquals("", response);
	}

	@Test
	public void testGetLatestProviewTitleInfo() throws Exception {
		String singleTitleTemplate = "/v1/titles/titleId";

		proviewClient.setSingleTitleTemplate("http://" + PROVIEW_DOMAIN_PREFIX + singleTitleTemplate);

		Map<String, String> urlParameters = new HashMap<String, String>();
		urlParameters.put(ProviewClientImpl.PROVIEW_HOST_PARAM, PROVIEW_HOST.getHostName());
		urlParameters.put("titleId", "uscl/sc/ca_evid");

		String response = "<titles apiversion=\"v1\" publisher=\"uscl\" status=\"all\"><title id=\"uscl/sc/ca_evid\" version=\"v1.0\" publisher=\"uscl\" lastupdate=\"20150508\" status=\"Cleanup\">Handbook of Practical Planning for Art	Collectors and Their Advisors</title></titles>";
		EasyMock.expect(mockRequestCallbackFactory.getStreamRequestCallback()).andReturn(mockRequestCallback);
		EasyMock.expect(mockResponseExtractorFactory.getResponseExtractor()).andReturn(mockResponseExtractor);
		EasyMock.expect(mockRestTemplate.execute("http://" + PROVIEW_DOMAIN_PREFIX + singleTitleTemplate,
				HttpMethod.GET, mockRequestCallback, mockResponseExtractor, urlParameters)).andReturn(response);

		replayAll();
		ProviewTitleInfo titleInfo = proviewClient.getLatestProviewTitleInfo("uscl/sc/ca_evid");

		Assert.assertEquals("uscl/sc/ca_evid", titleInfo.getTitleId());
	}

	@Test
	public void testGetSingleTitleGroupDetails() throws Exception {
		String singleTitleTemplate = "/v1/titles/titleId";

		proviewClient.setSingleTitleTemplate("http://" + PROVIEW_DOMAIN_PREFIX + singleTitleTemplate);

		Map<String, String> urlParameters = new HashMap<String, String>();
		urlParameters.put(ProviewClientImpl.PROVIEW_HOST_PARAM, PROVIEW_HOST.getHostName());
		urlParameters.put("titleId", "uscl/sc/ca_evid");

		String response = "<titles apiversion=\"v1\" publisher=\"uscl\" status=\"all\"><title id=\"uscl/sc/ca_evid\" version=\"v1.0\" publisher=\"uscl\" lastupdate=\"20150508\" status=\"Cleanup\">Handbook of Practical Planning for Art	Collectors and Their Advisors</title></titles>";
		EasyMock.expect(mockRequestCallbackFactory.getStreamRequestCallback()).andReturn(mockRequestCallback);
		EasyMock.expect(mockResponseExtractorFactory.getResponseExtractor()).andReturn(mockResponseExtractor);
		EasyMock.expect(mockRestTemplate.execute("http://" + PROVIEW_DOMAIN_PREFIX + singleTitleTemplate,
				HttpMethod.GET, mockRequestCallback, mockResponseExtractor, urlParameters)).andReturn(response);

		replayAll();
		List<GroupDetails> groupDetailsList = proviewClient.getSingleTitleGroupDetails("uscl/sc/ca_evid");

		Assert.assertEquals(1, groupDetailsList.size());
	}

	@Test
	public void testGetAllLatestProviewTitleInfo() throws Exception {
		proviewClient.setGetTitlesUriTemplate("http://" + PROVIEW_DOMAIN_PREFIX + getTitlesUriTemplate);

		String response = "<titles apiversion=\"v1\" publisher=\"uscl\" status=\"all\"><title id=\"uscl/sc/ca_evid\" version=\"v1.0\" publisher=\"uscl\" lastupdate=\"20150508\" status=\"Cleanup\">Handbook of Practical Planning for Art	Collectors and Their Advisors</title></titles>";

		EasyMock.expect(mockRequestCallbackFactory.getStreamRequestCallback()).andReturn(mockRequestCallback);
		EasyMock.expect(mockResponseExtractorFactory.getResponseExtractor()).andReturn(mockResponseExtractor);
		EasyMock.expect(mockRestTemplate.execute("http://" + PROVIEW_DOMAIN_PREFIX + getTitlesUriTemplate,
				HttpMethod.GET, mockRequestCallback, mockResponseExtractor, urlParameters)).andReturn(response);

		replayAll();
		List<ProviewTitleInfo> titleInfo = proviewClient.getAllLatestProviewTitleInfo();
		verifyAll();

		Assert.assertEquals(1, titleInfo.size());

	}

	@Test
	public void testGetAllLatestProviewTitleInfoByMap() throws Exception {
		Map<String, ProviewTitleContainer> map = new HashMap<String, ProviewTitleContainer>();
		ProviewTitleContainer groupContainer = new ProviewTitleContainer();
		ProviewTitleInfo title = new ProviewTitleInfo();
		title.setVersion("v2");
		List<ProviewTitleInfo> titleList = new ArrayList<ProviewTitleInfo>();
		titleList.add(title);
		groupContainer.setProviewTitleInfos(titleList);
		map.put("testGroupId", groupContainer);

		List<ProviewTitleInfo> proviewGroups = proviewClient.getAllLatestProviewTitleInfo(map);
		Assert.assertEquals(1, proviewGroups.size());
	}

	@Test
	public void getSingleTitleInfoByVersion() throws Exception {
		String singleTitleByVersionUriTemplate = "/v1/titles/titleId/eBookVersionNumber";

		proviewClient.setSingleTitleByVersionUriTemplate(
				"http://" + PROVIEW_DOMAIN_PREFIX + singleTitleByVersionUriTemplate);

		Map<String, String> urlParameters = new HashMap<String, String>();
		urlParameters.put(ProviewClientImpl.PROVIEW_HOST_PARAM, PROVIEW_HOST.getHostName());
		urlParameters.put("titleId", "uscl/an/coi");
		urlParameters.put("eBookVersionNumber", "v1.0");

		EasyMock.expect(mockRequestCallbackFactory.getXMLRequestCallback()).andReturn(mockXMLRequestCallback);
		EasyMock.expect(mockResponseExtractorFactory.getResponseExtractor()).andReturn(mockResponseExtractor);
		EasyMock.expect(mockRestTemplate.execute("http://" + PROVIEW_DOMAIN_PREFIX + singleTitleByVersionUriTemplate,
				HttpMethod.GET, mockXMLRequestCallback, mockResponseExtractor, urlParameters)).andReturn("");

		replayAll();
		String response = proviewClient.getSingleTitleInfoByVersion("uscl/an/coi", "v1.0");
		System.out.println("response " + response);
		verifyAll();

		Assert.assertEquals("", response);
	}

	@Test
	public void testPublishTitle() throws Exception {

		String titleId = "testTileId";
		String bookVersion = "v1.2";
		String fileContents = "Have some content";
		File tempRootDir = new File(System.getProperty("java.io.tmpdir"));
		tempRootDir.mkdir();
		try {
			File eBook = makeFile(tempRootDir, "tempBookFile", fileContents);

			String publishTitleUriTemplate = "SomeURI";

			proviewClient.setPublishTitleUriTemplate(publishTitleUriTemplate);

			Map<String, String> urlParameters = new HashMap<String, String>();
			urlParameters.put(ProviewClientImpl.PROVIEW_HOST_PARAM, PROVIEW_HOST.getHostName());
			urlParameters.put("titleId", titleId);
			urlParameters.put("eBookVersionNumber", bookVersion);

			EasyMock.expect(mockRequestCallbackFactory.getStreamRequestCallback()).andReturn(mockRequestCallback);
			EasyMock.expect(mockResponseExtractorFactory.getResponseExtractor()).andReturn(mockResponseExtractor);
			EasyMock.expect(mockRestTemplate.execute(publishTitleUriTemplate, HttpMethod.PUT, mockRequestCallback,
					mockResponseExtractor, urlParameters)).andReturn("=)");
			EasyMock.expectLastCall(); // mock
										// proviewRequestCallback.setEbookInputStream(ebookInputStream);
			replayAll();

			String response = proviewClient.publishTitle(titleId, bookVersion, eBook);

			Assert.assertEquals("=)", response);
		} catch (Exception e) {
			throw e;
		} finally {
			try { // may fail due to the input stream opened in publishTitle(..)
				FileUtils.deleteDirectory(tempRootDir);
			} catch (Exception e) {
			} // The file is in the temporary files directory, not a big deal
		}
	}

	@Test
	public void testPromoteTitle() throws Exception {

		String titleId = "testTileId";
		String bookVersion = "v1.2";
		String promoteTitleUriTemplate = "SomeURI";

		proviewClient.setPromoteTitleUriTemplate(promoteTitleUriTemplate);

		Map<String, String> urlParameters = new HashMap<String, String>();
		urlParameters.put(ProviewClientImpl.PROVIEW_HOST_PARAM, PROVIEW_HOST.getHostName());
		urlParameters.put("titleId", titleId);
		urlParameters.put("eBookVersionNumber", bookVersion);

		EasyMock.expect(mockRequestCallbackFactory.getStreamRequestCallback()).andReturn(mockRequestCallback);
		EasyMock.expect(mockResponseExtractorFactory.getResponseExtractor()).andReturn(mockResponseExtractor);
		EasyMock.expect(mockRestTemplate.execute(promoteTitleUriTemplate, HttpMethod.PUT, mockRequestCallback,
				mockResponseExtractor, urlParameters)).andReturn("=)");
		EasyMock.expectLastCall();
		replayAll();

		String response = proviewClient.promoteTitle(titleId, bookVersion);

		Assert.assertEquals("=)", response);
	}

	@Test
	public void testRemoveTitle() throws Exception {

		String titleId = "testTileId";
		String bookVersion = "v1.2";
		String removeTitleUriTemplate = "SomeURI";

		proviewClient.setRemoveTitleUriTemplate(removeTitleUriTemplate);

		Map<String, String> urlParameters = new HashMap<String, String>();
		urlParameters.put(ProviewClientImpl.PROVIEW_HOST_PARAM, PROVIEW_HOST.getHostName());
		urlParameters.put("titleId", titleId);
		urlParameters.put("eBookVersionNumber", bookVersion);

		EasyMock.expect(mockRequestCallbackFactory.getStreamRequestCallback()).andReturn(mockRequestCallback);
		EasyMock.expect(mockResponseExtractorFactory.getResponseExtractor()).andReturn(mockResponseExtractor);
		EasyMock.expect(mockRestTemplate.execute(removeTitleUriTemplate, HttpMethod.PUT, mockRequestCallback,
				mockResponseExtractor, urlParameters)).andReturn("=)");
		EasyMock.expectLastCall();
		replayAll();

		String response = proviewClient.removeTitle(titleId, bookVersion);

		Assert.assertEquals("=)", response);
	}

	@Test
	public void testDeleteTitle() throws Exception {

		String titleId = "testTileId";
		String bookVersion = "v1.2";
		String deleteTitleUriTemplate = "SomeURI";

		proviewClient.setDeleteTitleUriTemplate(deleteTitleUriTemplate);

		Map<String, String> urlParameters = new HashMap<String, String>();
		urlParameters.put(ProviewClientImpl.PROVIEW_HOST_PARAM, PROVIEW_HOST.getHostName());
		urlParameters.put("titleId", titleId);
		urlParameters.put("eBookVersionNumber", bookVersion);

		EasyMock.expect(mockRequestCallbackFactory.getStreamRequestCallback()).andReturn(mockRequestCallback);
		EasyMock.expect(mockResponseExtractorFactory.getResponseExtractor()).andReturn(mockResponseExtractor);
		EasyMock.expect(mockRestTemplate.execute(deleteTitleUriTemplate, HttpMethod.DELETE, mockRequestCallback,
				mockResponseExtractor, urlParameters)).andReturn("=)");
		EasyMock.expectLastCall();
		replayAll();

		String response = proviewClient.deleteTitle(titleId, bookVersion);

		Assert.assertEquals("=)", response);
	}

	@Test
	public void testHasTitleIdBeenPublished() throws Exception {
		proviewClient.setGetTitlesUriTemplate("http://" + PROVIEW_DOMAIN_PREFIX + getTitlesUriTemplate);
		String expectedResponse = "<title id='example' version='v1' publisher='uscl' lastupdate='20100205' status='Final'>Title</title>";

		EasyMock.expect(mockRequestCallbackFactory.getStreamRequestCallback()).andReturn(mockRequestCallback);
		EasyMock.expect(mockResponseExtractorFactory.getResponseExtractor()).andReturn(mockResponseExtractor);
		EasyMock.expect(mockRestTemplate.execute("http://" + PROVIEW_DOMAIN_PREFIX + getTitlesUriTemplate,
				HttpMethod.GET, mockRequestCallback, mockResponseExtractor, urlParameters)).andReturn(expectedResponse);
		replayAll();

		boolean reponse = proviewClient.hasTitleIdBeenPublished("example");
		Assert.assertEquals(true, reponse);
		verifyAll();
	}

	/**
	 * makeFile( File directory, String name, String content ) helper method to
	 * streamline file creation
	 * 
	 * @param directory
	 *            Location the new file will be created in
	 * @param name
	 *            Name of the new file
	 * @param content
	 *            Content to be written into the new file
	 * @return returns a File object directing to the new file returns null if
	 *         any errors occur
	 */
	private File makeFile(File directory, String name, String content) {
		try {
			File file = new File(directory, name);
			file.createNewFile();
			FileOutputStream out = new FileOutputStream(file);
			out.write(content.getBytes());
			out.flush();
			out.close();
			return file;
		} catch (Exception e) {
			return null;
		}
	}

	private Map<String, String> createURLParameters() {
		Map<String, String> urlParameters = new HashMap<String, String>();
		urlParameters.put(ProviewClientImpl.PROVIEW_HOST_PARAM, PROVIEW_HOST.getHostName());
		urlParameters.put("groupId", "uscl/groupTest");
		urlParameters.put("groupVersionNumber", "v1");
		return urlParameters;
	}

	private GroupDefinition createGroupDef() {

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
