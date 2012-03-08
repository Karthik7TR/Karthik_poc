/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.format.parsinghandler;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

import com.sun.xml.internal.bind.util.AttributesImpl;


/**
 * Filter that handles various Table elements and transforms them as needed.
 *
 * @author <a href="mailto:Ravi.Nandikolla@thomsonreuters.com">Ravi Nandikolla</a> c139353
 */
public class HTMLTableFilter extends XMLFilterImpl
{
    @Override
    public void characters(final char[] ch, final int offset, final int len)
    {
        //System.out.println("Info is : " + new String(ch, offset, len));
    }

    @Override
    public void endElement(final String uri, final String localName, final String qName)
        throws SAXException
    {
        super.endElement(uri, localName, qName);
    }

    @Override
    public void startElement(
        final String uri, final String localName, final String qName, final Attributes atts)
        throws SAXException
    {
        if (qName.equalsIgnoreCase("table"))
        {
            AttributesImpl newAtts = new AttributesImpl(atts);
            newAtts.clear();
            newAtts.addAttribute("", "", "class", "CDATA", "tr_table");
            super.startElement(uri, localName, qName, newAtts);
        }
        else
        {
            super.startElement(uri, localName, qName, atts);
        }
    }
}
