package com.thomsonreuters.uscl.ereader.gather.metadata.service;

import java.io.IOException;
import java.io.File;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.springframework.stereotype.Service;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.DefaultHandler;

import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadata;

public class DocMetaDataXMLParser extends DefaultHandler{

	private final static String MD_ROOT_ID = "n-metadata";
	private final static String MD_UUID = "md.uuid";
	private final static String MD_DOC_FAMILY_UUID = "md.doc.family.uuid";
	private final static String MD_NORMALIZED_CITE = "md.normalizedcite";
	private final static String MD_LEGACY_ID = "md.legacy.id";
	private final static String MD_INFO_TYPE = "md.infotype";
	
	
	private String tempVal;
	
	private String serialNumber;
	
	//to maintain context
	private DocMetadata docMetadata;
	private String titleId;
	private String jobInstanceId;
	private String docUuid;
	
	
	public DocMetaDataXMLParser(){
		docMetadata = new DocMetadata();
	}
	
	public void runExample() {
		parseDocument("Pirates", 12345, new File("C:\\Users\\U0072938\\Documents\\DocMetaDataSample.xml"));
//		printData();
	}

	public DocMetadata parseDocument(String titleId, Integer jobInstanceId, File metadataFile) {
		
		//get a factory
		SAXParserFactory spf = SAXParserFactory.newInstance();
		try {
		
			//get a new instance of parser
			SAXParser sp = spf.newSAXParser();
			
			this.titleId = titleId;
			this.jobInstanceId = jobInstanceId.toString();
			
			sp.parse(metadataFile, this);
			
			//parse the file and also register this class for call backs
//			sp.parse("C:\\Users\\U0072938\\Documents\\DocMetaDataSample.xml", this);
			
			printData();
			
		}catch(SAXException se) {
			se.printStackTrace();
		}catch(ParserConfigurationException pce) {
			pce.printStackTrace();
		}catch (IOException ie) {
			ie.printStackTrace();
		}
		return docMetadata;
	}

	/**
	 * Go through the doc metadata and print
	 * the contents
	 */
	private void printData(){
		
		System.out.println("Values of DocMetadata are being printed out ");
		System.out.println("Title ID  " + docMetadata.getTitleId());
		System.out.println("JobInstance Id  " + docMetadata.getJobInstanceId());
		System.out.println("doc uuid  " + docMetadata.getDocUuid());
		System.out.println("DOC FAMILY ID  " + docMetadata.getDocFamilyUuid());
		System.out.println("DOC UUID  " + docMetadata.getDocUuid());
		System.out.println("DOC Type  " + docMetadata.getDocType());
		System.out.println("NOrmalized first line cite  " + docMetadata.getNormalizedFirstlineCite());
		System.out.println("Serial number  " + docMetadata.getSerialNumber());
		System.out.println("Find Orig  " + docMetadata.getFindOrig());
		
	}
	

	//Event Handlers
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		//reset
		tempVal = "";
		if(qName.equalsIgnoreCase(MD_ROOT_ID)) {
			//create a new instance of doc metadata
			docMetadata = new DocMetadata();
			docMetadata.setTitleId(titleId);
			docMetadata.setJobInstanceId(new Integer(jobInstanceId));
			docMetadata.setDocUuid(docUuid);
		}
	}
	

	public void characters(char[] ch, int start, int length) throws SAXException {
		tempVal = new String(ch,start,length);
	}
	
	public void endElement(String uri, String localName, String qName) throws SAXException {

		if(qName.equalsIgnoreCase(MD_NORMALIZED_CITE)) {
			//add normalized first line cite
			docMetadata.setNormalizedFirstlineCite(tempVal);
		}else if (qName.equalsIgnoreCase(MD_UUID)) {
			docMetadata.setDocUuid(tempVal);
		}else if (qName.equalsIgnoreCase(MD_DOC_FAMILY_UUID)) {
			docMetadata.setDocFamilyUuid(tempVal);
		}else if (qName.equalsIgnoreCase(MD_INFO_TYPE)) {
			docMetadata.setDocType(tempVal);
		} else if (qName.equalsIgnoreCase(MD_LEGACY_ID)) {
			serialNumber = tempVal;
		}
		
	}
	
	//Event Handlers
	public void endDocument() throws SAXException {
		//reset
		if(docMetadata.getDocType().equalsIgnoreCase("Statute")) {
			docMetadata.setFindOrig(serialNumber);
		} else {
			docMetadata.setSerialNumber(new Integer(serialNumber));
		}
		}

/*	public static void main(String[] args){
		DocMetaDataXMLParser spe = new DocMetaDataXMLParser();
		spe.runExample();
	}*/
	
}




