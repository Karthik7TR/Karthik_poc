package com.thomsonreuters.uscl.ereader.format.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.SplitDocument;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionServiceImpl;
import com.thomsonreuters.uscl.ereader.notification.SendingEmailNotificationTest;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public final class AutoSplitGuidsServiceTest
{
    private AutoSplitGuidsServiceImpl autoSplitGuidsService;
    private BookDefinition bookDefinition;
    private Long jobInstanceId;
    private BookDefinitionServiceImpl service;
    private Long bookId = Long.valueOf(1);

    @Before
    public void setUp()
    {
        autoSplitGuidsService = new AutoSplitGuidsServiceImpl();
        bookDefinition = new BookDefinition();
        bookDefinition.setEbookDefinitionId(bookId);
        jobInstanceId = Long.valueOf(1);
        service = EasyMock.createMock(BookDefinitionServiceImpl.class);
        autoSplitGuidsService.setBookDefinitionService(service);
    }

    @After
    public void tearDown()
    {
        //Intentional left blank
    }

    @Test
    public void testSplitPartSize()
    {
        final int size = autoSplitGuidsService.getSizeforEachPart(5000, 5409);
        Assert.assertEquals(2704, size);
    }

    @Test
    public void testSplitPartPersistedDocs()
    {
        final String xmlTestStr = "<EBook>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_2</Guid>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_3</Guid><DocumentGuid>DOC_GUID2</DocumentGuid></EBookToc>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_4</Guid><DocumentGuid>DOC_GUID3</DocumentGuid></EBookToc>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_5</Guid><DocumentGuid>DOC_GUID4</DocumentGuid></EBookToc></EBookToc>"
            + "</EBook>";

        final List<String> expectTedGuidList = new ArrayList<>();
        expectTedGuidList.add("TABLEOFCONTENTS33CHARACTERSLONG_5");

        final ByteArrayInputStream input = new ByteArrayInputStream(xmlTestStr.getBytes());
        final Map<String, String> splitGuidTextMap = new HashMap<>();

        final List<SplitDocument> persistedSplitDocuments = new ArrayList<>();
        final SplitDocument splitDocument = new SplitDocument();
        splitDocument.setBookDefinition(bookDefinition);
        splitDocument.setNote("note");
        splitDocument.setTocGuid("TABLEOFCONTENTS33CHARACTERSLONG_5");
        persistedSplitDocuments.add(splitDocument);

        EasyMock.expect(service.findSplitDocuments(bookDefinition.getEbookDefinitionId()))
            .andReturn(persistedSplitDocuments);
        EasyMock.replay(service);

        final List<String> splitGuidList =
            autoSplitGuidsService.getAutoSplitNodes(input, bookDefinition, Integer.valueOf(5), jobInstanceId, false);
        EasyMock.verify(service);
        Assert.assertEquals(expectTedGuidList.size(), splitGuidList.size());
    }

    @Test
    public void testSplitPartNoList()
    {
        final String xmlTestStr = "<EBook>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_2</Guid>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_3</Guid><DocumentGuid>DOC_GUID2</DocumentGuid></EBookToc>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_4</Guid><DocumentGuid>DOC_GUID3</DocumentGuid></EBookToc>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_5</Guid><DocumentGuid>DOC_GUID4</DocumentGuid></EBookToc></EBookToc>"
            + "</EBook>";

        final ByteArrayInputStream input = new ByteArrayInputStream(xmlTestStr.getBytes());
        final Map<String, String> splitGuidTextMap = new HashMap<>();

        EasyMock.expect(service.findSplitDocuments(bookDefinition.getEbookDefinitionId())).andReturn(null);
        EasyMock.replay(service);

        final DocumentTypeCode dc = new DocumentTypeCode();
        dc.setThresholdValue(100);
        dc.setThresholdPercent(10);
        bookDefinition.setDocumentTypeCodes(dc);

        final List<String> splitGuidList =
            autoSplitGuidsService.getAutoSplitNodes(input, bookDefinition, Integer.valueOf(5), jobInstanceId, true);
        EasyMock.verify(service);
        Assert.assertEquals(0, splitGuidList.size());
    }

    @Test
    public void testSplitPartList()
    {
        final String xmlTestStr = "<EBook>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_2</Guid>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_3</Guid><DocumentGuid>DOC_GUID2</DocumentGuid></EBookToc>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_4</Guid><DocumentGuid>DOC_GUID3</DocumentGuid></EBookToc>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_5</Guid><DocumentGuid>DOC_GUID4</DocumentGuid></EBookToc></EBookToc>"
            + "</EBook>";

        final ByteArrayInputStream input = new ByteArrayInputStream(xmlTestStr.getBytes());
        final Map<String, String> splitGuidTextMap = new HashMap<>();

        EasyMock.expect(service.findSplitDocuments(bookDefinition.getEbookDefinitionId())).andReturn(null);
        EasyMock.replay(service);

        final DocumentTypeCode dc = new DocumentTypeCode();
        dc.setThresholdValue(4);
        dc.setThresholdPercent(10);
        bookDefinition.setDocumentTypeCodes(dc);

        final List<String> splitGuidList =
            autoSplitGuidsService.getAutoSplitNodes(input, bookDefinition, Integer.valueOf(5), jobInstanceId, true);
        EasyMock.verify(service);

        final List<String> expectTedGuidList = new ArrayList<>();
        expectTedGuidList.add("TABLEOFCONTENTS33CHARACTERSLONG_5");

        Assert.assertEquals(expectTedGuidList.size(), splitGuidList.size());
        Assert.assertEquals(expectTedGuidList.get(0), splitGuidList.get(0));
    }

    @Test
    public void testSplitPartListwithFile() throws Exception
    {
        final InputStream tocXml;
        final File tocFile;
        final URL url = SendingEmailNotificationTest.class.getResource("toc.xml");
        tocFile = new File(url.toURI());
        tocXml = new FileInputStream(tocFile);

        EasyMock.expect(service.findSplitDocuments(bookDefinition.getEbookDefinitionId())).andReturn(null);
        EasyMock.replay(service);

        final DocumentTypeCode dc = new DocumentTypeCode();
        dc.setThresholdValue(100);
        dc.setThresholdPercent(10);
        bookDefinition.setDocumentTypeCodes(dc);

        final List<String> splitGuidList =
            autoSplitGuidsService.getAutoSplitNodes(tocXml, bookDefinition, Integer.valueOf(18262), jobInstanceId, true);
        EasyMock.verify(service);

        final List<String> expectTedGuidList = new ArrayList<>();
        expectTedGuidList.add("N0EDBB190E9CC11DAA52BDDAF0B15BA68");

        Map<String, String> splitGuidTextMap = new HashMap<>();
        splitGuidTextMap = autoSplitGuidsService.getSplitGuidTextMap();
        System.out.println(splitGuidTextMap.size());
        for (final Map.Entry<String, String> entry : splitGuidTextMap.entrySet())
        {
            final String uuid = entry.getKey();
            final String name = entry.getValue();
            System.out.println(uuid + "  :  " + name + "\n");
        }
        Assert.assertEquals(expectTedGuidList.size(), splitGuidList.size());
        Assert.assertEquals(expectTedGuidList.get(0), splitGuidList.get(0));
    }
}
