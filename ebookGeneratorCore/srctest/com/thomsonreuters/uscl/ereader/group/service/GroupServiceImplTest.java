package com.thomsonreuters.uscl.ereader.group.service;

import java.util.ArrayList;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.thomsonreuters.uscl.ereader.GroupDefinition;
import com.thomsonreuters.uscl.ereader.GroupDefinition.SubGroupInfo;
import com.thomsonreuters.uscl.ereader.core.CoreConstants;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.PublisherCode;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewClient;

public class GroupServiceImplTest {
	
	private GroupServiceImpl groupService;
	String groupInfoXML;
	List<String> splitTitles;
	
	@Before
	public void setUp() throws Exception
	{
		this.groupService = new GroupServiceImpl();
		groupInfoXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<group id=\"uscl/grouptest\" status=\"Review\"><name>Group Test</name><type>standard</type><headtitle>uscl/an/book_lohisplitnodeinfo/v2</headtitle>"
				+ "<members><subgroup heading=\"2015\"><title>uscl/an/book_lohisplitnodeinfo/v2</title><title>uscl/an/book_lohisplitnodeinfo_pt2/v2</title></subgroup>"
				+ "<subgroup heading=\"2014\"><title>uscl/an/book_lohisplitnodeinfo/v1</title><title>uscl/an/book_lohisplitnodeinfo_pt2/v1</title></subgroup></members></group>";
		 splitTitles = new ArrayList<String>();
		 splitTitles.add("splitTitle");
		 splitTitles.add("splitTitle_pt2");
		 splitTitles.add("splitTitle_pt3");
	}

	@After
	public void tearDown() throws Exception
	{
		
	}
	
	@Test
	public void testTitleWithVersion(){
		Assert.assertTrue(groupService.isTitleWithVersion("us/an/ascl/v1.0"));
		Assert.assertTrue(groupService.isTitleWithVersion("us/an/ascl/v1"));
		Assert.assertTrue(groupService.isTitleWithVersion("us/an/ascl/v10.00"));
		Assert.assertFalse(groupService.isTitleWithVersion("us/an/ascl/va"));
		Assert.assertTrue(groupService.isTitleWithVersion("us/an/vascl/v1"));
	}
	
	@Test
	public void testGetGroupInfoByVersion() throws Exception {
		ProviewClient proviewClient = EasyMock.createMock(ProviewClient.class);
		groupService.setProviewClient(proviewClient);
		
		EasyMock.expect(proviewClient.getProviewGroupInfo("uscl/groupT", "v10")).andReturn("");
    	EasyMock.replay(proviewClient);
		
		String response = groupService.getGroupInfoByVersion("uscl/groupT", new Long(10));
		Assert.assertEquals("",response);
	}
	
	@Test
	public void testBuildGroupDefinitionForSplits() throws Exception{
		
		GroupDefinition groupDef = groupService.buildGroupDefinition("groupName", "2015", "splitTitle", "v1",splitTitles);
		
		Assert.assertEquals("splitTitle/v1",groupDef.getHeadTitle());
		Assert.assertEquals("groupName",groupDef.getName());
		Assert.assertEquals(1,groupDef.getSubGroupInfoList().size());
		
		Assert.assertEquals("2015",groupDef.getSubGroupInfoList().get(0).getHeading());
		Assert.assertEquals(3,groupDef.getSubGroupInfoList().get(0).getTitles().size());
		Assert.assertEquals("splitTitle/v1",groupDef.getSubGroupInfoList().get(0).getTitles().get(0));
		Assert.assertEquals("splitTitle_pt2/v1",groupDef.getSubGroupInfoList().get(0).getTitles().get(1));
		Assert.assertEquals("splitTitle_pt3/v1",groupDef.getSubGroupInfoList().get(0).getTitles().get(2));
		
	}
	
	@Test
	public void testBuildGroupDefinitionForSplitsException() throws Exception{
		boolean thrown = false;
		try{
			groupService.buildGroupDefinition("groupName","", "uscl/an/book_singlebookgroup", "v2",splitTitles);
		}
		catch(ProviewException ex){
			thrown = true;
			Assert.assertTrue(ex.getMessage().contains("Subgroup name cannot be empty"));
		}
		
		Assert.assertTrue(thrown);
		
	}
	
	
	@Test
	public void testGetNoGroupDef() throws Exception {
		groupInfoXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<group id=\"uscl/grouptest\" status=\"Review\"><name>Group Test</name><type>standard</type><headtitle>uscl/an/book_lohisplitnodeinfo/v2</headtitle>"
				+ "<members><subgroup heading=\"2015\"><title>uscl/an/book_lohisplitnodeinfo/v2</title><title>uscl/an/book_lohisplitnodeinfo_pt2/v2</title></subgroup>"
				+ "<subgroup heading=\"2014\"><title>uscl/an/book_lohisplitnodeinfo/v1</title><title>uscl/an/book_lohisplitnodeinfo_pt2/v1</title></subgroup></members></group>";
			splitTitles = new ArrayList<String>();
			splitTitles.add("uscl/an/book_lohisplitnodeinfo/v1");
			splitTitles.add("uscl/an/book_lohisplitnodeinfo_pt2/v1");
			Assert.assertEquals(null,groupService.getGroupDefinitionforAllBooks(groupInfoXML, "v2", "Group Test", "2015", "uscl/an/book_lohisplitnodeinfo", splitTitles, false, true));
	}
	
	@Test
	public void testDuplicateSubgroupSplitBook() throws Exception {
		groupInfoXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<group id=\"uscl/grouptest\" status=\"Review\"><name>Group Test</name><type>standard</type><headtitle>uscl/an/book_lohisplitnodeinfo/v2</headtitle>"
				+ "<members><subgroup heading=\"2015\"><title>uscl/an/book_lohisplitnodeinfo/v2</title></subgroup>"
				+ "<subgroup heading=\"2014\"><title>uscl/an/book_lohisplitnodeinfo/v1</title></subgroup></members></group>";
		boolean thrown = false;
		splitTitles = new ArrayList<String>();
		splitTitles.add("uscl/an/book_lohisplitnodeinfo/v3");
		splitTitles.add("uscl/an/book_lohisplitnodeinfo_pt2/v3");
		BookDefinition bookDefinition = new BookDefinition();
		bookDefinition.setSubGroupHeading("2014");
		bookDefinition.setEbookDefinitionId(new Long(1));
		try{
			groupService.validateResponse(bookDefinition, groupInfoXML, "v3");
			//.getGroupDefinitionForSplitBooks(groupInfoXML, "v3", "Group Test", "2014", "uscl/an/book_lohisplitnodeinfo",splitTitles);
		}catch(ProviewException ex){
				thrown = true;
				Assert.assertTrue(ex.getMessage().contains(CoreConstants.DUPLICATE_SUBGROUP_ERROR_MESSAGE));
			}
		Assert.assertTrue(thrown);
	}
	
	
	@Test
	public void testGetGroupDefVersionChange() throws Exception {
			splitTitles = new ArrayList<String>();
			splitTitles.add("uscl/an/book_lohisplitnodeinfo");
			splitTitles.add("uscl/an/book_lohisplitnodeinfo_pt2");
			
			GroupDefinition groupDef = groupService.getGroupDefinitionforAllBooks(groupInfoXML, "v3", "Group Test", "2016", "uscl/an/book_lohisplitnodeinfo", splitTitles, true, true);
			
			Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v3",groupDef.getHeadTitle());
			Assert.assertEquals("Group Test",groupDef.getName());
			Assert.assertEquals(3,groupDef.getSubGroupInfoList().size());
			
			Assert.assertEquals("2016",groupDef.getSubGroupInfoList().get(0).getHeading());
			Assert.assertEquals(2,groupDef.getSubGroupInfoList().get(0).getTitles().size());
			Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v3",groupDef.getSubGroupInfoList().get(0).getTitles().get(0));
			Assert.assertEquals("uscl/an/book_lohisplitnodeinfo_pt2/v3",groupDef.getSubGroupInfoList().get(0).getTitles().get(1));
			
			Assert.assertEquals("2015",groupDef.getSubGroupInfoList().get(1).getHeading());
			Assert.assertEquals(2,groupDef.getSubGroupInfoList().get(1).getTitles().size());
			Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v2",groupDef.getSubGroupInfoList().get(1).getTitles().get(0));
			Assert.assertEquals("uscl/an/book_lohisplitnodeinfo_pt2/v2",groupDef.getSubGroupInfoList().get(1).getTitles().get(1));
			
			Assert.assertEquals("2014",groupDef.getSubGroupInfoList().get(2).getHeading());
			Assert.assertEquals(2,groupDef.getSubGroupInfoList().get(2).getTitles().size());
			Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v1",groupDef.getSubGroupInfoList().get(2).getTitles().get(0));
			Assert.assertEquals("uscl/an/book_lohisplitnodeinfo_pt2/v1",groupDef.getSubGroupInfoList().get(2).getTitles().get(1));
	}
	
