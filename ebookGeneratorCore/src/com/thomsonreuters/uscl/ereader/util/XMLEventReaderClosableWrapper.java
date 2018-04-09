package com.thomsonreuters.uscl.ereader.util;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.exception.GatherException;

/**
 * Allows to use try(){} functionality for XMLEventReader.
 */
public class XMLEventReaderClosableWrapper implements AutoCloseable {
    private XMLEventReader reader;

    public XMLEventReaderClosableWrapper(final XMLEventReader reader) {
        this.reader = reader;
    }

    public boolean hasNext() {
        return reader.hasNext();
    }

    public XMLEvent next() {
        return (XMLEvent) reader.next();
    }

    public XMLEvent nextEvent() throws XMLStreamException {
        return reader.nextEvent();
    }

    @Override
    public void close() {
        if (reader != null) {
            try {
                reader.close();
            } catch (final XMLStreamException e) {
                final String message = "Closing reader doc file error";
                throw new RuntimeException(message, new GatherException(message, e, GatherResponse.CODE_FILE_ERROR));
            }
        }
    }
}
