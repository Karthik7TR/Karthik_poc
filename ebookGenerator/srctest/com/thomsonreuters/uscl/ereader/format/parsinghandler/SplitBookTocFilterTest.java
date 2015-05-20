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
		
		splitBookFilter.setTitleBreakText("Title part");
		
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
	public void testStringOutofBoundsInParser() throws Exception{
		
		
	}

	
	@Test
	public void testSplitTocXML() throws SAXException
	{	
		splitBookFilter.setNumber(0);
		String xmlTestStr = "<EBook>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_2</Guid><DocumentGuid>DOC_GUID2</DocumentGuid></EBookToc>"
				+ "</EBook>";
		
		String expectedResult = "<EBook>"
				+ "<titlebreak>Title part0</titlebreak>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
				+ "<titlebreak>Title part1</titlebreak>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_2</Guid><DocumentGuid>DOC_GUID2</DocumentGuid></EBookToc>"
				+ "</EBook>";
		
		testHelper(xmlTestStr, expectedResult);
	}
	
	@Test
	public void testSplitTocXMLNomatchUUID() throws SAXException
	{			
		splitBookFilter.setNumber(0);

		String xmlTestStr = "<EBook>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_2</Guid><DocumentGuid>DOC_GUID2</DocumentGuid></EBookToc>"
				+ "</EBook>";
		
		String expectedResult = "<EBook>"
				+ "<titlebreak>Title part0</titlebreak>"
				+ "<titlebreak>Title part1</titlebreak>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_2</Guid><DocumentGuid>DOC_GUID2</DocumentGuid></EBookToc>"
				+ "</EBook>";
		
		testHelper(xmlTestStr, expectedResult);
	}
	
	@Test
	public void testSplitTocXMLSingleUUID() throws SAXException
	{	
		splitBookFilter.setNumber(0);
		String xmlTestStr = "<EBook>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
				+ "</EBook>";
		
		String expectedResult = "<EBook>"
				+ "<titlebreak>Title part0</titlebreak>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
				+ "</EBook>";
		
		testHelper(xmlTestStr, expectedResult);
	}
	
	public void testHelper(String inputXML, String expectedResult) throws SAXException
	{
		ByteArrayInputStream input = null;
		ByteArrayOutputStream output = null;
		try
		{
			input = new ByteArrayInputStream(inputXML.getBytes());
			output = new ByteArrayOutputStream();
			
			serializer.setOutputStream(output);
			
			splitBookFilter.setContentHandler(serializer.asContentHandler());
			splitBookFilter.parse(new InputSource(input));
			
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
	}

}
