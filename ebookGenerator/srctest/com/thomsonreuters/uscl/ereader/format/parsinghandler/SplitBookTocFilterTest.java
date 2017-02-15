package com.thomsonreuters.uscl.ereader.format.parsinghandler;

import static org.junit.Assert.assertEquals;
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

public final class SplitBookTocFilterTest
{
    private SplitBookTocFilter splitBookFilter;
    private Serializer serializer;
    private List<String> splitTocGuidList;

    @Before
    public void setUp() throws ParserConfigurationException, SAXException
    {
        splitTocGuidList = new ArrayList<>();
        final String guid1 = "TABLEOFCONTENTS33CHARACTERSLONG_2";
        splitTocGuidList.add(guid1);

        final SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        final SAXParser saxParser = factory.newSAXParser();

        splitBookFilter = new SplitBookTocFilter();

        splitBookFilter.setParent(saxParser.getXMLReader());
        splitBookFilter.setSplitTocGuidList(splitTocGuidList);

        final Properties props = OutputPropertiesFactory.getDefaultMethodProperties(Method.XML);
        props.setProperty("omit-xml-declaration", "yes");
        serializer = SerializerFactory.getSerializer(props);
    }

    @After
    public void tearDown()
    {
        serializer = null;
        splitBookFilter = null;
    }

    @Test
    public void test2()
    {
        final String xmlTestStr = "<EBook>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
            + "<EBookToc><Name>BLARGH</Name><dummyTag>dummyValue</dummyTag><Guid>TABLEOFCONTENTS33CHARACTERSLONG_2</Guid><DocumentGuid>DOC_GUID2</DocumentGuid></EBookToc>"
            + "</EBook>";

        final String expectedResult = "<EBook>"
            + "<titlebreak>eBook 1 of 2</titlebreak>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
            + "<titlebreak>eBook 2 of 2</titlebreak>"
            + "<EBookToc><Name>BLARGH</Name><dummyTag>dummyValue</dummyTag><Guid>TABLEOFCONTENTS33CHARACTERSLONG_2</Guid><DocumentGuid>DOC_GUID2</DocumentGuid></EBookToc>"
            + "</EBook>";

        final List<String> wrongSplitTocNodes = testHelper(xmlTestStr, expectedResult);
        assertEquals(0, wrongSplitTocNodes.size());
    }

    @Test
    public void testMissingDoc()
    {
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

        final List<String> wrongSplitTocNodes = testHelper(xmlTestStr, expectedResult);
        assertEquals(0, wrongSplitTocNodes.size());
    }

    @Test
    public void test3()
    {
        final String guid1 = "TABLEOFCONTENTS33CHARACTERSLONG_3";
        splitTocGuidList.add(guid1);

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

        final List<String> wrongSplitTocNodes = testHelper(xmlTestStr, expectedResult);
        assertEquals(0, wrongSplitTocNodes.size());
    }

    @Test
    public void testMissingDoc2()
    {
        final String guid1 = "TABLEOFCONTENTS33CHARACTERSLONG_3";
        splitTocGuidList.add(guid1);

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

        final List<String> wrongSplitTocNodes = testHelper(xmlTestStr, expectedResult);
        assertEquals(0, wrongSplitTocNodes.size());
    }

    @Test
    public void test4()
    {
        final String xmlTestStr = "<EBook>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_2</Guid><dummyTag>dummyValue</dummyTag><DocumentGuid>DOC_GUID2</DocumentGuid></EBookToc>"
            + "</EBook>";

        final String expectedResult = "<EBook>"
            + "<titlebreak>eBook 1 of 2</titlebreak>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
            + "<titlebreak>eBook 2 of 2</titlebreak>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_2</Guid><dummyTag>dummyValue</dummyTag><DocumentGuid>DOC_GUID2</DocumentGuid></EBookToc>"
            + "</EBook>";

        final List<String> wrongSplitTocNodes = testHelper(xmlTestStr, expectedResult);
        assertEquals(0, wrongSplitTocNodes.size());
    }

    @Test
    public void test5()
    {
        final String guid1 = "TABLEOFCONTENTS33CHARACTERSLONG_3";
        splitTocGuidList.add(guid1);

        final String xmlTestStr = "<EBook>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_2</Guid><DocumentGuid>DOC_GUID2</DocumentGuid></EBookToc>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_3</Guid><DocumentGuid>DOC_GUID3</DocumentGuid></EBookToc>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_4</Guid></EBookToc>"
            + "</EBook>";

        final String expectedResult = "<EBook>"
            + "<titlebreak>eBook 1 of 3</titlebreak>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
            + "<titlebreak>eBook 2 of 3</titlebreak>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_2</Guid><DocumentGuid>DOC_GUID2</DocumentGuid></EBookToc>"
            + "<titlebreak>eBook 3 of 3</titlebreak>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_3</Guid><DocumentGuid>DOC_GUID3</DocumentGuid></EBookToc>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_4</Guid></EBookToc>"
            + "</EBook>";

        final List<String> wrongSplitTocNodes = testHelper(xmlTestStr, expectedResult);
        assertEquals(0, wrongSplitTocNodes.size());
    }

