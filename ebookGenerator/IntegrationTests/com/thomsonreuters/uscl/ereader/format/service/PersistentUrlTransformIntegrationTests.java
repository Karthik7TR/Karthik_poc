package com.thomsonreuters.uscl.ereader.format.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;
import com.thomsonreuters.uscl.ereader.format.parsinghandler.XSLIncludeResolver;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadata;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetadataService;
import com.thomsonreuters.uscl.ereader.ioutil.FileExtensionFilter;
import com.thomsonreuters.uscl.ereader.ioutil.FileHandlingHelper;
import org.apache.commons.io.IOUtils;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * This class is a spike implementation of XSLT pathway which produces persistent URLs to WLN
 * via the MUD.
 *
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "PersistentUrlTransformIntegrationTest-context.xml")
public final class PersistentUrlTransformIntegrationTests {
    private static final String MOCK_DOCTYPE = null;
    private static InputStream MOCK_INPUT_STREAM;
    private static final String MOCK_COLLECTION = "w_codesstaminrdp";
    private static final String CODES_STATUTES_XSLT = "CodesStatutes.xsl";
    private static final String ANALYTICAL_XSLT = "AnalyticalEaganProducts.xsl";
    private static final String PRE_RENDERED_GUID = "Iff5a0c8d7c8f11da9de6e47d6d5aa7a5";
    private static final String WEST_LAW_RENDERED_XML = "Iff5a0c8d7c8f11da9de6e47d6d5aa7a5_expected_wln.html";
    private static final String IMAGEBLOCK_XPATH_EXPR =
        "//div[@id='co_document']/div/div/div/div[@class='co_imageBlock']/a[@href]";
    private static final String IMAGEBLOCK_WEST_LAW_XPATH_EXPR =
        "//div[@id='co_document']/div/div/div/div/div[@class='co_imageBlock']/a[@href]";
    private static final String DOCUMENT_HEAD_XPATH_EXPR = "//div[@id='co_document']/div/div/a[@href]";
    private static final String CONTENT_BLOCK_XPATH_EXPR =
        "//div[@id='co_document']/div/div/div/div/div/div/div/div/a[@href]";
    private static final String PRELIMGOLDENLEAF_XPATH_EXPR_3 = "//div[@id='co_prelimGoldenLeaf']/a[@href]";
    private static final String PARAGRAPH_TEXT_XPATH_EXPR =
        "//div[@class='co_paragraph']/div[@class='co_paragraphText']/a[@href]";
    private static final String titleId = "IMPH";
    private static final String fullTitleId = "uscl/an/IMPH";
    private static final String staticContentDir = "/apps/ebookbuilder/staticContent/";
    private TransformerServiceImpl transformerService;
    private DocMetadata mockDocMetadata;
    private DocMetadataService mockDocMetadataService;
    private GenerateDocumentDataBlockServiceImpl mocGenerateDocumentDataBlockService;
    private BookDefinition bookDefinition;
    private String novusXmlFilename;
    private String novusMetadataFilename;
    private long jobId;
    @Rule
    public TemporaryFolder tempDirectory = new TemporaryFolder();

