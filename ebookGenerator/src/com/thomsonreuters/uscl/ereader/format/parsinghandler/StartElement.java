package com.thomsonreuters.uscl.ereader.format.parsinghandler;

import org.xml.sax.Attributes;

/**
 * This object represent one XML start element and used to save off buffered up tags.
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public class StartElement extends ParserEvent {
    private String uri;
    private String localName;
    private String qName;
    private Attributes atts;

    public StartElement(final String uri, final String localName, final String qName, final Attributes atts) {
        super(ParserEvent.START_EVENT);

        this.uri = uri;
        this.localName = localName;
        this.qName = qName;
        this.atts = atts;
    }

    public String getUri() {
        return uri;
    }

    public String getLocalName() {
        return localName;
    }

    public String getQName() {
        return qName;
    }

    public Attributes getAtts() {
        return atts;
    }
}
