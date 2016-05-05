package com.thomsonreuters.uscl.ereader.group.service;

import java.io.IOException;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.thomsonreuters.uscl.ereader.deliver.service.GroupDefinition;
import com.thomsonreuters.uscl.ereader.deliver.service.GroupDefinition.SubGroupInfo;


public class GroupDefinitionParserTest {

	private GroupDefinitionParser parser;

	@Before
	public void setUp() throws IOException {
		parser = new GroupDefinitionParser();
	}
	
	@Test
	public void parseOneGroupTest() {
		String groupId = "uscl/b_subgroup";
		String groupStatus = "Review";
		Long groupVersion = 1L;
		String groupName = "group name";
		String type = "standard";
		String headtitle = "uscl/an/b_subgroup/v1";
		String subgroupHeading = "sub heading";
		String xml = "<groups><group id=\""+ groupId +"\" status=\""+ groupStatus +"\" version=\"v"+ groupVersion +"\">"
				+ "<name>" + groupName + "</name><type>"+type+"</type><headtitle>"+headtitle+"</headtitle><members>"
				+ "<subgroup heading=\""+subgroupHeading+"\"><title>"+headtitle+"</title></subgroup>"
				+ "</members></group></groups>";
		
		try {
			List<GroupDefinition> groupDefinitions = parser.parse(xml);
			
			Assert.assertEquals(1, groupDefinitions.size());
			
			GroupDefinition group = groupDefinitions.get(0);
			Assert.assertEquals(groupId, group.getGroupId());
			Assert.assertEquals(groupStatus, group.getStatus());
			Assert.assertEquals(groupVersion, group.getGroupVersion());
			Assert.assertEquals(headtitle, group.getHeadTitle());
			Assert.assertEquals(groupName, group.getName());
			Assert.assertEquals(type, group.getType());
			Assert.assertEquals(1, group.getSubGroupInfoList().size());
			
			SubGroupInfo subgroup = group.getSubGroupInfoList().get(0);
			Assert.assertEquals(subgroupHeading, subgroup.getHeading());
			Assert.assertEquals(1, subgroup.getTitles().size());
			Assert.assertEquals(headtitle, subgroup.getTitles().get(0));
		} catch(Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void parseTwoGroupTest() {
		String groupId = "uscl/b_subgroup";
		String groupStatus = "Review";
		Long groupVersion = 1L;
		String groupName = "group name";
		String type = "standard";
		String headtitle = "uscl/an/b_subgroup/v1";
		String subgroupHeading = "sub heading";
		String xml = "<groups><group id=\""+ groupId +"\" status=\""+ groupStatus +"\" version=\"v"+ groupVersion +"\">"
				+ "<name>" + groupName + "</name><type>"+type+"</type><headtitle>"+headtitle+"</headtitle><members>"
				+ "<subgroup heading=\""+subgroupHeading+"\"><title>"+headtitle+"</title></subgroup>"
				+ "</members></group><group id=\""+ groupId +"\" status=\""+ groupStatus +"\" version=\"v"+ groupVersion +"\">"
				+ "<name>" + groupName + "</name><type>"+type+"</type><headtitle>"+headtitle+"</headtitle><members>"
				+ "<subgroup heading=\""+subgroupHeading+"\"><title>"+headtitle+"</title></subgroup>"
				+ "</members></group></groups>";
		
		try {
			List<GroupDefinition> groupDefinitions = parser.parse(xml);
			
			Assert.assertEquals(2, groupDefinitions.size());
			
			GroupDefinition group = groupDefinitions.get(0);
			Assert.assertEquals(groupId, group.getGroupId());
			Assert.assertEquals(groupStatus, group.getStatus());
			Assert.assertEquals(groupVersion, group.getGroupVersion());
			Assert.assertEquals(headtitle, group.getHeadTitle());
			Assert.assertEquals(groupName, group.getName());
			Assert.assertEquals(type, group.getType());
			Assert.assertEquals(1, group.getSubGroupInfoList().size());
			
			SubGroupInfo subgroup = group.getSubGroupInfoList().get(0);
			Assert.assertEquals(subgroupHeading, subgroup.getHeading());
			Assert.assertEquals(1, subgroup.getTitles().size());
			Assert.assertEquals(headtitle, subgroup.getTitles().get(0));
			
			GroupDefinition group2 = groupDefinitions.get(1);
			Assert.assertEquals(groupId, group2.getGroupId());
			Assert.assertEquals(groupStatus, group2.getStatus());
			Assert.assertEquals(groupVersion, group2.getGroupVersion());
			Assert.assertEquals(headtitle, group2.getHeadTitle());
			Assert.assertEquals(groupName, group2.getName());
			Assert.assertEquals(type, group2.getType());
			Assert.assertEquals(1, group2.getSubGroupInfoList().size());
			
			subgroup = group2.getSubGroupInfoList().get(0);
			Assert.assertEquals(subgroupHeading, subgroup.getHeading());
			Assert.assertEquals(1, subgroup.getTitles().size());
			Assert.assertEquals(headtitle, subgroup.getTitles().get(0));
		} catch(Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void noSubgroupHeadingTest() {
		String groupId = "uscl/b_subgroup";
		String groupStatus = "Review";
		Long groupVersion = 1L;
		String groupName = "group name";
		String type = "standard";
		String headtitle = "uscl/an/b_subgroup/v1";
		String xml = "<groups><group id=\""+ groupId +"\" status=\""+ groupStatus +"\" version=\"v"+ groupVersion +"\">"
				+ "<name>" + groupName + "</name><type>"+type+"</type><headtitle>"+headtitle+"</headtitle><members>"
				+ "<subgroup heading=\"\"><title>"+headtitle+"</title></subgroup>"
				+ "</members></group></groups>";
		
		try {
			List<GroupDefinition> groupDefinitions = parser.parse(xml);
			
			Assert.assertEquals(1, groupDefinitions.size());
			
			GroupDefinition group = groupDefinitions.get(0);
			Assert.assertEquals(groupId, group.getGroupId());
			Assert.assertEquals(groupStatus, group.getStatus());
			Assert.assertEquals(groupVersion, group.getGroupVersion());
			Assert.assertEquals(headtitle, group.getHeadTitle());
			Assert.assertEquals(groupName, group.getName());
			Assert.assertEquals(type, group.getType());
			Assert.assertEquals(1, group.getSubGroupInfoList().size());
			
			SubGroupInfo subgroup = group.getSubGroupInfoList().get(0);
			Assert.assertNull(subgroup.getHeading());
			Assert.assertEquals(1, subgroup.getTitles().size());
			Assert.assertEquals(headtitle, subgroup.getTitles().get(0));
		} catch(Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void parseNoStatusErrorTest() {
		String groupId = "uscl/b_subgroup";
		Long groupVersion = 1L;
		String groupName = "group name";
		String type = "standard";
		String headtitle = "uscl/an/b_subgroup/v1";
		String subgroupHeading = "sub heading";
		String xml = "<groups><group id=\""+ groupId +"\" version=\"v"+ groupVersion +"\">"
				+ "<name>" + groupName + "</name><type>"+type+"</type><headtitle>"+headtitle+"</headtitle><members>"
				+ "<subgroup heading=\""+subgroupHeading+"\"><title>"+headtitle+"</title></subgroup>"
				+ "</members></group></groups>";
		
		try {
			parser.parse(xml);
		} catch(Exception e) {
			e.printStackTrace();
			Assert.assertNotNull(e.getMessage());
		}
	}

}