	@Test
	public void testGetGroupDefGroupNameChangeOnlyChange() throws Exception {
			splitTitles = new ArrayList<String>();
			splitTitles.add("uscl/an/book_lohisplitnodeinfo");
			splitTitles.add("uscl/an/book_lohisplitnodeinfo_pt2");
			boolean versionChange = false;
			
			GroupDefinition groupDef = groupService.getGroupDefinitionforAllBooks(groupInfoXML, "v2", "Group Test1", "2015", "uscl/an/book_lohisplitnodeinfo", splitTitles, versionChange, true);
			
			Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v2",groupDef.getHeadTitle());
			Assert.assertEquals("Group Test1",groupDef.getName());
			Assert.assertEquals(2,groupDef.getSubGroupInfoList().size());
			
			Assert.assertEquals("2015",groupDef.getSubGroupInfoList().get(0).getHeading());
			Assert.assertEquals(2,groupDef.getSubGroupInfoList().get(0).getTitles().size());
			Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v2",groupDef.getSubGroupInfoList().get(0).getTitles().get(0));
			Assert.assertEquals("uscl/an/book_lohisplitnodeinfo_pt2/v2",groupDef.getSubGroupInfoList().get(0).getTitles().get(1));
			
			Assert.assertEquals("2014",groupDef.getSubGroupInfoList().get(1).getHeading());
			Assert.assertEquals(2,groupDef.getSubGroupInfoList().get(1).getTitles().size());
			Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v1",groupDef.getSubGroupInfoList().get(1).getTitles().get(0));
			Assert.assertEquals("uscl/an/book_lohisplitnodeinfo_pt2/v1",groupDef.getSubGroupInfoList().get(1).getTitles().get(1));
	}
	
	@Test
	public void testGetGroupDefGpSubGpNameChange() throws Exception {
			splitTitles = new ArrayList<String>();
			splitTitles.add("uscl/an/book_lohisplitnodeinfo");
			splitTitles.add("uscl/an/book_lohisplitnodeinfo_pt2");
			//GroupDefinition groupDef = groupService.getGroupDefinitionForSplitBooks(groupInfoXML, "v2", "Group Test1", "2016", "uscl/an/book_lohisplitnodeinfo", splitTitles);
			
			boolean versionChange = false;
			
			GroupDefinition groupDef = groupService.getGroupDefinitionforAllBooks(groupInfoXML, "v2", "Group Test1", "2016", "uscl/an/book_lohisplitnodeinfo", splitTitles, versionChange, true);
			
			Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v2",groupDef.getHeadTitle());
			Assert.assertEquals("Group Test1",groupDef.getName());
			Assert.assertEquals(2,groupDef.getSubGroupInfoList().size());
			
			Assert.assertEquals("2016",groupDef.getSubGroupInfoList().get(0).getHeading());
			Assert.assertEquals(2,groupDef.getSubGroupInfoList().get(0).getTitles().size());
			Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v2",groupDef.getSubGroupInfoList().get(0).getTitles().get(0));
			Assert.assertEquals("uscl/an/book_lohisplitnodeinfo_pt2/v2",groupDef.getSubGroupInfoList().get(0).getTitles().get(1));
			
			Assert.assertEquals("2014",groupDef.getSubGroupInfoList().get(1).getHeading());
			Assert.assertEquals(2,groupDef.getSubGroupInfoList().get(1).getTitles().size());
			Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v1",groupDef.getSubGroupInfoList().get(1).getTitles().get(0));
			Assert.assertEquals("uscl/an/book_lohisplitnodeinfo_pt2/v1",groupDef.getSubGroupInfoList().get(1).getTitles().get(1));
	}
	
	@Test
	public void testGetGroupDefSubGpNameChangeOnlyChange() throws Exception {
			splitTitles = new ArrayList<String>();
			splitTitles.add("uscl/an/book_lohisplitnodeinfo");
			splitTitles.add("uscl/an/book_lohisplitnodeinfo_pt2");
			
			boolean versionChange = false;
			
			GroupDefinition groupDef = groupService.getGroupDefinitionforAllBooks(groupInfoXML, "v2", "Group Test", "2016", "uscl/an/book_lohisplitnodeinfo", splitTitles, versionChange, true);
			
			Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v2",groupDef.getHeadTitle());
			Assert.assertEquals("Group Test",groupDef.getName());
			Assert.assertEquals(2,groupDef.getSubGroupInfoList().size());
			
			Assert.assertEquals("2016",groupDef.getSubGroupInfoList().get(0).getHeading());
			Assert.assertEquals(2,groupDef.getSubGroupInfoList().get(0).getTitles().size());
			Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v2",groupDef.getSubGroupInfoList().get(0).getTitles().get(0));
			Assert.assertEquals("uscl/an/book_lohisplitnodeinfo_pt2/v2",groupDef.getSubGroupInfoList().get(0).getTitles().get(1));
			
			Assert.assertEquals("2014",groupDef.getSubGroupInfoList().get(1).getHeading());
			Assert.assertEquals(2,groupDef.getSubGroupInfoList().get(1).getTitles().size());
			Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v1",groupDef.getSubGroupInfoList().get(1).getTitles().get(0));
			Assert.assertEquals("uscl/an/book_lohisplitnodeinfo_pt2/v1",groupDef.getSubGroupInfoList().get(1).getTitles().get(1));
	}
	
	@Test
	public void testGetGroupDefSplitChange() throws Exception {
			splitTitles = new ArrayList<String>();
			splitTitles.add("uscl/an/book_lohisplitnodeinfo");
			splitTitles.add("uscl/an/book_lohisplitnodeinfo_pt2");
			splitTitles.add("uscl/an/book_lohisplitnodeinfo_pt3");
			
			boolean versionChange = false;
			
			GroupDefinition groupDef = groupService.getGroupDefinitionforAllBooks(groupInfoXML, "v2", "Group Test", "2015", "uscl/an/book_lohisplitnodeinfo", splitTitles, versionChange, true);
			
			Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v2",groupDef.getHeadTitle());
			Assert.assertEquals("Group Test",groupDef.getName());
			Assert.assertEquals(2,groupDef.getSubGroupInfoList().size());
			
			Assert.assertEquals("2015",groupDef.getSubGroupInfoList().get(0).getHeading());
			Assert.assertEquals(3,groupDef.getSubGroupInfoList().get(0).getTitles().size());
			Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v2",groupDef.getSubGroupInfoList().get(0).getTitles().get(0));
			Assert.assertEquals("uscl/an/book_lohisplitnodeinfo_pt2/v2",groupDef.getSubGroupInfoList().get(0).getTitles().get(1));
			Assert.assertEquals("uscl/an/book_lohisplitnodeinfo_pt3/v2",groupDef.getSubGroupInfoList().get(0).getTitles().get(2));
			
			Assert.assertEquals("2014",groupDef.getSubGroupInfoList().get(1).getHeading());
			Assert.assertEquals(2,groupDef.getSubGroupInfoList().get(1).getTitles().size());
			Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v1",groupDef.getSubGroupInfoList().get(1).getTitles().get(0));
			Assert.assertEquals("uscl/an/book_lohisplitnodeinfo_pt2/v1",groupDef.getSubGroupInfoList().get(1).getTitles().get(1));
	}
	
