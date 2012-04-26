/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.format.parsinghandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.commons.lang.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;

import com.thomsonreuters.uscl.ereader.gather.image.domain.ImageMetadataEntity;
import com.thomsonreuters.uscl.ereader.gather.image.domain.ImageMetadataEntityKey;
import com.thomsonreuters.uscl.ereader.gather.image.service.ImageService;

/**
 * Filter that handles various Anchor "<a>" tags and transforms them as needed.
 *
 * @author <a href="mailto:Kirsten.Gunn@thomsonreuters.com">Kirsten Gunn</a> u0076257
 */
public class HTMLUnlinkInternalLinksFilter extends XMLFilterImpl {
	
	private HashSet<String> nameAnchors;
	private HashMap<String, HashSet<String>> targetAnchors;
	private boolean isBadLink = false;
	private int goodAnchor = 0;
	private String currentGuid;
	

	public String getCurrentGuid() {
		return currentGuid;
	}
	public void setCurrentGuid(String currentGuid) {
		this.currentGuid = currentGuid;
	}

	public void setTargetAnchors(HashMap<String, HashSet<String>> targetAnchors )
	{
		this.targetAnchors = targetAnchors;
	}
	public HashMap<String, HashSet<String>> getTargetAnchors()
	{
		return targetAnchors;
	}

	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException
	{
		if (qName.equalsIgnoreCase("a"))
		{
			if (atts != null && atts.getValue("href") != null && 
					atts.getValue("href").startsWith("er:#") && atts.getValue("href").contains("/"))
			{
				String guid = currentGuid;
				String guidList[] = atts.getValue("href").split("/");

				if (guidList.length > 1)
				{
					guid = guidList[0].substring(4);
				}

				nameAnchors =  targetAnchors.get(guid);
					
				if ( nameAnchors != null && nameAnchors.contains(atts.getValue("href")))
				{
					// remove anchor with no target.
					isBadLink = true;
				}
				else
				{
					super.startElement(uri, localName, qName, atts);
					goodAnchor++;
				}
			}
			else
			{
				super.startElement(uri, localName, qName, atts);
				goodAnchor++;
			}
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
		if (qName.equalsIgnoreCase("a"))
		{
			if (isBadLink) // an anchor has been removed
			{
				if (goodAnchor == 0)
				{
					isBadLink = false;
				}
				if (goodAnchor > 0)
				{
					super.endElement(uri, localName, qName);
					goodAnchor--;
				}	
			}
			else 
			{
				super.endElement(uri, localName, qName);
				if (goodAnchor > 0)
				{
					goodAnchor--;
				}
			}
		}
		else
		{
			super.endElement(uri, localName, qName);
		}
	}
}
