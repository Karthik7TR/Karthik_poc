package com.thomsonreuters.uscl.ereader.group.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.thomsonreuters.uscl.ereader.core.CoreConstants;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.PublisherCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.SplitNodeInfo;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewRuntimeException;
import com.thomsonreuters.uscl.ereader.deliver.service.GroupDefinition;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleContainer;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleInfo;
import com.thomsonreuters.uscl.ereader.deliver.service.GroupDefinition.SubGroupInfo;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewClient;

public class GroupServiceImplTest {
	private static final String GROUP_NAME = "groupName";
	private static final String GROUP_ID = "uscl/an_book_lohisplitnodeinfo";
	private static final String SUBGROUP_NAME = "2015";
	private static final String FULLY_QUALIFIED_TITLE_ID = "uscl/an/book_lohisplitnodeinfo";
	private static final String GROUP_INFO_SPLIT_ONE_SUBGROUP_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
			+ "<group id=\""+GROUP_ID+"\" status=\"Review\" version=\"v1\"><name>"+GROUP_NAME+"</name><type>standard</type>"
			+ "<headtitle>uscl/an/book_lohisplitnodeinfo/v1</headtitle><members><subgroup heading=\"2015\">"
			+ "<title>uscl/an/book_lohisplitnodeinfo/v1</title><title>uscl/an/book_lohisplitnodeinfo_pt2/v1</title>"
			+ "</subgroup></members></group>";
	private static final String GROUP_INFO_SPLIT_TWO_SUBGROUP_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
			+ "<group id=\""+GROUP_ID+"\" status=\"Review\" version=\"v1\"><name>"+GROUP_NAME+"</name><type>standard</type>"
			+ "<headtitle>uscl/an/book_lohisplitnodeinfo/v2</headtitle><members><subgroup heading=\"2015\">"
			+ "<title>uscl/an/book_lohisplitnodeinfo/v2</title><title>uscl/an/book_lohisplitnodeinfo_pt2/v2</title>"
			+ "</subgroup><subgroup heading=\"2014\"><title>uscl/an/book_lohisplitnodeinfo/v1</title>"
			+ "<title>uscl/an/book_lohisplitnodeinfo_pt2/v1</title></subgroup></members></group>";
	private static final String GROUP_INFO_NO_SUBGROUP_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
			+ "<group id=\""+GROUP_ID+"\" status=\"Review\" version=\"v1\"><name>"+GROUP_NAME+"</name><type>standard</type>"
			+ "<headtitle>uscl/an/book_lohisplitnodeinfo</headtitle><members><subgroup><title>uscl/an/book_lohisplitnodeinfo</title>"
			+ "</subgroup></members></group>";
	private static final String GROUP_INFO_SINGLE_TITLE_SUBGROUP_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
			+ "<group id=\""+GROUP_ID+"\" status=\"Review\" version=\"v1\"><name>"+GROUP_NAME+"</name><type>standard</type>"
			+ "<headtitle>uscl/an/book_lohisplitnodeinfo/v1</headtitle><members><subgroup heading=\"2014\">"
			+ "<title>uscl/an/book_lohisplitnodeinfo/v1</title></subgroup></members></group>";
	
	private static final String GROUP_INFO_MULTIPLE_SINGLE_TITLE_SUBGROUP_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
			+ "<group id=\""+GROUP_ID+"\" status=\"Review\" version=\"v1\"><name>"+GROUP_NAME+"</name><type>standard</type>"
			+ "<headtitle>uscl/an/book_lohisplitnodeinfo/v2</headtitle><members><subgroup heading=\"2015\">"
			+ "<title>uscl/an/book_lohisplitnodeinfo/v2</title>"
			+ "<title>uscl/an/book_lohisplitnodeinfo/v1</title></subgroup></members></group>";
	
	private static final String GROUP_INFO_SINGLE_TITLE_TWO_SUBGROUP_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
			+ "<group id=\""+GROUP_ID+"\" status=\"Review\" version=\"v1\"><name>"+GROUP_NAME+"</name><type>standard</type>"
			+ "<headtitle>uscl/an/book_lohisplitnodeinfo/v2</headtitle><members><subgroup heading=\"2015\">"
			+ "<title>uscl/an/book_lohisplitnodeinfo/v2</title></subgroup><subgroup heading=\"2014\">"
			+ "<title>uscl/an/book_lohisplitnodeinfo/v1</title></subgroup></members></group>";
	
	private static final String MULTIPLE_GROUP_INFO_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><groups>"
			+ "<group id=\""+GROUP_ID+"\" status=\"Review\" version=\"v1\">"
			+ "<name>"+GROUP_NAME+"</name><type>standard</type><headtitle>uscl/an/book_lohisplitnodeinfo</headtitle><members><subgroup>"
			+ "<title>uscl/an/book_lohisplitnodeinfo</title></subgroup></members></group>"
			+ "<group id=\""+GROUP_ID+"\" status=\"Review\" version=\"v2\">"
			+ "<name>"+GROUP_NAME+"</name><type>standard</type><headtitle>uscl/an/book_lohisplitnodeinfo</headtitle><members><subgroup>"
			+ "<title>uscl/an/book_lohisplitnodeinfo</title></subgroup></members></group></groups>";
	
	
	private GroupServiceImpl groupService;
	private ProviewClient mockProviewClient;
	private List<String> splitTitles = null;
	
	private BookDefinition BOOK_DEFINITION = null;
	
