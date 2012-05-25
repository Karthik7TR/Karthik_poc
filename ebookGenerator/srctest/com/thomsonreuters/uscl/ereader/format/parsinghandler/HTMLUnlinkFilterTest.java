/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.format.parsinghandler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.xml.serializer.Method;
import org.apache.xml.serializer.OutputPropertiesFactory;
import org.apache.xml.serializer.Serializer;
import org.apache.xml.serializer.SerializerFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadata;

/**
 * Test various HTMLUnlinkInternalLinksFilter data scenarios.
 *
 * @author <a href="mailto:Kirsten.Gunn@thomsonreuters.com">Kirsten Gunn</a> u0076257
 */
public class HTMLUnlinkFilterTest {

	private HTMLUnlinkInternalLinksFilter unlinkFilter;
	private Serializer serializer;
	private final String currentGuid = "ABC1234";
	private final String foundAnchor = "er:#ABC1234/foundAnchor";
	private final String foundAnchorId = "foundAnchor";
	
	
	@Before
	public void setUp() throws Exception
	{
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setNamespaceAware(true);
		SAXParser saxParser = factory.newSAXParser();
		HashMap<String, HashSet<String>> targetAnchors = new HashMap<String, HashSet<String>>();
		HashSet<String> hs = new HashSet<String>();
		hs.add(foundAnchor);
		hs.add(foundAnchor+"new");
		targetAnchors.put(currentGuid, hs);
		ArrayList<String> unlinkDocMetadataList = new ArrayList<String>();
		DocMetadata unlinkDocMetadata = new DocMetadata();
		unlinkFilter = new HTMLUnlinkInternalLinksFilter();
		unlinkFilter.setParent(saxParser.getXMLReader());
		unlinkFilter.setCurrentGuid(currentGuid);
		unlinkFilter.setTargetAnchors(targetAnchors);
		unlinkFilter.setUnlinkDocMetadataList(unlinkDocMetadataList);
		unlinkFilter.setUnlinkDocMetadata(unlinkDocMetadata);
		
		Properties props = OutputPropertiesFactory.getDefaultMethodProperties(Method.XHTML);
		props.setProperty("omit-xml-declaration", "yes");
		serializer = SerializerFactory.getSerializer(props);
	
	
	}
	
	@After
	public void tearDown() throws Exception
	{
		serializer = null;
		unlinkFilter = null;
	}
	
	/** 
	 * Helper method that sets up the repeating pieces of each test and modifies the ImageService
	 * values that are returned along with the input and output.
	 * 
	 * @param inputXML input string for the test.
	 * @param expectedResult the expected output for the specified input string.
	 */
	public void testHelper(String inputXML, String expectedResult) throws SAXException
	{
		ByteArrayInputStream input = null;
		ByteArrayOutputStream output = null;
		try
		{
			input = new ByteArrayInputStream(inputXML.getBytes());
			output = new ByteArrayOutputStream();
			
			serializer.setOutputStream(output);
			
			unlinkFilter.setContentHandler(serializer.asContentHandler());
			unlinkFilter.parse(new InputSource(input));
			
			String result = output.toString();
			
			assertEquals(expectedResult, result);
		}
		catch (SAXException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			fail("Encountered exception during test: " + e.getMessage());
		}
		finally
		{
			try
			{
				if (input != null)
				{
					input.close();
				}
				if (output != null)
				{
					output.close();
				}
			}
			catch (Exception e)
			{
				fail("Could clean up resources: " + e.getMessage());
			}
		}
	}
	
	
	@Test
	public void testRemoveAnchorTagFromId() throws SAXException
	{
		
		String xmlTestStr = "<test><a href=\""+foundAnchor+ "\"><sup>1</sup></a></test>";
		String expectedResult = "<test><sup>1</sup></test>";
		
		testHelper(xmlTestStr, expectedResult);
	}
	@Test
	public void testRemoveAnchorTagFromIdTwice() throws SAXException
	{
		
		String xmlTestStr = "<test><a href=\""+foundAnchor+ "\"><sup>1</sup></a><a href=\""+foundAnchor+ "\"><sup>2</sup></a></test>";
		String expectedResult = "<test><sup>1</sup><sup>2</sup></test>";
		
		testHelper(xmlTestStr, expectedResult);
	}
	@Test
	public void testRemoveAnchorTagFromIdWithEmbededTags() throws SAXException
	{
		
		String xmlTestStr = "<test><sup id=\""+foundAnchorId+ "\"><a href=\""+foundAnchor+ "\"><strong>1</strong></a></sup></test>";
		String expectedResult = "<test><sup id=\""+foundAnchorId+ "\"><strong>1</strong></sup></test>";
		
		testHelper(xmlTestStr, expectedResult);
	}
	