    @Before
    public void setUp() throws Exception {
        mockDocMetadata = EasyMock.createMock(DocMetadata.class);
        mockDocMetadataService = EasyMock.createMock(DocMetadataService.class);
        mocGenerateDocumentDataBlockService = EasyMock.createMock(GenerateDocumentDataBlockServiceImpl.class);
        MOCK_INPUT_STREAM = new ByteArrayInputStream("Head".getBytes());

        EasyMock
            .expect(
                mockDocMetadataService
                    .findDocMetadataByPrimaryKey(titleId, Long.valueOf(12345), "Iff49dfd67c8f11da9de6e47d6d5aa7a5"))
            .andReturn(mockDocMetadata);

        EasyMock.expect(mockDocMetadata.getCollectionName()).andReturn(MOCK_COLLECTION).times(2);
        EasyMock.expect(mockDocMetadata.getDocType()).andReturn(MOCK_DOCTYPE);
        EasyMock
            .expect(
                mocGenerateDocumentDataBlockService
                    .getDocumentDataBlockAsStream(titleId, Long.valueOf(12345), "Iff49dfd67c8f11da9de6e47d6d5aa7a5"))
            .andReturn(MOCK_INPUT_STREAM);
        EasyMock.replay(mockDocMetadataService);
        EasyMock.replay(mockDocMetadata);

        transformerService = new TransformerServiceImpl();
        transformerService.setDocMetadataService(mockDocMetadataService);
        transformerService.setGenerateDocumentDataBlockService(mocGenerateDocumentDataBlockService);

        bookDefinition = new BookDefinition();
        bookDefinition.setFullyQualifiedTitleId(fullTitleId);
        novusXmlFilename = "Iff49dfd67c8f11da9de6e47d6d5aa7a5.xml";
        novusMetadataFilename = "w_an_rcc_cajur-Iff49dfd67c8f11da9de6e47d6d5aa7a5.xml";
        jobId = 12345L;
    }

