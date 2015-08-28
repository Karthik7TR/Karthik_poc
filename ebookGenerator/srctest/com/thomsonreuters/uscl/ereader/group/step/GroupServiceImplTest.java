package com.thomsonreuters.uscl.ereader.group.step;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.thomsonreuters.uscl.ereader.GroupDefinition.SubGroupInfo;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookName;
import com.thomsonreuters.uscl.ereader.core.book.domain.PublisherCode;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetadataService;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetadataServiceImpl;
import com.thomsonreuters.uscl.ereader.util.UuidGenerator;

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
		List<SubGroupInfo> subGroupInfoList = groupService.getSubGroupsFromProviewResponse(groupInfoXML,"v1");
		Assert.assertEquals(1,subGroupInfoList.size());
		for (SubGroupInfo sub : subGroupInfoList){
			Assert.assertEquals("2015",sub.getHeading());
		}
	}
	
	@Test
	public void testGetGroupDefNoVersionmatch() throws Exception {
		List<SubGroupInfo> subGroupInfoList = groupService.getSubGroupsFromProviewResponse(groupInfoXML,"v3");
		Assert.assertEquals(2,subGroupInfoList.size());
		int i = 0;
		for (SubGroupInfo sub : subGroupInfoList){
			if (i==0)
			Assert.assertEquals("2014",sub.getHeading());
			else
				Assert.assertEquals("2015",sub.getHeading());
			i++;
		}
	}
	
	@Test
	public void testGetGroupID(){
		BookDefinition bookDefinition = new BookDefinition();
		PublisherCode publisherCode = new PublisherCode();
		publisherCode.setId(new Long(1));
		publisherCode.setName("ucl");
		bookDefinition.setPublisherCodes(publisherCode);
		bookDefinition.setFullyQualifiedTitleId("uscl/an/title");
		Assert.assertEquals("ucl/title",groupService.getGroupId(bookDefinition));
	}
	
	@Test
	public void testGetGroupDefEmptyList() throws Exception {
		groupInfoXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<group id=\"uscl/grouptest\" status=\"Review\"><name>Group Test</name><type>standard</type><headtitle>uscl/an/book_lohisplitnodeinfo/v1</headtitle>"
				+ "<members><subgroup heading=\"2014\"><title>uscl/an/book_lohisplitnodeinfo/v1</title><title>uscl/an/book_lohisplitnodeinfo_pt2/v1</title></subgroup>"
				+ "</members></group>";

		List<SubGroupInfo> subGroupInfoList = groupService.getSubGroupsFromProviewResponse(groupInfoXML,"v1");
		Assert.assertEquals(0,subGroupInfoList.size());
	}
	
	
	@Test
	public void testGetGroupDefNoPreviousSub() throws Exception {
		List<SubGroupInfo> subGroupInfoList = new ArrayList<SubGroupInfo>();
		groupInfoXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><group id=\"uscl/grouptest\" status=\"Review\">"
				+ "<name>Main Sub Series</name><type>standard</type><headtitle>uscl/an/book_lohigrouptest/v1</headtitle><members></members></group>";
	
		subGroupInfoList.addAll(groupService.getSubGroupsFromProviewResponse(groupInfoXML,"v1"));
		Assert.assertEquals(0,subGroupInfoList.size());
		
	}
	
	@Test
	public void testgetSubGroupInfo() {
		Long jobInstanceId = new Long(1);
		 List<String> splitTitles = new ArrayList<String>();
		 splitTitles.add("splitTitle1");
		 splitTitles.add("splitTitle2");
		 splitTitles.add("splitTitle3");

		 
		 DocMetadataService mockDocMetadataService = EasyMock.createMock(DocMetadataService.class);
		 EasyMock.expect(mockDocMetadataService.findDistinctSplitTitlesByJobId(jobInstanceId)).andReturn(splitTitles);
		 EasyMock.replay(mockDocMetadataService);
		 groupService.setDocMetadataService(mockDocMetadataService);
		 
		 SubGroupInfo subGroup = groupService.getSubGroupInfo(jobInstanceId, "v1");
		 String[] monthName = { "January", "February", "March", "April", "May", "June", "July",
			        "August", "September", "October", "November", "December" };
			Calendar cal = Calendar.getInstance();
			int year = cal.get(Calendar.YEAR);
		 Assert.assertEquals(3,subGroup.getTitles().size());
		 Assert.assertEquals(monthName[cal.get(Calendar.MONTH)]+" "+String.valueOf(year),subGroup.getHeading());
		
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
