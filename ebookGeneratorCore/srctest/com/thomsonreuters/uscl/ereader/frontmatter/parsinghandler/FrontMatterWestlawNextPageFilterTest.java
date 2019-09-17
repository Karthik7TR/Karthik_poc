package com.thomsonreuters.uscl.ereader.frontmatter.parsinghandler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Properties;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import com.thomsonreuters.uscl.ereader.FrontMatterFileName;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import org.apache.xml.serializer.Method;
import org.apache.xml.serializer.OutputPropertiesFactory;
import org.apache.xml.serializer.Serializer;
import org.apache.xml.serializer.SerializerFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.InputSource;

/**
 * Test various FrontMatterTitlePageFilter data scenarios.
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public final class FrontMatterWestlawNextPageFilterTest {
    private FrontMatterWestlawNextPageFilter westlawNextPageFilter;
    private BookDefinition bookDefinition;
    private Serializer serializer;
    private SAXParser saxParser;

    @Before
    public void setUp() throws Exception {
        final SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        saxParser = factory.newSAXParser();

        bookDefinition = new BookDefinition();

        final Properties props = OutputPropertiesFactory.getDefaultMethodProperties(Method.XHTML);
        props.setProperty("omit-xml-declaration", "yes");
        serializer = SerializerFactory.getSerializer(props);
    }

    @After
    public void tearDown() {
        serializer = null;
        westlawNextPageFilter = null;
    }

    /**
     * Helper method that sets up the repeating pieces of each test and modifies the Table
     * values that are returned along with the input and output.
     *
     * @param inputXML input string for the test.
     * @param expectedResult the expected output for the specified input string.
     */
    public void testHelper(final String inputXML, final String expectedResult, final boolean withPagebreaks) {
        ByteArrayInputStream input = null;
        ByteArrayOutputStream output = null;
        try {
            input = new ByteArrayInputStream(inputXML.getBytes());
            output = new ByteArrayOutputStream();

            westlawNextPageFilter = new FrontMatterWestlawNextPageFilter(withPagebreaks);
            westlawNextPageFilter.setParent(saxParser.getXMLReader());

            serializer.setOutputStream(output);

            westlawNextPageFilter.setContentHandler(serializer.asContentHandler());
            westlawNextPageFilter.parse(new InputSource(input));

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
    public void testFrontMatterPlaceholder_WestlawNextPageAnchor() {
        final String xmlTestStr = "<test><frontMatterPlaceholder_WestlawNextPageAnchor/></test>";
        final String expectedResult =
            "<test><a name=\"" + FrontMatterFileName.WESTLAW + FrontMatterFileName.ANCHOR + "\"> </a></test>";

        testHelper(xmlTestStr, expectedResult, false);
    }

    @Test
    public void testFrontMatterPlaceholder_WestlawNextPageAnchorWithPagebreaks() {
        final String xmlTestStr = "<test><frontMatterPlaceholder_WestlawNextPageAnchor/></test>";
        final String expectedResult =
            "<test><?pb label=\"iv\"?><a name=\"" + FrontMatterFileName.WESTLAW + FrontMatterFileName.ANCHOR + "\"> </a></test>";

        testHelper(xmlTestStr, expectedResult, true);
    }
}
