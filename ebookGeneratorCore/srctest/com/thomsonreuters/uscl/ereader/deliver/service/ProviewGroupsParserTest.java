package com.thomsonreuters.uscl.ereader.deliver.service;

import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ProviewGroupsParserTest {
	private ProviewGroupsParser parser;
	
	@Before
	public void setUp() throws Exception
	{
		parser = new ProviewGroupsParser();
	}
	
	@Test
	public void testParser(){
		String allGroupsResponse 
				= "<groups><group id=\"testGroupID1\" status=\"Test\" version=\"v1\"><name>Test Group Name</name>"
					+"<type>standard</type><headtitle>testHeadTitle</headtitle><members><subgroup heading=\"Subgroup Heading 1\">"
					+"<title>testTitleId/v1</title><title>testTitleId_pt2/v1</title></subgroup></members></group>"
				+ "<group id=\"testGroupID2\" status=\"Test\" version=\"v2\"><name>Test Group Name</name>"
					+"<type>standard</type><headtitle>testHeadTitle2</headtitle><members><subgroup heading=\"Subgroup Heading 2\">"
					+ "<title>testTitleId/v1</title><title>testTitleId_pt2/v1</title></subgroup></members></group></groups>";
		Map<String, ProviewGroupContainer> map = parser.process(allGroupsResponse);
		
		ProviewGroupContainer container = map.get("testGroupID1");
		Assert.assertEquals(2,map.size());
		Assert.assertEquals("testHeadTitle",container.getProviewGroups().get(0).getHeadTitle());
	}

}
