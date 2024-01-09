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
 * Test various HTMLTagIdDeduppingFilte data scenarios.
 *
 * @author <a href="mailto:Ravi.Nandikolla@thomsonreuters.com">Ravi Nandikolla</a> c139353
 */
public final class HTMLTagIdDeduppingFilterTest {
    private HTMLTagIdDedupingFilter tagIdDeduppingFilter;
    private Serializer serializer;

    @Before
    public void setUp() throws Exception {
        final SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        final SAXParser saxParser = factory.newSAXParser();

        tagIdDeduppingFilter = new HTMLTagIdDedupingFilter("12345678");
        tagIdDeduppingFilter.setParent(saxParser.getXMLReader());

        final Properties props = OutputPropertiesFactory.getDefaultMethodProperties(Method.XHTML);
        props.setProperty("omit-xml-declaration", "yes");
        serializer = SerializerFactory.getSerializer(props);
    }

    @After
    public void tearDown() {
        serializer = null;
        tagIdDeduppingFilter = null;
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

            tagIdDeduppingFilter.setContentHandler(serializer.asContentHandler());
            tagIdDeduppingFilter.parse(new InputSource(input));

            tagIdDeduppingFilter.getDuplicateIdList();

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
    public void testTagIdDeduppingAnchorWithMultiplearuguments() {
        final String xmlTestStr =
            "<test><hello id=\"ravi_1234\"/><div id=\"ravi_1234\"/><section id=\"ravi_1234\"/><section id=\"krishna_1234\"/><section id=\"krishna_1234\"/></test>";
        final String expectedResult =
            "<test><hello id=\"ravi_1234\"/><div id=\"ravi_1234_eBG_0\"/><section id=\"ravi_1234_eBG_1\"/><section id=\"krishna_1234\"/><section id=\"krishna_1234_eBG_0\"/></test>";

        testHelper(xmlTestStr, expectedResult);
    }

    @Test
    public void testTagIdDeduppingAnchor() {
        final String xmlTestStr = "<test><Div id=\"ravi_1234\"/><div id=\"ravi_1234\"/></test>";
        final String expectedResult = "<test><Div id=\"ravi_1234\"/><div id=\"ravi_1234_eBG_0\"/></test>";

        testHelper(xmlTestStr, expectedResult);
    }
}
