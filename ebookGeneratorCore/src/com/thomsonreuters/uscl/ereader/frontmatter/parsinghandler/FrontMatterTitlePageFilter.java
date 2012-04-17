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
import com.thomsonreuters.uscl.ereader.core.book.domain.Author;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookName;

/**
 * This filter transforms the Title Page Template by filling in any template placeholders
 * with the values retrieved from the eBook definition tables in the database.
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public class FrontMatterTitlePageFilter extends XMLFilterImpl
{
	/** Names of all the placeholder tags this filter handles */
	private static final String TOC_HEADING_ANCHOR_TAG = "frontMatterPlaceholder_TOCHeadingAnchor";
	private static final String TITLE_PAGE_ANCHOR_TAG = "frontMatterPlaceholder_TitlePageAnchor";
	private static final String BOOK_NAME_TAG = "frontMatterPlaceholder_bookname";
	private static final String BOOK_NAME2_TAG = "frontMatterPlaceholder_bookname2";
	private static final String BOOK_NAME3_TAG = "frontMatterPlaceholder_bookname3";
	private static final String CURRENCY_TAG = "frontMatterPlaceholder_currency";
	private static final String AUTHORS_TAG = "frontMatterPlaceholder_authors";
	
	/** Defines the HTML tags that will be created by the filter */
	private static final String HTML_DIV_TAG = "div";
	private static final String HTML_PARAGRAPH_TAG = "p";
	private static final String HTML_ANCHOR_TAG = "a";
	
	private static final String HTML_TAG_NAME_ATTRIBUTE = "name";
	private static final String HTML_TAG_CLASS_ATTRIBUTE = "class";
	
	private static final String CDATA = "CDATA";
	
	private static final boolean MULTI_LINE_FIELD = true;
	private static final boolean SINGLE_LINE_FIELD = false;
	
	private BookDefinition bookDefinition;
	
	public FrontMatterTitlePageFilter(BookDefinition bookDefinition)
	{
		this.bookDefinition = bookDefinition;
	}
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException
	{
		if (qName.equalsIgnoreCase(TOC_HEADING_ANCHOR_TAG))
		{
			AttributesImpl newAtts = new AttributesImpl();
			newAtts.addAttribute(uri, HTML_TAG_NAME_ATTRIBUTE, HTML_TAG_NAME_ATTRIBUTE, CDATA, 
					FrontMatterFileName.PUBLISHING_INFORMATION + FrontMatterFileName.ANCHOR);
			super.startElement(uri, HTML_ANCHOR_TAG, HTML_ANCHOR_TAG, newAtts);
		}
		else if (qName.equalsIgnoreCase(TITLE_PAGE_ANCHOR_TAG))
		{
			AttributesImpl newAtts = new AttributesImpl();
			newAtts.addAttribute(uri, HTML_TAG_NAME_ATTRIBUTE, HTML_TAG_NAME_ATTRIBUTE, CDATA, 
					FrontMatterFileName.FRONT_MATTER_TITLE + FrontMatterFileName.ANCHOR);
			super.startElement(uri, HTML_ANCHOR_TAG, HTML_ANCHOR_TAG, newAtts);
		}
		else if (qName.equalsIgnoreCase(BOOK_NAME_TAG))
		{
			EbookName name = bookDefinition.getEbookNames().get(0);
			printText(name.getBookNameText(), MULTI_LINE_FIELD);

		}
		else if (qName.equalsIgnoreCase(BOOK_NAME2_TAG))
		{
			if (bookDefinition.getEbookNames().size() > 1)
			{
				EbookName name = bookDefinition.getEbookNames().get(1);
				printText(name.getBookNameText(), MULTI_LINE_FIELD);
			}
		}
		else if (qName.equalsIgnoreCase(BOOK_NAME3_TAG))
		{
			if (bookDefinition.getEbookNames().size() > 2)
			{
				EbookName name = bookDefinition.getEbookNames().get(2);
				printText(name.getBookNameText(), MULTI_LINE_FIELD);
			}
			
		}
		else if (qName.equalsIgnoreCase(CURRENCY_TAG))
		{
			printText(bookDefinition.getCurrency(),MULTI_LINE_FIELD);
		}
		else if (qName.equalsIgnoreCase(AUTHORS_TAG))
		{
			createAuthorSection();
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
		if (qName.equalsIgnoreCase(TOC_HEADING_ANCHOR_TAG))
		{
			super.endElement(uri, HTML_ANCHOR_TAG, HTML_ANCHOR_TAG);
		}
		else if (qName.equalsIgnoreCase(TITLE_PAGE_ANCHOR_TAG))
		{
			super.endElement(uri, HTML_ANCHOR_TAG, HTML_ANCHOR_TAG);
		}
		else if (qName.equalsIgnoreCase(BOOK_NAME_TAG) || qName.equalsIgnoreCase(BOOK_NAME2_TAG)
				|| qName.equalsIgnoreCase(BOOK_NAME3_TAG)
				|| qName.equalsIgnoreCase(CURRENCY_TAG) || qName.equalsIgnoreCase(AUTHORS_TAG))
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
	
	private void createAuthorSection() throws SAXException
	{
		boolean firstAuthor = true;
		boolean isAuthorDisplayVertical = bookDefinition.isAuthorDisplayVertical();
		
		if (isAuthorDisplayVertical)
		{
			for (Author author : bookDefinition.getAuthors())
			{
				AttributesImpl newAtts = new AttributesImpl();
				if (firstAuthor)
				{
					newAtts.addAttribute("", HTML_TAG_CLASS_ATTRIBUTE, HTML_TAG_CLASS_ATTRIBUTE, CDATA, "author1");
					firstAuthor = false;
				}
				else
				{
					newAtts.addAttribute("", HTML_TAG_CLASS_ATTRIBUTE, HTML_TAG_CLASS_ATTRIBUTE, CDATA, "authorNext");
				}
				super.startElement("", HTML_DIV_TAG, HTML_DIV_TAG, newAtts);
				printText(author.getFullName(), SINGLE_LINE_FIELD);
				super.endElement("", HTML_DIV_TAG, HTML_DIV_TAG);
				newAtts = new AttributesImpl();
				newAtts.addAttribute("", HTML_TAG_CLASS_ATTRIBUTE, HTML_TAG_CLASS_ATTRIBUTE, CDATA, "authorInfo");
				super.startElement("", HTML_DIV_TAG, HTML_DIV_TAG, newAtts);
				newAtts = new AttributesImpl();
				super.startElement("", HTML_PARAGRAPH_TAG, HTML_PARAGRAPH_TAG, newAtts);
				printText(author.getAuthorAddlText(), MULTI_LINE_FIELD);
				newAtts = new AttributesImpl();
				super.endElement("", HTML_PARAGRAPH_TAG, HTML_PARAGRAPH_TAG);
				super.endElement("", HTML_DIV_TAG, HTML_DIV_TAG);
			}
		}
		else
		{
			//TODO: Update based on horizontal Author display spec
			AttributesImpl newAtts = new AttributesImpl();
			newAtts.addAttribute("", HTML_TAG_CLASS_ATTRIBUTE, HTML_TAG_CLASS_ATTRIBUTE, CDATA, "author1");
			super.startElement("", HTML_DIV_TAG, HTML_DIV_TAG, newAtts);
			int authorNum = 0;
			for (Author author : bookDefinition.getAuthors())
			{
				authorNum++;
				printText(author.getFullName(), SINGLE_LINE_FIELD);
				if (authorNum == bookDefinition.getAuthors().size() - 1)
				{
					printText(" and ", SINGLE_LINE_FIELD);
				}
				else if (authorNum != bookDefinition.getAuthors().size())
				{
					printText(", ", SINGLE_LINE_FIELD);
				}
			}
			super.endElement("", HTML_DIV_TAG, HTML_DIV_TAG);
		}
	}
}
