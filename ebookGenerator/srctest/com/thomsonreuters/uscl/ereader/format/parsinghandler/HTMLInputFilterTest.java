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
 * Test various HTMLInputFilter data scenarios.
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public final class HTMLInputFilterTest {
    private HTMLInputFilter inputFilter;
    private Serializer serializer;

    @Before
    public void setUp() throws Exception {
        final SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        final SAXParser saxParser = factory.newSAXParser();

        inputFilter = new HTMLInputFilter();
        inputFilter.setParent(saxParser.getXMLReader());

        final Properties props = OutputPropertiesFactory.getDefaultMethodProperties(Method.XHTML);
        props.setProperty("omit-xml-declaration", "yes");
        serializer = SerializerFactory.getSerializer(props);
    }

    @After
    public void tearDown() {
        serializer = null;
        inputFilter = null;
    }

    /** Helper method that sets up the repeating pieces of each test and just modifies the input and output.
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

            inputFilter.setContentHandler(serializer.asContentHandler());
            inputFilter.parse(new InputSource(input));

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
                fail("Could clean up resources: " + e.getMessage());
            }
        }
    }

    @Test
    public void testValidKeyCiteInputTag() {
        final String xmlTestStr = "<test><input id=\"co_keyCiteFlagPlaceHolder\" type=\"hidden\"/></test>";
        final String expectedResult = "<test/>";

        testHelper(xmlTestStr, expectedResult);
    }

    @Test
    public void testInputTagWithoutAttributes() {
        final String xmlTestStr = "<test><input/></test>";
        final String expectedResult = "<test/>";

        testHelper(xmlTestStr, expectedResult);
    }

    @Test
    public void testHiddenTypeOnlyInputTag() {
        final String xmlTestStr = "<test><input type=\"hidden\"/></test>";
        final String expectedResult = "<test/>";

        testHelper(xmlTestStr, expectedResult);
    }

    @Test
    public void testKeyCiteIDOnlyInputTag() {
        final String xmlTestStr = "<test><input id=\"co_keyCiteFlagPlaceHolder\"/></test>";
        final String expectedResult = "<test/>";

        testHelper(xmlTestStr, expectedResult);
    }

    @Test
    public void testMultipleValidKeyCiteInputTags() {
        final String xmlTestStr =
            "<test><input id=\"co_keyCiteFlagPlaceHolder\" type=\"hidden\"/><input id=\"co_keyCiteFlagPlaceHolder\" type=\"hidden\"/></test>";
        final String expectedResult = "<test/>";

        testHelper(xmlTestStr, expectedResult);
    }

    @Test
    public void testMultipleValidKeyCiteInputTagsWithEmbeddedContent() {
        final String xmlTestStr =
            "<test><input id=\"co_keyCiteFlagPlaceHolder\" type=\"hidden\"/><img src=\"test\"/><input id=\"co_keyCiteFlagPlaceHolder\" type=\"hidden\"/></test>";
        final String expectedResult = "<test><img src=\"test\"/></test>";

        testHelper(xmlTestStr, expectedResult);
    }

    @Test
    public void testKeyCiteInputTagWithCDATA() {
        final String xmlTestStr =
            "<test><input id=\"co_keyCiteFlagPlaceHolder\" type=\"hidden\">Test123</input></test>";
        final String expectedResult = "<test/>";

        testHelper(xmlTestStr, expectedResult);
    }

    @Test
    public void testInputTagWithCDATA() {
        final String xmlTestStr = "<test><input>Test123</input></test>";
        final String expectedResult = "<test/>";

        testHelper(xmlTestStr, expectedResult);
    }
}
