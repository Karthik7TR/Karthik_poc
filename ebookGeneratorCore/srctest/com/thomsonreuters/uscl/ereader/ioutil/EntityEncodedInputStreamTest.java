/*
* EntityEncodedInputStreamTest
* 
* Created on: Nov 2, 2010 By: u0009398
* 
* Copyright 2010: Thomson Reuters Global Resources. All Rights Reserved.
*
* Proprietary and Confidential information of TRGR. 
* Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited.
*/
package com.thomsonreuters.uscl.ereader.ioutil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import junit.framework.TestCase;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * A test class for EntityEncodedInputStream.
 *
 * @author bmartell
 * @version 1.0, Nov 10, 2003.
 */
public class EntityEncodedInputStreamTest extends TestCase
{
    public EntityEncodedInputStreamTest(String s)
    {
        super(s);
    }

    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(EntityEncodedInputStreamTest.class);
    }


    public void testPi1() throws Exception
    {
        String input = "abcd&<?PI a&b ?>&";

        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        EntityEncodedInputStream translateIn = new EntityEncodedInputStream(in);

        String expected = "abcd$#<?PI a&b ?>$#";

        assertEquals(expected, StreamHelper.inputStreamToString(translateIn));
    }

    public void testPi2() throws Exception
    {
        String input = "abcd&<?PI a$b ?>$";

        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        EntityEncodedInputStream translateIn = new EntityEncodedInputStream(in);

        String expected = "abcd$#<?PI a$b ?>$$";

        assertEquals(expected, StreamHelper.inputStreamToString(translateIn));
    }

    public void testAmpersandDollarSign() throws Exception
    {
        String input = "<element>apples &$amp; oranges cost $12.00.</element>";

        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        EntityEncodedInputStream translateIn = new EntityEncodedInputStream(in);

        String expected = "<element>apples $#$$amp; oranges cost $$12.00.</element>";

        assertEquals(expected, StreamHelper.inputStreamToString(translateIn));
    }

    public void testDollarSignAmpersand() throws Exception
    {
        String input = "<element>$#apples $&amp; oranges cost $12.00.</element>";

        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        EntityEncodedInputStream translateIn = new EntityEncodedInputStream(in);

        String expected = "<element>$$#apples $$$#amp; oranges cost $$12.00.</element>";

        assertEquals(expected, StreamHelper.inputStreamToString(translateIn));
    }

    public void testEncodeEntities() throws Exception
    {
        String input = "<element>apples &amp; oranges cost $12.00.</element>";

        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        EntityEncodedInputStream translateIn = new EntityEncodedInputStream(in);

        String expected = "<element>apples $#amp; oranges cost $$12.00.</element>";

        assertEquals(expected, StreamHelper.inputStreamToString(translateIn));
    }

    public void testEncodeEntitiesAtBufferEnd() throws Exception
    {
        String input = "apples oranges cost $";

        byte[] buffer = new byte[21];
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        EntityEncodedInputStream translateIn = new EntityEncodedInputStream(in);

        int bytesRead = translateIn.read(buffer);
        assertTrue(21 == bytesRead);
    }



    public void testOverflowDollarSignReadingOneByteAtATime()
        throws Exception
    {
        String input = "$12";

        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        EntityEncodedInputStream translateIn = new EntityEncodedInputStream(in);


        StringBuilder actual = new StringBuilder();
        int character;
        while ((character = translateIn.read()) > -1)
        {
            actual.append(String.valueOf((char) character));
        }

        assertEquals("$$12", actual.toString());
    }

    public void testWithParser() throws Exception
    {
        String input = "<element>apples &amp; oranges cost $12.00.</element>";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        in = new EntityEncodedInputStream(in);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        EntityDecodedOutputStream decoder = new EntityDecodedOutputStream(out);
        EchoHandler handler = new EchoHandler(decoder);

        XMLReader parser = XMLReaderFactory.createXMLReader();
        parser.setContentHandler(handler);
        parser.parse(new InputSource(in));
        out.close();

        assertEquals('\n' + input, '\n' + out.toString());
    }
}