	@Test
	public void testGetGroupDefinitionException() throws Exception{
		boolean thrown = false;
		try{
			splitTitles = new ArrayList<String>();
			splitTitles.add("uscl/an/book_lohisplitnodeinfo");
			splitTitles.add("uscl/an/book_lohisplitnodeinfo_pt2");
			BookDefinition bookDefinition = new BookDefinition();
			bookDefinition.setSubGroupHeading("2015");
			bookDefinition.setEbookDefinitionId(new Long(1));
			bookDefinition.setIsSplitBook(true);
			
			groupService.validateResponse(bookDefinition, groupInfoXML, "v3");
		}
		catch(ProviewException ex){
			thrown = true;
			Assert.assertTrue(ex.getMessage().contains(CoreConstants.SUBGROUP_SPLIT_ERROR_MESSAGE));
		}
		
		Assert.assertTrue(thrown);
		
	}
	
	
	@Test
	public void testgetSubGroupInfo() throws Exception{
		 List<String> splitTitles = new ArrayList<String>();
		 splitTitles.add("splitTitle1");
		 splitTitles.add("splitTitle2");
		 splitTitles.add("splitTitle3");
		 
		 SubGroupInfo subGroup = groupService.getSubGroupInfo( "v1","August", splitTitles);
		 Assert.assertEquals(3,subGroup.getTitles().size());
		 Assert.assertEquals("August",subGroup.getHeading());
		
	}
	
	@Test
	public void testNoNewGroup()  throws Exception{
		groupInfoXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<group id=\"uscl/grouptest\" status=\"Review\"><name>Group Test</name><type>standard</type><headtitle>uscl/an/book_lohisplitnodeinfo</headtitle>"
				+ "<members><subgroup><title>uscl/an/book_lohisplitnodeinfo</title></subgroup>"
				+ "</members></group>";
		String newGroupName ="Group Test"; //No change in group
		String newSubgroupHeading = "";
		
		boolean versionChange = false;
		splitTitles = new ArrayList<String>();
		GroupDefinition groupDef = groupService.getGroupDefinitionforAllBooks(groupInfoXML, "v2", newGroupName, newSubgroupHeading, "uscl/an/book_lohisplitnodeinfo", null, versionChange, false);
		Assert.assertEquals(null,groupDef);
		groupDef = groupService.getGroupDefinitionforAllBooks(groupInfoXML, "v2", newGroupName, null, "uscl/an/book_lohisplitnodeinfo", null, versionChange, false);
		Assert.assertEquals(null,groupDef);
		groupDef = groupService.getGroupDefinitionforAllBooks(groupInfoXML, "v1", newGroupName, null, "uscl/an/book_lohisplitnodeinfo", null, versionChange, false);
		Assert.assertEquals(null,groupDef);
		
		groupInfoXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<group id=\"uscl/grouptest\" status=\"Review\"><name>Group Test</name><type>standard</type><headtitle>uscl/an/book_lohisplitnodeinfo/v1</headtitle>"
				+ "<members><subgroup heading=\"2014\" ><title>uscl/an/book_lohisplitnodeinfo/v1</title></subgroup>"
				+ "</members></group>";
		groupDef = groupService.getGroupDefinitionforAllBooks(groupInfoXML, "v1", newGroupName, "2014", "uscl/an/book_lohisplitnodeinfo", null, versionChange, false);
		Assert.assertEquals(null,groupDef);
	}
	
	@Test
	public void testGroupNameChangeWithNoSubGroups()  throws Exception{
		groupInfoXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<group id=\"uscl/grouptest\" status=\"Review\"><name>Group Test</name><type>standard</type><headtitle>uscl/an/book_lohisplitnodeinfo</headtitle>"
				+ "<members><subgroup><title>uscl/an/book_lohisplitnodeinfo</title></subgroup>"
				+ "</members></group>";
		String newGroupName ="Group Test1"; //No change in group
		String newSubgroupHeading = "";
		
		//GroupDefinition groupDef = groupService.getGroupDefinitionForSingleBooks(groupInfoXML,"v2",newGroupName,newSubgroupHeading,"uscl/an/book_lohisplitnodeinfo");
		boolean versionChange = false;
		splitTitles = new ArrayList<String>();
		GroupDefinition groupDef = groupService.getGroupDefinitionforAllBooks(groupInfoXML, "v2", newGroupName, newSubgroupHeading, "uscl/an/book_lohisplitnodeinfo", splitTitles, versionChange, false);
		
		
		Assert.assertEquals("uscl/an/book_lohisplitnodeinfo",groupDef.getHeadTitle());
		Assert.assertEquals("Group Test1",groupDef.getName());
		Assert.assertEquals(1,groupDef.getSubGroupInfoList().size());
		Assert.assertEquals(null,groupDef.getSubGroupInfoList().get(0).getHeading());
		Assert.assertEquals(1,groupDef.getSubGroupInfoList().get(0).getTitles().size());
		Assert.assertEquals("uscl/an/book_lohisplitnodeinfo",groupDef.getSubGroupInfoList().get(0).getTitles().get(0));
	}
	
	@Test
	public void testGroupNameChangeWithSubGroups()  throws Exception{
		groupInfoXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<group id=\"uscl/grouptest\" status=\"Review\"><name>Group Test</name><type>standard</type><headtitle>uscl/an/book_lohisplitnodeinfo/v1</headtitle>"
				+ "<members><subgroup heading=\"2014\"><title>uscl/an/book_lohisplitnodeinfo/v1</title></subgroup>"
				+ "</members></group>";
		String newGroupName ="Group Test1"; //No change in group
		String newSubgroupHeading = "2014";
		
		boolean versionChange = false;
		splitTitles = new ArrayList<String>();
		GroupDefinition groupDef = groupService.getGroupDefinitionforAllBooks(groupInfoXML, "v1", newGroupName, newSubgroupHeading, "uscl/an/book_lohisplitnodeinfo", splitTitles, versionChange, false);
		
		
		Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v1",groupDef.getHeadTitle());
		Assert.assertEquals("Group Test1",groupDef.getName());
		Assert.assertEquals(1,groupDef.getSubGroupInfoList().size());
		Assert.assertEquals("2014",groupDef.getSubGroupInfoList().get(0).getHeading());
		Assert.assertEquals(1,groupDef.getSubGroupInfoList().get(0).getTitles().size());
		Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v1",groupDef.getSubGroupInfoList().get(0).getTitles().get(0));
	}
	
	@Test
	public void testGroupAndSubGroupNameChange()  throws Exception{
		groupInfoXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<group id=\"uscl/grouptest\" status=\"Review\"><name>Group Test</name><type>standard</type><headtitle>uscl/an/book_lohisplitnodeinfo/v1</headtitle>"
				+ "<members><subgroup heading=\"2014\"><title>uscl/an/book_lohisplitnodeinfo/v1</title></subgroup>"
				+ "</members></group>";
		String newGroupName ="Group Test1"; //No change in group
		String newSubgroupHeading = "2013";
		
		boolean versionChange = false;
		splitTitles = new ArrayList<String>();
		GroupDefinition groupDef = groupService.getGroupDefinitionforAllBooks(groupInfoXML, "v1", newGroupName, newSubgroupHeading, "uscl/an/book_lohisplitnodeinfo", splitTitles, versionChange, false);
		Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v1",groupDef.getHeadTitle());
		Assert.assertEquals("Group Test1",groupDef.getName());
		Assert.assertEquals(1,groupDef.getSubGroupInfoList().size());
		Assert.assertEquals("2013",groupDef.getSubGroupInfoList().get(0).getHeading());
		Assert.assertEquals(1,groupDef.getSubGroupInfoList().get(0).getTitles().size());
		Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v1",groupDef.getSubGroupInfoList().get(0).getTitles().get(0));
	}
	
