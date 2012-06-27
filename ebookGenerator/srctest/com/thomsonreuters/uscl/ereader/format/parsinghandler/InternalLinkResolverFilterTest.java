/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.apache.xml.serializer.Method;
import org.apache.xml.serializer.OutputPropertiesFactory;
import org.apache.xml.serializer.Serializer;
import org.apache.xml.serializer.SerializerFactory;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadata;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocumentMetadataAuthority;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.PaceMetadata;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetadataService;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.PaceMetadataService;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.PaceMetadataServiceImpl;
import com.thomsonreuters.uscl.ereader.util.UrlParsingUtil;


/**
 * Component tests for InternalLinkResolverFilter.
 *
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 */
public class InternalLinkResolverFilterTest
{
    private static Logger LOG = Logger.getLogger(InternalLinkResolverFilterTest.class);
    InternalLinkResolverFilter internalLinkResolverFilter;
    private InternalLinkResolverFilter internalLinksFilter;
    private Serializer serializer;
    private DocumentMetadataAuthority mockDocumentMetadataAuthority;
    private PaceMetadataService mockPaceMetadataService;
    private PaceMetadata mockPaceMetadata;
    private DocMetadataService mockDocMetadataService;
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();
    private File tempDir = null;

    @Before
    public void setUp() throws Exception
    {
    	mockPaceMetadataService = EasyMock.createMock(PaceMetadataServiceImpl.class);
        mockPaceMetadata = EasyMock.createMock(PaceMetadata.class);
        mockPaceMetadata.setPublicationName("CCPEMPLOYMENT");
        mockPaceMetadata.setAuditId(new Long("3862349"));
        mockPaceMetadata.setActive("L");
        mockPaceMetadata.setAuthorityName("EBOOK");
        mockPaceMetadata.setLongPubName("EBOOK_HELLO");
        mockPaceMetadata.setPublicationCode(new Long(126977));
        mockPaceMetadata.setSpecificCategory("TEST");
        mockPaceMetadata.setStdPubName("CCPEMP"); 
        mockPaceMetadata.setPublicationId(new Long("3862349"));

        List<PaceMetadata> pacemetadataList = new ArrayList<PaceMetadata>();
        EasyMock.expect(mockPaceMetadataService.findAllPaceMetadataForPubCode(new Long(126977)))
                .andReturn(pacemetadataList);
        
       // EasyMock.replay(mockPaceMetadata);
        EasyMock.replay(mockPaceMetadataService);
        mockDocumentMetadataAuthority = EasyMock.createMock(DocumentMetadataAuthority.class);
        internalLinkResolverFilter = new InternalLinkResolverFilter(mockDocumentMetadataAuthority);
        
        mockDocMetadataService = EasyMock.createMock(DocMetadataService.class);        

        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);

        SAXParser saxParser = factory.newSAXParser();

        tempDir = temporaryFolder.newFolder("junit_internalLinking");

        File internalLinkResolverTestFile = new File(tempDir, "internalLinkResolverTestFile.txt");
        writeDocumentLinkFile(internalLinkResolverTestFile);

        internalLinkResolverTestFile.exists();

        internalLinksFilter = new InternalLinkResolverFilter(
                mockDocumentMetadataAuthority, internalLinkResolverTestFile, mockPaceMetadataService,
                mockDocMetadataService, new Long("1234"));
        internalLinksFilter.setParent(saxParser.getXMLReader());

