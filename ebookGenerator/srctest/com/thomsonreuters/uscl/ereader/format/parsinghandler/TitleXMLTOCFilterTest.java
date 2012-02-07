/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.format.parsinghandler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;

/**
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public class TitleXMLTOCFilterTest {
	
	private TitleXMLTOCFilter tocFilter;
	private Serializer serializer;
	private File badMapFile;

    @Rule
    public TemporaryFolder testFiles = new TemporaryFolder();
	
	@Before
	public void setUp() throws EBookFormatException, IOException, SAXException, ParserConfigurationException
	{
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setNamespaceAware(true);
		SAXParser saxParser = factory.newSAXParser();
		
		File guidToTocMapping = testFiles.newFile("TitleXMLTOCFilterTestGuidToTocMapping");
    	OutputStream outputStream = new FileOutputStream(guidToTocMapping);
    	String mappingFileLine1 = "Iff5a5a987c8f11da9de6e47d6d5aa7a5,I0900caf0675c11da90ebf04471783734|" +
				"I0901b550675c11da90ebf04471783734|Iff5a5a977c8f11da9de6e47d6d5aa7a5|"; 
		outputStream.write(mappingFileLine1.getBytes());
		outputStream.write("\n".getBytes());
    	String mappingFileLine2 = "Iff5a5aa17c8f11da9de6e47d6d5aa7a5,Iff5a5aa37c8f11da9de6e47d6d5aa7a5|"; 
		outputStream.write(mappingFileLine2.getBytes());
		outputStream.flush();
		outputStream.close();
		
		badMapFile = testFiles.newFile("TitleXMLTOCFilterTestGuidToTocBadMapping");
    	OutputStream badOutStream = new FileOutputStream(badMapFile);
    	badOutStream.write("Iff5a5a987c8f11da9de6e47d6d5aa7a5,I0900caf0675c11da90ebf04471783734|".getBytes());
    	badOutStream.write("\n".getBytes());
    	badOutStream.write("Iff5a5aa17c8f11da9de6e47d6d5aa7a5,".getBytes());
    	badOutStream.flush();
    	badOutStream.close();
		
		tocFilter = new TitleXMLTOCFilter(guidToTocMapping);
		tocFilter.setParent(saxParser.getXMLReader());
		
		Properties props = OutputPropertiesFactory.getDefaultMethodProperties(Method.XHTML);
		props.setProperty("omit-xml-declaration", "yes");
		serializer = SerializerFactory.getSerializer(props);
	}
	
	@After
	public void tearDown() throws Exception
	{
		serializer = null;
		tocFilter = null;
	}
	
	/** 
	 * Helper method that sets up the repeating pieces of each test and modifies the ImageService
	 * values that are returned along with the input and output.
	 * 
	 * @param inputXML input string for the test.
	 * @param expectedResult the expected output for the specified input string.
	 */
	public void testHelper(String inputXML, String expectedResult) throws SAXException
	{
		ByteArrayInputStream input = null;
		ByteArrayOutputStream output = null;
		try
		{
			input = new ByteArrayInputStream(inputXML.getBytes());
			output = new ByteArrayOutputStream();
			
			serializer.setOutputStream(output);
			
			tocFilter.setContentHandler(serializer.asContentHandler());
			tocFilter.parse(new InputSource(input));
			
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
	
	@Test
	public void testFileNotExistConstructor() throws IOException, EBookFormatException
	{
		File testFile = new File("testFileNotExistConstructor");
		try
		{
			TitleXMLTOCFilter aFilter = new TitleXMLTOCFilter(testFile);
			aFilter.getTocToDocMapping();
		}
		catch (IllegalArgumentException e)
		{
			assertEquals("File passed into TitleXMLTOCFilter constructor must be a valid file.", 
					e.getMessage());
		}
	}
	
	@Test
	public void testEmptyFileConstructor() throws IOException
	{
		File testFile = testFiles.newFile("testFileNotExistConstructor");
		try
		{
			TitleXMLTOCFilter aFilter = new TitleXMLTOCFilter(testFile);
			aFilter.getTocToDocMapping();
		}
		catch (EBookFormatException e)
		{
			assertEquals("No TOC to DOC mapping were loaded, please double check that the following" +
					" file is not empty: " + testFile.getAbsolutePath(), 
					e.getMessage());
		}
	}
	
	@Test
	public void testBadMapFileConstructor() throws IOException
	{
		try
		{
			TitleXMLTOCFilter aFilter = new TitleXMLTOCFilter(badMapFile);
			aFilter.getTocToDocMapping();
		}
		catch (EBookFormatException e)
		{
			assertEquals("Please verify that each document GUID in the following file has " +
					"at least one TOC guid associated with it: " + 	badMapFile.getAbsolutePath(), 
					e.getMessage());
		}
	}

	@Test
	public void testSimpleEntryTransformation() throws SAXException
	{	
		String xmlTestStr = "<test><entry s=\"I0901b550675c11da90ebf04471783734\"/></test>";
		String expectedResult = "<test><entry s=\"Iff5a5a987c8f11da9de6e47d6d5aa7a5/" +
				"I0901b550675c11da90ebf04471783734\"/></test>";
		
		testHelper(xmlTestStr, expectedResult);
	}

	@Test
	public void testTOCGuidNotFoundInMap()
	{	
		try
		{
			String xmlTestStr = "<test><entry s=\"Iff5a81a47c8f11da9de6e47d6d5aa7a5\"/></test>";
			String expectedResult = "<test><entry s=\"Iff5a81a47c8f11da9de6e47d6d5aa7a5\"/></test>";
			
			testHelper(xmlTestStr, expectedResult);
		}
		catch (SAXException e)
		{
			assertEquals("Could not find DOC Guid in mapping file for TOC: Iff5a81a47c8f11da9de6e47d6d5aa7a5",
					e.getMessage());
		}
	}
	
	@Test
	public void testEntryNoSAttributeTransformation() throws SAXException
	{	
		String xmlTestStr = "<test><entry t=\"I0901b550675c11da90ebf04471783734\"/></test>";
		String expectedResult = "<test><entry t=\"I0901b550675c11da90ebf04471783734\"/></test>";
		
		testHelper(xmlTestStr, expectedResult);
	}
}
