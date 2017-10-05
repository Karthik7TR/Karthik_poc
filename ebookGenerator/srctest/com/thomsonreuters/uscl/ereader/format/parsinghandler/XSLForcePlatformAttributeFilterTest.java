package com.thomsonreuters.uscl.ereader.format.parsinghandler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.junit.Before;
import org.junit.Test;

/**
 * Test various XSLForcePlatformAttributeFilter data scenarios.
 *
 * @author <a href="mailto:Dong.Kim@thomsonreuters.com">Dong Kim</a> u0155568
 */
public final class XSLForcePlatformAttributeFilterTest {
    private SAXParser saxParser;

    @Before
    public void setUp() throws Exception {
        final SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        saxParser = factory.newSAXParser();
    }

    /** Helper method that sets up the repeating pieces of each test and just modifies the input
     *
     * @param inputXML input string for the test.
     * @param expectedResult
     */
    public void testHelper(final String inputXML, final String href, final boolean expectedResult) {
        ByteArrayInputStream input = null;
        try {
            final XSLForcePlatformAttributeFilter filter = new XSLForcePlatformAttributeFilter(href);

            input = new ByteArrayInputStream(inputXML.getBytes());
            saxParser.parse(input, filter);

            assertEquals(expectedResult, filter.isForcePlatform());
        } catch (final Exception e) {
            fail("Encountered exception during test: " + e.getMessage());
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
            } catch (final Exception e) {
                fail("Could clean up resources: " + e.getMessage());
            }
        }
    }

    @Test
    public void testForcePlatformNotFound() {
        final String href = "Somthing.xsl";
        final String xmlTestStr = "<nothing></nothing>";
        final boolean expectedResult = false;

        testHelper(xslWrapper(xmlTestStr), href, expectedResult);
    }

    @Test
    public void testForcePlatformNotFound2() {
        final String href = "Something.xsl";
        final String xmlTestStr = "<xsl:include href=\"Something.xsl\" />";
        final boolean expectedResult = false;

        testHelper(xslWrapper(xmlTestStr), href, expectedResult);
    }

    @Test
    public void testForcePlatformFound() {
        final String href = "Something.xsl";
        final String xmlTestStr = "<xsl:include href=\"Something.xsl\" forcePlatform=\"true\" />";
        final boolean expectedResult = true;

        testHelper(xslWrapper(xmlTestStr), href, expectedResult);
    }

    @Test
    public void testForcePlatformFound2() {
        final String href = "Something.xsl";
        final String xmlTestStr = "<xsl:include href=\"Something.xsl\" forcePlatform=\"TrUe\" />";
        final boolean expectedResult = true;

        testHelper(xslWrapper(xmlTestStr), href, expectedResult);
    }

    @Test
    public void testForcePlatformFoundButFalse() {
        final String href = "Something.xsl";
        final String xmlTestStr = "<xsl:include href=\"Something.xsl\" forcePlatform=\"false\" />";
        final boolean expectedResult = false;

        testHelper(xslWrapper(xmlTestStr), href, expectedResult);
    }

    private String xslWrapper(final String text) {
        final StringBuffer buffer = new StringBuffer();
        buffer.append("<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">");
        buffer.append(text);
        buffer.append("</xsl:stylesheet>");
        return buffer.toString();
    }
}
