/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.deliver.service;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.thomsonreuters.uscl.ereader.deliver.service.ProviewGroup.GroupDetails;

/**
 * Parse group information for a single title from ProView.
 * 
 * @author uc209819
 * 
 */
public class ProviewSingleTitleParser {

	private List<GroupDetails> GroupDetailsList = new ArrayList<GroupDetails>();

	/**
	 * 
	 * @param xml single title info from ProView
	 * @return Generate a list of the GroupDetails objects containing all subgroups for the title in ProView.
	 */
	public List<GroupDetails> process(String xml) {
		final Logger LOG = LogManager.getLogger(ProviewSingleTitleParser.class);

		try {
			SAXParserFactory parserFactory = SAXParserFactory.newInstance();
			parserFactory.setNamespaceAware(true);
			XMLReader reader = parserFactory.newSAXParser().getXMLReader();
			reader.setContentHandler(new DefaultHandler() {

				private static final String STATUS_TAG = "status";
				private static final String NAME_TAG = "name";
				private static final String VERSION = "version";
				private static final String LAST_UPDATE = "lastupdate";
				private static final String TITLE = "title";
				private static final String SPLIT_BOOK_NAMING_CONVENTION = " (eBook ";
				private static final String ID = "id";

				private String version;
				private String status;
				private String name;
				private String lastUpdate;
				private String titleId;

				/*
				 * (non-Javadoc)
				 * 
				 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String , java.lang.String, java.lang.String)
				 */
				public void endElement(String uri, String localName, String qName) throws SAXException {
					try {
						if (TITLE.equals(qName)) {
							GroupDetails groupDetails = new GroupDetails();
							groupDetails.setBookStatus(status);
							groupDetails.setBookVersion(version);
							name = StringUtils.substringBeforeLast(name, SPLIT_BOOK_NAMING_CONVENTION);
							groupDetails.setProviewDisplayName(name);
							groupDetails.setTitleId(titleId);
							groupDetails.setLastupdate(lastUpdate);
							String[] setTitleIdWithVersion = { titleId + "/" + version };
							groupDetails.setTitleIdtWithVersionArray(setTitleIdWithVersion);
							GroupDetailsList.add(groupDetails);
						}

					} catch (Exception e) {
						String message = "ProviewSingleTitleParser: Exception occured during parsing endElement. The error message is: " + e
								.getMessage();
						LOG.error(message, e);
						throw new RuntimeException(message, e);
					}
				}

				/*
				 * (non-Javadoc)
				 * 
				 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang .String, java.lang.String, java.lang.String,
				 * org.xml.sax.Attributes)
				 */
				public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
					try {
						if (TITLE.equals(qName)) {
							this.titleId = atts.getValue(ID);
							this.status = atts.getValue(STATUS_TAG);
							this.version = atts.getValue(VERSION);
							this.name = atts.getValue(NAME_TAG);
							this.lastUpdate = atts.getValue(LAST_UPDATE);
						}
					} catch (Exception e) {
						String message = "PublishedTitleParser: Exception  PublishedTitleParser parsing startElement. The error message is: "
								+ e.getMessage();
						LOG.error(message, e);
						throw new RuntimeException(message, e);
					}
				}
			});
			reader.parse(new InputSource(new StringReader(xml)));
			return GroupDetailsList;
		} catch (SAXException e) {
			throw new RuntimeException(e);
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
