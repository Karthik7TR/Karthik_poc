/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.format.parsinghandler;

import java.util.HashMap;
import java.util.HashSet;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * Filter that handles various Anchor "<a>" tags and transforms them as needed.
 *
 * @author <a href="mailto:Kirsten.Gunn@thomsonreuters.com">Kirsten Gunn</a> u0076257
 */
public class HTMLUnlinkInternalLinksFilter extends XMLFilterImpl {
	
	private HashSet<String> nameAnchors;
	private HashMap<String, HashSet<String>> targetAnchors;
	private int badLinkCntr = 0;
	private int goodAnchorCntr = 0;
	private String currentGuid;
	
	public String getCurrentGuid() 
	{
		return currentGuid;
	}
	public void setCurrentGuid(String currentGuid) 
	{
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

				if (targetAnchors != null)
				{
					nameAnchors =  targetAnchors.get(guid);
				}
					
				if ( nameAnchors != null && nameAnchors.contains(atts.getValue("href")))
				{
					// remove anchor with no target.
					badLinkCntr++;
				}
				else
				{
					super.startElement(uri, localName, qName, atts);
					goodAnchorCntr++;
				}
			}
			else
			{
				super.startElement(uri, localName, qName, atts);
				goodAnchorCntr++;
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
			if (badLinkCntr > 0) // an anchor has been removed
			{
				if (goodAnchorCntr == 0)
				{
					badLinkCntr--;
				}
				if (goodAnchorCntr > 0)
				{
					super.endElement(uri, localName, qName);
					goodAnchorCntr--;
				}	
			}
			else 
			{
				super.endElement(uri, localName, qName);
				if (goodAnchorCntr > 0)
				{
					goodAnchorCntr--;
				}
			}
		}
		else
		{
			super.endElement(uri, localName, qName);
		}
	}
}
