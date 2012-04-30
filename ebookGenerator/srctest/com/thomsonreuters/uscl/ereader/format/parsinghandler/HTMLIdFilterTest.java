/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.format.parsinghandler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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

import org.easymock.EasyMock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Test various HTMLAnchorFilter data scenarios.
 *
 * @author <a href="mailto:Kirsten.Gunn@thomsonreuters.com">Kirsten Gunn</a> u0076257
 */
public class HTMLIdFilterTest {

	private HTMLIdFilter anchorFilter;
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
		
		anchorFilter = new HTMLIdFilter();
		anchorFilter.setParent(saxParser.getXMLReader());
		anchorFilter.setCurrentGuid(currentGuid);
		anchorFilter.setTargetAnchors(targetAnchors);

		Properties props = OutputPropertiesFactory.getDefaultMethodProperties(Method.XHTML);
		props.setProperty("omit-xml-declaration", "yes");
		serializer = SerializerFactory.getSerializer(props);
	
	
	}
	
	@After
	public void tearDown() throws Exception
	{
		serializer = null;
		anchorFilter = null;
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
			
			anchorFilter.setContentHandler(serializer.asContentHandler());
			anchorFilter.parse(new InputSource(input));
			
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
	public void testCreateAnchorTagFromId() throws SAXException
	{
		
		String xmlTestStr = "<test><sup id=\""+foundAnchorId+ "\">1</sup></test>";
		String expectedResult = "<test><sup id=\""+foundAnchorId+ "\"><a name=\"" + foundAnchorId +"\">1</a></sup></test>";
		
		testHelper(xmlTestStr, expectedResult);
	}
	
	@Test
	public void testCreateAnchorTagFromIdTwice() throws SAXException
	{
		
		String xmlTestStr = "<test><sup id=\""+foundAnchorId+ "\">1</sup><sup id=\""+foundAnchorId+ "new\">2</sup></test>";
		String expectedResult = "<test><sup id=\""+foundAnchorId+ "\"><a name=\"" + foundAnchorId +"\">1</a></sup><sup id=\""+foundAnchorId+ "new\"><a name=\"" + foundAnchorId +"new\">2</a></sup></test>";
		
		testHelper(xmlTestStr, expectedResult);
	}
	@Test
	public void testCreateAnchorTagFromIdWithEmbededTags() throws SAXException
	{
		
		String xmlTestStr = "<test><sup id=\""+foundAnchorId+ "\"><strong>1</strong></sup></test>";
		String expectedResult = "<test><sup id=\""+foundAnchorId+ "\"><a name=\"" + foundAnchorId +"\"><strong>1</strong></a></sup></test>";
		
		testHelper(xmlTestStr, expectedResult);
	}
	
	@Test
	public void testCreateAnchorTagFromIdWithExtraTags() throws SAXException
	{
		
		String xmlTestStr = "<test><div>divVal</div><sup id=\""+foundAnchorId+ "\"><strong>1</strong></sup></test>";
		String expectedResult = "<test><div>divVal</div><sup id=\""+foundAnchorId+ "\"><a name=\"" + foundAnchorId +"\"><strong>1</strong></a></sup></test>";
				
		testHelper(xmlTestStr, expectedResult);
	}
	
	@Test
	public void testCreateAnchorTagFromIdWithExtraEmbeddedTags() throws SAXException
	{
		
		String xmlTestStr = "<test><div><div>divVal</div><sup id=\""+foundAnchorId+ "\"><strong>1</strong></sup></div></test>";
		String expectedResult = "<test><div><div>divVal</div><sup id=\""+foundAnchorId+ "\"><a name=\"" + foundAnchorId +"\"><strong>1</strong></a></sup></div></test>";
				
		testHelper(xmlTestStr, expectedResult);
	}
	
	@Test
	public void testCreateAnchorTagFromTwoIdWithExtraEmbeddedTags() throws SAXException
	{
		
		String xmlTestStr = "<test><div><div>divVal</div><sup id=\""+foundAnchorId+ "\"><span id=\""+foundAnchorId+ "\" class=\"KG\"><strong>1</strong></span></sup></div></test>";
		String expectedResult = "<test><div><div>divVal</div><sup id=\""+foundAnchorId+ "\"><a name=\"" + foundAnchorId +"\"><span id=\""+foundAnchorId+ "\" class=\"KG\"><strong>1</strong></span></a></sup></div></test>";
				
		testHelper(xmlTestStr, expectedResult);
	}
	@Test
	public void testCreateAnchorTagFromTwoIdDifferentNamesNested() throws SAXException
	{
		
		String xmlTestStr = "<test><div><div>divVal</div><sup id=\""+foundAnchorId+ "\"><span id=\""+foundAnchorId+ "new\" class=\"KG\"><strong>1</strong></span></sup></div></test>";
		String expectedResult = "<test><div><div>divVal</div><sup id=\""+foundAnchorId+ "\"><a name=\"" + foundAnchorId +"\"><span id=\""+foundAnchorId+ "new\" class=\"KG\"><a name=\"" + foundAnchorId +"new\"><strong>1</strong></a></span></a></sup></div></test>";
				
		testHelper(xmlTestStr, expectedResult);
	}
	@Test
	public void testCreateAnchorTagFromTwoIdDifferentNames() throws SAXException
	{
		
		String xmlTestStr = "<test><div><div>divVal</div><sup id=\""+foundAnchorId+ "\">SupText</sup><span id=\""+foundAnchorId+ "new\" class=\"KG\"><strong>1</strong></span></div></test>";
		String expectedResult = "<test><div><div>divVal</div><sup id=\""+foundAnchorId+ "\"><a name=\"" + foundAnchorId +"\">SupText</a></sup><span id=\""+foundAnchorId+ "new\" class=\"KG\"><a name=\"" + foundAnchorId +"new\"><strong>1</strong></a></span></div></test>";
				
		testHelper(xmlTestStr, expectedResult);
	}
	@Test
	public void testDoNotCreateAnchorTagFromSpanNotInTargetList() throws SAXException
	{
		
		String xmlTestStr = "<test><div><div>divVal</div><sup id=\""+foundAnchorId+ "\"><span id=\""+foundAnchorId+ "notinlist\" class=\"KG\"><strong>1<br/>break</strong></span></sup></div></test>";
		String expectedResult = "<test><div><div>divVal</div><sup id=\""+foundAnchorId+ "\"><a name=\"" + foundAnchorId +"\"><span id=\""+foundAnchorId+ "notinlist\" class=\"KG\"><strong>1<br/>break</strong></span></a></sup></div></test>";
				
		testHelper(xmlTestStr, expectedResult);
	}
	@Ignore
	public void testCreateAnchorTagWithNestingTags() throws SAXException
	{
		
		String xmlTestStr = "<test><div><div>divVal</div><strong><sup id=\""+foundAnchorId+ "\"><strong><span id=\""+foundAnchorId+ "new\" class=\"KG\">text</span></strong><strong>1<br/>break</strong></sup></strong></div></test>";
		String expectedResult = "<test><div><div>divVal</div><strong><sup id=\""+foundAnchorId+ "\"><a name=\"" + foundAnchorId +"\"><strong><span id=\""+foundAnchorId+ "new\" class=\"KG\"><a name=\"foundAnchornew\">text</a></span></strong><strong>1<br/>break</strong></a></sup></strong></div></test>";
				
		testHelper(xmlTestStr, expectedResult);
	}
	
	@Test
	public void testNoIdField() throws SAXException
	{
		
		String xmlTestStr = "<test><div>divVal</div><strong>1</strong></test>";
		String expectedResult = "<test><div>divVal</div><strong>1</strong></test>";
				
		testHelper(xmlTestStr, expectedResult);
	}
	

}
