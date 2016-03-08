package com.thomsonreuters.uscl.ereader.deliver.service;

import java.util.List;

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
		String allGroupsResponse = "<groups><group id=\"uscl/az_local_qed_cwb_auto_split\" status=\"Review\" version=\"v92\"><name>Test Group Name</name>"
				+"<type>standard</type><headtitle>uscl/cr/az_local_qed_cwb_auto_split/v1</headtitle><members><subgroup heading=\"November 2015\">"
				+"<title>uscl/cr/az_local_qed_cwb_auto_split/v1</title><title>uscl/cr/az_local_qed_cwb_auto_split_pt2/v1</title></subgroup>"
				+"</members></group><group id=\"uscl/az_local_qed_cwb_auto_split\" status=\"Review\" version=\"v91\"><name>Test Group Name</name>"
				+"<type>standard</type><headtitle>uscl/cr/az_local_qed_cwb_auto_split/v1</headtitle>"
				+"<members><subgroup heading=\"November 2015\"><title>uscl/cr/az_local_qed_cwb_auto_split/v1</title><title>uscl/cr/az_local_qed_cwb_auto_split_pt2/v1</title></subgroup></members></group></groups>";
		List<ProviewGroup> map = parser.process(allGroupsResponse);
		Assert.assertEquals(2,map.size());
		Assert.assertEquals("uscl/az_local_qed_cwb_auto_split",map.get(0).getGroupId());
	}

}
