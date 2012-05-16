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
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;
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
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookName;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterPage;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterPdf;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterSection;
import com.thomsonreuters.uscl.ereader.frontmatter.parsinghandler.FrontMatterAdditionalFrontMatterPageFilter;

/**
 * Test various FrontMatterAdditionalFrontMatterPageFilter data scenarios.
 *
 * @author <a href="mailto:Kirsten.Gunn@thomsonreuters.com">Kirsten Gunn</a> u0076257
 */
public class FrontMatterAdditionalFrontMatterPageFilterTest {
	private FrontMatterAdditionalFrontMatterPageFilter frontMatterPageFilter;
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

		List<FrontMatterPage> frontMatterPageList = new ArrayList<FrontMatterPage>();
		


		//  One PDF with section text
		FrontMatterPage frontMatterPage4 = new FrontMatterPage();
		frontMatterPage4.setId((long) 40);
		
		List<FrontMatterSection> frontMatterSectionsList4 = new ArrayList<FrontMatterSection>();
		FrontMatterSection frontMatterSection4 = new FrontMatterSection();
		frontMatterSection4.setId((long) 41);
		frontMatterSection4.setSectionText("Jack Sparrow was here!\r\nElizabeth Swan was too!" );
		frontMatterSection4.setSectionHeading("Pirate graffiti");
		frontMatterSection4.setSequenceNum(1);
		// do not set a section heading
		
		List<FrontMatterPdf> frontMatterPdfList4 = new ArrayList<FrontMatterPdf>();
		FrontMatterPdf frontMatterPdfs4 = new FrontMatterPdf();
		frontMatterPdfs4.setId((long) 1);
		frontMatterPdfs4.setPdfFilename("pdfFilename4.pdf");
		frontMatterPdfs4.setPdfLinkText("pdfLinkText4");
		frontMatterPdfs4.setSequenceNum(1);

		frontMatterPdfList4.add(frontMatterPdfs4);
		
		frontMatterSectionsList4.add(frontMatterSection4);
		frontMatterSection4.setPdfs(frontMatterPdfList4);

		frontMatterPage4.setFrontMatterSections(frontMatterSectionsList4);
		frontMatterPage4.setPageTocLabel("Pirate, Arrrrrrrrrrr Page TOC Label");
		frontMatterPage4.setSequenceNum(2);
		frontMatterPageList.add(frontMatterPage4);

		bookDefinition.setFrontMatterPages(frontMatterPageList);

		
		frontMatterPageFilter = new FrontMatterAdditionalFrontMatterPageFilter(bookDefinition, bookDefinition.getFrontMatterPages().get(0).getId());
		frontMatterPageFilter.setParent(saxParser.getXMLReader());
		
