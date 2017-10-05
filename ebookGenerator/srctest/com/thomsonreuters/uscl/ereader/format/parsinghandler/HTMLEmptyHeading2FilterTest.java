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
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public final class HTMLEmptyHeading2FilterTest {
    private HTMLEmptyHeading2Filter h2Filter;
    private Serializer serializer;

    @Before
    public void setUp() throws Exception {
        final SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        final SAXParser saxParser = factory.newSAXParser();

        h2Filter = new HTMLEmptyHeading2Filter();
        h2Filter.setParent(saxParser.getXMLReader());

        final Properties props = OutputPropertiesFactory.getDefaultMethodProperties(Method.XHTML);
        props.setProperty("omit-xml-declaration", "yes");
        serializer = SerializerFactory.getSerializer(props);
    }

    @After
    public void tearDown() {
        serializer = null;
        h2Filter = null;
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

            h2Filter.setContentHandler(serializer.asContentHandler());
            h2Filter.parse(new InputSource(input));

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
    public void testSimpleEmptyH2TagDelete() {
        final String xmlTestStr = "<test><h2></h2></test>";
        final String expectedResult = "<test/>";

        testHelper(xmlTestStr, expectedResult);
    }

    @Test
    public void testSimpleH2TagWithTextPreserved() {
        final String xmlTestStr = "<test><h2>Testing</h2></test>";
        final String expectedResult = "<test><h2>Testing</h2></test>";

        testHelper(xmlTestStr, expectedResult);
    }

    @Test
    public void testSimpleH2TagWithTextAndTagsPreserved() {
        final String xmlTestStr = "<test><h2>Testing <bold>Bold</bold></h2></test>";
        final String expectedResult = "<test><h2>Testing <bold>Bold</bold></h2></test>";

        testHelper(xmlTestStr, expectedResult);
    }

    @Test
    public void testSimpleH2TagWithAttributesAndTagsPreserved() {
        final String xmlTestStr = "<test><h2>Testing</h2><test2/><h2></h2>"
            + "<test3/><h2></h2>"
            + "<test4/><h2><a name=\"name1\">Hello</a><a name=\"name2\">Hello2</a></h2></test>";
        final String expectedResult = "<test><h2>Testing</h2><test2/>"
            + "<test3/>"
            + "<test4/><h2><a name=\"name1\">Hello</a><a name=\"name2\">Hello2</a></h2></test>";

        testHelper(xmlTestStr, expectedResult);
    }

    @Test
    public void testSimpleH2TagWithTextAndTagsBeforeAndAfterPreserved() {
        final String xmlTestStr = "<test><h2>Testing <bold>Bold</bold> and <i>italics</i></h2></test>";
        final String expectedResult = "<test><h2>Testing <bold>Bold</bold> and <i>italics</i></h2></test>";

        testHelper(xmlTestStr, expectedResult);
    }

    @Test
    public void testMultipleEmptyH2Tags() {
        final String xmlTestStr = "<test><h2></h2><h2></h2><tester/><h2></h2></test>";
        final String expectedResult = "<test><tester/></test>";

        testHelper(xmlTestStr, expectedResult);
    }

    @Test
    public void testMultipleEmptyH2TagsMixedWithNonEmpty() {
        final String xmlTestStr =
            "<test><h2>Testing</h2><test2/><h2></h2><test3/><h2></h2>" + "<test4/><h2><a>Hello</a></h2></test>";
        final String expectedResult = "<test><h2>Testing</h2><test2/><test3/><test4/><h2><a>Hello</a></h2></test>";

        testHelper(xmlTestStr, expectedResult);
    }

    @Test
    public void testNestedMultipleEmptyH2TagsDelete() {
        final String xmlTestStr = "<test><h2><h2></h2><h2></h2></h2></test>";
        final String expectedResult = "<test/>";

        testHelper(xmlTestStr, expectedResult);
    }

    @Test
    public void testNestedMultipleLevelsEmptyH2TagsDelete() {
        final String xmlTestStr = "<test><h2><h2><h2></h2></h2></h2></test>";
        final String expectedResult = "<test/>";

        testHelper(xmlTestStr, expectedResult);
    }

    @Test
    public void testComplexEmptyH2Tags() {
        final String xmlTestStr = "<test><h2></h2><h2><h2><h2><h2><h2></h2></h2></h2></h2></h2>"
            + "<tester/><h2><h2><h2></h2></h2></h2></test>";
        final String expectedResult = "<test><tester/></test>";

        testHelper(xmlTestStr, expectedResult);
    }

    @Test
    public void testComplexEmptyAndNonEmptyH2Tags() {
        final String xmlTestStr = "<test><h2></h2><h2><h2><h2><h2><h2></h2></h2></h2></h2></h2>"
            + "<tester/><h2><h2><h2>Test</h2></h2></h2></test>";
        final String expectedResult = "<test><tester/><h2><h2><h2>Test</h2></h2></h2></test>";

        testHelper(xmlTestStr, expectedResult);
    }
}
