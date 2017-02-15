package com.thomsonreuters.uscl.ereader.ioutil;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * An abstract template for creating InputStreams that have all read methods call the abstract
 * read(byte [], int start, int length) method.
 *
 * @author Ben Martell
 * @version 1.0. Aug 27, 2004
 */
abstract class BaseInputStream extends FilterInputStream
{
    private final byte[] oneByteArray = new byte[1];

    /**
     * @param in an InputStream.
     */
    protected BaseInputStream(final InputStream in)
    {
        super(in);
    }

    /**
     * Reads up to length bytes of data from this input stream into an array of bytes. <br> Note
     * that the while loop is needed since in some applications of inputstreams the read may return
     * zero bytes.
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
    public abstract int read(byte[] buffer, int offset, int length) throws IOException;

    /**
     * Read one byte of data.
     *
     * @return the next byte of data, or -1 if the end of the stream is reached.
     *
     * @throws java.io.IOException on failure.
     */
    @Override
    public int read() throws IOException
    {
        int bytesRead = this.read(oneByteArray, 0, 1);

        while (bytesRead == 0)
        {
            bytesRead = this.read(oneByteArray, 0, 1);
        }

        if (-1 == bytesRead)
        {
            return -1;
        }
        else
        {
            return oneByteArray[0] & 0xFF;
        }
    }

    /**
     * Reads up to byte.length bytes of data from this input stream into an array of bytes.
     *
     * @param b a Byte array of data to read.
     *
     * @return the next byte of data, or -1 if the end of the stream is reached.
     *
     * @throws java.io.IOException on failure.
     */
    @Override
    public int read(final byte[] b) throws IOException
    {
        return this.read(b, 0, b.length);
    }
}
