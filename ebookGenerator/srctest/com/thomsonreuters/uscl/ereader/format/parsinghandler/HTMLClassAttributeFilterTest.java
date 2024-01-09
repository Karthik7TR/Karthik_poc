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
import org.xml.sax.SAXException;

/**
 * Test various HTMLClassAttributeFilter data scenarios.
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public final class HTMLClassAttributeFilterTest {
    private HTMLClassAttributeFilter classAttFilter;
    private Serializer serializer;

    @Before
    public void setUp() throws Exception {
        final SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        final SAXParser saxParser = factory.newSAXParser();

        classAttFilter = new HTMLClassAttributeFilter();
        classAttFilter.setParent(saxParser.getXMLReader());

        final Properties props = OutputPropertiesFactory.getDefaultMethodProperties(Method.XHTML);
        props.setProperty("omit-xml-declaration", "yes");
        serializer = SerializerFactory.getSerializer(props);
    }

    @After
    public void tearDown() {
        serializer = null;
        classAttFilter = null;
    }

    /**
     * Helper method that sets up the repeating pieces of each test and modifies the ImageService
     * values that are returned along with the input and output.
     *
     * @param inputXML input string for the test.
     * @param expectedResult the expected output for the specified input string.
     */
    public void testHelper(final String inputXML, final String expectedResult) throws SAXException {
        ByteArrayInputStream input = null;
        ByteArrayOutputStream output = null;
        try {
            input = new ByteArrayInputStream(inputXML.getBytes());
            output = new ByteArrayOutputStream();

            serializer.setOutputStream(output);

            classAttFilter.setContentHandler(serializer.asContentHandler());
            classAttFilter.parse(new InputSource(input));

            final String result = output.toString();

            assertEquals(expectedResult, result);
        } catch (final SAXException e) {
            throw e;
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
    public void testNoCSSClass() throws SAXException {
        final String xmlTestStr = "<test><img src=\"/images/TestImage.jpg\"/></test>";
        final String expectedResult = "<test><img src=\"/images/TestImage.jpg\"/></test>";

        testHelper(xmlTestStr, expectedResult);
    }

    @Test
    public void testOneCSSClassNotChanged() throws SAXException {
        final String xmlTestStr = "<test><img class=\"good\" src=\"/images/TestImage.jpg\"/></test>";
        final String expectedResult = "<test><img class=\"good\" src=\"/images/TestImage.jpg\"/></test>";

        testHelper(xmlTestStr, expectedResult);
    }

    @Test
    public void testTwoCSSClass() throws SAXException {
        final String xmlTestStr = "<test><img class=\"good bad\" src=\"/images/TestImage.jpg\"/></test>";
        final String expectedResult = "<test><img src=\"/images/TestImage.jpg\" class=\"good\"/></test>";

        testHelper(xmlTestStr, expectedResult);
    }

    @Test
    public void testFiveCSSClass() throws SAXException {
        final String xmlTestStr = "<test><img class=\"good bad bad2 bad3 bad4\" src=\"/images/TestImage.jpg\"/></test>";
        final String expectedResult = "<test><img src=\"/images/TestImage.jpg\" class=\"good\"/></test>";

        testHelper(xmlTestStr, expectedResult);
    }

    @Test
    public void testMultipleTagsWithMulitpleCSSClasses() throws SAXException {
        final String xmlTestStr = "<test><img class=\"good bad\" src=\"/images/TestImage.jpg\"/>"
            + "<testingMachine id=\"test\" class=\"good bad worse\"/></test>";
        final String expectedResult = "<test><img src=\"/images/TestImage.jpg\" class=\"good\"/>"
            + "<testingMachine id=\"test\" class=\"good\"/></test>";

        testHelper(xmlTestStr, expectedResult);
    }
}
