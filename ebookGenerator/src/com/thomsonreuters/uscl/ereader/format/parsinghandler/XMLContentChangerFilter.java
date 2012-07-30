/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.format.parsinghandler;

import java.util.HashMap;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * Filter that handles various content changes
 *
 * @author <a href="mailto:Dong.Kim@thomsonreuters.com">Dong Kim</a> u0155568
 */
public class XMLContentChangerFilter extends XMLFilterImpl {
	private static final String CURRENCY_TAG = "include.currency";
	private boolean isCurrencyChanging = false;;
	private String currencyMessage;
	private HashMap<String, String> currencyMap;
	
	private static final String COPYRIGHT_TAG = "include.copyright";
	private boolean isCopyrightChanging = false;
	private String copyrightMessage;
	private HashMap<String, String> copyrightMap;
	
	private static final String GUID_ATTR = "n-include_guid";
	
	private boolean isFinalStage;

	public void setFinalStage(boolean isFinalStage) {
		this.isFinalStage = isFinalStage;
	}

	public void setCurrencyMap(HashMap<String, String> currencyMap) {
		this.currencyMap = currencyMap;
	}

	public void setCopyrightMap(HashMap<String, String> copyrightMap) {
		this.copyrightMap = copyrightMap;
	}

	@Override
    public void startElement(
        final String uri, final String localName, final String qName, final Attributes atts)
        throws SAXException
    {
        if (!isFinalStage && qName.equalsIgnoreCase(CURRENCY_TAG))
        {
        	String guid = atts.getValue(GUID_ATTR);
        	if(currencyMap.containsKey(guid)) {
        		isCurrencyChanging = true;
        		currencyMessage = currencyMap.get(guid);
        	}
        }
        else if (!isFinalStage && qName.equalsIgnoreCase(COPYRIGHT_TAG))
        {
        	String guid = atts.getValue(GUID_ATTR);
        	if(copyrightMap.containsKey(guid)) {
        		isCopyrightChanging = true;
        		copyrightMessage = copyrightMap.get(guid);
        	}
        }
        
        super.startElement(uri, localName, qName, atts);
    }
	
	@Override
	public void characters(char buf[], int offset, int len) throws SAXException
	{
		if (isCurrencyChanging)
		{
			buf = updateMessage(buf, offset, len, currencyMessage);
			len = currencyMessage.length();
		}
		else if (isCopyrightChanging)
		{
			buf = updateMessage(buf, offset, len, copyrightMessage);
			len = copyrightMessage.length();
		}
		
		super.characters(buf, offset, len);
	}
	
	private char[] updateMessage(char buf[], int offset, int len, String message) {
		StringBuilder buffer = new StringBuilder(String.valueOf(buf));
		buffer = buffer.replace(offset, offset + len, message);
		return buffer.toString().toCharArray();
	}

    @Override
    public void endElement(final String uri, final String localName, final String qName)
        throws SAXException
    {
    	isCurrencyChanging = false;
    	isCopyrightChanging = false;
    	
        super.endElement(uri, localName, qName);
    }
}
