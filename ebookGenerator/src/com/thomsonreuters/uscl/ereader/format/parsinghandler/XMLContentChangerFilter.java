/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.format.parsinghandler;

import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentCopyright;
import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentCurrency;

/**
 * Filter that handles various content changes
 *
 * @author <a href="mailto:Dong.Kim@thomsonreuters.com">Dong Kim</a> u0155568
 */
public class XMLContentChangerFilter extends XMLFilterImpl {
	private static final String GUID_ATTR = "n-include_guid";
	private static final String CURRENCY_TAG = "include.currency";
	private boolean isChanging = false;
	private List<DocumentCopyright> copyrights;
	private List<DocumentCopyright> copyCopyrights;
	
	private static final String COPYRIGHT_TAG = "include.copyright";
	private List<DocumentCurrency> currencies;
	private List<DocumentCurrency> copyCurrencies;
	
	public XMLContentChangerFilter(List<DocumentCopyright> copyrights, List<DocumentCopyright> copyCopyrights,
			List<DocumentCurrency> currencies, List<DocumentCurrency> copyCurrencies) {
		super();
		this.copyrights = copyrights;
		this.copyCopyrights = copyCopyrights;
		this.currencies = currencies;
		this.copyCurrencies = copyCurrencies;
	}

	@Override
    public void startElement(
        final String uri, final String localName, final String qName, final Attributes atts)
        throws SAXException
    {
        if (qName.equalsIgnoreCase(CURRENCY_TAG))
        {
        	String guid = atts.getValue(GUID_ATTR);
        	for(DocumentCurrency currency: currencies) 
        	{
        		if(currency.getCurrencyGuid().equalsIgnoreCase(guid)) 
        		{
        			isChanging = true;
        			copyCurrencies.remove(currency);
        			replaceMessageElement(uri, localName, qName, atts, currency.getNewText());
        		}
        	}
        }
        else if (qName.equalsIgnoreCase(COPYRIGHT_TAG))
        {
        	String guid = atts.getValue(GUID_ATTR);
        	for(DocumentCopyright copyright: copyrights) 
        	{
        		if(copyright.getCopyrightGuid().equalsIgnoreCase(guid)) 
        		{
        			isChanging = true;
        			copyCopyrights.remove(copyright);
        			replaceMessageElement(uri, localName, qName, atts, copyright.getNewText());
        		}
        	}
        } 
        // Only use current element is it is not changing
        if(!isChanging) 
        {
        	super.startElement(uri, localName, qName, atts);
        }
    }
	
	private void replaceMessageElement(String uri, String localName, String qName, Attributes atts, String message) 
			throws SAXException {
		super.startElement(uri, localName, qName, atts);
		super.characters(message.toCharArray(), 0, message.length());
		super.endElement(uri, localName, qName);
	}
	
	@Override
	public void characters(char buf[], int offset, int len) throws SAXException
	{
		if (!isChanging)
		{
			super.characters(buf, offset, len);
		}
	}

    @Override
    public void endElement(final String uri, final String localName, final String qName)
        throws SAXException
    {
    	if(isChanging) {
        	isChanging = false;
    	} else {
            super.endElement(uri, localName, qName);
    	}
    	
    }
}
