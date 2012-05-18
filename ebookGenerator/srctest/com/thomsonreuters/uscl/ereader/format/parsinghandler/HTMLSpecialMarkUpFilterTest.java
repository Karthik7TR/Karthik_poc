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
import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Test various HTMLMarkUpFilter data scenarios.
 *
 * @author <a href="mailto:Nirupam.Chatterjee@thomsonreuters.com">Nirupam Chatterjee</a> u0072938
 */
public class HTMLSpecialMarkUpFilterTest {

	private HTMLSpecialMarkupFilter specialMarkUpFilter;
	private Serializer serializer;

	
	
	@Before
	public void setUp() throws Exception
	{
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setNamespaceAware(true);
		SAXParser saxParser = factory.newSAXParser();
		HashMap<String, HashSet<String>> targetAnchors = new HashMap<String, HashSet<String>>();
		HashSet<String> hs = new HashSet<String>();

		
		specialMarkUpFilter = new HTMLSpecialMarkupFilter();
		specialMarkUpFilter.setParent(saxParser.getXMLReader());


		Properties props = OutputPropertiesFactory.getDefaultMethodProperties(Method.XHTML);
		props.setProperty("omit-xml-declaration", "yes");
		serializer = SerializerFactory.getSerializer(props);
	
	
	}
	
	@After
	public void tearDown() throws Exception
	{
		serializer = null;
		specialMarkUpFilter = null;
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
			
			specialMarkUpFilter.setContentHandler(serializer.asContentHandler());
			specialMarkUpFilter.parse(new InputSource(input));
			
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
	public void testDelWithSingleEmbeddedElements() throws SAXException
	{
		
		String xmlTestStr = "<del><ins>and</ins></del>";
		String expectedResult = "<span class=\"co_crosshatch\"><ins>and</ins></span>";
		
		testHelper(xmlTestStr, expectedResult);
	}

	@Test
	public void testDelWithAttributes() throws SAXException
	{
		
		String xmlTestStr = "<del>and</del>";
		String expectedResult = "<span class=\"co_crosshatch\">and</span>";
		
		testHelper(xmlTestStr, expectedResult);
	}

	@Test
	public void testDelWithMultipleEmbeddedElements() throws SAXException
	{
		
		String xmlTestStr = "<del><ins><nirupam value=\"2\"></nirupam>and</ins></del>";
		String expectedResult = "<span class=\"co_crosshatch\"><ins><nirupam value=\"2\"/>and</ins></span>";
		
		testHelper(xmlTestStr, expectedResult);
	}
	

}
