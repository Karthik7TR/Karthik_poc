package com.thomsonreuters.uscl.ereader.format.parsinghandler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import com.thomsonreuters.uscl.ereader.gather.image.domain.ImageMetadataEntity;
import com.thomsonreuters.uscl.ereader.gather.image.domain.ImageMetadataEntityKey;
import com.thomsonreuters.uscl.ereader.gather.image.service.ImageService;
import org.apache.xml.serializer.Method;
import org.apache.xml.serializer.OutputPropertiesFactory;
import org.apache.xml.serializer.Serializer;
import org.apache.xml.serializer.SerializerFactory;
import org.easymock.EasyMock;
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
public final class HTMLAnchorFilterTest {
    private HTMLAnchorFilter anchorFilter;
    private Serializer serializer;
    private final long testJobId = 123L;
    private final String testGuid = "NFA730F80D58A11DCBFA7F697EE59258B";
    private final String docGuid = "1FA730F80D58A11DCBFA7F697EE59258B";
    private final String invalidGuid = "badGuid";
    private final String firstlineCite = "ABC";
    private final String currentGuid = "ABC1234";

    private ImageMetadataEntity regularImgMetadata;
    private ImageMetadataEntity largeImgMetadata;
    private ImageMetadataEntity largeHeightImgMetadata;
    private ImageMetadataEntity largeWidthImgMetadata;

    @Before
    public void setUp() throws Exception {
        final SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        final SAXParser saxParser = factory.newSAXParser();
        final Map<String, Set<String>> targetAnchors = new HashMap<>();

        anchorFilter = new HTMLAnchorFilter();
        anchorFilter.setjobInstanceId(testJobId);
        anchorFilter.setDocGuid(docGuid);
        anchorFilter.setFirstlineCite(firstlineCite);
        anchorFilter.setParent(saxParser.getXMLReader());
        anchorFilter.setCurrentGuid(currentGuid);
        anchorFilter.setTargetAnchors(targetAnchors);

        final Properties props = OutputPropertiesFactory.getDefaultMethodProperties(Method.XHTML);
        props.setProperty("omit-xml-declaration", "yes");
        serializer = SerializerFactory.getSerializer(props);

        regularImgMetadata = new ImageMetadataEntity();
        regularImgMetadata.setHeight(200L);
        regularImgMetadata.setWidth(200L);

        largeImgMetadata = new ImageMetadataEntity();
        largeImgMetadata.setHeight(2048L);
        largeImgMetadata.setWidth(2048L);

        largeHeightImgMetadata = new ImageMetadataEntity();
        largeHeightImgMetadata.setHeight(670L);
        largeHeightImgMetadata.setWidth(200L);

        largeWidthImgMetadata = new ImageMetadataEntity();
        largeWidthImgMetadata.setHeight(200L);
        largeWidthImgMetadata.setWidth(650L);
    }

    @After
    public void tearDown() {
        serializer = null;
        anchorFilter = null;
    }

