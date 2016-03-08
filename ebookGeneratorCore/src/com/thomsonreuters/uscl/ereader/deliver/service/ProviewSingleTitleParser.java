package com.thomsonreuters.uscl.ereader.deliver.service;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ProviewSingleTitleParser extends DefaultHandler {
	private String version;
	private String status;
	private String name;
	private String lastUpdate;
	private static final String STATUS_TAG = "status";
	private static final String NAME_TAG = "name";
	private static final String VERSION = "version";
	private static final String LAST_UPDATE = "lastupdate";
	
	private static final Logger LOG = Logger.getLogger(ProviewSingleTitleParser.class);

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(String lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	private StringBuffer charBuffer = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
	 */
	public void characters(char[] ch, int start, int length) throws SAXException {
		if (charBuffer != null) {
			charBuffer.append(new String(ch, start, length));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String ,
	 * java.lang.String, java.lang.String)
	 */
	public void endElement(String uri, String localName, String qName) throws SAXException {
		try {
			String value = null;

			if (charBuffer != null) {
				value = StringUtils.trim(charBuffer.toString());
			}
			if (NAME_TAG.equals(qName)) {
				this.name = value;
			}
			if (STATUS_TAG.equals(qName)) {
				this.status = value;
			}
			if (LAST_UPDATE.equals(qName)) {
				this.lastUpdate = value;
			}
			if (VERSION.equals(qName)) {
				this.version = value;
			}
			
			charBuffer = null;
		} catch (Exception e) {
			String message = "ProviewSingleTitleParser: Exception occured during parsing endElement. The error message is: "
					+ e.getMessage();
			LOG.error(message, e);
			throw new RuntimeException(message, e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang .String,
	 * java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		try {
			if (NAME_TAG.equals(qName) || STATUS_TAG.equals(qName) || LAST_UPDATE.equals(qName) || VERSION.equals(qName)) {
				charBuffer = new StringBuffer();
			}
		} catch (Exception e) {
			String message = "PublishedTitleParser: Exception  PublishedTitleParser parsing startElement. The error message is: "
					+ e.getMessage();
			LOG.error(message, e);
			throw new RuntimeException(message, e);
		}
	}

}
