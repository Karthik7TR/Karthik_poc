package com.thomsonreuters.uscl.ereader.ioutil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * A test class for EntityEncodedInputStream.
 *
 * @author bmartell
 * @version 1.0, Nov 10, 2003.
 */
public final class EntityEncodedInputStreamTest {
    @Test
    public void testPi1() throws Exception {
        final String input = "abcd&<?PI a&b ?>&";

        final ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        final EntityEncodedInputStream translateIn = new EntityEncodedInputStream(in);

        final String expected = "abcd$#<?PI a&b ?>$#";

        assertEquals(expected, StreamHelper.inputStreamToString(translateIn));
    }

    @Test
    public void testPi2() throws Exception {
        final String input = "abcd&<?PI a$b ?>$";

        final ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        final EntityEncodedInputStream translateIn = new EntityEncodedInputStream(in);

        final String expected = "abcd$#<?PI a$b ?>$$";

        assertEquals(expected, StreamHelper.inputStreamToString(translateIn));
    }

    @Test
    public void testAmpersandDollarSign() throws Exception {
        final String input = "<element>apples &$amp; oranges cost $12.00.</element>";

        final ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        final EntityEncodedInputStream translateIn = new EntityEncodedInputStream(in);

        final String expected = "<element>apples $#$$amp; oranges cost $$12.00.</element>";

        assertEquals(expected, StreamHelper.inputStreamToString(translateIn));
    }

    @Test
    public void testDollarSignAmpersand() throws Exception {
        final String input = "<element>$#apples $&amp; oranges cost $12.00.</element>";

        final ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        final EntityEncodedInputStream translateIn = new EntityEncodedInputStream(in);

        final String expected = "<element>$$#apples $$$#amp; oranges cost $$12.00.</element>";

        assertEquals(expected, StreamHelper.inputStreamToString(translateIn));
    }

    @Test
    public void testEncodeEntities() throws Exception {
        final String input = "<element>apples &amp; oranges cost $12.00.</element>";

        final ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        final EntityEncodedInputStream translateIn = new EntityEncodedInputStream(in);

        final String expected = "<element>apples $#amp; oranges cost $$12.00.</element>";

        assertEquals(expected, StreamHelper.inputStreamToString(translateIn));
    }

    @Test
    public void testEncodeEntitiesAtBufferEnd() throws Exception {
        final String input = "apples oranges cost $";

        final byte[] buffer = new byte[21];
        final ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        final EntityEncodedInputStream translateIn = new EntityEncodedInputStream(in);

        final int bytesRead = translateIn.read(buffer);
        assertTrue(21 == bytesRead);
    }

    @Test
    public void testOverflowDollarSignReadingOneByteAtATime() throws Exception {
        final String input = "$12";

        final ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        final EntityEncodedInputStream translateIn = new EntityEncodedInputStream(in);

        final StringBuilder actual = new StringBuilder();
        int character;
        while ((character = translateIn.read()) > -1) {
            actual.append(String.valueOf((char) character));
        }

        assertEquals("$$12", actual.toString());
    }

    @Test
    public void testWithParser() throws Exception {
        final String input = "<element>apples &amp; oranges cost $12.00.</element>";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        in = new EntityEncodedInputStream(in);

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final EntityDecodedOutputStream decoder = new EntityDecodedOutputStream(out);
        final EchoHandler handler = new EchoHandler(decoder);

        final XMLReader parser = XMLReaderFactory.createXMLReader();
        parser.setContentHandler(handler);
        parser.parse(new InputSource(in));
        out.close();

        assertEquals('\n' + input, '\n' + out.toString());
    }
}
