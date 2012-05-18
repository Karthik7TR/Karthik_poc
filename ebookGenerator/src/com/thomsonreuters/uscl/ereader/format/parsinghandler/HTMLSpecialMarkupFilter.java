/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.format.parsinghandler;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * Filter that handles various HTML special Markup tags and transforms them as needed.
 * 
 * The filter changes the following xml elements
 * 
 * Input xml element : <del>Tell the Feds Mike drove you away from the bank.</del>
 * Output xml element: <span class=”co_crosshatch”>Tell the Feds Mike drove you away from the bank.</span>
 *
 * @author <a href="mailto:nirupam.chatterjee@thomsonreuters.com">Nirupam Chatterjee</a> u0072938
 */
public class HTMLSpecialMarkupFilter extends XMLFilterImpl {
	
	private boolean isdelTag = false;

	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException
	{
		if (qName.equalsIgnoreCase("del")) {
			isdelTag = true;
			AttributesImpl newAtts = new AttributesImpl(atts);
			newAtts.addAttribute("", "", "class", "CDATA", "co_crosshatch");
			super.startElement(uri, localName, "span", newAtts);
		} else {
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
		//Remove any del tags and replace with span tag
		if (qName.equalsIgnoreCase("del")) {
		if (isdelTag) {
			super.endElement(uri, localName, "span");
			isdelTag = false;
		} 
		} else {
			super.endElement(uri, localName, qName);
		}
	}	
}