    @Test
    public void test6()
    {
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
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_3</Guid>"
            + "<titlebreak>eBook 3 of 3</titlebreak>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_4</Guid><DocumentGuid>DOC_GUID3</DocumentGuid></EBookToc>"
            + "</EBookToc>"
            + "</EBook>";

        final List<String> wrongSplitTocNodes = testHelper(xmlTestStr, expectedResult);
        assertEquals(1, wrongSplitTocNodes.size());
        for (final String tocNode : wrongSplitTocNodes)
        {
            assertEquals(guid1, tocNode);
        }
    }

    @Test
    public void test7()
    {
        final String guid1 = "TABLEOFCONTENTS33CHARACTERSLONG_1";
        splitTocGuidList.add(guid1);

        final String xmlTestStr = "<EBook>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_0</Guid>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_2</Guid><DocumentGuid>DOC_GUID2</DocumentGuid></EBookToc>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_3</Guid><DocumentGuid>DOC_GUID3</DocumentGuid></EBookToc>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_4</Guid></EBookToc>"
            + "</EBookToc>"
            + "</EBook>";

        final String expectedResult = "<EBook>"
            + "<titlebreak>eBook 1 of 3</titlebreak>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_0</Guid>"
            + "<titlebreak>eBook 2 of 3</titlebreak>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
            + "<titlebreak>eBook 3 of 3</titlebreak>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_2</Guid><DocumentGuid>DOC_GUID2</DocumentGuid></EBookToc>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_3</Guid><DocumentGuid>DOC_GUID3</DocumentGuid></EBookToc>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_4</Guid></EBookToc>"
            + "</EBookToc>"
            + "</EBook>";

        final List<String> wrongSplitTocNodes = testHelper(xmlTestStr, expectedResult);
        assertEquals(1, wrongSplitTocNodes.size());
        for (final String tocNode : wrongSplitTocNodes)
        {
            assertEquals(guid1, tocNode);
        }
    }

    @Test
    public void test8()
    {
        final String guid1 = "TABLEOFCONTENTS33CHARACTERSLONG_3";
        splitTocGuidList.add(guid1);

        final String xmlTestStr = "<EBook>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_0</Guid>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_2</Guid><DocumentGuid>DOC_GUID2</DocumentGuid></EBookToc>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_3</Guid><DocumentGuid>DOC_GUID3</DocumentGuid></EBookToc>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_4</Guid></EBookToc>"
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
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_4</Guid></EBookToc>"
            + "</EBookToc>"
            + "</EBook>";

        final List<String> wrongSplitTocNodes = testHelper(xmlTestStr, expectedResult);
        assertEquals(0, wrongSplitTocNodes.size());
    }

    @Test
    public void test9()
    {
        final String guid1 = "TABLEOFCONTENTS33CHARACTERSLONG_4";
        splitTocGuidList.add(guid1);

        final String xmlTestStr = "<EBook>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_0</Guid>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_2</Guid>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_3</Guid><DocumentGuid>DOC_GUID3</DocumentGuid></EBookToc>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_4</Guid></EBookToc>"
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
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_4</Guid></EBookToc>"
            + "</EBookToc>"
            + "</EBookToc>"
            + "</EBook>";

        final List<String> wrongSplitTocNodes = testHelper(xmlTestStr, expectedResult);
        assertEquals(0, wrongSplitTocNodes.size());
    }

    @Test
    public void testSplitTocXML()
    {
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

        final List<String> wrongSplitTocNodes = testHelper(xmlTestStr, expectedResult);
        assertEquals(0, wrongSplitTocNodes.size());
    }

    @Test
    public void testSplitBookTocDuplicateDoc()
    {
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

        final List<String> wrongSplitTocNodes = testHelper(xmlTestStr, expectedResult);
        assertEquals(0, wrongSplitTocNodes.size());
    }

    @Test
    public void testSplitTocXMLSingleUUID()
    {
        final String xmlTestStr = "<EBook>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
            + "</EBook>";

        final String expectedResult = "<EBook>"
            + "<titlebreak>eBook 1 of 2</titlebreak>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
            + "</EBook>";

        final List<String> wrongSplitTocNodes = testHelper(xmlTestStr, expectedResult);
        assertEquals(0, wrongSplitTocNodes.size());
    }

    public List<String> testHelper(final String inputXML, final String expectedResult)
    {
        List<String> wrongSplitTocNode = new ArrayList<>();
        ByteArrayInputStream input = null;
        ByteArrayOutputStream output = null;
        try
        {
            input = new ByteArrayInputStream(inputXML.getBytes());
            output = new ByteArrayOutputStream();

            serializer.setOutputStream(output);

            splitBookFilter.setContentHandler(serializer.asContentHandler());
            splitBookFilter.parse(new InputSource(input));
            wrongSplitTocNode = splitBookFilter.getWrongSplitTocNode();
            splitBookFilter.setSplitTilteId("title");

            final String result = output.toString();

            assertEquals(expectedResult, result);
        }
        catch (final Exception e)
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
            catch (final Exception e)
            {
                fail("Couldn't clean up resources: " + e.getMessage());
            }
        }

        return wrongSplitTocNode;
    }
}
