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
 * Filter that handles various Table elements and transforms them as needed.
 *
 * @author <a href="mailto:Ravi.Nandikolla@thomsonreuters.com">Ravi Nandikolla</a> c139353
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public class HTMLTableFilter extends XMLFilterImpl
{
	private static final String TABLE_TAG = "table";
	private static final String TABLE_DATA_TAG = "td";
	private static final String CLASS_ATTRIBUTE = "class";
	private static final String COPYRIGHT_CSS_CLASS = "co_endOfDocCopyright";
	private static final String PROVIEW_TABLE_VIEWER_CLASS = "tr_table";
	
	private boolean isCopyrightTag = false;
	
	private boolean tableViewerSupport = false;
	
	public HTMLTableFilter(boolean tableViewerSupport)
	{
		this.tableViewerSupport = tableViewerSupport;
	}
	
	public void setTableViewerSupport(boolean tableViewerSupport)
	{
		this.tableViewerSupport = tableViewerSupport;
	}
	
	public boolean getTableViewerSupport()
	{
		return tableViewerSupport;
	}

    @Override
    public void startElement(
        final String uri, final String localName, final String qName, final Attributes atts)
        throws SAXException
    {
        if (tableViewerSupport && qName.equalsIgnoreCase(TABLE_TAG))
        {
            AttributesImpl newAtts = new AttributesImpl(atts);
            newAtts.clear();
            newAtts.addAttribute("", "", CLASS_ATTRIBUTE, "CDATA", PROVIEW_TABLE_VIEWER_CLASS);
            super.startElement(uri, localName, qName, newAtts);
        }
        else if (qName.equalsIgnoreCase(TABLE_DATA_TAG))
        {
        	String cssClass = atts.getValue(CLASS_ATTRIBUTE);
        	if (cssClass != null && cssClass.equalsIgnoreCase(COPYRIGHT_CSS_CLASS))
        	{
        		isCopyrightTag = true;
        	}
        	super.startElement(uri, localName, qName, atts);
        }
        else
        {
            super.startElement(uri, localName, qName, atts);
        }
    }
	
	@Override
	public void characters(char buf[], int offset, int len) throws SAXException
	{
		if (isCopyrightTag)
		{
			//Remove the copyright text, if NPD decides to inject placeholder text just event it here
		}
		else
		{
			super.characters(buf, offset, len);
		}
	}

    @Override
    public void endElement(final String uri, final String localName, final String qName)
        throws SAXException
    {
        if (qName.equalsIgnoreCase(TABLE_DATA_TAG) && isCopyrightTag)
        {
        	isCopyrightTag = false;
        }
        super.endElement(uri, localName, qName);
    }
}
