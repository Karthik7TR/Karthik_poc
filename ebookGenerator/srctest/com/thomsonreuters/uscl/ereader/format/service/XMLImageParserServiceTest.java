/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.format.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;
import com.thomsonreuters.uscl.ereader.ioutil.FileExtensionFilter;
import com.thomsonreuters.uscl.ereader.ioutil.FileHandlingHelper;

/**
 * Test the Image Parser Service.
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public class XMLImageParserServiceTest {

    @Rule
    public TemporaryFolder testFiles = new TemporaryFolder();
    
    protected XMLImageParserServiceImpl imgParserService;
    
    protected List<String> guidList;
    
    protected File xmlDir;
    
    protected File imgListFile;
    protected File xmlFile;
    protected File xmlFile2;
    protected File invalidXmlFile;
    protected File emptyXmlFile;
    
	private String xmlText = "<primary.notes><para><bop /><bos /><paratext>The Sentencing Table " +
			"used to determine the guideline range follows:</paratext>" +
			"<image.block><image.link target=\"I1d163990094d11e085f5891ac64a9905\" /></image.block>" +
			"<image.block><image.link target=\"I1d163990094d11e085f5891ac64a9906\" /></image.block>" +
			"<eos /><eop /></para></primary.notes>";
	
	private String xmlText2 = "<primary.notes><para><bop /><bos /><paratext>The Sentencing Table " +
			"used to determine the guideline range follows:</paratext>" +
			"<image.block><image.link target=\"I1d163990094d11e085f5891ac64a9905\" /></image.block>" +
			"<image.block><image.link target=\"I1d163990094d11e085f5891ac64a9906\" /></image.block>" +
			"<eos /><eop /></para></primary.notes>";
	
	private String invalidXml = "<primary.notes><para><bop /><bos /><paratext>The Sentencing Table " +
			"used to determine the guideline range follows:</paratext>";
    
    @Before
    public void setUp() throws Exception
    {
    	FileExtensionFilter filter = new FileExtensionFilter();
    	filter.setAcceptedFileExtensions(new String[]{".xml"});
    	FileHandlingHelper ioHelper = new FileHandlingHelper();
    	ioHelper.setFilter(filter);
    	
    	imgParserService = new XMLImageParserServiceImpl();
    	imgParserService.setfileHandler(ioHelper);
    	
    	imgListFile = testFiles.newFile("jUnitImageListFile");
    	
    	xmlDir = testFiles.newFolder("XMLImageParserTest");
    	emptyXmlFile = testFiles.newFile("emptyXMLFile.xml");
    	
    	xmlFile = new File(xmlDir, "xmlTestFile.xml");
    	OutputStream outputStream = new FileOutputStream(xmlFile);
		outputStream.write(xmlText.getBytes());
		outputStream.flush();
		outputStream.close();
    	
    	xmlFile2 = new File(xmlDir, "xmlTestFile2.xml");
    	OutputStream outputStream2 = new FileOutputStream(xmlFile2);
		outputStream2.write(xmlText2.getBytes());
		outputStream2.flush();
		outputStream2.close();
		
		invalidXmlFile = testFiles.newFile("invalidXmlTestFile.xml");
    	OutputStream outputStream3 = new FileOutputStream(invalidXmlFile);
		outputStream3.write(invalidXml.getBytes());
		outputStream3.flush();
		outputStream3.close();
    	
    	guidList = new ArrayList<String>();
		guidList.add("I5d463990094d11e085f5891ac64a9905");
		guidList.add("I8A302FE4920F47B00079B5381C71638B");
    }
    
    @Test
    public void testGuidListGenerationFromDirectoryXMLParsing()
    {
    	try
    	{
    		long initFileSize = imgListFile.length();
    		imgParserService.generateImageList(xmlDir, imgListFile);
    		assertTrue(initFileSize < imgListFile.length());
    	}
    	catch(EBookFormatException e)
    	{
    		fail("Encountered EBookFormatException when not expected.");
    	}
    }
    
    @Test
    public void testFileParsing()
    {
    	try
    	{
    		imgParserService.parseXMLFile(xmlFile, guidList);
    		assertEquals(4, guidList.size());
    	}
    	catch(EBookFormatException e)
    	{
    		fail("Encountered EBookFormatException when not expected.");
    	}
    }
    
    @Test
    public void testFileParsingEmptyXMLFile()
    {
    	try
    	{
    		imgParserService.parseXMLFile(emptyXmlFile, guidList);
    		fail("EBookFormatException was not thrown for empty XML file and it was expected.");
    	}
    	catch(EBookFormatException e)
    	{
    		
    	}
    }
    
    @Test
    public void testFileParsingInvalidXMLFile()
    {
    	try
    	{
    		imgParserService.parseXMLFile(invalidXmlFile, guidList);
    		fail("EBookFormatException was not thrown for invalid XML file and it was expected.");
    	}
    	catch(EBookFormatException e)
    	{
    		
    	}
    }
    
    @Test
    public void testListCreation()
    {
    	long initialSize = imgListFile.length();
    	try
    	{
        	imgParserService.createImageList(imgListFile, guidList);
        	long newSize = imgListFile.length();
        	assertTrue(initialSize < newSize);
    	}
    	catch(EBookFormatException e)
    	{
    		fail("Encountered EBookFormatException when not expected.");
    	}
    }
    
    @Test
    public void testListCreationNullGuid()
    {
    	try
    	{
    		guidList.add(null);
        	imgParserService.createImageList(imgListFile, guidList);
        	fail("EBookFormatException was not thrown for null GUID.");
    	}
    	catch(EBookFormatException e)
    	{
    		
    	}
    }
    
    @Test
    public void testListCreationShortGuid()
    {
    	try
    	{
    		guidList.add("I5d463990094d11e085f5891ac64a99"); //check 30 char GUID
        	imgParserService.createImageList(imgListFile, guidList);
        	fail("EBookFormatException was not thrown for short GUID.");
    	}
    	catch(EBookFormatException e)
    	{
    		
    	}
    }
    
    @Test
    public void testListCreationLongGuid()
    {
    	try
    	{
    		guidList.add("I5d463990094d11e085f5891ac64a9955555"); //check 35 char GUID
        	imgParserService.createImageList(imgListFile, guidList);
        	fail("EBookFormatException was not thrown for long GUID.");
    	}
    	catch(EBookFormatException e)
    	{
    		
    	}
    }
}
