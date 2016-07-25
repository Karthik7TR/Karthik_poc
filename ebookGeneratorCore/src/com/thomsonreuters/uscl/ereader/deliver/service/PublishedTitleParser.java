/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
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
 import org.apache.log4j.LogManager; import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Parse all title info from proview.
 * 
 * @author U0057241
 * 
 */
public class PublishedTitleParser {

	private Map<String, ProviewTitleContainer> titleMap = new HashMap<String, ProviewTitleContainer>();

	/**
	 * 
	 * @param xml
	 *            all title info from proview
	 * @return Generate a map of the ProviewTitleContainer objects where the key
	 *         is the title id.
	 */
	public Map<String, ProviewTitleContainer> process(String xml) {
		final Logger LOG = LogManager.getLogger(PublishedTitleParser.class);

		try {
			SAXParserFactory parserFactory = SAXParserFactory.newInstance();
			parserFactory.setNamespaceAware(true);

			XMLReader reader = parserFactory.newSAXParser().getXMLReader();
			reader.setContentHandler(new DefaultHandler() {
				private static final String TITLE_TAG = "title";
				private static final String ID = "id";
				private static final String NAME = "name";
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

							if(StringUtils.isNotBlank(value)) {
								proviewTitleInfo.setTitle(value);
							}

							if (titleMap.get(proviewTitleInfo.getTitleId()) == null) {
								titleMap.put(proviewTitleInfo.getTitleId(),
										new ProviewTitleContainer());
							}

							titleMap.get(proviewTitleInfo.getTitleId())
									.getProviewTitleInfos()
									.add(proviewTitleInfo);

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
							proviewTitleInfo.setTitle(atts.getValue(NAME));
							proviewTitleInfo.setVersion(atts.getValue(VERSION));
							proviewTitleInfo.setPublisher(atts
									.getValue(PUBLISHER));
							proviewTitleInfo.setLastupdate(atts
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
			return titleMap;
		} catch (SAXException e) {
			throw new RuntimeException(e);
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
