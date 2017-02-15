package com.thomsonreuters.uscl.ereader.format.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import com.thomsonreuters.uscl.ereader.core.book.domain.TableViewer;
import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadata;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocumentMetadataAuthority;
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
import org.springframework.util.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
* Applies any post transformation on the HTML that needs to be done to cleanup or make
 * the HTML acceptable for ProView.
 *
 * @author <a href="mailto:ravi.nandikolla@thomsonreuters.com">Ravi Nandikolla</a> c139353
 */
public final class HTMLTransforServiceIntegrationTest
{
    private static final String preRenderedInput = "NADB029C0880F11D881E9FEF4A4D44D69";
    private String titleId;
    private String novusXmlFilename;
    private String novusXmlFileName1;
    private long jobId;
    private FileHandlingHelper fileHandlingHelper;
    private FileExtensionFilter fileExtFilter;
    private HTMLTransformerServiceImpl htmlTransforService;
    private DocMetadata mockDocMetadata;
    private DocMetadataService mockDocMetadataService;
    private DocumentMetadataAuthority mockDocumentMetadataAuthority;
    private Map<String, Set<String>> targetAnchors;

    private final String testExtension = ".html";
    private File docsGuidFile;
    @Rule
    public TemporaryFolder tempDirectory = new TemporaryFolder();
    private String version = "1";

    @Before
    public void setUp()
    {
//        mockDocMetadataService = EasyMock.createMock(DocMetadataService.class);
//        EasyMock.expect(
//            mockDocMetadataService.findDocMetadataByPrimaryKey(
//                "uscl/an/IMPH", new Integer(12345), preRenderedInput)).andReturn(mockDocMetadata);
        fileExtFilter = EasyMock.createMock(FileExtensionFilter.class);
        fileHandlingHelper = EasyMock.createMock(FileHandlingHelper.class);
        fileExtFilter.setAcceptedFileExtensions(new String[] {testExtension});
        fileHandlingHelper.setFilter(fileExtFilter);
        htmlTransforService = new HTMLTransformerServiceImpl();
        htmlTransforService.setfileHandlingHelper(fileHandlingHelper);

        // mockDocumentMetadataAuthority = EasyMock.createMock(DocumentMetadataAuthority.class);
        // EasyMock.replay(mockDocMetadataService);
        EasyMock.replay(fileExtFilter);
        EasyMock.replay(fileHandlingHelper);

        titleId = "uscl/an/IMPH";
        targetAnchors = new HashMap<>();
        novusXmlFilename = "NADB029C0880F11D881E9FEF4A4D44D69.html";
        novusXmlFileName1 = "Id8ec0a72cfe111da9bb4a39a5015044e.html";
        jobId = 12345L;
        final String docGuid = "sample-docs-guid.txt";

        docsGuidFile = new File(HTMLTransforServiceIntegrationTest.class.getResource(docGuid).getFile());
    }

    @Ignore
    @Test
    public void transformWithOutTableView() throws EBookFormatException, IOException
    {
        final int count = getTableViewInfo(false);

        Assert.isTrue(count > 0, "Unable to find  table in document");
    }

    @Ignore
    @Test
    public void transformWithTableView() throws EBookFormatException, FileNotFoundException, IOException
    {
        final int count = getTableViewInfo(true);

        Assert.isTrue(count > 0, "Unable to transform as Table View");
    }

    @Ignore
    @Test
    public void testInternalLinks() throws EBookFormatException, FileNotFoundException, IOException
    {
        final int count = getInternalLinkInfo();

        Assert.isTrue(count == 0, "Unable to transform as Table View");
    }

    @Test
    public void testInternalDocumentLinks()
    {
//        int count = getInternalLinkInfo();
//        Assert.isTrue(count == 0, "Unable to transform as Table View");
    }