	@Test
	public void testVersionChangeOnly()  throws Exception{
		groupInfoXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<group id=\"uscl/grouptest\" status=\"Review\"><name>Group Test</name><type>standard</type><headtitle>uscl/an/book_lohisplitnodeinfo/v1</headtitle>"
				+ "<members><subgroup heading=\"2014\"><title>uscl/an/book_lohisplitnodeinfo/v1</title></subgroup>"
				+ "</members></group>";
		String newGroupName ="Group Test"; //No change in group
		String newSubgroupHeading = "2014";
		
		//GroupDefinition groupDef = groupService.getGroupDefinitionForSingleBooks(groupInfoXML,"v2",newGroupName,newSubgroupHeading,"uscl/an/book_lohisplitnodeinfo");
		boolean versionChange = true;
		splitTitles = new ArrayList<String>();
		GroupDefinition groupDef = groupService.getGroupDefinitionforAllBooks(groupInfoXML, "v2", newGroupName, newSubgroupHeading, "uscl/an/book_lohisplitnodeinfo", splitTitles, versionChange, false);
		
		Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v2",groupDef.getHeadTitle());
		Assert.assertEquals("Group Test",groupDef.getName());
		Assert.assertEquals(1,groupDef.getSubGroupInfoList().size());
		Assert.assertEquals("2014",groupDef.getSubGroupInfoList().get(0).getHeading());
		Assert.assertEquals(2,groupDef.getSubGroupInfoList().get(0).getTitles().size());
		Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v2",groupDef.getSubGroupInfoList().get(0).getTitles().get(0));
		Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v1",groupDef.getSubGroupInfoList().get(0).getTitles().get(1));
	}
	
	@Test
	public void testGroupVersionChange()  throws Exception{
		groupInfoXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<group id=\"uscl/grouptest\" status=\"Review\"><name>Group Test</name><type>standard</type><headtitle>uscl/an/book_lohisplitnodeinfo/v1</headtitle>"
				+ "<members><subgroup heading=\"2014\"><title>uscl/an/book_lohisplitnodeinfo/v1</title></subgroup>"
				+ "</members></group>";
		String newGroupName ="Group Test1"; //No change in group
		String newSubgroupHeading = "2014";
		
		boolean versionChange = true;
		splitTitles = new ArrayList<String>();
		GroupDefinition groupDef = groupService.getGroupDefinitionforAllBooks(groupInfoXML, "v2", newGroupName, newSubgroupHeading, "uscl/an/book_lohisplitnodeinfo", splitTitles, versionChange, false);
		
		
		Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v2",groupDef.getHeadTitle());
		Assert.assertEquals("Group Test1",groupDef.getName());
		Assert.assertEquals(1,groupDef.getSubGroupInfoList().size());
		Assert.assertEquals("2014",groupDef.getSubGroupInfoList().get(0).getHeading());
		Assert.assertEquals(2,groupDef.getSubGroupInfoList().get(0).getTitles().size());
		Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v2",groupDef.getSubGroupInfoList().get(0).getTitles().get(0));
	}
	
	@Test
	public void testGroupSubGroupVersionChange()  throws Exception{
		groupInfoXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<group id=\"uscl/grouptest\" status=\"Review\"><name>Group Test</name><type>standard</type><headtitle>uscl/an/book_lohisplitnodeinfo/v1</headtitle>"
				+ "<members><subgroup heading=\"2014\"><title>uscl/an/book_lohisplitnodeinfo/v1</title></subgroup>"
				+ "</members></group>";
		String newGroupName ="Group Test1"; //No change in group
		String newSubgroupHeading = "2015";
		
		boolean versionChange = true;
		splitTitles = new ArrayList<String>();
		GroupDefinition groupDef = groupService.getGroupDefinitionforAllBooks(groupInfoXML, "v2", newGroupName, newSubgroupHeading, "uscl/an/book_lohisplitnodeinfo", splitTitles, versionChange, false);
		
		
		Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v2",groupDef.getHeadTitle());
		Assert.assertEquals("Group Test1",groupDef.getName());
		Assert.assertEquals(2,groupDef.getSubGroupInfoList().size());
		
		Assert.assertEquals("2015",groupDef.getSubGroupInfoList().get(0).getHeading());
		Assert.assertEquals(1,groupDef.getSubGroupInfoList().get(0).getTitles().size());
		Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v2",groupDef.getSubGroupInfoList().get(0).getTitles().get(0));
		
		Assert.assertEquals("2014",groupDef.getSubGroupInfoList().get(1).getHeading());
		Assert.assertEquals(1,groupDef.getSubGroupInfoList().get(1).getTitles().size());
		Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v1",groupDef.getSubGroupInfoList().get(1).getTitles().get(0));
	}
	
	@Test
	public void testSubGroupVersionChange()  throws Exception{
		groupInfoXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<group id=\"uscl/grouptest\" status=\"Review\"><name>Group Test</name><type>standard</type><headtitle>uscl/an/book_lohisplitnodeinfo/v1</headtitle>"
				+ "<members><subgroup heading=\"2014\"><title>uscl/an/book_lohisplitnodeinfo/v1</title></subgroup>"
				+ "</members></group>";
		String newGroupName ="Group Test"; //No change in group
		String newSubgroupHeading = "2015";
		
		boolean versionChange = true;
		splitTitles = new ArrayList<String>();
		GroupDefinition groupDef = groupService.getGroupDefinitionforAllBooks(groupInfoXML, "v2", newGroupName, newSubgroupHeading, "uscl/an/book_lohisplitnodeinfo", splitTitles, versionChange, false);
		
		Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v2",groupDef.getHeadTitle());
		Assert.assertEquals("Group Test",groupDef.getName());
		Assert.assertEquals(2,groupDef.getSubGroupInfoList().size());
		
		Assert.assertEquals("2015",groupDef.getSubGroupInfoList().get(0).getHeading());
		Assert.assertEquals(1,groupDef.getSubGroupInfoList().get(0).getTitles().size());
		Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v2",groupDef.getSubGroupInfoList().get(0).getTitles().get(0));
		
		Assert.assertEquals("2014",groupDef.getSubGroupInfoList().get(1).getHeading());
		Assert.assertEquals(1,groupDef.getSubGroupInfoList().get(1).getTitles().size());
		Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v1",groupDef.getSubGroupInfoList().get(1).getTitles().get(0));
	}
	
	@Test
	public void testLastGroupVersion()  throws Exception{
		groupInfoXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><groups><group id=\"uscl/book_singlebookgroup\" status=\"Review\" version=\"v1\">"
				+ "<name>Single book Group</name><type>standard</type><headtitle>uscl/an/book_singlebookgroup</headtitle><members><subgroup>"
				+ "<title>uscl/an/book_singlebookgroup</title></subgroup></members></group></groups>";
		List<String> groupVersions = new ArrayList<String>();
		Assert.assertEquals("1",groupService.getLastGroupVerionFromProviewResponse(groupInfoXML,groupVersions).toString());
	}
	
