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
import java.util.ArrayList;
import java.util.List;
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
import com.thomsonreuters.uscl.ereader.core.book.domain.Author;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookName;
import com.thomsonreuters.uscl.ereader.frontmatter.parsinghandler.FrontMatterResearchAssistancePageFilter;

/**
 * Test various FrontMatterTitlePageFilter data scenarios.
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public class FrontMatterResearchAssistancePageFilterTest {
	private FrontMatterResearchAssistancePageFilter researchAssistancePageFilter;
	private BookDefinition bookDefinition;
	private Serializer serializer;
	
	@Before
	public void setUp() throws Exception {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setNamespaceAware(true);
		SAXParser saxParser = factory.newSAXParser();
		
		bookDefinition = new BookDefinition();
		bookDefinition.setProviewDisplayName("Display TEST Title");
		List<EbookName> ebookNames = new ArrayList<EbookName>();
		EbookName title = new EbookName();
		title.setBookNameText("TEST Title");
		title.setSequenceNum(1);
		ebookNames.add(title);
		EbookName edition = new EbookName();
		edition.setBookNameText("TEST Edition");
		edition.setSequenceNum(2);
		ebookNames.add(edition);
		bookDefinition.setEbookNames(ebookNames);
		bookDefinition.setCurrency("Currency Test");
		
		List<Author> authors = new ArrayList<Author>();
		Author author1 = new Author();
		author1.setAuthorFirstName("John");
		author1.setAuthorLastName("Tester1");
		author1.setAuthorAddlText("Pirate, Arrrrrrrrrrr");
		author1.setSequenceNum(1);
		authors.add(author1);
		
		Author author2 = new Author();
		author2.setAuthorFirstName("Joel");
		author2.setAuthorLastName("Tester2");
		author2.setAuthorAddlText("Also a \r\nPirate, Arrrrrrrrrrr");
		author2.setSequenceNum(2);
		authors.add(author2);
		
		Author author3 = new Author();
		author3.setAuthorFirstName("Ender");
		author3.setAuthorLastName("Tester3");
		author3.setAuthorAddlText("Another \r\nPirate, Arrrrrrrrrrr");
		author3.setSequenceNum(3);
		authors.add(author3);
		
		bookDefinition.setAuthors(authors);
		bookDefinition.setIsAuthorDisplayVertical(true);
		
		researchAssistancePageFilter = new FrontMatterResearchAssistancePageFilter(bookDefinition);
		researchAssistancePageFilter.setParent(saxParser.getXMLReader());
		
		Properties props = OutputPropertiesFactory.getDefaultMethodProperties(Method.XHTML);
		props.setProperty("omit-xml-declaration", "yes");
		serializer = SerializerFactory.getSerializer(props);
	}
	
	@After
	public void tearDown() throws Exception
	{
		serializer = null;
		researchAssistancePageFilter = null;
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
			
			researchAssistancePageFilter.setContentHandler(serializer.asContentHandler());
			researchAssistancePageFilter.parse(new InputSource(input));
			
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
	public void testFrontMatterPlaceholder_researchAssistancePageAnchor() throws SAXException
	{	
		String xmlTestStr = "<test><frontMatterPlaceholder_researchAssistancePageAnchor/></test>";
		String expectedResult = "<test><a name=\"" + FrontMatterFileName.RESEARCH_ASSISTANCE + 
				FrontMatterFileName.ANCHOR + "\"/></test>";
		
		testHelper(xmlTestStr, expectedResult);
	}
	
	@Test
	public void testFrontMatterPlaceholder_researchAssistanceEmail() throws SAXException
	{	
		String xmlTestStr = "<test><frontMatterPlaceholder_researchAssistanceEmail/></test>";
		String expectedResult = "<test><a class=\"additional_info_email\" " +
				"href=\"mailto:west.ebooksuggestions@thomsonreuters.com?subject=" +
				"eBook Questions and Suggestions for Display TEST Title\">" +
				"west.ebooksuggestions@thomsonreuters.com</a></test>";
		
		testHelper(xmlTestStr, expectedResult);
	}
}
