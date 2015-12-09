package com.thomsonreuters.uscl.ereader.mgr.web.controller.group.list;

import java.io.StringReader;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParserFactory;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.thomsonreuters.uscl.ereader.deliver.service.ProviewGroupInfo;

public class GroupListControllerTest {
	
	private GroupListController groupListController;
	
	@Before
	public void setUp() throws Exception {
		groupListController = new GroupListController();
	}

	@Test
	public void testPreviewContentSelection() throws Exception{
		String proviewResponse = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><group id=\"uscl/book_lohisplitnodeinfo\" status=\"Review\"><name>SplitNodeInfo</name>"
				+ "<type>standard</type><headtitle>uscl/an/book_lohisplitnodeinfo/v1</headtitle><members><subgroup heading=\"2014\"><title>uscl/an/book_lohisplitnodeinfo/v1</title>"
				+ "<title>uscl/an/book_lohisplitnodeinfo_pt2/v1</title></subgroup></members></group>";
		
		SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		parserFactory.setNamespaceAware(true);
		XMLReader reader = parserFactory.newSAXParser().getXMLReader();
		GroupXMLHandler groupXMLHandler = new GroupXMLHandler();
		reader.setContentHandler(groupXMLHandler);
		reader.parse(new InputSource(new StringReader(proviewResponse)));
		Map<String, String> versionSubGroupMap = groupXMLHandler.getSubGroupVersionMap();
		Assert.assertEquals(versionSubGroupMap.size(),1);	
		
	}
}
