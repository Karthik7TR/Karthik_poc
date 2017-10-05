package com.thomsonreuters.uscl.ereader.format.parsinghandler;

/**
 * Base class that defines the order the event was fired off and the type of event.
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public class ParserEvent {
    public static final int START_EVENT = 0;
    public static final int CHAR_EVENT = 1;
    public static final int END_EVENT = 2;

    private int eventType;

    public ParserEvent(final int eventType) {
        this.eventType = eventType;
    }

    public int getEventType() {
        return eventType;
    }
}
