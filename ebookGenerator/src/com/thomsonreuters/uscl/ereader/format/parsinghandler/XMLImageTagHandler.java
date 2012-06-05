/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.format.parsinghandler;

import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This class defines the handler that parses the XML image.link tags to generate a list of GUIDs.
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public class XMLImageTagHandler extends DefaultHandler
{
	private List<String> guidList;
	
	public void startElement(String uri, String localName, String qName, Attributes atts)
			throws SAXParseException
	{
		if (qName.equalsIgnoreCase("image.link"))
		{
			guidList.add(atts.getValue("target"));
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
