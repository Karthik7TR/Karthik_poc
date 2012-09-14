/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.format.parsinghandler;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Test various XSLForcePlatformAttributeFilter data scenarios.
 *
 * @author <a href="mailto:Dong.Kim@thomsonreuters.com">Dong Kim</a> u0155568
 */
public class XSLForcePlatformAttributeFilterTest {
	private SAXParser saxParser;
	
	@Before
	public void setUp() throws Exception
	{
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setNamespaceAware(true);
		saxParser = factory.newSAXParser();
	}
	
	/** Helper method that sets up the repeating pieces of each test and just modifies the input
	 * 
	 * @param inputXML input string for the test.
	 * @param expectedResult
	 */
	public void testHelper(String inputXML, String href, boolean expectedResult)
	{
		ByteArrayInputStream input = null;
		try
		{
			XSLForcePlatformAttributeFilter filter = new XSLForcePlatformAttributeFilter(href);
			
			input = new ByteArrayInputStream(inputXML.getBytes());
			saxParser.parse(input, filter);

			assertEquals(expectedResult, filter.isForcePlatform());
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
			}
			catch (Exception e)
			{
				fail("Could clean up resources: " + e.getMessage());
			}
		}
	}
	
	@Test
	public void testForcePlatformNotFound()
	{
		String href = "Somthing.xsl";
		String xmlTestStr = "<nothing></nothing>";
		boolean expectedResult = false;
		
		testHelper(xslWrapper(xmlTestStr), href,expectedResult);
	}
	
	@Test
	public void testForcePlatformNotFound2()
	{
		String href = "Something.xsl";
		String xmlTestStr = "<xsl:include href=\"Something.xsl\" />";
		boolean expectedResult = false;
		
		testHelper(xslWrapper(xmlTestStr), href,expectedResult);
	}
	
	@Test
	public void testForcePlatformFound()
	{
		String href = "Something.xsl";
		String xmlTestStr = "<xsl:include href=\"Something.xsl\" forcePlatform=\"true\" />";
		boolean expectedResult = true;
		
		testHelper(xslWrapper(xmlTestStr), href,expectedResult);
	}
	
	@Test
	public void testForcePlatformFound2()
	{
		String href = "Something.xsl";
		String xmlTestStr = "<xsl:include href=\"Something.xsl\" forcePlatform=\"TrUe\" />";
		boolean expectedResult = true;
		
		testHelper(xslWrapper(xmlTestStr), href,expectedResult);
	}
	
	@Test
	public void testForcePlatformFoundButFalse()
	{
		String href = "Something.xsl";
		String xmlTestStr = "<xsl:include href=\"Something.xsl\" forcePlatform=\"false\" />";
		boolean expectedResult = false;
		
		testHelper(xslWrapper(xmlTestStr), href,expectedResult);
	}
	
	private String xslWrapper(String text)
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append("<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">");
		buffer.append(text);
		buffer.append("</xsl:stylesheet>");
		return buffer.toString();
	}
}
