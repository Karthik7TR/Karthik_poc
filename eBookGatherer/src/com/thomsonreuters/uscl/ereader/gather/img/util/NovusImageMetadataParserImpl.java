/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.gather.img.util;

import java.io.StringReader;

import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.thomsonreuters.uscl.ereader.gather.util.ImgMetadataInfo;

public class NovusImageMetadataParserImpl implements NovusImageMetadataParser {
	private static final Logger Log = LogManager.getLogger(NovusImageMetadataParserImpl.class);

	@Override
	@NotNull
	public ImgMetadataInfo parse(@NotNull String metadata) {
		SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		parserFactory.setNamespaceAware(true);
		try {
			XMLReader reader = parserFactory.newSAXParser().getXMLReader();
			ImageMetadataHandler imageMetadataHandler = new ImageMetadataHandler();
			reader.setContentHandler(imageMetadataHandler);
			reader.parse(new InputSource(new StringReader(metadata)));
			return imageMetadataHandler.getImgMetadataInfo();
		} catch (Exception e) {
			String msg = "Cannot parse image metadata from Novus";
			Log.error(msg, e);
			throw new RuntimeException(msg, e);
		}
	}

}
