package com.thomsonreuters.uscl.ereader.format.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.format.step.DocumentInfo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public final class SplitBookTocParseServiceTest {
    private SplitBookTocParseServiceImpl splitBookTocParseService;

    private InputStream tocXml;
    private OutputStream splitTocXml;
    private List<String> splitTocGuidList;
    private String title = "title";
    private String splitTitleId = "splitTitle";

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Before
    public void setUp() {
        splitBookTocParseService = new SplitBookTocParseServiceImpl();

        splitTocGuidList = new ArrayList<>();
        final String guid1 = "TABLEOFCONTENTS33CHARACTERSLONG_2";
        splitTocGuidList.add(guid1);

        tocXml = new ByteArrayInputStream(
            "<EBook><EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc><EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_2</Guid><DocumentGuid>DOC_GUID2</DocumentGuid></EBookToc></EBook>"
                .getBytes());
        splitTocXml = new ByteArrayOutputStream();
    }

    @Test
    public void testSplitBookToc() {
        Map<String, DocumentInfo> documentInfoMap = new HashMap<>();

        documentInfoMap =
            splitBookTocParseService.generateSplitBookToc(tocXml, splitTocXml, splitTocGuidList, splitTitleId);

        /*System.out.println("-----taskMap---------");
        for (Map.Entry<String, DocumentInfo> entry : documentInfoMap.entrySet()) {
        	System.out.println(entry.getKey() + "/" + entry.getValue().toString());
        }*/

        final DocumentInfo expectedDocInfo1 = new DocumentInfo();
        expectedDocInfo1.setSplitTitleId("splitTitle");

        final DocumentInfo expectedDocInfo2 = new DocumentInfo();
        expectedDocInfo2.setSplitTitleId("splitTitle_pt2");

        final DocumentInfo docInfo1 = documentInfoMap.get("DOC_GUID1");
        final DocumentInfo docInfo2 = documentInfoMap.get("DOC_GUID2");
        Assert.assertEquals(expectedDocInfo1.toString(), docInfo1.toString());
        Assert.assertEquals(expectedDocInfo2.toString(), docInfo2.toString());
    }

    @Test
    public void testSplitTocDuplicateDoc() {
        Map<String, DocumentInfo> documentInfoMap = new HashMap<>();

        final String xmlTestStr = "<EBook>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_2</Guid><DocumentGuid>DOC_GUID2</DocumentGuid></EBookToc>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_3</Guid><DocumentGuid>DOC_GUID2</DocumentGuid></EBookToc>"
            + "</EBook>";

        tocXml = new ByteArrayInputStream(xmlTestStr.getBytes());

        documentInfoMap =
            splitBookTocParseService.generateSplitBookToc(tocXml, splitTocXml, splitTocGuidList, splitTitleId);

        /*System.out.println("-----taskMap---------");
        for (Map.Entry<String, DocumentInfo> entry : documentInfoMap.entrySet()) {
        	System.out.println(entry.getKey() + "/" + entry.getValue().toString());
        }*/

        final DocumentInfo expectedDocInfo1 = new DocumentInfo();
        expectedDocInfo1.setSplitTitleId("splitTitle");

        final DocumentInfo expectedDocInfo2 = new DocumentInfo();
        expectedDocInfo2.setSplitTitleId("splitTitle_pt2");

        final DocumentInfo docInfo1 = documentInfoMap.get("DOC_GUID1");
        final DocumentInfo docInfo2 = documentInfoMap.get("DOC_GUID2");
        Assert.assertEquals(expectedDocInfo1.toString(), docInfo1.toString());
        Assert.assertEquals(expectedDocInfo2.toString(), docInfo2.toString());
        Assert.assertEquals(documentInfoMap.size(), 2);
    }

    @Test
    public void testSplitTocDuplicateDoc40Charac() {
        Map<String, DocumentInfo> documentInfoMap = new HashMap<>();

        final String guid = "I381A9010867911D99564CBDD35F58A0E";
        splitTocGuidList.add(guid);

        final String xmlTestStr = "<EBook>"
            + "<EBookToc><Name>BLARGH</Name><Guid>I23A2B1D0867911D99564CBDD35F58A0E-0000066</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
            + "<EBookToc><Name>BLARGH</Name><Guid>I381A9010867911D99564CBDD35F58A0E-00001111</Guid><DocumentGuid>DOC_GUID2</DocumentGuid></EBookToc>"
            + "<EBookToc><Name>BLARGH</Name><Guid>I23A2B1D0867911D99564CBDD35F58A0E-00001414</Guid><DocumentGuid>DOC_GUID2</DocumentGuid></EBookToc>"
            + "</EBook>";

        tocXml = new ByteArrayInputStream(xmlTestStr.getBytes());

        documentInfoMap =
            splitBookTocParseService.generateSplitBookToc(tocXml, splitTocXml, splitTocGuidList, splitTitleId);

        final DocumentInfo expectedDocInfo1 = new DocumentInfo();
        expectedDocInfo1.setSplitTitleId("splitTitle");

        final DocumentInfo expectedDocInfo2 = new DocumentInfo();
        expectedDocInfo2.setSplitTitleId("splitTitle_pt2");

        final DocumentInfo docInfo1 = documentInfoMap.get("DOC_GUID1");
        final DocumentInfo docInfo2 = documentInfoMap.get("DOC_GUID2");
        Assert.assertEquals(expectedDocInfo1.toString(), docInfo1.toString());
        Assert.assertEquals(expectedDocInfo2.toString(), docInfo2.toString());
        Assert.assertEquals(documentInfoMap.size(), 2);
    }
}
