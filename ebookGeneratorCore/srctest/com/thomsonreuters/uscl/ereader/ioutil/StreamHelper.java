package com.thomsonreuters.uscl.ereader.ioutil;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.apache.commons.io.IOUtils;

/**
 * A utility class for working with streams.
 *
 * @author bmartell
 * @version 1.0, Nov 10, 2003.
 */
public final class StreamHelper
{
    private StreamHelper()
    {
    }

    /**
     * Compares 2 input streams of data.
     *
     * @param one an InputStream
     * @param two an InputStream
     * @param detail a Stringbuffer for holding the difference display if method returns false.
     *
     * @return boolean true if streams contained identical data, false if data is different.
     *
     * @throws java.io.IOException if anything fails.
     */
    public static synchronized boolean compare(final InputStream one, final InputStream two, final StringBuilder detail)
        throws IOException
    {
        boolean equal = false;

        final StringBuilder bufferOne = new StringBuilder(50);
        final StringBuilder bufferTwo = new StringBuilder(50);

        final int bufferSize = bufferOne.capacity();

        final InputStream streamOne = new BufferedInputStream(one, 1024 * 20);
        final InputStream streamTwo = new BufferedInputStream(two, 1024 * 20);

        int byteOne = streamOne.read();
        int byteTwo = streamTwo.read();

        bufferOne.append((char) byteOne);
        bufferTwo.append((char) byteTwo);

        int byteCount = 0;

        while ((byteOne != -1) && (byteOne == byteTwo))
        {
            byteOne = streamOne.read();
            byteTwo = streamTwo.read();
            bufferOne.append((char) byteOne);
            bufferTwo.append((char) byteTwo);
            byteCount++;

            if (byteCount > bufferSize)
            {
                byteCount--;
                bufferOne.delete(0, 1);
                bufferTwo.delete(0, 1);
            }
        }

        if (byteOne == -1)
        {
            equal = true;
        }
        else
        {
            if (byteCount < bufferSize)
            {
                final byte[] buffer = new byte[bufferSize - byteCount];
                int bytesRead;
                bytesRead = streamOne.read(buffer);
                bufferOne.append(new String(buffer, 0, bytesRead));
                bytesRead = streamTwo.read(buffer);
                bufferTwo.append(new String(buffer, 0, bytesRead));
            }
        }

        if (!equal)
        {
            detail.append("expected: ");
            detail.append(" \n").append(bufferOne).append("\n");
            detail.append("but was: ").append("\n").append(bufferTwo);
        }

        IOUtils.closeQuietly(one);
        IOUtils.closeQuietly(two);

        return equal;
    }

    /**
     * Converts InputStream to String.
     *
     * @param input the underlying InputStream
     *
     * @return contents of inputStream.
     *
     * @throws java.io.IOException on failure.
     */
    public static String inputStreamToString(final InputStream input) throws IOException
    {
        final StringBuilder value = new StringBuilder();
        int bytesRead;
        final byte[] buffer = new byte[1024];

        while ((bytesRead = input.read(buffer, 0, buffer.length)) >= 0)
        {
            value.append(new String(buffer, 0, bytesRead));
        }

        input.close();

        return value.toString();
    }

    /**
     * Converts Reader to String.
     *
     *
     * @param input the underlying InputStream
     *
     * @return contents of input.
     *
     * @throws java.io.IOException on failure.
     */
    public static String readerToString(final Reader input) throws IOException
    {
        final StringBuilder value = new StringBuilder();
        int bytesRead;
        final char[] buffer = new char[1024];

        while ((bytesRead = input.read(buffer, 0, buffer.length)) >= 0)
        {
            value.append(buffer, 0, bytesRead);
        }

        return value.toString();
    }
}
