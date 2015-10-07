package com.thomsonreuters.uscl.ereader.format.parsinghandler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

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


public class SplitBookTocFilterTest {
	
	private SplitBookTocFilter splitBookFilter;
	private Serializer serializer;
	List<String> splitTocGuidList;
	
	@Before
	public void setUp() throws Exception {
		splitTocGuidList = new ArrayList<String>();
		String guid1 = "TABLEOFCONTENTS33CHARACTERSLONG_2";
		splitTocGuidList.add(guid1);
		
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setNamespaceAware(true);
		SAXParser saxParser = factory.newSAXParser();
		
		splitBookFilter = new SplitBookTocFilter();
		
		splitBookFilter.setParent(saxParser.getXMLReader());
		splitBookFilter.setSplitTocGuidList(splitTocGuidList);
		
		splitBookFilter.setTitleBreakText("Title part ");
		
		Properties props = OutputPropertiesFactory.getDefaultMethodProperties(Method.XML);
		props.setProperty("omit-xml-declaration", "yes");
		serializer = SerializerFactory.getSerializer(props);
	}
	
	@After
	public void tearDown() throws Exception
	{
		serializer = null;
		splitBookFilter = null;
	}
		
	
	
	@Test
	public void test2() throws Exception{
		String xmlTestStr = "<EBook>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
				+ "<EBookToc><Name>BLARGH</Name><dummyTag>dummyValue</dummyTag><Guid>TABLEOFCONTENTS33CHARACTERSLONG_2</Guid><DocumentGuid>DOC_GUID2</DocumentGuid></EBookToc>"
				+ "</EBook>";
		
		String expectedResult = "<EBook>"
				+ "<titlebreak>Title part 1</titlebreak>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
				+ "<titlebreak>Title part 2</titlebreak>"
				+ "<EBookToc><Name>BLARGH</Name><dummyTag>dummyValue</dummyTag><Guid>TABLEOFCONTENTS33CHARACTERSLONG_2</Guid><DocumentGuid>DOC_GUID2</DocumentGuid></EBookToc>"
				+ "</EBook>";
		
		List<String> wrongSplitTocNodes = testHelper(xmlTestStr,expectedResult);
		assertEquals(0, wrongSplitTocNodes.size());
	}
	
	@Test
	public void testMissingDoc() throws Exception{
		
		String xmlTestStr = "<EBook>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_2</Guid><MissingDocument/></EBookToc>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_3</Guid>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_4</Guid><DocumentGuid>DOC_GUID3</DocumentGuid></EBookToc>"
				+ "</EBookToc>"
				+ "</EBook>";
		
		String expectedResult =  "<EBook>"
				+ "<titlebreak>Title part 1</titlebreak>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
				+ "<titlebreak>Title part 2</titlebreak>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_2</Guid><MissingDocument/></EBookToc>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_3</Guid>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_4</Guid><DocumentGuid>DOC_GUID3</DocumentGuid></EBookToc>"
				+ "</EBookToc>"
				+ "</EBook>";
		
		List<String> wrongSplitTocNodes = testHelper(xmlTestStr, expectedResult);
		assertEquals(0, wrongSplitTocNodes.size());
		
	}
	
	@Test
	public void test3() throws Exception{
		String guid1 = "TABLEOFCONTENTS33CHARACTERSLONG_3";
		splitTocGuidList.add(guid1);
		
		String xmlTestStr = "<EBook>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_2</Guid><DocumentGuid>DOC_GUID2</DocumentGuid></EBookToc>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_3</Guid><DocumentGuid>DOC_GUID3</DocumentGuid></EBookToc>"
				+ "</EBook>";
		
		String expectedResult = "<EBook>"
				+ "<titlebreak>Title part 1</titlebreak>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
				+ "<titlebreak>Title part 2</titlebreak>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_2</Guid><DocumentGuid>DOC_GUID2</DocumentGuid></EBookToc>"
				+ "<titlebreak>Title part 3</titlebreak>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_3</Guid><DocumentGuid>DOC_GUID3</DocumentGuid></EBookToc>"
				+ "</EBook>";
		
		List<String> wrongSplitTocNodes = testHelper(xmlTestStr,expectedResult);
		assertEquals(0, wrongSplitTocNodes.size());
	}
	
	@Test
	public void testMissingDoc2() throws Exception{
		String guid1 = "TABLEOFCONTENTS33CHARACTERSLONG_3";
		splitTocGuidList.add(guid1);
		
		String xmlTestStr = "<EBook>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_2</Guid><MissingDocument/></EBookToc>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_3</Guid>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_4</Guid><DocumentGuid>DOC_GUID3</DocumentGuid></EBookToc>"
				+ "</EBookToc>"
				+ "</EBook>";
		
		String expectedResult =  "<EBook>"
				+ "<titlebreak>Title part 1</titlebreak>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
				+ "<titlebreak>Title part 2</titlebreak>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_2</Guid><MissingDocument/></EBookToc>"
				+ "<titlebreak>Title part 3</titlebreak>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_3</Guid>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_4</Guid><DocumentGuid>DOC_GUID3</DocumentGuid></EBookToc>"
				+ "</EBookToc>"
				+ "</EBook>";
		
		List<String> wrongSplitTocNodes = testHelper(xmlTestStr, expectedResult);
		assertEquals(0, wrongSplitTocNodes.size());
		
	}
	
