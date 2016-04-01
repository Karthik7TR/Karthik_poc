package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewgroup;

import java.io.StringReader;

import javax.xml.parsers.SAXParserFactory;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public class GroupXMLParserTest {
	
	GroupXMLParser groupXMLParser;
	
	@Before
	public void setUp() throws Exception {
		groupXMLParser = new GroupXMLParser();
	}
	
	@Test
	public void testProviewGroup() throws Exception{
		String proviewResponse = "<group id=\"uscl/book_grouptestqed\" status=\"Final\" version=\"v13\">"
				+ "<name>GroupQED</name><type>standard</type><headtitle>uscl/an/book_grouptestqed/v1</headtitle>"
				+ "<members><subgroup heading=\"SubGroup\">"
				+ "<title>uscl/an/book_grouptestqed/v1</title>"
				+ "<title>uscl/an/book_grouptestqed_pt2/v1</title>"
				+ "<title>uscl/an/book_grouptestqed_pt6/v1</title></subgroup>"
				+ "</members></group>";
		
		SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		parserFactory.setNamespaceAware(true);
		XMLReader reader = parserFactory.newSAXParser().getXMLReader();
		
		
		reader.setContentHandler(groupXMLParser);
		reader.parse(new InputSource(new StringReader(proviewResponse)));
		Assert.assertEquals("GroupQED",groupXMLParser.getGroupName());	
		Assert.assertEquals("Final",groupXMLParser.getGroupStatus());	
		Assert.assertEquals("uscl/an/book_grouptestqed/v1",groupXMLParser.getHeadTitle());
		Assert.assertEquals(1,groupXMLParser.getSubGroupVersionListMap().size());
		Assert.assertEquals(true,groupXMLParser.getSubGroupVersionListMap().containsKey("SubGroup"));
		Assert.assertEquals(3,groupXMLParser.getTitleIdList().size());
	}
	
	@Test
	public void testProviewGroupwithoutSubgroup() throws Exception{
		String proviewResponse = "<group id=\"uscl/book_grouptestqed\" status=\"Final\" version=\"v13\">"
				+ "<name>GroupQED</name><type>standard</type><headtitle>uscl/an/book_grouptestqed/v1</headtitle>"
				+ "<members><subgroup>"
				+ "<title>uscl/an/book_grouptestqed/v1</title>"
				+ "<title>uscl/an/book_grouptestqed_pt2/v1</title>"
				+ "<title>uscl/an/book_grouptestqed_pt6/v1</title></subgroup>"
				+ "</members></group>";
		
		SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		parserFactory.setNamespaceAware(true);
		XMLReader reader = parserFactory.newSAXParser().getXMLReader();
		
		
		reader.setContentHandler(groupXMLParser);
		reader.parse(new InputSource(new StringReader(proviewResponse)));
		Assert.assertEquals("GroupQED",groupXMLParser.getGroupName());	
		Assert.assertEquals("Final",groupXMLParser.getGroupStatus());	
		Assert.assertEquals("uscl/an/book_grouptestqed/v1",groupXMLParser.getHeadTitle());
		Assert.assertEquals(0,groupXMLParser.getSubGroupVersionListMap().size());
		Assert.assertEquals(false,groupXMLParser.getSubGroupVersionListMap().containsKey("SubGroup"));
		Assert.assertEquals(3,groupXMLParser.getTitleIdList().size());
	}


}
