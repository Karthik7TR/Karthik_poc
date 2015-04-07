/*
 * Copyright 2015: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.gather.codesworkbench.parsinghandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.apache.log4j.Logger;

import com.thomsonreuters.uscl.ereader.core.EBConstants;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.exception.GatherException;

public class NovusDocFileParser {
	private static final Logger Log = Logger.getLogger(NovusDocFileParser.class);
	
	private static String CHARSET = "UTF-8";	// explicitly set the character set
	private static String N_DOCUMENT = "n-document";
	private static String N_DOCBODY = "n-docbody";
	private static String N_METADATA = "n-metadata";
	private XMLInputFactory xmlInputFactory;
	private XMLOutputFactory xmlOutFactory;
	
	private boolean isDocbody = false;
	private boolean isMetadata = false;
	private boolean foundDocument = false;
	private String docGuid = null;
	private String docCollectionName;
	private HashMap<String, Integer> docGuidsMap;
	private File contentDestinationDirectory; 
	private File metadataDestinationDirectory;
	private Integer tocSequence;
	private GatherResponse gatherResponse;
	
	public NovusDocFileParser(String docCollectionName, HashMap<String, Integer> docGuidsMap,
			File contentDestinationDirectory, File metadataDestinationDirectory, GatherResponse gatherResponse,
			Integer tocSequence) {
		this.xmlInputFactory = XMLInputFactory.newFactory();
		this.xmlInputFactory.setProperty("javax.xml.stream.isCoalescing", true);
		this.xmlOutFactory = XMLOutputFactory.newFactory();
		
		this.docCollectionName = docCollectionName;
		this.docGuidsMap = docGuidsMap;
		this.contentDestinationDirectory = contentDestinationDirectory;
		this.metadataDestinationDirectory = metadataDestinationDirectory;
		this.gatherResponse = gatherResponse;
		this.tocSequence = tocSequence;
	}
	
	public void parseXML(File docFile) throws GatherException {
		XMLEventReader reader = null;
		FileOutputStream docbodyOutput = null;
		FileOutputStream metadataOutput = null;
		XMLEventWriter docbodyWriter = null;
		XMLEventWriter metadataWriter = null;
		
		int docFoundCounter = 0;
		int metaDocFoundCounter = 0;
		try (FileInputStream input = new FileInputStream(docFile)) {
			reader = xmlInputFactory.createXMLEventReader(input, CHARSET);
			while(reader.hasNext()) {
				XMLEvent event = (XMLEvent) reader.next();
				
				if(event.isStartElement()) {
					StartElement element = event.asStartElement();
					if(element.getName().getLocalPart().equalsIgnoreCase(N_DOCUMENT)) {
						Attribute guid = element.getAttributeByName(QName.valueOf("guid"));
						docGuid = guid.getValue();
						
						if(docGuidsMap.containsKey(docGuid)) {
							foundDocument = true;
							tocSequence++;
							docGuidsMap.remove(docGuid);
							
							// Create file names
							String docbodyBasename = docGuid + EBConstants.XML_FILE_EXTENSION;
							String metadataBasename = tocSequence + "-" + docCollectionName + "-"
									+ docGuid + EBConstants.XML_FILE_EXTENSION;
							
							docbodyOutput = new FileOutputStream(new File(contentDestinationDirectory, docbodyBasename));
							metadataOutput = new FileOutputStream(new File(metadataDestinationDirectory, metadataBasename));
							docbodyWriter = xmlOutFactory.createXMLEventWriter(docbodyOutput, CHARSET);
							metadataWriter = xmlOutFactory.createXMLEventWriter(metadataOutput, CHARSET);
						} else {
							Log.debug("Match Not Found for Novus Document GUID or was already processed: " + docGuid);
						}
					} else if(element.getName().getLocalPart().equalsIgnoreCase(N_DOCBODY) && foundDocument) {
						isDocbody = true;
						docFoundCounter++;
					} else if (element.getName().getLocalPart().equalsIgnoreCase(N_METADATA) && foundDocument) {
						isMetadata = true;
						metaDocFoundCounter++;
					}
				}
				// write out to files if condition is met
				if(isDocbody) {
					docbodyWriter.add(event);
				} else if(isMetadata) {
					metadataWriter.add(event);
				}
				// Needs to be after writers.
				if(event.isEndElement()) {
					EndElement element = event.asEndElement();
					if(element.getName().getLocalPart().equalsIgnoreCase(N_DOCBODY) && isDocbody) {
						isDocbody = false;
						docbodyWriter.flush();
						docbodyWriter.close();
						docbodyOutput.close();
					} else if (element.getName().getLocalPart().equalsIgnoreCase(N_METADATA) && isMetadata) {
						isMetadata = false;
						metadataWriter.flush();
						metadataWriter.close();
						metadataOutput.close();
					} else if(element.getName().getLocalPart().equalsIgnoreCase(N_DOCUMENT)) {
						foundDocument = false;
					}
				}
			}
			
		} catch (FileNotFoundException e) {
			GatherException ge = new GatherException(
					"File Not Found error", e,
					GatherResponse.CODE_FILE_ERROR);
			throw ge;
		} catch (IOException e) {
			GatherException ge = new GatherException(
					"File I/O error on document with GUID " + docGuid, e,
					GatherResponse.CODE_FILE_ERROR);
			throw ge;
		} catch (XMLStreamException e) {
			GatherException ge = new GatherException("Streaming doc file error " + docGuid, e,
					GatherResponse.CODE_FILE_ERROR);
			throw ge;
		} catch (Exception e) {
			GatherException ge = new GatherException("Error occurred parsing " + docGuid, e,
					GatherResponse.CODE_FILE_ERROR);
			throw ge;
		} finally {
			docFoundCounter += gatherResponse.getDocCount();
			gatherResponse.setDocCount(docFoundCounter); // retrieved doc count
			metaDocFoundCounter += gatherResponse.getDocCount2();
			gatherResponse.setDocCount2(metaDocFoundCounter); // retrieved doc count
			
			if(docbodyWriter != null) {
				try {
					docbodyWriter.close();
				} catch (XMLStreamException e) {
					Log.error("Error closing docbody writer. ");
				}
			}
			if(docbodyOutput != null) {
				try {
					docbodyOutput.close();
				} catch (IOException e) {
					Log.error("Error closing docbody output stream. ");
				}		
			}
			if(metadataWriter != null) {
				try {
					metadataWriter.close();
				} catch (XMLStreamException e) {
					Log.error("Error closing metadata writer. ");
				}
			}
			if(metadataOutput != null) {
				try {
					metadataOutput.close();
				} catch (IOException e) {
					Log.error("Error closing metadata output stream. ");
				}
			} 
			try {
				if(reader != null) {
					reader.close();
				}
			} catch (XMLStreamException e) {
				GatherException ge = new GatherException("Closing reader doc file error", e,
						GatherResponse.CODE_FILE_ERROR);
				throw ge;
			}
		}
	}

	public Integer getTocSequence() {
		return tocSequence;
	}

	public void setTocSequence(Integer tocSequence) {
		this.tocSequence = tocSequence;
	}
}