    /**
     * @param str renderedOutput String.
     * @param xpathExpression xpathExpression to extract Table anchors.
     * @return
     */
    private int buildTableAnchorsFromOutput(final String str, final String xpathExpression)
    {
        final DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware(true);

        final DocumentBuilder builder;
        NodeList nodes = null;

        try
        {
            builder = domFactory.newDocumentBuilder();

            final Document doc = builder.parse(new ByteArrayInputStream(str.getBytes("UTF-8")));

            final XPathFactory factory = XPathFactory.newInstance();
            final XPath xpath = factory.newXPath();
            final XPathExpression expr = xpath.compile(xpathExpression);
            final Object result = expr.evaluate(doc, XPathConstants.NODESET);
            nodes = (NodeList) result;
        }
        catch (final Exception e)
        {
            e.printStackTrace();
        }

        return nodes.getLength();
    }

    /**
     * Returns count of Table views from renderedOutput.
     * @param isTableViewRequired is a boolean value .
     * @return count of Table view anchors from renderedOutput.
     * @throws EBookFormatException
     * @throws FileNotFoundException
     * @throws IOException
     */
    private int getTableViewInfo(final boolean isTableViewRequired)
        throws EBookFormatException, FileNotFoundException, IOException
    {
        mockDocMetadataService = EasyMock.createMock(DocMetadataService.class);
        EasyMock.expect(
            mockDocMetadataService.findDocMetadataByPrimaryKey("uscl/an/IMPH", Long.valueOf(12345), preRenderedInput))
            .andReturn(mockDocMetadata);

        EasyMock.replay(mockDocMetadataService);

        final Set<DocMetadata> documentMetadataSet = new HashSet<>();
        final List<DocMetadata> docMetaList = new ArrayList<>();
        final DocMetadata doc = new DocMetadata();
        doc.setDocUuid("ID4D58042D3-43461C8C9EE-73AA2A319F3");
        doc.setDocFamilyUuid("123456789");
        doc.setJobInstanceId(12345L);
        doc.setTitleId(titleId);
        docMetaList.add(doc);

        final DocMetadata doc1 = new DocMetadata();
        doc1.setNormalizedFirstlineCite("lk(CAIND) lk(CAINR) lk(CASTERR)");
        doc1.setDocFamilyUuid("x123456789");
        doc1.setDocUuid("222222");
        doc1.setJobInstanceId(12345L);
        doc1.setTitleId(titleId);
        docMetaList.add(doc1);

        final DocMetadata doc2 = new DocMetadata();
        doc2.setDocUuid("I649D987021-CD4A9CA1EF7-B06AECEA59A");
        doc2.setDocFamilyUuid("x123456789");
        doc2.setJobInstanceId(12345L);
        doc2.setTitleId(titleId);
        docMetaList.add(doc2);

        final DocMetadata doc3 = new DocMetadata();
        doc3.setDocUuid("I70A4B3CDA7-51471A8B6A0-43256BAD23B");
        doc3.setDocFamilyUuid("x123456789");
        doc3.setJobInstanceId(12345L);
        doc3.setTitleId(titleId);
        docMetaList.add(doc3);

        final DocMetadata doc4 = new DocMetadata();
        doc4.setNormalizedFirstlineCite("CAINS12975.8");
        doc4.setDocFamilyUuid("x123456789");
        doc4.setDocUuid("555555");
        doc4.setJobInstanceId(12345L);
        doc4.setTitleId(titleId);
        docMetaList.add(doc4);

        final DocMetadata doc5 = new DocMetadata();
        doc5.setNormalizedFirstlineCite("CAINS12975.1");
        doc5.setDocFamilyUuid("x123456789");
        doc5.setDocUuid("666666");
        doc5.setJobInstanceId(12345L);
        doc5.setTitleId(titleId);
        docMetaList.add(doc5);

        final DocMetadata doc6 = new DocMetadata();
        doc6.setNormalizedFirstlineCite("CAINS1874.8");
        doc6.setDocFamilyUuid("x123456789");
        doc6.setDocUuid("777777");
        doc6.setJobInstanceId(12345L);
        doc6.setTitleId(titleId);
        docMetaList.add(doc6);

        //IF35149D0465911E1AD76A188D0BE1F6D

        final DocMetadata doc7 = new DocMetadata();
        doc7.setDocFamilyUuid("123x123456789");
        doc7.setDocUuid("IF35149D0465911E1AD76A188D0BE1F6D");
        doc7.setJobInstanceId(12345L);
        doc7.setTitleId(titleId);
        docMetaList.add(doc7);

        final DocMetadata doc8 = new DocMetadata();
        doc8.setDocFamilyUuid("123x123456789");
        doc8.setDocUuid("NADB029C0880F11D881E9FEF4A4D44D69");
        doc8.setJobInstanceId(12345L);
        doc8.setTitleId(titleId);
        docMetaList.add(doc8);

        documentMetadataSet.addAll(docMetaList);

        mockDocumentMetadataAuthority = new DocumentMetadataAuthority(documentMetadataSet);

        htmlTransforService.setdocMetadataService(mockDocMetadataService);

        final Set<String> staticImages = new HashSet<>();
        final File transformedDirectory = tempDirectory.newFolder("transformed");
        final File novusXml =
            new File(HTMLTransforServiceIntegrationTest.class.getResource(novusXmlFilename).getFile());
        htmlTransforService.transformHTMLFile(
            novusXml,
            transformedDirectory,
            staticImages,
            new ArrayList<TableViewer>(),
            new ArrayList<TableViewer>(),
            titleId,
            jobId,
            mockDocumentMetadataAuthority,
            targetAnchors,
            docsGuidFile,
            docsGuidFile,
            false,
            false,
            false,
            version);

        final String renderedOutput =
            IOUtils.toString(new FileInputStream(new File(transformedDirectory, preRenderedInput + ".postTransform")));
        return buildTableAnchorsFromOutput(renderedOutput, "//table[@class='tr_table']");
    }

