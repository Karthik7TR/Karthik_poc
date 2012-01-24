/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.format.parsinghandler;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Test various HTMLInputFilter data scenarios.
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public class HTMLInputFilterTest {

	private HTMLInputFilter inputFilter;
	private Serializer serializer;
	
	@Before
	public void setUp() throws Exception
	{
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setNamespaceAware(true);
		SAXParser saxParser = factory.newSAXParser();
		
		inputFilter = new HTMLInputFilter();
		inputFilter.setParent(saxParser.getXMLReader());
		
		Properties props = OutputPropertiesFactory.getDefaultMethodProperties(Method.XHTML);
		props.setProperty("omit-xml-declaration", "yes");
		serializer = SerializerFactory.getSerializer(props);
	}
	
	@After
	public void tearDown() throws Exception
	{
		serializer = null;
		inputFilter = null;
	}
	
	/** Helper method that sets up the repeating pieces of each test and just modifies the input and output.
	 * 
	 * @param inputXML input string for the test.
	 * @param expectedResult the expected output for the specified input string.
	 */
	public void testHelper(String inputXML, String expectedResult)
	{
		ByteArrayInputStream input = null;
		ByteArrayOutputStream output = null;
		try
		{
			input = new ByteArrayInputStream(inputXML.getBytes());
			output = new ByteArrayOutputStream();
			
			serializer.setOutputStream(output);
			
			inputFilter.setContentHandler(serializer.asContentHandler());
			inputFilter.parse(new InputSource(input));
			
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
				fail("Could clean up resources: " + e.getMessage());
			}
		}
	}
	
	@Test
	public void testValidKeyCiteInputTag()
	{
		String xmlTestStr = "<test><input id=\"co_keyCiteFlagPlaceHolder\" type=\"hidden\"/></test>";
		String expectedResult = "<test/>";
		
		testHelper(xmlTestStr, expectedResult);
	}
	
	@Test
	public void testInputTagWithoutAttributes()
	{
		String xmlTestStr = "<test><input/></test>";
		String expectedResult = "<test/>";
		
		testHelper(xmlTestStr, expectedResult);
	}
	
	@Test
	public void testHiddenTypeOnlyInputTag()
	{
		String xmlTestStr = "<test><input type=\"hidden\"/></test>";
		String expectedResult = "<test/>";
		
		testHelper(xmlTestStr, expectedResult);
	}
	
	@Test
	public void testKeyCiteIDOnlyInputTag()
	{
		String xmlTestStr = "<test><input id=\"co_keyCiteFlagPlaceHolder\"/></test>";
		String expectedResult = "<test/>";
		
		testHelper(xmlTestStr, expectedResult);
	}
	
	@Test
	public void testMultipleValidKeyCiteInputTags()
	{
		String xmlTestStr = "<test><input id=\"co_keyCiteFlagPlaceHolder\" type=\"hidden\"/><input id=\"co_keyCiteFlagPlaceHolder\" type=\"hidden\"/></test>";
		String expectedResult = "<test/>";
		
		testHelper(xmlTestStr, expectedResult);
	}
	
	@Test
	public void testMultipleValidKeyCiteInputTagsWithEmbeddedContent()
	{
		String xmlTestStr = "<test><input id=\"co_keyCiteFlagPlaceHolder\" type=\"hidden\"/><img src=\"test\"/><input id=\"co_keyCiteFlagPlaceHolder\" type=\"hidden\"/></test>";
		String expectedResult = "<test><img src=\"test\"/></test>";
		
		testHelper(xmlTestStr, expectedResult);
	}
	
	@Test
	public void testKeyCiteInputTagWithCDATA()
	{
		String xmlTestStr = "<test><input id=\"co_keyCiteFlagPlaceHolder\" type=\"hidden\">Test123</input></test>";
		String expectedResult = "<test/>";
		
		testHelper(xmlTestStr, expectedResult);
	}
	
	@Test
	public void testInputTagWithCDATA()
	{
		String xmlTestStr = "<test><input>Test123</input></test>";
		String expectedResult = "<test/>";
		
		testHelper(xmlTestStr, expectedResult);
	}
}