	@Test
	public void test4() throws Exception{
		String xmlTestStr = "<EBook>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_2</Guid><dummyTag>dummyValue</dummyTag><DocumentGuid>DOC_GUID2</DocumentGuid></EBookToc>"
				+ "</EBook>";
		
		String expectedResult = "<EBook>"
				+ "<titlebreak>Title part 1</titlebreak>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
				+ "<titlebreak>Title part 2</titlebreak>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_2</Guid><dummyTag>dummyValue</dummyTag><DocumentGuid>DOC_GUID2</DocumentGuid></EBookToc>"
				+ "</EBook>";
		
		List<String> wrongSplitTocNodes = testHelper(xmlTestStr,expectedResult);
		assertEquals(0, wrongSplitTocNodes.size());
	}
	
	@Test
	public void test5() throws Exception{
		String guid1 = "TABLEOFCONTENTS33CHARACTERSLONG_3";
		splitTocGuidList.add(guid1);
		
		String xmlTestStr = "<EBook>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_2</Guid><DocumentGuid>DOC_GUID2</DocumentGuid></EBookToc>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_3</Guid><DocumentGuid>DOC_GUID3</DocumentGuid></EBookToc>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_4</Guid></EBookToc>"
				+ "</EBook>";
		
		String expectedResult = "<EBook>"
				+ "<titlebreak>Title part 1</titlebreak>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
				+ "<titlebreak>Title part 2</titlebreak>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_2</Guid><DocumentGuid>DOC_GUID2</DocumentGuid></EBookToc>"
				+ "<titlebreak>Title part 3</titlebreak>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_3</Guid><DocumentGuid>DOC_GUID3</DocumentGuid></EBookToc>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_4</Guid></EBookToc>"
				+ "</EBook>";
		
		List<String> wrongSplitTocNodes = testHelper(xmlTestStr,expectedResult);
		assertEquals(0, wrongSplitTocNodes.size());
	}
	
	@Test
	public void test6() throws Exception{
		String guid1 = "TABLEOFCONTENTS33CHARACTERSLONG_4";
		splitTocGuidList.add(guid1);
		
		String xmlTestStr = "<EBook>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_2</Guid><DocumentGuid>DOC_GUID2</DocumentGuid></EBookToc>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_3</Guid>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_4</Guid><DocumentGuid>DOC_GUID3</DocumentGuid></EBookToc>"
				+ "</EBookToc>"
				+ "</EBook>";
		
		String expectedResult = "<EBook>"
				+ "<titlebreak>Title part 1</titlebreak>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
				+ "<titlebreak>Title part 2</titlebreak>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_2</Guid><DocumentGuid>DOC_GUID2</DocumentGuid></EBookToc>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_3</Guid>"
				+ "<titlebreak>Title part 3</titlebreak>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_4</Guid><DocumentGuid>DOC_GUID3</DocumentGuid></EBookToc>"
				+ "</EBookToc>"
				+ "</EBook>";
		
		List<String> wrongSplitTocNodes = testHelper(xmlTestStr, expectedResult);
		assertEquals(1, wrongSplitTocNodes.size());
		for (String tocNode : wrongSplitTocNodes){
			assertEquals(guid1, tocNode);
		}
		
	}
	
	@Test
	public void test7() throws Exception{
		String guid1 = "TABLEOFCONTENTS33CHARACTERSLONG_1";
		splitTocGuidList.add(guid1);
		
		String xmlTestStr = "<EBook>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_0</Guid>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_2</Guid><DocumentGuid>DOC_GUID2</DocumentGuid></EBookToc>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_3</Guid><DocumentGuid>DOC_GUID3</DocumentGuid></EBookToc>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_4</Guid></EBookToc>"
				+ "</EBookToc>"
				+ "</EBook>";
		
		String expectedResult = "<EBook>"
				+ "<titlebreak>Title part 1</titlebreak>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_0</Guid>"
				+ "<titlebreak>Title part 2</titlebreak>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
				+ "<titlebreak>Title part 3</titlebreak>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_2</Guid><DocumentGuid>DOC_GUID2</DocumentGuid></EBookToc>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_3</Guid><DocumentGuid>DOC_GUID3</DocumentGuid></EBookToc>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_4</Guid></EBookToc>"
				+ "</EBookToc>"
				+ "</EBook>";
		
		List<String> wrongSplitTocNodes = testHelper(xmlTestStr, expectedResult);
		assertEquals(1, wrongSplitTocNodes.size());	
		for (String tocNode : wrongSplitTocNodes){
			assertEquals(guid1, tocNode);
		}
	}
	