    /**
     * Returns count of internal Link url from renderedOutput.
     * @return count of internal Link url from renderedOutput.
     * @throws EBookFormatException
     * @throws FileNotFoundException
     * @throws IOException
     */
    private int getInternalLinkInfo() throws EBookFormatException, FileNotFoundException, IOException
    {
        mockDocMetadataService = EasyMock.createMock(DocMetadataService.class);
        EasyMock.expect(
            mockDocMetadataService.findDocMetadataByPrimaryKey("uscl/an/IMPH", Long.valueOf(12345), preRenderedInput))
            .andReturn(mockDocMetadata);

        EasyMock.replay(mockDocMetadataService);

        final Set<DocMetadata> documentMetadataSet = new HashSet<>();
        final List<DocMetadata> docMetaList = new ArrayList<>();
        final DocMetadata doc = new DocMetadata();
        doc.setDocUuid("ID4D58042D3-43461C8C9EE-73AA2A319F3");
        doc.setDocFamilyUuid("123456789");
        doc.setJobInstanceId(12345L);
        doc.setTitleId(titleId);
        doc.setSpitBookTitle("splitBookTitle");
        docMetaList.add(doc);

        final DocMetadata doc1 = new DocMetadata();
        doc1.setNormalizedFirstlineCite("lk(CAIND) lk(CAINR) lk(CASTERR)");
        doc1.setDocFamilyUuid("x123456789");
        doc1.setDocUuid("222222");
        doc1.setJobInstanceId(12345L);
        doc1.setTitleId(titleId);
        doc1.setSpitBookTitle("splitBookTitle1");
        docMetaList.add(doc1);

        final DocMetadata doc2 = new DocMetadata();
        doc2.setDocUuid("I649D987021-CD4A9CA1EF7-B06AECEA59A");
        doc2.setDocFamilyUuid("x123456789");
        doc2.setJobInstanceId(12345L);
        doc2.setTitleId(titleId);
        doc2.setSpitBookTitle("splitBookTitle2");
        docMetaList.add(doc2);

        final DocMetadata doc3 = new DocMetadata();
        doc3.setDocUuid("I70A4B3CDA7-51471A8B6A0-43256BAD23B");
        doc3.setDocFamilyUuid("x123456789");
        doc3.setJobInstanceId(12345L);
        doc3.setTitleId(titleId);
        doc3.setSpitBookTitle("splitBookTitle3");
        docMetaList.add(doc3);

        final DocMetadata doc4 = new DocMetadata();
        doc4.setNormalizedFirstlineCite("CAINS12975.8");
        doc4.setDocFamilyUuid("x123456789");
        doc4.setDocUuid("555555");
        doc4.setJobInstanceId(12345L);
        doc4.setTitleId(titleId);
        doc4.setSpitBookTitle("splitBookTitle4");
        docMetaList.add(doc4);

        final DocMetadata doc5 = new DocMetadata();
        doc5.setNormalizedFirstlineCite("CAINS12975.1");
        doc5.setDocFamilyUuid("x123456789");
        doc5.setDocUuid("666666");
        doc5.setJobInstanceId(12345L);
        doc5.setTitleId(titleId);
        doc5.setSpitBookTitle("splitBookTitle5");
        docMetaList.add(doc5);

        final DocMetadata doc6 = new DocMetadata();
        doc6.setNormalizedFirstlineCite("CAINS1874.8");
        doc6.setDocFamilyUuid("x123456789");
        doc6.setDocUuid("777777");
        doc6.setJobInstanceId(12345L);
        doc6.setTitleId(titleId);
        doc6.setSpitBookTitle("splitBookTitle6");
        docMetaList.add(doc6);

        //IF35149D0465911E1AD76A188D0BE1F6D

        final DocMetadata doc7 = new DocMetadata();
        doc7.setDocFamilyUuid("123x123456789");
        doc7.setDocUuid("IF35149D0465911E1AD76A188D0BE1F6D");
        doc7.setJobInstanceId(12345L);
        doc7.setTitleId(titleId);
        doc7.setSpitBookTitle("splitBookTitle7");
        docMetaList.add(doc7);

        final DocMetadata doc8 = new DocMetadata();
        doc8.setDocFamilyUuid("123x123456789");
        doc8.setDocUuid("NADB029C0880F11D881E9FEF4A4D44D69");
        doc8.setJobInstanceId(12345L);
        doc8.setTitleId(titleId);
        doc8.setSpitBookTitle("splitBookTitle8");
        docMetaList.add(doc8);

        documentMetadataSet.addAll(docMetaList);

        mockDocumentMetadataAuthority = new DocumentMetadataAuthority(documentMetadataSet);

        htmlTransforService.setdocMetadataService(mockDocMetadataService);

        final Set<String> staticImages = new HashSet<>();
        final File transformedDirectory = tempDirectory.newFolder("transformed");

        final File novusXml =
            new File(HTMLTransforServiceIntegrationTest.class.getResource(novusXmlFilename).getFile());
        htmlTransforService.transformHTMLFile(
            novusXml,
            transformedDirectory,
            staticImages,
            new ArrayList<TableViewer>(),
            new ArrayList<TableViewer>(),
            titleId,
            jobId,
            mockDocumentMetadataAuthority,
            targetAnchors,
            docsGuidFile,
            docsGuidFile,
            false,
            false,
            false,
            version);

        final String renderedOutput =
            IOUtils.toString(new FileInputStream(new File(transformedDirectory, preRenderedInput + ".postTransform")));
        return buildTableAnchorsFromOutput(renderedOutput, "//a[@href='er:#x123456789']");
    }

