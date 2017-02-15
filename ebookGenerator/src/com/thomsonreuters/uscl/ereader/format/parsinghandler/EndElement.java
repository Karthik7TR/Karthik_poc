package com.thomsonreuters.uscl.ereader.format.parsinghandler;

/**
 * This object represent the buffered up character of an XML tag and is used to save off buffered up tags.
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public class EndElement extends ParserEvent
{
    private String uri;
    private String localName;
    private String qName;

    public EndElement(final String uri, final String localName, final String qName)
    {
        super(ParserEvent.END_EVENT);

        this.uri = uri;
        this.localName = localName;
        this.qName = qName;
    }

    public String getUri()
    {
        return uri;
    }

    public String getLocalName()
    {
        return localName;
    }

    public String getQName()
    {
        return qName;
    }
}
