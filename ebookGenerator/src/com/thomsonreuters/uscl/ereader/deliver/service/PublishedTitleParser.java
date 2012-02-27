/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.deliver.service;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Parse all title info from proview. Generate a map of the ProviewTitleInfo objects where the key is the title id.
 * process method must be called wit proview AllPublishedTitles response string.
 * @author U0057241
 *
 */
public class PublishedTitleParser {

	private Map<String, ProviewTitleInfo> titleMap = new HashMap<String, ProviewTitleInfo>();

	public Map<String, ProviewTitleInfo> getTitleMap() {
		return titleMap;
	}

	public void setTitleMap(Map<String, ProviewTitleInfo> titleMap) {
		this.titleMap = titleMap;
	}

	public void process(String xml) {
		final Logger LOG = Logger.getLogger(PublishedTitleParser.class);

		try {
			SAXParserFactory parserFactory = SAXParserFactory.newInstance();
			parserFactory.setNamespaceAware(true);

			XMLReader reader = parserFactory.newSAXParser().getXMLReader();
			reader.setContentHandler(new DefaultHandler() {
				private static final String TITLE_TAG = "title";
				private static final String ID = "id";
				private static final String VERSION = "version";
				private static final String PUBLISHER = "publisher";
				private static final String LAST_UPDATE = "lastupdate";
				private static final String STATUS = "status";
			
				private StringBuffer charBuffer = null;
				private ProviewTitleInfo proviewTitleInfo;

				/*
				 * (non-Javadoc)
				 * 
				 * @see org.xml.sax.helpers.DefaultHandler#characters(char[],
				 * int, int)
				 */
				public void characters(char[] ch, int start, int length)
						throws SAXException {
					if (charBuffer != null) {
						charBuffer.append(new String(ch, start, length));
					}
				}

				/*
				 * (non-Javadoc)
				 * 
				 * @see
				 * org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String
				 * , java.lang.String, java.lang.String)
				 */
				public void endElement(String uri, String localName,
						String qName) throws SAXException {
					try {
						String value = null;

						if (charBuffer != null) {
							value = StringUtils.trim(charBuffer.toString());
						}

						if (TITLE_TAG.equals(qName)) {

							proviewTitleInfo.setTitle(value);
							titleMap.put(proviewTitleInfo.getTitleId(),
									proviewTitleInfo);

						}

						charBuffer = null;
					} catch (Exception e) {
						String message = "PublishedTitleParser: Exception occured during PublishedTitleParser parsing endElement. The error message is: "
								+ e.getMessage();
						LOG.error(message, e);
						throw new RuntimeException(message, e);
					}
				}

				/*
				 * (non-Javadoc)
				 * 
				 * @see
				 * org.xml.sax.helpers.DefaultHandler#startElement(java.lang
				 * .String, java.lang.String, java.lang.String,
				 * org.xml.sax.Attributes)
				 */
				public void startElement(String uri, String localName,
						String qName, Attributes atts) throws SAXException {
					try {
						if (TITLE_TAG.equals(qName))

						{
							charBuffer = new StringBuffer();
							proviewTitleInfo = new ProviewTitleInfo();

							proviewTitleInfo.setTitleId(atts.getValue(ID));
							proviewTitleInfo.setVesrion(atts.getValue(VERSION));
							proviewTitleInfo.setPublisher(atts
									.getValue(PUBLISHER));
							proviewTitleInfo.setPublisher(atts
									.getValue(LAST_UPDATE));
							proviewTitleInfo.setStatus(atts.getValue(STATUS));

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
		} catch (SAXException e) {
			throw new RuntimeException(e);
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
