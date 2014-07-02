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
 * This class defines the handler that parses the Toc xml to generate a list of
 * GUIDs.
 * 
 * @author <a href="mailto:Nirupam.Chatterjee@thomsonreuters.com">Nirupam
 *         Chatterjee</a> u0072938
 */
public class TOCXmlHandler extends DefaultHandler {
	private HashMap<String, List<String>> docGuidList = new HashMap<String, List<String>>();
	private List<String> tocGuidList = new ArrayList<String>();
	private StringBuffer tempVal = null;

	private static final String DOCUMENT_GUID_ELEMENT = "DocumentGuid";
	private static final String TOC_GUID_ELEMENT = "Guid";

	public void startElement(String uri, String localName, String qName,
			Attributes atts) throws SAXParseException {
		if (qName.equalsIgnoreCase(DOCUMENT_GUID_ELEMENT)
				|| qName.equalsIgnoreCase(TOC_GUID_ELEMENT)) {
			tempVal = new StringBuffer();
		} else {
			tempVal = null;
		}
	}

	public void characters(char[] ch, int start, int length)
			throws SAXException {
		if(tempVal != null) {
			for(int i=start; i<start+length; i++) {
				tempVal.append(ch[i]);
			}
		}
	}

	public void endElement(String uri, String localName, String qName)
			throws SAXException {

		if (qName.equalsIgnoreCase(DOCUMENT_GUID_ELEMENT)) {
			// add only if there is a guid available
			if (tempVal.length() != 0) {
				// check to see if there is already a toc guid list list for
				// this doc uuid
				if (!docGuidList.containsKey(tempVal)) {
					docGuidList.put(tempVal.toString(), tocGuidList);
				} else { // append to the list of exisitng toc guids
					List<String> existingTocGuidList = docGuidList.get(tempVal);
					for (int i = 0; i < existingTocGuidList.size(); i++) {
						tocGuidList.add(existingTocGuidList.get(i));
					}
					docGuidList.remove(tempVal);
					docGuidList.put(tempVal.toString(), tocGuidList);
				}
				tocGuidList = new ArrayList<String>();
			}
		}

		if (qName.equalsIgnoreCase(TOC_GUID_ELEMENT)) {
			if (tempVal.length() != 0) {
				// add only if there is a guid available
				tocGuidList.add(tempVal.toString());
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

	public void setDocGuidList(HashMap<String, List<String>> docGuidList) {
		this.docGuidList = docGuidList;
	}
}
