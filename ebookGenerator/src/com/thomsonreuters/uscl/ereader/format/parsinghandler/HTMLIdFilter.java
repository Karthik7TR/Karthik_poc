/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.format.parsinghandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * Filter that handles various Anchor "<a>" tags and transforms them as needed.
 *
 * @author <a href="mailto:Kirsten.Gunn@thomsonreuters.com">Kirsten Gunn</a> u0076257
 */
public class HTMLIdFilter extends XMLFilterImpl {
	
	private String currentGuid;
	private HashSet<String> nameAnchors;
	private HashMap<String, HashSet<String>> targetAnchors;
	private int anchorAddedCntr = 0;
	private int goodStartCntr = 0;
	private ArrayList<Integer> goodCntrList = new ArrayList<Integer>();

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
		String guid = currentGuid;
				if (atts != null && atts.getValue("id") != null)
				{
					String guidList[] = atts.getValue("id").split("/");
					if (guidList.length > 1)
					{
						guid = guidList[0];
					}

					if(targetAnchors != null)
					{
					nameAnchors =  targetAnchors.get(guid);
					}
					// add anchor id after <span id="co_footnote_I6a32d800568d11e199970000837bc6dd"> like
					// <a name="er:#I38680d13677511dc8ebc0000837214a9/co_footnote_I6a32d800568d11e199970000837bc6dd">
					String fullyQualifiedId = "er:#" + guid + "/" + atts.getValue("id");
					
					if (nameAnchors != null && nameAnchors.contains(fullyQualifiedId))
					{
						// insert missing named anchor
						AttributesImpl newAtts = new AttributesImpl();
							
							newAtts.addAttribute( "", "", "name", "CDATA", atts.getValue("id"));
							
							if (anchorAddedCntr > 0)
							{
								goodCntrList.add(goodStartCntr);
								goodStartCntr = 0;
							}
							anchorAddedCntr++;
							// remove from list
							nameAnchors.remove(fullyQualifiedId);
							targetAnchors.get(guid).remove(fullyQualifiedId);
							// write existing id
							super.startElement(uri, localName, qName, atts);
							super.startElement(uri, localName, "a", newAtts);
					}
					else
					{
						super.startElement(uri, localName, qName, atts);
						if (anchorAddedCntr > 0)
						{
							goodStartCntr++;
						}
					}						
			}
			else
			{
				super.startElement(uri, localName, qName, atts);
				if (anchorAddedCntr > 0)
				{
					goodStartCntr++;
				}
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
		
		if (anchorAddedCntr> 0)
		{
			if (goodStartCntr == 0)
			{
				super.endElement(uri, localName, "a");
				anchorAddedCntr--;
				if (anchorAddedCntr >= 1)
				{
					int lastIdx = goodCntrList.size()-1;
					goodStartCntr = goodCntrList.get(lastIdx);
					goodCntrList.remove(lastIdx);
				}
			}
			else
			{
				goodStartCntr--;			
			}
		}
		super.endElement(uri, localName, qName);
	}
}
