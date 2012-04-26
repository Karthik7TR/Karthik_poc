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
public class HTMLIdFilter extends XMLFilterImpl {
	
	private String currentGuid;
	private HashSet<String> nameAnchors;
	private HashMap<String, HashSet<String>> targetAnchors;
	private boolean isAnchorAdded = false;


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
		String guid = currentGuid;
				if (atts != null && atts.getValue("id") != null)
				{
					String guidList[] = atts.getValue("id").split("/");
					if (guidList.length > 1)
					{
						guid = guidList[0];
					}


					nameAnchors =  targetAnchors.get(guid);
					// add anchor id after <span id="co_footnote_I6a32d800568d11e199970000837bc6dd"> like
					// <a name="er:#I38680d13677511dc8ebc0000837214a9/co_footnote_I6a32d800568d11e199970000837bc6dd">
					String fullyQualifiedId = "er:#" + guid + "/" + atts.getValue("id");
					
					if (nameAnchors != null && nameAnchors.contains(fullyQualifiedId))
					{
						// insert missing named anchor
						AttributesImpl newAtts = new AttributesImpl(atts);
							
							int indexId = newAtts.getIndex("id");
							if (indexId > -1)
							{
								newAtts.setAttribute(indexId, "", "", "name", "CDATA", atts.getValue("id"));
							}
							isAnchorAdded = true;
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
		if (isAnchorAdded)
		{
			super.endElement(uri, localName, "a");
			isAnchorAdded = false;
		}
		super.endElement(uri, localName, qName);
		

	}
}
