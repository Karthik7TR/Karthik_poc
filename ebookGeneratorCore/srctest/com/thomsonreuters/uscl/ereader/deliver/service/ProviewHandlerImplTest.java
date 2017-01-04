/*
* Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.deliver.service;

import java.io.File;
import java.io.FileOutputStream;
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
import com.thomsonreuters.uscl.ereader.deliver.service.GroupDefinition.SubGroupInfo;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewGroup.GroupDetails;

/**
 * Component tests for ProviewHandlerImpl.
 * 
 * @author <a href="mailto:zack.farrell@thomsonreuters.com">Zack Farrell</a> uc209819
 */
public class ProviewHandlerImplTest {
	// private static final Logger LOG = LogManager.getLogger(ProviewHandlerImplTest.class);

	private ProviewHandlerImpl proviewHandler;

	private static final String GROUP_ID = "uscl/test_group_id";

	private ProviewClient mockProviewClient;
	private GroupDefinition groupDefinition;

	@Before
	public void setUp() throws Exception {
		this.proviewHandler = new ProviewHandlerImpl();
		mockProviewClient = EasyMock.createMock(ProviewClient.class);
		proviewHandler.setProviewClient(mockProviewClient);

	}

	@After
	public void tearDown() throws Exception {

	}

	private String getGroupsRequestXml(GroupDefinition groupDefinition) {
		String buffer = "<group id=\"" + groupDefinition.getGroupId() + "\"><name>" + groupDefinition.getName() + "</name><type>"
				+ groupDefinition.getType() + "</type><headtitle>" + groupDefinition.getHeadTitle() + "</headtitle><members>";
		for (SubGroupInfo subgroup : groupDefinition.getSubGroupInfoList()) {
			if (subgroup.getHeading() == null) {
				buffer += "<subgroup>";
			} else {
				buffer += "<subgroup heading=\"" + subgroup.getHeading() + "\">";
			}
			for (String title : subgroup.getTitles()) {
				buffer += "<title>" + title + "</title>";
			}
			buffer += "</subgroup>";
		}
		buffer += "</members></group>";
		return buffer;
	}

	private String getGroupsResponseXml(GroupDefinition groupDefinition) {
		String buffer = "<group id=\"" + groupDefinition.getGroupId() + "\" status=\"" + groupDefinition.getStatus() + "\" " + "version=\"v"
				+ groupDefinition.getGroupVersion() + "\">" + "<name>" + groupDefinition.getName() + "</name><type>" + groupDefinition
						.getType() + "</type><headtitle>" + groupDefinition.getHeadTitle() + "</headtitle><members>";
		for (SubGroupInfo subgroup : groupDefinition.getSubGroupInfoList()) {
			if (subgroup.getHeading() == null) {
				buffer += "<subgroup>";
			} else {
				buffer += "<subgroup heading=\"" + subgroup.getHeading() + "\">";
			}
			for (String title : subgroup.getTitles()) {
				buffer += "<title>" + title + "</title>";
			}
			buffer += "</subgroup>";
		}
		buffer += "</members></group>";
		return buffer;
	}

	private void initGroupDef() {
		groupDefinition = new GroupDefinition();
		groupDefinition.setGroupId(GROUP_ID);
		groupDefinition.setGroupVersion(1L);
		groupDefinition.setHeadTitle("uscl/test/title_id");
		groupDefinition.setName("Group Name");
		groupDefinition.setStatus("test");
		groupDefinition.setType("someType");
		groupDefinition.setSubGroupInfoList(new ArrayList<SubGroupInfo>());
	}

	private void initSubgroupHeading() {
		SubGroupInfo subgroup = new SubGroupInfo();
		subgroup.setHeading("2017");
		subgroup.addTitle("test1");
		subgroup.addTitle("test2");
		groupDefinition.addSubGroupInfo(subgroup);
	}