	@Test
	public void testAllGroupVersions()  throws Exception{
		groupInfoXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><groups><group id=\"uscl/book_singlebookgroup\" status=\"Review\" version=\"v1\">"
				+ "<name>Single book Group</name><type>standard</type><headtitle>uscl/an/book_singlebookgroup</headtitle><members><subgroup>"
				+ "<title>uscl/an/book_singlebookgroup</title></subgroup></members></group>"
				+ "<group id=\"uscl/book_singlebookgroup\" status=\"Review\" version=\"v2\">"
				+ "<name>Single book Group</name><type>standard</type><headtitle>uscl/an/book_singlebookgroup</headtitle><members><subgroup>"
				+ "<title>uscl/an/book_singlebookgroup</title></subgroup></members></group></groups>";
		List<String> groupVersions = new ArrayList<String>();
		groupService.getLastGroupVerionFromProviewResponse(groupInfoXML,groupVersions);
		Assert.assertEquals(2,groupVersions.size());
	}

	@Test
	public void testLastGroupVersionofMultitple()  throws Exception{
		groupInfoXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><groups><group id=\"uscl/book_singlebookgroup\" status=\"Review\" version=\"v1\">"
				+ "<name>Single book Group</name><type>standard</type><headtitle>uscl/an/book_singlebookgroup</headtitle><members><subgroup>"
				+ "<title>uscl/an/book_singlebookgroup</title></subgroup></members></group>"
				+ "<group id=\"uscl/book_singlebookgroup\" status=\"Review\" version=\"v2\">"
				+ "<name>Single book Group</name><type>standard</type><headtitle>uscl/an/book_singlebookgroup</headtitle><members><subgroup>"
				+ "<title>uscl/an/book_singlebookgroup</title></subgroup></members></group></groups>";
		List<String> groupVersions = new ArrayList<String>();
		Assert.assertEquals("2",groupService.getLastGroupVerionFromProviewResponse(groupInfoXML,groupVersions).toString());
	}
	
	@Test
	public void testLastGroupVersionofMultitpleWithNoOrder()  throws Exception{
		groupInfoXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><groups><group id=\"uscl/book_singlebookgroup\" status=\"Review\" version=\"v2\">"
				+ "<name>Single book Group</name><type>standard</type><headtitle>uscl/an/book_singlebookgroup</headtitle><members><subgroup>"
				+ "<title>uscl/an/book_singlebookgroup</title></subgroup></members></group>"
				+ "<group id=\"uscl/book_singlebookgroup\" status=\"Review\" version=\"v1\">"
				+ "<name>Single book Group</name><type>standard</type><headtitle>uscl/an/book_singlebookgroup</headtitle><members><subgroup>"
				+ "<title>uscl/an/book_singlebookgroup</title></subgroup></members></group></groups>";
		List<String> groupVersions = new ArrayList<String>();
		Assert.assertEquals("2",groupService.getLastGroupVerionFromProviewResponse(groupInfoXML,groupVersions).toString());
	}
	
	@Test
	public void testBuildGroupDefinition() throws Exception{
		GroupDefinition groupDef = groupService.buildGroupDefinition("groupName", "2015", "uscl/an/book_singlebookgroup", "v1",null);
		
		Assert.assertEquals("uscl/an/book_singlebookgroup/v1",groupDef.getHeadTitle());
		Assert.assertEquals("groupName",groupDef.getName());
		Assert.assertEquals(1,groupDef.getSubGroupInfoList().size());
		
		Assert.assertEquals("2015",groupDef.getSubGroupInfoList().get(0).getHeading());
		Assert.assertEquals(1,groupDef.getSubGroupInfoList().get(0).getTitles().size());
		Assert.assertEquals("uscl/an/book_singlebookgroup/v1",groupDef.getSubGroupInfoList().get(0).getTitles().get(0));
		
	}
	
	@Test
	public void testBuildGroupDefinitionNoSub() throws Exception{
		GroupDefinition groupDef = groupService.buildGroupDefinition("groupName", null, "uscl/an/book_singlebookgroup", "v1",null);
		
		Assert.assertEquals("uscl/an/book_singlebookgroup",groupDef.getHeadTitle());
		Assert.assertEquals("groupName",groupDef.getName());
		Assert.assertEquals(1,groupDef.getSubGroupInfoList().size());
		
		Assert.assertEquals(null,groupDef.getSubGroupInfoList().get(0).getHeading());
		Assert.assertEquals(1,groupDef.getSubGroupInfoList().get(0).getTitles().size());
		Assert.assertEquals("uscl/an/book_singlebookgroup",groupDef.getSubGroupInfoList().get(0).getTitles().get(0));
		
	}
	
	@Test
	public void testBuildGroupDefinitionException() throws Exception{
		boolean thrown = false;
		try{
			groupService.buildGroupDefinition("groupName","subGroup", "uscl/an/book_singlebookgroup", "v2",null);
		}
		catch(ProviewException ex){
			thrown = true;
			Assert.assertTrue(ex.getMessage().contains(CoreConstants.SUBGROUP_ERROR_MESSAGE));
		}
		
		Assert.assertTrue(thrown);
		
	}
	
	
	
	@Test
	public void testSubGroupsToNoSub() throws Exception{
		groupInfoXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<group id=\"uscl/grouptest\" status=\"Review\"><name>Group Test</name><type>standard</type><headtitle>uscl/an/book_lohisplitnodeinfo/v1</headtitle>"
				+ "<members><subgroup heading=\"2014\"><title>uscl/an/book_lohisplitnodeinfo/v1</title></subgroup>"
				+ "</members></group>";
		
		String newGroupName ="Group Test"; //No change in group
		String newSubgroupHeading = null;
		
		boolean versionChange = true;
		splitTitles = new ArrayList<String>();
		GroupDefinition groupDef = groupService.getGroupDefinitionforAllBooks(groupInfoXML, "v2", newGroupName, newSubgroupHeading, "uscl/an/book_lohisplitnodeinfo", splitTitles, versionChange, false);
		
		
		Assert.assertEquals("uscl/an/book_lohisplitnodeinfo",groupDef.getHeadTitle());
		Assert.assertEquals("Group Test",groupDef.getName());
		Assert.assertEquals(1,groupDef.getSubGroupInfoList().size());
		
		Assert.assertEquals(null,groupDef.getSubGroupInfoList().get(0).getHeading());
		Assert.assertEquals(1,groupDef.getSubGroupInfoList().get(0).getTitles().size());
		Assert.assertEquals("uscl/an/book_lohisplitnodeinfo",groupDef.getSubGroupInfoList().get(0).getTitles().get(0));
		
	}
	
	@Test
	public void testVersionChangeOnNoSub() throws Exception{
		groupInfoXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<group id=\"uscl/grouptest\" status=\"Review\"><name>Group Test</name><type>standard</type><headtitle>uscl/an/book_lohisplitnodeinfo</headtitle>"
				+ "<members><subgroup><title>uscl/an/book_lohisplitnodeinfo</title></subgroup>"
				+ "</members></group>";
		String newGroupName ="Group Test"; //No change in group
		String newSubgroupHeading = "";
		
		boolean versionChange = false;
		splitTitles = new ArrayList<String>();
		GroupDefinition groupDef = groupService.getGroupDefinitionforAllBooks(groupInfoXML, "v2", newGroupName, newSubgroupHeading, "uscl/an/book_lohisplitnodeinfo", splitTitles, versionChange, false);
		
		
		Assert.assertEquals(null,groupDef);
	}
	
