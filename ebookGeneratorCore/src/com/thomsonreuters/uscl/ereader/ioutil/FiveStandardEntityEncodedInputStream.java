/*
* FiveStandardEntityEncodedInputStream
* 
* Created on: 1/6/11 By: u0009398
* 
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
*
* Proprietary and Confidential information of TRGR. 
* Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited.
*/
package com.thomsonreuters.uscl.ereader.ioutil;


import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;


/**
 * A helper class for encoding only the five standard entities so that the entities may be ignored
 * by standard xml parsers. Enities are encoded as thusly:  &amp;some_entity; --&gt; $#some_entity;
 * All &amp; are replaced with $#.  $ in the data are replaced with $$.
 *
 * @author bmartell
 * @version 1.0, March 30, 2005.
 */
public class FiveStandardEntityEncodedInputStream extends BaseInputStream
{
    private String overflow = "";
    private boolean eosInEntityread = false;
    private Set<String> set = new HashSet<String>(5);
    private boolean inProcessingInstruction = false;
    private char previousCharacter = '.';   //initialize to anything but '<' or '?'


    /**
     * Creates an EntityEncodedInputStream.
     *
     * @param in the underlying InputStream ( of xml )
     */
    public FiveStandardEntityEncodedInputStream(InputStream in)
    {
        super(new BufferedInputStream(in, 1024));

        set.add("lt;");
        set.add("gt;");
        set.add("amp;");
        set.add("apos;");
        set.add("quot;");
    }

    /**
     * Reads up to length bytes of data from this input stream into an array of bytes. This read
     * will check for entities and identify any that are the standard 5 and will encode them in the
     * format: $#entity; so they can be ignored by a parser. To make this work any $ encountered
     * will be escapped with a second $.
     * <p/>
     * <br> The data should be passed thru the EntityDecodedInput (or Output) stream(s) before it is
     * used to correct the encoded data.
     *
     * @param buffer - a Byte array of data.
     * @param offset - an int representing offset location in buffer to begin read.
     * @param length - an int representing the number of bytes to read.
     *
     * @return the number of bytes read or -1 if end of stream reached.
     *
     * @throws java.io.IOException on failure.
     */
    public int read(byte[] buffer, int offset, int length)
        throws IOException
    {
        int bytesRead = 0;
        boolean endOfStream = false;

        while ((bytesRead < length) && !endOfStream)
        {
            while ((overflow.length() > 0) && (bytesRead < length))
            {
                buffer[offset + bytesRead] = (byte) overflow.charAt(0);
                this.previousCharacter = overflow.charAt(0);
                bytesRead++;

                if (overflow.length() > 1)
                {
                    overflow = overflow.substring(1);
                }
                else
                {
                    overflow = "";

                    if (eosInEntityread)
                    {
                        endOfStream = true;
                    }
                }
            }

            if (!(bytesRead < length))
            {
                break;
            }

            byte character = (byte) in.read();

            switch (character)
            {

                case '&':
                    overflow = readEntity();

                    if (inProcessingInstruction || !set.contains(overflow))
                    {
                        //no encoding in processing instructions
                        buffer[offset + bytesRead] = (byte) '&';
                        bytesRead++;
                    }
                    else
                    {
                        buffer[offset + bytesRead] = (byte) '$';
                        bytesRead++;

                        //NOTE: potential snag in that 'previousCharacter' will register '&' instead of '$' -- but neither is important at this time

                        if (!(bytesRead < length))
                        {
                            overflow = "#" + overflow;
                        }
                        else
                        {
                            buffer[offset + bytesRead] = (byte) '#';
                            bytesRead++;
                        }
                    }

                    break;

                case '$':
                    if (inProcessingInstruction)
                    {
                        //no encoding in processing instructions
                        buffer[offset + bytesRead] = (byte) '$';
                        bytesRead++;
                    }
                    else
                    {
                        buffer[offset + bytesRead] = (byte) '$';
                        bytesRead++;

                        if (!(bytesRead < length))
                        {
                            overflow = overflow + "$";
                        }
                        else
                        {
                            buffer[offset + bytesRead] = (byte) '$';
                            bytesRead++;
                        }
                    }

                    break;

                case '?':
                    if (!inProcessingInstruction && this.previousCharacter == '<')
                    {
                        this.inProcessingInstruction = true;  //hit the sequence "<?"
                    }

                    buffer[offset + bytesRead] = (byte) '?';

                    bytesRead++;

                    break;

                case '>':

                    if (inProcessingInstruction && this.previousCharacter == '?')
                    {
                        this.inProcessingInstruction = false; //hit the sequence "?>"
                    }

                    buffer[offset + bytesRead] = (byte) '>';

                    bytesRead++;

                    break;

                case -1:
                    endOfStream = true;

                    break;

                default:

                    try
                    {
                        buffer[offset + bytesRead] = character;
                    }
                    catch (Exception t)
                    {
                        throw new StreamException(t);
                    }

                    bytesRead++;
            }

            this.previousCharacter =
                (char) character;  //needed to detect processing instruction start/end tags

        }

        if (endOfStream && (0 == bytesRead))
        {
            bytesRead = -1;
        }

        return bytesRead;
    }

    private String readEntity() throws IOException
    {
        final StringBuilder value = new StringBuilder();
        int byteRead;
        do
        {
            byteRead = in.read();

            if (byteRead != -1)
            {
                value.append((char) byteRead);
            }
        }
        while ((byteRead != ';') && (byteRead != ' ') && (byteRead != -1));

        if (byteRead == -1)
        {
            eosInEntityread = true;
        }

        return value.toString();
    }
}
