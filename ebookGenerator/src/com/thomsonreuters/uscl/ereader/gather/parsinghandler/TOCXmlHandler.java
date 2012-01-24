/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.gather.parsinghandler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadata;

/**
 * This class defines the handler that parses the Toc xml to generate a list of GUIDs.
 *
 * @author <a href="mailto:Nirupam.Chatterjee@thomsonreuters.com">Nirupam Chatterjee</a> u0072938
 */
public class TOCXmlHandler extends DefaultHandler
{
	private List<String> guidList = new ArrayList<String>();
	private String tempVal;
	

	private static final String DOCUMENT_GUID_ELEMENT ="DocumentGuid";
	
	public void startElement(String uri, String localName, String qName, Attributes atts)
			throws SAXParseException
	{
		if (qName.equalsIgnoreCase(DOCUMENT_GUID_ELEMENT))
		{
			tempVal = "";
		}
	}
	
	public void characters(char[] ch, int start, int length) throws SAXException {
		tempVal = new String(ch,start,length);
	}
	
	public void endElement(String uri, String localName, String qName) throws SAXException {

		if(qName.equalsIgnoreCase(DOCUMENT_GUID_ELEMENT)) {
			if (tempVal.length() != 0) {
			//add only if there is a guid available
			guidList.add(tempVal);
			}
		}
	}	
	
	public void setGuidList(List<String> aList)
	{
		guidList = aList;
	}
	
	public List<String> getGuidList()
	{
		return guidList;
	}	
}
