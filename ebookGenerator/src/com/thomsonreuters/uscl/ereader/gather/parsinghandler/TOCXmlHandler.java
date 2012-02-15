/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.gather.parsinghandler;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This class defines the handler that parses the Toc xml to generate a list of GUIDs.
 *
 * @author <a href="mailto:Nirupam.Chatterjee@thomsonreuters.com">Nirupam Chatterjee</a> u0072938
 */
public class TOCXmlHandler extends DefaultHandler
{
	private ArrayList<HashMap<String, List<String>>> finalDocGuidList = new ArrayList<HashMap<String, List<String>>>();
	

	private HashMap<String, List<String>> docGuidList = new HashMap<String, List<String>>(); 
	private List<String> tocGuidList = new ArrayList<String>();	
	private String tempVal;
	

	private static final String DOCUMENT_GUID_ELEMENT ="DocumentGuid";
	private static final String TOC_GUID_ELEMENT =	"Guid";
	
	public void startElement(String uri, String localName, String qName, Attributes atts)
			throws SAXParseException
	{
		if (qName.equalsIgnoreCase(DOCUMENT_GUID_ELEMENT) ||
				qName.equalsIgnoreCase(TOC_GUID_ELEMENT))
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
				docGuidList.put(tempVal,tocGuidList);
				tocGuidList = new ArrayList<String>();
				finalDocGuidList.add(docGuidList);
				docGuidList = new HashMap<String, List<String>>();

			}
		}
		
		if(qName.equalsIgnoreCase(TOC_GUID_ELEMENT)) {
			if (tempVal.length() != 0) {
			//add only if there is a guid available
				tocGuidList.add(tempVal);
			}
		}		
	}	
	
	public List<String> getTocGuidList() {
		return tocGuidList;
	}

	public void setTocGuidList(List<String> tocGuidList) {
		this.tocGuidList = tocGuidList;
	}

	public HashMap<String, List<String>> getDocGuidList() {
		return docGuidList;
	}
	public ArrayList<HashMap<String, List<String>>> getFinalDocGuidList() {
		return finalDocGuidList;
	}

	public void setDocGuidList(HashMap<String, List<String>> docGuidList) {
		this.docGuidList = docGuidList;
	}	
}
