/*
* Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.frontmatter.parsinghandler;

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

import com.thomsonreuters.uscl.ereader.FrontMatterFileName;
import com.thomsonreuters.uscl.ereader.frontmatter.parsinghandler.FrontMatterWestlawNextPageFilter;

/**
 * Test various FrontMatterTitlePageFilter data scenarios.
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public class FrontMatterWestlawNextPageFilterTest {
	private FrontMatterWestlawNextPageFilter westlawNextPageFilter;
	private Serializer serializer;
	
	@Before
	public void setUp() throws Exception {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setNamespaceAware(true);
		SAXParser saxParser = factory.newSAXParser();
		
		
		westlawNextPageFilter = new FrontMatterWestlawNextPageFilter();
		westlawNextPageFilter.setParent(saxParser.getXMLReader());
		
		Properties props = OutputPropertiesFactory.getDefaultMethodProperties(Method.XHTML);
		props.setProperty("omit-xml-declaration", "yes");
		serializer = SerializerFactory.getSerializer(props);
	}
	
	@After
	public void tearDown() throws Exception
	{
		serializer = null;
		westlawNextPageFilter = null;
	}
	
	/** 
	 * Helper method that sets up the repeating pieces of each test and modifies the Table
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
			
			westlawNextPageFilter.setContentHandler(serializer.asContentHandler());
			westlawNextPageFilter.parse(new InputSource(input));
			
			String result = output.toString();
			
			assertEquals(expectedResult, result);
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
				fail("Couldn't clean up resources: " + e.getMessage());
			}
		}
	}
	
	@Test
	public void testFrontMatterPlaceholder_WestlawNextPageAnchor() throws SAXException
	{	
		String xmlTestStr = "<test><frontMatterPlaceholder_WestlawNextPageAnchor/></test>";
		String expectedResult = "<test><a name=\"" + FrontMatterFileName.WESTLAWNEXT + 
				FrontMatterFileName.ANCHOR + "\"/></test>";
		
		testHelper(xmlTestStr, expectedResult);
	}
}