	@Test
	public void testGetAllLatestProviewGroupInfo() throws Exception {

		String response = "<groups><group id=\"uscl/abook_testgroup\" status=\"Review\" version=\"v2\">"
				+ "<name>Group1</name><type>standard</type><headtitle>uscl/an/abook_testgroup/v1</headtitle>"
				+ "<members><subgroup heading=\"2010\"><title>uscl/an/abook_testgroup/v1</title>"
				+ "<title>uscl/an/abook_testgroup_pt2/v1</title></subgroup></members></group>"
				+ "<group id=\"uscl/abook_testgroup\" status=\"Final\" version=\"v1\"><name>Group1</name>"
				+ "<type>standard</type><headtitle>uscl/an/abook_testgroup</headtitle><members><subgroup>"
				+ "<title>uscl/an/abook_testgroup</title></subgroup></members></group></groups>";
		EasyMock.expect(mockProviewClient.getAllProviewGroups()).andReturn(response);
		EasyMock.replay(mockProviewClient);

		List<ProviewGroup> proviewGroups = proviewHandler.getAllLatestProviewGroupInfo();
		Assert.assertEquals(1, proviewGroups.size());
		Assert.assertEquals((Integer) 2, proviewGroups.get(0).getTotalNumberOfVersions());
	}

	@Test
	public void testGetProviewGroupContainerById() throws Exception {
		initGroupDef();
		initSubgroupHeading();
		String response = getGroupsResponseXml(groupDefinition);
		EasyMock.expect(mockProviewClient.getProviewGroupById(GROUP_ID)).andReturn(response);
		EasyMock.replay(mockProviewClient);

		ProviewGroupContainer groupContainer = proviewHandler.getProviewGroupContainerById(GROUP_ID);

		Assert.assertNotNull(groupContainer);
		Assert.assertNotNull(groupContainer.getProviewGroups());
		Assert.assertEquals(1, groupContainer.getProviewGroups().size());
	}

	@Test
	public void testGetGroupDefinitionByVersion() throws Exception {
		initGroupDef();
		initSubgroupHeading();
		String response = getGroupsResponseXml(groupDefinition);

		EasyMock.expect(mockProviewClient.getProviewGroupInfo(GROUP_ID, groupDefinition.getProviewGroupVersionString())).andReturn(
				response);
		EasyMock.replay(mockProviewClient);

		GroupDefinition groupDef = proviewHandler.getGroupDefinitionByVersion(GROUP_ID, groupDefinition.getGroupVersion());
		Assert.assertTrue(groupDefinition.equals(groupDef));
	}

