package com.thomsonreuters.uscl.ereader.assemble.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterPage;
import com.thomsonreuters.uscl.ereader.proview.TableOfContents;
import com.thomsonreuters.uscl.ereader.proview.TitleMetadata;
import com.thomsonreuters.uscl.ereader.proview.TocEntry;
import com.thomsonreuters.uscl.ereader.proview.TocNode;
import com.thomsonreuters.uscl.ereader.util.FileUtilsFacade;
import com.thomsonreuters.uscl.ereader.util.UuidGenerator;
import org.apache.commons.io.IOUtils;
import org.apache.xml.serializer.Method;
import org.apache.xml.serializer.OutputPropertiesFactory;
import org.apache.xml.serializer.Serializer;
import org.apache.xml.serializer.SerializerFactory;
import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Difference;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public final class SplitTocManifestFilterTest extends TitleMetadataTestBase {
    private UuidGenerator uuidGenerator;
    private SAXParserFactory saxParserFactory;
    private SplitTocManifestFilter splitTocManifestFilterTest;
    private TitleMetadata titleMetadata;
    private OutputStream titleManifestOutputStream;
    private InputStream tocXml;
    private Serializer serializer;
    private SAXParser saxParser;
    private XMLReader xmlReader;
    private ByteArrayOutputStream resultStream;
    private FileUtilsFacade mockFileUtilsFacade;
    private PlaceholderDocumentService mockPlaceholderDocumentService;
    private TitleMetadataServiceImpl titleMetadataService;
    private File altIdFile;

    private static final String EXPECTED__SPLIT_TOC =
        "<toc><titlebreak/><entry s=\"yarr/pirates#FrontMatterTitle/FrontMatterTitleAnchor\"><text>Title Page</text></entry><entry s=\"yarr/pirates#Copyright/PublishingInformationAnchor\"><text>PUBLISHING INFORMATION</text><entry s=\"yarr/pirates#Copyright/CopyrightAnchor\"><text>Copyright Page</text></entry><entry s=\"yarr/pirates#ResearchAssistance/ResearchAssistanceAnchor\"><text>Additional Information or Research Assistance</text></entry><entry s=\"yarr/pirates#Westlaw/WestlawAnchor\"><text>Westlaw</text></entry></entry>";
    private static final String EXPECTED_END_MANIFEST = "</title>";
    private File altIdDir;

    private String lastupdated = new SimpleDateFormat("yyyyMMdd").format(new Date());
    private String onlineexpiration = "29991231";

    @Rule
    public static TemporaryFolder tempDirectory = new TemporaryFolder();
    private File temporaryDirectory;
    private Map<String, List<String>> docImageMap;

    @Override
    @Before
    public void setUp() throws Exception {
        // Add 2 years to the current date for online expiration
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.YEAR, 2);

        tempDirectory.create();
        temporaryDirectory = tempDirectory.newFolder("temp");
        uuidGenerator = new UuidGenerator();
        saxParserFactory = SAXParserFactory.newInstance();
        titleMetadata = getTitleMetadata();
        titleManifestOutputStream = new FileOutputStream(tempDirectory.newFile("title.xml"));
        saxParserFactory.setNamespaceAware(true);
        saxParser = saxParserFactory.newSAXParser();
        xmlReader = saxParser.getXMLReader();
        mockFileUtilsFacade = EasyMock.createMock(FileUtilsFacade.class);
        mockPlaceholderDocumentService = EasyMock.createMock(PlaceholderDocumentService.class);
        titleMetadataService = new TitleMetadataServiceImpl();
        final Properties props = OutputPropertiesFactory.getDefaultMethodProperties(Method.XML);
        props.setProperty("omit-xml-declaration", "yes");

        serializer = SerializerFactory.getSerializer(props);
        resultStream = new ByteArrayOutputStream(1024);
        serializer.setOutputStream(resultStream);

        // URL pathToClass = this.getClass().getResource("yarr_pirates.csv");
        // altIdFile = new File(pathToClass.toURI());

        tocXml = new ByteArrayInputStream(
            "<EBook><EBookToc><Name>BLARGH</Name><Guid>TOC_GUID</Guid><DocumentGuid>DOC_GUID</DocumentGuid></EBookToc></EBook>"
                .getBytes());
        docImageMap = new HashMap<>();
        splitTocManifestFilterTest = new SplitTocManifestFilter(
            titleMetadata,
            new HashMap<String, String>(),
            uuidGenerator,
            temporaryDirectory,
            mockFileUtilsFacade,
            mockPlaceholderDocumentService,
            docImageMap);
        splitTocManifestFilterTest.setParent(xmlReader);
        splitTocManifestFilterTest.setContentHandler(serializer.asContentHandler());
    }

    @Override
    @After
    public void tearDown() throws Exception {
        //Intentionally left blank
    }

    @Test
    public void testVoidInput() {
        // titleMetadata test
        try {
            new SplitTocManifestFilter(
                null,
                new HashMap<String, String>(),
                uuidGenerator,
                temporaryDirectory,
                mockFileUtilsFacade,
                mockPlaceholderDocumentService,
                docImageMap);
            fail("Should throw IllegalArugmentException");
        } catch (final IllegalArgumentException e) {
            // expected exception
            e.printStackTrace();
        }
        // docImage Map test
        try {
            new SplitTocManifestFilter(
                titleMetadata,
                null,
                uuidGenerator,
                temporaryDirectory,
                mockFileUtilsFacade,
                mockPlaceholderDocumentService,
                docImageMap);
            fail("Should throw IllegalArugmentException");
        } catch (final IllegalArgumentException e) {
            // expected exception
            e.printStackTrace();
        }
        // uuidGenerator test
        try {
            new SplitTocManifestFilter(
                titleMetadata,
                new HashMap<String, String>(),
                null,
                temporaryDirectory,
                mockFileUtilsFacade,
                mockPlaceholderDocumentService,
                docImageMap);
            fail("Should throw IllegalArugmentException");
        } catch (final IllegalArgumentException e) {
            // expected exception
            e.printStackTrace();
        }
        // activeDirectory tests
        try {
            new SplitTocManifestFilter(
                titleMetadata,
                new HashMap<String, String>(),
                uuidGenerator,
                null,
                mockFileUtilsFacade,
                mockPlaceholderDocumentService,
                docImageMap);
            fail("Should throw IllegalArugmentException");
        } catch (final IllegalArgumentException e) {
            // expected exception
            e.printStackTrace();
        }
        try {
            new SplitTocManifestFilter(
                titleMetadata,
                new HashMap<String, String>(),
                uuidGenerator,
                new File("test"), //TODO this file is not deleted
                mockFileUtilsFacade,
                mockPlaceholderDocumentService,
                docImageMap);
            fail("Should throw IllegalArugmentException");
        } catch (final IllegalArgumentException e) {
            // expected exception
            e.printStackTrace();
        }
        // file utilities facade test
        try {
            new SplitTocManifestFilter(
                titleMetadata,
                new HashMap<String, String>(),
                uuidGenerator,
                temporaryDirectory,
                null,
                mockPlaceholderDocumentService,
                docImageMap);
            fail("Should throw IllegalArugmentException");
        } catch (final IllegalArgumentException e) {
            // expected exception
            e.printStackTrace();
        }
        // placeholder document services test
        try {
            new SplitTocManifestFilter(
                titleMetadata,
                new HashMap<String, String>(),
                uuidGenerator,
                temporaryDirectory,
                mockFileUtilsFacade,
                null,
                docImageMap);
            fail("Should throw IllegalArugmentException");
        } catch (final IllegalArgumentException e) {
            // expected exception
            e.printStackTrace();
        }
    }

    /*
     * @Test public void testStartElement(){ try{
     * splitTocManifestFilterTest.startElement(URI, TITLE_ELEMENT,
     * "MissingDocument", EMPTY_ATTRIBUTES); fail("Should throw SAXException");
     * }catch(SAXException e){ //expected exception e.printStackTrace(); } }
     */
    @Test
    public void testStartDocument() throws Exception {
        splitTocManifestFilterTest.startDocument();
        splitTocManifestFilterTest.endDocument();
        Assert.assertEquals("<title/>", resultStreamToString(resultStream));
    }

    @Test
    public void testEndDocumentWithoutParsingToc() throws Exception {
        splitTocManifestFilterTest.endDocument();
        Assert.assertEquals(EXPECTED_END_MANIFEST, resultStreamToString(resultStream));
    }

    @Test
    public void testEndDocumentWithParsingToc() throws Exception {
        splitTocManifestFilterTest.parse(new InputSource(tocXml));
        final String expected = "<title><toc><titlebreak/>"
            + "<entry s=\"yarr/pirates#FrontMatterTitle/FrontMatterTitleAnchor\"><text>Title Page</text></entry>"
            + "<entry s=\"yarr/pirates#Copyright/PublishingInformationAnchor\"><text>PUBLISHING INFORMATION</text>"
            + "<entry s=\"yarr/pirates#Copyright/CopyrightAnchor\"><text>Copyright Page</text></entry>"
            + "<entry s=\"yarr/pirates#ResearchAssistance/ResearchAssistanceAnchor\"><text>Additional Information or Research Assistance</text></entry>"
            + "<entry s=\"yarr/pirates#Westlaw/WestlawAnchor\"><text>Westlaw</text></entry></entry><entry s=\"yarr/pirates#DOC_GUID/TOC_GUID\"><text>BLARGH</text></entry></toc></title>";
        Assert.assertEquals(expected, resultStreamToString(resultStream));
    }

    @Test
    public void testTitleManifestFilterHappyPath() throws Exception {
        titleMetadata.setTitleId("uscl/an/book_splittitletest");
        final InputStream immigrationProceduresHandbook =
            SplitTocManifestFilterTest.class.getResourceAsStream("SPLIT_TOC.xml");
        splitTocManifestFilterTest.parse(new InputSource(immigrationProceduresHandbook));
        final InputSource result = new InputSource(new ByteArrayInputStream(resultStream.toByteArray()));
        final InputSource expected =
            new InputSource(SplitTocManifestFilterTest.class.getResourceAsStream("SPLIT_TITLE.xml"));

        final DetailedDiff diff = new DetailedDiff(compareXML(expected, result));
        final List<Difference> differences = diff.getAllDifferences();
        Assert.assertTrue(differences.size() == 0);
    }

    @Test
    public void testFamilyGuidReplacementHappyPath() throws Exception {
        final Map<String, String> familyGuidMap = new HashMap<>();
        familyGuidMap.put("DOC_GUID1", "FAM_GUID1");
        familyGuidMap.put("DOC_GUID2", "FAM_GUID2");
        familyGuidMap.put("DOC_GUID3", "FAM_GUID3");
        final String input = "<EBook>"
            + "<EBookToc><Name>1</Name><Guid>TOC_GUID1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
            + "<EBookToc><Name>2</Name><Guid>TOC_GUID2</Guid><DocumentGuid>DOC_GUID2</DocumentGuid></EBookToc>"
            + "<EBookToc><Name>3</Name><Guid>TOC_GUID3</Guid><DocumentGuid>DOC_GUID3</DocumentGuid></EBookToc>"
            + "</EBook>";

        final String expectedToc =
            "<toc><titlebreak/><entry s=\"yarr/pirates#FrontMatterTitle/FrontMatterTitleAnchor\"><text>Title Page</text></entry><entry s=\"yarr/pirates#Copyright/PublishingInformationAnchor\"><text>PUBLISHING INFORMATION</text><entry s=\"yarr/pirates#Copyright/CopyrightAnchor\"><text>Copyright Page</text></entry><entry s=\"yarr/pirates#AdditionalFrontMatter1/AdditionalFrontMatter1Anchor\"><text>Pirates Toc Page</text></entry><entry s=\"yarr/pirates#ResearchAssistance/ResearchAssistanceAnchor\"><text>Additional Information or Research Assistance</text></entry><entry s=\"yarr/pirates#Westlaw/WestlawAnchor\"><text>Westlaw</text></entry></entry><entry s=\"yarr/pirates#FAM_GUID1/TOC_GUID1\"><text>1</text></entry><entry s=\"yarr/pirates#FAM_GUID2/TOC_GUID2\"><text>2</text></entry><entry s=\"yarr/pirates#FAM_GUID3/TOC_GUID3\"><text>3</text></entry></toc>";

        final String expected = "<title>" + expectedToc + EXPECTED_END_MANIFEST;

        final InputStream inputXml = new ByteArrayInputStream(input.getBytes());

        final FrontMatterPage fmPage = new FrontMatterPage();
        fmPage.setId((long) 1);
        fmPage.setPageTocLabel("Pirates Toc Page");
        final List<FrontMatterPage> frontMatterPages = new ArrayList<>();
        frontMatterPages.add(fmPage);
        titleMetadata.setFrontMatterPages(frontMatterPages);

        final SplitTocManifestFilter filter = new SplitTocManifestFilter(
            titleMetadata,
            familyGuidMap,
            uuidGenerator,
            temporaryDirectory,
            mockFileUtilsFacade,
            mockPlaceholderDocumentService,
            docImageMap);

        filter.setParent(xmlReader);
        filter.setContentHandler(serializer.asContentHandler());
        filter.parse(new InputSource(inputXml));
        // System.out.println(expected+"---------"+resultStreamToString(resultStream));
        final InputSource resultInputSource = new InputSource(new ByteArrayInputStream(resultStream.toByteArray()));
        final InputSource expectedInputSource = new InputSource(new ByteArrayInputStream(expected.getBytes()));

        assertXMLEqual(expectedInputSource, resultInputSource);
    }

    @Test
    public void testDuplicateDocGuidHappyPath() throws Exception {
        final Map<String, String> familyGuidMap = new HashMap<>();
        familyGuidMap.put("DOC_GUID1", "FAM_GUID1");
        familyGuidMap.put("DOC_GUID3", "FAM_GUID3");
        final String input = "<EBook>"
            + "<EBookToc><Name>1</Name><Guid>TOC_GUID1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
            + "<EBookToc><Name>2</Name><Guid>TOC_GUID2</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
            + "<EBookToc><Name>3</Name><Guid>TOC_GUID3</Guid><DocumentGuid>DOC_GUID3</DocumentGuid></EBookToc>"
            + "</EBook>";

        final String expectedToc = EXPECTED__SPLIT_TOC
            + "<entry s=\"yarr/pirates#FAM_GUID1/TOC_GUID1\"><text>1</text></entry><entry s=\"yarr/pirates#GENERATED_DOC_GUID/TOC_GUID2\"><text>2</text></entry><entry s=\"yarr/pirates#FAM_GUID3/TOC_GUID3\"><text>3</text></entry></toc>";
        final String expected = "<title>" + expectedToc + EXPECTED_END_MANIFEST;

        final InputStream inputXml = new ByteArrayInputStream(input.getBytes());

        final UuidGenerator mockUuidGenerator = EasyMock.createMock(UuidGenerator.class);
        EasyMock.expect(mockUuidGenerator.generateUuid()).andReturn("GENERATED_DOC_GUID");
        EasyMock.replay(mockUuidGenerator);

        mockFileUtilsFacade.copyFile(
            new File(temporaryDirectory.getAbsolutePath() + "\\DOC_GUID1.html"),
            new File(temporaryDirectory.getAbsolutePath() + "\\GENERATED_DOC_GUID.html"));
        EasyMock.replay(mockFileUtilsFacade);
        final SplitTocManifestFilter filter = new SplitTocManifestFilter(
            titleMetadata,
            familyGuidMap,
            mockUuidGenerator,
            temporaryDirectory,
            mockFileUtilsFacade,
            mockPlaceholderDocumentService,
            docImageMap);

        filter.setParent(xmlReader);
        filter.setContentHandler(serializer.asContentHandler());
        filter.parse(new InputSource(inputXml));

        final InputSource resultInputSource = new InputSource(new ByteArrayInputStream(resultStream.toByteArray()));
        final InputSource expectedInputSource = new InputSource(new ByteArrayInputStream(expected.getBytes()));

        assertXMLEqual(expectedInputSource, resultInputSource);
    }

    @Test
    public void testDuplicateFamilyGuidHappyPath() throws Exception {
        final Map<String, String> familyGuidMap = new HashMap<>();
        familyGuidMap.put("DOC_GUID1", "FAM_GUID1");
        familyGuidMap.put("DOC_GUID2", "FAM_GUID2");
        familyGuidMap.put("DOC_GUID3", "FAM_GUID1");
        final String input = "<EBook>"
            + "<EBookToc><Name>1</Name><Guid>TOC_GUID1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
            + "<EBookToc><Name>2</Name><Guid>TOC_GUID2</Guid><DocumentGuid>DOC_GUID2</DocumentGuid></EBookToc>"
            + "<EBookToc><Name>3</Name><Guid>TOC_GUID3</Guid><DocumentGuid>DOC_GUID3</DocumentGuid></EBookToc>"
            + "</EBook>";

        final String expectedToc = EXPECTED__SPLIT_TOC
            + "<entry s=\"yarr/pirates#FAM_GUID1/TOC_GUID1\"><text>1</text></entry><entry s=\"yarr/pirates#FAM_GUID2/TOC_GUID2\"><text>2</text></entry><entry s=\"yarr/pirates#GENERATED_FAMILY_GUID/TOC_GUID3\"><text>3</text></entry></toc>";
        final String expected = "<title>" + expectedToc + EXPECTED_END_MANIFEST;

        final InputStream inputXml = new ByteArrayInputStream(input.getBytes());

        final UuidGenerator mockUuidGenerator = EasyMock.createMock(UuidGenerator.class);
        mockFileUtilsFacade.copyFile(
            new File(temporaryDirectory.getAbsolutePath() + "\\DOC_GUID3.html"),
            new File(temporaryDirectory.getAbsolutePath() + "\\GENERATED_FAMILY_GUID.html"));
        EasyMock.replay(mockFileUtilsFacade);
        EasyMock.expect(mockUuidGenerator.generateUuid()).andReturn("GENERATED_FAMILY_GUID");
        EasyMock.replay(mockUuidGenerator);
        final SplitTocManifestFilter filter = new SplitTocManifestFilter(
            titleMetadata,
            familyGuidMap,
            mockUuidGenerator,
            temporaryDirectory,
            mockFileUtilsFacade,
            mockPlaceholderDocumentService,
            docImageMap);

        filter.setParent(xmlReader);
        filter.setContentHandler(serializer.asContentHandler());
        filter.parse(new InputSource(inputXml));

        final InputSource resultInputSource = new InputSource(new ByteArrayInputStream(resultStream.toByteArray()));
        final InputSource expectedInputSource = new InputSource(new ByteArrayInputStream(expected.getBytes()));

        assertXMLEqual(expectedInputSource, resultInputSource);
    }

    @Test
    public void testDuplicateDocGuidAndFamilyGuidHappyPath() throws Exception {
        final Map<String, String> familyGuidMap = new HashMap<>();
        familyGuidMap.put("DOC_GUID1", "FAM_GUID1");
        familyGuidMap.put("DOC_GUID2", "FAM_GUID2");
        familyGuidMap.put("DOC_GUID3", "FAM_GUID1");
        final String input = "<EBook>"
            + "<EBookToc><Name>1</Name><Guid>TOC_GUID1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
            + "<EBookToc><Name>2</Name><Guid>TOC_GUID2</Guid><DocumentGuid>DOC_GUID2</DocumentGuid></EBookToc>"
            + "<EBookToc><Name>3</Name><Guid>TOC_GUID3</Guid><DocumentGuid>DOC_GUID3</DocumentGuid></EBookToc>"
            + "<EBookToc><Name>4</Name><Guid>TOC_GUID4</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
            + "</EBook>";

        final String expectedToc = EXPECTED__SPLIT_TOC
            + "<entry s=\"yarr/pirates#FAM_GUID1/TOC_GUID1\"><text>1</text></entry><entry s=\"yarr/pirates#FAM_GUID2/TOC_GUID2\"><text>2</text></entry><entry s=\"yarr/pirates#GENERATED_FAMILY_GUID/TOC_GUID3\"><text>3</text></entry><entry s=\"yarr/pirates#GENERATED_DOC_GUID/TOC_GUID4\"><text>4</text></entry></toc>";
        final String expected = "<title>" + expectedToc + EXPECTED_END_MANIFEST;

        final InputStream inputXml = new ByteArrayInputStream(input.getBytes());

        mockFileUtilsFacade.copyFile(
            new File(temporaryDirectory.getAbsolutePath() + "\\DOC_GUID3.html"),
            new File(temporaryDirectory.getAbsolutePath() + "\\GENERATED_FAMILY_GUID.html"));
        mockFileUtilsFacade.copyFile(
            new File(temporaryDirectory.getAbsolutePath() + "\\DOC_GUID1.html"),
            new File(temporaryDirectory.getAbsolutePath() + "\\GENERATED_DOC_GUID.html"));
        EasyMock.replay(mockFileUtilsFacade);

        final UuidGenerator mockUuidGenerator = EasyMock.createMock(UuidGenerator.class);
        EasyMock.expect(mockUuidGenerator.generateUuid()).andReturn("GENERATED_FAMILY_GUID");
        EasyMock.expect(mockUuidGenerator.generateUuid()).andReturn("GENERATED_DOC_GUID");
        EasyMock.replay(mockUuidGenerator);
        final SplitTocManifestFilter filter = new SplitTocManifestFilter(
            titleMetadata,
            familyGuidMap,
            mockUuidGenerator,
            temporaryDirectory,
            mockFileUtilsFacade,
            mockPlaceholderDocumentService,
            docImageMap);

        filter.setParent(xmlReader);
        filter.setContentHandler(serializer.asContentHandler());
        filter.parse(new InputSource(inputXml));

        final InputSource resultInputSource = new InputSource(new ByteArrayInputStream(resultStream.toByteArray()));
        final InputSource expectedInputSource = new InputSource(new ByteArrayInputStream(expected.getBytes()));
        assertXMLEqual(expectedInputSource, resultInputSource);
    }

    /**
     * If this test does not pass something has gone seriously wrong with the
     * filter; possibly related to the way we cascade identifiers.
     */
    @Test
    public void testCascadeAcrossSiblingsWorksProperlyCaFedRules2012() throws Exception {
        final Map<String, String> familyGuidMap = new HashMap<>();
        familyGuidMap.put("DOC_GUID1", "FAM_GUID1");
        familyGuidMap.put("DOC_GUID2", "FAM_GUID2");
        familyGuidMap.put("DOC_GUID3", "FAM_GUID1");

        final InputStream caFedRules2012 =
            SplitTocManifestFilterTest.class.getResourceAsStream("CA_FED_RULES_2012.xml");

        final UuidGenerator mockUuidGenerator = EasyMock.createMock(UuidGenerator.class);
        EasyMock.expect(mockUuidGenerator.generateUuid()).andReturn("GENERATED_PLACEHOLDER_DOCUMENT_UUID_1");
        EasyMock.expect(mockUuidGenerator.generateUuid()).andReturn("GENERATED_PLACEHOLDER_DOCUMENT_UUID_2");
        EasyMock.expect(mockUuidGenerator.generateUuid()).andReturn("GENERATED_PLACEHOLDER_DOCUMENT_UUID_3");
        EasyMock.replay(mockUuidGenerator);
        final SplitTocManifestFilter filter = new SplitTocManifestFilter(
            titleMetadata,
            familyGuidMap,
            mockUuidGenerator,
            temporaryDirectory,
            mockFileUtilsFacade,
            mockPlaceholderDocumentService,
            docImageMap);

        filter.setParent(xmlReader);
        filter.setContentHandler(serializer.asContentHandler());

        filter.parse(new InputSource(caFedRules2012));
        final InputSource result = new InputSource(new ByteArrayInputStream(resultStream.toByteArray()));
        final InputSource expected = new InputSource(
            SplitTocManifestFilterTest.class.getResourceAsStream("CA_FED_RULES_2012_EXPECTED_SPLIT_MANIFEST.xml"));
        final DetailedDiff diff = new DetailedDiff(compareXML(expected, result));
        final List<Difference> differences = diff.getAllDifferences();
        Assert.assertTrue(differences.size() == 0);
    }

    @Test
    public void testCaFedRules2012WithoutDeletedNodes() throws Exception {
        final InputStream caFedRules2012 =
            SplitTocManifestFilterTest.class.getResourceAsStream("CA_FED_RULES_2012_WITHOUT_DELETED_NODES.xml");
        splitTocManifestFilterTest.parse(new InputSource(caFedRules2012));
        // System.out.println(resultStreamToString(resultStream));
        final InputSource result = new InputSource(new ByteArrayInputStream(resultStream.toByteArray()));
        final InputSource expected = new InputSource(
            SplitTocManifestFilterTest.class
                .getResourceAsStream("CA_FED_RULES_2012_WITHOUT_DELETED_NODES_EXPECTED.xml"));

        final DetailedDiff diff = new DetailedDiff(compareXML(expected, result));
        final List<Difference> differences = diff.getAllDifferences();
        Assert.assertTrue(differences.size() == 0);
    }

    @Test
    public void testWriteTocNodeHappyPath() throws Exception {
        final TableOfContents tableOfContents = new TableOfContents();
        tableOfContents.addChild(new TocEntry("TOC1", "DOC1", "FOO", 1));
        tableOfContents.addChild(new TocEntry("TOC2", "DOC2", "BAR", 1));
        final TocNode basil = new TocEntry("TOC3", "DOC3", "BAZ", 1);
        basil.addChild(new TocEntry("TOC3.1", "DOC3.1", "BASIL", 2));
        tableOfContents.addChild(basil);
        splitTocManifestFilterTest.setTableOfContents(tableOfContents);
        splitTocManifestFilterTest.endDocument(); // indirectly tests
                                                  // writeTableOfContents,
                                                  // which is called as part
                                                  // of invoking
                                                  // endDocument().
        final String expected =
            "<toc><titlebreak/><entry s=\"DOC1/TOC1\"><text>FOO</text></entry><entry s=\"DOC2/TOC2\"><text>BAR</text></entry><entry s=\"DOC3/TOC3\"><text>BAZ</text><entry s=\"DOC3.1/TOC3.1\"><text>BASIL</text></entry></entry></toc></title>";
        Assert.assertEquals(expected, resultStreamToString(resultStream));
    }

    private String resultStreamToString(final ByteArrayOutputStream resultStream) throws Exception {
        return IOUtils.toString(resultStream.toByteArray(), "UTF-8");
    }
}