	@Test
	public void testSingleBookNoSubChangeMajorVersion() throws Exception {
		groupInfoXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<group id=\"uscl/grouptest\" status=\"Review\"><name>Group Test</name><type>standard</type><headtitle>uscl/an/book_lohisplitnodeinfo/v2</headtitle>"
				+ "<members><subgroup heading=\"2015\"><title>uscl/an/book_lohisplitnodeinfo/v2</title></subgroup>"
				+ "<subgroup heading=\"2014\"><title>uscl/an/book_lohisplitnodeinfo/v1</title></subgroup></members></group>";
		
		//GroupDefinition groupDef = groupService.getGroupDefinitionForSingleBooks(groupInfoXML, "v3", "Group Test", "2015", "uscl/an/book_lohisplitnodeinfo");
		
		boolean versionChange = true;
		splitTitles = new ArrayList<String>();
		GroupDefinition groupDef = groupService.getGroupDefinitionforAllBooks(groupInfoXML, "v3", "Group Test", "2015", "uscl/an/book_lohisplitnodeinfo", splitTitles, versionChange, false);
		
		
		Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v3",groupDef.getHeadTitle());
		Assert.assertEquals("Group Test",groupDef.getName());
		Assert.assertEquals(2,groupDef.getSubGroupInfoList().size());
		
		Assert.assertEquals("2015",groupDef.getSubGroupInfoList().get(0).getHeading());
		Assert.assertEquals(2,groupDef.getSubGroupInfoList().get(0).getTitles().size());
		Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v3",groupDef.getSubGroupInfoList().get(0).getTitles().get(0));
		Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v2",groupDef.getSubGroupInfoList().get(0).getTitles().get(1));
		
		Assert.assertEquals("2014",groupDef.getSubGroupInfoList().get(1).getHeading());
		Assert.assertEquals(1,groupDef.getSubGroupInfoList().get(1).getTitles().size());
		Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v1",groupDef.getSubGroupInfoList().get(1).getTitles().get(0));
	}
	
	
	@Test
	public void testGroupWithSubgroupNameChangeSingleBook() throws Exception {
		groupInfoXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<group id=\"uscl/grouptest\" status=\"Review\"><name>Group Test</name><type>standard</type><headtitle>uscl/an/book_lohisplitnodeinfo/v2</headtitle>"
				+ "<members><subgroup heading=\"2015\"><title>uscl/an/book_lohisplitnodeinfo/v2</title></subgroup>"
				+ "<subgroup heading=\"2014\"><title>uscl/an/book_lohisplitnodeinfo/v1</title></subgroup></members></group>";
		
		boolean versionChange = false;
		splitTitles = new ArrayList<String>();
		GroupDefinition groupDef = groupService.getGroupDefinitionforAllBooks(groupInfoXML, "v2", "Group Test", "2016", "uscl/an/book_lohisplitnodeinfo", splitTitles, versionChange, false);
		
		Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v2",groupDef.getHeadTitle());
		Assert.assertEquals("Group Test",groupDef.getName());
		Assert.assertEquals(2,groupDef.getSubGroupInfoList().size());
		
		Assert.assertEquals("2016",groupDef.getSubGroupInfoList().get(0).getHeading());
		Assert.assertEquals("2014",groupDef.getSubGroupInfoList().get(1).getHeading());
		
	}
	
	@Test
	public void testNoToYesSubgroupSingleBook() throws Exception {
		groupInfoXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<group id=\"uscl/grouptest\" status=\"Review\"><name>Group Test</name><type>standard</type><headtitle>uscl/an/book_lohisplitnodeinfo</headtitle>"
				+ "<members><subgroup><title>uscl/an/book_lohisplitnodeinfo</title></subgroup></members></group>";
		
		//GroupDefinition groupDef =	groupService.getGroupDefinitionForSingleBooks(groupInfoXML, "v1", "Group Test", "2016", "uscl/an/book_lohisplitnodeinfo");
		boolean versionChange = false;
		splitTitles = new ArrayList<String>();
		GroupDefinition groupDef = groupService.getGroupDefinitionforAllBooks(groupInfoXML, "v1", "Group Test", "2016", "uscl/an/book_lohisplitnodeinfo", splitTitles, versionChange, false);
		
		Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v1",groupDef.getHeadTitle());
		Assert.assertEquals("Group Test",groupDef.getName());
		Assert.assertEquals(1,groupDef.getSubGroupInfoList().size());
		
		Assert.assertEquals("2016",groupDef.getSubGroupInfoList().get(0).getHeading());
		
	}
	
	@Test
	public void testYesToNoSubgroupSingleBook() throws Exception {
		groupInfoXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<group id=\"uscl/grouptest\" status=\"Review\"><name>Group Test</name><type>standard</type><headtitle>uscl/an/book_lohisplitnodeinfo/v1</headtitle>"
				+ "<members><subgroup heading=\"2015\"><title>uscl/an/book_lohisplitnodeinfo/v1</title></subgroup></members></group>";
		
		boolean versionChange = false;
		splitTitles = new ArrayList<String>();
		GroupDefinition groupDef = groupService.getGroupDefinitionforAllBooks(groupInfoXML, "v1", "Group Test", null, "uscl/an/book_lohisplitnodeinfo", splitTitles, versionChange, false);
		
		Assert.assertEquals("uscl/an/book_lohisplitnodeinfo",groupDef.getHeadTitle());
		Assert.assertEquals("Group Test",groupDef.getName());
		Assert.assertEquals(1,groupDef.getSubGroupInfoList().size());
		Assert.assertEquals(null,groupDef.getSubGroupInfoList().get(0).getHeading());
		Assert.assertEquals(1,groupDef.getSubGroupInfoList().get(0).getTitles().size());
		Assert.assertEquals("uscl/an/book_lohisplitnodeinfo",groupDef.getSubGroupInfoList().get(0).getTitles().get(0));
		
		
		groupInfoXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<group id=\"uscl/grouptest\" status=\"Review\"><name>Group Test</name><type>standard</type><headtitle>uscl/an/book_lohisplitnodeinfo/v2</headtitle>"
				+ "<members><subgroup heading=\"2015\"><title>uscl/an/book_lohisplitnodeinfo/v2</title></subgroup>"
				+ "<subgroup heading=\"2014\"><title>uscl/an/book_lohisplitnodeinfo/v1</title></subgroup></members></group>";
		
		//groupDef =	groupService.getGroupDefinitionForSingleBooks(groupInfoXML, "v4", "Group Test", null, "uscl/an/book_lohisplitnodeinfo");
		groupDef = groupService.getGroupDefinitionforAllBooks(groupInfoXML, "v4", "Group Test", null, "uscl/an/book_lohisplitnodeinfo", splitTitles, versionChange, false);
		Assert.assertEquals("uscl/an/book_lohisplitnodeinfo",groupDef.getHeadTitle());
		Assert.assertEquals("Group Test",groupDef.getName());
		Assert.assertEquals(1,groupDef.getSubGroupInfoList().size());
		Assert.assertEquals(null,groupDef.getSubGroupInfoList().get(0).getHeading());
		Assert.assertEquals(1,groupDef.getSubGroupInfoList().get(0).getTitles().size());
		Assert.assertEquals("uscl/an/book_lohisplitnodeinfo",groupDef.getSubGroupInfoList().get(0).getTitles().get(0));
	}
	

	
	@Test
	public void testSingleBookToSplitVersion1() throws Exception {
		groupInfoXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<group id=\"uscl/grouptest\" status=\"Review\"><name>Group Test</name><type>standard</type><headtitle>uscl/an/book_lohisplitnodeinfo</headtitle>"
				+ "<members><subgroup><title>uscl/an/book_lohisplitnodeinfo</title></subgroup></members></group>";
		
			splitTitles = new ArrayList<String>();
			splitTitles.add("uscl/an/book_lohisplitnodeinfo");
			splitTitles.add("uscl/an/book_lohisplitnodeinfo_pt2");
			
			
			boolean versionChange = false;
			GroupDefinition groupDef = groupService.getGroupDefinitionforAllBooks(groupInfoXML, "v1", "Group Test", "2015", "uscl/an/book_lohisplitnodeinfo", splitTitles, versionChange, true);
			
			
			Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v1",groupDef.getHeadTitle());
			Assert.assertEquals("Group Test",groupDef.getName());
			Assert.assertEquals(1,groupDef.getSubGroupInfoList().size());
			
			Assert.assertEquals("2015",groupDef.getSubGroupInfoList().get(0).getHeading());
			Assert.assertEquals(2,groupDef.getSubGroupInfoList().get(0).getTitles().size());
			Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v1",groupDef.getSubGroupInfoList().get(0).getTitles().get(0));
			Assert.assertEquals("uscl/an/book_lohisplitnodeinfo_pt2/v1",groupDef.getSubGroupInfoList().get(0).getTitles().get(1));
	}
	
	

