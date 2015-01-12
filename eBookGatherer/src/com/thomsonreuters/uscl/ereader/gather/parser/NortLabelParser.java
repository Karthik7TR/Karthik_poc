/*
 * Copyright 2015: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.gather.parser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.SequenceInputStream;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.apache.commons.lang.StringEscapeUtils;

import com.thomsonreuters.uscl.ereader.gather.exception.NortLabelParseException;

public class NortLabelParser {
	private static final String HEADING_ELEMENT_NAME = "heading";
	private static final String START_WRAPPER_TAG = "<labels>";
	private static final String END_WRAPPER_TAG = "</labels>";
	
	private boolean inHeadingElement = false;
	private XMLInputFactory factory = null;
	
	
	
	public NortLabelParser() {
		factory = XMLInputFactory.newInstance();
		factory.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, false);
		factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
	}

	public String parse(String text) throws NortLabelParseException {
		StringBuffer buffer = new StringBuffer();
		XMLEventReader r = null;
		
		
		try (InputStream startInput = new ByteArrayInputStream(START_WRAPPER_TAG.getBytes("UTF-8"));
				InputStream textInput = new ByteArrayInputStream(text.getBytes("UTF-8"));
				InputStream intermediateStream =  new SequenceInputStream(startInput, textInput);
				InputStream endInput = new ByteArrayInputStream(END_WRAPPER_TAG.getBytes("UTF-8"));
				InputStream input =  new SequenceInputStream(intermediateStream, endInput);
				) {
			r = factory.createXMLEventReader(input, "UTF-8");
			
			int level = 0;
			while(r.hasNext()) {
				XMLEvent event = r.nextEvent();
				
				if(event.isStartElement()) {
					StartElement element = event.asStartElement();
					System.out.println(element.getName().getLocalPart());
					if(element.getName().getLocalPart().equalsIgnoreCase(HEADING_ELEMENT_NAME)) {
						inHeadingElement = true;
					} else {
						level++;
					}
				}
				
				if(event.isCharacters()) {
					Characters character = event.asCharacters();
					if(inHeadingElement) {
						buffer.append(character.getData());
					} else if (level == 1) {
						buffer.append(character.getData());
					}
				}

				
				if(event.isEndElement()) {
					EndElement element = event.asEndElement();
					if(element.getName().getLocalPart().equalsIgnoreCase(HEADING_ELEMENT_NAME)) {
						inHeadingElement = false;
					} else {
						level--;
					}
				}
			}
		} catch (Exception e) {
			throw new NortLabelParseException("Error while processing label: " + text, e);
		} finally {
			try {
				r.close();
			} catch (XMLStreamException e) {
				throw new NortLabelParseException("Failed to close XMLEventReader.", e);
			}
		}
		
		return StringEscapeUtils.escapeXml(buffer.toString());
	}
	
}
