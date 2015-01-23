/*
* Copyright 2015: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.format.parsinghandler;

import org.apache.commons.lang.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * Filter that handles removal of the Editors' Notes heading added by WLN style sheets
 * 
 *
 * @author <a href="mailto:dong.kim@thomsonreuters.com">Dong Kim</a> u0155568
 */
public class HTMLEditorNotesFilter extends XMLFilterImpl 
{

	private boolean delEditorNodeHeading = false;
	private boolean inEditorNotesBlock = false;
	private int level = 0;
	
	protected static final String DIV_TAG = "div";
	
	public HTMLEditorNotesFilter(boolean delEditorNodeHeading) {
		this.delEditorNodeHeading = delEditorNodeHeading;
	}
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException
	{
		String eBookEditorNoteAttribute = atts.getValue("eBookEditorNotes");
		if(inEditorNotesBlock) 
		{
			// Do not process elements
			level++;
		}
		else if(delEditorNodeHeading && StringUtils.isNotBlank(eBookEditorNoteAttribute) && 
				eBookEditorNoteAttribute.equals("true"))
		{
			// Do not process elements
			inEditorNotesBlock = true;
		}
		else
		{
			super.startElement(uri, localName, qName, atts);
		}
	}
	
	@Override
	public void characters(char buf[], int offset, int len) throws SAXException
	{
		//Only remove characters in Editors' Notes block
		if (!inEditorNotesBlock)
		{
			super.characters(buf, offset, len);
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException
	{
		if (delEditorNodeHeading && inEditorNotesBlock && level == 0 && qName.equalsIgnoreCase(DIV_TAG))
		{
			// Do not process elements
			inEditorNotesBlock = false;
		}
		else if(inEditorNotesBlock) 
		{
			// Do not process elements
			level--;
		}
		else
		{
		    super.endElement(uri, localName, qName);
	    }
	}	
}
