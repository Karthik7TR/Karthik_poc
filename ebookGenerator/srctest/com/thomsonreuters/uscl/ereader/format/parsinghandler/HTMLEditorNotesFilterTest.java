package com.thomsonreuters.uscl.ereader.format.parsinghandler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Properties;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.xml.serializer.Method;
import org.apache.xml.serializer.OutputPropertiesFactory;
import org.apache.xml.serializer.Serializer;
import org.apache.xml.serializer.SerializerFactory;
import org.junit.After;
import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Test various HTMLEditorNotesFilter data scenarios.
 *
 * @author <a href="mailto:dong.kim@thomsonreuters.com">Dong Kim</a> u0155568
 */
public final class HTMLEditorNotesFilterTest
{
    private HTMLEditorNotesFilter filter;
    private Serializer serializer;

    private void setUp(final boolean removeNote) throws Exception
    {
        final SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        final SAXParser saxParser = factory.newSAXParser();

        filter = new HTMLEditorNotesFilter(removeNote);
        filter.setParent(saxParser.getXMLReader());

        final Properties props = OutputPropertiesFactory.getDefaultMethodProperties(Method.XHTML);
        props.setProperty("omit-xml-declaration", "yes");
        serializer = SerializerFactory.getSerializer(props);
    }

    @After
    public void tearDown()
    {
        serializer = null;
        filter = null;
    }

    /**
     * Helper method that sets up the repeating pieces of each test and modifies the
     * values that are returned along with the input and output.
     *
     * @param inputXML input string for the test.
     * @param expectedResult the expected output for the specified input string.
     */
    public void testHelper(final String inputXML, final String expectedResult) throws SAXException
    {
        ByteArrayInputStream input = null;
        ByteArrayOutputStream output = null;
        try
        {
            input = new ByteArrayInputStream(inputXML.getBytes());
            output = new ByteArrayOutputStream();

            serializer.setOutputStream(output);

            filter.setContentHandler(serializer.asContentHandler());
            filter.parse(new InputSource(input));

            final String result = output.toString();

            assertEquals(expectedResult, result);
        }
        catch (final SAXException e)
        {
            throw e;
        }
        catch (final Exception e)
        {
            fail("Encountered exception during test: " + e.getMessage());
        }
        finally
        {
            try
            {
                if (input != null)
                {
                    input.close();
                }
                if (output != null)
                {
                    output.close();
                }
            }
            catch (final Exception e)
            {
                fail("Could clean up resources: " + e.getMessage());
            }
        }
    }

    @Test
    public void testRemoveNote() throws Exception
    {
        setUp(true);
        final String xmlTestStr = "<div eBookEditorNotes=\"true\">this is gone</div>";
        final String expectedResult = "";

        testHelper(xmlTestStr, expectedResult);
    }

    @Test
    public void testRemoveNote2() throws Exception
    {
        setUp(true);
        final String xmlTestStr =
            "<div><div eBookEditorNotes=\"true\"><div>should be gone</div>this is gone</div><div>more text</div></div>";
        final String expectedResult = "<div><div>more text</div></div>";

        testHelper(xmlTestStr, expectedResult);
    }

    @Test
    public void testKeepHeading() throws Exception
    {
        setUp(false);
        final String xmlTestStr = "<div eBookEditorNotes=\"true\"><h2>should be here</h2></div>";
        final String expectedResult = "<div eBookEditorNotes=\"true\"><h2>should be here</h2></div>";

        testHelper(xmlTestStr, expectedResult);
    }
}
