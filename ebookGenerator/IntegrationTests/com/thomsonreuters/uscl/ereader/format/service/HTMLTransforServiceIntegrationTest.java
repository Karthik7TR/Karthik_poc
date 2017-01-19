/*
* Copyright 2015: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
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
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.springframework.util.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.thomsonreuters.uscl.ereader.core.book.domain.TableViewer;
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
    String novusXmlFileName1;
    String novusMetadataFilename;
    long jobId;
    FileHandlingHelper fileHandlingHelper;
    FileExtensionFilter fileExtFilter;
    HTMLTransformerServiceImpl htmlTransforService;
    DocMetadata mockDocMetadata;
    DocMetadataService mockDocMetadataService;
    DocumentMetadataAuthority mockDocumentMetadataAuthority;
    HashMap<String, HashSet<String>>  targetAnchors;

    private final String testExtension = ".html";
    File docsGuidFile;
    @Rule
    public TemporaryFolder tempDirectory = new TemporaryFolder();
    String version = "1";

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
        targetAnchors = new  HashMap<String, HashSet<String>>();
        novusXmlFilename = "NADB029C0880F11D881E9FEF4A4D44D69.html";
        novusXmlFileName1 = "Id8ec0a72cfe111da9bb4a39a5015044e.html";
        novusMetadataFilename = "27-w_codesstacanvdu-NADB029C0880F11D881E9FEF4A4D44D69.xml";
        jobId = 12345L;
        String docGuid = "sample-docs-guid.txt";
        
        docsGuidFile = new File(
                HTMLTransforServiceIntegrationTest.class.getResource(docGuid).getFile());
        
    }

    @Ignore
    @Test
    public void transformWithOutTableView() throws EBookFormatException, IOException
    {
        int count = getTableViewInfo(false);
        
        Assert.isTrue(count > 0, "Unable to find  table in document");
    }

    @Ignore
    @Test
    public void transformWithTableView()
        throws EBookFormatException, FileNotFoundException, IOException
    {
        int count = getTableViewInfo(true);

        Assert.isTrue(count > 0, "Unable to transform as Table View");
    }
    
    @Ignore
    @Test
    public void testInternalLinks()
        throws EBookFormatException, FileNotFoundException, IOException
    {
        int count = getInternalLinkInfo();
        
        Assert.isTrue(count == 0, "Unable to transform as Table View");
    }
    
       
    
    @Test
    public void testInternalDocumentLinks()
        throws EBookFormatException, FileNotFoundException, IOException
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
    	
    	mockDocMetadataService = EasyMock.createMock(DocMetadataService.class);
        EasyMock.expect(
            mockDocMetadataService.findDocMetadataByPrimaryKey(
                "uscl/an/IMPH", new Long(12345), preRenderedInput)).andReturn(mockDocMetadata);

        EasyMock.replay(mockDocMetadataService);
        
    	Set<DocMetadata> documentMetadataSet = new HashSet<DocMetadata>();
    	List<DocMetadata> docMetaList = new ArrayList<DocMetadata>();
    	DocMetadata doc = new DocMetadata();
    	doc.setDocUuid("ID4D58042D3-43461C8C9EE-73AA2A319F3");
    	doc.setDocFamilyUuid("123456789");
    	doc.setJobInstanceId(12345l);
    	doc.setTitleId(titleId);
    	docMetaList.add(doc);
    	    	
    	DocMetadata doc1 = new DocMetadata();
    	doc1.setNormalizedFirstlineCite("lk(CAIND) lk(CAINR) lk(CASTERR)");
    	doc1.setDocFamilyUuid("x123456789");
    	doc1.setDocUuid("222222");
    	doc1.setJobInstanceId(12345l);
    	doc1.setTitleId(titleId);
    	docMetaList.add(doc1);
    	
    	
    	DocMetadata doc2 = new DocMetadata();
    	doc2.setDocUuid("I649D987021-CD4A9CA1EF7-B06AECEA59A");
    	doc2.setDocFamilyUuid("x123456789");
    	doc2.setJobInstanceId(12345l);
    	doc2.setTitleId(titleId);
    	docMetaList.add(doc2);
    	
    	DocMetadata doc3 = new DocMetadata();
    	doc3.setDocUuid("I70A4B3CDA7-51471A8B6A0-43256BAD23B");
    	doc3.setDocFamilyUuid("x123456789");
    	doc3.setJobInstanceId(12345l);
    	doc3.setTitleId(titleId);
    	docMetaList.add(doc3);
    	
    	DocMetadata doc4 = new DocMetadata();
    	doc4.setNormalizedFirstlineCite("CAINS12975.8");
    	doc4.setDocFamilyUuid("x123456789");
    	doc4.setDocUuid("555555");
    	doc4.setJobInstanceId(12345l);
    	doc4.setTitleId(titleId);
    	docMetaList.add(doc4);
    	
    	DocMetadata doc5 = new DocMetadata();
    	doc5.setNormalizedFirstlineCite("CAINS12975.1");
    	doc5.setDocFamilyUuid("x123456789");
    	doc5.setDocUuid("666666");
    	doc5.setJobInstanceId(12345l);
    	doc5.setTitleId(titleId);
    	docMetaList.add(doc5);
    	
    	DocMetadata doc6 = new DocMetadata();
    	doc6.setNormalizedFirstlineCite("CAINS1874.8");
    	doc6.setDocFamilyUuid("x123456789");
    	doc6.setDocUuid("777777");
    	doc6.setJobInstanceId(12345l);
    	doc6.setTitleId(titleId);
    	docMetaList.add(doc6);
    	
    	//IF35149D0465911E1AD76A188D0BE1F6D
    	
    	DocMetadata doc7 = new DocMetadata();
    	doc7.setDocFamilyUuid("123x123456789");
    	doc7.setDocUuid("IF35149D0465911E1AD76A188D0BE1F6D");
    	doc7.setJobInstanceId(12345l);
    	doc7.setTitleId(titleId);
    	docMetaList.add(doc7);
    	
    	DocMetadata doc8 = new DocMetadata();
    	doc8.setDocFamilyUuid("123x123456789");
    	doc8.setDocUuid("NADB029C0880F11D881E9FEF4A4D44D69");
    	doc8.setJobInstanceId(12345l);
    	doc8.setTitleId(titleId);
    	docMetaList.add(doc8);
    	
    	documentMetadataSet.addAll(docMetaList);
    	
    	    	
    	mockDocumentMetadataAuthority = new DocumentMetadataAuthority(documentMetadataSet);
    		
        htmlTransforService.setdocMetadataService(mockDocMetadataService);

        Set<String> staticImages = new HashSet<String>();
        File transformedDirectory = tempDirectory.newFolder("transformed");
        File novusXml =
            new File(
                HTMLTransforServiceIntegrationTest.class.getResource(novusXmlFilename).getFile());
        htmlTransforService.transformHTMLFile(
            novusXml, transformedDirectory, staticImages, new ArrayList<TableViewer>(), new ArrayList<TableViewer>(), 
            titleId, jobId, mockDocumentMetadataAuthority, targetAnchors, docsGuidFile, docsGuidFile, false, false, false, version);

        String renderedOutput =
            IOUtils.toString(
                new FileInputStream(
                    new File(transformedDirectory, preRenderedInput + ".postTransform")));
        return buildTableAnchorsFromOutput(renderedOutput, "//table[@class='tr_table']");
    }
    
    
    /**
     * Returns count of internal Link url from renderedOutput.
     * @return count of internal Link url from renderedOutput.
     * @throws EBookFormatException
     * @throws FileNotFoundException
     * @throws IOException
     */
    private int getInternalLinkInfo()
        throws EBookFormatException, FileNotFoundException, IOException
    {
    	mockDocMetadataService = EasyMock.createMock(DocMetadataService.class);
        EasyMock.expect(
            mockDocMetadataService.findDocMetadataByPrimaryKey(
                "uscl/an/IMPH", new Long(12345), preRenderedInput)).andReturn(mockDocMetadata);

        EasyMock.replay(mockDocMetadataService);
        
    	Set<DocMetadata> documentMetadataSet = new HashSet<DocMetadata>();
    	List<DocMetadata> docMetaList = new ArrayList<DocMetadata>();
    	DocMetadata doc = new DocMetadata();
    	doc.setDocUuid("ID4D58042D3-43461C8C9EE-73AA2A319F3");
    	doc.setDocFamilyUuid("123456789");
    	doc.setJobInstanceId(12345l);
    	doc.setTitleId(titleId);
    	doc.setSpitBookTitle("splitBookTitle");
    	docMetaList.add(doc);
    	    	
    	DocMetadata doc1 = new DocMetadata();
    	doc1.setNormalizedFirstlineCite("lk(CAIND) lk(CAINR) lk(CASTERR)");
    	doc1.setDocFamilyUuid("x123456789");
    	doc1.setDocUuid("222222");
    	doc1.setJobInstanceId(12345l);
    	doc1.setTitleId(titleId);
    	doc1.setSpitBookTitle("splitBookTitle1");
    	docMetaList.add(doc1);
    	
    	
    	DocMetadata doc2 = new DocMetadata();
    	doc2.setDocUuid("I649D987021-CD4A9CA1EF7-B06AECEA59A");
    	doc2.setDocFamilyUuid("x123456789");
    	doc2.setJobInstanceId(12345l);
    	doc2.setTitleId(titleId);
    	doc2.setSpitBookTitle("splitBookTitle2");
    	docMetaList.add(doc2);
    	
    	DocMetadata doc3 = new DocMetadata();
    	doc3.setDocUuid("I70A4B3CDA7-51471A8B6A0-43256BAD23B");
    	doc3.setDocFamilyUuid("x123456789");
    	doc3.setJobInstanceId(12345l);
    	doc3.setTitleId(titleId);
    	doc3.setSpitBookTitle("splitBookTitle3");
    	docMetaList.add(doc3);
    	
    	DocMetadata doc4 = new DocMetadata();
    	doc4.setNormalizedFirstlineCite("CAINS12975.8");
    	doc4.setDocFamilyUuid("x123456789");
    	doc4.setDocUuid("555555");
    	doc4.setJobInstanceId(12345l);
    	doc4.setTitleId(titleId);
    	doc4.setSpitBookTitle("splitBookTitle4");
    	docMetaList.add(doc4);
    	
    	DocMetadata doc5 = new DocMetadata();
    	doc5.setNormalizedFirstlineCite("CAINS12975.1");
    	doc5.setDocFamilyUuid("x123456789");
    	doc5.setDocUuid("666666");
    	doc5.setJobInstanceId(12345l);
    	doc5.setTitleId(titleId);
    	doc5.setSpitBookTitle("splitBookTitle5");
    	docMetaList.add(doc5);
    	
    	DocMetadata doc6 = new DocMetadata();
    	doc6.setNormalizedFirstlineCite("CAINS1874.8");
    	doc6.setDocFamilyUuid("x123456789");
    	doc6.setDocUuid("777777");
    	doc6.setJobInstanceId(12345l);
    	doc6.setTitleId(titleId);
    	doc6.setSpitBookTitle("splitBookTitle6");
    	docMetaList.add(doc6);
    	
    	//IF35149D0465911E1AD76A188D0BE1F6D
    	
    	DocMetadata doc7 = new DocMetadata();
    	doc7.setDocFamilyUuid("123x123456789");
    	doc7.setDocUuid("IF35149D0465911E1AD76A188D0BE1F6D");
    	doc7.setJobInstanceId(12345l);
    	doc7.setTitleId(titleId);
    	doc7.setSpitBookTitle("splitBookTitle7");
    	docMetaList.add(doc7);
    	
    	DocMetadata doc8 = new DocMetadata();
    	doc8.setDocFamilyUuid("123x123456789");
    	doc8.setDocUuid("NADB029C0880F11D881E9FEF4A4D44D69");
    	doc8.setJobInstanceId(12345l);
    	doc8.setTitleId(titleId);
    	doc8.setSpitBookTitle("splitBookTitle8");
    	docMetaList.add(doc8);
    	
    	
    	documentMetadataSet.addAll(docMetaList);
    	
    	    	
    	mockDocumentMetadataAuthority = new DocumentMetadataAuthority(documentMetadataSet);
    		
        htmlTransforService.setdocMetadataService(mockDocMetadataService);

        Set<String> staticImages = new HashSet<String>();
        File transformedDirectory = tempDirectory.newFolder("transformed");

        File novusXml =
            new File(
                HTMLTransforServiceIntegrationTest.class.getResource(novusXmlFilename).getFile());
        htmlTransforService.transformHTMLFile(
            novusXml, transformedDirectory, staticImages, new ArrayList<TableViewer>(), new ArrayList<TableViewer>(), 
            titleId, jobId,mockDocumentMetadataAuthority, targetAnchors, docsGuidFile, docsGuidFile, false, false, false, version);

        String renderedOutput =
            IOUtils.toString(
                new FileInputStream(
                    new File(transformedDirectory, preRenderedInput + ".postTransform")));
        return buildTableAnchorsFromOutput(renderedOutput, "//a[@href='er:#x123456789']");
    }
    
    
      
    
    
    /**
     * Returns count of internal Link url from renderedOutput.
     * @return count of internal Link url from renderedOutput.
     * @throws EBookFormatException
     * @throws FileNotFoundException
     * @throws IOException
     */
    private int getInternalLinkInfo1()
        throws EBookFormatException, FileNotFoundException, IOException
    {
    	mockDocMetadataService = EasyMock.createMock(DocMetadataService.class);
        EasyMock.expect(
            mockDocMetadataService.findDocMetadataByPrimaryKey(
                "uscl/an/IMPH", new Long(12345), "Id8ec0a72cfe111da9bb4a39a5015044e")).andReturn(mockDocMetadata);

        EasyMock.replay(mockDocMetadataService);
    	Set<DocMetadata> documentMetadataSet = new HashSet<DocMetadata>();
    	List<DocMetadata> docMetaList = new ArrayList<DocMetadata>();
    	DocMetadata doc = new DocMetadata();
    	doc.setNormalizedFirstlineCite("MSEVDMYCORCH1");
    	doc.setDocUuid("999999999999");
    	doc.setDocFamilyUuid("9999XXXX123456789");
    	doc.setJobInstanceId(12345l);
    	doc.setTitleId(titleId);
    	docMetaList.add(doc);
    	    	
    	DocMetadata doc1 = new DocMetadata();
    	doc1.setNormalizedFirstlineCite("MODSCIEVIDs1:12");
    	doc1.setDocFamilyUuid("x123456789");
    	doc1.setDocUuid("222222");
    	doc1.setJobInstanceId(12345l);
    	doc1.setTitleId(titleId);
    	docMetaList.add(doc1);
    	
    	
    	DocMetadata doc2 = new DocMetadata();
    	doc2.setDocUuid("Id8ec0a75cfe111da9bb4a39a5015044e");
    	doc2.setDocFamilyUuid("x123456789");
    	//doc2.setDocUuid("333333");
    	doc2.setJobInstanceId(12345l);
    	doc2.setTitleId(titleId);
    	docMetaList.add(doc2);
    	
    	DocMetadata doc3 = new DocMetadata();
    	doc3.setDocUuid("I70A4B3CDA7-51471A8B6A0-43256BAD23B");
    	doc3.setDocFamilyUuid("x123456789");
    	doc3.setNormalizedFirstlineCite("MODSCIEVIDs1:11");
    	doc3.setJobInstanceId(12345l);
    	doc3.setTitleId(titleId);
    	docMetaList.add(doc3);
    	
    	DocMetadata doc31 = new DocMetadata();
    	doc31.setDocUuid("I127ff9304e8111e181c20000837bc6dd");
    	doc31.setDocFamilyUuid("12345xxx6789");
    	doc31.setJobInstanceId(12345l);
    	doc31.setTitleId(titleId);
    	docMetaList.add(doc31);
    	
    	DocMetadata doc41 = new DocMetadata();
    	doc41.setDocUuid("I127fab104e8111e181c20000837bc6dd");
    	doc41.setDocFamilyUuid("12345xxx6789");
    	doc41.setJobInstanceId(12345l);
    	doc41.setTitleId(titleId);
    	docMetaList.add(doc41);
    	
    	DocMetadata doc51 = new DocMetadata();
    	doc51.setDocUuid("I1280bc804e8111e181c20000837bc6dd");
    	doc51.setDocFamilyUuid("1234yxxx6789");
    	doc51.setJobInstanceId(12345l);
    	doc51.setTitleId(titleId);
    	docMetaList.add(doc51);
    	
    	DocMetadata doc15 = new DocMetadata();
    	doc15.setDocUuid("I128417e04e8111e181c20000837bc6dd");
    	doc15.setDocFamilyUuid("1234yxxx6789");
    	doc15.setJobInstanceId(12345l);
    	doc15.setTitleId(titleId);
    	docMetaList.add(doc15);
    	
    	DocMetadata doc16 = new DocMetadata();
    	doc16.setDocUuid("I12848d104e8111e181c20000837bc6dd");
    	doc16.setDocFamilyUuid("1234yxxx6789");
    	doc16.setJobInstanceId(12345l);
    	doc16.setTitleId(titleId);
    	docMetaList.add(doc16);
    	
    	DocMetadata doc61 = new DocMetadata();
    	doc61.setDocUuid("I128158c04e8111e181c20000837bc6dd");
    	doc61.setDocFamilyUuid("1234yxxx6789");
    	doc61.setJobInstanceId(12345l);
    	doc61.setTitleId(titleId);
    	docMetaList.add(doc61);
    	
    	DocMetadata doc71 = new DocMetadata();
    	doc71.setDocUuid("I1281f5004e8111e181c20000837bc6dd");
    	doc71.setDocFamilyUuid("1234yxxx6789");
    	doc71.setJobInstanceId(12345l);
    	doc71.setTitleId(titleId);
    	docMetaList.add(doc71);
    	
    	DocMetadata doc81 = new DocMetadata();
    	doc81.setDocUuid("I1281f5004e8111e181c20000837bc6dd");
    	doc81.setDocFamilyUuid("1234yyxx6789");
    	doc81.setJobInstanceId(12345l);
    	doc81.setTitleId(titleId);
    	docMetaList.add(doc81);
    	
    	DocMetadata doc91 = new DocMetadata();
    	doc91.setDocUuid("I1282df604e8111e181c20000837bc6dd");
    	doc91.setDocFamilyUuid("1234yyxx6789");
    	doc91.setJobInstanceId(12345l);
    	doc91.setTitleId(titleId);
    	docMetaList.add(doc91);
    	
    	DocMetadata doc92 = new DocMetadata();
    	doc92.setDocUuid("I128354904e8111e181c20000837bc6dd");
    	doc92.setDocFamilyUuid("1234yyxx6789");
    	doc92.setJobInstanceId(12345l);
    	doc92.setTitleId(titleId);
    	docMetaList.add(doc92);
    	
    	
    	
    	DocMetadata doc4 = new DocMetadata();
    	doc4.setNormalizedFirstlineCite("MODSCIEVIDs40:1");
    	doc4.setDocFamilyUuid("x123456789");
    	doc4.setDocUuid("555555");
    	doc4.setJobInstanceId(12345l);
    	doc4.setTitleId(titleId);
    	docMetaList.add(doc4);
    	
    	DocMetadata doc5 = new DocMetadata();
    	doc5.setNormalizedFirstlineCite("27JFORENSICSCI684");
    	doc5.setDocFamilyUuid("x123456789");
    	doc5.setDocUuid("666666");
    	doc5.setJobInstanceId(12345l);
    	doc5.setTitleId(titleId);
    	docMetaList.add(doc5);
    	
    	DocMetadata doc6 = new DocMetadata();
    	doc6.setNormalizedFirstlineCite("MODSCIEVIDs16");
    	doc6.setDocFamilyUuid("x123456789");
    	doc6.setDocUuid("8777777");
    	doc6.setJobInstanceId(12345l);
    	doc6.setTitleId(titleId);
    	docMetaList.add(doc6);
    	
    	
    	DocMetadata doc9 = new DocMetadata();
    	Long ss = new Long("0105114615");
    	doc9.setSerialNumber(ss);
    	doc9.setDocFamilyUuid("8816x123456789");
    	doc9.setDocUuid("1689898989");
    	doc9.setJobInstanceId(12345l);
    	doc9.setTitleId(titleId);
    	docMetaList.add(doc9);
    	
    	DocMetadata doc8 = new DocMetadata();
    	ss = new Long("0292127061");
    	doc8.setSerialNumber(ss);
    	doc8.setDocFamilyUuid("8916x123456789");
    	doc8.setDocUuid("1789898989");
    	doc8.setJobInstanceId(12345l);
    	doc8.setTitleId(titleId);
    	docMetaList.add(doc8);
    	
    	DocMetadata doc11 = new DocMetadata();
    	doc11.setSerialNumber(new Long(1977110977));
    	doc11.setDocFamilyUuid("516x123456789");
    	doc11.setDocUuid("669898989");
    	doc11.setJobInstanceId(12345l);
    	doc11.setTitleId(titleId);
    	docMetaList.add(doc11);
    	
    	//1924122438
    	
    	DocMetadata doc12 = new DocMetadata();
    	doc12.setSerialNumber(new Long(1924122438));
    	doc12.setDocFamilyUuid("777x123456789");
    	doc12.setDocUuid("873429898989");
    	doc12.setJobInstanceId(12345l);
    	doc12.setTitleId(titleId);
    	docMetaList.add(doc12);
    	
    	DocMetadata doc13 = new DocMetadata();
    	doc13.setSerialNumber(new Long(1924122438));
    	doc13.setDocFamilyUuid("777x123456789");
    	doc13.setDocUuid("NADB029C0880F11D881E9FEF4A4D44D69");
    	doc13.setJobInstanceId(12345l);
    	doc13.setTitleId(titleId);
    	docMetaList.add(doc13);
    	
    	    	
    	documentMetadataSet.addAll(docMetaList);
    	
    	mockDocumentMetadataAuthority = new DocumentMetadataAuthority(documentMetadataSet);
    		
        htmlTransforService.setdocMetadataService(mockDocMetadataService);

        Set<String> staticImages = new HashSet<String>();
        File transformedDirectory = tempDirectory.newFolder("transformed");

        File novusXml =
            new File(
                HTMLTransforServiceIntegrationTest.class.getResource(novusXmlFileName1).getFile());
        htmlTransforService.transformHTMLFile(
            novusXml, transformedDirectory, staticImages, new ArrayList<TableViewer>(), new ArrayList<TableViewer>(), 
            titleId, jobId,mockDocumentMetadataAuthority, targetAnchors, docsGuidFile, docsGuidFile, false, false, false, version);
        String renderedOutput =
            IOUtils.toString(
                new FileInputStream(
                    new File(transformedDirectory, "Id8ec0a72cfe111da9bb4a39a5015044e" + ".postTransform")));
       
        return buildTableAnchorsFromOutput(renderedOutput, "//a[@href='er:#777x123456789']");
    }
}
