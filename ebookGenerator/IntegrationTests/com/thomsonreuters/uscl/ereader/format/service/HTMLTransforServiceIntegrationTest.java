package com.thomsonreuters.uscl.ereader.format.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.IOUtils;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.springframework.util.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadata;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocumentMetadataAuthority;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetadataService;
import com.thomsonreuters.uscl.ereader.ioutil.FileExtensionFilter;
import com.thomsonreuters.uscl.ereader.ioutil.FileHandlingHelper;


/**
* Applies any post transformation on the HTML that needs to be done to cleanup or make
 * the HTML acceptable for ProView.
 *
 * @author <a href="mailto:ravi.nandikolla@thomsonreuters.com">Ravi Nandikolla</a> c139353
 */
public class HTMLTransforServiceIntegrationTest
{
    private static final String preRenderedInput = "NADB029C0880F11D881E9FEF4A4D44D69";
    String titleId;
    String novusXmlFilename;
    String novusMetadataFilename;
    long jobId;
    FileHandlingHelper fileHandlingHelper;
    FileExtensionFilter fileExtFilter;
    HTMLTransformerServiceImpl htmlTransforService;
    DocMetadata mockDocMetadata;
    DocMetadataService mockDocMetadataService;
    DocumentMetadataAuthority mockDocumentMetadataAuthority;
    private final String testExtension = ".html";
    @Rule
    public TemporaryFolder tempDirectory = new TemporaryFolder();

    @Before
    public void setUp()
    {
        mockDocMetadataService = EasyMock.createMock(DocMetadataService.class);
        EasyMock.expect(
            mockDocMetadataService.findDocMetadataByPrimaryKey(
                "uscl/an/IMPH", new Integer(12345), preRenderedInput)).andReturn(mockDocMetadata);
        fileExtFilter = EasyMock.createMock(FileExtensionFilter.class);
        fileHandlingHelper = EasyMock.createMock(FileHandlingHelper.class);
        fileExtFilter.setAcceptedFileExtensions(new String[] {testExtension});
        fileHandlingHelper.setFilter(fileExtFilter);
        htmlTransforService = new HTMLTransformerServiceImpl();
        htmlTransforService.setfileHandlingHelper(fileHandlingHelper);
        
        mockDocumentMetadataAuthority = EasyMock.createMock(DocumentMetadataAuthority.class);
        EasyMock.replay(mockDocMetadataService);
        EasyMock.replay(fileExtFilter);
        EasyMock.replay(fileHandlingHelper);

        titleId = "uscl/an/IMPH";
        novusXmlFilename = "NADB029C0880F11D881E9FEF4A4D44D69.html";
        novusMetadataFilename = "27-w_codesstacanvdu-NADB029C0880F11D881E9FEF4A4D44D69.xml";
        jobId = 12345L;
    }

    @Test
    public void transformWithOutTableView() throws EBookFormatException, IOException
    {
        int count = getTableViewInfo(false);

        Assert.isTrue(count == 0, "Unable to find  table in document");
    }

    @Test
    public void transformWithTableView()
        throws EBookFormatException, FileNotFoundException, IOException
    {
        int count = getTableViewInfo(true);

        Assert.isTrue(count > 0, "Unable to transform as Table View");
    }

    /**
     * @param str renderedOutput String.
     * @param xpathExpression xpathExpression to extract Table anchors.
     * @return
     */
    private int buildTableAnchorsFromOutput(final String str, final String xpathExpression)
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
    private int getTableViewInfo(boolean isTableViewRequired)
        throws EBookFormatException, FileNotFoundException, IOException
    {
        htmlTransforService.setdocMetadataService(mockDocMetadataService);

        Set<String> staticImages = new HashSet<String>();
        File transformedDirectory = tempDirectory.newFolder("transformed");

        File novusXml =
            new File(
                HTMLTransforServiceIntegrationTest.class.getResource(novusXmlFilename).getFile());
        htmlTransforService.transformHTMLFile(
            novusXml, transformedDirectory, staticImages, isTableViewRequired, titleId, jobId,mockDocumentMetadataAuthority);

        String renderedOutput =
            IOUtils.toString(
                new FileInputStream(
                    new File(transformedDirectory, preRenderedInput + ".postTransform")));

        return buildTableAnchorsFromOutput(renderedOutput, "//table[@class='tr_table']");
    }
}