	@Test
	public void testBuildRequestBodyNoSubgroup() throws Exception {
		initGroupDef();
		String expected = getGroupsRequestXml(groupDefinition);
		String actual = proviewHandler.buildRequestBody(groupDefinition);

		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testCreateGroup() throws Exception {
		initGroupDef();

		EasyMock.expect(mockProviewClient.createGroup(groupDefinition.getGroupId(), groupDefinition.getProviewGroupVersionString(),
				getGroupsRequestXml(groupDefinition))).andReturn("");
		EasyMock.replay(mockProviewClient);

		String response = proviewHandler.createGroup(groupDefinition);

		Assert.assertEquals("", response);
	}

	@Test
	public void testPromoteGroup() throws Exception {
		initGroupDef();

		EasyMock.expect(mockProviewClient.promoteGroup(groupDefinition.getGroupId(), groupDefinition.getProviewGroupVersionString()))
				.andReturn("");
		EasyMock.replay(mockProviewClient);

		String response = proviewHandler.promoteGroup(groupDefinition.getGroupId(), groupDefinition.getProviewGroupVersionString());

		Assert.assertEquals("", response);
	}

	@Test
	public void testRemoveGroup() throws Exception {
		initGroupDef();

		EasyMock.expect(mockProviewClient.removeGroup(groupDefinition.getGroupId(), groupDefinition.getProviewGroupVersionString()))
				.andReturn("");
		EasyMock.replay(mockProviewClient);

		String response = proviewHandler.removeGroup(groupDefinition.getGroupId(), groupDefinition.getProviewGroupVersionString());

		Assert.assertEquals("", response);
	}

	@Test
	public void testDeleteGroup() throws Exception {
		initGroupDef();

		EasyMock.expect(mockProviewClient.deleteGroup(groupDefinition.getGroupId(), groupDefinition.getProviewGroupVersionString()))
				.andReturn("=)");
		EasyMock.replay(mockProviewClient);

		String response = proviewHandler.deleteGroup(groupDefinition.getGroupId(), groupDefinition.getProviewGroupVersionString());

		Assert.assertEquals("=)", response);
	}

	@Test
	public void testGetLatestProviewTitleInfo() throws Exception {
		String titleId = "testTileId";
		String latest = "20200101";
		String response = "<titles><title id=\"" + titleId + "\" version=\"v1.0\" publisher=\"uscl\" "
				+ "lastupdate=\"20150508\" status=\"Cleanup\">Test Book Name</title>"
				+ "<title id=\"" + titleId + "\" version=\"v1.1\" publisher=\"uscl\" "
				+ "lastupdate=\"20150509\" status=\"Cleanup\">Test Book Name</title>"
				+ "<title id=\"" + titleId + "\" version=\"v2.0\" publisher=\"uscl\" "
				+ "lastupdate=\"20150510\" status=\"Cleanup\">Test Book Name</title>"
				+ "<title id=\"" + titleId + "\" version=\"v2.1\" publisher=\"uscl\" "
				+ "lastupdate=\""+latest+"\" status=\"Cleanup\">Test Book Name</title></titles>";
		EasyMock.expect(mockProviewClient.getSinglePublishedTitle(titleId)).andReturn(response);
		EasyMock.replay(mockProviewClient);

		ProviewTitleInfo titleInfo = proviewHandler.getLatestProviewTitleInfo(titleId);

		Assert.assertEquals(titleId, titleInfo.getTitleId());
		Assert.assertEquals(latest, titleInfo.getLastupdate());
	}

	@Test
	public void testGetSingleTitleGroupDetails() throws Exception {
		String titleId = "testTileId";
		String response = "<titles><title id=\"" + titleId + "\" version=\"v1.0\" publisher=\"uscl\" "
				+ "lastupdate=\"20150508\" status=\"Cleanup\">Test Book Name</title>"
				+ "<title id=\"" + titleId + "\" version=\"v1.1\" publisher=\"uscl\" "
				+ "lastupdate=\"20150508\" status=\"Cleanup\">Test Book Name</title>"
				+ "<title id=\"" + titleId + "\" version=\"v2.0\" publisher=\"uscl\" "
				+ "lastupdate=\"20150508\" status=\"Cleanup\">Test Book Name</title>"
				+ "<title id=\"" + titleId + "\" version=\"v2.1\" publisher=\"uscl\" "
				+ "lastupdate=\"20150508\" status=\"Cleanup\">Test Book Name</title></titles>";
		EasyMock.expect(mockProviewClient.getSinglePublishedTitle(titleId)).andReturn(response);
		EasyMock.replay(mockProviewClient);
		
		List<GroupDetails> groupDetailsList = proviewHandler.getSingleTitleGroupDetails(titleId);

		Assert.assertEquals(4, groupDetailsList.size());
		Assert.assertEquals(titleId, groupDetailsList.get(0).getTitleId());
	}

	@Test
	public void testGetAllLatestProviewTitleInfo() throws Exception {
		String response = "<titles apiversion=\"v1\" publisher=\"uscl\" status=\"all\">"
				+ "<title id=\"uscl/abadocs/art\" version=\"v1.0\" publisher=\"uscl\" "
				+ "lastupdate=\"20150508\" status=\"Cleanup\"> Handbook of Practical "
				+ "Planning for Art Collectors and Their Advisors</title>"
				+ "<title id=\"uscl/abadocs/art\" version=\"v1.1\" publisher=\"uscl\" "
				+ "lastupdate=\"20150508\" status=\"Review\">Handbook of Practical "
				+ "Planning for Art Collectors and Their Advisors </title></titles>";
		EasyMock.expect(mockProviewClient.getAllPublishedTitles()).andReturn(response);
		EasyMock.replay(mockProviewClient);

		List<ProviewTitleInfo> titleInfo = proviewHandler.getAllLatestProviewTitleInfo();

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

		List<ProviewTitleInfo> proviewGroups = proviewHandler.getAllLatestProviewTitleInfo(map);
		Assert.assertEquals(1, proviewGroups.size());
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

			EasyMock.expect(mockProviewClient.publishTitle(titleId, bookVersion, eBook)).andReturn("=)");
			EasyMock.replay(mockProviewClient);

			String response = proviewHandler.publishTitle(titleId, bookVersion, eBook);

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

		EasyMock.expect(mockProviewClient.promoteTitle(titleId, bookVersion)).andReturn("=)");
		EasyMock.replay(mockProviewClient);

		String response = proviewHandler.promoteTitle(titleId, bookVersion);

		Assert.assertEquals("=)", response);
	}

	@Test
	public void testRemoveTitle() throws Exception {

		String titleId = "testTileId";
		String bookVersion = "v1.2";

		EasyMock.expect(mockProviewClient.removeTitle(titleId, bookVersion)).andReturn("=)");
		EasyMock.replay(mockProviewClient);

		String response = proviewHandler.removeTitle(titleId, bookVersion);

		Assert.assertEquals("=)", response);
	}

	@Test
	public void testDeleteTitle() throws Exception {

		String titleId = "testTileId";
		String bookVersion = "v1.2";

		boolean response = proviewHandler.deleteTitle(titleId, bookVersion);

		Assert.assertEquals(true, response);
	}

	@Test
	public void testHasTitleIdBeenPublishedNoBook() throws Exception {
		String titleId = "testTileId";
		EasyMock.expect(mockProviewClient.getSinglePublishedTitle(titleId)).andReturn("<title></title>");
		EasyMock.replay(mockProviewClient);

		boolean reponse = proviewHandler.hasTitleIdBeenPublished(titleId);
		Assert.assertTrue(!reponse);
	}

	@Test
	public void testHasTitleIdBeenPublishedFalse() throws Exception {
		String titleId = "testTileId";
		EasyMock.expect(mockProviewClient.getSinglePublishedTitle(titleId)).andReturn("<title id=\"" + titleId
				+ "\" version=\"v1.0\" publisher=\"uscl\" lastupdate=\"20150508\" status=\"Cleanup\">Test Book Name</title>");
		EasyMock.replay(mockProviewClient);

		boolean response = proviewHandler.hasTitleIdBeenPublished(titleId);
		Assert.assertTrue(!response);
	}
	
	@Test
	public void testHasTitleIdBeenPublishedTrue() throws Exception {
		String titleId = "testTileId";
		EasyMock.expect(mockProviewClient.getSinglePublishedTitle(titleId)).andReturn("<title id=\"" + titleId
				+ "\" version=\"v1.0\" publisher=\"uscl\" lastupdate=\"20150508\" status=\"final\">Test Book Name</title>");
		EasyMock.replay(mockProviewClient);

		boolean response = proviewHandler.hasTitleIdBeenPublished(titleId);
		Assert.assertTrue(response);
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

		List<ProviewGroup> proviewGroups = proviewHandler.getAllLatestProviewGroupInfo(map);
		Assert.assertEquals(1, proviewGroups.size());
	}

	/**
	 * makeFile( File directory, String name, String content ) helper method to streamline file creation
	 * 
	 * @param directory Location the new file will be created in
	 * @param name Name of the new file
	 * @param content Content to be written into the new file
	 * @return returns a File object directing to the new file returns null if any errors occur
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

}
