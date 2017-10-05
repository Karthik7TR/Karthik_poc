package com.thomsonreuters.uscl.ereader.format.parsinghandler;

/**
 * This object represent the buffered up character of an XML tag and is used to save off buffered up tags.
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public class Characters extends ParserEvent {
    private char[] buf;
    private int offset;
    private int len;

    public Characters(final char[] buf, final int offset, final int len) {
        super(ParserEvent.CHAR_EVENT);

        this.buf = buf;
        this.offset = offset;
        this.len = len;
    }

    public char[] getBuffer() {
        return buf;
    }

    public int getOffset() {
        return offset;
    }

    public int getLength() {
        return len;
    }
}
