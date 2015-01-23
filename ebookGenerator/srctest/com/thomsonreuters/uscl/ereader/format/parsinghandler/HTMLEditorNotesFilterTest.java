/*
* Copyright 2015: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.format.parsinghandler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
 * Test various HTMLEditorNotesFilter data scenarios.
 *
 * @author <a href="mailto:dong.kim@thomsonreuters.com">Dong Kim</a> u0155568
 */
public class HTMLEditorNotesFilterTest {

	private HTMLEditorNotesFilter filter;
	private Serializer serializer;
	
	private void setUp(boolean removeNote) throws Exception
	{
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setNamespaceAware(true);
		SAXParser saxParser = factory.newSAXParser();
		
		filter = new HTMLEditorNotesFilter(removeNote);
		filter.setParent(saxParser.getXMLReader());


		Properties props = OutputPropertiesFactory.getDefaultMethodProperties(Method.XHTML);
		props.setProperty("omit-xml-declaration", "yes");
		serializer = SerializerFactory.getSerializer(props);
	
	
	}
	
	@After
	public void tearDown() throws Exception
	{
		serializer = null;
		filter = null;
	}
	
	/** 
	 * Helper method that sets up the repeating pieces of each test and modifies the
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
			
			filter.setContentHandler(serializer.asContentHandler());
			filter.parse(new InputSource(input));
			
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
	public void testRemoveNote() throws Exception
	{
		setUp(true);
		String xmlTestStr = "<div eBookEditorNotes=\"true\">this is gone</div>";
		String expectedResult = "";
		
		testHelper(xmlTestStr, expectedResult);
	}
	
	@Test
	public void testRemoveNote2() throws Exception
	{
		setUp(true);
		String xmlTestStr = "<div><div eBookEditorNotes=\"true\"><div>should be gone</div>this is gone</div><div>more text</div></div>";
		String expectedResult = "<div><div>more text</div></div>";
		
		testHelper(xmlTestStr, expectedResult);
	}

	@Test
	public void testKeepHeading() throws Exception
	{
		setUp(false);
		String xmlTestStr = "<div eBookEditorNotes=\"true\"><h2>should be here</h2></div>";
		String expectedResult = "<div eBookEditorNotes=\"true\"><h2>should be here</h2></div>";
		
		testHelper(xmlTestStr, expectedResult);
	}

}