		Properties props = OutputPropertiesFactory.getDefaultMethodProperties(Method.XHTML);
		props.setProperty("omit-xml-declaration", "yes");
		serializer = SerializerFactory.getSerializer(props);
	}
	
	@After
	public void tearDown() throws Exception
	{
		serializer = null;
		frontMatterPageFilter = null;
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
			
			frontMatterPageFilter.setContentHandler(serializer.asContentHandler());
			frontMatterPageFilter.parse(new InputSource(input));
			
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
	public void testFrontMatterAdditionalFrontMatterAnchor() throws SAXException
	{	
		String xmlTestStr = "<test><frontMatterPlaceholder_AdditionalPageAnchor/></test>";
		String expectedResult = "<test><a name=\"" + FrontMatterFileName.ADDITIONAL_FRONT_MATTER + "40" +
				FrontMatterFileName.ANCHOR + "\"> </a></test>";
		
		testHelper(xmlTestStr, expectedResult);
	}
	
	@Test
	public void testFrontMatterPlaceholder_pageTitleLabel() throws SAXException, ParserConfigurationException
	{	

		
		String xmlTestStr = "<test><frontMatterPlaceholder_additionFrontMatterTitle/></test>";
		 List<FrontMatterPage> fmps = bookDefinition.getFrontMatterPages();
		 
		String expectedResult = "<test>" +fmps.get(0).getPageTocLabel() + "</test>";
		
		testHelper(xmlTestStr, expectedResult);
		
	}
	
	@Test
	public void testFrontMatterPlaceholder_pageTitleHeader() throws SAXException, ParserConfigurationException
	{	

		String xmlTestStr = "<test><frontMatterPlaceholder_additionFrontMatterTitle/></test>";
		 List<FrontMatterPage> fmps = bookDefinition.getFrontMatterPages();
		 FrontMatterPage frontMatterPage4 = fmps.get(0);
			frontMatterPage4.setPageHeadingLabel("Pirate, Arrrrrrrrrrr Page Heading");

		String expectedResult = "<test>" +fmps.get(0).getPageHeadingLabel() + "</test>";
		
		testHelper(xmlTestStr, expectedResult);
		
	}
	
	@Test
	public void testFrontMatterPlaceholder_SectionsWithPDF() throws SAXException, ParserConfigurationException
	{	

		String xmlTestStr = "<frontMatterPlaceholder_sections/>";

		String expectedResult = "<div class=\"section_heading\">Pirate graffiti</div>" +
								"<div class=\"section_text\"><p>Jack Sparrow was here!</p>" +
								"<p>Elizabeth Swan was too!</p></div>" +
								"<div class=\"section_pdf\"><br/><a class=\"section_pdf_hyperlink\" href=\"er:#pdfFilename4\">pdfLinkText4</a><br/></div>";
		
		testHelper(xmlTestStr, expectedResult);
		
	}
	
	@Test
	public void testFrontMatterPlaceholder_SectionsNoPdf() throws SAXException, ParserConfigurationException
	{	

		String xmlTestStr = "<frontMatterPlaceholder_sections/>";

		String expectedResult = "<div class=\"section_heading\">Pirate graffiti</div>" +
		"<div class=\"section_text\"><p>Jack Sparrow was here!</p>" +
		"<p>Elizabeth Swan was too!</p></div>";
		
		 List<FrontMatterPage> fmps = bookDefinition.getFrontMatterPages();
		 FrontMatterPage frontMatterPage4 = fmps.get(0);
		 for (FrontMatterSection fms : frontMatterPage4.getFrontMatterSections())
		 {
			fms.setPdfs(null);
		 }
			testHelper(xmlTestStr, expectedResult);
			
	}
	
	@Test
	public void testFrontMatterPlaceholder_SectionsTwoPdfs() throws SAXException, ParserConfigurationException
	{	

		String xmlTestStr = "<frontMatterPlaceholder_sections/>";

		String expectedResult = "<div class=\"section_heading\">Pirate graffiti</div>" +
		"<div class=\"section_text\"><p>Jack Sparrow was here!</p>" +
		"<p>Elizabeth Swan was too!</p></div>"+
		"<div class=\"section_pdf\"><br/><a class=\"section_pdf_hyperlink\" href=\"er:#pdfFilename4\">pdfLinkText4</a><br/>"+
		"<br/><a class=\"section_pdf_hyperlink\" href=\"er:#pdfFilename5\">pdfLinkText5</a><br/></div>";

		 List<FrontMatterPage> fmps = bookDefinition.getFrontMatterPages();
		 FrontMatterPage frontMatterPage4 = fmps.get(0);
		 for (FrontMatterSection fms : frontMatterPage4.getFrontMatterSections())
		 {
			 List<FrontMatterPdf> frontMatterPdfList4 = new ArrayList<FrontMatterPdf>();
			 FrontMatterPdf frontMatterPdfs4 = new FrontMatterPdf();
			 frontMatterPdfs4.setId((long) 1);
			 frontMatterPdfs4.setPdfFilename("pdfFilename4.pdf");
			 frontMatterPdfs4.setPdfLinkText("pdfLinkText4");
			 frontMatterPdfs4.setSequenceNum(1);
			 frontMatterPdfList4.add(frontMatterPdfs4);
			
			 frontMatterPdfs4 = new FrontMatterPdf();
			 frontMatterPdfs4.setId((long) 2);
			 frontMatterPdfs4.setPdfFilename("pdfFilename5.pdf");
			 frontMatterPdfs4.setPdfLinkText("pdfLinkText5");
			 frontMatterPdfs4.setSequenceNum(2);
			 frontMatterPdfList4.add(frontMatterPdfs4);
			
			 fms.setPdfs(frontMatterPdfList4);
		 }
			
		 testHelper(xmlTestStr, expectedResult);
			
	}

	@Test
	public void testFrontMatterPlaceholder_NoSectionsOnlyPdf() throws SAXException, ParserConfigurationException
	{	

		String xmlTestStr = "<frontMatterPlaceholder_sections/>";

		String expectedResult = "<div class=\"section_pdf\"><br/><a class=\"section_pdf_hyperlink\" href=\"er:#pdfFilename4\">pdfLinkText4</a><br/></div>";
		
		 List<FrontMatterPage> fmps = bookDefinition.getFrontMatterPages();
		 FrontMatterPage frontMatterPage4 = fmps.get(0);
		 for (FrontMatterSection fms : frontMatterPage4.getFrontMatterSections())
		 {
			fms.setSectionText(null);
			fms.setSectionHeading(null);
		 }
		 
		testHelper(xmlTestStr, expectedResult);
		
	}
	
	@Test
	public void testFrontMatterPlaceholder_MultipleSectionsMultiReturn() throws SAXException, ParserConfigurationException
	{	

		String xmlTestStr = "<frontMatterPlaceholder_sections/>";

		String expectedResult = "<div class=\"section_heading\">Pirate graffiti</div>" +
		"<div class=\"section_text\"><p>Jack Sparrow was here!</p>" +
		"<p>Elizabeth Swan was too!</p></div>" +
		"<div class=\"section_pdf\"><br/><a class=\"section_pdf_hyperlink\" href=\"er:#pdfFilename4\">pdfLinkText4</a><br/></div>" +
		"<div class=\"section_heading\">Pirate Stories</div>"+
		"<div class=\"section_text\"><p>BlackBeard's Ghost!</p><p/><p>Scary</p></div>";
		
		List<FrontMatterPage> fmps = bookDefinition.getFrontMatterPages();
		FrontMatterPage frontMatterPage4 = fmps.get(0);
		Collection<FrontMatterSection> frontMatterSections4 = frontMatterPage4.getFrontMatterSections();
		FrontMatterSection frontMatterSectionNew = new FrontMatterSection();
		frontMatterSectionNew.setId((long) 42);
		// add another section and extra newline 
		frontMatterSectionNew.setSectionText("BlackBeard's Ghost!\r\n\r\nScary" );
		frontMatterSectionNew.setSectionHeading("Pirate Stories");
		frontMatterSectionNew.setSequenceNum(2);

		frontMatterSections4.add(frontMatterSectionNew);
		frontMatterPage4.setFrontMatterSections(frontMatterSections4);
		 
		testHelper(xmlTestStr, expectedResult);
		
	}
}
