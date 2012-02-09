/*
* FiveStandardEntityEncodedInputStreamTest
* 
* Created on: 1/13/11 By: u0009398
* 
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
*
* Proprietary and Confidential information of TRGR. 
* Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited.
*/
package com.thomsonreuters.uscl.ereader.ioutil;


import java.io.ByteArrayInputStream;
import junit.framework.TestCase;
import org.junit.Test;

/**
 * TestCase for FiveStandardEntityEncodedInputStream.
 *
 * @author bmartell
 * @version 1.0, Mar 30, 2005
 */

public class FiveStandardEntityEncodedInputStreamTest extends TestCase
{
    @Test
    public void test5StandardEntitiesOnlyEncoded() throws Exception
    {
        String input =
            "<element>apple&apos;s &amp; oranges&gt; &myentity;cost&lt;  &quot;$12.00.&quot;</element>";

        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        FiveStandardEntityEncodedInputStream translateIn =
            new FiveStandardEntityEncodedInputStream(in);

        String expected =
            "<element>apple$#apos;s $#amp; oranges$#gt; &myentity;cost$#lt;  $#quot;$$12.00.$#quot;</element>";

        assertEquals(expected, StreamHelper.inputStreamToString(translateIn));
    }

    @Test
    public void test5StandardEntitiesOnlyEncodedWhenCommentContainsAmp() throws Exception
    {
        String input =
            "<!-- foo & bar --><element>apple&apos;s &amp; oranges&gt; &myentity;cost&lt;  &quot;$12.00.&quot;</element>";

        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        FiveStandardEntityEncodedInputStream translateIn =
            new FiveStandardEntityEncodedInputStream(in);

        String expected =
            "<!-- foo & bar --><element>apple$#apos;s $#amp; oranges$#gt; &myentity;cost$#lt;  $#quot;$$12.00.$#quot;</element>";

        assertEquals(expected, StreamHelper.inputStreamToString(translateIn));
    }

    @Test
    public void testEncodeEntitiesAtBufferEnd() throws Exception
    {
        String input = "apples oranges cost $";

        byte[] buffer = new byte[21];
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        FiveStandardEntityEncodedInputStream translateIn =
            new FiveStandardEntityEncodedInputStream(in);

        int bytesRead = translateIn.read(buffer);
        assertTrue(21 == bytesRead);
    }

    @Test
    public void testOverflowDollarSignReadingOneByteAtATime()
        throws Exception
    {
        String input = "$12";

        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        FiveStandardEntityEncodedInputStream translateIn =
            new FiveStandardEntityEncodedInputStream(in);

        int character;
        StringBuffer actual = new StringBuffer();

        while ((character = translateIn.read()) > -1)
        {
            actual.append(String.valueOf((char) character));
        }

        assertEquals("$$12", actual.toString());
    }

    @Test
    public void testPi1() throws Exception
    {
        String input = "ab&arf;cd&lt;<?PI a&lt;b ?>&arf;cd&lt;";

        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        FiveStandardEntityEncodedInputStream translateIn =
            new FiveStandardEntityEncodedInputStream(in);

        String expected = "ab&arf;cd$#lt;<?PI a&lt;b ?>&arf;cd$#lt;";

        assertEquals(expected, StreamHelper.inputStreamToString(translateIn));
    }

    @Test
    public void testPi2() throws Exception
    {
        String input = "ab&arf;cd&lt;<?PI a$b ?>$";

        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        FiveStandardEntityEncodedInputStream translateIn =
            new FiveStandardEntityEncodedInputStream(in);

        String expected = "ab&arf;cd$#lt;<?PI a$b ?>$$";

        assertEquals(expected, StreamHelper.inputStreamToString(translateIn));
    }

    public FiveStandardEntityEncodedInputStreamTest()
    {
    }
}

