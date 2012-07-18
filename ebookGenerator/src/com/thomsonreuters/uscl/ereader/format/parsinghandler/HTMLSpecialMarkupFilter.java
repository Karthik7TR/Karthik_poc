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
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public class HTMLSpecialMarkupFilter extends XMLFilterImpl 
{
	protected static final String DEL_TAG = "del";
	protected static final String INS_TAG = "ins";
	protected static final String SPAN_TAG = "span";
	protected static final String INS_STYLE = "background-color: #ccffff; text-decoration: none;";
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException
	{
		if (qName.equalsIgnoreCase(DEL_TAG)) 
		{
			AttributesImpl newAtts = new AttributesImpl(atts);
			newAtts.addAttribute("", "", "class", "CDATA", "co_crosshatch");
			super.startElement(uri, localName, SPAN_TAG, newAtts);
		} 
		else if (qName.equalsIgnoreCase(INS_TAG))
		{
			AttributesImpl newAtts = new AttributesImpl(atts);
			newAtts.addAttribute("", "", "style", "CDATA", INS_STYLE);
			super.startElement(uri, localName, SPAN_TAG, newAtts);
		} 
		else
		{
			super.startElement(uri, localName, qName, atts);
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException
	{
		//Remove any del or ins tags and replace with span tag
		if (qName.equalsIgnoreCase(DEL_TAG)) 
		{
			super.endElement(uri, localName, SPAN_TAG);
	    } 
		else if (qName.equalsIgnoreCase(INS_TAG))
		{
		    super.endElement(uri, localName, SPAN_TAG);
	    } 
		else
		{
		    super.endElement(uri, localName, qName);
	    }
	}	
}
