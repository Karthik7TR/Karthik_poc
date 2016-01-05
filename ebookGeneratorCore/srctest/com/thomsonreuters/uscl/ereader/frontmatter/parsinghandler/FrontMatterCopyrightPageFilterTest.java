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
import com.thomsonreuters.uscl.ereader.frontmatter.parsinghandler.FrontMatterCopyrightPageFilter;

/**
 * Test various FrontMatterTitlePageFilter data scenarios.
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public class FrontMatterCopyrightPageFilterTest {
	private FrontMatterCopyrightPageFilter copyrightPageFilter;
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
		bookDefinition.setCopyright("&#169; 2012 Thomson Reuters \r\n Test Copyright");
		bookDefinition.setCopyrightPageText("Copyright is not claimed as to any part of the original work prepared " +
				"by a United States Government officer or employee as part of that person's official duties." +
				"\r\nTest paragraph 2.");
		bookDefinition.setIsbn("978-0-314-93983-8");
		bookDefinition.setAdditionalTrademarkInfo("McKINNEY'S is registerd in the U.S. Patent and Trademark Office.\r\n" +
				"McKINNEY'S NEW YORK CIVIL PRACTICE LAW AND RULES\r\n" +
				"is a trademark of West Publishing Corporation.");
		
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
		
		copyrightPageFilter = new FrontMatterCopyrightPageFilter(bookDefinition);
		copyrightPageFilter.setParent(saxParser.getXMLReader());
		
		Properties props = OutputPropertiesFactory.getDefaultMethodProperties(Method.XHTML);
		props.setProperty("omit-xml-declaration", "yes");
		serializer = SerializerFactory.getSerializer(props);
	}
	
	@After
	public void tearDown() throws Exception
	{
		serializer = null;
		copyrightPageFilter = null;
	}
	
	@Test
	public void testFrontMatterPlaceholder_TOCHeadingAnchor() throws SAXException
	{	
		String xmlTestStr = "<test><frontMatterPlaceholder_TOCHeadingAnchor/></test>";
		String expectedResult = "<test><a name=\"" + FrontMatterFileName.PUBLISHING_INFORMATION + 
				FrontMatterFileName.ANCHOR + "\"/></test>";
		
		testHelper(xmlTestStr, expectedResult);
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
			
			copyrightPageFilter.setContentHandler(serializer.asContentHandler());
			copyrightPageFilter.parse(new InputSource(input));
			
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
	public void testFrontMatterPlaceholder_CopyrightPageAnchor() throws SAXException
	{	
		String xmlTestStr = "<test><frontMatterPlaceholder_CopyrightPageAnchor/></test>";
		String expectedResult = "<test><a name=\"" + FrontMatterFileName.COPYRIGHT + 
				FrontMatterFileName.ANCHOR + "\"/></test>";
		
		testHelper(xmlTestStr, expectedResult);
	}
	
	@Test
	public void testFrontMatterPlaceholder_copyright() throws SAXException
	{	
		String xmlTestStr = "<test><p><frontMatterPlaceholder_copyright/></p></test>";
		String expectedResult = "<test><p>&amp;#169; 2012 Thomson Reuters </p><p> Test Copyright</p></test>";
		
		testHelper(xmlTestStr, expectedResult);
	}
	
	@Test
	public void testFrontMatterPlaceholder_copyrightPageText() throws SAXException
	{	
		String xmlTestStr = "<test><p><frontMatterPlaceholder_copyrightPageText/></p></test>";
		String expectedResult = "<test><p>Copyright is not claimed as to any part of the original work prepared " +
				"by a United States Government officer or employee as part of that person's official duties." +
				"</p><p>Test paragraph 2.</p></test>";
		
		testHelper(xmlTestStr, expectedResult);
	}
	
	@Test
	public void testFrontMatterPlaceholder_copyrightISBN() throws SAXException
	{	
		String xmlTestStr = "<test><frontMatterPlaceholder_copyrightISBN/></test>";
		String expectedResult = "<test>978-0-314-93983-8</test>";
		
		testHelper(xmlTestStr, expectedResult);
	}
	
	@Test
	public void testFrontMatterPlaceholder_copyrightTrademark() throws SAXException
	{	
		String xmlTestStr = "<test><p><frontMatterPlaceholder_copyrightTrademarkLine/></p><p>West's and Westlaw are registered in the U.S. Patent and Trademark Office.</p></test>";
		String expectedResult = "<test><p>McKINNEY'S is registerd in the U.S. Patent and Trademark Office.</p><p>McKINNEY'S NEW YORK CIVIL PRACTICE LAW AND RULES</p><p>is a trademark of West Publishing Corporation.</p><p>West's and Westlaw are registered in the U.S. Patent and Trademark Office.</p></test>";
		
		testHelper(xmlTestStr, expectedResult);
	}
}
