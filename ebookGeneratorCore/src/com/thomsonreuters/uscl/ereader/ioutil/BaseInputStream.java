/*
 * BaseInputStream.java
 *
 * Created on: Oct 30, 2010 by: davicar
 *
 * Copyright 2010 Thomson Reuters Global Resources.  All Rights Reserved.
 * 
 * Proprietary and Confidential information of TRGR.
 * Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited.
 */
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
    protected BaseInputStream(InputStream in)
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
    public abstract int read(byte[] buffer, int offset, int length)
        throws IOException;

    /**
     * Read one byte of data.
     *
     * @return the next byte of data, or -1 if the end of the stream is reached.
     *
     * @throws java.io.IOException on failure.
     */
    public int read() throws IOException
    {
        int bytesRead = this.read(this.oneByteArray, 0, 1);

        while (bytesRead == 0)
        {
            bytesRead = this.read(this.oneByteArray, 0, 1);
        }

        if (-1 == bytesRead)
        {
            return -1;
        }
        else
        {
            return (int) this.oneByteArray[0] & 0xFF;
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
    public int read(byte[] b) throws IOException
    {
        return this.read(b, 0, b.length);
    }
}
