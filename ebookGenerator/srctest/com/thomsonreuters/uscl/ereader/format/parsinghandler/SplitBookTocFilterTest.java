package com.thomsonreuters.uscl.ereader.format.parsinghandler;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.xml.serializer.Method;
import org.apache.xml.serializer.OutputPropertiesFactory;
import org.apache.xml.serializer.Serializer;
import org.apache.xml.serializer.SerializerFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public final class SplitBookTocFilterTest {
    private SplitBookTocFilter splitBookFilter;
    private Serializer serializer;
    private List<String> splitTocGuidList;

    @Before
    public void setUp() throws ParserConfigurationException, SAXException {
        splitTocGuidList = new ArrayList<>();
        final String splitGuid = "TABLEOFCONTENTS33CHARACTERSLONG_2";
        splitTocGuidList.add(splitGuid);

        final SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        final SAXParser saxParser = factory.newSAXParser();

        splitBookFilter = new SplitBookTocFilter("title", splitTocGuidList);

        splitBookFilter.setParent(saxParser.getXMLReader());

        final Properties props = OutputPropertiesFactory.getDefaultMethodProperties(Method.XML);
        props.setProperty("omit-xml-declaration", "yes");
        serializer = SerializerFactory.getSerializer(props);
    }

    @After
    public void tearDown() {
        serializer = null;
        splitBookFilter = null;
    }

    @Test
    public void test2() throws Exception {
        final String xmlTestStr = "<EBook>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
            + "<EBookToc><Name>BLARGH</Name><dummyTag>dummyValue</dummyTag><Guid>TABLEOFCONTENTS33CHARACTERSLONG_2</Guid><DocumentGuid>DOC_GUID2</DocumentGuid></EBookToc>"
            + "</EBook>";

        final String expectedResult = "<EBook>"
            + "<titlebreak>eBook 1 of 2</titlebreak>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
            + "<titlebreak>eBook 2 of 2</titlebreak>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_2</Guid><DocumentGuid>DOC_GUID2</DocumentGuid></EBookToc>"
            + "</EBook>";

        testHelper(xmlTestStr, expectedResult);
    }

    @Test
    public void testMissingDoc() throws Exception {
        final String xmlTestStr = "<EBook>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_2</Guid><MissingDocument/></EBookToc>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_3</Guid>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_4</Guid><DocumentGuid>DOC_GUID3</DocumentGuid></EBookToc>"
            + "</EBookToc>"
            + "</EBook>";

        final String expectedResult = "<EBook>"
            + "<titlebreak>eBook 1 of 2</titlebreak>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
            + "<titlebreak>eBook 2 of 2</titlebreak>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_2</Guid><MissingDocument/></EBookToc>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_3</Guid>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_4</Guid><DocumentGuid>DOC_GUID3</DocumentGuid></EBookToc>"
            + "</EBookToc>"
            + "</EBook>";

        testHelper(xmlTestStr, expectedResult);
    }

    @Test
    public void test3() throws Exception {
        final String splitGuid = "TABLEOFCONTENTS33CHARACTERSLONG_3";
        splitTocGuidList.add(splitGuid);

        final String xmlTestStr = "<EBook>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_2</Guid><DocumentGuid>DOC_GUID2</DocumentGuid></EBookToc>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_3</Guid><DocumentGuid>DOC_GUID3</DocumentGuid></EBookToc>"
            + "</EBook>";

        final String expectedResult = "<EBook>"
            + "<titlebreak>eBook 1 of 3</titlebreak>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
            + "<titlebreak>eBook 2 of 3</titlebreak>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_2</Guid><DocumentGuid>DOC_GUID2</DocumentGuid></EBookToc>"
            + "<titlebreak>eBook 3 of 3</titlebreak>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_3</Guid><DocumentGuid>DOC_GUID3</DocumentGuid></EBookToc>"
            + "</EBook>";

        testHelper(xmlTestStr, expectedResult);
    }

    @Test
    public void testMissingDoc2() throws Exception {
        final String splitGuid = "TABLEOFCONTENTS33CHARACTERSLONG_3";
        splitTocGuidList.add(splitGuid);

        final String xmlTestStr = "<EBook>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_2</Guid><MissingDocument/></EBookToc>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_3</Guid>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_4</Guid><DocumentGuid>DOC_GUID3</DocumentGuid></EBookToc>"
            + "</EBookToc>"
            + "</EBook>";

        final String expectedResult = "<EBook>"
            + "<titlebreak>eBook 1 of 3</titlebreak>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
            + "<titlebreak>eBook 2 of 3</titlebreak>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_2</Guid><MissingDocument/></EBookToc>"
            + "<titlebreak>eBook 3 of 3</titlebreak>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_3</Guid>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_4</Guid><DocumentGuid>DOC_GUID3</DocumentGuid></EBookToc>"
            + "</EBookToc>"
            + "</EBook>";

        testHelper(xmlTestStr, expectedResult);
    }

    @Test
    public void test4() throws Exception {
        final String xmlTestStr = "<EBook>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_2</Guid><dummyTag>dummyValue</dummyTag><DocumentGuid>DOC_GUID2</DocumentGuid></EBookToc>"
            + "</EBook>";

        final String expectedResult = "<EBook>"
            + "<titlebreak>eBook 1 of 2</titlebreak>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
            + "<titlebreak>eBook 2 of 2</titlebreak>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_2</Guid><DocumentGuid>DOC_GUID2</DocumentGuid></EBookToc>"
            + "</EBook>";

        testHelper(xmlTestStr, expectedResult);
    }

    @Test
    public void test5() throws Exception {
        final String guid1 = "TABLEOFCONTENTS33CHARACTERSLONG_3";
        splitTocGuidList.add(guid1);

        final String xmlTestStr = "<EBook>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_2</Guid><DocumentGuid>DOC_GUID2</DocumentGuid></EBookToc>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_3</Guid><DocumentGuid>DOC_GUID3</DocumentGuid></EBookToc>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_4</Guid><DocumentGuid>DOC_GUID4</DocumentGuid></EBookToc>"
            + "</EBook>";

        final String expectedResult = "<EBook>"
            + "<titlebreak>eBook 1 of 3</titlebreak>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
            + "<titlebreak>eBook 2 of 3</titlebreak>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_2</Guid><DocumentGuid>DOC_GUID2</DocumentGuid></EBookToc>"
            + "<titlebreak>eBook 3 of 3</titlebreak>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_3</Guid><DocumentGuid>DOC_GUID3</DocumentGuid></EBookToc>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_4</Guid><DocumentGuid>DOC_GUID4</DocumentGuid></EBookToc>"
            + "</EBook>";

        testHelper(xmlTestStr, expectedResult);
    }

    @Test
    public void test6() throws Exception {
        final String guid1 = "TABLEOFCONTENTS33CHARACTERSLONG_4";
        splitTocGuidList.add(guid1);

        final String xmlTestStr = "<EBook>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_2</Guid><DocumentGuid>DOC_GUID2</DocumentGuid></EBookToc>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_3</Guid>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_4</Guid><DocumentGuid>DOC_GUID3</DocumentGuid></EBookToc>"
            + "</EBookToc>"
            + "</EBook>";

        final String expectedResult = "<EBook>"
            + "<titlebreak>eBook 1 of 3</titlebreak>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
            + "<titlebreak>eBook 2 of 3</titlebreak>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_2</Guid><DocumentGuid>DOC_GUID2</DocumentGuid></EBookToc>"
            + "<titlebreak>eBook 3 of 3</titlebreak>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_3</Guid>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_4</Guid><DocumentGuid>DOC_GUID3</DocumentGuid></EBookToc>"
            + "</EBookToc>"
            + "</EBook>";

        testHelper(xmlTestStr, expectedResult);
    }

    @Test(expected = RuntimeException.class)
    public void test7() throws Exception {
        final String guid1 = "TABLEOFCONTENTS33CHARACTERSLONG_1";
        splitTocGuidList.add(guid1);

        final String xmlTestStr = "<EBook>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_0</Guid>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_2</Guid><DocumentGuid>DOC_GUID2</DocumentGuid></EBookToc>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_3</Guid><DocumentGuid>DOC_GUID3</DocumentGuid></EBookToc>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_4</Guid><DocumentGuid>DOC_GUID4</DocumentGuid></EBookToc>"
            + "</EBookToc>"
            + "</EBook>";

        final String expectedResult = "";

        try {
            testHelper(xmlTestStr, expectedResult);
        } catch (final Exception e) {
            assertThat(e.getMessage(), containsString("Redundant split TOC uuid found: TABLEOFCONTENTS33CHARACTERSLONG_1"));
            throw e;
        }
        fail();
    }

    @Test
    public void test8() throws Exception {
        final String guid1 = "TABLEOFCONTENTS33CHARACTERSLONG_3";
        splitTocGuidList.add(guid1);

        final String xmlTestStr = "<EBook>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_0</Guid>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_2</Guid><DocumentGuid>DOC_GUID2</DocumentGuid></EBookToc>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_3</Guid><DocumentGuid>DOC_GUID3</DocumentGuid></EBookToc>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_4</Guid><DocumentGuid>DOC_GUID4</DocumentGuid></EBookToc>"
            + "</EBookToc>"
            + "</EBook>";

        final String expectedResult = "<EBook>"
            + "<titlebreak>eBook 1 of 3</titlebreak>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_0</Guid>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
            + "<titlebreak>eBook 2 of 3</titlebreak>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_2</Guid><DocumentGuid>DOC_GUID2</DocumentGuid></EBookToc>"
            + "<titlebreak>eBook 3 of 3</titlebreak>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_3</Guid><DocumentGuid>DOC_GUID3</DocumentGuid></EBookToc>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_4</Guid><DocumentGuid>DOC_GUID4</DocumentGuid></EBookToc>"
            + "</EBookToc>"
            + "</EBook>";

        testHelper(xmlTestStr, expectedResult);
    }

    @Test
    public void test9() throws Exception {
        final String guid1 = "TABLEOFCONTENTS33CHARACTERSLONG_4";
        splitTocGuidList.add(guid1);

        final String xmlTestStr = "<EBook>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_0</Guid>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_2</Guid>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_3</Guid><DocumentGuid>DOC_GUID3</DocumentGuid></EBookToc>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_4</Guid><DocumentGuid>DOC_GUID4</DocumentGuid></EBookToc>"
            + "</EBookToc>"
            + "</EBookToc>"
            + "</EBook>";

        final String expectedResult = "<EBook>"
            + "<titlebreak>eBook 1 of 3</titlebreak>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_0</Guid>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
            + "<titlebreak>eBook 2 of 3</titlebreak>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_2</Guid>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_3</Guid><DocumentGuid>DOC_GUID3</DocumentGuid></EBookToc>"
            + "<titlebreak>eBook 3 of 3</titlebreak>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_4</Guid><DocumentGuid>DOC_GUID4</DocumentGuid></EBookToc>"
            + "</EBookToc>"
            + "</EBookToc>"
            + "</EBook>";

        testHelper(xmlTestStr, expectedResult);
    }

    @Test
    public void testCombinedSplitBook() throws Exception {
        splitTocGuidList.clear();
        splitTocGuidList.add("TABLEOFCONTENTS33CHARACTERSLONG_3");
        final String xmlTestStr = "<EBook>"
                + "<EBookTitle titleId=\"uscl-an-keyrules_small_test\" proviewName=\"KeyRules\"/>"
                + "<EBookInlineToc/>"
                + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_0</Guid>"
                + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
                + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_2</Guid>"
                + "<EBookTitle titleId=\"uscl-an-book2_small_test\" proviewName=\"Book2\"/>"
                + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_3</Guid><DocumentGuid>DOC_GUID3</DocumentGuid></EBookToc>"
                + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_4</Guid><DocumentGuid>DOC_GUID4</DocumentGuid></EBookToc>"
                + "</EBookToc>"
                + "</EBookToc>"
                + "<EBookPublishingInformation/>"
                + "</EBook>";
        final String expectedResult = "<EBook>"
                + "<titlebreak>eBook 1 of 2</titlebreak>"
                + "<EBookTitle titleId=\"uscl-an-keyrules_small_test\" proviewName=\"KeyRules\"/>"
                + "<EBookInlineToc/>"
                + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_0</Guid>"
                + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
                + "<EBookTitle titleId=\"uscl-an-book2_small_test\" proviewName=\"Book2\"/>"
                + "<titlebreak>eBook 2 of 2</titlebreak>"
                + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_2</Guid>"
                + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_3</Guid><DocumentGuid>DOC_GUID3</DocumentGuid></EBookToc>"
                + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_4</Guid><DocumentGuid>DOC_GUID4</DocumentGuid></EBookToc>"
                + "</EBookToc>"
                + "</EBookToc>"
                + "<EBookPublishingInformation/>"
                + "</EBook>";
        testHelper(xmlTestStr, expectedResult);
    }

    @Test
    public void testSplitTocXML() throws Exception {
        final String xmlTestStr = "<EBook>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_2</Guid><DocumentGuid>DOC_GUID2</DocumentGuid></EBookToc>"
            + "</EBook>";

        final String expectedResult = "<EBook>"
            + "<titlebreak>eBook 1 of 2</titlebreak>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
            + "<titlebreak>eBook 2 of 2</titlebreak>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_2</Guid><DocumentGuid>DOC_GUID2</DocumentGuid></EBookToc>"
            + "</EBook>";

        testHelper(xmlTestStr, expectedResult);
    }

    @Test
    public void testSplitBookTocDuplicateDoc() throws Exception {
        final String xmlTestStr = "<EBook>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_2</Guid><DocumentGuid>DOC_GUID2</DocumentGuid></EBookToc>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_3</Guid><DocumentGuid>DOC_GUID2</DocumentGuid></EBookToc>"
            + "</EBook>";

        final String expectedResult = "<EBook>"
            + "<titlebreak>eBook 1 of 2</titlebreak>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
            + "<titlebreak>eBook 2 of 2</titlebreak>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_2</Guid><DocumentGuid>DOC_GUID2</DocumentGuid></EBookToc>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_3</Guid><DocumentGuid>DOC_GUID2</DocumentGuid></EBookToc>"
            + "</EBook>";

        testHelper(xmlTestStr, expectedResult);
    }

    @Test
    public void testSplitTocXMLSingleUUID() throws Exception {
        final String xmlTestStr = "<EBook>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
            + "</EBook>";

        final String expectedResult = "<EBook>"
            + "<titlebreak>eBook 1 of 2</titlebreak>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
            + "</EBook>";

        testHelper(xmlTestStr, expectedResult);
    }

    @Test(expected = RuntimeException.class)
    public void testFollowingSplitUuids() throws Exception {
        splitTocGuidList.add("TABLEOFCONTENTS33CHARACTERSLONG_2");
        splitTocGuidList.add("TABLEOFCONTENTS33CHARACTERSLONG_4");

        final String xmlTestStr = "<EBook>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_0</Guid>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_2</Guid>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_3</Guid>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_4</Guid><DocumentGuid>DOC_GUID4</DocumentGuid></EBookToc>"
            + "</EBookToc>"
            + "</EBookToc>"
            + "</EBookToc>"
            + "</EBook>";

        final String expectedResult = "";

        try {
            testHelper(xmlTestStr, expectedResult);
        } catch (final Exception e) {
            assertThat(e.getMessage(), containsString("Redundant split TOC uuid found: TABLEOFCONTENTS33CHARACTERSLONG_4"));
            throw e;
        }
        fail();
    }

    public void testHelper(final String inputXML, final String expectedResult) throws Exception {
        ByteArrayInputStream input = null;
        ByteArrayOutputStream output = null;
        try {
            input = new ByteArrayInputStream(inputXML.getBytes());
            output = new ByteArrayOutputStream();

            serializer.setOutputStream(output);

            splitBookFilter.setContentHandler(serializer.asContentHandler());
            splitBookFilter.parse(new InputSource(input));

            final String result = output.toString();

            assertEquals(expectedResult, result);
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
                if (output != null) {
                    output.close();
                }
            } catch (final Exception e) {
                fail("Couldn't clean up resources: " + e.getMessage());
            }
        }
    }
}