	@Before
	public void setUp() throws Exception
	{
		this.groupService = new GroupServiceImpl();
		this.mockProviewClient = EasyMock.createMock(ProviewClient.class);
		groupService.setProviewClient(mockProviewClient);
		
		splitTitles = new ArrayList<String>();
		splitTitles.add("uscl/an/book_lohisplitnodeinfo");
		splitTitles.add("uscl/an/book_lohisplitnodeinfo_pt2");
		
		BOOK_DEFINITION = new BookDefinition();
		BOOK_DEFINITION.setFullyQualifiedTitleId(FULLY_QUALIFIED_TITLE_ID);
		BOOK_DEFINITION.setGroupName(GROUP_NAME);
		BOOK_DEFINITION.setSubGroupHeading(SUBGROUP_NAME);
		
		DocumentTypeCode documentTypeCode = new DocumentTypeCode();
    	documentTypeCode.setId(Long.parseLong("1"));
    	documentTypeCode.setAbbreviation("an");
    	documentTypeCode.setName("Analytical");
    	BOOK_DEFINITION.setDocumentTypeCodes(documentTypeCode);
    	
    	PublisherCode publisherCode = new PublisherCode();
    	publisherCode.setId(1L);
    	publisherCode.setName("uscl");
    	BOOK_DEFINITION.setPublisherCodes(publisherCode);
    	
    	BOOK_DEFINITION.setIsSplitBook(true);
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
	public void testGetGroupInfoByVersion() {
		String groupId = "uscl/b_subgroup";
		String status = "Review";
		Long groupVersion = 10L;
		String groupName = "Group1";
		String type = "standard";
		String headTitle = "uscl/an/b_subgroup/v1";
		String result = "<group id=\""+groupId+"\" status=\""+status+"\" version=\"v"+groupVersion+"\">"
				+ "<name>"+groupName+"</name><type>"+type+"</type><headtitle>"+headTitle+"</headtitle>"
				+ "<members><subgroup heading=\"2013\"><title>uscl/an/b_subgroup/v1</title></subgroup></members></group>";

		try {
			EasyMock.expect(mockProviewClient.getProviewGroupInfo(groupId, "v10")).andReturn(result);
	    	EasyMock.replay(mockProviewClient);
			
			GroupDefinition group = groupService.getGroupInfoByVersion(groupId, new Long(10));
			Assert.assertEquals(groupVersion, group.getGroupVersion());
			Assert.assertEquals(groupId, group.getGroupId());
			Assert.assertEquals(headTitle, group.getHeadTitle());
			Assert.assertEquals(groupName, group.getName());
			Assert.assertEquals(status, group.getStatus());
			Assert.assertEquals(type, group.getType());
		} catch(Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
		
		EasyMock.verify(mockProviewClient);
	}
	
	@Test
	public void testGetGroupInfoByVersion2() {
		String groupId = "uscl/b_subgroup";
		String status = "Review";
		Long groupVersion = 10L;
		String groupName = "Group1";
		String type = "standard";
		String headTitle = "uscl/an/b_subgroup/v1";
		String result = "<group id=\""+groupId+"\" status=\""+status+"\" version=\"v"+groupVersion+"\">"
				+ "<name>"+groupName+"</name><type>"+type+"</type><headtitle>"+headTitle+"</headtitle>"
				+ "<members><subgroup heading=\"2013\"><title>uscl/an/b_subgroup/v1</title></subgroup></members></group>";
		
		try {
			EasyMock.expect(mockProviewClient.getProviewGroupInfo(groupId, "v11")).andThrow(new ProviewRuntimeException("400", "No such group id and version exist"));
			EasyMock.expect(mockProviewClient.getProviewGroupInfo(groupId, "v10")).andReturn(result);
	    	EasyMock.replay(mockProviewClient);
			
			GroupDefinition group = groupService.getGroupInfoByVersionAutoDecrement(groupId, new Long(11));
			Assert.assertEquals(groupVersion, group.getGroupVersion());
			Assert.assertEquals(groupId, group.getGroupId());
			Assert.assertEquals(headTitle, group.getHeadTitle());
			Assert.assertEquals(groupName, group.getName());
			Assert.assertEquals(status, group.getStatus());
			Assert.assertEquals(type, group.getType());
		} catch(Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
		
		EasyMock.verify(mockProviewClient);
	}
	
	@Test
	public void testGetGroupInfoByVersion3() {
		String errorCode = "404";
		String errorMessage = "error message";

		try {
			EasyMock.expect(mockProviewClient.getProviewGroupInfo("uscl/groupT", "v11")).andThrow(new ProviewRuntimeException(errorCode, errorMessage));
	    	EasyMock.replay(mockProviewClient);
			
			groupService.getGroupInfoByVersion("uscl/groupT", new Long(11));
			Assert.fail();
		} catch(ProviewException ex) {
			Assert.assertEquals(errorMessage, ex.getMessage());
		}
		
		EasyMock.verify(mockProviewClient);
	}
	
	@Test
	public void testBuildGroupDefinitionForSplits() {
    	String groupId = groupService.getGroupId(BOOK_DEFINITION);

		GroupDefinition groupDef;
		try {
			EasyMock.expect(mockProviewClient.getProviewGroupById(groupId)).andThrow(new ProviewRuntimeException("404", "No such groups exist"));
	    	EasyMock.replay(mockProviewClient);
	    	
			groupDef = groupService.createGroupDefinition(BOOK_DEFINITION, "v1.0", splitTitles);
			
			Assert.assertEquals(FULLY_QUALIFIED_TITLE_ID + "/v1", groupDef.getHeadTitle());
			Assert.assertEquals(GROUP_NAME, groupDef.getName());
			Assert.assertEquals(1, groupDef.getSubGroupInfoList().size());
			
			Assert.assertEquals(SUBGROUP_NAME, groupDef.getSubGroupInfoList().get(0).getHeading());
			Assert.assertEquals(2, groupDef.getSubGroupInfoList().get(0).getTitles().size());
			Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v1", groupDef.getSubGroupInfoList().get(0).getTitles().get(0));
			Assert.assertEquals("uscl/an/book_lohisplitnodeinfo_pt2/v1", groupDef.getSubGroupInfoList().get(0).getTitles().get(1));
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
		
		EasyMock.verify(mockProviewClient);
	}
	
	@Test
	public void testSplitEmptySubgroup() throws Exception{
		BOOK_DEFINITION.setSubGroupHeading(null);
		String groupId = groupService.getGroupId(BOOK_DEFINITION);

		try {
			EasyMock.expect(mockProviewClient.getProviewGroupById(groupId)).andThrow(new ProviewRuntimeException("404", "No such groups exist"));
	    	EasyMock.replay(mockProviewClient);
	    	
			groupService.createGroupDefinition(BOOK_DEFINITION, "v1.0", splitTitles);
			Assert.fail();
		}
		catch(ProviewException ex){
			Assert.assertTrue(ex.getMessage().contains("Subgroup name cannot be empty"));
		}
		EasyMock.verify(mockProviewClient);
	}
	
	/**
	 * 
	 * Adding subgroup for split title requires previous group to already have subgroups with title(s)
	 * Has no previous group.
	 */
	@Test
	public void testNoPreviousSubgroupsError() {
		String groupId = groupService.getGroupId(BOOK_DEFINITION);

		try {
			EasyMock.expect(mockProviewClient.getProviewGroupById(groupId)).andThrow(new ProviewRuntimeException("404", "No such groups exist"));
	    	EasyMock.replay(mockProviewClient);
	    	
			groupService.createGroupDefinition(BOOK_DEFINITION, "v2.0", splitTitles);
			Assert.fail();
		}
		catch(Exception ex){
			Assert.assertTrue(ex.getMessage().contains(CoreConstants.SUBGROUP_ERROR_MESSAGE));
		}
		EasyMock.verify(mockProviewClient);
	}
	
	/**
	 * Adding subgroup for split title requires previous group to already have subgroups with title(s)
	 * Previous group only has group with no subgroups
	 */
	@Test
	public void testNoPreviousSubgroupsError2() {
		String groupId = groupService.getGroupId(BOOK_DEFINITION);

		try {
			EasyMock.expect(mockProviewClient.getProviewGroupById(groupId)).andReturn(GROUP_INFO_NO_SUBGROUP_XML);
	    	EasyMock.replay(mockProviewClient);
	    	
			groupService.createGroupDefinition(BOOK_DEFINITION, "v2.0", splitTitles);
			Assert.fail();
		}
		catch(Exception ex){
			Assert.assertTrue(ex.getMessage().contains(CoreConstants.SUBGROUP_ERROR_MESSAGE));
		}
		EasyMock.verify(mockProviewClient);
	}
	
	/**
	 * Adding subgroup for single title requires previous group to already have subgroups with title(s)
	 * Previous group only has group with no subgroups
	 */
	@Test
	public void testNoPreviousSubgroupsError3() {
		BOOK_DEFINITION.setIsSplitBook(false);
		String groupId = groupService.getGroupId(BOOK_DEFINITION);

		try {
			EasyMock.expect(mockProviewClient.getProviewGroupById(groupId)).andReturn(GROUP_INFO_NO_SUBGROUP_XML);
	    	EasyMock.replay(mockProviewClient);
	    	
			groupService.createGroupDefinition(BOOK_DEFINITION, "v2.0", null);
			Assert.fail();
		}
		catch(Exception ex){
			Assert.assertTrue(ex.getMessage().contains(CoreConstants.SUBGROUP_ERROR_MESSAGE));
		}
		EasyMock.verify(mockProviewClient);
	}
	
	@Test
	public void testEmptyGroup() {
		BOOK_DEFINITION.setGroupName(null);
		BOOK_DEFINITION.setSubGroupHeading(null);
		String groupId = groupService.getGroupId(BOOK_DEFINITION);

		try {
			EasyMock.expect(mockProviewClient.getProviewGroupById(groupId)).andThrow(new ProviewRuntimeException("404", "No such groups exist"));
	    	EasyMock.replay(mockProviewClient);
	    	
			groupService.createGroupDefinition(BOOK_DEFINITION, "v1.0", splitTitles);
			Assert.fail();
		}
		catch(Exception ex){
			Assert.assertTrue(ex.getMessage().contains(CoreConstants.EMPTY_GROUP_ERROR_MESSAGE));
		}
		EasyMock.verify(mockProviewClient);
	}
	
	@Test
	public void testDuplicateSubgroupHeading() {
		BOOK_DEFINITION.setSubGroupHeading("2014");
		String groupId = groupService.getGroupId(BOOK_DEFINITION);

		try {
			EasyMock.expect(mockProviewClient.getProviewGroupById(groupId)).andReturn(GROUP_INFO_SINGLE_TITLE_TWO_SUBGROUP_XML);
	    	EasyMock.replay(mockProviewClient);

	    	groupService.createGroupDefinition(BOOK_DEFINITION, "v3.0", splitTitles);
	    	Assert.fail();
		} catch (Exception e) {
			Assert.assertTrue(e.getMessage().contains(CoreConstants.DUPLICATE_SUBGROUP_ERROR_MESSAGE));
		}
		
		EasyMock.verify(mockProviewClient);
	}
	
	@Test
	public void testDuplicateSubgroupHeadingSplitBook() {
		BOOK_DEFINITION.setSubGroupHeading("2014");
		String groupId = groupService.getGroupId(BOOK_DEFINITION);

		try {
			EasyMock.expect(mockProviewClient.getProviewGroupById(groupId)).andReturn(GROUP_INFO_SPLIT_TWO_SUBGROUP_XML);
	    	EasyMock.replay(mockProviewClient);

	    	groupService.createGroupDefinition(BOOK_DEFINITION, "v3.0", splitTitles);
	    	Assert.fail();
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertTrue(e.getMessage().contains(CoreConstants.DUPLICATE_SUBGROUP_ERROR_MESSAGE));
		}
		
		EasyMock.verify(mockProviewClient);
	}
	
	@Test
	public void testSimilarGroupDef() {
		String groupId = groupService.getGroupId(BOOK_DEFINITION);

		try {
			EasyMock.expect(mockProviewClient.getProviewGroupById(groupId)).andReturn(GROUP_INFO_SPLIT_TWO_SUBGROUP_XML).times(2);
	    	EasyMock.replay(mockProviewClient);
	    	
	    	GroupDefinition lastGroup = groupService.getLastGroup(BOOK_DEFINITION);
	    	GroupDefinition currentGroup = groupService.createGroupDefinition(BOOK_DEFINITION, "v2.0", splitTitles);
	    	Assert.assertTrue(currentGroup.isSimilarGroup(lastGroup));
	    	
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
		
		EasyMock.verify(mockProviewClient);
	}
	
	@Test
	public void testGetGroupDefVersionChange() {
		String subgroupName = "2016";
		BOOK_DEFINITION.setSubGroupHeading(subgroupName);
		
		String groupId = groupService.getGroupId(BOOK_DEFINITION);

		try {
			EasyMock.expect(mockProviewClient.getProviewGroupById(groupId)).andReturn(GROUP_INFO_SPLIT_TWO_SUBGROUP_XML);
	    	EasyMock.replay(mockProviewClient);

	    	GroupDefinition groupDef = groupService.createGroupDefinition(BOOK_DEFINITION, "v3.0", splitTitles);

			Assert.assertEquals(subgroupName,groupDef.getSubGroupInfoList().get(0).getHeading());
			Assert.assertEquals(2,groupDef.getSubGroupInfoList().get(0).getTitles().size());
			Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v3",groupDef.getSubGroupInfoList().get(0).getTitles().get(0));
			Assert.assertEquals("uscl/an/book_lohisplitnodeinfo_pt2/v3",groupDef.getSubGroupInfoList().get(0).getTitles().get(1));
			
			assertGroup(groupDef, GROUP_NAME,FULLY_QUALIFIED_TITLE_ID + "/v3",  "2015", "2014", 1);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
		
		EasyMock.verify(mockProviewClient);
	}
	
	@Test
	public void testGetGroupDefGroupNameChangeOnlyChange() {
		String groupName = "ChangeName";
		
		BOOK_DEFINITION.setGroupName(groupName);
		String groupId = groupService.getGroupId(BOOK_DEFINITION);

		try {
			EasyMock.expect(mockProviewClient.getProviewGroupById(groupId)).andReturn(GROUP_INFO_SPLIT_TWO_SUBGROUP_XML);
	    	EasyMock.replay(mockProviewClient);
	    	
	    	GroupDefinition groupDef = groupService.createGroupDefinition(BOOK_DEFINITION, "v2.0", splitTitles);
	    	assertGroup(groupDef, groupName, FULLY_QUALIFIED_TITLE_ID + "/v2", "2015", "2014", 0);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
		
		EasyMock.verify(mockProviewClient);
	}
	
	@Test
	public void testGetGroupDefGpSubGpNameChange() {
		String groupName = "GroupChangeName";
		String subGroupName = "SubGroupChangeName";
		BOOK_DEFINITION.setGroupName(groupName);
		BOOK_DEFINITION.setSubGroupHeading(subGroupName);
		String groupId = groupService.getGroupId(BOOK_DEFINITION);

		try {
			EasyMock.expect(mockProviewClient.getProviewGroupById(groupId)).andReturn(GROUP_INFO_SPLIT_TWO_SUBGROUP_XML);
	    	EasyMock.replay(mockProviewClient);
	    	
	    	GroupDefinition groupDef = groupService.createGroupDefinition(BOOK_DEFINITION, "v2.0", splitTitles);
	    	assertGroup(groupDef, groupName, FULLY_QUALIFIED_TITLE_ID + "/v2", subGroupName, "2014", 0);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
		
		EasyMock.verify(mockProviewClient);
	}
	
	@Test
	public void testGetGroupDefSubGpNameChangeOnly() {
		String subGroupName = "SubGroupChangeName";
		BOOK_DEFINITION.setSubGroupHeading(subGroupName);
		String groupId = groupService.getGroupId(BOOK_DEFINITION);

		try {
			EasyMock.expect(mockProviewClient.getProviewGroupById(groupId)).andReturn(GROUP_INFO_SPLIT_TWO_SUBGROUP_XML);
	    	EasyMock.replay(mockProviewClient);
	    	
	    	GroupDefinition groupDef = groupService.createGroupDefinition(BOOK_DEFINITION, "v2.0", splitTitles);
	    	assertGroup(groupDef, GROUP_NAME, FULLY_QUALIFIED_TITLE_ID + "/v2", subGroupName, "2014", 0);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
		
		EasyMock.verify(mockProviewClient);
	}
	
	/**
	 * Going from two split book to three split book
	 */
	@Test
	public void testGetGroupDefSplitChange() {
		splitTitles = new ArrayList<String>();
		splitTitles.add("uscl/an/book_lohisplitnodeinfo");
		splitTitles.add("uscl/an/book_lohisplitnodeinfo_pt2");
		splitTitles.add("uscl/an/book_lohisplitnodeinfo_pt3");
		String groupId = groupService.getGroupId(BOOK_DEFINITION);

		try {
			EasyMock.expect(mockProviewClient.getProviewGroupById(groupId)).andReturn(GROUP_INFO_SPLIT_TWO_SUBGROUP_XML);
	    	EasyMock.replay(mockProviewClient);
	    	
	    	GroupDefinition groupDef = groupService.createGroupDefinition(BOOK_DEFINITION, "v2.1", splitTitles);
	    	
	    	Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v2",groupDef.getHeadTitle());
			Assert.assertEquals(GROUP_NAME,groupDef.getName());
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
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
		
		EasyMock.verify(mockProviewClient);
	}
	
	@Test
	public void testSplitSubgroupNameChangeOnMajorVersionUpdate() {
		String groupId = groupService.getGroupId(BOOK_DEFINITION);

		try {
			EasyMock.expect(mockProviewClient.getProviewGroupById(groupId)).andReturn(GROUP_INFO_SPLIT_TWO_SUBGROUP_XML);
	    	EasyMock.replay(mockProviewClient);
	    	
	    	groupService.createGroupDefinition(BOOK_DEFINITION, "v3.0", splitTitles);
	    	Assert.fail();
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertTrue(e.getMessage().contains(CoreConstants.SUBGROUP_SPLIT_ERROR_MESSAGE));
		}
		
		EasyMock.verify(mockProviewClient);
	}
	
	@Test
	public void testGroupNameChangeWithNoSubGroups() {
		String groupName = "GroupNameChange";
		BOOK_DEFINITION.setGroupName(groupName);
		BOOK_DEFINITION.setSubGroupHeading(null);
		BOOK_DEFINITION.setIsSplitBook(false);
		String groupId = groupService.getGroupId(BOOK_DEFINITION);

		try {
			EasyMock.expect(mockProviewClient.getProviewGroupById(groupId)).andReturn(GROUP_INFO_NO_SUBGROUP_XML);
	    	EasyMock.replay(mockProviewClient);
	    	
	    	GroupDefinition groupDef = groupService.createGroupDefinition(BOOK_DEFINITION, "v2.0", null);
	    	
	    	Assert.assertEquals(FULLY_QUALIFIED_TITLE_ID,groupDef.getHeadTitle());
			Assert.assertEquals(groupName,groupDef.getName());
			Assert.assertEquals(1, groupDef.getSubGroupInfoList().size());
			Assert.assertEquals(null, groupDef.getSubGroupInfoList().get(0).getHeading());
			Assert.assertEquals(1, groupDef.getSubGroupInfoList().get(0).getTitles().size());
			Assert.assertEquals(FULLY_QUALIFIED_TITLE_ID, groupDef.getSubGroupInfoList().get(0).getTitles().get(0));
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
		
		EasyMock.verify(mockProviewClient);
	}
	
	/*
	 * Single title with one subgroup.  Major version change only
	 */
	@Test
	public void testVersionChangeOnly() {
		BOOK_DEFINITION.setIsSplitBook(false);
		BOOK_DEFINITION.setSubGroupHeading("2014");
		String groupId = groupService.getGroupId(BOOK_DEFINITION);

		try {
			EasyMock.expect(mockProviewClient.getProviewGroupById(groupId)).andReturn(GROUP_INFO_SINGLE_TITLE_SUBGROUP_XML);
	    	EasyMock.replay(mockProviewClient);
	    	
	    	GroupDefinition groupDef = groupService.createGroupDefinition(BOOK_DEFINITION, "v2.0", null);
	
			Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v2",groupDef.getHeadTitle());
			Assert.assertEquals(GROUP_NAME,groupDef.getName());
			Assert.assertEquals(1,groupDef.getSubGroupInfoList().size());
			
			SubGroupInfo subgroupInfo = groupDef.getSubGroupInfoList().get(0);
			Assert.assertEquals("2014",subgroupInfo.getHeading());
			Assert.assertEquals(2,subgroupInfo.getTitles().size());
			Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v2",subgroupInfo.getTitles().get(0));
			Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v1",subgroupInfo.getTitles().get(1));
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
		
		EasyMock.verify(mockProviewClient);
	}
	
	@Test
	public void testGroupSubGroupVersionChange() {
		String groupName = "change name";
		String subgroupName = "sub change name";
		BOOK_DEFINITION.setGroupName(groupName);
		BOOK_DEFINITION.setSubGroupHeading(subgroupName);
		String groupId = groupService.getGroupId(BOOK_DEFINITION);

		try {
			EasyMock.expect(mockProviewClient.getProviewGroupById(groupId)).andReturn(GROUP_INFO_SINGLE_TITLE_SUBGROUP_XML);
	    	EasyMock.replay(mockProviewClient);
	    	
	    	GroupDefinition groupDef = groupService.createGroupDefinition(BOOK_DEFINITION, "v2.0", null);
	
	    	Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v2",groupDef.getHeadTitle());
			Assert.assertEquals(groupName,groupDef.getName());
			Assert.assertEquals(2,groupDef.getSubGroupInfoList().size());
			
			Assert.assertEquals(subgroupName,groupDef.getSubGroupInfoList().get(0).getHeading());
			Assert.assertEquals(1,groupDef.getSubGroupInfoList().get(0).getTitles().size());
			Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v2",groupDef.getSubGroupInfoList().get(0).getTitles().get(0));
			
			Assert.assertEquals("2014",groupDef.getSubGroupInfoList().get(1).getHeading());
			Assert.assertEquals(1,groupDef.getSubGroupInfoList().get(1).getTitles().size());
			Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v1",groupDef.getSubGroupInfoList().get(1).getTitles().get(0));
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
		
		EasyMock.verify(mockProviewClient);
	}
	
	@Test
	public void testSubGroupVersionChange() {
		String subgroupName = "sub change name";
		BOOK_DEFINITION.setSubGroupHeading(subgroupName);
		String groupId = groupService.getGroupId(BOOK_DEFINITION);

		try {
			EasyMock.expect(mockProviewClient.getProviewGroupById(groupId)).andReturn(GROUP_INFO_SINGLE_TITLE_SUBGROUP_XML);
	    	EasyMock.replay(mockProviewClient);
	    	
	    	GroupDefinition groupDef = groupService.createGroupDefinition(BOOK_DEFINITION, "v2.0", null);
	
	    	Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v2",groupDef.getHeadTitle());
			Assert.assertEquals(GROUP_NAME,groupDef.getName());
			Assert.assertEquals(2,groupDef.getSubGroupInfoList().size());
			
			Assert.assertEquals(subgroupName,groupDef.getSubGroupInfoList().get(0).getHeading());
			Assert.assertEquals(1,groupDef.getSubGroupInfoList().get(0).getTitles().size());
			Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v2",groupDef.getSubGroupInfoList().get(0).getTitles().get(0));
			
			Assert.assertEquals("2014",groupDef.getSubGroupInfoList().get(1).getHeading());
			Assert.assertEquals(1,groupDef.getSubGroupInfoList().get(1).getTitles().size());
			Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v1",groupDef.getSubGroupInfoList().get(1).getTitles().get(0));
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
		
		EasyMock.verify(mockProviewClient);
	}
	
	@Test
	public void testLastGroup() {
		String groupId = groupService.getGroupId(BOOK_DEFINITION);
		
		try {
			EasyMock.expect(mockProviewClient.getProviewGroupById(groupId)).andReturn(MULTIPLE_GROUP_INFO_XML);
	    	EasyMock.replay(mockProviewClient);
	    	
	    	GroupDefinition groupDef = groupService.getLastGroup(BOOK_DEFINITION);
	
	    	Assert.assertEquals(FULLY_QUALIFIED_TITLE_ID,groupDef.getHeadTitle());
			Assert.assertEquals(GROUP_NAME,groupDef.getName());
			Assert.assertEquals(new Long(2) ,groupDef.getGroupVersion());
			Assert.assertEquals(1, groupDef.getSubGroupInfoList().size());
			
			SubGroupInfo subgroupInfo = groupDef.getSubGroupInfoList().get(0);
			Assert.assertNull(subgroupInfo.getHeading());
			Assert.assertEquals(1,subgroupInfo.getTitles().size());
			Assert.assertEquals(FULLY_QUALIFIED_TITLE_ID,subgroupInfo.getTitles().get(0));
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
		
		EasyMock.verify(mockProviewClient);
	}
	
	@Test
	public void testAllGroupsVersionDescendingForBook() {
		String groupId = groupService.getGroupId(BOOK_DEFINITION);
		
		try {
			EasyMock.expect(mockProviewClient.getProviewGroupById(groupId)).andReturn(MULTIPLE_GROUP_INFO_XML);
	    	EasyMock.replay(mockProviewClient);
	    	
	    	List<GroupDefinition> groups = groupService.getGroups(BOOK_DEFINITION);
	    	Assert.assertEquals(2 ,groups.size());
	    	
	    	// Check order
	    	GroupDefinition group1 = groups.get(0);
	    	Assert.assertEquals(new Long(2), group1.getGroupVersion());
	    	GroupDefinition group2 = groups.get(1);
	    	Assert.assertEquals(new Long(1), group2.getGroupVersion());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
		
		EasyMock.verify(mockProviewClient);
	}
	
	@Test
	public void testSubGroupsToNoSub() {
		BOOK_DEFINITION.setSubGroupHeading(null);
		BOOK_DEFINITION.setIsSplitBook(false);
		String groupId = groupService.getGroupId(BOOK_DEFINITION);

		try {
			EasyMock.expect(mockProviewClient.getProviewGroupById(groupId)).andReturn(GROUP_INFO_SINGLE_TITLE_SUBGROUP_XML);
	    	EasyMock.replay(mockProviewClient);
	    	
	    	GroupDefinition groupDef = groupService.createGroupDefinition(BOOK_DEFINITION, "v2.0", null);
	
			Assert.assertEquals(FULLY_QUALIFIED_TITLE_ID,groupDef.getHeadTitle());
			Assert.assertEquals(GROUP_NAME,groupDef.getName());
			Assert.assertEquals(1,groupDef.getSubGroupInfoList().size());
			
			Assert.assertEquals(null, groupDef.getSubGroupInfoList().get(0).getHeading());
			Assert.assertEquals(1, groupDef.getSubGroupInfoList().get(0).getTitles().size());
			Assert.assertEquals(FULLY_QUALIFIED_TITLE_ID, groupDef.getSubGroupInfoList().get(0).getTitles().get(0));
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
		
		EasyMock.verify(mockProviewClient);
	}
	
	/*
	 * Single title with two subgroups.  Major version change only
	 */
	@Test
	public void testSingleBookNoSubChangeMajorVersion() {
		BOOK_DEFINITION.setIsSplitBook(false);
		String groupId = groupService.getGroupId(BOOK_DEFINITION);

		try {
			EasyMock.expect(mockProviewClient.getProviewGroupById(groupId)).andReturn(GROUP_INFO_SINGLE_TITLE_TWO_SUBGROUP_XML);
	    	EasyMock.replay(mockProviewClient);
	    	
	    	GroupDefinition groupDef = groupService.createGroupDefinition(BOOK_DEFINITION, "v3.0", null);
	
			Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v3",groupDef.getHeadTitle());
			Assert.assertEquals(GROUP_NAME,groupDef.getName());
			Assert.assertEquals(2,groupDef.getSubGroupInfoList().size());
			
			SubGroupInfo subgroupInfo1 = groupDef.getSubGroupInfoList().get(0);
			Assert.assertEquals(SUBGROUP_NAME,subgroupInfo1.getHeading());
			Assert.assertEquals(2,subgroupInfo1.getTitles().size());
			Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v3",subgroupInfo1.getTitles().get(0));
			Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v2",subgroupInfo1.getTitles().get(1));
			
			SubGroupInfo subgroupInfo2 = groupDef.getSubGroupInfoList().get(1);
			Assert.assertEquals("2014",subgroupInfo2.getHeading());
			Assert.assertEquals(1,subgroupInfo2.getTitles().size());
			Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v1",subgroupInfo2.getTitles().get(0));
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
		
		EasyMock.verify(mockProviewClient);
	}
	
	@Test
	public void testSingleBookSubgroupNameChange() {
		String subgroupName = "random name";
		BOOK_DEFINITION.setIsSplitBook(false);
		BOOK_DEFINITION.setSubGroupHeading(subgroupName);
		String groupId = groupService.getGroupId(BOOK_DEFINITION);

		try {
			EasyMock.expect(mockProviewClient.getProviewGroupById(groupId)).andReturn(GROUP_INFO_SINGLE_TITLE_TWO_SUBGROUP_XML);
	    	EasyMock.replay(mockProviewClient);
	    	
	    	GroupDefinition groupDef = groupService.createGroupDefinition(BOOK_DEFINITION, "v2.0", null);
	
			Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v2",groupDef.getHeadTitle());
			Assert.assertEquals(GROUP_NAME,groupDef.getName());
			Assert.assertEquals(2,groupDef.getSubGroupInfoList().size());
			
			SubGroupInfo subgroupInfo1 = groupDef.getSubGroupInfoList().get(0);
			Assert.assertEquals(subgroupName,subgroupInfo1.getHeading());
			Assert.assertEquals(1,subgroupInfo1.getTitles().size());
			Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v2",subgroupInfo1.getTitles().get(0));
			
			SubGroupInfo subgroupInfo2 = groupDef.getSubGroupInfoList().get(1);
			Assert.assertEquals("2014",subgroupInfo2.getHeading());
			Assert.assertEquals(1,subgroupInfo2.getTitles().size());
			Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v1",subgroupInfo2.getTitles().get(0));
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
		
		EasyMock.verify(mockProviewClient);
	}
	
	@Test
	public void testNoToYesSubgroupSingleBook() {
		BOOK_DEFINITION.setIsSplitBook(false);
		String groupId = groupService.getGroupId(BOOK_DEFINITION);

		try {
			EasyMock.expect(mockProviewClient.getProviewGroupById(groupId)).andReturn(GROUP_INFO_NO_SUBGROUP_XML);
	    	EasyMock.replay(mockProviewClient);
	    	
	    	GroupDefinition groupDef = groupService.createGroupDefinition(BOOK_DEFINITION, "v1.0", null);
	    	
	    	Assert.assertEquals(FULLY_QUALIFIED_TITLE_ID + "/v1",groupDef.getHeadTitle());
			Assert.assertEquals(GROUP_NAME,groupDef.getName());
			Assert.assertEquals(1, groupDef.getSubGroupInfoList().size());
			Assert.assertEquals(SUBGROUP_NAME, groupDef.getSubGroupInfoList().get(0).getHeading());
			Assert.assertEquals(1, groupDef.getSubGroupInfoList().get(0).getTitles().size());
			Assert.assertEquals(FULLY_QUALIFIED_TITLE_ID + "/v1", groupDef.getSubGroupInfoList().get(0).getTitles().get(0));
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
		
		EasyMock.verify(mockProviewClient);
	}
	
	/**
	 * User removing subgroups from book definition
	 * Previous group has 1 subgroup
	 */
	@Test
	public void testYesToNoSubgroupSingleBook() {
		BOOK_DEFINITION.setIsSplitBook(false);
		BOOK_DEFINITION.setSubGroupHeading(null);
		String groupId = groupService.getGroupId(BOOK_DEFINITION);

		try {
			EasyMock.expect(mockProviewClient.getProviewGroupById(groupId)).andReturn(GROUP_INFO_SINGLE_TITLE_SUBGROUP_XML);
	    	EasyMock.replay(mockProviewClient);
	    	
	    	GroupDefinition groupDef = groupService.createGroupDefinition(BOOK_DEFINITION, "v1.0", null);
	    	
	    	Assert.assertEquals(FULLY_QUALIFIED_TITLE_ID,groupDef.getHeadTitle());
			Assert.assertEquals(GROUP_NAME,groupDef.getName());
			Assert.assertEquals(1, groupDef.getSubGroupInfoList().size());
			Assert.assertNull(groupDef.getSubGroupInfoList().get(0).getHeading());
			Assert.assertEquals(1, groupDef.getSubGroupInfoList().get(0).getTitles().size());
			Assert.assertEquals(FULLY_QUALIFIED_TITLE_ID, groupDef.getSubGroupInfoList().get(0).getTitles().get(0));
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
		
		EasyMock.verify(mockProviewClient);
	}
	
	/**
	 * User removing subgroups from book definition
	 * Previous group has two subgroups
	 */
	@Test
	public void testYesToNoSubgroupSingleBook2() {
		BOOK_DEFINITION.setIsSplitBook(false);
		BOOK_DEFINITION.setSubGroupHeading(null);
		String groupId = groupService.getGroupId(BOOK_DEFINITION);

		try {
			EasyMock.expect(mockProviewClient.getProviewGroupById(groupId)).andReturn(GROUP_INFO_SINGLE_TITLE_TWO_SUBGROUP_XML);
	    	EasyMock.replay(mockProviewClient);
	    	
	    	GroupDefinition groupDef = groupService.createGroupDefinition(BOOK_DEFINITION, "v1.0", null);
	    	
	    	Assert.assertEquals(FULLY_QUALIFIED_TITLE_ID,groupDef.getHeadTitle());
			Assert.assertEquals(GROUP_NAME,groupDef.getName());
			Assert.assertEquals(1, groupDef.getSubGroupInfoList().size());
			Assert.assertNull(groupDef.getSubGroupInfoList().get(0).getHeading());
			Assert.assertEquals(1, groupDef.getSubGroupInfoList().get(0).getTitles().size());
			Assert.assertEquals(FULLY_QUALIFIED_TITLE_ID, groupDef.getSubGroupInfoList().get(0).getTitles().get(0));
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
		
		EasyMock.verify(mockProviewClient);
	}
	
	/**
	 * Converting single title group to split title group
	 */
	@Test
	public void testSingleBookToSplitVersion() {
		String groupId = groupService.getGroupId(BOOK_DEFINITION);

		try {
			EasyMock.expect(mockProviewClient.getProviewGroupById(groupId)).andReturn(GROUP_INFO_NO_SUBGROUP_XML);
	    	EasyMock.replay(mockProviewClient);
	    	
	    	GroupDefinition groupDef = groupService.createGroupDefinition(BOOK_DEFINITION, "v1.0", splitTitles);
	    	
	    	Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v1",groupDef.getHeadTitle());
			Assert.assertEquals(GROUP_NAME,groupDef.getName());
			Assert.assertEquals(1,groupDef.getSubGroupInfoList().size());
			
			Assert.assertEquals(SUBGROUP_NAME,groupDef.getSubGroupInfoList().get(0).getHeading());
			Assert.assertEquals(2,groupDef.getSubGroupInfoList().get(0).getTitles().size());
			Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v1",groupDef.getSubGroupInfoList().get(0).getTitles().get(0));
			Assert.assertEquals("uscl/an/book_lohisplitnodeinfo_pt2/v1",groupDef.getSubGroupInfoList().get(0).getTitles().get(1));
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
		
		EasyMock.verify(mockProviewClient);
	}
	
	@Test
	public void testSingleBookWithSubgroupToSplitVersion() {
		String groupId = groupService.getGroupId(BOOK_DEFINITION);

		try {
			EasyMock.expect(mockProviewClient.getProviewGroupById(groupId)).andReturn(GROUP_INFO_MULTIPLE_SINGLE_TITLE_SUBGROUP_XML);
	    	EasyMock.replay(mockProviewClient);
	    	
	    	GroupDefinition groupDef = groupService.createGroupDefinition(BOOK_DEFINITION, "v2.0", splitTitles);
	    	
	    	Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v2",groupDef.getHeadTitle());
			Assert.assertEquals(GROUP_NAME,groupDef.getName());
			Assert.assertEquals(1,groupDef.getSubGroupInfoList().size());
			
			Assert.assertEquals(SUBGROUP_NAME,groupDef.getSubGroupInfoList().get(0).getHeading());
			Assert.assertEquals(3,groupDef.getSubGroupInfoList().get(0).getTitles().size());
			Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v2",groupDef.getSubGroupInfoList().get(0).getTitles().get(0));
			Assert.assertEquals("uscl/an/book_lohisplitnodeinfo_pt2/v2",groupDef.getSubGroupInfoList().get(0).getTitles().get(1));
			Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v1",groupDef.getSubGroupInfoList().get(0).getTitles().get(2));
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
		
		EasyMock.verify(mockProviewClient);
	}
	
	/**
	 * Changing group from single title to split title with minor update and subgroup name change
	 */
	@Test
	public void testSingleBookWithSubgroupToSplitVersionMinorUpdate() {
		String subgroupName = "changing it";
		BOOK_DEFINITION.setSubGroupHeading(subgroupName);
		String groupId = groupService.getGroupId(BOOK_DEFINITION);

		try {
			EasyMock.expect(mockProviewClient.getProviewGroupById(groupId)).andReturn(GROUP_INFO_MULTIPLE_SINGLE_TITLE_SUBGROUP_XML);
	    	EasyMock.replay(mockProviewClient);
	    	
	    	GroupDefinition groupDef = groupService.createGroupDefinition(BOOK_DEFINITION, "v2.2", splitTitles);
	    	
	    	Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v2",groupDef.getHeadTitle());
			Assert.assertEquals(GROUP_NAME,groupDef.getName());
			Assert.assertEquals(2,groupDef.getSubGroupInfoList().size());
			
			SubGroupInfo subgroupInfo1 = groupDef.getSubGroupInfoList().get(0);
			Assert.assertEquals(subgroupName, subgroupInfo1.getHeading());
			Assert.assertEquals(2, subgroupInfo1.getTitles().size());
			Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v2", subgroupInfo1.getTitles().get(0));
			Assert.assertEquals("uscl/an/book_lohisplitnodeinfo_pt2/v2", subgroupInfo1.getTitles().get(1));
			
			SubGroupInfo subgroupInfo2 = groupDef.getSubGroupInfoList().get(1);
			Assert.assertEquals(SUBGROUP_NAME, subgroupInfo2.getHeading());
			Assert.assertEquals(1, subgroupInfo2.getTitles().size());
			Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v1", subgroupInfo2.getTitles().get(0));
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
		
		EasyMock.verify(mockProviewClient);
	}
	
	@Test
	public void testSingleBookWithSubgroupToSplitVersionMajorUpdate() {
		String subgroupName = "2016";
		BOOK_DEFINITION.setSubGroupHeading(subgroupName);
		String groupId = groupService.getGroupId(BOOK_DEFINITION);

		try {
			EasyMock.expect(mockProviewClient.getProviewGroupById(groupId)).andReturn(GROUP_INFO_MULTIPLE_SINGLE_TITLE_SUBGROUP_XML);
	    	EasyMock.replay(mockProviewClient);
	    	
	    	GroupDefinition groupDef = groupService.createGroupDefinition(BOOK_DEFINITION, "v3.0", splitTitles);
	    	
	    	Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v3",groupDef.getHeadTitle());
			Assert.assertEquals(GROUP_NAME,groupDef.getName());
			Assert.assertEquals(2,groupDef.getSubGroupInfoList().size());
			
			SubGroupInfo subgroupInfo1 = groupDef.getSubGroupInfoList().get(0);
			Assert.assertEquals(subgroupName, subgroupInfo1.getHeading());
			Assert.assertEquals(2, subgroupInfo1.getTitles().size());
			Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v3", subgroupInfo1.getTitles().get(0));
			Assert.assertEquals("uscl/an/book_lohisplitnodeinfo_pt2/v3", subgroupInfo1.getTitles().get(1));
			
			SubGroupInfo subgroupInfo2 = groupDef.getSubGroupInfoList().get(1);
			Assert.assertEquals(SUBGROUP_NAME, subgroupInfo2.getHeading());
			Assert.assertEquals(2, subgroupInfo2.getTitles().size());
			Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v2", subgroupInfo2.getTitles().get(0));
			Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v1", subgroupInfo2.getTitles().get(1));
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
		
		EasyMock.verify(mockProviewClient);
	}
	
	@Test
	public void testSplitBookToSingleWithNoSubgroup() {
		BOOK_DEFINITION.setSubGroupHeading(null);
		BOOK_DEFINITION.setIsSplitBook(false);
		String groupId = groupService.getGroupId(BOOK_DEFINITION);

		try {
			EasyMock.expect(mockProviewClient.getProviewGroupById(groupId)).andReturn(GROUP_INFO_SPLIT_ONE_SUBGROUP_XML);
	    	EasyMock.replay(mockProviewClient);
	    	
	    	GroupDefinition groupDef = groupService.createGroupDefinition(BOOK_DEFINITION, "v1.0", null);
	    	
	    	Assert.assertEquals(FULLY_QUALIFIED_TITLE_ID,groupDef.getHeadTitle());
			Assert.assertEquals(GROUP_NAME,groupDef.getName());
			Assert.assertEquals(1,groupDef.getSubGroupInfoList().size());
			
			SubGroupInfo subgroupInfo1 = groupDef.getSubGroupInfoList().get(0);
			Assert.assertNull(subgroupInfo1.getHeading());
			Assert.assertEquals(1, subgroupInfo1.getTitles().size());
			Assert.assertEquals(FULLY_QUALIFIED_TITLE_ID, subgroupInfo1.getTitles().get(0));
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
		
		EasyMock.verify(mockProviewClient);
	}
	
	@Test
	public void testSplitBookToSingleWithSubgroup() {
		String subgroupName = "sub group name change";
		BOOK_DEFINITION.setSubGroupHeading(subgroupName);
		BOOK_DEFINITION.setIsSplitBook(false);
		String groupId = groupService.getGroupId(BOOK_DEFINITION);

		try {
			EasyMock.expect(mockProviewClient.getProviewGroupById(groupId)).andReturn(GROUP_INFO_SPLIT_ONE_SUBGROUP_XML);
	    	EasyMock.replay(mockProviewClient);
	    	
	    	GroupDefinition groupDef = groupService.createGroupDefinition(BOOK_DEFINITION, "v1.0", null);
	    	
	    	Assert.assertEquals(FULLY_QUALIFIED_TITLE_ID + "/v1",groupDef.getHeadTitle());
			Assert.assertEquals(GROUP_NAME,groupDef.getName());
			Assert.assertEquals(1,groupDef.getSubGroupInfoList().size());
			
			SubGroupInfo subgroupInfo1 = groupDef.getSubGroupInfoList().get(0);
			Assert.assertEquals(subgroupName, subgroupInfo1.getHeading());
			Assert.assertEquals(1, subgroupInfo1.getTitles().size());
			Assert.assertEquals(FULLY_QUALIFIED_TITLE_ID + "/v1", subgroupInfo1.getTitles().get(0));
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
		
		EasyMock.verify(mockProviewClient);
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
	
	@Test
	public void testGetProViewTitlesForGroupNoTitles() {
		try {
			EasyMock.expect(mockProviewClient.getProviewTitleContainer(BOOK_DEFINITION.getFullyQualifiedTitleId())).andReturn(null);
	    	EasyMock.replay(mockProviewClient);
			
			Map<String, ProviewTitleInfo> proviewTitleMap = groupService.getProViewTitlesForGroup(BOOK_DEFINITION);
			Assert.assertEquals(0, proviewTitleMap.size());
		} catch(Exception e) {
			Assert.fail();
		}
		EasyMock.verify(mockProviewClient);
	}
	
	@Test
	public void testGetProViewTitlesForGroupOneTitles() {
		List<ProviewTitleInfo> proviewTitleInfos = new ArrayList<>();
		ProviewTitleInfo proviewTitleInfo = new ProviewTitleInfo();
		proviewTitleInfo.setLastupdate("date");
		proviewTitleInfo.setPublisher("uscl");
		proviewTitleInfo.setStatus("Review");
		proviewTitleInfo.setTitle("Book name");
		proviewTitleInfo.setTitleId("uscl/an/test");
		proviewTitleInfo.setTotalNumberOfVersions(2);
		proviewTitleInfo.setVersion("v1.0");
		proviewTitleInfos.add(proviewTitleInfo);
		
		ProviewTitleContainer container = new ProviewTitleContainer();
		container.setProviewTitleInfos(proviewTitleInfos);
		try {
			EasyMock.expect(mockProviewClient.getProviewTitleContainer(BOOK_DEFINITION.getFullyQualifiedTitleId())).andReturn(container);
	    	EasyMock.replay(mockProviewClient);
			
			Map<String, ProviewTitleInfo> proviewTitleMap = groupService.getProViewTitlesForGroup(BOOK_DEFINITION);
			Assert.assertEquals(1, proviewTitleMap.size());
		} catch(Exception e) {
			Assert.fail();
		}
		EasyMock.verify(mockProviewClient);
	}
	
	@Test
	public void testGetProViewTitlesForGroupSplitTitles() {
		List<ProviewTitleInfo> proviewTitleInfos = new ArrayList<>();
		ProviewTitleInfo proviewTitleInfo = new ProviewTitleInfo();
		proviewTitleInfo.setLastupdate("date");
		proviewTitleInfo.setPublisher("uscl");
		proviewTitleInfo.setStatus("Review");
		proviewTitleInfo.setTitle("Book name");
		proviewTitleInfo.setTitleId("uscl/an/test");
		proviewTitleInfo.setTotalNumberOfVersions(2);
		proviewTitleInfo.setVersion("v1.0");
		proviewTitleInfos.add(proviewTitleInfo);
		
		ProviewTitleInfo proviewTitleInfo2 = new ProviewTitleInfo();
		proviewTitleInfo2.setLastupdate("date");
		proviewTitleInfo2.setPublisher("uscl");
		proviewTitleInfo2.setStatus("Review");
		proviewTitleInfo2.setTitle("Book name");
		proviewTitleInfo2.setTitleId("uscl/an/test");
		proviewTitleInfo2.setTotalNumberOfVersions(2);
		proviewTitleInfo2.setVersion("v2.0");
		proviewTitleInfos.add(proviewTitleInfo2);
		
		ProviewTitleContainer container = new ProviewTitleContainer();
		container.setProviewTitleInfos(proviewTitleInfos);
		
		Set<SplitNodeInfo> splitNodes = new HashSet<SplitNodeInfo>();
		SplitNodeInfo splitNodeInfo = new SplitNodeInfo();
		splitNodeInfo.setBookDefinition(BOOK_DEFINITION);
		splitNodeInfo.setBookVersionSubmitted("v2.0");
		splitNodeInfo.setSpitBookTitle("uscl/an/test_pt2");
		splitNodes.add(splitNodeInfo);
		BOOK_DEFINITION.setSplitNodes(splitNodes);
		
		proviewTitleInfos = new ArrayList<>();
		proviewTitleInfo = new ProviewTitleInfo();
		proviewTitleInfo.setLastupdate("date");
		proviewTitleInfo.setPublisher("uscl");
		proviewTitleInfo.setStatus("Review");
		proviewTitleInfo.setTitle("Book name");
		proviewTitleInfo.setTitleId("uscl/an/test_pt2");
		proviewTitleInfo.setTotalNumberOfVersions(2);
		proviewTitleInfo.setVersion("v2.0");
		proviewTitleInfos.add(proviewTitleInfo);
		
		ProviewTitleContainer container2 = new ProviewTitleContainer();
		container2.setProviewTitleInfos(proviewTitleInfos);
		
		
		try {
			EasyMock.expect(mockProviewClient.getProviewTitleContainer("uscl/an/test_pt2")).andReturn(container2);
			EasyMock.expect(mockProviewClient.getProviewTitleContainer(BOOK_DEFINITION.getFullyQualifiedTitleId())).andReturn(container);
	    	EasyMock.replay(mockProviewClient);
			
			Map<String, ProviewTitleInfo> proviewTitleMap = groupService.getProViewTitlesForGroup(BOOK_DEFINITION);
			Assert.assertEquals(3, proviewTitleMap.size());
			Assert.assertTrue(proviewTitleMap.containsKey("uscl/an/test/v1"));
			Assert.assertTrue(proviewTitleMap.containsKey("uscl/an/test/v2"));
			Assert.assertTrue(proviewTitleMap.containsKey("uscl/an/test_pt2/v2"));
		} catch(Exception e) {
			Assert.fail();
		}
		EasyMock.verify(mockProviewClient);
	}
	
	@Test
	public void testGetProViewTitlesForGroupPilotBook() {
		BOOK_DEFINITION.setFullyQualifiedTitleId("uscl/an/test_waspilot");
		List<ProviewTitleInfo> proviewTitleInfos = new ArrayList<>();
		ProviewTitleInfo proviewTitleInfo = new ProviewTitleInfo();
		proviewTitleInfo.setLastupdate("date");
		proviewTitleInfo.setPublisher("uscl");
		proviewTitleInfo.setStatus("Review");
		proviewTitleInfo.setTitle("Book name");
		proviewTitleInfo.setTitleId("uscl/an/test_waspilot");
		proviewTitleInfo.setTotalNumberOfVersions(2);
		proviewTitleInfo.setVersion("v1.0");
		proviewTitleInfos.add(proviewTitleInfo);

		ProviewTitleContainer container = new ProviewTitleContainer();
		container.setProviewTitleInfos(proviewTitleInfos);

		proviewTitleInfos = new ArrayList<>();
		proviewTitleInfo = new ProviewTitleInfo();
		proviewTitleInfo.setLastupdate("date");
		proviewTitleInfo.setPublisher("uscl");
		proviewTitleInfo.setStatus("Review");
		proviewTitleInfo.setTitle("Book name");
		proviewTitleInfo.setTitleId("uscl/an/test");
		proviewTitleInfo.setTotalNumberOfVersions(2);
		proviewTitleInfo.setVersion("v10");
		proviewTitleInfos.add(proviewTitleInfo);
		
		ProviewTitleContainer container2 = new ProviewTitleContainer();
		container2.setProviewTitleInfos(proviewTitleInfos);
		
		
		try {
			EasyMock.expect(mockProviewClient.getProviewTitleContainer("uscl/an/test")).andReturn(container2);
			EasyMock.expect(mockProviewClient.getProviewTitleContainer(BOOK_DEFINITION.getFullyQualifiedTitleId())).andReturn(container);
	    	EasyMock.replay(mockProviewClient);
			
			Map<String, ProviewTitleInfo> proviewTitleMap = groupService.getProViewTitlesForGroup(BOOK_DEFINITION);
			Assert.assertEquals(2, proviewTitleMap.size());
			Assert.assertTrue(proviewTitleMap.containsKey("uscl/an/test/v10"));
			Assert.assertTrue(proviewTitleMap.containsKey("uscl/an/test_waspilot/v1"));
		} catch(Exception e) {
			Assert.fail();
		}
		EasyMock.verify(mockProviewClient);
	}
	
	private void assertGroup(GroupDefinition groupDef, String groupName, String headTitle, String firstSubgroupName, 
			String secondSubgroup, Integer startIndex) {
		Assert.assertEquals(headTitle, groupDef.getHeadTitle());
		Assert.assertEquals(groupName,groupDef.getName());
		Assert.assertEquals(startIndex + 2,groupDef.getSubGroupInfoList().size());
		
		SubGroupInfo subgroupInfo = groupDef.getSubGroupInfoList().get(startIndex);
		Assert.assertEquals(firstSubgroupName, subgroupInfo.getHeading());
		Assert.assertEquals(2, subgroupInfo.getTitles().size());
		Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v2", subgroupInfo.getTitles().get(0));
		Assert.assertEquals("uscl/an/book_lohisplitnodeinfo_pt2/v2", subgroupInfo.getTitles().get(1));
		
		SubGroupInfo subgroupInfo2 = groupDef.getSubGroupInfoList().get(startIndex + 1);
		Assert.assertEquals(secondSubgroup, subgroupInfo2.getHeading());
		Assert.assertEquals(2, subgroupInfo2.getTitles().size());
		Assert.assertEquals("uscl/an/book_lohisplitnodeinfo/v1", subgroupInfo2.getTitles().get(0));
		Assert.assertEquals("uscl/an/book_lohisplitnodeinfo_pt2/v1", subgroupInfo2.getTitles().get(1));
	}
	
	@Test
	public void testMajorVersion() {
		ProviewTitleContainer proviewTitleContainer = new ProviewTitleContainer();
		ProviewTitleInfo title = new ProviewTitleInfo();
		title.setTitleId("uscl/an/title_id");
		title.setVersion("v2");
		List<ProviewTitleInfo> titleList = new ArrayList<ProviewTitleInfo>();
		titleList.add(title);

		ProviewTitleInfo title1 = new ProviewTitleInfo();
		title1.setTitleId("uscl/an/title_id");
		title1.setVersion("v3");
		titleList.add(title1);

		proviewTitleContainer.setProviewTitleInfos(titleList);

		String titleId = "uscl/an/title_id";

		try {
			EasyMock.expect(mockProviewClient.getProviewTitleContainer("uscl/an/title_id")).andReturn(proviewTitleContainer);
			EasyMock.replay(mockProviewClient);

			List<ProviewTitleInfo> proviewTitleInfo = groupService.getMajorVersionProviewTitles(titleId);			
			Assert.assertEquals(new Integer(3), proviewTitleInfo.get(0).getMajorVersion());
			Assert.assertEquals(1, proviewTitleInfo.size());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
}
