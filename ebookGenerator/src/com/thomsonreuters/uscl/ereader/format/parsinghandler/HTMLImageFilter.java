/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.format.parsinghandler;

import java.util.Set;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * Filter that handles various Anchor "<img>" tags and transforms them as needed.
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public class HTMLImageFilter extends XMLFilterImpl {
	
	private Set<String> staticImageRefs;
	
	public Set<String> getStaticImageRefs() {
		return staticImageRefs;
	}

	public void setStaticImageRefs(Set<String> staticImageRefs) {
		this.staticImageRefs = staticImageRefs;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException
	{
		if (qName.equalsIgnoreCase("img"))
		{
			if (atts != null)
			{
				String src = atts.getValue("src");
				if ( src != null && src.startsWith("/images/"))
				{
					staticImageRefs.add(src.substring(8));
					src = src.replace("/images/", "er:#");
					src = src.substring(0, src.indexOf("."));
		
					AttributesImpl newAtts = new AttributesImpl(atts);
					newAtts.removeAttribute(newAtts.getIndex("src"));
					newAtts.addAttribute("", "", "src", "CDATA", src);

					super.startElement(uri, localName, qName, newAtts);
				}
				else
				{
					throw new SAXException("Could not retrieve src attribute for img tag. " +
							"Code should be added to handle these.");
				}
			}
			else
			{
				throw new SAXException("Could not retrieve attributes for an image tag.");
			}
		}
		else
		{
			super.startElement(uri, localName, qName, atts);
		}
	}
}
