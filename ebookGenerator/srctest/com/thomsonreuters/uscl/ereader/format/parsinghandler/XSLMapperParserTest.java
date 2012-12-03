/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.format.parsinghandler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author <a href="mailto:Dong.Kim@thomsonreuters.com">Dong Kim</a> u0155568
 */
public class XSLMapperParserTest {

	private XSLMapperParser xslMapperParser;
	
	@Before
	public void setUp() throws Exception
	{
		
		xslMapperParser = new XSLMapperParser();
	}
	
	@After
	public void tearDown() throws Exception
	{
		xslMapperParser = null;
	}
	
	/** Helper method that sets up the repeating pieces of each test and just modifies the input and output.
	 * 
	 * @param inputXML input string for the test.
	 * @param expectedResult the expected output for the specified input string.
	 */
	public void testHelper(String inputXML, Map<String,String> expectedResult)
	{

		ByteArrayInputStream input = null;
		try
		{
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setNamespaceAware(true);
			SAXParser saxParser = factory.newSAXParser();
			
			input = new ByteArrayInputStream(inputXML.getBytes());

			saxParser.parse(input, xslMapperParser);
			
			Map<String,String> result = xslMapperParser.getXSLTMapperEntityByCollectionName();
			

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
			}
			catch (Exception e)
			{
				fail("Could clean up resources: " + e.getMessage());
			}
		}
	}
	
	@Test
	public void testMapperWithoutDocType()
	{
		StringBuilder xmlTestStr = new StringBuilder();
		xmlTestStr.append("<CollectionMappingData>");
		xmlTestStr.append("<ContentTypes>");
		xmlTestStr.append("<ContentType ContentTypeId=\"290\" ContentTypeName=\"Admin Decisions - CCH\" Stylesheet=\"AdminDecisionsCCH.xsl\" />");
		xmlTestStr.append("</ContentTypes>");
		xmlTestStr.append("<CollectionGroup CollectionName=\"w_3rd_cchabac\" SubgroupName=\"Default\" ContentTypeName=\"Admin Decisions - CCH\" />");
		xmlTestStr.append("</CollectionMappingData>");
		
		Map<String, String> expectedResult = new HashMap<String, String>();
		expectedResult.put("w_3rd_cchabac", "AdminDecisionsCCH.xsl");
		
		testHelper(xmlTestStr.toString(), expectedResult);
	}
	
	@Test
	public void testMapperWithDocType()
	{
		StringBuilder xmlTestStr = new StringBuilder();
		xmlTestStr.append("<CollectionMappingData>");
		xmlTestStr.append("<ContentTypes>");
		xmlTestStr.append("<ContentType ContentTypeId=\"290\" ContentTypeName=\"Admin Decisions - CCH\" Stylesheet=\"AdminDecisionsCCH.xsl\" />");
		xmlTestStr.append("</ContentTypes>");
		xmlTestStr.append("<CollectionGroup CollectionName=\"w_3rd_cchabac\" SubgroupName=\"w_3rd_cchabac 6G\" ContentTypeName=\"Admin Decisions - CCH\">");
		xmlTestStr.append("<Attribute XPathValue=\"6G\" />");
		xmlTestStr.append("</CollectionGroup>");
		xmlTestStr.append("</CollectionMappingData>");
		
		Map<String, String> expectedResult = new HashMap<String, String>();
		expectedResult.put("w_3rd_cchabac 6G", "AdminDecisionsCCH.xsl");
		
		testHelper(xmlTestStr.toString(), expectedResult);
	}
	
}
