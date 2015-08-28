package com.thomsonreuters.uscl.ereader.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLXpathEvaluatorTest {
	
	@Before
	public void setUp()
	{	}

	@After
	public void tearDown() 
	{
		
	}
	
	@Test
	public void testExtractWithDom() throws ParserConfigurationException, SAXException, IOException{
		String xmlstring = "<parent><child attr='attr1'><prop>propvalue</prop></child></parent>";
		ByteArrayInputStream xml = new ByteArrayInputStream(xmlstring.getBytes());
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder()	;
		Document dom     = builder.parse(xml);
		
		XMLXpathEvaluator extractor = new XMLXpathEvaluator(dom);
		Assert.assertEquals( "propvalue" , extractor.evaluate("/parent/child/prop"));
		Assert.assertEquals( "attr1", extractor.evaluate("/parent/child/@attr"));
	}
	
	@Test
	public void testExtract() throws ParserConfigurationException, SAXException, IOException{
		String xmlstring = "<parent><child attr='attr1'><prop>propvalue</prop></child></parent>";
		XMLXpathEvaluator extractor = new XMLXpathEvaluator(xmlstring);
		Assert.assertEquals( "propvalue", extractor.evaluate("/parent/child/prop"));
		Assert.assertEquals( "attr1", extractor.evaluate("/parent/child/@attr"));
	}
	
	
	@Test
	public void testExtractMoreCases() throws Exception{
		String xmlstring = "<parent><child attr='attr1'><prop>propvalue</prop></child></parent>";

		XMLXpathEvaluator extractor = new XMLXpathEvaluator(xmlstring);
		assert null == extractor.evaluate("/parent/abc/prop");
		assert null == extractor.evaluate("/parent/child/@abc");
		
		List<String> list = extractor.evaluateList("/parent/child");
		Assert.assertEquals( 1 ,list.size());
		
		Node node = extractor.evaluateNode("/parent/child");
		Assert.assertNotNull(node);
			
	}
	
	@Test
	public void testExtractBadXpath() throws Exception{
		String xmlstring = "<parent><child attr='attr1'><prop>propvalue</prop></child></parent>";

		XMLXpathEvaluator extractor = new XMLXpathEvaluator(xmlstring);
		Assert.assertEquals (null ,extractor.evaluate("/parent/abc=123/prop"));
		
		
		List<String> list = extractor.evaluateList("/parent/child=123");
		Assert.assertEquals( 0 , list.size());
		
		Node node = extractor.evaluateNode("/parent/child=123");
		Assert.assertEquals( null ,node);
	}
	
	@Test
	public void testExtractToString() throws Exception{
		String xmlstring = "<parent><child attr='attr1'><prop>propvalue</prop></child></parent>";

		XMLXpathEvaluator extractor = new XMLXpathEvaluator(xmlstring);
		
		String expected = "<?xml version=\"1.0\" encoding=\"UTF-16\"?>\n<parent><child attr=\"attr1\"><prop>propvalue</prop></child></parent>";
		Assert.assertEquals(expected,extractor.toXml());
	}
	
	@Test
	public void testExtractNodeList() throws ParserConfigurationException, SAXException, IOException {
		String xmlstring = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<group id=\"uscl/grouptest\" status=\"Review\"><name>Group Test</name><type>standard</type><headtitle>uscl/an/book_lohisplitnodeinfo/v1</headtitle>"
				+ "<members><subgroup heading=\"2014\"><title>uscl/an/book_lohisplitnodeinfo/v1</title><title>uscl/an/book_lohisplitnodeinfo_pt2/v1</title></subgroup>"
				+ "<subgroup heading=\"2015\"><title>uscl/an/book_lohisplitnodeinfo/v2</title><title>uscl/an/book_lohisplitnodeinfo_pt2/v2</title></subgroup></members></group>";

		XMLXpathEvaluator extractor = new XMLXpathEvaluator(xmlstring);
		NodeList subGroups = extractor.evaluateNodeList("group/members/subgroup");
		Assert.assertEquals(2,subGroups.getLength());
		
	}

}
