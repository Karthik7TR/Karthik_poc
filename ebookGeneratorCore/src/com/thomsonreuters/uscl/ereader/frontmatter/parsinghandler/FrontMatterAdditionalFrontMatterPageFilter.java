/*
* Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.frontmatter.parsinghandler;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;

import com.thomsonreuters.uscl.ereader.FrontMatterFileName;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterPage;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterPdf;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterSection;
import com.thomsonreuters.uscl.ereader.frontmatter.exception.EBookFrontMatterGenerationException;

/**
 * This filter transforms the Title Page Template by filling in any template placeholders
 * with the values retrieved from the eBook definition tables in the database.
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public class FrontMatterAdditionalFrontMatterPageFilter extends XMLFilterImpl
{
	private static final Logger LOG = Logger.getLogger(FrontMatterAdditionalFrontMatterPageFilter.class);
	
	/** Names of all the placeholder tags this filter handles */
	private static final String ADDITIONAL_HEADING_ANCHOR_TAG = "frontMatterPlaceholder_AdditionalPageAnchor";
	private static final String ADDITIONAL_TITLE_PAGE_TAG = "frontMatterPlaceholder_additionFrontMatterTitle";
	private static final String SECTIONS_TAG = "frontMatterPlaceholder_sections";
	
	/** Defines the HTML tags that will be created by the filter */
	private static final String HTML_DIV_TAG = "div";
	private static final String HTML_PARAGRAPH_TAG = "p";
	private static final String HTML_ANCHOR_TAG = "a";
	
	private static final String HTML_TAG_NAME_ATTRIBUTE = "name";
	private static final String HTML_TAG_CLASS_ATTRIBUTE = "class";
	private static final String HTML_TAG_HREF_ATTRIBUTE = "href";
	private static final String HTML_TAG_BREAK_ATTRIBUTE = "br";
	
	private static final String CDATA = "CDATA";
	
	private static final boolean MULTI_LINE_FIELD = true;
	private static final boolean SINGLE_LINE_FIELD = false;
	
	private FrontMatterPage frontMatterPage;
	private Long FRONT_MATTER_PAGE_ID;
	
	public FrontMatterAdditionalFrontMatterPageFilter(BookDefinition bookDefinition, Long FRONT_MATTER_PAGE_ID)
		throws EBookFrontMatterGenerationException
	{
		this.FRONT_MATTER_PAGE_ID = FRONT_MATTER_PAGE_ID;
		for (FrontMatterPage fmp : bookDefinition.getFrontMatterPages())
		{	
			if (fmp.getId().equals(FRONT_MATTER_PAGE_ID))
			{
				this.frontMatterPage = fmp;
				break;
			}
		}
		
		if (this.frontMatterPage == null)
		{
			String message = "Could not retrieve additional front matter page with id: " 
					+ FRONT_MATTER_PAGE_ID;
			LOG.error(message);
			throw new EBookFrontMatterGenerationException(message);
		}
	}
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException
	{
		if (qName.equalsIgnoreCase(ADDITIONAL_HEADING_ANCHOR_TAG))
		{
			AttributesImpl newAtts = new AttributesImpl();
			newAtts.addAttribute(uri, HTML_TAG_NAME_ATTRIBUTE, HTML_TAG_NAME_ATTRIBUTE, CDATA, 
					FrontMatterFileName.ADDITIONAL_FRONT_MATTER + FRONT_MATTER_PAGE_ID + FrontMatterFileName.ANCHOR);
			super.startElement(uri, HTML_ANCHOR_TAG, HTML_ANCHOR_TAG, newAtts);
			printText(" ", SINGLE_LINE_FIELD);
		}
		else if (qName.equalsIgnoreCase(ADDITIONAL_TITLE_PAGE_TAG))
		{
			if (frontMatterPage.getPageHeadingLabel() == null)
			{
				printText(frontMatterPage.getPageTocLabel(), SINGLE_LINE_FIELD);
			}
			else
			{
				printText(frontMatterPage.getPageHeadingLabel(), SINGLE_LINE_FIELD);
			}
		}

		else if (qName.equalsIgnoreCase(SECTIONS_TAG))
		{
			createSections();
		}
		else
		{
			super.startElement(uri, localName, qName, atts);
		}
	}
	
	@Override
	public void characters(char buf[], int offset, int len) throws SAXException
	{
		super.characters(buf, offset, len);
	}
	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException
	{
		if (qName.equalsIgnoreCase(ADDITIONAL_HEADING_ANCHOR_TAG))
		{
			super.endElement(uri, HTML_ANCHOR_TAG, HTML_ANCHOR_TAG);
		}
		else if (qName.equalsIgnoreCase(ADDITIONAL_TITLE_PAGE_TAG) || qName.equalsIgnoreCase(SECTIONS_TAG))
		{
			//Remove the placeholder tag
		}
		else
		{
			super.endElement(uri, localName, qName);
		}
	}
	
	private void printText(String text, boolean isMultiLineField) throws SAXException
	{
		if (text != null)
		{
			if (isMultiLineField)
			{
	            String[] lines = text.split("\\\r\\\n", -1);
	            for (int i = 0; i < lines.length; i++)
	            {
	                  if (i == lines.length - 1)
	                  {
	                        super.characters(lines[i].toCharArray(), 0, lines[i].length());
	                  }
	                  else
	                  {
	                        if (lines[i].trim().length() == 0)
	                        {
	                              super.characters("&nbsp;".toCharArray(), 0, lines[i].length());
	                        }
	                        else
	                        {
	                              super.characters(lines[i].toCharArray(), 0, lines[i].length());
	                        }
	
						AttributesImpl atts = new AttributesImpl();
						super.endElement("", HTML_PARAGRAPH_TAG, HTML_PARAGRAPH_TAG);
						super.startElement("", HTML_PARAGRAPH_TAG, HTML_PARAGRAPH_TAG, atts);
					}
				}
			}
			else
			{
				super.characters(text.toCharArray(), 0, text.length());
			}
		}
	}
	
	private void createSections() throws SAXException
	{
		for (FrontMatterSection fms : frontMatterPage.getFrontMatterSections())
			{
				AttributesImpl newAtts = new AttributesImpl();
				// Add Optional Section Heading 
				if (fms.getSectionHeading() != null)
				{
					newAtts.addAttribute("", HTML_TAG_CLASS_ATTRIBUTE, HTML_TAG_CLASS_ATTRIBUTE, CDATA, "section_heading");
					super.startElement("", HTML_DIV_TAG, HTML_DIV_TAG, newAtts);
					printText(fms.getSectionHeading(), SINGLE_LINE_FIELD);
					super.endElement("", HTML_DIV_TAG, HTML_DIV_TAG);
				}
				
				// Add Optional Section Text 
				if (fms.getSectionText() != null)
				{				
					newAtts = new AttributesImpl();
					newAtts.addAttribute("", HTML_TAG_CLASS_ATTRIBUTE, HTML_TAG_CLASS_ATTRIBUTE, CDATA, "section_text");
					super.startElement("", HTML_DIV_TAG, HTML_DIV_TAG, newAtts);
					newAtts = new AttributesImpl();
					super.startElement("", HTML_PARAGRAPH_TAG, HTML_PARAGRAPH_TAG, newAtts);
					printText(fms.getSectionText(), MULTI_LINE_FIELD);
					super.endElement("", HTML_PARAGRAPH_TAG, HTML_PARAGRAPH_TAG);
					super.endElement("", HTML_DIV_TAG, HTML_DIV_TAG);
				}

				//Add optional PDF Section
				if(fms.getPdfs() != null && fms.getPdfs().size() != 0)
				{
					newAtts.addAttribute("", HTML_TAG_CLASS_ATTRIBUTE, HTML_TAG_CLASS_ATTRIBUTE, CDATA, "section_pdf");
					super.startElement("", HTML_DIV_TAG, HTML_DIV_TAG, newAtts);
	
					for (FrontMatterPdf fmp : fms.getPdfs())
					{
						if(fmp.getSequenceNum() != 1)
						{
							newAtts = new AttributesImpl();
							super.startElement("", HTML_TAG_BREAK_ATTRIBUTE, HTML_TAG_BREAK_ATTRIBUTE, newAtts);
							super.endElement("", HTML_TAG_BREAK_ATTRIBUTE, HTML_TAG_BREAK_ATTRIBUTE);
						}
						
						newAtts = new AttributesImpl();
						newAtts.addAttribute("", HTML_TAG_CLASS_ATTRIBUTE, HTML_TAG_CLASS_ATTRIBUTE, CDATA, "section_pdf_hyperlink");
						// Add er:# and strip .pdf
						int startOfPDFExtension = fmp.getPdfFilename().lastIndexOf(".");
						if (startOfPDFExtension == -1)
						{
							startOfPDFExtension = fmp.getPdfFilename().length();
						}
						String pdfHREF = "er:#" + fmp.getPdfFilename().substring(0, startOfPDFExtension);
						newAtts.addAttribute("", HTML_TAG_HREF_ATTRIBUTE, HTML_TAG_HREF_ATTRIBUTE, CDATA, pdfHREF);
						super.startElement("", HTML_ANCHOR_TAG, HTML_ANCHOR_TAG, newAtts);
						printText(fmp.getPdfLinkText(), SINGLE_LINE_FIELD);
						super.endElement("", HTML_ANCHOR_TAG, HTML_ANCHOR_TAG); // end anchor tag for PDF
					}
					super.endElement("", HTML_DIV_TAG, HTML_DIV_TAG); /// end section_pdf
				}
			}
	}
}
