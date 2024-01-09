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
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.InputSource;

/**
 * Test various HTMLTableFilter data scenarios.
 *
 * @author <a href="mailto:Ravi.Nandikolla@thomsonreuters.com">Ravi Nandikolla</a> c139353
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public final class HTMLTableFilterTest {
    private HTMLTableFilter tableFilter;
    private Serializer serializer;

    @Before
    public void setUp() throws Exception {
        final SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        final SAXParser saxParser = factory.newSAXParser();

        tableFilter = new HTMLTableFilter(true);
        tableFilter.setParent(saxParser.getXMLReader());

        final Properties props = OutputPropertiesFactory.getDefaultMethodProperties(Method.XHTML);
        props.setProperty("omit-xml-declaration", "yes");
        serializer = SerializerFactory.getSerializer(props);
    }

    @After
    public void tearDown() {
        serializer = null;
        tableFilter = null;
    }

    /**
     * Helper method that sets up the repeating pieces of each test and modifies the Table
     * values that are returned along with the input and output.
     *
     * @param inputXML input string for the test.
     * @param expectedResult the expected output for the specified input string.
     */
    public void testHelper(final String inputXML, final String expectedResult) {
        ByteArrayInputStream input = null;
        ByteArrayOutputStream output = null;
        try {
            input = new ByteArrayInputStream(inputXML.getBytes());
            output = new ByteArrayOutputStream();

            serializer.setOutputStream(output);

            tableFilter.setContentHandler(serializer.asContentHandler());
            tableFilter.parse(new InputSource(input));

            final String result = output.toString();

            assertEquals(expectedResult, result);
        } catch (final Exception e) {
            fail("Encountered exception during test: " + e.getMessage());
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
                if (output != null) {
                    output.close();
                }
            } catch (final Exception e) {
                fail("Couldn't clean up resources: " + e.getMessage());
            }
        }
    }

    @Test
    public void testTableAnchorWithMultiplearuguments() {
        final String xmlTestStr = "<test><table class=\"co_borderTop \" style=\"width:100%;\"/></test>";
        final String expectedResult = "<test><table class=\"tr_table\"/></test>";

        testHelper(xmlTestStr, expectedResult);
    }

    @Test
    public void testTableAnchor() {
        final String xmlTestStr = "<test><table style=\"width:100%;\"/></test>";
        final String expectedResult = "<test><table class=\"tr_table\"/></test>";

        testHelper(xmlTestStr, expectedResult);
    }

    @Test
    public void testCopyrightRemovalNoCopyrightDataBlock() {
        final String xmlTestStr = "<test><table id=\"co_endOfDocument\"><tr><td>End of Document</td>"
            + "<td class=\"co_endOfDocCopyright\"></td></tr></table></test>";
        final String expectedResult = "<test><table id=\"co_endOfDocument\"><tr><td>End of Document</td>"
            + "<td class=\"co_endOfDocCopyright\"/></tr></table></test>";

        testHelper(xmlTestStr, expectedResult);
    }

    @Test
    public void testCopyrightRemoval() {
        final String xmlTestStr = "<test><table id=\"co_endOfDocument\"><tr><td>End of Document</td>"
            + "<td class=\"co_endOfDocCopyright\">Copyright Text</td></tr></table></test>";
        final String expectedResult = "<test><table id=\"co_endOfDocument\"><tr><td>End of Document</td>"
            + "<td class=\"co_endOfDocCopyright\"/></tr></table></test>";

        testHelper(xmlTestStr, expectedResult);
    }

    @Test
    public void testCopyrightRemovalWithoutTableViewerSupport() {
        tableFilter.setTableViewerSupport(false);
        final String xmlTestStr = "<test><table id=\"co_endOfDocument\"><tr><td>End of Document</td>"
            + "<td class=\"co_endOfDocCopyright\">Copyright Text</td></tr></table></test>";
        final String expectedResult = "<test><table id=\"co_endOfDocument\"><tr><td>End of Document</td>"
            + "<td class=\"co_endOfDocCopyright\"/></tr></table></test>";

        testHelper(xmlTestStr, expectedResult);
    }
}
