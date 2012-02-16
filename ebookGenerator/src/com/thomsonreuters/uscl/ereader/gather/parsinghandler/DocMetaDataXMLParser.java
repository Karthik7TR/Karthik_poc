package com.thomsonreuters.uscl.ereader.gather.parsinghandler;

import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import java.sql.Timestamp;
import java.util.Calendar;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.InputSource;

import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadata;

public class DocMetaDataXMLParser extends DefaultHandler {

	private final static String MD_ROOT_ID = "n-metadata";
	private final static String MD_UUID = "md.uuid";
	private final static String MD_DOC_FAMILY_UUID = "md.doc.family.uuid";
	private final static String MD_NORMALIZED_CITE = "md.normalizedcite";
	private final static String MD_LEGACY_ID = "md.legacy.id";
	private final static String MD_DMS_SERIAL = "md.dmsserial";
	private final static String MD_DOC_TYPE_NAME = "md.doctype.name";

	private String tempVal;
	private String serialNumber;
	private String findOrig;

	// to maintain context
	private DocMetadata docMetadata;
	private String titleId;
	private String jobInstanceId;
	private String docUuid;
	private String collectionName;
	private String tocSequenceNum;

	public DocMetaDataXMLParser() {
		docMetadata = new DocMetadata();
	}

	public DocMetadata parseDocument(String titleId, Integer jobInstanceId,
			String collectionName, File metadataFile, String tocSequenceNum) {

		// get a factory
		SAXParserFactory spf = SAXParserFactory.newInstance();
		try {

			// get a new instance of parser
			SAXParser sp = spf.newSAXParser();

			this.titleId = titleId;
			this.jobInstanceId = jobInstanceId.toString();
			this.collectionName = collectionName;
			this.tocSequenceNum = tocSequenceNum;

			InputStream inputStream = new FileInputStream(metadataFile);
			Reader reader = new InputStreamReader(inputStream, "UTF-8");
			InputSource is = new InputSource(reader);
			is.setEncoding("UTF-8");
			sp.parse(is, this);
			// printData();

		} catch (SAXException se) {
			se.printStackTrace();
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (IOException ie) {
			ie.printStackTrace();
		}
		return docMetadata;
	}

	/**
	 * Go through the doc metadata and print the contents
	 */
	private void printData() {

		System.out.println("Values of DocMetadata are being printed out ");
		System.out.println("Title ID  " + docMetadata.getTitleId());
		System.out.println("JobInstance Id  " + docMetadata.getJobInstanceId());
		System.out.println("doc uuid  " + docMetadata.getDocUuid());
		System.out.println("DOC FAMILY ID  " + docMetadata.getDocFamilyUuid());
		System.out.println("DOC UUID  " + docMetadata.getDocUuid());
		System.out.println("DOC Type  " + docMetadata.getDocType());
		System.out.println("NOrmalized first line cite  "
				+ docMetadata.getNormalizedFirstlineCite());
		System.out.println("Serial number  " + docMetadata.getSerialNumber());
		System.out.println("Find Orig  " + docMetadata.getFindOrig());

	}

	// Event Handlers
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		// reset
		tempVal = "";
		if (qName.equalsIgnoreCase(MD_ROOT_ID)) {
			// create a new instance of doc metadata
			docMetadata = new DocMetadata();
			docMetadata.setTitleId(titleId);
			docMetadata.setJobInstanceId(new Integer(jobInstanceId));
			docMetadata.setDocUuid(docUuid);
			docMetadata.setCollectionName(collectionName);
		}
	}

	public void characters(char[] ch, int start, int length)
			throws SAXException {
		tempVal = new String(ch, start, length);
	}

	public void endElement(String uri, String localName, String qName)
			throws SAXException {

		if (qName.equalsIgnoreCase(MD_NORMALIZED_CITE)) {
			// add normalized first line cite
			docMetadata.setNormalizedFirstlineCite(tempVal);
		} else if (qName.equalsIgnoreCase(MD_UUID)) {
			docMetadata.setDocUuid(tempVal);
		} else if (qName.equalsIgnoreCase(MD_DOC_FAMILY_UUID)) {
			docMetadata.setDocFamilyUuid(tempVal);
		} else if (qName.equalsIgnoreCase(MD_DOC_TYPE_NAME)) {
			docMetadata.setDocType(tempVal);
		} else if (qName.equalsIgnoreCase(MD_LEGACY_ID)) {
			docMetadata.setFindOrig(tempVal);
		} else if (qName.equalsIgnoreCase(MD_DMS_SERIAL)) {
			docMetadata.setSerialNumber(new Integer(tempVal));
		}

	}

	// Event Handlers
	public void endDocument() throws SAXException {
		docMetadata.setLastUpdated(getCurrentTimeStamp());
	}

	/**
	 * Get the current timestamp
	 * 
	 * @return
	 */
	private Timestamp getCurrentTimeStamp() {

		// create a java calendar instance
		Calendar calendar = Calendar.getInstance();
		return new java.sql.Timestamp(calendar.getTime().getTime());

	}
}
