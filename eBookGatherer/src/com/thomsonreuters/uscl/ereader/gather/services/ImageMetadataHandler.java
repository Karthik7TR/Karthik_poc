package com.thomsonreuters.uscl.ereader.gather.services;

import org.apache.commons.lang.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

import com.thomsonreuters.uscl.ereader.gather.util.ImgMetadataInfo;

public class ImageMetadataHandler extends XMLFilterImpl {
	
	private static final String DPI = "dpi";
	private static final String HEIGHT = "height";
	private static final String WIDTH = "width";
	private static final String DIM_UNITS = "units";
	private ImgMetadataInfo imgMetadataInfo = new ImgMetadataInfo();
	private StringBuffer charBuffer = null;
	
	public ImgMetadataInfo getImgMetadataInfo() {
		return this.imgMetadataInfo;
	}

	public void setImgMetadataInfo(ImgMetadataInfo imgMetadataInfo) {
		this.imgMetadataInfo = imgMetadataInfo;
	}

	public void startElement(String uri, String localName, String qName, Attributes atts)
			throws SAXException {
		charBuffer = new StringBuffer();
		super.startElement(uri, localName, qName, atts);
	}
	
	public void endElement(String uri, String localName, String qName) throws SAXException {
		try {
			String value = null;

			if (charBuffer != null) {
				value = StringUtils.trim(charBuffer.toString());
				if (qName.contains(DPI)) {
					this.imgMetadataInfo.setDpi(Long.valueOf(value));
				} else if (qName.contains(HEIGHT)) {
					this.imgMetadataInfo.setHeight(Long.valueOf(value));
				} else if (qName.contains(WIDTH)) {
					this.imgMetadataInfo.setWidth(Long.valueOf(value));
				} else if (qName.contains(DIM_UNITS)) {
					this.imgMetadataInfo.setDimUnit(value);
				}
			}

			charBuffer = null;
		} catch (Exception e) {
			String message = "Exception occured during Novus IMGMetadata parsing endElement. The error message is: "
					+ e.getMessage();
			throw new RuntimeException(message, e);
		}
	}
	
	public void characters(char[] ch, int start, int length) throws SAXException {
		if (charBuffer != null) {
			charBuffer.append(new String(ch, start, length));
		}
	}

}
