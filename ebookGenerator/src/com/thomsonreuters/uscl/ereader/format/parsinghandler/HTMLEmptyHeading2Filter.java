/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.format.parsinghandler;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * Filter that removes non-nested empty h2 tags.
 * 
 * Please reference com.thomsonreuters.uscl.ereader.format.parsinghandler.HTMLEmptyHeading2FilterTest for
 * detailed test scenarios that filter covers.
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public class HTMLEmptyHeading2Filter extends XMLFilterImpl{
	private boolean isHeading2 = false;
	private int expectedNumClosedH2s = 0;
	private List<ParserEvent> eventBuffer = new ArrayList<ParserEvent>();
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException
	{		

		if (qName.equalsIgnoreCase("h2"))
		{
			isHeading2 = true;
			expectedNumClosedH2s++;
			eventBuffer.add(new StartElement(uri, localName, qName, new AttributesImpl(atts)));
		}
		else if (isHeading2)
		{
			eventBuffer.add(new StartElement(uri, localName, qName, new AttributesImpl(atts)));
		}
		else
		{
			super.startElement(uri, localName, qName, new AttributesImpl(atts));
		}
	}
	
	@Override
	public void characters(char buf[], int offset, int len) throws SAXException
	{
		if (isHeading2)
		{
			eventBuffer.add(new Characters(buf, offset, len));
		}
		else
		{
			super.characters(buf, offset, len);
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException
	{
		if (isHeading2)
		{
			if (qName.equalsIgnoreCase("h2"))
			{	
				if (expectedNumClosedH2s > 1)
				{
					ParserEvent event = eventBuffer.get(eventBuffer.size() - 1);
					if (event.getEventType() == ParserEvent.START_EVENT 
							&& ((StartElement)event).getQName().equalsIgnoreCase("h2"))
					{
						eventBuffer.remove(eventBuffer.size() - 1);
					}
					else
					{
						eventBuffer.add(new EndElement(uri, localName, qName));
					}
				}
				else
				{
					if (eventBuffer.size() > 1)
					{
						eventBuffer.add(new EndElement(uri, localName, qName));
						dumpOutBuffer();
					}
					else
					{
						isHeading2 = false;
						eventBuffer= new ArrayList<ParserEvent>();
					}
				}
				
				expectedNumClosedH2s--;
			}
			else
			{
				eventBuffer.add(new EndElement(uri, localName, qName));
			}
		}
		else
		{
			super.endElement(uri, localName, qName);
		}
	}
	
	/**
	 * Helper method that dumps out the buffer.
	 * 
	 * @throws SAXException if any SAX issues are encountered
	 */
	private void dumpOutBuffer() throws SAXException
	{
		for (ParserEvent event : eventBuffer)
		{
			if (event.getEventType() == ParserEvent.START_EVENT)
			{
				StartElement elem = (StartElement) event;
				super.startElement(elem.getUri(), elem.getLocalName(), elem.getQName(), elem.getAtts());
			}
			else if (event.getEventType() == ParserEvent.CHAR_EVENT)
			{
				Characters elem = (Characters) event;
				super.characters(elem.getBuffer(), elem.getOffset(), elem.getLength());
			}
			else
			{
				EndElement elem = (EndElement) event;
				super.endElement(elem.getUri(), elem.getLocalName(), elem.getQName());
			}
		}
		
		isHeading2 = false;
		eventBuffer= new ArrayList<ParserEvent>();
	}
}