    /**
     * Returns count of internal Link url from renderedOutput.
     * @return count of internal Link url from renderedOutput.
     * @throws EBookFormatException
     * @throws FileNotFoundException
     * @throws IOException
     */
    private int getInternalLinkInfo1() throws EBookFormatException, FileNotFoundException, IOException
    {
        mockDocMetadataService = EasyMock.createMock(DocMetadataService.class);
        EasyMock
            .expect(
                mockDocMetadataService.findDocMetadataByPrimaryKey(
                    "uscl/an/IMPH",
                    Long.valueOf(12345),
                    "Id8ec0a72cfe111da9bb4a39a5015044e"))
            .andReturn(mockDocMetadata);

        EasyMock.replay(mockDocMetadataService);
        final Set<DocMetadata> documentMetadataSet = new HashSet<>();
        final List<DocMetadata> docMetaList = new ArrayList<>();
        final DocMetadata doc = new DocMetadata();
        doc.setNormalizedFirstlineCite("MSEVDMYCORCH1");
        doc.setDocUuid("999999999999");
        doc.setDocFamilyUuid("9999XXXX123456789");
        doc.setJobInstanceId(12345L);
        doc.setTitleId(titleId);
        docMetaList.add(doc);

        final DocMetadata doc1 = new DocMetadata();
        doc1.setNormalizedFirstlineCite("MODSCIEVIDs1:12");
        doc1.setDocFamilyUuid("x123456789");
        doc1.setDocUuid("222222");
        doc1.setJobInstanceId(12345L);
        doc1.setTitleId(titleId);
        docMetaList.add(doc1);

        final DocMetadata doc2 = new DocMetadata();
        doc2.setDocUuid("Id8ec0a75cfe111da9bb4a39a5015044e");
        doc2.setDocFamilyUuid("x123456789");
        //doc2.setDocUuid("333333");
        doc2.setJobInstanceId(12345L);
        doc2.setTitleId(titleId);
        docMetaList.add(doc2);

        final DocMetadata doc3 = new DocMetadata();
        doc3.setDocUuid("I70A4B3CDA7-51471A8B6A0-43256BAD23B");
        doc3.setDocFamilyUuid("x123456789");
        doc3.setNormalizedFirstlineCite("MODSCIEVIDs1:11");
        doc3.setJobInstanceId(12345L);
        doc3.setTitleId(titleId);
        docMetaList.add(doc3);

        final DocMetadata doc31 = new DocMetadata();
        doc31.setDocUuid("I127ff9304e8111e181c20000837bc6dd");
        doc31.setDocFamilyUuid("12345xxx6789");
        doc31.setJobInstanceId(12345L);
        doc31.setTitleId(titleId);
        docMetaList.add(doc31);

        final DocMetadata doc41 = new DocMetadata();
        doc41.setDocUuid("I127fab104e8111e181c20000837bc6dd");
        doc41.setDocFamilyUuid("12345xxx6789");
        doc41.setJobInstanceId(12345L);
        doc41.setTitleId(titleId);
        docMetaList.add(doc41);

        final DocMetadata doc51 = new DocMetadata();
        doc51.setDocUuid("I1280bc804e8111e181c20000837bc6dd");
        doc51.setDocFamilyUuid("1234yxxx6789");
        doc51.setJobInstanceId(12345L);
        doc51.setTitleId(titleId);
        docMetaList.add(doc51);

        final DocMetadata doc15 = new DocMetadata();
        doc15.setDocUuid("I128417e04e8111e181c20000837bc6dd");
        doc15.setDocFamilyUuid("1234yxxx6789");
        doc15.setJobInstanceId(12345L);
        doc15.setTitleId(titleId);
        docMetaList.add(doc15);

        final DocMetadata doc16 = new DocMetadata();
        doc16.setDocUuid("I12848d104e8111e181c20000837bc6dd");
        doc16.setDocFamilyUuid("1234yxxx6789");
        doc16.setJobInstanceId(12345L);
        doc16.setTitleId(titleId);
        docMetaList.add(doc16);

        final DocMetadata doc61 = new DocMetadata();
        doc61.setDocUuid("I128158c04e8111e181c20000837bc6dd");
        doc61.setDocFamilyUuid("1234yxxx6789");
        doc61.setJobInstanceId(12345L);
        doc61.setTitleId(titleId);
        docMetaList.add(doc61);

        final DocMetadata doc71 = new DocMetadata();
        doc71.setDocUuid("I1281f5004e8111e181c20000837bc6dd");
        doc71.setDocFamilyUuid("1234yxxx6789");
        doc71.setJobInstanceId(12345L);
        doc71.setTitleId(titleId);
        docMetaList.add(doc71);

        final DocMetadata doc81 = new DocMetadata();
        doc81.setDocUuid("I1281f5004e8111e181c20000837bc6dd");
        doc81.setDocFamilyUuid("1234yyxx6789");
        doc81.setJobInstanceId(12345L);
        doc81.setTitleId(titleId);
        docMetaList.add(doc81);

        final DocMetadata doc91 = new DocMetadata();
        doc91.setDocUuid("I1282df604e8111e181c20000837bc6dd");
        doc91.setDocFamilyUuid("1234yyxx6789");
        doc91.setJobInstanceId(12345L);
        doc91.setTitleId(titleId);
        docMetaList.add(doc91);

        final DocMetadata doc92 = new DocMetadata();
        doc92.setDocUuid("I128354904e8111e181c20000837bc6dd");
        doc92.setDocFamilyUuid("1234yyxx6789");
        doc92.setJobInstanceId(12345L);
        doc92.setTitleId(titleId);
        docMetaList.add(doc92);

        final DocMetadata doc4 = new DocMetadata();
        doc4.setNormalizedFirstlineCite("MODSCIEVIDs40:1");
        doc4.setDocFamilyUuid("x123456789");
        doc4.setDocUuid("555555");
        doc4.setJobInstanceId(12345L);
        doc4.setTitleId(titleId);
        docMetaList.add(doc4);

        final DocMetadata doc5 = new DocMetadata();
        doc5.setNormalizedFirstlineCite("27JFORENSICSCI684");
        doc5.setDocFamilyUuid("x123456789");
        doc5.setDocUuid("666666");
        doc5.setJobInstanceId(12345L);
        doc5.setTitleId(titleId);
        docMetaList.add(doc5);

        final DocMetadata doc6 = new DocMetadata();
        doc6.setNormalizedFirstlineCite("MODSCIEVIDs16");
        doc6.setDocFamilyUuid("x123456789");
        doc6.setDocUuid("8777777");
        doc6.setJobInstanceId(12345L);
        doc6.setTitleId(titleId);
        docMetaList.add(doc6);

        final DocMetadata doc9 = new DocMetadata();
        Long ss = Long.valueOf("0105114615");
        doc9.setSerialNumber(ss);
        doc9.setDocFamilyUuid("8816x123456789");
        doc9.setDocUuid("1689898989");
        doc9.setJobInstanceId(12345L);
        doc9.setTitleId(titleId);
        docMetaList.add(doc9);

        final DocMetadata doc8 = new DocMetadata();
        ss = Long.valueOf("0292127061");
        doc8.setSerialNumber(ss);
        doc8.setDocFamilyUuid("8916x123456789");
        doc8.setDocUuid("1789898989");
        doc8.setJobInstanceId(12345L);
        doc8.setTitleId(titleId);
        docMetaList.add(doc8);

        final DocMetadata doc11 = new DocMetadata();
        doc11.setSerialNumber(Long.valueOf(1977110977));
        doc11.setDocFamilyUuid("516x123456789");
        doc11.setDocUuid("669898989");
        doc11.setJobInstanceId(12345L);
        doc11.setTitleId(titleId);
        docMetaList.add(doc11);

        //1924122438

        final DocMetadata doc12 = new DocMetadata();
        doc12.setSerialNumber(Long.valueOf(1924122438));
        doc12.setDocFamilyUuid("777x123456789");
        doc12.setDocUuid("873429898989");
        doc12.setJobInstanceId(12345L);
        doc12.setTitleId(titleId);
        docMetaList.add(doc12);

        final DocMetadata doc13 = new DocMetadata();
        doc13.setSerialNumber(Long.valueOf(1924122438));
        doc13.setDocFamilyUuid("777x123456789");
        doc13.setDocUuid("NADB029C0880F11D881E9FEF4A4D44D69");
        doc13.setJobInstanceId(12345L);
        doc13.setTitleId(titleId);
        docMetaList.add(doc13);

        documentMetadataSet.addAll(docMetaList);

        mockDocumentMetadataAuthority = new DocumentMetadataAuthority(documentMetadataSet);

        htmlTransforService.setdocMetadataService(mockDocMetadataService);

        final Set<String> staticImages = new HashSet<>();
        final File transformedDirectory = tempDirectory.newFolder("transformed");

        final File novusXml =
            new File(HTMLTransforServiceIntegrationTest.class.getResource(novusXmlFileName1).getFile());
        htmlTransforService.transformHTMLFile(
            novusXml,
            transformedDirectory,
            staticImages,
            new ArrayList<TableViewer>(),
            new ArrayList<TableViewer>(),
            titleId,
            jobId,
            mockDocumentMetadataAuthority,
            targetAnchors,
            docsGuidFile,
            docsGuidFile,
            false,
            false,
            false,
            version);
        final String renderedOutput = IOUtils.toString(
            new FileInputStream(
                new File(transformedDirectory, "Id8ec0a72cfe111da9bb4a39a5015044e" + ".postTransform")));

        return buildTableAnchorsFromOutput(renderedOutput, "//a[@href='er:#777x123456789']");
    }
}