	@Test
	public void testRemoveAnchorTagFromIdWithExtraTags() throws SAXException
	{
		
		String xmlTestStr = "<test><div>divVal</div><sup id=\""+foundAnchorId+ "\"><a href=\"" + foundAnchor +"\"><strong>1</strong></a></sup></test>";
		String expectedResult = "<test><div>divVal</div><sup id=\""+foundAnchorId+ "\"><strong>1</strong></sup></test>";
				
		testHelper(xmlTestStr, expectedResult);
	}
	
	@Test
	public void testRemoveAnchorTagFromIdWithExtraEmbeddedTags() throws SAXException
	{
		
		String xmlTestStr = "<test><div><div>divVal</div><sup id=\""+foundAnchorId+ "\"><a href=\"" + foundAnchor +"\"><strong>1</strong></a></sup></div></test>";
		String expectedResult = "<test><div><div>divVal</div><sup id=\""+foundAnchorId+ "\"><strong>1</strong></sup></div></test>";
				
		testHelper(xmlTestStr, expectedResult);
	}
	
	@Test
	public void testRemoveAnchorTagFromTwoIdWithExtraEmbeddedTags() throws SAXException
	{
		
		String xmlTestStr = "<test><div><div>divVal</div><sup id=\""+foundAnchorId+ "\"><a href=\"" + foundAnchor +"\"><span id=\""+foundAnchorId+ "\" class=\"KG\"><strong>1</strong></span></a></sup></div></test>";
		String expectedResult = "<test><div><div>divVal</div><sup id=\""+foundAnchorId+ "\"><span id=\""+foundAnchorId+ "\" class=\"KG\"><strong>1</strong></span></sup></div></test>";
				
		testHelper(xmlTestStr, expectedResult);
	}
	@Test
	public void testRemoveAnchorTagFromTwoIdDifferentNamesNested() throws SAXException
	{
		
		String xmlTestStr = "<test><div><div>divVal</div><sup id=\""+foundAnchorId+ "\"><a href=\"" + foundAnchor +"\"><span id=\""+foundAnchorId+ "new\" class=\"KG\"><a href=\"" + foundAnchor +"new\"><strong>1</strong></a></span></a></sup></div></test>";
		String expectedResult = "<test><div><div>divVal</div><sup id=\""+foundAnchorId+ "\"><span id=\""+foundAnchorId+ "new\" class=\"KG\"><strong>1</strong></span></sup></div></test>";
				
		testHelper(xmlTestStr, expectedResult);
	}
	@Test
	public void testRemoveAnchorTagFromTwoIdDifferentNames() throws SAXException
	{
		
		String xmlTestStr = "<test><div><div>divVal</div><sup id=\""+foundAnchorId+ "\"><a href=\"" + foundAnchor +"\">SupText</a></sup><span id=\""+foundAnchorId+ "new\" class=\"KG\"><a href=\"" + foundAnchor +"new\"><strong>1</strong></a></span></div></test>";
		String expectedResult = "<test><div><div>divVal</div><sup id=\""+foundAnchorId+ "\">SupText</sup><span id=\""+foundAnchorId+ "new\" class=\"KG\"><strong>1</strong></span></div></test>";
				
		testHelper(xmlTestStr, expectedResult);
	}
	@Test
	public void testDoNotRemoveAnchorTagFromSpanNotInTargetList() throws SAXException
	{
		
		String xmlTestStr = "<test><div><div>divVal</div><sup id=\""+foundAnchorId+ "\"><a href=\"" + foundAnchor +"\"><span id=\""+foundAnchorId + "notinlist\" class=\"KG\"><a href=\"" + foundAnchor +"notinlist\"><strong>1<br/>break</strong></a></span></a></sup></div></test>";
		String expectedResult = "<test><div><div>divVal</div><sup id=\""+foundAnchorId+ "\"><span id=\""+foundAnchorId+ "notinlist\" class=\"KG\"><a href=\"" + foundAnchor +"notinlist\"><strong>1<br/>break</strong></a></span></sup></div></test>";
				
		testHelper(xmlTestStr, expectedResult);
	}
	@Test
	public void testRemoveAnchorTagWithNestingTags() throws SAXException
	{
		
		String xmlTestStr = "<test><div><div>divVal</div><strong><sup id=\""+foundAnchorId+ "\"><a href=\"" + foundAnchor +"\"><strong><span id=\""+foundAnchorId+ "new\" class=\"KG\"><a href=\"" + foundAnchor +"new\">text</a></span></strong><strong>1<br/>break</strong></a></sup></strong></div></test>";
		String expectedResult = "<test><div><div>divVal</div><strong><sup id=\""+foundAnchorId+ "\"><strong><span id=\""+foundAnchorId+ "new\" class=\"KG\">text</span></strong><strong>1<br/>break</strong></sup></strong></div></test>";
				
		testHelper(xmlTestStr, expectedResult);
	}
	
