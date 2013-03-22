/*
* Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.format.parsinghandler;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


/**
 * Filter that looks for XSL attribute forcePlatform that has been added to the xsl:include
 *
 * @author <a href="mailto:Dong.Kim@thomsonreuters.com">Dong Kim</a> u0155568
 */
public class XSLForcePlatformAttributeFilter extends DefaultHandler {
	private static final String FORCE_PLATFORM_ATTR = "forcePlatform";
	private static final String HREF_ATTR = "href";
	private static final String INCLUDE_TAG = "xsl:include";
	private static final String IMPORT_TAG = "xsl:import";
	private boolean isForcePlatform = false;
	private String href;

	public XSLForcePlatformAttributeFilter(String href) {
		super();
		this.href = href;
	}

	public boolean isForcePlatform() {
		return isForcePlatform;
	}

	@Override
    public void startElement(
        final String uri, final String localName, final String qName, final Attributes atts)
        throws SAXException
    {
        if (qName.equalsIgnoreCase(INCLUDE_TAG) || qName.equalsIgnoreCase(IMPORT_TAG))
        {
        	String hrefAttributeValue = atts.getValue(HREF_ATTR);
        	
        	if (hrefAttributeValue != null && hrefAttributeValue.equalsIgnoreCase(href))
        	{
	        	String forcePlatformStr = atts.getValue(FORCE_PLATFORM_ATTR);
	        	if(forcePlatformStr != null && forcePlatformStr.equalsIgnoreCase("true")) 
	        	{
	        		isForcePlatform = true;
	        	}
        	}
        }
        
        super.startElement(uri, localName, qName, atts);
    }
	

}
