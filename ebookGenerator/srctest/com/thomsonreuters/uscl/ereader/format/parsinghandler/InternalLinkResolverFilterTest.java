package com.thomsonreuters.uscl.ereader.format.parsinghandler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import com.thomsonreuters.uscl.ereader.format.service.InternalLinkResolverService;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadata;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocumentMetadataAuthority;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.PaceMetadata;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.PaceMetadataService;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.PaceMetadataServiceImpl;
import com.thomsonreuters.uscl.ereader.util.UrlParsingUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.xml.serializer.Method;
import org.apache.xml.serializer.OutputPropertiesFactory;
import org.apache.xml.serializer.Serializer;
import org.apache.xml.serializer.SerializerFactory;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Component tests for InternalLinkResolverFilter.
 *
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = InternalLinkResolverFilterTest.Config.class)
public final class InternalLinkResolverFilterTest {
    private static final String VERSION = "1";
    private static final String DOC_UUID = "NF8C65500AFF711D8803AE0632FEDDFBF";
    private static final String DOC_FAMILY_UUID = "IC6A94E80FF6011DC95B0EEFA5102EA59";
    private static final String FIRST_LINE_CITE = "42USCAS1395W-133";
    private static final String FIRST_LINE_CITE_WITH_DATE = FIRST_LINE_CITE + "(11/1/18)";
    private static final String DOC_UUID_2 = "docUuid2";
    private static final String DOC_FAMILY_UUID_2 = "docUuid2";
    private static final String FIRST_LINE_CITE_2 = "FEDFORMS";
    private static final String DOC_UUID_CURRENT = "dummyDocGuid";
    private static final String DOC_FAMILY_UUID_CURRENT = "IC6A94E80FF6011DC95B0EEFA5102EA58";
    private InternalLinkResolverFilter internalLinksFilter;
    private Serializer serializer;
    private DocumentMetadataAuthority mockDocumentMetadataAuthority;
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();
    @Autowired
    private InternalLinkResolverService internalLinkResolverService;
    @Autowired
    private PaceMetadataService mockPaceMetadataService;

    @Before
    public void setUp() throws Exception {
        PaceMetadata mockPaceMetadata = EasyMock.createMock(PaceMetadata.class);
        mockPaceMetadata.setPublicationName("CCPEMPLOYMENT");
        mockPaceMetadata.setAuditId(Long.valueOf("3862349"));
        mockPaceMetadata.setActive("L");
        mockPaceMetadata.setAuthorityName("EBOOK");
        mockPaceMetadata.setLongPubName("EBOOK_HELLO");
        mockPaceMetadata.setPublicationCode(126977L);
        mockPaceMetadata.setSpecificCategory("TEST");
        mockPaceMetadata.setStdPubName("CCPEMP");
        mockPaceMetadata.setPublicationId(Long.valueOf("3862349"));

        final List<PaceMetadata> pacemetadataList = new ArrayList<>();
        EasyMock.expect(mockPaceMetadataService.findAllPaceMetadataForPubCode(126977L))
            .andReturn(pacemetadataList);

        EasyMock.replay(mockPaceMetadataService);
        mockDocumentMetadataAuthority = new DocumentMetadataAuthority(Collections.emptySet());

        final SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);

        final SAXParser saxParser = factory.newSAXParser();

        File tempDir = temporaryFolder.newFolder("junit_internalLinking");

        final File internalLinkResolverTestFile = new File(tempDir, "internalLinkResolverTestFile.txt");
        writeDocumentLinkFile(internalLinkResolverTestFile);

        internalLinksFilter = new InternalLinkResolverFilter(
                internalLinkResolverService,
            mockDocumentMetadataAuthority,
            internalLinkResolverTestFile,
            DOC_UUID_CURRENT,
            VERSION);
        internalLinksFilter.setParent(saxParser.getXMLReader());