	@Test
	public void testNoIdField() throws SAXException
	{
		
		String xmlTestStr = "<test><div>divVal</div><strong>1</strong></test>";
		String expectedResult = "<test><div>divVal</div><strong>1</strong></test>";
				
		testHelper(xmlTestStr, expectedResult);
	}
	@Test
	public void testReplaceAnchorTagFromId() throws SAXException
	{
		HashMap<String, String> anchorDupTargets = new HashMap<String, String>();
		anchorDupTargets.put(foundAnchor, foundAnchor+"new");
		unlinkFilter.setAnchorDupTargets(anchorDupTargets);

		String xmlTestStr = "<test><a href=\""+foundAnchor+ "\"><sup>1</sup></a></test>";
		String expectedResult = "<test><a href=\""+foundAnchor+ "new\"><sup>1</sup></a></test>";
		
		testHelper(xmlTestStr, expectedResult);
	}
	@Test
	public void testReplaceAnchorTagFromIdMultiple() throws SAXException
	{
		HashMap<String, String> anchorDupTargets = new HashMap<String, String>();
		anchorDupTargets.put(foundAnchor, foundAnchor+"_new2");
		anchorDupTargets.put(foundAnchor, foundAnchor+"new");
		unlinkFilter.setAnchorDupTargets(anchorDupTargets);

		String xmlTestStr = "<test><a href=\""+foundAnchor+ "\"><sup>1</sup></a></test>";
		String expectedResult = "<test><a href=\""+foundAnchor+ "new\"><sup>1</sup></a></test>";
		
		testHelper(xmlTestStr, expectedResult);
	}
	@Test
	public void testNOReplaceAnchorTagFromIdMultiple() throws SAXException
	{
		HashMap<String, String> anchorDupTargets = new HashMap<String, String>();
		anchorDupTargets.put(foundAnchor+"new", foundAnchor+"_new2");
		unlinkFilter.setAnchorDupTargets(anchorDupTargets);

		String xmlTestStr = "<test><a href=\""+foundAnchor+ "\"><sup>1</sup></a></test>";
		String expectedResult = "<test><sup>1</sup></test>";
		
		testHelper(xmlTestStr, expectedResult);
	}
}
