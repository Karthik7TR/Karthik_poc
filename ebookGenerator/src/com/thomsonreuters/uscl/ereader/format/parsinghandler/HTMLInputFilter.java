/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.format.parsinghandler;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * Filter that handles various input "<input>" tags and transforms them as needed. Most
 * input tags should be removed but some placeholder tags might be used to inject more
 * relevant data.
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public class HTMLInputFilter extends XMLFilterImpl {
	
	private boolean keyCitePlaceholder = false;
	private boolean isInputTag = false;

	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException
	{
		if (qName.equalsIgnoreCase("input"))
		{
			isInputTag = true;
			if (atts != null)
			{
				String id = atts.getValue("id");
				String type = atts.getValue("type");
				if ( id != null && id.equalsIgnoreCase("co_keyCiteFlagPlaceHolder")
						&& type != null && type.equalsIgnoreCase("hidden"))
				{
					//TODO: Add KeyCite link or display generation based on NPD rules.
					keyCitePlaceholder = true;
				}
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
		if (keyCitePlaceholder)
		{
			//TODO: Add KeyCite link or display generation based on NPD rules.
		}
		else if(isInputTag)
		{
			//Remove anything from within the input tags.
		}
		else
		{
			super.characters(buf, offset, len);
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException
	{
		if (keyCitePlaceholder)
		{
			keyCitePlaceholder = false;
			isInputTag = false;
			//TODO: Add KeyCite link or display generation based on NPD rules.
		}
		else if(isInputTag)
		{
			isInputTag = false;
		}
		else
		{
			super.endElement(uri, localName, qName);
		}
	}	
}
