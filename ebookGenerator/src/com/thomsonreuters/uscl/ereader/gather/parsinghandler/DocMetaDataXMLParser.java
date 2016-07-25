/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.gather.parsinghandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Date;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

 import org.apache.log4j.LogManager; import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadata;
import com.thomsonreuters.uscl.ereader.util.NormalizationRulesUtil;

public class DocMetaDataXMLParser extends DefaultHandler {
	private static final Logger LOG = LogManager.getLogger(DocMetaDataXMLParser.class);
	
	private final static String MD_ROOT_ID = "n-metadata";
	private final static String MD_UUID = "md.uuid";
	private final static String MD_DOC_FAMILY_UUID = "md.doc.family.uuid";
	private final static String MD_NORMALIZED_CITE = "md.normalizedcite";
	private final static String MD_FIRSTLINE_CITE = "md.first.line.cite";
	private final static String MD_SECONDLINE_CITE = "md.second.line.cite";
	private final static String MD_THIRDLINE_CITE = "md.third.line.cite";
	private final static String MD_LEGACY_ID = "md.legacy.id";
	private final static String MD_DMS_SERIAL = "md.dmsserial";
	private final static String MD_DOC_TYPE_NAME = "md.doctype.name";
	private final static String MD_PUB_ID = "md.pubid";
	private final static String MD_PUB_PAGE = "md.pubpage";
	
	private StringBuffer tempValBuffer;

	// to maintain context
	private DocMetadata docMetadata;
	private String titleId;
	private Long jobInstanceId;
	private String docUuid;
	private String collectionName;
	private boolean processedPubPage = false;
	private boolean processedFirstPubId = false;
	private boolean processedSecondPubId = false;
	private boolean processedThirdPubId = false;
	
	/**
	 * Create factory method to defend against it being created as a Spring bean, which it should not be since it is not thread safe. 
	 */
	public static DocMetaDataXMLParser create() {
		return new DocMetaDataXMLParser();
	}

	private DocMetaDataXMLParser() {
		docMetadata = new DocMetadata();
	}

	public DocMetadata parseDocument(String titleId, Long jobInstanceId,
			String collectionName, File metadataFile) throws Exception {

		InputStream inputStream = new FileInputStream(metadataFile);
		// get a factory
		SAXParserFactory spf = SAXParserFactory.newInstance();
		try {
			// get a new instance of parser
			SAXParser sp = spf.newSAXParser();

			this.titleId = titleId;
			this.jobInstanceId = jobInstanceId;
			this.collectionName = collectionName;

			Reader reader = new InputStreamReader(inputStream, "UTF-8");
			InputSource is = new InputSource(reader);
			is.setEncoding("UTF-8");
			sp.parse(is, this);
			// printData();
		} finally {
			inputStream.close();
		}
		return docMetadata;
	}

	/**
	 * Go through the doc metadata and print the contents
	 */
//	private void printData() {
//
//		System.out.println("Values of DocMetadata are being printed out ");
//		System.out.println("Title ID  " + docMetadata.getTitleId());
//		System.out.println("JobInstance Id  " + docMetadata.getJobInstanceId());
//		System.out.println("doc uuid  " + docMetadata.getDocUuid());
//		System.out.println("DOC FAMILY ID  " + docMetadata.getDocFamilyUuid());
//		System.out.println("DOC UUID  " + docMetadata.getDocUuid());
//		System.out.println("DOC Type  " + docMetadata.getDocType());
//		System.out.println("NOrmalized first line cite  "
//				+ docMetadata.getNormalizedFirstlineCite());
//		System.out.println("Serial number  " + docMetadata.getSerialNumber());
//		System.out.println("Find Orig  " + docMetadata.getFindOrig());
//
//	}

	// Event Handlers
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		// reset
		tempValBuffer = new StringBuffer();
		if (qName.equalsIgnoreCase(MD_ROOT_ID)) {
			// create a new instance of doc metadata
			docMetadata = new DocMetadata();
			docMetadata.setTitleId(titleId);
			docMetadata.setJobInstanceId(new Long(jobInstanceId));
			docMetadata.setDocUuid(docUuid);
			docMetadata.setCollectionName(collectionName);
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		tempValBuffer.append(new String(ch, start, length));
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		
		String tempVal = tempValBuffer.toString();

		if (qName.equalsIgnoreCase(MD_NORMALIZED_CITE)) {
			// add normalized first line cite
			
			// Fix for Bug 339843 replace a hyphen with a dash
			// so that the normalized cite matches with one form the
			// document
			tempVal = tempVal.replaceAll("\u2013", "\u002D");
			docMetadata.setNormalizedFirstlineCite(tempVal);
		} else if (qName.equalsIgnoreCase(MD_FIRSTLINE_CITE)){
			docMetadata.setFirstlineCite(tempVal);
		} else if (qName.equalsIgnoreCase(MD_SECONDLINE_CITE)){
			docMetadata.setSecondlineCite(tempVal);
		} else if (qName.equalsIgnoreCase(MD_THIRDLINE_CITE)){
			docMetadata.setThirdlineCite(tempVal);
		} else if (qName.equalsIgnoreCase(MD_UUID)) {
			docMetadata.setDocUuid(tempVal);
		} else if (qName.equalsIgnoreCase(MD_DOC_FAMILY_UUID)) {
			docMetadata.setDocFamilyUuid(tempVal);
		} else if (qName.equalsIgnoreCase(MD_DOC_TYPE_NAME)) {
			docMetadata.setDocType(tempVal);
		} else if (qName.equalsIgnoreCase(MD_LEGACY_ID)) {
			docMetadata.setFindOrig(tempVal);
		} else if (qName.equalsIgnoreCase(MD_DMS_SERIAL)) {
			docMetadata.setSerialNumber(new Long(tempVal));
		} else if (qName.equalsIgnoreCase(MD_PUB_PAGE) && !processedPubPage) {
			String normalizedPubpage = NormalizationRulesUtil.pubPageNormalizationRules(tempVal);
			docMetadata.setFirstlineCitePubpage(normalizedPubpage);
			processedPubPage = true;
		} else if (qName.equalsIgnoreCase(MD_PUB_ID)) {
			
			try {
				Long publicationCode = Long.parseLong(tempVal);
				
				if(!processedFirstPubId) {
					docMetadata.setFirstlineCitePubId(publicationCode);
					processedFirstPubId = true;
				} else if(!processedSecondPubId) {
					docMetadata.setSecondlineCitePubId(publicationCode);
					processedSecondPubId = true;
				} else if(!processedThirdPubId) {
					docMetadata.setThirdlineCitePubId(publicationCode);
					processedThirdPubId = true;
				}
			} catch (NumberFormatException e) {
				//not a valid serial number
        		LOG.debug("Encountered a publicationCode: " + tempVal + " which is not a valid number.");
			}
		}
	}
	
	@Override
	public void endDocument() throws SAXException {
		docMetadata.setLastUpdated(new Date());
	}
}
