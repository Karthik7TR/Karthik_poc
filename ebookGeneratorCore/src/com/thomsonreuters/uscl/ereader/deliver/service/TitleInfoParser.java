/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.deliver.service;

import static java.util.Optional.ofNullable;
import static org.springframework.util.Assert.notNull;

import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import com.thomsonreuters.uscl.ereader.common.exception.EBookException;
import com.thomsonreuters.uscl.ereader.proview.Doc;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

@Slf4j
public class TitleInfoParser {

    @Getter
    @NotNull
    private String id;

    @Getter
    @NotNull
    private List<Doc> documents = new LinkedList<>();

    public TitleInfoParser(@NotNull final String xml) {
        notNull(xml, "xml should not be null");
        try {
            final SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
            parser.parse(new InputSource(new StringReader(xml)), new TitleInfoHandler());
        } catch (ParserConfigurationException | SAXException | IOException e) {
            log.error("TitleInfoParser failed to parse response", e);
            throw new EBookException("TitleInfoParser failed to parse response", e);
        }
    }

    private class TitleInfoHandler extends DefaultHandler {
        private static final String TITLE_TAG = "title";
        private static final String DOC_TAG = "doc";
        private static final String ID = "id";
        private static final String SRC = "src";

        @Override
        public void startElement(
            final String uri,
            final String localName,
            final String qName,
            final Attributes attributes) throws SAXException {
            if (TITLE_TAG.equals(qName)) {
                id = ofNullable(attributes.getValue(ID)).orElseThrow(() -> new SAXException("title tag must have id attribute"));
            } else if (DOC_TAG.equals(qName)) {
                final String id = attributes.getValue(ID);
                final String src = attributes.getValue(SRC);
                if (id == null || src == null) {
                    throw new SAXException("doc tag must have id and src attributes");
                }
                documents.add(new Doc(id, src, 0, null));
            }
        }
    }
}
