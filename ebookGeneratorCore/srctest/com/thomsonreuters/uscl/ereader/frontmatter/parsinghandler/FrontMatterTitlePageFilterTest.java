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
import com.thomsonreuters.uscl.ereader.frontmatter.parsinghandler.FrontMatterTitlePageFilter;

/**
 * Test various FrontMatterTitlePageFilter data scenarios.
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public class FrontMatterTitlePageFilterTest {
	private FrontMatterTitlePageFilter titlePageFilter;
	private BookDefinition bookDefinition;
	private Serializer serializer;
	
	@Before
	public void setUp() throws Exception {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setNamespaceAware(true);
		SAXParser saxParser = factory.newSAXParser();
		
		bookDefinition = new BookDefinition();
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
		
		titlePageFilter = new FrontMatterTitlePageFilter(bookDefinition);
		titlePageFilter.setParent(saxParser.getXMLReader());
		
		Properties props = OutputPropertiesFactory.getDefaultMethodProperties(Method.XHTML);
		props.setProperty("omit-xml-declaration", "yes");
		serializer = SerializerFactory.getSerializer(props);
	}
	
	@After
	public void tearDown() throws Exception
	{
		serializer = null;
		titlePageFilter = null;
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
			
			titlePageFilter.setContentHandler(serializer.asContentHandler());
			titlePageFilter.parse(new InputSource(input));
			
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
	public void testFrontMatterPlaceholder_TOCHeadingAnchor() throws SAXException
	{	
		String xmlTestStr = "<test><frontMatterPlaceholder_TOCHeadingAnchor/></test>";
		String expectedResult = "<test><a name=\"" + FrontMatterFileName.PUBLISHING_INFORMATION + 
				FrontMatterFileName.ANCHOR + "\"/></test>";
		
		testHelper(xmlTestStr, expectedResult);
	}
	
	@Test
	public void testFrontMatterPlaceholder_TitlePageAnchor() throws SAXException
	{	
		String xmlTestStr = "<test><frontMatterPlaceholder_TitlePageAnchor/></test>";
		String expectedResult = "<test><a name=\"" + FrontMatterFileName.FRONT_MATTER_TITLE + 
				FrontMatterFileName.ANCHOR + "\"/></test>";
		
		testHelper(xmlTestStr, expectedResult);
	}
	
	@Test
	public void testFrontMatterPlaceholder_bookname() throws SAXException
	{	
		String xmlTestStr = "<test><frontMatterPlaceholder_bookname/></test>";
		String expectedResult = "<test>TEST Title</test>";
		
		testHelper(xmlTestStr, expectedResult);
	}
	
	@Test
	public void testFrontMatterPlaceholder_bookname2() throws SAXException
	{	
		String xmlTestStr = "<test><frontMatterPlaceholder_bookname2/></test>";
		String expectedResult = "<test>TEST Edition</test>";
		
		testHelper(xmlTestStr, expectedResult);
	}
	
	@Test
	public void testFrontMatterPlaceholder_currency() throws SAXException
	{	
		String xmlTestStr = "<test><frontMatterPlaceholder_currency/></test>";
		String expectedResult = "<test>Currency Test</test>";
		
		testHelper(xmlTestStr, expectedResult);
	}
	
	@Test
	public void testFrontMatterPlaceholder_authorsVertical() throws SAXException
	{	
		String xmlTestStr = "<test><frontMatterPlaceholder_authors/></test>";
		String expectedResult = "<test><div class=\"author1\">John Tester1</div><div class=\"authorInfo\">" +
				"<p>Pirate, Arrrrrrrrrrr</p></div><div class=\"authorNext\">Joel Tester2</div>" +
				"<div class=\"authorInfo\"><p>Also a </p><p>Pirate, Arrrrrrrrrrr</p></div>" +
				"<div class=\"authorNext\">Ender Tester3</div><div class=\"authorInfo\"><p>Another </p><p>Pirate, Arrrrrrrrrrr</p></div></test>";
		
		testHelper(xmlTestStr, expectedResult);
	}
	
	@Test
	public void testFrontMatterPlaceholder_authorsHorizontal() throws Exception
	{	
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setNamespaceAware(true);
		SAXParser saxParser = factory.newSAXParser();
		
		bookDefinition.setIsAuthorDisplayVertical(false);
				
		titlePageFilter = new FrontMatterTitlePageFilter(bookDefinition);
		titlePageFilter.setParent(saxParser.getXMLReader());
		
		String xmlTestStr = "<test><frontMatterPlaceholder_authors/></test>";
		String expectedResult = "<test><div class=\"author1\">John Tester1, Joel Tester2 and Ender Tester3</div></test>";
		
		testHelper(xmlTestStr, expectedResult);
	}
}