	@Test 
	public void testSingleToSplit1() throws Exception{
		groupInfoXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<group id=\"uscl/grouptest\" status=\"Review\"><name>Group Test</name><type>standard</type><headtitle>uscl/an/book_lohisplitnodeinfo/v2</headtitle>"
				+ "<members><subgroup heading=\"2015\"><title>uscl/an/book_lohisplitnodeinfo/v2</title><title>uscl/an/book_lohisplitnodeinfo/v1</title></subgroup>"
				+ "</members></group>";
		
			splitTitles = new ArrayList<String>();
			splitTitles.add("uscl/an/book_lohisplitnodeinfo");
			splitTitles.add("uscl/an/book_lohisplitnodeinfo_pt2");
			
			
			boolean versionChange = false;
			GroupDefinition groupDef = groupService.getGroupDefinitionforAllBooks(groupInfoXML, "v2", "Group Test", "2015", "uscl/an/book_lohisplitnodeinfo", splitTitles, versionChange, true);
			
			
			Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v2",groupDef.getHeadTitle());
			Assert.assertEquals("Group Test",groupDef.getName());
			Assert.assertEquals(1,groupDef.getSubGroupInfoList().size());
			
			Assert.assertEquals("2015",groupDef.getSubGroupInfoList().get(0).getHeading());
			Assert.assertEquals(3,groupDef.getSubGroupInfoList().get(0).getTitles().size());
			Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v2",groupDef.getSubGroupInfoList().get(0).getTitles().get(0));
			Assert.assertEquals("uscl/an/book_lohisplitnodeinfo_pt2/v2",groupDef.getSubGroupInfoList().get(0).getTitles().get(1));
			Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v1",groupDef.getSubGroupInfoList().get(0).getTitles().get(2));
	}
	
	@Test 
	public void testSingleToSingleWithMultiples() throws Exception{
		groupInfoXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<group id=\"uscl/grouptest\" status=\"Review\"><name>Group Test</name><type>standard</type><headtitle>uscl/an/book_lohisplitnodeinfo/v2</headtitle>"
				+ "<members><subgroup heading=\"2015\"><title>uscl/an/book_lohisplitnodeinfo/v2</title><title>uscl/an/book_lohisplitnodeinfo/v1</title></subgroup>"
				+ "</members></group>";
		
			splitTitles = new ArrayList<String>();
			splitTitles.add("uscl/an/book_lohisplitnodeinfo");
			splitTitles.add("uscl/an/book_lohisplitnodeinfo_pt2");
			
			boolean versionChange = false;
			GroupDefinition groupDef = groupService.getGroupDefinitionforAllBooks(groupInfoXML, "v2", "Group Test", "2016", "uscl/an/book_lohisplitnodeinfo", null, versionChange, false);
			
			
			Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v2",groupDef.getHeadTitle());
			Assert.assertEquals("Group Test",groupDef.getName());
			Assert.assertEquals(1,groupDef.getSubGroupInfoList().size());
			
			Assert.assertEquals("2016",groupDef.getSubGroupInfoList().get(0).getHeading());
			Assert.assertEquals(2,groupDef.getSubGroupInfoList().get(0).getTitles().size());
			Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v2",groupDef.getSubGroupInfoList().get(0).getTitles().get(0));
			Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v1",groupDef.getSubGroupInfoList().get(0).getTitles().get(1));
	}

	
	@Test
	public void testSplitBookToSingleWithNullSubgroup() throws Exception {
		groupInfoXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<group id=\"uscl/grouptest\" status=\"Review\"><name>Group Test</name><type>standard</type><headtitle>uscl/an/book_lohisplitnodeinfo/v2</headtitle>"
				+ "<members><subgroup heading=\"2015\"><title>uscl/an/book_lohisplitnodeinfo/v2</title></subgroup>"
				+ "<subgroup heading=\"2014\"><title>uscl/an/book_lohisplitnodeinfo/v1</title></subgroup></members></group>";
		
			splitTitles = new ArrayList<String>();
			boolean versionChange = false;
			GroupDefinition groupDef = groupService.getGroupDefinitionforAllBooks(groupInfoXML, "v1", "Group Test", null, "uscl/an/book_lohisplitnodeinfo", splitTitles, versionChange, false);
		
			Assert.assertEquals("uscl/an/book_lohisplitnodeinfo",groupDef.getHeadTitle());
			Assert.assertEquals("Group Test",groupDef.getName());
			Assert.assertEquals(1,groupDef.getSubGroupInfoList().size());
			
			Assert.assertEquals(null,groupDef.getSubGroupInfoList().get(0).getHeading());
			Assert.assertEquals(1,groupDef.getSubGroupInfoList().get(0).getTitles().size());
			Assert.assertEquals("uscl/an/book_lohisplitnodeinfo",groupDef.getSubGroupInfoList().get(0).getTitles().get(0));
		
	}
	
	@Test
	public void testSplitBookToSingleWithSubgroupNoversionChange() throws Exception {
		groupInfoXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<group id=\"uscl/grouptest\" status=\"Review\"><name>Group Test</name><type>standard</type><headtitle>uscl/an/book_lohisplitnodeinfo/v1</headtitle>"
				+ "<members><subgroup heading=\"2015\"><title>uscl/an/book_lohisplitnodeinfo/v1</title><title>uscl/an/book_lohisplitnodeinfo_pt2/v1</title></subgroup>"
				+ "</members></group>";
		
			splitTitles = new ArrayList<String>();
			boolean versionChange = false;
			GroupDefinition groupDef = groupService.getGroupDefinitionforAllBooks(groupInfoXML, "v1", "Group Test", "2015", "uscl/an/book_lohisplitnodeinfo", splitTitles, versionChange, false);
			Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v1",groupDef.getHeadTitle());
			Assert.assertEquals("Group Test",groupDef.getName());
			Assert.assertEquals(1,groupDef.getSubGroupInfoList().size());
			
			Assert.assertEquals("2015",groupDef.getSubGroupInfoList().get(0).getHeading());
			Assert.assertEquals(1,groupDef.getSubGroupInfoList().get(0).getTitles().size());
			Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v1",groupDef.getSubGroupInfoList().get(0).getTitles().get(0));			
		
	}
	
	@Test
	public void testSplitBookToSingleWithSubgroupCahnge() throws Exception {
		groupInfoXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<group id=\"uscl/grouptest\" status=\"Review\"><name>Group Test</name><type>standard</type><headtitle>uscl/an/book_lohisplitnodeinfo/v1</headtitle>"
				+ "<members><subgroup heading=\"2015\"><title>uscl/an/book_lohisplitnodeinfo/v1</title><title>uscl/an/book_lohisplitnodeinfo_pt2/v1</title></subgroup>"
				+ "</members></group>";
		
			splitTitles = new ArrayList<String>();
			boolean versionChange = false;
			GroupDefinition groupDef = groupService.getGroupDefinitionforAllBooks(groupInfoXML, "v1", "Group Test", "2014", "uscl/an/book_lohisplitnodeinfo", splitTitles, versionChange, false);
	
			
			Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v1",groupDef.getHeadTitle());
			Assert.assertEquals("Group Test",groupDef.getName());
			Assert.assertEquals(1,groupDef.getSubGroupInfoList().size());
			
			Assert.assertEquals("2014",groupDef.getSubGroupInfoList().get(0).getHeading());
			Assert.assertEquals(1,groupDef.getSubGroupInfoList().get(0).getTitles().size());
			Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v1",groupDef.getSubGroupInfoList().get(0).getTitles().get(0));
		
	}
	
