package com.thomsonreuters.uscl.ereader.mgr.web.controller.group.list;

import java.io.StringReader;
import java.util.Map;

import javax.xml.parsers.SAXParserFactory;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public class GroupXMLHandlerTest {
	
	GroupXMLHandler groupXMLHandler;
	
	@Before
	public void setUp() throws Exception {
		groupXMLHandler = new GroupXMLHandler();
	}
	
	@Test
	public void testPreviewContentSelection() throws Exception{
		String proviewResponse = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><group id=\"uscl/book_lohisplitnodeinfo\" status=\"Review\"><name>SplitNodeInfo</name>"
				+ "<type>standard</type><headtitle>uscl/an/book_lohisplitnodeinfo/v1</headtitle><members><subgroup heading=\"2014\"><title>uscl/an/book_lohisplitnodeinfo/v1</title>"
				+ "<title>uscl/an/book_lohisplitnodeinfo_pt2/v1</title></subgroup></members></group>";
		
		SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		parserFactory.setNamespaceAware(true);
		XMLReader reader = parserFactory.newSAXParser().getXMLReader();
		
		
		reader.setContentHandler(groupXMLHandler);
		reader.parse(new InputSource(new StringReader(proviewResponse)));
		Map<String, String> versionSubGroupMap = groupXMLHandler.getVersionSubGroupMap();
		Assert.assertEquals(versionSubGroupMap.size(),1);	
		Assert.assertEquals("SplitNodeInfo",groupXMLHandler.getGroupName());
		Assert.assertEquals("Review",groupXMLHandler.getGroupStatus());
		
	}
	
	@Test
	public void testPreviewContentSelection1() throws Exception{
		String proviewResponse = "<html><head><title>400</title><meta name=\"ereader\" content=\"noindex,nofollow\" /></head><body><h1><resultsummary>No such group id and version exist"
				+ " uscl/grouptest/v1</resultsummary></h1></body></html>";
		
		SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		parserFactory.setNamespaceAware(true);
		XMLReader reader = parserFactory.newSAXParser().getXMLReader();
		
		
		reader.setContentHandler(groupXMLHandler);
		reader.parse(new InputSource(new StringReader(proviewResponse)));
		Map<String, String> versionSubGroupMap = groupXMLHandler.getVersionSubGroupMap();		
		Assert.assertEquals(versionSubGroupMap.size(),0);	
		
	}

}
