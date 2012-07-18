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
		String expectedResult = "<span class=\"co_crosshatch\"><span style=\"background-color: #ccffff; text-decoration: none;\">and</span></span>";
		
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
		
		String xmlTestStr = "<del><ins><name>nirupam</name>and</ins></del>";
		String expectedResult = "<span class=\"co_crosshatch\"><span style=\"background-color: #ccffff; text-decoration: none;\"><name>nirupam</name>and</span></span>";
		
		testHelper(xmlTestStr, expectedResult);
	}
	
	@Test
	public void testDelElements() throws SAXException
	{
		
		String xmlTestStr = "<div id=\"co_document\" class=\"co_document co_codesStatutes\"><input alt=\"metadata\" value=\"{ &quot;citations&quot;: {  } }\" id=\"co_document_starPageMetadata\" type=\"hidden\"/><div class=\"co_documentHead\"><div class=\"co_genericBox\"><div class=\"co_genericBoxHeader\"><span/></div><div class=\"co_genericBoxContent\"><div class=\"co_genericBoxContentRight\"><a href=\"#\" class=\"co_widget_collapseIcon\"/><div class=\"co_contentBlock co_prelimBlock\">West's Revised Statutes of Nebraska Annotated <a href=\"#co_anchor_I9E486E80731511E1885FE7C411CD6DC6\" class=\"co_internalLink co_excludeAnnotations\">Currentness</a><div id=\"co_prelimContainer\"><div class=\"co_contentBlock co_prelimHead co_headtext\" id=\"co_prelimGoldenLeaf\">Rules of the District Court of the Tenth Judicial District</div></div></div></div></div><div class=\"co_genericBoxFooter\"><span/></div></div><div class=\"co_cites\">Neb Tenth Judicial District Court Rule 10-15</div><div class=\"co_title\"><div id=\"co_anchor_IA3D5A770E25511DF94BF8E0064702D09\" class=\"co_headtext\"><strong>RULE 10-15. INTERPRETERS</strong></div></div></div><!--DHE--><div class=\"co_contentBlock co_rule\" id=\"co_anchor_IA3D58060E25511DF94BF8E0064702D09\"><div class=\"co_contentBlock co_body\"><div class=\"co_paragraph\" id=\"co_anchor_IA3D5A772E25511DF94BF8E0064702D09\"><div class=\"co_paragraphText\"><ins>It is the duty of a pro se party needing an interpreter or counsel for a party needing an interpreter to notify the clerk of the district court 10 days prior to any hearing of the need for an interpreter. It shall be specified whether an interpreter is needed for one of the parties and/or one or more witnesses. It is not permissible for the parties to use a friend or relative as an interpreter. The cost for an interpreter is not assessed to the parties.</ins></div></div></div><div class=\"co_printHeading\" id=\"co_anchor_Credits\"><h2>Credits</h2></div><div class=\"co_paragraph\" id=\"co_anchor_IA3D5A773E25511DF94BF8E0064702D09\"><div class=\"co_paragraphText\">Adopted effective November 15, 2000. Amended June 30, 2010.</div></div></div>Neb Tenth Judicial District Court Rule 10-15, NE R 10 DIST Rule 10-15<table id=\"co_endOfDocument\"><tr><td>End of Document</td><td class=\"co_endOfDocCopyright\">© 2012 </td></tr></table></div>";
		xmlTestStr = "<div class=\"co_paragraphText\"><ins>It is the duty of a pro se party needing an interpreter or counsel for a party needing an interpreter to notify the clerk of the district court 10 days prior to any hearing of the need for an interpreter. It shall be specified whether an interpreter is needed for one of the parties and/or one or more witnesses. It is not permissible for the parties to use a friend or relative as an interpreter. The cost for an interpreter is not assessed to the parties.</ins></div>";
		String expectedResult = "<div class=\"co_paragraphText\"><span style=\"background-color: #ccffff; text-decoration: none;\">It is the duty of a pro se party needing an interpreter or counsel for a party needing an interpreter to notify the clerk of the district court 10 days prior to any hearing of the need for an interpreter. It shall be specified whether an interpreter is needed for one of the parties and/or one or more witnesses. It is not permissible for the parties to use a friend or relative as an interpreter. The cost for an interpreter is not assessed to the parties.</span></div>";
		
		testHelper(xmlTestStr, expectedResult);
	}
	

}
