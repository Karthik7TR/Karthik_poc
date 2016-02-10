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

public class ProviewGroupsParser {
	private Map<String, ProviewGroup> groupMap = new HashMap<String, ProviewGroup>();

	/**
	 * 
	 * @param xml
	 *            all title info from proview
	 * @return Generate a map of the ProviewGroupContainer objects where the key
	 *         is the title id.
	 */
	public Map<String, ProviewGroup> process(String xml) {
		final Logger LOG = Logger.getLogger(PublishedTitleParser.class);

		try {
			SAXParserFactory parserFactory = SAXParserFactory.newInstance();
			parserFactory.setNamespaceAware(true);

			XMLReader reader = parserFactory.newSAXParser().getXMLReader();
			reader.setContentHandler(new DefaultHandler() {
				private static final String GROUP_TAG = "group";
				private static final String NAME_TAG = "name";
				private static final String HEAD_TITLE = "headtitle";
				private static final String ID = "id";
				private static final String VERSION = "version";

				private StringBuffer charBuffer = null;
				private ProviewGroup proviewGroup;

				/*
				 * (non-Javadoc)
				 * 
				 * @see org.xml.sax.helpers.DefaultHandler#characters(char[],
				 * int, int)
				 */
				public void characters(char[] ch, int start, int length) throws SAXException {
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
				public void endElement(String uri, String localName, String qName) throws SAXException {
					try {
						String value = null;
						
						if (charBuffer != null) {
							value = StringUtils.trim(charBuffer.toString());
						}
						if (HEAD_TITLE.equals(qName)) {
							value = StringUtils.substringBeforeLast(value, "/");
							proviewGroup.setTitleId(value);
						}
						if (NAME_TAG.equals(qName)) {
							proviewGroup.setGroupName(value);
						}

						if (GROUP_TAG.equals(qName)) {
							if (groupMap.get(proviewGroup.getGroupId()) != null) {
								ProviewGroup oldProviewGroup = groupMap.get(proviewGroup.getTitleId());
								if (oldProviewGroup.getGroupVersion() < proviewGroup.getGroupVersion()) {
									groupMap.put(proviewGroup.getTitleId(), proviewGroup);
								}
							} else {
								groupMap.put(proviewGroup.getTitleId(), proviewGroup);
							}
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
				public void startElement(String uri, String localName, String qName, Attributes atts)
						throws SAXException {
					try {
						if (GROUP_TAG.equals(qName))
						{
							charBuffer = new StringBuffer();
							proviewGroup = new ProviewGroup();
							proviewGroup.setGroupId(atts.getValue(ID));
							String groupVersion = StringUtils.substringAfter(atts.getValue(VERSION), "v");
							proviewGroup.setGroupVersion(new Integer(groupVersion));

						}
						if (HEAD_TITLE.equals(qName)) {
							charBuffer = new StringBuffer();
						}
						if (NAME_TAG.equals(qName)) {
							charBuffer = new StringBuffer();
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
			return groupMap;
		} catch (SAXException e) {
			throw new RuntimeException(e);
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
