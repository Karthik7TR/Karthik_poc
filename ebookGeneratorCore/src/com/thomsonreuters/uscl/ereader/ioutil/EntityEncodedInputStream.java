package com.thomsonreuters.uscl.ereader.ioutil;

import java.io.IOException;
import java.io.InputStream;

/**
 * A helper class for encoding entities so that the entities may be ignored by standard xml
 * parsers.
 *
 * <p>
 * Enities are encoded as thusly:  &amp;some_entity; --&gt; $#some_entity;
 * </p>
 *
 * <p>
 * All &amp; are replaced with $#.  $ in the data are replaced with $$.
 * </p>
 *
 * @author bmartell

 */
public class EntityEncodedInputStream extends BaseInputStream {
    private boolean hasOverflow;
    private char overflowChar;
    private boolean inProcessingInstruction;
    private char previousCharacter = '.'; //initialize to anything but '<' or '?'

    /**
     * Creates an EntityEncodedInputStream.
     *
     * @param in the underlying InputStream ( of xml )
     */
    public EntityEncodedInputStream(final InputStream in) {
        super(in);
    }

    /**
     * Reads up to length bytes of data from this input stream into an array of bytes.
     *
     * @param buffer - a Byte array of data.
     * @param offset - an int representing offset location in buffer to begin read.
     * @param length - an int representing the number of bytes to read.
     *
     * @return the number of bytes read or -1 if end of stream reached.
     *
     * @throws java.io.IOException on failure.
     */
    @Override
    public int read(final byte[] buffer, final int offset, final int length) throws IOException {
        int bytesRead = 0;
        boolean endOfStream = false;

        while ((bytesRead < length) && !endOfStream) {
            if (hasOverflow) {
                buffer[offset + bytesRead] = (byte) overflowChar;
                bytesRead++;
                hasOverflow = false;

                if (!(bytesRead < length)) {
                    break;
                }
            }

            final byte character = (byte) in.read();

            switch (character) {
            case '&':
                if (inProcessingInstruction) {
                    //no encoding in processing instructions
                    buffer[offset + bytesRead] = (byte) '&';
                    bytesRead++;
                } else {
                    buffer[offset + bytesRead] = (byte) '$';
                    bytesRead++;

                    //NOTE: potential snag in that 'previousCharacter' will register '&' instead of '$' -- but neither is important at this time

                    if (!(bytesRead < length)) {
                        hasOverflow = true;
                        overflowChar = '#';
                    } else {
                        buffer[offset + bytesRead] = (byte) '#';
                        bytesRead++;
                    }
                }

                break;

            case '$':
                if (inProcessingInstruction) {
                    //no encoding in processing instructions
                    buffer[offset + bytesRead] = (byte) '$';
                    bytesRead++;
                } else {
                    buffer[offset + bytesRead] = (byte) '$';
                    bytesRead++;

                    if (!(bytesRead < length)) {
                        hasOverflow = true;
                        overflowChar = '$';
                    } else {
                        buffer[offset + bytesRead] = (byte) '$';
                        bytesRead++;
                    }
                }

                break;

            case '?':
                if (!inProcessingInstruction && previousCharacter == '<') {
                    inProcessingInstruction = true; //hit the sequence "<?"
                }

                buffer[offset + bytesRead] = (byte) '?';

                bytesRead++;

                break;

            case '>':

                if (inProcessingInstruction && previousCharacter == '?') {
                    inProcessingInstruction = false; //hit the sequence "?>"
                }

                buffer[offset + bytesRead] = (byte) '>';

                bytesRead++;

                break;

            case -1:
                endOfStream = true;

                break;

            default:

                try {
                    buffer[offset + bytesRead] = character;
                } catch (final Exception t) {
                    throw new StreamException(t);
                }

                bytesRead++;
            }

            previousCharacter = (char) character; //needed to detect processing instruction start/end tags
        }

        if (endOfStream && (0 == bytesRead)) {
            bytesRead = -1;
        }

        return bytesRead;
    }
}
