/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.deliver.service;

import static org.springframework.util.Assert.notNull;

import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import com.thomsonreuters.uscl.ereader.proview.Doc;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class TitleInfoParser {
    private static final Logger LOG = LogManager.getLogger(TitleInfoParser.class);

    @NotNull
    private List<Doc> docs = new LinkedList<>();

    @NotNull
    public List<Doc> getDocuments(@NotNull final String xml) throws ProviewException {
        notNull(xml, "xml should not be null");
        try {
            final SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
            parser.parse(new InputSource(new StringReader(xml)), new TitleInfoHandler());
            return docs;
        } catch (ParserConfigurationException | SAXException | IOException e) {
            LOG.error("TitleInfoParser failed to parse response", e);
            throw new ProviewException("TitleInfoParser failed to parse response", e);
        }
    }

    private class TitleInfoHandler extends DefaultHandler {
        private static final String DOC_TAG = "doc";
        private static final String ID = "id";
        private static final String SRC = "src";

        @Override
        public void startElement(
            final String uri,
            final String localName,
            final String qName,
            final Attributes attributes) throws SAXException {
            if (DOC_TAG.equals(qName)) {
                final String id = attributes.getValue(ID);
                final String src = attributes.getValue(SRC);
                if (id == null || src == null) {
                    throw new SAXException("doc tag must have id and src attributes");
                }
                docs.add(new Doc(id, src, 0, null));
            }
        }
    }
}
