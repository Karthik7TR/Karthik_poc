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
 * Filter that handles class attributes on all tags and removes any CSS classes after the first
 * one specified.
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public class HTMLClassAttributeFilter extends XMLFilterImpl {

	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException
	{
		if (atts != null)
		{
			String classAtt = atts.getValue("class");
			if ( classAtt != null && classAtt.contains(" "))
			{
				classAtt = classAtt.substring(0, classAtt.indexOf(" "));
	
				AttributesImpl newAtts = new AttributesImpl(atts);
				newAtts.removeAttribute(newAtts.getIndex("class"));
				newAtts.addAttribute("", "", "class", "CDATA", classAtt);

				atts = newAtts;
			}
		}
		
		super.startElement(uri, localName, qName, atts);
	}
}
