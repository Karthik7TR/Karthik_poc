package com.thomsonreuters.uscl.ereader.group.step;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.thomsonreuters.uscl.ereader.GroupDefinition.SubGroupInfo;
import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookName;

public class GroupServiceImplTest {
	
	private GroupServiceImpl groupService;
	String groupInfoXML;
	
	@Before
	public void setUp() throws Exception
	{
		this.groupService = new GroupServiceImpl();
		groupInfoXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<group id=\"uscl/grouptest\" status=\"Review\"><name>Group Test</name><type>standard</type><headtitle>uscl/an/book_lohisplitnodeinfo/v1</headtitle>"
				+ "<members><subgroup heading=\"2014\"><title>uscl/an/book_lohisplitnodeinfo/v1</title><title>uscl/an/book_lohisplitnodeinfo_pt2/v1</title></subgroup>"
				+ "<subgroup heading=\"2015\"><title>uscl/an/book_lohisplitnodeinfo/v2</title><title>uscl/an/book_lohisplitnodeinfo_pt2/v2</title></subgroup></members></group>";
	}

	@After
	public void tearDown() throws Exception
	{
		
	}
	
	@Test
	public void testGetGroupDef() throws Exception {
		groupService.getSubGroupsFromProviewResponse(groupInfoXML,"v1");
		
	}
	
	@Test
	public void testGetGroupDefNoPreviousSub() throws Exception {
		List<SubGroupInfo> subGroupInfoList = new ArrayList<SubGroupInfo>();
		groupInfoXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><group id=\"uscl/grouptest\" status=\"Review\">"
				+ "<name>Main Sub Series</name><type>standard</type><headtitle>uscl/an/book_lohigrouptest/v1</headtitle><members><subgroup heading=\"August2015\">"
				+ "<title>uscl/an/book_lohigrouptest/v1</title><title>uscl/an/book_lohigrouptest_pt2/v1</title><title>uscl/an/book_lohigrouptest_pt3/v1</title>"
				+ "<title>uscl/an/book_lohigrouptest_pt4/v1</title></subgroup></members></group>";
	
		subGroupInfoList.addAll(groupService.getSubGroupsFromProviewResponse(groupInfoXML,"v1"));
		
	}
	
	
	@Test
	public void testGetGroupNameNoSeries(){
		
		DocumentTypeCode documentTypeCode = new DocumentTypeCode();
		documentTypeCode.setId(new Long(2));
		
		List<EbookName> names = mockNames();
		String groupName = groupService.getGroupName(documentTypeCode, names);
		Assert.assertEquals("Main SubTitle",groupName);
	}
	
	
	@Test
	public void testGetGroupName(){
		DocumentTypeCode documentTypeCode = new DocumentTypeCode();
		documentTypeCode.setId(new Long(1));
		
		List<EbookName> names = mockNames();
		String groupName = groupService.getGroupName(documentTypeCode, names);
		Assert.assertEquals("Main SubTitle Series",groupName);
	}
	
	protected List<EbookName> mockNames(){
		List<EbookName> names = new ArrayList<EbookName>();
		EbookName name1 = new EbookName();
		name1.setSequenceNum(1);
		name1.setBookNameText("Main");
		
		EbookName name2 = new EbookName();
		name2.setSequenceNum(2);
		name2.setBookNameText("SubTitle");
		
		EbookName name3 = new EbookName();
		name3.setSequenceNum(3);
		name3.setBookNameText("Series");
		
		names.add(name1);
		names.add(name2);
		names.add(name3);
		
		return names;
	}

}
