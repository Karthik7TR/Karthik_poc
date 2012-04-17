/*
* Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.frontmatter.parsinghandler;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;

import com.thomsonreuters.uscl.ereader.FrontMatterFileName;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;

/**
 * This filter transforms the Copyright Page Template by filling in any template placeholders
 * with the values retrieved from the eBook definition tables in the database.
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public class FrontMatterCopyrightPageFilter extends XMLFilterImpl
{
	/** Names of all the placeholder tags this filter handles */
	private static final String COPYRIGHT_PAGE_ANCHOR_TAG = "frontMatterPlaceholder_CopyrightPageAnchor";
	private static final String COPYRIGHT_TAG = "frontMatterPlaceholder_copyright";
	private static final String COPYRIGHT_PAGE_TEXT_TAG = "frontMatterPlaceholder_copyrightPageText";
	private static final String ISBN_TAG = "frontMatterPlaceholder_copyrightISBN";
	private static final String COPYRIGHT_TRADEMARK_TAG = "frontMatterPlaceholder_copyrightTrademarkLine";
	
	/** Defines the HTML tags that will be created by the filter */
	private static final String HTML_PARAGRAPH_TAG = "p";
	private static final String HTML_ANCHOR_TAG = "a";
	
	private static final String HTML_TAG_NAME_ATTRIBUTE = "name";
	
	private static final String CDATA = "CDATA";
	
	private static final boolean MULTI_LINE_FIELD = true;
	private static final boolean SINGLE_LINE_FIELD = false;
	
	private BookDefinition bookDefinition;
	
	public FrontMatterCopyrightPageFilter(BookDefinition bookDefinition)
	{
		this.bookDefinition = bookDefinition;
	}
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException
	{
		if (qName.equalsIgnoreCase(COPYRIGHT_PAGE_ANCHOR_TAG))
		{
			AttributesImpl newAtts = new AttributesImpl();
			newAtts.addAttribute(uri, HTML_TAG_NAME_ATTRIBUTE, HTML_TAG_NAME_ATTRIBUTE, CDATA, 
					FrontMatterFileName.COPYRIGHT + FrontMatterFileName.ANCHOR);
			super.startElement(uri, HTML_ANCHOR_TAG, HTML_ANCHOR_TAG, newAtts);
		}
		else if (qName.equalsIgnoreCase(COPYRIGHT_TAG))
		{
			printText(bookDefinition.getCopyright(), MULTI_LINE_FIELD);
		}
		else if (qName.equalsIgnoreCase(COPYRIGHT_PAGE_TEXT_TAG))
		{
			printText(bookDefinition.getCopyrightPageText(), MULTI_LINE_FIELD);
		}
		else if (qName.equalsIgnoreCase(ISBN_TAG))
		{
			printText(bookDefinition.getIsbn(), SINGLE_LINE_FIELD);
		}
		else if (qName.equalsIgnoreCase(COPYRIGHT_TRADEMARK_TAG))
		{
			printText(bookDefinition.getAdditionalTrademarkInfo(), MULTI_LINE_FIELD);
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
		if (qName.equalsIgnoreCase(COPYRIGHT_PAGE_ANCHOR_TAG))
		{
			super.endElement(uri, HTML_ANCHOR_TAG, HTML_ANCHOR_TAG);
		}
		else if (qName.equalsIgnoreCase(COPYRIGHT_TAG) || qName.equalsIgnoreCase(COPYRIGHT_PAGE_TEXT_TAG) 
				|| qName.equalsIgnoreCase(ISBN_TAG) || qName.equalsIgnoreCase(COPYRIGHT_TRADEMARK_TAG))
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
				String[] lines = text.split("\\\r\\\n");
				for (int i = 0; i < lines.length; i++)
				{
					if (i == lines.length - 1)
					{
						super.characters(lines[i].toCharArray(), 0, lines[i].length());
					}
					else
					{
						super.characters(lines[i].toCharArray(), 0, lines[i].length());
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
}
