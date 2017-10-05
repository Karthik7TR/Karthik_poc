package com.thomsonreuters.uscl.ereader.ioutil;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * A helper class for decoding entities during writes to the outputstream. <p/> <p> Ecoded Enities
 * are decoded as thusly: $#some_entity; --&gt &amp;some_entity; </p> <p/> <p> All $# are replaced
 * with &amp;.   <br>$$ in the data are replaced with $. </p> <p/> <p> Note that this class should
 * only be used on data that has been entity encoded. </p>
 *
 * @author bmartell
 * @version 1.0, Nov 7, 2003.
 */
public class EntityDecodedOutputStream extends FilterOutputStream {
    private byte leftoverByte;
    private byte[] oneByteArray;
    private boolean allowExistingEntities;
    private boolean inProcessingInstruction;
    private char previousCharacter = '.';

    /**
     * Constructor.
     *
     * @param out the underlying OutputStream ( of xml )
     */
    public EntityDecodedOutputStream(final OutputStream out) {
        super(out);
        oneByteArray = new byte[1];
        leftoverByte = -1;
    }

    /**
     * Constructor.
     *
     * @param out the underlying output stream of xml
     * @param allowExistingEntities if set to false (default), any entity characters (i.e. '&')
     *        will cause an IllegalArgumentException. true will allow those to pass through.   You
     *        should only set this if you are processing a stream with XSLTs that output entities
     *        already
     */
    public EntityDecodedOutputStream(final OutputStream out, final boolean allowExistingEntities) {
        this(out);
        this.allowExistingEntities = allowExistingEntities;
    }

    /**
     * Validates that end of data stream was entity encoded.
     *
     * @throws java.io.IOException on failure.
     */
    @Override
    public void close() throws IOException {
        if ('$' == leftoverByte && !allowExistingEntities) {
            throw new IllegalArgumentException(
                "at close an extra $ was left in the buffer! " + "input does not appear to be entity encoded");
        } else if ('$' == leftoverByte && allowExistingEntities) {
            out.write('$');
        }

        super.close();
    }

    /**
     * Writes b.length bytes to this output stream.
     *
     * @param b the data to be written
     *
     * @throws java.io.IOException on failure.
     */
    @Override
    public void write(final byte[] b) throws IOException {
        this.write(b, 0, b.length);
    }

    /**
     * Writes len bytes from the specified byte array starting at offset off to this output stream.
     *
     * @param buffer the data to be written.
     * @param offset the start offset in the data.
     * @param length the number of bytes to write.
     *
     * @throws java.io.IOException on failure.
     */
    @Override
    public void write(final byte[] buffer, final int offset, final int length) throws IOException {
        final int endIndex = length + offset;

        for (int index = offset; index < endIndex; index++) {
            switch (buffer[index]) {
            case '&':

                if (!inProcessingInstruction && !allowExistingEntities) {
                    final String subString = errorRelevantSubstring(buffer, offset, endIndex, index);
                    final String message = "Detected entity in the input "
                        + "when no unencoded entitites are allowed. Here is the input ..."
                        + subString
                        + "...";
                    throw new IllegalArgumentException(message);
                } else {
                    out.write(buffer[index]);
                }

                break;

            case '#':

                if (!inProcessingInstruction && '$' == leftoverByte) {
                    out.write('&');
                    leftoverByte = -1;
                } else {
                    out.write(buffer[index]);
                }

                break;

            case '$':
                if (inProcessingInstruction) {
                    out.write('$');
                } else {
                    if ('$' == leftoverByte) {
                        out.write('$');
                        leftoverByte = -1;
                    } else if ((index + 1) != (offset + length)) {
                        final byte peekAhead = buffer[index + 1];

                        if (peekAhead == '$') {
                            out.write('$');
                            index++;
                        } else if (peekAhead == '#') {
                            out.write('&');
                            index++;
                        } else if (allowExistingEntities) {
                            out.write('$');
                        } else {
                            final String subString = errorRelevantSubstring(buffer, offset, endIndex, index);
                            final String message =
                                "Input does not appear to be entity encoded. Expected either a $ or # to follow.  Buffer =..."
                                    + subString
                                    + "...";
                            throw new IllegalArgumentException(message);
                        }
                    } else {
                        leftoverByte = (byte) '$';
                    }
                }

                break;

            case '?':

                if (!inProcessingInstruction && previousCharacter == '<') {
                    inProcessingInstruction = true; //hit the sequence "<?"
                }

                out.write('?');

                break;

            case '>':

                if (inProcessingInstruction && previousCharacter == '?') {
                    inProcessingInstruction = false; //hit the sequence "?>"
                }

                out.write('>');

                break;

            default:
                out.write(buffer[index]);

                break;
            }

            previousCharacter = (char) buffer[index];
        }
    }

    /**
     * Returns portion of string relevant to error.
     * This prevents dumping a possibly huge buffer into the log.
     *
     * @param buffer input.
     * @param offset into buffer.
     * @param endIndex marks end of buffer.
     * @param index current position in buffer.
     * @return 40 chars on either side of the current index.
     */
    private String errorRelevantSubstring(final byte[] buffer, final int offset, final int endIndex, final int index) {
        int leftIndex = index - 40;
        if (leftIndex < 0) {
            leftIndex = 0;
        }
        int rightIndex = index + 40;
        if (rightIndex > endIndex) {
            rightIndex = endIndex;
        }
        return new String(buffer, offset + leftIndex, rightIndex - leftIndex);
    }

    /**
     * Writes the specified byte to this output stream.
     *
     * @param b the data to be written.
     *
     * @throws java.io.IOException on failure.
     */
    @Override
    public void write(final int b) throws IOException {
        oneByteArray[0] = (byte) b;
        this.write(oneByteArray, 0, 1);
    }
}
