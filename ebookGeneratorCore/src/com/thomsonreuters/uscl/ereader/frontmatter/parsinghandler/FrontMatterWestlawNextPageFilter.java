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

/**
 * This filter transforms the WestlawNext Page Template by filling in any template placeholders
 * with the values retrieved from the eBook definition tables in the database.
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public class FrontMatterWestlawNextPageFilter extends XMLFilterImpl
{
	/** Names of all the placeholder tags this filter handles */
	private static final String WESTLAW_NEXT_PAGE_ANCHOR_TAG = "frontMatterPlaceholder_WestlawNextPageAnchor";
	
	/** Defines the HTML tags that will be created by the filter */
	private static final String HTML_ANCHOR_TAG = "a";
	
	private static final String HTML_TAG_NAME_ATTRIBUTE = "name";
	
	private static final String CDATA = "CDATA";
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException
	{
		if (qName.equalsIgnoreCase(WESTLAW_NEXT_PAGE_ANCHOR_TAG))
		{
			AttributesImpl newAtts = new AttributesImpl();
			newAtts.addAttribute(uri, HTML_TAG_NAME_ATTRIBUTE, HTML_TAG_NAME_ATTRIBUTE, CDATA, 
					FrontMatterFileName.WESTLAWNEXT + FrontMatterFileName.ANCHOR);
			super.startElement(uri, HTML_ANCHOR_TAG, HTML_ANCHOR_TAG, newAtts);
			super.characters(" ".toCharArray(), 0, 1);
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
		if (qName.equalsIgnoreCase(WESTLAW_NEXT_PAGE_ANCHOR_TAG))
		{
			super.endElement(uri, HTML_ANCHOR_TAG, HTML_ANCHOR_TAG);
		}
		else
		{
			super.endElement(uri, localName, qName);
		}
	}
}