    /**
     * Helper method that sets up the repeating pieces of each test and modifies the ImageService
     * values that are returned along with the input and output.
     *
     * @param imgService image service object that returns image metadata
     * @param inputXML input string for the test.
     * @param expectedResult the expected output for the specified input string.
     */
    public void testHelper(final ImageService imgService, final String inputXML, final String expectedResult)
        throws SAXException {
        ByteArrayInputStream input = null;
        ByteArrayOutputStream output = null;
        try {
            input = new ByteArrayInputStream(inputXML.getBytes());
            output = new ByteArrayOutputStream();

            serializer.setOutputStream(output);

            anchorFilter.setimgService(imgService);
            anchorFilter.setContentHandler(serializer.asContentHandler());
            anchorFilter.parse(new InputSource(input));

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
    public void testSimpleImageAnchorTag() throws SAXException {
        final ImageMetadataEntityKey key = new ImageMetadataEntityKey(testJobId, testGuid, docGuid);

        final ImageService mockImgService = EasyMock.createMock(ImageService.class);
        EasyMock.expect(mockImgService.findImageMetadata(key)).andReturn(regularImgMetadata);
        EasyMock.replay(mockImgService);

        final String xmlTestStr =
            "<test><a href=\"http://www.test/Blob/" + testGuid + ".jpg?\" type=\"image/jpeg\"/></test>";
        final String expectedResult = "<test><img src=\"er:#" + testGuid + "\"/></test>";

        testHelper(mockImgService, xmlTestStr, expectedResult);
    }

    @Test
    public void testSimpleImageAnchorTagWithV1URI() throws SAXException {
        final ImageMetadataEntityKey key = new ImageMetadataEntityKey(testJobId, testGuid, docGuid);

        final ImageService mockImgService = EasyMock.createMock(ImageService.class);
        EasyMock.expect(mockImgService.findImageMetadata(key)).andReturn(regularImgMetadata);
        EasyMock.replay(mockImgService);

        final String xmlTestStr =
            "<test><a href=\"http://www.test/Blob/v1/" + testGuid + ".jpg?\" type=\"image/jpeg\"/></test>";
        final String expectedResult = "<test><img src=\"er:#" + testGuid + "\"/></test>";

        testHelper(mockImgService, xmlTestStr, expectedResult);
    }

    @Test(expected = SAXException.class)
    public void testSimpleImageAnchorTagWithInvalidGuidV1URI() throws SAXException {
        final ImageService mockImgService = EasyMock.createMock(ImageService.class);

        final String xmlTestStr =
            "<test><a href=\"http://www.test/Blob/v1/" + invalidGuid + ".jpg?\" type=\"image/jpeg\"/></test>";
        final String expectedResult = "<test><img  src=\"er:#" + testGuid + "\"/></test>";

        testHelper(mockImgService, xmlTestStr, expectedResult);
    }

    @Test
    public void testSimpleLargeHeightImageAnchorTag() throws SAXException {
        final ImageMetadataEntityKey key = new ImageMetadataEntityKey(testJobId, testGuid, docGuid);

        final ImageService mockImgService = EasyMock.createMock(ImageService.class);
        EasyMock.expect(mockImgService.findImageMetadata(key)).andReturn(largeHeightImgMetadata);
        EasyMock.replay(mockImgService);

        final String xmlTestStr =
            "<test><a href=\"http://www.test/Blob/" + testGuid + ".jpg?\" type=\"image/jpeg\"/></test>";
        final String expectedResult = "<test><img src=\"er:#" + testGuid + "\" class=\"tr_image\"/></test>";

        testHelper(mockImgService, xmlTestStr, expectedResult);
    }

    @Test
    public void testSimpleLargeWidthImageAnchorTag() throws SAXException {
        final ImageMetadataEntityKey key = new ImageMetadataEntityKey(testJobId, testGuid, docGuid);

        final ImageService mockImgService = EasyMock.createMock(ImageService.class);
        EasyMock.expect(mockImgService.findImageMetadata(key)).andReturn(largeWidthImgMetadata);
        EasyMock.replay(mockImgService);

        final String xmlTestStr =
            "<test><a href=\"http://www.test/Blob/" + testGuid + ".jpg?\" type=\"image/jpeg\"/></test>";
        final String expectedResult = "<test><img src=\"er:#" + testGuid + "\" class=\"tr_image\"/></test>";

        testHelper(mockImgService, xmlTestStr, expectedResult);
    }

    @Test
    public void testSimpleLargeImageAnchorTag() throws SAXException {
        final ImageMetadataEntityKey key = new ImageMetadataEntityKey(testJobId, testGuid, docGuid);

        final ImageService mockImgService = EasyMock.createMock(ImageService.class);
        EasyMock.expect(mockImgService.findImageMetadata(key)).andReturn(largeImgMetadata);
        EasyMock.replay(mockImgService);

        final String xmlTestStr =
            "<test><a href=\"http://www.test/Blob/" + testGuid + ".jpg?\" type=\"image/jpeg\"/></test>";
        final String expectedResult = "<test><img src=\"er:#" + testGuid + "\" class=\"tr_image\"/></test>";

        testHelper(mockImgService, xmlTestStr, expectedResult);
    }

    @Test(expected = SAXException.class)
    public void testImageAnchorTagWithInvalidGuid() throws SAXException {
        final ImageService mockImgService = EasyMock.createMock(ImageService.class);

        final String xmlTestStr =
            "<test><a href=\"http://www.test/Blob/" + invalidGuid + ".jpg?\" type=\"image/jpeg\"/></test>";
        final String expectedResult = "<test><img  src=\"er:#" + testGuid + "\" class=\"tr_image\"/></test>";

        testHelper(mockImgService, xmlTestStr, expectedResult);
    }

    @Test
    public void testSimpleEmptyAnchorTag() throws SAXException {
        final ImageService mockImgService = EasyMock.createMock(ImageService.class);

        final String xmlTestStr = "<test><a href=\"#\"/></test>";
        final String expectedResult = "<test/>";

        testHelper(mockImgService, xmlTestStr, expectedResult);
    }

    @Test
    public void testSimpleEmptyAnchorWithContentTag() throws SAXException {
        final ImageService mockImgService = EasyMock.createMock(ImageService.class);

        final String xmlTestStr = "<test><a href=\"#\">Test123</a></test>";
        final String expectedResult = "<test/>";

        testHelper(mockImgService, xmlTestStr, expectedResult);
    }

    @Test
    public void testSimpleEmptyAnchorWithOtherAttributes() throws SAXException {
        final ImageService mockImgService = EasyMock.createMock(ImageService.class);

        final String xmlTestStr = "<test><a href=\"#\" class=\"test\">Test123</a></test>";
        final String expectedResult = "<test/>";

        testHelper(mockImgService, xmlTestStr, expectedResult);
    }

    @Test
    public void testSimpleNonEmptyAnchorWithOtherAttributes() throws SAXException {
        final ImageService mockImgService = EasyMock.createMock(ImageService.class);

        final String xmlTestStr = "<test><a href=\"#co_test\" class=\"test\">Test123</a></test>";
        final String expectedResult = "<test><a href=\"er:#ABC1234/co_test\" class=\"test\">Test123</a></test>";

        testHelper(mockImgService, xmlTestStr, expectedResult);
    }

    @Test
    public void testSimpleNonEmptyAnchorWithSP() throws SAXException {
        final ImageService mockImgService = EasyMock.createMock(ImageService.class);

        final String xmlTestStr = "<test><a href=\"#co_pp_sp_1000600_50660000823d1\" class=\"test\">Test123</a></test>";
        final String expectedResult =
            "<test><a href=\"er:#ABC1234/co_pp_50660000823d1\" class=\"test\">Test123</a></test>";

        testHelper(mockImgService, xmlTestStr, expectedResult);
    }

    @Test
    public void testRutterAnchorWithSP() throws SAXException {
        final ImageService mockImgService = EasyMock.createMock(ImageService.class);

        final String xmlTestStr =
            "<test><a refType=\"TS\" href=\"er:#co_pp_sp_1000600_50660000823d1\" class=\"test\">Test123</a></test>";
        final String expectedResult =
            "<test><a refType=\"TS\" href=\"er:#ABC1234/co_pp_sp_1000600_50660000823d1\" class=\"test\">Test123</a></test>";

        testHelper(mockImgService, xmlTestStr, expectedResult);
    }

    @Test
    public void testNonRutterAnchorWithSP() throws SAXException {
        final ImageService mockImgService = EasyMock.createMock(ImageService.class);

        final String xmlTestStr =
            "<test><a href=\"er:#co_pp_sp_1000600_50660000823d1\" class=\"test\">Test123</a></test>";
        final String expectedResult =
            "<test><a href=\"er:#ABC1234/co_pp_50660000823d1\" class=\"test\">Test123</a></test>";

        testHelper(mockImgService, xmlTestStr, expectedResult);
    }

    @Test
    public void testSimpleNonEmptyAnchorWithERSP() throws SAXException {
        final ImageService mockImgService = EasyMock.createMock(ImageService.class);

        final String xmlTestStr =
            "<test><a href=\"er:#ABC1234/co_pp_sp_1000600_50660000823d1\" class=\"test\">Test123</a></test>";
        final String expectedResult =
            "<test><a href=\"er:#ABC1234/co_pp_50660000823d1\" class=\"test\">Test123</a></test>";

        testHelper(mockImgService, xmlTestStr, expectedResult);
    }

    @Test
    public void testSimpleNonEmptyAnchorWithERmissingSP() throws SAXException {
        final ImageService mockImgService = EasyMock.createMock(ImageService.class);

        final String xmlTestStr =
            "<test><a href=\"er:#co_pp_sp_1000600_50660000823d1\" class=\"test\">Test123</a></test>";
        final String expectedResult =
            "<test><a href=\"er:#ABC1234/co_pp_50660000823d1\" class=\"test\">Test123</a></test>";

        testHelper(mockImgService, xmlTestStr, expectedResult);
    }

    @Test
    public void testSimpleNonEmptyAnchorWithBadSP() throws SAXException {
        final ImageService mockImgService = EasyMock.createMock(ImageService.class);

//		String xmlTestStr = "<test><a href=\"er:#_sp_\" class=\"test\">Test123</a></test>";
        final String xmlTestStr = "<test><a href=\"er:#co_pp_sp_1000600\" class=\"test\">Test123</a></test>";
        final String expectedResult = "<test><a href=\"er:#ABC1234/co_pp_1000600\" class=\"test\">Test123</a></test>";

        testHelper(mockImgService, xmlTestStr, expectedResult);
    }

    @Test
    public void testSimpleAnchorNotStripped() throws SAXException {
        final ImageService mockImgService = EasyMock.createMock(ImageService.class);

        final String xmlTestStr = "<test><a id=\"co_Test\">Test123</a></test>";
        final String expectedResult = "<test><a id=\"co_Test\">Test123</a></test>";

        testHelper(mockImgService, xmlTestStr, expectedResult);
    }

    @Test
    public void testSimpleAnchorNotStrippedDup() throws SAXException {
        final ImageService mockImgService = EasyMock.createMock(ImageService.class);

        final String xmlTestStr = "<test><a id=\"co_Test\">Test123</a><a id=\"co_Test\">Test123</a></test>";
        final String expectedResult = "<test><a id=\"co_Test\">Test123</a><a id=\"co_Testdup1\">Test123</a></test>";

        testHelper(mockImgService, xmlTestStr, expectedResult);
    }

    @Test
    public void testSimpleAnchorNoIdDup() throws SAXException {
        final ImageService mockImgService = EasyMock.createMock(ImageService.class);

        final String xmlTestStr = "<test><a href=\"co_href\">Test123</a><a id=\"co_Test\">Test123</a></test>";
        final String expectedResult = "<test><a href=\"co_href\">Test123</a><a id=\"co_Test\">Test123</a></test>";

        testHelper(mockImgService, xmlTestStr, expectedResult);
    }

    @Test
    public void testAnchorNotStrippedDup() throws SAXException {
        final ImageService mockImgService = EasyMock.createMock(ImageService.class);

        final String xmlTestStr =
            "<test><a id=\"co_Test\" class=\"co_class\" href=\"co_href\">Test123</a><a id=\"co_Test\" class=\"co_class\" href=\"co_href\">Test123</a></test>";
        final String expectedResult =
            "<test><a id=\"co_Test\" class=\"co_class\" href=\"co_href\">Test123</a><a id=\"co_Testdup1\" class=\"co_class\" href=\"co_href\">Test123</a></test>";

        testHelper(mockImgService, xmlTestStr, expectedResult);
    }

    @Test
    public void testAnchorNotStrippedDup2() throws SAXException {
        final ImageService mockImgService = EasyMock.createMock(ImageService.class);

        final String xmlTestStr =
            "<test><a id=\"co_Test\" class=\"co_class\" href=\"co_href\">Test123</a><a id=\"co_Test2\" class=\"co_class2\" href=\"co_href2\">Test234</a><a id=\"co_Test\" class=\"co_class\" href=\"co_href\">Test123</a><a id=\"co_Test\" class=\"co_class\" href=\"co_href\">Test123</a></test>";
        final String expectedResult =
            "<test><a id=\"co_Test\" class=\"co_class\" href=\"co_href\">Test123</a><a id=\"co_Test2\" class=\"co_class2\" href=\"co_href2\">Test234</a><a id=\"co_Testdup1\" class=\"co_class\" href=\"co_href\">Test123</a><a id=\"co_Testdup2\" class=\"co_class\" href=\"co_href\">Test123</a></test>";

        testHelper(mockImgService, xmlTestStr, expectedResult);
    }

    @Test
    public void testNoAnchorInput() throws SAXException {
        final ImageService mockImgService = EasyMock.createMock(ImageService.class);

        final String xmlTestStr = "<test><testing/><testing123>123</testing123></test>";
        final String expectedResult = "<test><testing/><testing123>123</testing123></test>";

        testHelper(mockImgService, xmlTestStr, expectedResult);
    }

    @Test
    public void testSetMaxHeight() {
        final long newMaxHeight = 23L;
        anchorFilter.setImgMaxHeight(newMaxHeight);
        assertEquals(newMaxHeight, anchorFilter.getImgMaxHeight());
    }

    @Test
    public void testSetMaxWidth() {
        final long newMaxWidth = 27L;
        anchorFilter.setImgMaxWidth(newMaxWidth);
        assertEquals(newMaxWidth, anchorFilter.getImgMaxWidth());
    }

    @Test
    public void testWithModifiedHeight() throws SAXException {
        final long newMaxHeight = 27L;
        anchorFilter.setImgMaxWidth(newMaxHeight);

        final ImageMetadataEntityKey key = new ImageMetadataEntityKey(testJobId, testGuid, docGuid);

        final ImageService mockImgService = EasyMock.createMock(ImageService.class);
        EasyMock.expect(mockImgService.findImageMetadata(key)).andReturn(regularImgMetadata);
        EasyMock.replay(mockImgService);

        final String xmlTestStr =
            "<test><a href=\"http://www.test/Blob/" + testGuid + ".jpg?\" type=\"image/jpeg\"/></test>";
        final String expectedResult = "<test><img src=\"er:#" + testGuid + "\" class=\"tr_image\"/></test>";

        testHelper(mockImgService, xmlTestStr, expectedResult);
    }

    @Test
    public void testWithModifiedWidth() throws SAXException {
        final long newMaxWidth = 27L;
        anchorFilter.setImgMaxWidth(newMaxWidth);

        final ImageMetadataEntityKey key = new ImageMetadataEntityKey(testJobId, testGuid, docGuid);

        final ImageService mockImgService = EasyMock.createMock(ImageService.class);
        EasyMock.expect(mockImgService.findImageMetadata(key)).andReturn(regularImgMetadata);
        EasyMock.replay(mockImgService);

        final String xmlTestStr =
            "<test><a href=\"http://www.test/Blob/" + testGuid + ".jpg?\" type=\"image/jpeg\"/></test>";
        final String expectedResult = "<test><img src=\"er:#" + testGuid + "\" class=\"tr_image\"/></test>";

        testHelper(mockImgService, xmlTestStr, expectedResult);
    }

    @Test
    public void testMultipleAnchors() throws SAXException {
        final ImageMetadataEntityKey key = new ImageMetadataEntityKey(testJobId, testGuid, docGuid);
        final ImageMetadataEntityKey key2 =
            new ImageMetadataEntityKey(testJobId, "AFA730F80D58A11DCBFA7F697EE59258B", docGuid);

        final ImageService mockImgService = EasyMock.createMock(ImageService.class);
        EasyMock.expect(mockImgService.findImageMetadata(key)).andReturn(regularImgMetadata);
        EasyMock.expect(mockImgService.findImageMetadata(key2)).andReturn(largeImgMetadata);
        EasyMock.replay(mockImgService);

        final String xmlTestStr = "<test><a href=\"http://www.test/Blob/"
            + testGuid
            + ".jpg?\" type=\"image/jpeg\"/><a href=\"http://www.test/Blob/"
            + "AFA730F80D58A11DCBFA7F697EE59258B.jpg?\" type=\"image/jpeg\"/>"
            + "<a href=\"#\" class=\"test\"/></test>";
        final String expectedResult = "<test><img src=\"er:#"
            + testGuid
            + "\"/><img src=\"er:#AFA730F80D58A11DCBFA7F697EE59258B\" class=\"tr_image\"/></test>";

        testHelper(mockImgService, xmlTestStr, expectedResult);
    }

    @Test
    public void testPDFAnchor() throws SAXException {
        final ImageService mockImgService = EasyMock.createMock(ImageService.class);

        final String xmlTestStr = "<test><a href=\"http://www.test/Link/Document/Blob/AFA730F80D58A11DCBFA7F697EE59258B"
            + ".pdf?test=testing\" type=\"application/pdf\">TestPDF</a></test>";
        final String expectedResult = "<test><a href=\"er:#AFA730F80D58A11DCBFA7F697EE59258B\">TestPDF</a></test>";

        testHelper(mockImgService, xmlTestStr, expectedResult);
    }

    @Test
    public void testPDFAnchorWithCssClass() throws SAXException {
        final ImageService mockImgService = EasyMock.createMock(ImageService.class);

        final String xmlTestStr = "<test><a href=\"http://www.test/Link/Document/Blob/AFA730F80D58A11DCBFA7F697EE59258B"
            + ".pdf?test=testing\" class=\"testCSS\" type=\"application/pdf\">TestPDF</a></test>";
        final String expectedResult =
            "<test><a href=\"er:#AFA730F80D58A11DCBFA7F697EE59258B\" class=\"testCSS\">TestPDF</a></test>";

        testHelper(mockImgService, xmlTestStr, expectedResult);
    }
}
