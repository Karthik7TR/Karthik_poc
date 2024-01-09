package com.thomsonreuters.uscl.ereader.format.parsinghandler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashSet;
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
 * Test various HTMLAnchorFilter data scenarios.
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public final class HTMLImageFilterTest {
    private HTMLImageFilter imageFilter;
    private Serializer serializer;

    @Before
    public void setUp() throws Exception {
        final SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        final SAXParser saxParser = factory.newSAXParser();

        imageFilter = new HTMLImageFilter();
        imageFilter.setStaticImageRefs(new HashSet<String>());
        imageFilter.setParent(saxParser.getXMLReader());

        final Properties props = OutputPropertiesFactory.getDefaultMethodProperties(Method.XHTML);
        props.setProperty("omit-xml-declaration", "yes");
        serializer = SerializerFactory.getSerializer(props);
    }

    @After
    public void tearDown() {
        serializer = null;
        imageFilter = null;
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

            imageFilter.setContentHandler(serializer.asContentHandler());
            imageFilter.parse(new InputSource(input));

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
    public void testValidImageTagSrcUpdate() throws SAXException {
        final String xmlTestStr = "<test><img src=\"/images/TestImage.jpg\"/></test>";
        final String expectedResult = "<test><img src=\"er:#TestImage\"/></test>";

        testHelper(xmlTestStr, expectedResult);

        assertEquals(1, imageFilter.getStaticImageRefs().size());
    }

    @Test(expected = SAXException.class)
    public void testImageTagWithoutSrcAttribute() throws SAXException {
        final String xmlTestStr = "<test><img/></test>";
        final String expectedResult = "";

        testHelper(xmlTestStr, expectedResult);
    }

    @Test
    public void testImageTagSrcUpdateOtherAttributesPreserved() throws SAXException {
        final String xmlTestStr =
            "<test><img type=\"image\" src=\"/images/TestImage.jpg\" " + "class=\"co_test\" /></test>";
        final String expectedResult = "<test><img type=\"image\" class=\"co_test\" src=\"er:#TestImage\"/></test>";

        testHelper(xmlTestStr, expectedResult);

        assertEquals(1, imageFilter.getStaticImageRefs().size());
    }

    @Test
    public void testMultipleImageTagsBeingParsed() throws SAXException {
        final String xmlTestStr = "<test><img src=\"/images/TestImage.jpg\"/><div id=\"testingDiv\">Test"
            + "<img type=\"image\" src=\"/images/TestImage2.jpg\" class=\"co_test\" /></div>"
            + "</test>";
        final String expectedResult = "<test><img src=\"er:#TestImage\"/><div id=\"testingDiv\">Test"
            + "<img type=\"image\" class=\"co_test\" src=\"er:#TestImage2\"/></div>"
            + "</test>";

        testHelper(xmlTestStr, expectedResult);

        assertEquals(2, imageFilter.getStaticImageRefs().size());
    }

    @Test
    public void testSimplePDFImageTagRemoval() throws SAXException {
        final String xmlTestStr = "<test><img alt=\"PDF\" src=\"/images/TestImage.jpg\"/></test>";
        final String expectedResult = "<test/>";

        testHelper(xmlTestStr, expectedResult);
    }

    @Test
    public void testPDFImageTagRemovalWithOtherTags() throws SAXException {
        final String xmlTestStr = "<test><img alt=\"PDF\" src=\"/images/TestImage.jpg\"/><div id=\"testingDiv\">Test"
            + "<img type=\"image\" src=\"/images/TestImage2.jpg\" class=\"co_test\" /></div>"
            + "</test>";
        final String expectedResult = "<test><div id=\"testingDiv\">Test"
            + "<img type=\"image\" class=\"co_test\" src=\"er:#TestImage2\"/></div>"
            + "</test>";

        testHelper(xmlTestStr, expectedResult);
    }

    @Test
    public void testImageRemovalAnchorBecomesImage() throws SAXException {
        final String xmlTestStr = "<test><img src=\"https://1.next.westlaw.com/Link/Document/Blob/"
            + "Ie043aca0675a11da90ebf04471783734.jpg?targetType=&amp;originationContext=document&amp;"
            + "transitionType=DocumentImage\" alt=\"Image 2 within \" height=\"1201\" width=\"925\" "
            + "class=\"co_shrunkImage\"/></test>";
        final String expectedResult = "<test/>";

        testHelper(xmlTestStr, expectedResult);
    }

    @Test
    public void testImageRemovalDiffDomainAnchorBecomesImage() throws SAXException {
        final String xmlTestStr = "<test><img src=\"https://test.com/Link/Document/Blob/"
            + "Ie043aca0675a11da90ebf04471783734.jpg?targetType=&amp;originationContext=document&amp;"
            + "transitionType=DocumentImage\" alt=\"Image 2 within \" height=\"1201\" width=\"925\" "
            + "class=\"co_shrunkImage\"/></test>";
        final String expectedResult = "<test/>";

        testHelper(xmlTestStr, expectedResult);
    }

    @Test
    public void testImageRemovalNestedInAnchor() throws SAXException {
        final String xmlTestStr = "<test><a type=\"image/jpeg\" href=\"https://1.next.westlaw.com/Link/Document/"
            + "Blob/Ie043aca0675a11da90ebf04471783734.jpg?targetType=&amp;originationContext=document&amp;"
            + "transitionType=DocumentImage\" class=\"co_imageLink\">"
            + "<img src=\"https://1.next.westlaw.com/Link/Document/Blob/"
            + "Ie043aca0675a11da90ebf04471783734.jpg?targetType=&amp;originationContext=document&amp;"
            + "transitionType=DocumentImage\" alt=\"Image 2 within \" height=\"1201\" width=\"925\" "
            + "class=\"co_shrunkImage\"/></a></test>";
        final String expectedResult = "<test><a type=\"image/jpeg\" href=\"https://1.next.westlaw.com/Link/Document/"
            + "Blob/Ie043aca0675a11da90ebf04471783734.jpg?targetType=&amp;originationContext=document&amp;"
            + "transitionType=DocumentImage\" class=\"co_imageLink\"/></test>";

        testHelper(xmlTestStr, expectedResult);
    }
}