	@Test
	public void testvalidateResponse1() throws Exception {
		groupInfoXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<group id=\"uscl/grouptest\" status=\"Review\"><name>Group Test</name><type>standard</type><headtitle>uscl/an/book_lohisplitnodeinfo/v2</headtitle>"
				+ "<members><subgroup heading=\"2015\"><title>uscl/an/book_lohisplitnodeinfo/v2</title><title>uscl/an/book_lohisplitnodeinfo_pt2/v2</title></subgroup>"
				+ "<subgroup heading=\"2014\"><title>uscl/an/book_lohisplitnodeinfo/v1</title><title>uscl/an/book_lohisplitnodeinfo_pt2/v1</title></subgroup></members></group>";
		
		BookDefinition bookDefinition = new BookDefinition();
		bookDefinition.setEbookDefinitionId(new Long(1));
		boolean thrown = false;
		try{
		groupService.validateResponse(bookDefinition, groupInfoXML,"v2");
		}
		catch(ProviewException ex){
			thrown = true;		}
		
		Assert.assertTrue(!thrown);

	}
	
	@Test
	public void testvalidateResponse2() throws Exception {
		groupInfoXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<group id=\"uscl/grouptest\" status=\"Review\"><name>Group Test</name><type>standard</type><headtitle>uscl/an/book_lohisplitnodeinfo/v2</headtitle>"
				+ "<members><subgroup heading=\"2015\"><title>uscl/an/book_lohisplitnodeinfo/v2</title><title>uscl/an/book_lohisplitnodeinfo_pt2/v2</title></subgroup>"
				+ "<subgroup heading=\"2014\"><title>uscl/an/book_lohisplitnodeinfo/v1</title><title>uscl/an/book_lohisplitnodeinfo_pt2/v1</title></subgroup></members></group>";
		
		BookDefinition bookDefinition = new BookDefinition();
		bookDefinition.setEbookDefinitionId(new Long(1));
		bookDefinition.setSubGroupHeading("2014");
		bookDefinition.setIsSplitBook(true);
		boolean thrown = false;
		try{
		groupService.validateResponse(bookDefinition, groupInfoXML,"v3");
		}
		catch(ProviewException ex){
			thrown = true;
			Assert.assertTrue(ex.getMessage().contains(CoreConstants.DUPLICATE_SUBGROUP_ERROR_MESSAGE));
		}
		
		Assert.assertTrue(thrown);
	}
	
	@Test
	public void testvalidateResponse5() throws Exception {
		groupInfoXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<group id=\"uscl/grouptest\" status=\"Review\"><name>Group Test</name><type>standard</type><headtitle>uscl/an/book_lohisplitnodeinfo/v2</headtitle>"
				+ "<members><subgroup heading=\"2015\"><title>uscl/an/book_lohisplitnodeinfo/v2</title><title>uscl/an/book_lohisplitnodeinfo_pt2/v2</title></subgroup>"
				+ "<subgroup heading=\"2014\"><title>uscl/an/book_lohisplitnodeinfo/v1</title><title>uscl/an/book_lohisplitnodeinfo_pt2/v1</title></subgroup></members></group>";
		
		BookDefinition bookDefinition = new BookDefinition();
		bookDefinition.setEbookDefinitionId(new Long(1));
		bookDefinition.setSubGroupHeading("2015");
		bookDefinition.setIsSplitBook(true);
		boolean thrown = false;
		try{
		groupService.validateResponse(bookDefinition, groupInfoXML,"v3");
		}
		catch(ProviewException ex){
			thrown = true;
			Assert.assertTrue(ex.getMessage().contains(CoreConstants.SUBGROUP_SPLIT_ERROR_MESSAGE));
		}
		
		Assert.assertTrue(thrown);
	}
	
	@Test
	public void testvalidateResponse4() throws Exception {
		groupInfoXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<group id=\"uscl/grouptest\" status=\"Review\"><name>Group Test</name><type>standard</type><headtitle>uscl/an/book_lohisplitnodeinfo/v2</headtitle>"
				+ "<members><subgroup heading=\"2015\"><title>uscl/an/book_lohisplitnodeinfo/v2</title><title>uscl/an/book_lohisplitnodeinfo_pt2/v2</title></subgroup>"
				+ "<subgroup heading=\"2014\"><title>uscl/an/book_lohisplitnodeinfo/v1</title><title>uscl/an/book_lohisplitnodeinfo_pt2/v1</title></subgroup></members></group>";
		
		BookDefinition bookDefinition = new BookDefinition();
		bookDefinition.setEbookDefinitionId(new Long(1));
		bookDefinition.setSubGroupHeading("2014");
		bookDefinition.setIsSplitBook(false);
		boolean thrown = false;
		try{
		groupService.validateResponse(bookDefinition, groupInfoXML,"v3");
		}
		catch(ProviewException ex){
			thrown = true;
			Assert.assertTrue(ex.getMessage().contains(CoreConstants.DUPLICATE_SUBGROUP_ERROR_MESSAGE));
		}
		
		Assert.assertTrue(thrown);
	}
	
	
	@Test
	public void testvalidateResponse3() throws Exception {
		groupInfoXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<group id=\"uscl/grouptest\" status=\"Review\"><name>Group Test</name><type>standard</type><headtitle>uscl/an/book_lohisplitnodeinfo/v2</headtitle>"
				+ "<members><subgroup><title>uscl/an/book_lohisplitnodeinfo/v2</title><title>uscl/an/book_lohisplitnodeinfo_pt2/v2</title></subgroup>"
				+ "<subgroup><title>uscl/an/book_lohisplitnodeinfo/v2</title><title>uscl/an/book_lohisplitnodeinfo_pt2/v2</title></subgroup></members></group>";
		
		BookDefinition bookDefinition = new BookDefinition();
		bookDefinition.setEbookDefinitionId(new Long(1));
		bookDefinition.setSubGroupHeading("2014");
		boolean thrown = false;
		try{
		groupService.validateResponse(bookDefinition, groupInfoXML,"v2");
		}
		catch(ProviewException ex){
			thrown = true;
			Assert.assertTrue(ex.getMessage().contains(CoreConstants.SUBGROUP_ERROR_MESSAGE));
		}
		
		Assert.assertTrue(thrown);
	}
	
	@Test
	public void testGroupIdWithContentType(){
		BookDefinition bookDefinition = new BookDefinition();
		DocumentTypeCode dc = new DocumentTypeCode();
		dc.setAbbreviation("sc");
		bookDefinition.setDocumentTypeCodes(dc);
		PublisherCode publisherCode = new PublisherCode();
		publisherCode.setId(new Long(1));
		publisherCode.setName("ucl");
		bookDefinition.setPublisherCodes(publisherCode);
		bookDefinition.setFullyQualifiedTitleId("uscl/sc/book_abcd");
		
		String groupId = groupService.getGroupId(bookDefinition);
		Assert.assertEquals("ucl/sc_book_abcd",groupId);
		
		
	}
	
	@Test
	public void testGroupIdWithNoContentType(){
		BookDefinition bookDefinition = new BookDefinition();
		PublisherCode publisherCode = new PublisherCode();
		publisherCode.setId(new Long(1));
		publisherCode.setName("ucl");
		bookDefinition.setPublisherCodes(publisherCode);
		bookDefinition.setFullyQualifiedTitleId("uscl/book_abcd");
		
		String groupId = groupService.getGroupId(bookDefinition);
		Assert.assertEquals("ucl/book_abcd",groupId);
	}
}