        final Properties props = OutputPropertiesFactory.getDefaultMethodProperties(Method.XHTML);
        props.setProperty("omit-xml-declaration", "yes");
        serializer = SerializerFactory.getSerializer(props);
    }

    @After
    public void tearDown() {
        serializer = null;
        internalLinksFilter = null;
        EasyMock.reset(mockPaceMetadataService);
    }

    @Test
    public void testGetDocumentUuidFromResourceUrl() {
        String resourceUrl =
            "https://a.next.westlaw.com/Document/FullText?Iff5a5aaa7c8f11da9de6e47d6d5aa7a5/View/FullText.html?transitionType=Default&contextData=(sc.Default)";
        Map<String, String> urlValues = UrlParsingUtil.parseUrlContents(resourceUrl);
        String documentUuid = urlValues.get("documentUuid");
        String expectedUuid = "Iff5a5aaa7c8f11da9de6e47d6d5aa7a5";
        assertEquals(expectedUuid, documentUuid);
        resourceUrl =
            "https://a.next.westlaw.com/Document/FullText?Iff5a5aaa7c-8f11da9de6e-47d6d5aa7a5/View/FullText.html?transitionType=Default&contextData=(sc.Default)";
        urlValues = UrlParsingUtil.parseUrlContents(resourceUrl);
        documentUuid = urlValues.get("documentUuid");
        expectedUuid = "Iff5a5aaa7c-8f11da9de6e-47d6d5aa7a5";
        assertEquals(expectedUuid, documentUuid);
    }

    @Test
    public void testExpectedLinkParameterFromResourceUrl() throws Exception {
        final String resourceUrl =
                "http://www.westlaw.com/Link/Document/FullText?findType=L&amp;pubNum=1000546&cite=42USCAS1395W-133&originationContext=ebook&amp;RS=ebbp3.0&amp;vr=3.0#co_pp_8b3b0000958a";
        final Map<String, String> urlValues = UrlParsingUtil.parseUrlContents(resourceUrl);
        final String linkParameter = urlValues.get("reference");
        final String normalizedCite = urlValues.get("cite");
        final String expectedLinkParameter = "co_pp_8b3b0000958a";
        assertEquals(expectedLinkParameter, linkParameter);
        assertEquals(FIRST_LINE_CITE, normalizedCite);
    }

    @Test
    public void testGetNormalizedCiteFromResourceUrl() throws Exception {
        String resourceUrl =
                "https://1.next.westlaw.com/Link/Document/FullText?findType=Y&pubNum=119616&cite=SECOPINION\u00A739%3A7&originationContext=ebook";
        Map<String, String> urlValues = UrlParsingUtil.parseUrlContents(resourceUrl);
        String normalizedCite = urlValues.get("cite");
        String expectedNormalizedCite = "SECOPINIONS39:7";
        assertEquals(expectedNormalizedCite, normalizedCite);

        resourceUrl =
                "https://1.next.westlaw.com/Link/Document/FullText?findType=L&pubNum=1000600&cite=USFRCPR20&originatingDoc=I86827039c15111ddb9c7909664ff7808&refType=LQ&amp;originationContext=ebook";

        urlValues = UrlParsingUtil.parseUrlContents(resourceUrl);
        normalizedCite = urlValues.get("cite");
        expectedNormalizedCite = "USFRCPR20";
        assertEquals(expectedNormalizedCite, normalizedCite);

        resourceUrl =
                "https://www.westlaw.com/Link/Document/FullText?findType=Y&pubNum=126977&cite=CCPEMPs2%3A89&originationContext=ebook&RS=ebbp3.0&vr=3.0";

        urlValues = UrlParsingUtil.parseUrlContents(resourceUrl);
        normalizedCite = urlValues.get("cite");
        System.out.println(normalizedCite);
        expectedNormalizedCite = "CCPEMPS2:89";
        assertEquals(expectedNormalizedCite, normalizedCite);
    }

    @Test
    public void testGetLinkParameter() throws Exception {
        mockDocumentMetadataAuthority.initializeMaps(getDocsMetadata(FIRST_LINE_CITE));

        final String inputLink =
            "<a id=\"co_link_I2c86d170883611e19a0cc90d102a8215\" class=\"co_link co_drag ui-draggable\" href=\"http://www.westlaw.com/Link/Document/FullText?findType=L&amp;pubNum=1000546&amp;cite=42USCAS1395W-133&amp;originationContext=ebook&amp;RS=ebbp3.0&amp;vr=3.0#co_pp_8b3b0000958a4\">section 1395w-133(a)</a>";
        final String expectedLink =
            "<a id=\"co_link_I2c86d170883611e19a0cc90d102a8215\" class=\"co_link co_drag ui-draggable\" href=\"er:#IC6A94E80FF6011DC95B0EEFA5102EA59/co_pp_8b3b0000958a4\">section 1395w-133(a)</a>";

        testHelperLinks(inputLink, expectedLink);
    }

    @Test
    public void testGetLinkParameterWithDate() throws Exception {
        mockDocumentMetadataAuthority.initializeMaps(getDocsMetadata(FIRST_LINE_CITE_WITH_DATE));

        final String inputLink =
                "<a id=\"co_link_I2c86d170883611e19a0cc90d102a8215\" class=\"co_link co_drag ui-draggable\" href=\"http://www.westlaw.com/Link/Document/FullText?findType=L&amp;pubNum=1000546&amp;cite=42USCAS1395W-133&amp;originationContext=ebook&amp;RS=ebbp3.0&amp;vr=3.0#co_pp_8b3b0000958a4\">section 1395w-133(a)</a>";
        final String expectedLink =
                "<a id=\"co_link_I2c86d170883611e19a0cc90d102a8215\" class=\"co_link co_drag ui-draggable\" href=\"er:#IC6A94E80FF6011DC95B0EEFA5102EA59/co_pp_8b3b0000958a4\">section 1395w-133(a)</a>";

        testHelperLinks(inputLink, expectedLink);
    }


    @Test
    public void testSplitDocLink() throws Exception {
        mockDocumentMetadataAuthority.initializeMaps(getDocsMetadataSplit(FIRST_LINE_CITE, null));

        final String inputCite = "42USCAS1395W-133";
        final String expectedHref = "er:us/an/splitBookTitle/v1#IC6A94E80FF6011DC95B0EEFA5102EA59/co_pp_8b3b0000958a4";

        testHelper(inputCite, expectedHref);
    }

    @Test
    public void testLinkwithinDoc() throws Exception {
        mockDocumentMetadataAuthority.initializeMaps(getDocsMetadata(FIRST_LINE_CITE));

        final String inputLink =
            "<a id=\"co_link_I2c86d170883611e19a0cc90d102a8215\" class=\"co_link co_drag ui-draggable\" href=\"http://www.westlaw.com/Link/Document/FullText?findType=L&amp;pubNum=1000546&amp;cite=42USCAS1395W-133&amp;originationContext=ebook&amp;RS=ebbp3.0&amp;vr=3.0#co_pp_8b3b0000958a4\">section 1395w-133(a)</a>";
        final String expectedLink =
            "<a id=\"co_link_I2c86d170883611e19a0cc90d102a8215\" class=\"co_link co_drag ui-draggable\" href=\"er:#IC6A94E80FF6011DC95B0EEFA5102EA59/co_pp_8b3b0000958a4\">section 1395w-133(a)</a>";

        testHelperLinks(inputLink, expectedLink);
    }

    @Test
    public void testGetLinkParameterwithSpace() throws Exception {
        mockDocumentMetadataAuthority.initializeMaps(getDocsMetadata(FIRST_LINE_CITE));

        final String inputLink =
            "<a id=\"co_link_I2c86d170883611e19a0cc90d102a8215\" class=\"co_link co_drag ui-draggable\" href=\"http://www.westlaw.com/Link/Document/FullText?findType=L&amp;pubNum=1000546%20&amp;cite=42USCAS1395W-133&amp;originationContext=ebook&amp;RS=ebbp3.0&amp;vr=3.0#co_pp_8b3b0000958a4\">section 1395w-133(a)</a>";
        final String expectedLink =
            "<a id=\"co_link_I2c86d170883611e19a0cc90d102a8215\" class=\"co_link co_drag ui-draggable\" href=\"er:#IC6A94E80FF6011DC95B0EEFA5102EA59/co_pp_8b3b0000958a4\">section 1395w-133(a)</a>";

        testHelperLinks(inputLink, expectedLink);
    }

    @Test
    public void testGetLinkParameterwithOnlySpace() throws Exception {
        mockDocumentMetadataAuthority.initializeMaps(getDocsMetadata(FIRST_LINE_CITE));

        final String inputLink =
            "<a id=\"co_link_I2c86d170883611e19a0cc90d102a8215\" class=\"co_link co_drag ui-draggable\" href=\"http://www.westlaw.com/Link/Document/FullText?findType=L&amp;pubNum=%20&amp;cite=42USCAS1395W-133&amp;originationContext=ebook&amp;RS=ebbp3.0&amp;vr=3.0#co_pp_8b3b0000958a4\">section 1395w-133(a)</a>";
        final String expectedLink =
            "<a id=\"co_link_I2c86d170883611e19a0cc90d102a8215\" class=\"co_link co_drag ui-draggable\" href=\"er:#IC6A94E80FF6011DC95B0EEFA5102EA59/co_pp_8b3b0000958a4\">section 1395w-133(a)</a>";

        testHelperLinks(inputLink, expectedLink);
    }

    @Test
    public void testRutterLink() throws Exception {
        mockDocumentMetadataAuthority.initializeMaps(getDocsMetadata(FIRST_LINE_CITE));

        final String inputLink =
            "<a id=\"co_link_I2c86d170883611e19a0cc90d102a8215\" class=\"co_link co_drag ui-draggable\" href=\"http://www.westlaw.com/Link/Document/FullText?findType=L&amp;pubNum=%20&amp;cite=42USCAS1395W-133&amp;refType=TS&amp;originationContext=ebook&amp;RS=ebbp3.0&amp;vr=3.0#co_pp_8b3b0000958a4\">section 1395w-133(a)</a>";
        final String expectedLink =
            "<a id=\"co_link_I2c86d170883611e19a0cc90d102a8215\" class=\"co_internalLink\" href=\"er:#IC6A94E80FF6011DC95B0EEFA5102EA59/co_pp_8b3b0000958a4\" refType=\"TS\">section 1395w-133(a)</a>";

        testHelperLinks(inputLink, expectedLink);
    }

    @Test
    @Ignore
    public void testGetNormalizedCiteWithPaceMetadata() throws Exception {
        final String documentMetadataFirstLineCite = "CCPEMPs2:89";
        mockDocumentMetadataAuthority.initializeMaps(getDocsMetadata(documentMetadataFirstLineCite));

        final String inputCite = "CCPEMPs2%3A89";
        final String expectedHref = "er:#I7dd370d22f6d11d997cad7305e16d23d/co_pp_8b3b0000958a4";

        testHelper(inputCite, expectedHref);
    }

    @Test
    public void testGetNormalizedCiteDocumentUuidFromResourceUrl() throws Exception {
        final String resourceUrl =
            "https://1.next.westlaw.com/Link/Document/FullText?findType=l&pubNum=1077005&cite=UUID%28ID4D58042D3-43461C8C9EE-73AA2A319F3%29&originationContext=ebook";
        final Map<String, String> urlValues = UrlParsingUtil.parseUrlContents(resourceUrl);

        final String documentUuid = urlValues.get("documentUuid");
        final String expectedNormalizedCiteUUID = "ID4D58042D3-43461C8C9EE-73AA2A319F3";
        assertEquals(expectedNormalizedCiteUUID,documentUuid);
    }

    @Test
    public void testGetSerialNumberFromResourceUrl() throws Exception {
        final String resourceUrl =
            "https://a.next.westlaw.com/Document/FullText?findType=Y&serNum=123456&transitionType=Default&contextData=(sc.Default)";
        final Map<String, String> urlValues = UrlParsingUtil.parseUrlContents(resourceUrl);
        final String serialNumber = urlValues.get("serNum");
        final String expectedSerialNumber = "123456";
        assertEquals(expectedSerialNumber, serialNumber);
    }

    @Test
    public void testGetTOCGuid() throws Exception {
        final String documentMetadataFirstLineCite = "42USCAS1395W-133";
        mockDocumentMetadataAuthority.initializeMaps(getDocsMetadata(documentMetadataFirstLineCite));

        final String inputCite = "42USCAS1395W-133";
        final String expectedHref = "er:#IC6A94E80FF6011DC95B0EEFA5102EA59/co_pp_8b3b0000958a4";

        testHelper(inputCite, expectedHref);
    }

    @Test
    public void testDashesAndWhitespaces() throws Exception {
        final String firstLineCite = "FLETCHER-FRM CH 1 COR";

        mockDocumentMetadataAuthority.initializeMaps(getDocsMetadata(firstLineCite));

        final String inputCite = "FLETCHER-FRM%20CH%201%20COR";
        final String expectedHref = "er:#IC6A94E80FF6011DC95B0EEFA5102EA59/co_pp_8b3b0000958a4";

        testHelper(inputCite, expectedHref);
    }

    @Test
    public void testThirdLineCite() throws Exception {
        final String firstLineCite = "6 FEDFORMS § 1:9";
        final String thirdLineCite = "West&apos;s Fed. Forms, Bankruptcy Courts § 1:9 (5th ed.)";

        mockDocumentMetadataAuthority.initializeMaps(getDocsMetadata(firstLineCite, thirdLineCite));

        final String inputCite = "WESTSFEDFORMSBANKRUPTCYCOURTSs1%3A9";
        final String expectedHref = "er:#IC6A94E80FF6011DC95B0EEFA5102EA59/co_pp_8b3b0000958a4";

        testHelper(inputCite, expectedHref);
    }

    @Test
    public void testThirdLineCiteSplitBook() throws Exception {
        final String firstLineCite = "1B FEDFORMS § 2:41";
        final String thirdLineCite = "West&apos;s Fed. Forms, Courts of Appeals § 2:41 (6th ed.)";

        mockDocumentMetadataAuthority.initializeMaps(getDocsMetadataSplit(firstLineCite, thirdLineCite));

        final String inputCite = "WESTSFEDFORMSCOURTSOFAPPEALSs2%3A41";
        final String expectedHref = "er:us/an/splitBookTitle/v1#IC6A94E80FF6011DC95B0EEFA5102EA59/co_pp_8b3b0000958a4";

        testHelper(inputCite, expectedHref);
    }

    @Test
    public void testNormalizedCiteWithParagraphSign() throws Exception {
        final String firstLineCite = "GAEVIDENCE S 5:7";
        final String inputCite = "GAEVIDENCEs5%3A7";
        final String expectedHref = "er:#IC6A94E80FF6011DC95B0EEFA5102EA59/co_pp_8b3b0000958a4";

        mockDocumentMetadataAuthority.initializeMaps(getDocsMetadata(firstLineCite));
        testHelper(inputCite, expectedHref);
    }

    @Test
    public void testNormalizedCiteNoParagraphSign() throws Exception {
        final String firstLineCite = "GAEVIDENCE S 5:7";
        final String inputCite = "GAEVIDENCE5%3A7";
        final String expectedHref = "er:#IC6A94E80FF6011DC95B0EEFA5102EA59/co_pp_8b3b0000958a4";

        mockDocumentMetadataAuthority.initializeMaps(getDocsMetadata(firstLineCite));
        testHelper(inputCite, expectedHref);
    }

    private Set<DocMetadata> getDocsMetadataSplit(final String firstLineCite, final String thirdLineCite) {
        final DocMetadata dm = getDocMetadata(DOC_UUID, DOC_FAMILY_UUID, firstLineCite, thirdLineCite, "us/an/splitBookTitle");
        return toCollectionWithDocMetadataCurrent(dm);
    }

    private Set<DocMetadata> getDocsMetadata(final String firstLineCite, final String thirdLineCite) {
        final DocMetadata dm = getDocMetadata(DOC_UUID, DOC_FAMILY_UUID, firstLineCite, thirdLineCite, null);
        return toCollectionWithDocMetadataCurrent(dm);
    }

    private Set<DocMetadata> getDocsMetadata(final String firstLineCite) {
        final DocMetadata dm = getDocMetadata(DOC_UUID, DOC_FAMILY_UUID, firstLineCite, null, null);

        final DocMetadata dm2 = getDocMetadata(DOC_UUID_2, DOC_FAMILY_UUID_2, FIRST_LINE_CITE_2, null, null);
        return toCollectionWithDocMetadataCurrent(dm, dm2);
    }

    private DocMetadata getDocMetadata(final String docUuid, final String docFamilyUuid, final String firstLineCite, final String thirdLineCite, final String splitTitleId) {
        final DocMetadata dm = new DocMetadata();
        dm.setDocFamilyUuid(docFamilyUuid);
        dm.setDocUuid(docUuid);
        dm.setNormalizedFirstlineCite(firstLineCite);
        dm.setThirdlineCite(thirdLineCite);
        dm.setSpitBookTitle(splitTitleId);
        return dm;
    }

    private Set<DocMetadata> toCollectionWithDocMetadataCurrent(final DocMetadata...dm) {
        final Set<DocMetadata> dms = new HashSet<>(Arrays.asList(dm));
        dms.add(getDocMetadataCurrent());
        return dms;
    }

    private DocMetadata getDocMetadataCurrent() {
        return getDocMetadata(DOC_UUID_CURRENT, DOC_FAMILY_UUID_CURRENT, null, null, null);
    }

    public void testHelper(final String inputCite, final String expectedHref) throws SAXException {
        final String inputXML =
                String.format("<a id=\"co_link_I2c86d170883611e19a0cc90d102a8215\" class=\"co_link co_drag ui-draggable\" href=\"http://www.westlaw.com/Link/Document/FullText?findType=L&amp;pubNum=1000546&amp;cite=%s&amp;originationContext=ebook&amp;RS=ebbp3.0&amp;vr=3.0#co_pp_8b3b0000958a4\">section 1395w-133(a)</a>", inputCite);
        final String expectedResult =
                String.format("<a id=\"co_link_I2c86d170883611e19a0cc90d102a8215\" class=\"co_link co_drag ui-draggable\" href=\"%s\">section 1395w-133(a)</a>", expectedHref);
        testHelperLinks(inputXML, expectedResult);
    }

    /**
     * Helper method that sets up the repeating pieces of each test and modifies the
     * ImageService values that are returned along with the input and output.
     *
     * @param inputXML input string for the test.
     * @param expectedResult the expected output for the specified input string.
     */
    public void testHelperLinks(final String inputXML, final String expectedResult) throws SAXException {
        try (ByteArrayInputStream input = new ByteArrayInputStream(inputXML.getBytes());
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            serializer.setOutputStream(output);

            internalLinksFilter.setContentHandler(serializer.asContentHandler());
            internalLinksFilter.parse(new InputSource(input));

            final String result = output.toString();

            assertEquals(expectedResult, result);
        } catch (final SAXException e) {
            throw e;
        } catch (final Throwable e) {
            fail("Encountered exception during test: " + e.getMessage());
        }
    }

    protected void writeDocumentLinkFile(final File internalLinkResolverTestFile) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(internalLinkResolverTestFile))) {
            writer.write("NF8C65500AFF711D8803AE0632FEDDFBF,N129FCFD29AA24CD5ABBAA83B0A8A2D7B275|");
            writer.newLine();
            writer.write("NDF4CB9C0AFF711D8803AE0632FEDDFBF,N8E37708B96244CD1B394155616B3C66F190|");

            writer.newLine();

            writer.flush();
        } catch (final IOException e) {
            final String errMessage =
                "Encountered an IO Exception while processing: " + internalLinkResolverTestFile.getAbsolutePath();
            log.error(errMessage, e);
        }
        log.debug("size of file : " + internalLinkResolverTestFile.length());
    }

    @Configuration
    public static class Config {
        @Bean
        public InternalLinkResolverService internalLinkResolverService() {
            return new InternalLinkResolverService();
        }
        @Bean
        public PaceMetadataService paceMetadataService() {
            return EasyMock.createMock(PaceMetadataServiceImpl.class);
        }
    }
}