	@Test
	public void test8() throws Exception{
		String guid1 = "TABLEOFCONTENTS33CHARACTERSLONG_3";
		splitTocGuidList.add(guid1);
		
		String xmlTestStr = "<EBook>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_0</Guid>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_2</Guid><DocumentGuid>DOC_GUID2</DocumentGuid></EBookToc>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_3</Guid><DocumentGuid>DOC_GUID3</DocumentGuid></EBookToc>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_4</Guid></EBookToc>"
				+ "</EBookToc>"
				+ "</EBook>";
		
		String expectedResult = "<EBook>"
				+ "<titlebreak>Title part 1</titlebreak>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_0</Guid>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
				+ "<titlebreak>Title part 2</titlebreak>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_2</Guid><DocumentGuid>DOC_GUID2</DocumentGuid></EBookToc>"
				+ "<titlebreak>Title part 3</titlebreak>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_3</Guid><DocumentGuid>DOC_GUID3</DocumentGuid></EBookToc>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_4</Guid></EBookToc>"
				+ "</EBookToc>"
				+ "</EBook>";
		
		List<String> wrongSplitTocNodes = testHelper(xmlTestStr, expectedResult);
		assertEquals(0, wrongSplitTocNodes.size());		
	}
	
	@Test
	public void test9() throws Exception{
		String guid1 = "TABLEOFCONTENTS33CHARACTERSLONG_4";
		splitTocGuidList.add(guid1);
		
		String xmlTestStr = "<EBook>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_0</Guid>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_2</Guid>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_3</Guid><DocumentGuid>DOC_GUID3</DocumentGuid></EBookToc>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_4</Guid></EBookToc>"
				+ "</EBookToc>"
				+ "</EBookToc>"
				+ "</EBook>";
		
		String expectedResult = "<EBook>"
				+ "<titlebreak>Title part 1</titlebreak>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_0</Guid>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
				+ "<titlebreak>Title part 2</titlebreak>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_2</Guid>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_3</Guid><DocumentGuid>DOC_GUID3</DocumentGuid></EBookToc>"
				+ "<titlebreak>Title part 3</titlebreak>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_4</Guid></EBookToc>"
				+ "</EBookToc>"
				+ "</EBookToc>"
				+ "</EBook>";
		
		List<String> wrongSplitTocNodes = testHelper(xmlTestStr, expectedResult);
		assertEquals(0, wrongSplitTocNodes.size());
	}
		
	
	@Test
	public void testSplitTocXML() throws SAXException
	{	
		String xmlTestStr = "<EBook>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_2</Guid><DocumentGuid>DOC_GUID2</DocumentGuid></EBookToc>"
				+ "</EBook>";
		
		String expectedResult = "<EBook>"
				+ "<titlebreak>Title part 1</titlebreak>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
				+ "<titlebreak>Title part 2</titlebreak>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_2</Guid><DocumentGuid>DOC_GUID2</DocumentGuid></EBookToc>"
				+ "</EBook>";
		
		List<String> wrongSplitTocNodes = testHelper(xmlTestStr, expectedResult);
		assertEquals(0, wrongSplitTocNodes.size());
	}
	
	@Test
	public void testSplitBookTocDuplicateDoc() throws Exception {
		
		String xmlTestStr = "<EBook>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_2</Guid><DocumentGuid>DOC_GUID2</DocumentGuid></EBookToc>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_3</Guid><DocumentGuid>DOC_GUID2</DocumentGuid></EBookToc>"
				+ "</EBook>";
		
		String expectedResult = "<EBook>"
				+ "<titlebreak>Title part 1</titlebreak>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
				+ "<titlebreak>Title part 2</titlebreak>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_2</Guid><DocumentGuid>DOC_GUID2</DocumentGuid></EBookToc>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_3</Guid><DocumentGuid>DOC_GUID2</DocumentGuid></EBookToc>"
				+ "</EBook>";
		
		List<String> wrongSplitTocNodes = testHelper(xmlTestStr, expectedResult);
		assertEquals(0, wrongSplitTocNodes.size());
	}
	
		
	@Test
	public void testSplitTocXMLSingleUUID() throws SAXException
	{			
		String xmlTestStr = "<EBook>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
				+ "</EBook>";
		
		String expectedResult = "<EBook>"
				+ "<titlebreak>Title part 1</titlebreak>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
				+ "</EBook>";
		
		List<String> wrongSplitTocNodes = testHelper(xmlTestStr, expectedResult);
		assertEquals(0, wrongSplitTocNodes.size());
	}
	
	public List<String> testHelper(String inputXML, String expectedResult) throws SAXException
	{
		List<String> wrongSplitTocNode = new ArrayList<String>();
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
			
			String result = output.toString();
			
			assertEquals(expectedResult, result);
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
				fail("Couldn't clean up resources: " + e.getMessage());
			}
		}

		return wrongSplitTocNode;
	}

}
