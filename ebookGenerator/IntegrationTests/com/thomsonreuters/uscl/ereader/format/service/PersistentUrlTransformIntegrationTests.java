/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.format.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

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

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
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

import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;
import com.thomsonreuters.uscl.ereader.format.parsinghandler.XSLIncludeResolver;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadata;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetadataService;


/**
 * This class is a spike implementation of XSLT pathway which produces persistent URLs to WLN
 * via the MUD.
 *
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "PersistentUrlTransformIntegrationTest-context.xml")
public class PersistentUrlTransformIntegrationTests
{
    private static final String MOCK_DOCTYPE = "analytical";
    private static InputStream MOCK_INPUT_STREAM ; 
    private static final String MOCK_COLLECTION = "w_foo_collection";
    private static final String CODES_STATUTES_XSLT = "CodesStatutes.xsl";
    private static final String ANALYTICAL_XSLT = "AnalyticalEaganProducts.xsl";
    private static final String PRE_RENDERED_GUID = "Iff5a0c8d7c8f11da9de6e47d6d5aa7a5";
    private static final String WEST_LAW_RENDERED_XML =
        "Iff5a0c8d7c8f11da9de6e47d6d5aa7a5_expected_wln.html";
    private static final String IMAGEBLOCK_XPATH_EXPR =
        "//div[@id='co_document']/div/div/div/div[@class='co_imageBlock']/a[@href]";
    private static final String IMAGEBLOCK_WEST_LAW_XPATH_EXPR =
        "//div[@id='co_document']/div/div/div/div/div[@class='co_imageBlock']/a[@href]";
    private static final String DOCUMENT_HEAD_XPATH_EXPR =
        "//div[@id='co_document']/div/div/a[@href]";
    private static final String CONTENT_BLOCK_XPATH_EXPR =
        "//div[@id='co_document']/div/div/div/div/div/div/div/div/a[@href]";
    private static final String PRELIMGOLDENLEAF_XPATH_EXPR_3 =
        "//div[@id='co_prelimGoldenLeaf']/a[@href]";
    private static final String PARAGRAPH_TEXT_XPATH_EXPR =
        "//div[@class='co_paragraph']/div[@class='co_paragraphText']/a[@href]";
    private static final String PRINT_HEADING_XPATH_EXPR =
        "//div[@class='co_notesOfDecisions']/div[@class='co_printHeading']/div[@class='co_notesOfDecisionsHeading']/a[@href]";
    TransformerServiceImpl transformerService;
    XSLTMapperService mockXsltMapperService;
    DocMetadata mockDocMetadata;
    DocMetadataService mockDocMetadataService;
    GenerateDocumentDataBlockServiceImpl mocGenerateDocumentDataBlockService;
    String titleId;
    String novusXmlFilename;
    String novusMetadataFilename;
    long jobId;
    @Rule
    public TemporaryFolder tempDirectory = new TemporaryFolder();

    @Before
    public void setUp() throws Exception
    {
        mockXsltMapperService = EasyMock.createMock(XSLTMapperService.class);
        mockDocMetadata = EasyMock.createMock(DocMetadata.class);
        mockDocMetadataService = EasyMock.createMock(DocMetadataService.class);
        mocGenerateDocumentDataBlockService = EasyMock.createMock(GenerateDocumentDataBlockServiceImpl.class);
       // mocGenerateDocumentDataBlockService.setDocMetadataService(mockDocMetadataService);
        MOCK_INPUT_STREAM = new ByteArrayInputStream("Head".getBytes());

        EasyMock.expect(
            mockDocMetadataService.findDocMetadataByPrimaryKey(
                "uscl/an/IMPH", new Long(12345), "Iff49dfd67c8f11da9de6e47d6d5aa7a5"))
                .andReturn(mockDocMetadata);

        EasyMock.expect(mockDocMetadata.getCollectionName()).andReturn(MOCK_COLLECTION).times(2);
        EasyMock.expect(mockDocMetadata.getDocType()).andReturn(MOCK_DOCTYPE);
        EasyMock.expect(mockXsltMapperService.getXSLT(MOCK_COLLECTION, MOCK_DOCTYPE))
                .andReturn(CODES_STATUTES_XSLT);
        EasyMock.expect(mocGenerateDocumentDataBlockService.getDocumentDataBlockAsStream("uscl/an/IMPH", new Long(12345), "Iff49dfd67c8f11da9de6e47d6d5aa7a5"))
        .andReturn(MOCK_INPUT_STREAM);
        EasyMock.replay(mockDocMetadataService);
        EasyMock.replay(mockDocMetadata);
        EasyMock.replay(mockXsltMapperService);

        transformerService = new TransformerServiceImpl();
        transformerService.setxsltMapperService(mockXsltMapperService);
        transformerService.setdocMetadataService(mockDocMetadataService);
        transformerService.setGenerateDocumentDataBlockService(mocGenerateDocumentDataBlockService);

        titleId = "uscl/an/IMPH";
        novusXmlFilename = "Iff49dfd67c8f11da9de6e47d6d5aa7a5.xml";
        novusMetadataFilename = "w_an_rcc_cajur-Iff49dfd67c8f11da9de6e47d6d5aa7a5.xml";
        jobId = 12345L;
    }

    @Test
    public void testCiteQueryAdapterLinksUsingCodesStatutesStylesheet()
        throws Exception
    {
        String renderedOutput = getWestLawNextRenderedOutput("Iff49dfd67c8f11da9de6e47d6d5aa7a5");

        int result = buildUrlsFromString(renderedOutput, DOCUMENT_HEAD_XPATH_EXPR);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location " + DOCUMENT_HEAD_XPATH_EXPR + "  in prerendered output  "
            + renderedOutput);

        result = buildUrlsFromFile(WEST_LAW_RENDERED_XML, DOCUMENT_HEAD_XPATH_EXPR);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location " + DOCUMENT_HEAD_XPATH_EXPR
            + "  in West_Law_rendered xml  " + WEST_LAW_RENDERED_XML);
    }

    @Test
    @Ignore
    public void testN13AAADB07CC011DC828EC052D3B59944ContentBlock()
        throws Exception
    {
        String renderedOutput = getWestLawNextRenderedOutput("N13AAADB07CC011DC828EC052D3B59944");

        int result = buildUrlsFromString(renderedOutput, CONTENT_BLOCK_XPATH_EXPR);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location " + CONTENT_BLOCK_XPATH_EXPR + "  in prerendered output  "
            + renderedOutput);

        result = buildUrlsFromFile(
                "N13AAADB07CC011DC828EC052D3B59944_expected_wln.html", CONTENT_BLOCK_XPATH_EXPR);

        System.out.println(result);
        Assert.isTrue(
            result != 0,
            "Unable to find Url location " + CONTENT_BLOCK_XPATH_EXPR
            + "  in West_Law_rendered xml  " + WEST_LAW_RENDERED_XML);
    }

    @Test
    @Ignore
    public void testN13AAADB07CC011DC828EC052D3B59944ParagraphText()
        throws Exception
    {
        String renderedOutput = getWestLawNextRenderedOutput("N13AAADB07CC011DC828EC052D3B59944");

        int result = buildUrlsFromString(renderedOutput, PARAGRAPH_TEXT_XPATH_EXPR);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location " + PARAGRAPH_TEXT_XPATH_EXPR
            + "  in prerendered output  " + renderedOutput);

        result = buildUrlsFromFile(
                "N13AAADB07CC011DC828EC052D3B59944_expected_wln.html", PARAGRAPH_TEXT_XPATH_EXPR);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location " + PARAGRAPH_TEXT_XPATH_EXPR
            + "  in West_Law_rendered xml  " + WEST_LAW_RENDERED_XML);
    }

    @Test
    @Ignore
    public void testN13AAADB07CC011DC828EC052D3B59944PrelimGoldenLeaf()
        throws Exception
    {
        String renderedOutput = getWestLawNextRenderedOutput("N13AAADB07CC011DC828EC052D3B59944");

        int result = buildUrlsFromString(renderedOutput, PRELIMGOLDENLEAF_XPATH_EXPR_3);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location " + PRELIMGOLDENLEAF_XPATH_EXPR_3
            + "  in prerendered output  " + renderedOutput);

        result = buildUrlsFromFile(
                "N13AAADB07CC011DC828EC052D3B59944_expected_wln.html", PRELIMGOLDENLEAF_XPATH_EXPR_3);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location " + PRELIMGOLDENLEAF_XPATH_EXPR_3
            + "  in West_Law_rendered xml  " + WEST_LAW_RENDERED_XML);
    }

    @Test
    public void testN1E6B9EE08D7111D8A8ACD145B11214D7ContentBlock()
        throws Exception
    {
        String renderedOutput = getWestLawNextRenderedOutput("N1E6B9EE08D7111D8A8ACD145B11214D7");

        int result = buildUrlsFromString(renderedOutput, CONTENT_BLOCK_XPATH_EXPR);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location " + CONTENT_BLOCK_XPATH_EXPR + "  in prerendered output  "
            + renderedOutput);

        result = buildUrlsFromFile(
                "N1E6B9EE08D7111D8A8ACD145B11214D7_expected_wln.html", CONTENT_BLOCK_XPATH_EXPR);

         Assert.isTrue(
            result != 0,
            "Unable to find Url location " + CONTENT_BLOCK_XPATH_EXPR
            + "  in West_Law_rendered xml  " + WEST_LAW_RENDERED_XML);
    }

    @Test
    public void testN1E6B9EE08D7111D8A8ACD145B11214D7ParagraphText()
        throws Exception
    {
        String renderedOutput = getWestLawNextRenderedOutput("N1E6B9EE08D7111D8A8ACD145B11214D7");

        int result = buildUrlsFromString(renderedOutput, PARAGRAPH_TEXT_XPATH_EXPR);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location " + PARAGRAPH_TEXT_XPATH_EXPR
            + "  in prerendered output  " + renderedOutput);

        result = buildUrlsFromFile(
                "N1E6B9EE08D7111D8A8ACD145B11214D7_expected_wln.html", PARAGRAPH_TEXT_XPATH_EXPR);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location " + PARAGRAPH_TEXT_XPATH_EXPR
            + "  in West_Law_rendered xml  " + WEST_LAW_RENDERED_XML);
    }

    @Test
    public void testN1E6B9EE08D7111D8A8ACD145B11214D7PrelimGoldenLeaf()
        throws Exception
    {
        String renderedOutput = getWestLawNextRenderedOutput("N1E6B9EE08D7111D8A8ACD145B11214D7");

        System.out.println("Output is " + renderedOutput);
        int result = buildUrlsFromString(renderedOutput, PRELIMGOLDENLEAF_XPATH_EXPR_3);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location " + PRELIMGOLDENLEAF_XPATH_EXPR_3
            + "  in prerendered output  " + renderedOutput);

        result = buildUrlsFromFile(
                "N1E6B9EE08D7111D8A8ACD145B11214D7_expected_wln.html", PRELIMGOLDENLEAF_XPATH_EXPR_3);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location " + PRELIMGOLDENLEAF_XPATH_EXPR_3
            + "  in West_Law_rendered xml  " + WEST_LAW_RENDERED_XML);
    }

    @Test
    public void testN2129FB908D7111D8A8ACD145B11214D71ParagraphText()
        throws Exception
    {
        String renderedOutput = getWestLawNextRenderedOutput("N2129FB908D7111D8A8ACD145B11214D7");

        int result = buildUrlsFromString(renderedOutput, PARAGRAPH_TEXT_XPATH_EXPR);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location " + PARAGRAPH_TEXT_XPATH_EXPR
            + "  in prerendered output  " + renderedOutput);

        result = buildUrlsFromFile(
                "N2129FB908D7111D8A8ACD145B11214D7_expected_wln.html", PARAGRAPH_TEXT_XPATH_EXPR);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location " + PARAGRAPH_TEXT_XPATH_EXPR
            + "  in West_Law_rendered xml  " + WEST_LAW_RENDERED_XML);
    }

    @Test
    public void testN2129FB908D7111D8A8ACD145B11214D7ContentBlock()
        throws Exception
    {
        String renderedOutput = getWestLawNextRenderedOutput("N2129FB908D7111D8A8ACD145B11214D7");

        int result = buildUrlsFromString(renderedOutput, CONTENT_BLOCK_XPATH_EXPR);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location " + CONTENT_BLOCK_XPATH_EXPR + "  in prerendered output  "
            + renderedOutput);

        result = buildUrlsFromFile(
                "N2129FB908D7111D8A8ACD145B11214D7_expected_wln.html", CONTENT_BLOCK_XPATH_EXPR);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location " + CONTENT_BLOCK_XPATH_EXPR
            + "  in West_Law_rendered xml  " + WEST_LAW_RENDERED_XML);
    }

    @Test
    public void testN2129FB908D7111D8A8ACD145B11214D7PrelimGoldenLeaf()
        throws Exception
    {
        String renderedOutput = getWestLawNextRenderedOutput("N2129FB908D7111D8A8ACD145B11214D7");

        int result = buildUrlsFromString(renderedOutput, PRELIMGOLDENLEAF_XPATH_EXPR_3);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location " + PRELIMGOLDENLEAF_XPATH_EXPR_3
            + "  in prerendered output  " + renderedOutput);

        result = buildUrlsFromFile(
                "N2129FB908D7111D8A8ACD145B11214D7_expected_wln.html", PRELIMGOLDENLEAF_XPATH_EXPR_3);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location " + PRELIMGOLDENLEAF_XPATH_EXPR_3
            + "  in West_Law_rendered xml  " + WEST_LAW_RENDERED_XML);
    }

    @Test
    @Ignore
    public void testN2129FB908D7111D8A8ACD145B11214D7PrintHeading()
        throws Exception
    {
        String renderedOutput = getWestLawNextRenderedOutput("N2129FB908D7111D8A8ACD145B11214D7");

        int result = buildUrlsFromString(renderedOutput, PRINT_HEADING_XPATH_EXPR);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location " + PRINT_HEADING_XPATH_EXPR + "  in prerendered output  "
            + renderedOutput);

        result = buildUrlsFromFile(
                "N2129FB908D7111D8A8ACD145B11214D7_expected_wln.html", PRINT_HEADING_XPATH_EXPR);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location " + PRINT_HEADING_XPATH_EXPR
            + "  in West_Law_rendered xml  " + WEST_LAW_RENDERED_XML);
    }

    @Test
    @Ignore
    public void testN588DDF107CC011DC83339EA319FEB44FContentBlock()
        throws Exception
    {
        String renderedOutput = getWestLawNextRenderedOutput("N588DDF107CC011DC83339EA319FEB44F");

        int result = buildUrlsFromString(renderedOutput, CONTENT_BLOCK_XPATH_EXPR);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location " + CONTENT_BLOCK_XPATH_EXPR + "  in prerendered output  "
            + renderedOutput);

        result = buildUrlsFromFile(
                "N588DDF107CC011DC83339EA319FEB44F_expected_wln.html", CONTENT_BLOCK_XPATH_EXPR);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location " + CONTENT_BLOCK_XPATH_EXPR
            + "  in West_Law_rendered xml  " + WEST_LAW_RENDERED_XML);
    }

    @Test
    @Ignore
    public void testN588DDF107CC011DC83339EA319FEB44FParagraphText()
        throws Exception
    {
        String renderedOutput = getWestLawNextRenderedOutput("N588DDF107CC011DC83339EA319FEB44F");

        int result = buildUrlsFromString(renderedOutput, PARAGRAPH_TEXT_XPATH_EXPR);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location " + PARAGRAPH_TEXT_XPATH_EXPR
            + "  in prerendered output  " + renderedOutput);

        result = buildUrlsFromFile(
                "N588DDF107CC011DC83339EA319FEB44F_expected_wln.html", PARAGRAPH_TEXT_XPATH_EXPR);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location " + PARAGRAPH_TEXT_XPATH_EXPR
            + "  in West_Law_rendered xml  " + WEST_LAW_RENDERED_XML);
    }

    @Test
    @Ignore
    public void testN588DDF107CC011DC83339EA319FEB44FPrelimGoldenLeaf()
        throws Exception
    {
        String renderedOutput = getWestLawNextRenderedOutput("N588DDF107CC011DC83339EA319FEB44F");

        int result = buildUrlsFromString(renderedOutput, PRELIMGOLDENLEAF_XPATH_EXPR_3);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location " + PRELIMGOLDENLEAF_XPATH_EXPR_3
            + "  in prerendered output  " + renderedOutput);

        result = buildUrlsFromFile(
                "N588DDF107CC011DC83339EA319FEB44F_expected_wln.html", PRELIMGOLDENLEAF_XPATH_EXPR_3);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location " + PRELIMGOLDENLEAF_XPATH_EXPR_3
            + "  in West_Law_rendered xml  " + WEST_LAW_RENDERED_XML);
    }

    @Test
    public void testN5AE4A3C0D2A311DFA872E294CFCC8A91ContentBlock()
        throws Exception
    {
        String renderedOutput = getWestLawNextRenderedOutput("N5AE4A3C0D2A311DFA872E294CFCC8A91");

        int result = buildUrlsFromString(renderedOutput, CONTENT_BLOCK_XPATH_EXPR);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location " + CONTENT_BLOCK_XPATH_EXPR + "  in prerendered output  "
            + renderedOutput);

        result = buildUrlsFromFile(
                "N5AE4A3C0D2A311DFA872E294CFCC8A91_expected_wln.html", CONTENT_BLOCK_XPATH_EXPR);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location " + CONTENT_BLOCK_XPATH_EXPR
            + "  in West_Law_rendered xml  " + WEST_LAW_RENDERED_XML);
    }

    @Test
    public void testN5AE4A3C0D2A311DFA872E294CFCC8A91ParagraphText()
        throws Exception
    {
        String renderedOutput = getWestLawNextRenderedOutput("N5AE4A3C0D2A311DFA872E294CFCC8A91");

        int result = buildUrlsFromString(renderedOutput, PARAGRAPH_TEXT_XPATH_EXPR);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location " + PARAGRAPH_TEXT_XPATH_EXPR
            + "  in prerendered output  " + renderedOutput);

        result = buildUrlsFromFile(
                "N5AE4A3C0D2A311DFA872E294CFCC8A91_expected_wln.html", PARAGRAPH_TEXT_XPATH_EXPR);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location " + PARAGRAPH_TEXT_XPATH_EXPR
            + "  in West_Law_rendered xml  " + WEST_LAW_RENDERED_XML);
    }

    @Test
    public void testN5AE4A3C0D2A311DFA872E294CFCC8A91PrelimGoldenLeaf()
        throws Exception
    {
        String renderedOutput = getWestLawNextRenderedOutput("N5AE4A3C0D2A311DFA872E294CFCC8A91");

        int result = buildUrlsFromString(renderedOutput, PRELIMGOLDENLEAF_XPATH_EXPR_3);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location " + PRELIMGOLDENLEAF_XPATH_EXPR_3
            + "  in prerendered output  " + renderedOutput);

        result = buildUrlsFromFile(
                "N5AE4A3C0D2A311DFA872E294CFCC8A91_expected_wln.html", PRELIMGOLDENLEAF_XPATH_EXPR_3);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location " + PRELIMGOLDENLEAF_XPATH_EXPR_3
            + "  in West_Law_rendered xml  " + WEST_LAW_RENDERED_XML);
    }

    @Test
    @Ignore
    public void testN5AE4A3C0D2A311DFA872E294CFCC8A91PrintHeading()
        throws Exception
    {
        String renderedOutput = getWestLawNextRenderedOutput("N5AE4A3C0D2A311DFA872E294CFCC8A91");

        int result = buildUrlsFromString(renderedOutput, PRINT_HEADING_XPATH_EXPR);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location " + PRINT_HEADING_XPATH_EXPR + "  in prerendered output  "
            + renderedOutput);

        result = buildUrlsFromFile(
                "N5AE4A3C0D2A311DFA872E294CFCC8A91_expected_wln.html", PRINT_HEADING_XPATH_EXPR);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location " + PRINT_HEADING_XPATH_EXPR
            + "  in West_Law_rendered xml  " + WEST_LAW_RENDERED_XML);
    }

    @Test
    @Ignore
    public void testN7BE432707CC011DC8B69829BAAB1B5B5ContentBlock()
        throws Exception
    {
        String renderedOutput = getWestLawNextRenderedOutput("N7BE432707CC011DC8B69829BAAB1B5B5");

        int result = buildUrlsFromString(renderedOutput, CONTENT_BLOCK_XPATH_EXPR);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location " + CONTENT_BLOCK_XPATH_EXPR + "  in prerendered output  "
            + renderedOutput);

        result = buildUrlsFromFile(
                "N7BE432707CC011DC8B69829BAAB1B5B5_expected_wln.html", CONTENT_BLOCK_XPATH_EXPR);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location " + CONTENT_BLOCK_XPATH_EXPR
            + "  in West_Law_rendered xml  " + WEST_LAW_RENDERED_XML);
    }

    @Test
    @Ignore
    public void testN7BE432707CC011DC8B69829BAAB1B5B5ParagraphText()
        throws Exception
    {
        String renderedOutput = getWestLawNextRenderedOutput("N7BE432707CC011DC8B69829BAAB1B5B5");

        int result = buildUrlsFromString(renderedOutput, PARAGRAPH_TEXT_XPATH_EXPR);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location " + PARAGRAPH_TEXT_XPATH_EXPR
            + "  in prerendered output  " + renderedOutput);

        result = buildUrlsFromFile(
                "N7BE432707CC011DC8B69829BAAB1B5B5_expected_wln.html", PARAGRAPH_TEXT_XPATH_EXPR);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location " + PARAGRAPH_TEXT_XPATH_EXPR
            + "  in West_Law_rendered xml  " + WEST_LAW_RENDERED_XML);
    }

    @Test
    @Ignore
    public void testN7BE432707CC011DC8B69829BAAB1B5B5PrelimGoldenLeaf()
        throws Exception
    {
        String renderedOutput = getWestLawNextRenderedOutput("N7BE432707CC011DC8B69829BAAB1B5B5");

        int result = buildUrlsFromString(renderedOutput, PRELIMGOLDENLEAF_XPATH_EXPR_3);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location " + PRELIMGOLDENLEAF_XPATH_EXPR_3
            + "  in prerendered output  " + renderedOutput);

        result = buildUrlsFromFile(
                "N7BE432707CC011DC8B69829BAAB1B5B5_expected_wln.html", PRELIMGOLDENLEAF_XPATH_EXPR_3);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location " + PRELIMGOLDENLEAF_XPATH_EXPR_3
            + "  in West_Law_rendered xml  " + WEST_LAW_RENDERED_XML);
    }

    @Test
    @Ignore
    public void testN98740E607CC011DC8B69829BAAB1B5B57ContentBlock()
        throws Exception
    {
        String renderedOutput = getWestLawNextRenderedOutput("N98740E607CC011DC8B69829BAAB1B5B5");

        int result = buildUrlsFromString(renderedOutput, CONTENT_BLOCK_XPATH_EXPR);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location " + CONTENT_BLOCK_XPATH_EXPR + "  in prerendered output  "
            + renderedOutput);

        result = buildUrlsFromFile(
                "N98740E607CC011DC8B69829BAAB1B5B5_expected_wln.html", CONTENT_BLOCK_XPATH_EXPR);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location " + CONTENT_BLOCK_XPATH_EXPR
            + "  in West_Law_rendered xml  " + WEST_LAW_RENDERED_XML);
    }

    @Test
    public void testN98740E607CC011DC8B69829BAAB1B5B5ParagraphText()
        throws Exception
    {
        String renderedOutput = getWestLawNextRenderedOutput("N2129FB908D7111D8A8ACD145B11214D7");

        int result = buildUrlsFromString(renderedOutput, PARAGRAPH_TEXT_XPATH_EXPR);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location " + PARAGRAPH_TEXT_XPATH_EXPR
            + "  in prerendered output  " + renderedOutput);

        result = buildUrlsFromFile(
                "N98740E607CC011DC8B69829BAAB1B5B5_expected_wln.html", PARAGRAPH_TEXT_XPATH_EXPR);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location " + PARAGRAPH_TEXT_XPATH_EXPR
            + "  in West_Law_rendered xml  " + WEST_LAW_RENDERED_XML);
    }

    @Test
    @Ignore
    public void testN98740E607CC011DC8B69829BAAB1B5B5PrelimGoldenLeaf()
        throws Exception
    {
        String renderedOutput = getWestLawNextRenderedOutput("N98740E607CC011DC8B69829BAAB1B5B5");

        int result = buildUrlsFromString(renderedOutput, PRELIMGOLDENLEAF_XPATH_EXPR_3);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location " + PRELIMGOLDENLEAF_XPATH_EXPR_3
            + "  in prerendered output  " + renderedOutput);

        result = buildUrlsFromFile(
                "N98740E607CC011DC8B69829BAAB1B5B5_expected_wln.html", PRELIMGOLDENLEAF_XPATH_EXPR_3);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location " + PRELIMGOLDENLEAF_XPATH_EXPR_3
            + "  in West_Law_rendered xml  " + WEST_LAW_RENDERED_XML);
    }

    @Test
    public void testNB915D0707CBD11DC8EE4814D1B0549C2ContentBlock()
        throws Exception
    {
        String renderedOutput = getWestLawNextRenderedOutput("NB915D0707CBD11DC8EE4814D1B0549C2");

        int result = buildUrlsFromString(renderedOutput, CONTENT_BLOCK_XPATH_EXPR);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location " + CONTENT_BLOCK_XPATH_EXPR + "  in prerendered output  "
            + renderedOutput);

        result = buildUrlsFromFile(
                "NB915D0707CBD11DC8EE4814D1B0549C2_expected_wln.html", CONTENT_BLOCK_XPATH_EXPR);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location " + CONTENT_BLOCK_XPATH_EXPR
            + "  in West_Law_rendered xml  " + WEST_LAW_RENDERED_XML);
    }

    @Test
    public void testNB915D0707CBD11DC8EE4814D1B0549C2ParagraphText()
        throws Exception
    {
        String renderedOutput = getWestLawNextRenderedOutput("NB915D0707CBD11DC8EE4814D1B0549C2");

        int result = buildUrlsFromString(renderedOutput, PARAGRAPH_TEXT_XPATH_EXPR);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location " + PARAGRAPH_TEXT_XPATH_EXPR
            + "  in prerendered output  " + renderedOutput);

        result = buildUrlsFromFile(
                "NB915D0707CBD11DC8EE4814D1B0549C2_expected_wln.html", PARAGRAPH_TEXT_XPATH_EXPR);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location " + PARAGRAPH_TEXT_XPATH_EXPR
            + "  in West_Law_rendered xml  " + WEST_LAW_RENDERED_XML);
    }

    @Test
    public void testNB915D0707CBD11DC8EE4814D1B0549C2PrelimGoldenLeaf()
        throws Exception
    {
        String renderedOutput = getWestLawNextRenderedOutput("NB915D0707CBD11DC8EE4814D1B0549C2");

        int result = buildUrlsFromString(renderedOutput, PRELIMGOLDENLEAF_XPATH_EXPR_3);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location " + PRELIMGOLDENLEAF_XPATH_EXPR_3
            + "  in prerendered output  " + renderedOutput);

        result = buildUrlsFromFile(
                "NB915D0707CBD11DC8EE4814D1B0549C2_expected_wln.html", PRELIMGOLDENLEAF_XPATH_EXPR_3);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location " + PRELIMGOLDENLEAF_XPATH_EXPR_3
            + "  in West_Law_rendered xml  " + WEST_LAW_RENDERED_XML);
    }

    @Test
    public void testUrlBuilderAdapterForKeyciteFlagUrl()
        throws Exception
    {
        String inputXmlFragment = "<keyCiteFlagLink.Url docGuid=\"DOC_GUID\"/>";
        StreamSource inputSource =
            new StreamSource(new ByteArrayInputStream(inputXmlFragment.getBytes("UTF-8")));

        Source xsltSource =
            new StreamSource(
                "/apps/ebookbuilder/staticContent/ContentTypes/" + CODES_STATUTES_XSLT);

        TransformerFactory transFact = TransformerFactory.newInstance();
        XSLIncludeResolver resolver = new XSLIncludeResolver();
        transFact.setURIResolver(resolver);

        Transformer trans = transFact.newTransformer(xsltSource);
        trans.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

        Result result = new StreamResult(System.out);

        trans.transform(inputSource, result);
    }

    @Test
    public void testUrlBuilderAdapterLinksUsingPdfImageHref()
        throws Exception
    {
        String renderedOutput = getWestLawNextRenderedOutput(PRE_RENDERED_GUID);
        System.out.println("Chinana" + renderedOutput);

        int result = buildUrlsFromString(renderedOutput, DOCUMENT_HEAD_XPATH_EXPR);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location " + DOCUMENT_HEAD_XPATH_EXPR + "  in prerendered output  "
            + renderedOutput);

        result = buildUrlsFromFile(WEST_LAW_RENDERED_XML, DOCUMENT_HEAD_XPATH_EXPR);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location " + DOCUMENT_HEAD_XPATH_EXPR
            + "  in West_Law_rendered xml  " + WEST_LAW_RENDERED_XML);
    }

    @Test
    public void testUrlBuilderAdapterLinksUsingPdfImageMetadata()
        throws Exception
    {
        String renderedOutput = getWestLawNextRenderedOutput(PRE_RENDERED_GUID);

        int result = buildUrlsFromString(renderedOutput, IMAGEBLOCK_XPATH_EXPR);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location " + IMAGEBLOCK_XPATH_EXPR + "  in prerendered output  "
            + renderedOutput);

        result = buildUrlsFromFile(WEST_LAW_RENDERED_XML, IMAGEBLOCK_WEST_LAW_XPATH_EXPR);

        Assert.isTrue(
            result != 0,
            "Unable to find Url location " + IMAGEBLOCK_WEST_LAW_XPATH_EXPR
            + "  in West_Law_rendered xml  " + WEST_LAW_RENDERED_XML);
    }

    @Ignore
    @Test
    public void testUrlBuilderForImphAnalyticalProduct()
        throws Exception
    {
        File transformedDirectory = tempDirectory.newFolder("transformed");
        File novusXmlDirectory = new File("/nas/imph/gather/documents");
        Map<String, Transformer> xsltCache = new HashMap<String, Transformer>();

        setExpectationsForNovusDocumentCalls(novusXmlDirectory);

        for (File novusXmlDocument : novusXmlDirectory.listFiles())
        {
            if (!novusXmlDocument.isDirectory())
            {
                transformerService.transformFile(
                    novusXmlDocument, novusXmlDocument.getParentFile(),
                    novusXmlDocument.getParentFile(), transformedDirectory, titleId, jobId,
                    xsltCache, false);
            }
        }

        verifyAll();
    }

    private int buildUrlsFromFile(final String xmlFile, final String xpathString)
    {
        File expectedWlnOutput =
            new File(PersistentUrlTransformIntegrationTests.class.getResource(xmlFile).getFile());

        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware(true);

        DocumentBuilder builder;
        NodeList nodes = null;

        try
        {
            builder = domFactory.newDocumentBuilder();

            Document doc = builder.parse(expectedWlnOutput);

            XPathFactory factory = XPathFactory.newInstance();
            XPath xpath = factory.newXPath();
            XPathExpression expr = xpath.compile(xpathString);
            Object result = expr.evaluate(doc, XPathConstants.NODESET);
            nodes = (NodeList) result;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return nodes.getLength();
    }

    private int buildUrlsFromString(final String str, final String xpathExpression)
    {
        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware(true);

        DocumentBuilder builder;
        NodeList nodes = null;

        try
        {
            builder = domFactory.newDocumentBuilder();

            Document doc = builder.parse(new ByteArrayInputStream(str.getBytes("UTF-8")));

            XPathFactory factory = XPathFactory.newInstance();
            XPath xpath = factory.newXPath();
            XPathExpression expr = xpath.compile(xpathExpression);
            Object result = expr.evaluate(doc, XPathConstants.NODESET);
            nodes = (NodeList) result;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        for (int i = 0; i < nodes.getLength(); i++)
        {
        }

        return nodes.getLength();
    }

    private String getWestLawNextRenderedOutput(String preRenderedInput)
        throws EBookFormatException, FileNotFoundException, IOException
    {
        novusXmlFilename = preRenderedInput + ".xml";

        mockDocMetadataService = EasyMock.createMock(DocMetadataService.class);

        EasyMock.expect(
            mockDocMetadataService.findDocMetadataByPrimaryKey(
                "uscl/an/IMPH", new Long(12345), preRenderedInput)).andReturn(mockDocMetadata);

        EasyMock.replay(mockDocMetadataService);
        MOCK_INPUT_STREAM = new ByteArrayInputStream("Head".getBytes());

        EasyMock.expect(mocGenerateDocumentDataBlockService.getDocumentDataBlockAsStream("uscl/an/IMPH", new Long(12345), preRenderedInput))
        .andReturn(MOCK_INPUT_STREAM);
        transformerService.setdocMetadataService(mockDocMetadataService);
        transformerService.setGenerateDocumentDataBlockService(mocGenerateDocumentDataBlockService);
        EasyMock.replay(mocGenerateDocumentDataBlockService);

        File novusXml =
            new File(
                PersistentUrlTransformIntegrationTests.class.getResource(novusXmlFilename).getFile());
        File transformedDirectory = tempDirectory.newFolder("transformed");
        Map<String, Transformer> xsltCache = new HashMap<String, Transformer>();

        transformerService.transformFile(
            novusXml, novusXml.getParentFile(), novusXml.getParentFile(), transformedDirectory,
            titleId, jobId, xsltCache, false);

        verifyAll();

        String renderedOutput =
            IOUtils.toString(
                new FileInputStream(
                    new File(transformedDirectory, preRenderedInput + ".TRANSFORMED")));

        return renderedOutput;
    }

    private void setExpectationsForNovusDocumentCalls(File novusXmlDirectory)
    {
        mockXsltMapperService = EasyMock.createMock(XSLTMapperService.class);
        mockDocMetadata = EasyMock.createMock(DocMetadata.class);
        mockDocMetadataService = EasyMock.createMock(DocMetadataService.class);

        for (File novusXmlDocument : novusXmlDirectory.listFiles())
        {
            if (!novusXmlDocument.isDirectory())
            {
                EasyMock.expect(
                    mockDocMetadataService.findDocMetadataByPrimaryKey(
                        "uscl/an/IMPH", new Long(12345),
                        StringUtils.substringBefore(novusXmlDocument.getName(), ".")))
                        .andReturn(mockDocMetadata);

                EasyMock.expect(mockDocMetadata.getCollectionName()).andReturn(MOCK_COLLECTION)
                        .times(2);
                EasyMock.expect(mockDocMetadata.getDocType()).andReturn(MOCK_DOCTYPE);
                EasyMock.expect(mockXsltMapperService.getXSLT(MOCK_COLLECTION, MOCK_DOCTYPE))
                        .andReturn(ANALYTICAL_XSLT);
            }
        }

        EasyMock.replay(mockDocMetadataService);
        EasyMock.replay(mockDocMetadata);
        EasyMock.replay(mockXsltMapperService);
        transformerService = new TransformerServiceImpl();
        transformerService.setxsltMapperService(mockXsltMapperService);
        transformerService.setdocMetadataService(mockDocMetadataService);
    }

    private void verifyAll()
    {
        EasyMock.verify(mockDocMetadataService);
        EasyMock.verify(mockDocMetadata);
        EasyMock.verify(mockXsltMapperService);
    }
}