        Properties props = OutputPropertiesFactory.getDefaultMethodProperties(Method.XHTML);
        props.setProperty("omit-xml-declaration", "yes");
        serializer = SerializerFactory.getSerializer(props);
    }

    @After
    public void tearDown() throws Exception
    {
        serializer = null;
        internalLinksFilter = null;
    }

    @Test
    public void testGetDocumentUuidFromResourceUrl() throws Exception
    {
        String resourceUrl =
            "https://a.next.westlaw.com/Document/FullText?Iff5a5aaa7c8f11da9de6e47d6d5aa7a5/View/FullText.html?transitionType=Default&contextData=(sc.Default)";
        Map<String, String> urlValues = UrlParsingUtil.parseUrlContents(resourceUrl);
        String documentUuid = urlValues.get("documentUuid");
        String expectedUuid = "Iff5a5aaa7c8f11da9de6e47d6d5aa7a5";
        Assert.assertEquals(expectedUuid, documentUuid);
        resourceUrl = "https://a.next.westlaw.com/Document/FullText?Iff5a5aaa7c-8f11da9de6e-47d6d5aa7a5/View/FullText.html?transitionType=Default&contextData=(sc.Default)";
        urlValues = UrlParsingUtil.parseUrlContents(resourceUrl);
        documentUuid = urlValues.get("documentUuid");
        expectedUuid = "Iff5a5aaa7c-8f11da9de6e-47d6d5aa7a5";
        Assert.assertEquals(expectedUuid, documentUuid);
    }

    @Test
    public void testGetLinkParameter() throws Exception
    {
        String resourceUrl =
            "http://www.westlaw.com/Link/Document/FullText?findType=L&amp;pubNum=1000546&cite=42USCAS1395W-133&originationContext=ebook&amp;RS=ebbp3.0&amp;vr=3.0#co_pp_8b3b0000958a";
        Map<String, String> urlValues = UrlParsingUtil.parseUrlContents(resourceUrl);
        String linkParameter = urlValues.get("reference");
        String normalizedCite = urlValues.get("cite");
        String expectedLinkParameter = "co_pp_8b3b0000958a";
        Assert.assertEquals(expectedLinkParameter, linkParameter);

        DocMetadata dm = new DocMetadata();
        dm.setDocFamilyUuid("IC6A94E80FF6011DC95B0EEFA5102EA59");
        dm.setDocUuid("NF8C65500AFF711D8803AE0632FEDDFBF");

        Map<String, DocMetadata> mp = new HashMap<String, DocMetadata>();
        mp.put(normalizedCite, dm);
        EasyMock.expect(mockDocumentMetadataAuthority.getDocMetadataKeyedByCite()).andReturn(mp);
        EasyMock.replay(mockDocumentMetadataAuthority);

        String inputXML =
            "<a id=\"co_link_I2c86d170883611e19a0cc90d102a8215\" class=\"co_link co_drag ui-draggable\" href=\"http://www.westlaw.com/Link/Document/FullText?findType=L&amp;pubNum=1000546&amp;cite=42USCAS1395W-133&amp;originationContext=ebook&amp;RS=ebbp3.0&amp;vr=3.0#co_pp_8b3b0000958a4\">section 1395w-133(a)</a>";
        String expectedResult =
            "<a id=\"co_link_I2c86d170883611e19a0cc90d102a8215\" class=\"co_link co_drag ui-draggable\" href=\"er:#IC6A94E80FF6011DC95B0EEFA5102EA59/co_pp_8b3b0000958a4\">section 1395w-133(a)</a>";

        testHelper(inputXML, expectedResult);
    }
    @Test
    public void testGetLinkParameterwithSpace() throws Exception
    {
        String resourceUrl =
            "http://www.westlaw.com/Link/Document/FullText?findType=L&amp;pubNum=1000546 &cite=42USCAS1395W-133&originationContext=ebook&amp;RS=ebbp3.0&amp;vr=3.0#co_pp_8b3b0000958a";
        Map<String, String> urlValues = UrlParsingUtil.parseUrlContents(resourceUrl);
        String linkParameter = urlValues.get("reference");
        String normalizedCite = urlValues.get("cite");
        String expectedLinkParameter = "co_pp_8b3b0000958a";
        Assert.assertEquals(expectedLinkParameter, linkParameter);

        DocMetadata dm = new DocMetadata();
        dm.setDocFamilyUuid("IC6A94E80FF6011DC95B0EEFA5102EA59");
        dm.setDocUuid("NF8C65500AFF711D8803AE0632FEDDFBF");

        Map<String, DocMetadata> mp = new HashMap<String, DocMetadata>();
        mp.put(normalizedCite, dm);
        EasyMock.expect(mockDocumentMetadataAuthority.getDocMetadataKeyedByCite()).andReturn(mp);
        EasyMock.replay(mockDocumentMetadataAuthority);

        String inputXML =
            "<a id=\"co_link_I2c86d170883611e19a0cc90d102a8215\" class=\"co_link co_drag ui-draggable\" href=\"http://www.westlaw.com/Link/Document/FullText?findType=L&amp;pubNum=1000546%20&amp;cite=42USCAS1395W-133&amp;originationContext=ebook&amp;RS=ebbp3.0&amp;vr=3.0#co_pp_8b3b0000958a4\">section 1395w-133(a)</a>";
        String expectedResult =
            "<a id=\"co_link_I2c86d170883611e19a0cc90d102a8215\" class=\"co_link co_drag ui-draggable\" href=\"er:#IC6A94E80FF6011DC95B0EEFA5102EA59/co_pp_8b3b0000958a4\">section 1395w-133(a)</a>";

        testHelper(inputXML, expectedResult);
    }
    @Test
    public void testGetLinkParameterwithOnlySpace() throws Exception
    {
        String resourceUrl =
            "http://www.westlaw.com/Link/Document/FullText?findType=L&amp;pubNum=1000546 &cite=42USCAS1395W-133&originationContext=ebook&amp;RS=ebbp3.0&amp;vr=3.0#co_pp_8b3b0000958a";
        Map<String, String> urlValues = UrlParsingUtil.parseUrlContents(resourceUrl);
        String linkParameter = urlValues.get("reference");
        String normalizedCite = urlValues.get("cite");
        String expectedLinkParameter = "co_pp_8b3b0000958a";
        Assert.assertEquals(expectedLinkParameter, linkParameter);

        DocMetadata dm = new DocMetadata();
        dm.setDocFamilyUuid("IC6A94E80FF6011DC95B0EEFA5102EA59");
        dm.setDocUuid("NF8C65500AFF711D8803AE0632FEDDFBF");

        Map<String, DocMetadata> mp = new HashMap<String, DocMetadata>();
        mp.put(normalizedCite, dm);
        EasyMock.expect(mockDocumentMetadataAuthority.getDocMetadataKeyedByCite()).andReturn(mp);
        EasyMock.replay(mockDocumentMetadataAuthority);

        String inputXML =
            "<a id=\"co_link_I2c86d170883611e19a0cc90d102a8215\" class=\"co_link co_drag ui-draggable\" href=\"http://www.westlaw.com/Link/Document/FullText?findType=L&amp;pubNum=%20&amp;cite=42USCAS1395W-133&amp;originationContext=ebook&amp;RS=ebbp3.0&amp;vr=3.0#co_pp_8b3b0000958a4\">section 1395w-133(a)</a>";
        String expectedResult =
            "<a id=\"co_link_I2c86d170883611e19a0cc90d102a8215\" class=\"co_link co_drag ui-draggable\" href=\"er:#IC6A94E80FF6011DC95B0EEFA5102EA59/co_pp_8b3b0000958a4\">section 1395w-133(a)</a>";

        testHelper(inputXML, expectedResult);
    }
    @Test
    @Ignore
    public void testGetNormalizedCiteWithPaceMetadata() throws Exception
    {
       
        String resourceUrl =
            "https://www.westlaw.com/Link/Document/FullText?findType=L&amp;pubNum=126977&cite=CCPEMPs2%3A89&originationContext=ebook&RS=ebbp3.0&vr=3.0";
        Map<String, String> urlValues = UrlParsingUtil.parseUrlContents(resourceUrl);
        String normalizedCite = urlValues.get("cite");
       

        DocMetadata dm = new DocMetadata();
        dm.setDocFamilyUuid("I7dd370d22f6d11d997cad7305e16d23d");
        dm.setDocUuid("I410098c9b67411d9947c9ea867b7826a");
        dm=null;

        Map<String, DocMetadata> mp = new HashMap<String, DocMetadata>();
        mp.put(normalizedCite, dm);
        EasyMock.expect(mockDocumentMetadataAuthority.getDocMetadataKeyedByCite()).andReturn(mp);
        EasyMock.replay(mockDocumentMetadataAuthority);

        String inputXML =
            "<a id=\"co_link_I2c86d170883611e19a0cc90d102a8215\" class=\"co_link co_drag ui-draggable\" href=\"https://www.westlaw.com/Link/Document/FullText?findType=L&amp;pubNum=126977&amp;cite=CCPEMPs2%3A89&amp;originationContext=ebook&amp;RS=ebbp3.0&amp;vr=3.0#co_pp_8b3b0000958a4\">section 1395w-133(a)</a>";
        String expectedResult =
            "<a id=\"co_link_I2c86d170883611e19a0cc90d102a8215\" class=\"co_link co_drag ui-draggable\" href=\"er:#I7dd370d22f6d11d997cad7305e16d23d/co_pp_8b3b0000958a4\">section 1395w-133(a)</a>";

        testHelper(inputXML, expectedResult);
    }

    @Test
    public void testGetNormalizedCiteDocumentUuidFromResourceUrl()
        throws Exception
    {
        String resourceUrl =
            "https://1.next.westlaw.com/Link/Document/FullText?findType=l&pubNum=1077005&cite=UUID%28ID4D58042D3-43461C8C9EE-73AA2A319F3%29&originationContext=ebook";
        Map<String, String> urlValues = UrlParsingUtil.parseUrlContents(resourceUrl);

        String documentUuid = urlValues.get("documentUuid");
        String expectedNormalizedCiteUUID = "ID4D58042D3-43461C8C9EE-73AA2A319F3";
        Assert.assertTrue(expectedNormalizedCiteUUID.equals(documentUuid));
    }

    @Test
    public void testGetNormalizedCiteFromResourceUrl()
        throws Exception
    {
        String resourceUrl =
            "https://1.next.westlaw.com/Link/Document/FullText?findType=Y&pubNum=119616&cite=SECOPINION§39%3A7&originationContext=ebook";
        Map<String, String> urlValues = UrlParsingUtil.parseUrlContents(resourceUrl);
        String normalizedCite = urlValues.get("cite");
        String expectedNormalizedCite = "SECOPINIONS39:7";
       // System.out.println(normalizedCite);
        Assert.assertTrue(expectedNormalizedCite.equals(normalizedCite));

        resourceUrl = "https://1.next.westlaw.com/Link/Document/FullText?findType=L&pubNum=1000600&cite=USFRCPR20&originatingDoc=I86827039c15111ddb9c7909664ff7808&refType=LQ&amp;originationContext=ebook";

        urlValues = UrlParsingUtil.parseUrlContents(resourceUrl);
        normalizedCite = urlValues.get("cite");
       // System.out.println(normalizedCite);
        expectedNormalizedCite = "USFRCPR20";
        Assert.assertTrue(expectedNormalizedCite.equals(normalizedCite));
        
        resourceUrl = "https://www.westlaw.com/Link/Document/FullText?findType=Y&pubNum=126977&cite=CCPEMPs2%3A89&originationContext=ebook&RS=ebbp3.0&vr=3.0";

        urlValues = UrlParsingUtil.parseUrlContents(resourceUrl);
        normalizedCite = urlValues.get("cite");
        System.out.println(normalizedCite);
        expectedNormalizedCite = "CCPEMPS2:89";
        Assert.assertTrue(expectedNormalizedCite.equals(normalizedCite));
    }
 
    @Test
    public void testGetSerialNumberFromResourceUrl() throws Exception
    {
        String resourceUrl =
            "https://a.next.westlaw.com/Document/FullText?findType=Y&serNum=123456&transitionType=Default&contextData=(sc.Default)";
        Map<String, String> urlValues = UrlParsingUtil.parseUrlContents(resourceUrl);
        String serialNumber = urlValues.get("serNum");
        String expectedSerialNumber = "123456";
        Assert.assertTrue(expectedSerialNumber.equals(serialNumber));
    }

    @Test
    public void testGetTOCGuid() throws Exception
    {
        String resourceUrl =
            "http://www.westlaw.com/Link/Document/FullText?findType=L&pubNum=1000546&cite=42USCAS1395W-133&originationContext=ebook&RS=ebbp3.0&amp;vr=3.0#co_pp_8b3b0000958a";
        Map<String, String> urlValues = UrlParsingUtil.parseUrlContents(resourceUrl);
        String linkParameter = urlValues.get("reference");
        String normalizedCite = urlValues.get("cite");
        String expectedNormalizedCite = "42USCAS1395W-133";
        Assert.assertEquals(expectedNormalizedCite, normalizedCite);

        String expectedLinkParameter = "co_pp_8b3b0000958a";
        Assert.assertEquals(expectedLinkParameter, linkParameter);

        DocMetadata dm = new DocMetadata();
        dm.setDocFamilyUuid("IC6A94E80FF6011DC95B0EEFA5102EA59");
        dm.setDocUuid("NF8C65500AFF711D8803AE0632FEDDFBF");

        Map<String, DocMetadata> mp = new HashMap<String, DocMetadata>();
        mp.put(normalizedCite, dm);
        EasyMock.expect(mockDocumentMetadataAuthority.getDocMetadataKeyedByCite()).andReturn(mp);
        EasyMock.replay(mockDocumentMetadataAuthority);

        String inputXML =
            "<a id=\"co_link_I2c86d170883611e19a0cc90d102a8215\" class=\"co_link co_drag ui-draggable\" href=\"http://www.westlaw.com/Link/Document/FullText?findType=L&amp;pubNum=1000546&amp;cite=42USCAS1395W-133&amp;originationContext=ebook&amp;RS=ebbp3.0&amp;vr=3.0\">section 1395w-133(a)</a>";
        String expectedResult =
            "<a id=\"co_link_I2c86d170883611e19a0cc90d102a8215\" class=\"co_link co_drag ui-draggable\" href=\"er:#IC6A94E80FF6011DC95B0EEFA5102EA59/N129FCFD29AA24CD5ABBAA83B0A8A2D7B275\">section 1395w-133(a)</a>";

        testHelper(inputXML, expectedResult);
    }

    /**
     * Helper method that sets up the repeating pieces of each test and modifies the
     * ImageService values that are returned along with the input and output.
     *
     * @param inputXML input string for the test.
     * @param expectedResult the expected output for the specified input string.
     */
    public void testHelper(String inputXML, String expectedResult)
        throws SAXException
    {
        ByteArrayInputStream input = null;
        ByteArrayOutputStream output = null;

        try
        {
            input = new ByteArrayInputStream(inputXML.getBytes());
            output = new ByteArrayOutputStream();

            serializer.setOutputStream(output);

            internalLinksFilter.setContentHandler(serializer.asContentHandler());
            internalLinksFilter.parse(new InputSource(input));

            String result = output.toString();

            assertEquals(expectedResult, result);
        }
        catch (SAXException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            fail("Encountered exception during test: " + e.getMessage());
        }
        finally
        {
            try
            {
                if (input != null)
                {
                    input.close();
                }

                if (output != null)
                {
                    output.close();
                }
            }
            catch (Exception e)
            {
                fail("Could clean up resources: " + e.getMessage());
            }
        }
    }

    protected void writeDocumentLinkFile(File internalLinkResolverTestFile)
    {
        BufferedWriter writer = null;

        try
        {
            writer = new BufferedWriter(new FileWriter(internalLinkResolverTestFile));

            writer.write("NF8C65500AFF711D8803AE0632FEDDFBF,N129FCFD29AA24CD5ABBAA83B0A8A2D7B275|");
            writer.newLine();
            writer.write("NDF4CB9C0AFF711D8803AE0632FEDDFBF,N8E37708B96244CD1B394155616B3C66F190|");

            writer.newLine();

            writer.flush();
        }
        catch (IOException e)
        {
            String errMessage =
                "Encountered an IO Exception while processing: "
                + internalLinkResolverTestFile.getAbsolutePath();
            LOG.error(errMessage);
        }
        finally
        {
            try
            {
                if (writer != null)
                {
                    writer.close();
                }
            }
            catch (IOException e)
            {
                LOG.error("Unable to close anchor target list file.", e);
            }
        }

        LOG.debug("size of file : " + internalLinkResolverTestFile.length());
    }
}
