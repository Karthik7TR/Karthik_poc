/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.format.parsinghandler;

import org.apache.commons.lang.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * Filter that handles various Anchor "<a>" tags and transforms them as needed.
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public class HTMLAnchorFilter extends XMLFilterImpl {
	
	private boolean isImageLink = false;
	
	private int imgEncountered = 0;
	
	private long jobInstanceId;
	
	public void setjobInstanceId(long jobInstanceId)
	{
		this.jobInstanceId = jobInstanceId;
	}
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException
	{
		if (!isImageLink)
		{
			if (qName.equalsIgnoreCase("a"))
			{
				if (atts != null && atts.getValue("type") != null && atts.getValue("type").equalsIgnoreCase("image/jpeg"))
				{
					isImageLink = true;
					imgEncountered++;
					qName = "img";
					String imgGuid = "";
					String href = atts.getValue("href");
					if (!StringUtils.isEmpty(href))
					{
						imgGuid = href.substring(href.indexOf("/Blob/") + 6, href.indexOf(".jpg?"));
					}
		
					if (imgGuid.length() >= 32 && imgGuid.length() < 34)
					{
						AttributesImpl newAtts = new AttributesImpl();
						newAtts.addAttribute("", "", "alt", "CDATA", "Image " + imgEncountered + " within document.");
						newAtts.addAttribute("", "", "src", "CDATA", "er:#" + imgGuid);
						
						atts = newAtts;
					}
					else
					{
						throw new SAXException("Could not retrieve a valid image guid from the image hyperlink anchor");
					}
				}
			}
		}
		
		super.startElement(uri, localName, qName, atts);
	}
	
	@Override
	public void characters(char buf[], int offset, int len) throws SAXException
	{
		if (!isImageLink)
		{
			super.characters(buf, offset, len);
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException
	{
		if (!isImageLink)
		{
			super.endElement(uri, localName, qName);
		}
		else
		{
			if (qName.equalsIgnoreCase("a"))
			{
				qName = "img";
				isImageLink = false;
				super.endElement(uri, localName, qName);
			}
		}
	}
}