    @Test
    @Ignore //ignored due to 30+ second runtime (and broken)
    public void testCiteQueryAdapterLinksUsingCodesStatutesStylesheet() throws Exception {
        final String renderedOutput = getWestLawNextRenderedOutput("Iff49dfd67c8f11da9de6e47d6d5aa7a5");

        int result = buildUrlsFromString(renderedOutput, DOCUMENT_HEAD_XPATH_EXPR);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location " + DOCUMENT_HEAD_XPATH_EXPR + "  in prerendered output  " + renderedOutput);

        result = buildUrlsFromFile(WEST_LAW_RENDERED_XML, DOCUMENT_HEAD_XPATH_EXPR);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location "
                + DOCUMENT_HEAD_XPATH_EXPR
                + "  in West_Law_rendered xml  "
                + WEST_LAW_RENDERED_XML);
    }

    @Test
    @Ignore //ignored due to 30+ second runtime (and broken)
    public void testN1E6B9EE08D7111D8A8ACD145B11214D7ContentBlock() throws Exception {
        final String renderedOutput = getWestLawNextRenderedOutput("N1E6B9EE08D7111D8A8ACD145B11214D7");

        int result = buildUrlsFromString(renderedOutput, CONTENT_BLOCK_XPATH_EXPR);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location " + CONTENT_BLOCK_XPATH_EXPR + "  in prerendered output  " + renderedOutput);

        result = buildUrlsFromFile("N1E6B9EE08D7111D8A8ACD145B11214D7_expected_wln.html", CONTENT_BLOCK_XPATH_EXPR);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location "
                + CONTENT_BLOCK_XPATH_EXPR
                + "  in West_Law_rendered xml  "
                + WEST_LAW_RENDERED_XML);
    }

    @Test
    @Ignore //ignored due to 30+ second runtime (and broken)
    public void testN1E6B9EE08D7111D8A8ACD145B11214D7ParagraphText() throws Exception {
        final String renderedOutput = getWestLawNextRenderedOutput("N1E6B9EE08D7111D8A8ACD145B11214D7");

        int result = buildUrlsFromString(renderedOutput, PARAGRAPH_TEXT_XPATH_EXPR);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location " + PARAGRAPH_TEXT_XPATH_EXPR + "  in prerendered output  " + renderedOutput);

        result = buildUrlsFromFile("N1E6B9EE08D7111D8A8ACD145B11214D7_expected_wln.html", PARAGRAPH_TEXT_XPATH_EXPR);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location "
                + PARAGRAPH_TEXT_XPATH_EXPR
                + "  in West_Law_rendered xml  "
                + WEST_LAW_RENDERED_XML);
    }

    @Test
    @Ignore //ignored due to 30+ second runtime (and broken)
    public void testN1E6B9EE08D7111D8A8ACD145B11214D7PrelimGoldenLeaf() throws Exception {
        final String renderedOutput = getWestLawNextRenderedOutput("N1E6B9EE08D7111D8A8ACD145B11214D7");

        System.out.println("Output is " + renderedOutput);
        int result = buildUrlsFromString(renderedOutput, PRELIMGOLDENLEAF_XPATH_EXPR_3);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location "
                + PRELIMGOLDENLEAF_XPATH_EXPR_3
                + "  in prerendered output  "
                + renderedOutput);

        result =
            buildUrlsFromFile("N1E6B9EE08D7111D8A8ACD145B11214D7_expected_wln.html", PRELIMGOLDENLEAF_XPATH_EXPR_3);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location "
                + PRELIMGOLDENLEAF_XPATH_EXPR_3
                + "  in West_Law_rendered xml  "
                + WEST_LAW_RENDERED_XML);
    }

    @Test
    @Ignore //ignored due to 30+ second runtime (and broken)
    public void testN2129FB908D7111D8A8ACD145B11214D71ParagraphText() throws Exception {
        final String renderedOutput = getWestLawNextRenderedOutput("N2129FB908D7111D8A8ACD145B11214D7");

        int result = buildUrlsFromString(renderedOutput, PARAGRAPH_TEXT_XPATH_EXPR);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location " + PARAGRAPH_TEXT_XPATH_EXPR + "  in prerendered output  " + renderedOutput);

        result = buildUrlsFromFile("N2129FB908D7111D8A8ACD145B11214D7_expected_wln.html", PARAGRAPH_TEXT_XPATH_EXPR);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location "
                + PARAGRAPH_TEXT_XPATH_EXPR
                + "  in West_Law_rendered xml  "
                + WEST_LAW_RENDERED_XML);
    }

    @Test
    @Ignore //ignored due to 30+ second runtime (and broken)
    public void testN2129FB908D7111D8A8ACD145B11214D7ContentBlock() throws Exception {
        final String renderedOutput = getWestLawNextRenderedOutput("N2129FB908D7111D8A8ACD145B11214D7");

        int result = buildUrlsFromString(renderedOutput, CONTENT_BLOCK_XPATH_EXPR);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location " + CONTENT_BLOCK_XPATH_EXPR + "  in prerendered output  " + renderedOutput);

        result = buildUrlsFromFile("N2129FB908D7111D8A8ACD145B11214D7_expected_wln.html", CONTENT_BLOCK_XPATH_EXPR);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location "
                + CONTENT_BLOCK_XPATH_EXPR
                + "  in West_Law_rendered xml  "
                + WEST_LAW_RENDERED_XML);
    }

    @Test
    @Ignore //ignored due to 30+ second runtime (and broken)
    public void testN2129FB908D7111D8A8ACD145B11214D7PrelimGoldenLeaf() throws Exception {
        final String renderedOutput = getWestLawNextRenderedOutput("N2129FB908D7111D8A8ACD145B11214D7");

        int result = buildUrlsFromString(renderedOutput, PRELIMGOLDENLEAF_XPATH_EXPR_3);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location "
                + PRELIMGOLDENLEAF_XPATH_EXPR_3
                + "  in prerendered output  "
                + renderedOutput);

        result =
            buildUrlsFromFile("N2129FB908D7111D8A8ACD145B11214D7_expected_wln.html", PRELIMGOLDENLEAF_XPATH_EXPR_3);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location "
                + PRELIMGOLDENLEAF_XPATH_EXPR_3
                + "  in West_Law_rendered xml  "
                + WEST_LAW_RENDERED_XML);
    }

    @Test
    @Ignore //ignored due to 30+ second runtime (and broken)
    public void testN5AE4A3C0D2A311DFA872E294CFCC8A91ContentBlock() throws Exception {
        final String renderedOutput = getWestLawNextRenderedOutput("N5AE4A3C0D2A311DFA872E294CFCC8A91");

        int result = buildUrlsFromString(renderedOutput, CONTENT_BLOCK_XPATH_EXPR);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location " + CONTENT_BLOCK_XPATH_EXPR + "  in prerendered output  " + renderedOutput);

        result = buildUrlsFromFile("N5AE4A3C0D2A311DFA872E294CFCC8A91_expected_wln.html", CONTENT_BLOCK_XPATH_EXPR);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location "
                + CONTENT_BLOCK_XPATH_EXPR
                + "  in West_Law_rendered xml  "
                + WEST_LAW_RENDERED_XML);
    }

    @Test
    @Ignore //ignored due to 30+ second runtime (and broken)
    public void testN5AE4A3C0D2A311DFA872E294CFCC8A91ParagraphText() throws Exception {
        final String renderedOutput = getWestLawNextRenderedOutput("N5AE4A3C0D2A311DFA872E294CFCC8A91");

        int result = buildUrlsFromString(renderedOutput, PARAGRAPH_TEXT_XPATH_EXPR);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location " + PARAGRAPH_TEXT_XPATH_EXPR + "  in prerendered output  " + renderedOutput);

        result = buildUrlsFromFile("N5AE4A3C0D2A311DFA872E294CFCC8A91_expected_wln.html", PARAGRAPH_TEXT_XPATH_EXPR);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location "
                + PARAGRAPH_TEXT_XPATH_EXPR
                + "  in West_Law_rendered xml  "
                + WEST_LAW_RENDERED_XML);
    }

    @Test
    @Ignore //ignored due to 30+ second runtime (and broken)
    public void testN5AE4A3C0D2A311DFA872E294CFCC8A91PrelimGoldenLeaf() throws Exception {
        final String renderedOutput = getWestLawNextRenderedOutput("N5AE4A3C0D2A311DFA872E294CFCC8A91");

        int result = buildUrlsFromString(renderedOutput, PRELIMGOLDENLEAF_XPATH_EXPR_3);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location "
                + PRELIMGOLDENLEAF_XPATH_EXPR_3
                + "  in prerendered output  "
                + renderedOutput);

        result =
            buildUrlsFromFile("N5AE4A3C0D2A311DFA872E294CFCC8A91_expected_wln.html", PRELIMGOLDENLEAF_XPATH_EXPR_3);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location "
                + PRELIMGOLDENLEAF_XPATH_EXPR_3
                + "  in West_Law_rendered xml  "
                + WEST_LAW_RENDERED_XML);
    }

    @Test
    @Ignore //ignored due to 30+ second runtime (and broken)
    public void testN98740E607CC011DC8B69829BAAB1B5B5ParagraphText() throws Exception {
        final String renderedOutput = getWestLawNextRenderedOutput("N2129FB908D7111D8A8ACD145B11214D7");

        int result = buildUrlsFromString(renderedOutput, PARAGRAPH_TEXT_XPATH_EXPR);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location " + PARAGRAPH_TEXT_XPATH_EXPR + "  in prerendered output  " + renderedOutput);

        result = buildUrlsFromFile("N98740E607CC011DC8B69829BAAB1B5B5_expected_wln.html", PARAGRAPH_TEXT_XPATH_EXPR);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location "
                + PARAGRAPH_TEXT_XPATH_EXPR
                + "  in West_Law_rendered xml  "
                + WEST_LAW_RENDERED_XML);
    }

    @Test
    @Ignore //ignored due to 30+ second runtime (and broken)
    public void testNB915D0707CBD11DC8EE4814D1B0549C2ContentBlock() throws Exception {
        final String renderedOutput = getWestLawNextRenderedOutput("NB915D0707CBD11DC8EE4814D1B0549C2");

        int result = buildUrlsFromString(renderedOutput, CONTENT_BLOCK_XPATH_EXPR);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location " + CONTENT_BLOCK_XPATH_EXPR + "  in prerendered output  " + renderedOutput);

        result = buildUrlsFromFile("NB915D0707CBD11DC8EE4814D1B0549C2_expected_wln.html", CONTENT_BLOCK_XPATH_EXPR);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location "
                + CONTENT_BLOCK_XPATH_EXPR
                + "  in West_Law_rendered xml  "
                + WEST_LAW_RENDERED_XML);
    }

    @Test
    @Ignore //ignored due to 30+ second runtime (and broken)
    public void testNB915D0707CBD11DC8EE4814D1B0549C2ParagraphText() throws Exception {
        final String renderedOutput = getWestLawNextRenderedOutput("NB915D0707CBD11DC8EE4814D1B0549C2");

        int result = buildUrlsFromString(renderedOutput, PARAGRAPH_TEXT_XPATH_EXPR);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location " + PARAGRAPH_TEXT_XPATH_EXPR + "  in prerendered output  " + renderedOutput);

        result = buildUrlsFromFile("NB915D0707CBD11DC8EE4814D1B0549C2_expected_wln.html", PARAGRAPH_TEXT_XPATH_EXPR);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location "
                + PARAGRAPH_TEXT_XPATH_EXPR
                + "  in West_Law_rendered xml  "
                + WEST_LAW_RENDERED_XML);
    }

    @Test
    @Ignore //ignored due to 30+ second runtime (and broken)
    public void testNB915D0707CBD11DC8EE4814D1B0549C2PrelimGoldenLeaf() throws Exception {
        final String renderedOutput = getWestLawNextRenderedOutput("NB915D0707CBD11DC8EE4814D1B0549C2");

        int result = buildUrlsFromString(renderedOutput, PRELIMGOLDENLEAF_XPATH_EXPR_3);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location "
                + PRELIMGOLDENLEAF_XPATH_EXPR_3
                + "  in prerendered output  "
                + renderedOutput);

        result =
            buildUrlsFromFile("NB915D0707CBD11DC8EE4814D1B0549C2_expected_wln.html", PRELIMGOLDENLEAF_XPATH_EXPR_3);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location "
                + PRELIMGOLDENLEAF_XPATH_EXPR_3
                + "  in West_Law_rendered xml  "
                + WEST_LAW_RENDERED_XML);
    }

    @Test
    @Ignore
    public void testUrlBuilderAdapterForKeyciteFlagUrl() throws Exception {
        final String inputXmlFragment = "<keyCiteFlagLink.Url docGuid=\"DOC_GUID\"/>";
        final StreamSource inputSource = new StreamSource(new ByteArrayInputStream(inputXmlFragment.getBytes("UTF-8")));

        final Source xsltSource = new StreamSource(
            "/apps/ebookbuilder/staticContent/WestlawNext/DefaultProductView/ContentTypes/" + CODES_STATUTES_XSLT);

        final TransformerFactory transFact = TransformerFactory.newInstance();
        final XSLIncludeResolver resolver = new XSLIncludeResolver();
        transFact.setURIResolver(resolver);

        final Transformer trans = transFact.newTransformer(xsltSource);
        trans.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

        final Result result = new StreamResult(System.out);

        trans.transform(inputSource, result);
    }

    @Test
    @Ignore //ignored due to 30+ second runtime (and broken)
    public void testUrlBuilderAdapterLinksUsingPdfImageHref() throws Exception {
        final String renderedOutput = getWestLawNextRenderedOutput(PRE_RENDERED_GUID);
        System.out.println("Chinana" + renderedOutput);

        int result = buildUrlsFromString(renderedOutput, DOCUMENT_HEAD_XPATH_EXPR);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location " + DOCUMENT_HEAD_XPATH_EXPR + "  in prerendered output  " + renderedOutput);

        result = buildUrlsFromFile(WEST_LAW_RENDERED_XML, DOCUMENT_HEAD_XPATH_EXPR);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location "
                + DOCUMENT_HEAD_XPATH_EXPR
                + "  in West_Law_rendered xml  "
                + WEST_LAW_RENDERED_XML);
    }

    @Test
    @Ignore //ignored due to 30+ second runtime (and broken)
    public void testUrlBuilderAdapterLinksUsingPdfImageMetadata() throws Exception {
        final String renderedOutput = getWestLawNextRenderedOutput(PRE_RENDERED_GUID);

        int result = buildUrlsFromString(renderedOutput, IMAGEBLOCK_XPATH_EXPR);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location " + IMAGEBLOCK_XPATH_EXPR + "  in prerendered output  " + renderedOutput);

        result = buildUrlsFromFile(WEST_LAW_RENDERED_XML, IMAGEBLOCK_WEST_LAW_XPATH_EXPR);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location "
                + IMAGEBLOCK_WEST_LAW_XPATH_EXPR
                + "  in West_Law_rendered xml  "
                + WEST_LAW_RENDERED_XML);
    }

    private int buildUrlsFromFile(final String xmlFile, final String xpathString) {
        final File expectedWlnOutput =
            new File(PersistentUrlTransformIntegrationTests.class.getResource(xmlFile).getFile());

        final DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware(true);

        final DocumentBuilder builder;
        NodeList nodes = null;

        try {
            builder = domFactory.newDocumentBuilder();

            final Document doc = builder.parse(expectedWlnOutput);

            final XPathFactory factory = XPathFactory.newInstance();
            final XPath xpath = factory.newXPath();
            final XPathExpression expr = xpath.compile(xpathString);
            final Object result = expr.evaluate(doc, XPathConstants.NODESET);
            nodes = (NodeList) result;
        } catch (final Exception e) {
            e.printStackTrace();
        }

        return nodes.getLength();
    }

    private int buildUrlsFromString(final String str, final String xpathExpression) {
        final DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware(true);

        final DocumentBuilder builder;
        NodeList nodes = null;

        try {
            builder = domFactory.newDocumentBuilder();

            final Document doc = builder.parse(new ByteArrayInputStream(str.getBytes("UTF-8")));

            final XPathFactory factory = XPathFactory.newInstance();
            final XPath xpath = factory.newXPath();
            final XPathExpression expr = xpath.compile(xpathExpression);
            final Object result = expr.evaluate(doc, XPathConstants.NODESET);
            nodes = (NodeList) result;
        } catch (final Exception e) {
            e.printStackTrace();
        }

        return nodes.getLength();
    }

    private String getWestLawNextRenderedOutput(final String preRenderedInput)
        throws EBookFormatException, FileNotFoundException, IOException {
        novusXmlFilename = preRenderedInput + ".xml";

        mockDocMetadataService = EasyMock.createMock(DocMetadataService.class);

        EasyMock
            .expect(mockDocMetadataService.findDocMetadataByPrimaryKey(titleId, Long.valueOf(12345), preRenderedInput))
            .andReturn(mockDocMetadata);

        EasyMock.replay(mockDocMetadataService);
        MOCK_INPUT_STREAM = new ByteArrayInputStream("Head".getBytes());

        EasyMock
            .expect(
                mocGenerateDocumentDataBlockService
                    .getDocumentDataBlockAsStream(titleId, Long.valueOf(12345), preRenderedInput))
            .andReturn(MOCK_INPUT_STREAM);
        transformerService.setDocMetadataService(mockDocMetadataService);
        transformerService.setGenerateDocumentDataBlockService(mocGenerateDocumentDataBlockService);
        EasyMock.replay(mocGenerateDocumentDataBlockService);

        final File novusXml =
            new File(PersistentUrlTransformIntegrationTests.class.getResource(novusXmlFilename).getFile());
        final File transformedDirectory = tempDirectory.newFolder("transformed");

        transformerService.setfileHandlingHelper(getFileHandlingHelper(novusXmlFilename.toLowerCase()));
        transformerService.transformXMLDocuments(
            novusXml.getParentFile(),
            novusXml.getParentFile(),
            novusXml.getParentFile(),
            transformedDirectory,
            jobId,
            bookDefinition,
            new File(staticContentDir));

        verifyAll();

        final String renderedOutput =
            IOUtils.toString(new FileInputStream(new File(transformedDirectory, preRenderedInput + ".TRANSFORMED")));

        return renderedOutput;
    }

    private FileHandlingHelper getFileHandlingHelper(final String novusXmlFilename) {
        final FileHandlingHelper fileHandlingHelper = new FileHandlingHelper();
        final FileExtensionFilter fileExtensionFilter = new FileExtensionFilter();
        fileExtensionFilter.setAcceptedFileExtensions(new String[] {novusXmlFilename});
        fileHandlingHelper.setFilter(fileExtensionFilter);
        return fileHandlingHelper;
    }

    private void verifyAll() {
        EasyMock.verify(mockDocMetadataService);
        EasyMock.verify(mockDocMetadata);
    }
}
