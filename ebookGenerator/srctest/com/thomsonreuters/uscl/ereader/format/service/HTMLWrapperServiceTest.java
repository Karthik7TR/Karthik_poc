/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.format.service;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;
import com.thomsonreuters.uscl.ereader.ioutil.FileExtensionFilter;
import com.thomsonreuters.uscl.ereader.ioutil.FileHandlingHelper;


/**
 * JUnit test for the Transformer service.
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public class HTMLWrapperServiceTest 
{
    /**
     * The service being tested, injected by Spring.
     */
    @Autowired
    protected HTMLWrapperServiceImpl htmlWrapperService;
    
    @Rule
    public TemporaryFolder testFiles = new TemporaryFolder();
    
    protected OutputStream outputStreamTOCTests = null;
    
    protected Map<String, String[]> anchorMap;
    
    protected File emptyTransDir;
    protected File transDir;
    
    protected File htmlDir;
    protected File nonExistHtmlDir;
    
    protected File transformedFile;
    protected File transformedFile2;
    
    protected File docToTocMapFile;
    
    
    /**
     * Create the Transformer Service and initialize test variables.
     * 
     * @throws Exception issues encountered during set up
     */
    @Before
    public void setUp() throws Exception 
    {
    	outputStreamTOCTests = null;
    	
    	FileExtensionFilter filter = new FileExtensionFilter();
    	filter.setAcceptedFileExtensions(new String[]{".transformed"});
    	FileHandlingHelper ioHelper = new FileHandlingHelper();
    	ioHelper.setFilter(filter);
    	
    	//create instance of service
    	htmlWrapperService = new HTMLWrapperServiceImpl();
    	
    	htmlWrapperService.setfileHandlingHelper(ioHelper);    	
    	
    	//set up Transformed directories
    	emptyTransDir = testFiles.newFolder("WrapperTestEmptyTransDir");
    	emptyTransDir.mkdir();
    	
    	transDir = testFiles.newFolder("WrapperTestTransformed");
    	transDir.mkdir();
    	
    	htmlDir = testFiles.newFolder("WrapperTestHTML");
    	htmlDir.mkdir();
    	
    	nonExistHtmlDir = new File("WrapperTestNonExistHTML");
    	
    	docToTocMapFile = testFiles.newFile("testDocToTocMapFile");
    	docToTocMapFile.createNewFile();
    	
    	String htmStr = "<div class=\"co_documentHead\">";
    	String htmStr2 = "</div>";

		anchorMap = new HashMap<String, String[]>();
		anchorMap.put("transFile", new String[]{"anchor1"});
		anchorMap.put("transFile2", new String[]{"anchor2", "anchor3"});
    	
    	transformedFile = new File(transDir, "transFile.transformed");
    	OutputStream outputStream = new FileOutputStream(transformedFile);
		outputStream.write(htmStr.getBytes());
		outputStream.write("1".getBytes());
		outputStream.write(htmStr2.getBytes());
		outputStream.flush();
		outputStream.close();
		
    	transformedFile2 = new File(transDir, "transFile2.transformed");
    	OutputStream outputStream2 = new FileOutputStream(transformedFile2);
    	outputStream2.write(htmStr.getBytes());
    	outputStream2.write("2".getBytes());
    	outputStream2.write(htmStr2.getBytes());
    	outputStream2.flush();
    	outputStream2.close();
		
    	File txtFile = new File(transDir, "txtFile.txt");
    	txtFile.createNewFile();
    	File htmlFile = new File(transDir, "htmlFile.html");
    	htmlFile.createNewFile();
    }
    
    @After
    public void tearDown() throws IOException
    {
		if (outputStreamTOCTests != null)
		{
			outputStreamTOCTests.close();
		}
    }
    
    /**
     * Verifies that an IllegalArgumentException is thrown when no parameters are specified.
     * 
     */
    @Test(expected = IllegalArgumentException.class)
    public void testNullArgsAddHTMLWrapper()
    {
    	try
    	{
        	htmlWrapperService.addHTMLWrappers(null, null, docToTocMapFile);    		
    	}
    	catch(EBookFormatException e)
    	{
    		fail("EBookFormatException raised instead of IllegalArgumentException");
    	}
    }
    
    /**
     * Verifies that an IllegalArgumentException is thrown when a file is passed in instead of source directory.
     * 
     */
    @Test(expected = IllegalArgumentException.class)
    public void testFileArgAddHTMLWrapper()
    {
    	try
    	{
        	htmlWrapperService.addHTMLWrappers(transformedFile, htmlDir, docToTocMapFile);    		
    	}
    	catch(EBookFormatException e)
    	{
    		fail("EBookFormatException raised instead of IllegalArgumentException");
    	}
    }
    
    /**
     * Verifies that an IllegalArgumentException is thrown when an non existing Doc to Toc mapping file 
     * is passed.
     * 
     */
    @Test(expected = IllegalArgumentException.class)
    public void testNullDocToTocFileArgAddHTMLWrapper()
    {
    	try
    	{
        	htmlWrapperService.addHTMLWrappers(transformedFile, htmlDir, null);    		
    	}
    	catch(EBookFormatException e)
    	{
    		fail("EBookFormatException raised instead of IllegalArgumentException");
    	}
    }
    
    /**
     * Verifies that an IllegalArgumentException is thrown when an non existing Doc to Toc mapping file 
     * is passed.
     * 
     */
    @Test(expected = IllegalArgumentException.class)
    public void testBadDocToTocFileArgAddHTMLWrapper()
    {
    	try
    	{
    		File testFile = new File("NonExisting");
        	htmlWrapperService.addHTMLWrappers(transformedFile, htmlDir, testFile);    		
    	}
    	catch(EBookFormatException e)
    	{
    		fail("EBookFormatException raised instead of IllegalArgumentException");
    	}
    }
    
    /**
     * Verify that wrapper method creates a file in the target directory and that the file is larger than the original.
     * 
     */
    @Test
    public void testWrapFile()
    {
    	try
    	{
    		assertEquals(0, htmlDir.listFiles().length);
    		htmlWrapperService.addHTMLWrapperToFile(transformedFile, htmlDir, anchorMap);
    		File[] htmlFiles = htmlDir.listFiles();
    		assertEquals(1, htmlFiles.length);
    		
    		assertTrue(transformedFile.length() < htmlFiles[0].length());
    	}
    	catch(EBookFormatException e)
    	{
    		fail("EBookFormatException raised when it was not expected");
    	}
    }
    
    /**
     * Verify that wrapper method when a non-existing .transformed file is passed in.
     * 
     */
    @Test
    public void testWrapFileWithNonExistingTransFile()
    {
    	try
    	{
    		File test = new File(transDir, "test.transformed");

    		htmlWrapperService.addHTMLWrapperToFile(test, htmlDir, anchorMap);

    		fail("EBookFormatException was not raised when non existing file was passed in.");
    	}
    	catch(EBookFormatException e)
    	{

    	}
    }
    
    /**
     * Verify that all .transformed files that are in a directory are processed.
     * 
     */
    @Test
    public void testWrapAllFilesInDirectory() throws IOException
    {
    	try
    	{
    		OutputStream outputStream = new FileOutputStream(docToTocMapFile);
    		anchorMap.put("transFile", new String[]{"anchor1"});
    		anchorMap.put("transFile2", new String[]{"anchor2", "anchor3"});
    		outputStream.write("transFile,anchor1|".getBytes());
    		outputStream.write("\n".getBytes());
    		outputStream.write("transFile2,anchor2|anchor3|".getBytes());
    		outputStream.flush();
    		outputStream.close();
    		
    		assertEquals(2, htmlWrapperService.addHTMLWrappers(transDir, htmlDir, docToTocMapFile));
    	}
    	catch(EBookFormatException e)
    	{
    		fail("EBookFormatException raised when it was not expected: " + e.getMessage());
    	}
    }
    
    @Test
	public void testMapGenerationFromTOCFileNoTOCOnThirdDoc() throws IOException, EBookFormatException
	{	
		File testFile = new File("test");
		try
		{
			testFile = testFiles.newFile("DocTOCTestFile1.txt");
			OutputStream outputStream = new FileOutputStream(testFile);
			outputStream.write("I1f5a5aa17c8f11da9de6e47d6d5aa7a1,TOC1|TOC2|".getBytes());
			outputStream.write("\n".getBytes());
			outputStream.write("I1f5a81a27c8f11da9de6e47d6d5aa7a2,TOC3|".getBytes());
			outputStream.write("\n".getBytes());
			outputStream.write("I1f5a81a27c8f11da9de6e47d6d5aa7a3,".getBytes());
			outputStream.write("\n".getBytes());
			outputStream.write("I1f5a81a27c8f11da9de6e47d6d5aa7a4,TOC4|".getBytes());
			outputStream.flush();
			outputStream.close();
			
			Map<String, String[]> docToTocGuidMap = new HashMap<String, String[]>();
			
			htmlWrapperService.readTOCGuidList(testFile, docToTocGuidMap);
			
			fail("Expected EBookFormatException to be thrown since no TOC guids have been " +
					"associated with one or more DOC guids.");
		}
		catch (EBookFormatException e)
		{
			assertEquals("No TOC guid was found for the 3 document. " +
							"Please verify that each document GUID in the following file has " +
							"at least one TOC guid associated with it: " + testFile.getAbsolutePath(), 
							e.getMessage());
		}
	}
	
	@Test
	public void testMapGenerationFromTOCFileNoTOCAllDocs() throws IOException, EBookFormatException
	{	
		File testFile = new File("test");
		try
		{
			testFile = testFiles.newFile("DocTOCTestFile1.txt");
			OutputStream outputStream = new FileOutputStream(testFile);
			outputStream.write("Iff5a5aa17c8f11da9de6e47d6d5aa7a5,".getBytes());
			outputStream.write("\n".getBytes());
			outputStream.write("Iff5a81a27c8f11da9de6e47d6d5aa7a5,".getBytes());
			outputStream.flush();
			outputStream.close();
			
			Map<String, String[]> docToTocGuidMap = new HashMap<String, String[]>();
			
			htmlWrapperService.readTOCGuidList(testFile, docToTocGuidMap);
			
			fail("Expected EBookFormatException to be thrown since no TOC guids have been " +
					"associated with one or more DOC guids.");
		}
		catch (EBookFormatException e)
		{
			assertEquals("No TOC guid was found for the 1 document. " +
					"Please verify that each document GUID in the following file has " +
					"at least one TOC guid associated with it: " + testFile.getAbsolutePath(), 
					e.getMessage());
		}
	}
	
	@Test
	public void testMapGenerationFromTOCFile() throws IOException, EBookFormatException
	{	
		File testFile = testFiles.newFile("DocTOCTestFile2.txt");
		outputStreamTOCTests = new FileOutputStream(testFile);
		outputStreamTOCTests.write("Iff5a5aa17c8f11da9de6e47d6d5aa7a1,TocGuid1|TocGuid2|".getBytes());
		outputStreamTOCTests.write("\n".getBytes());
		outputStreamTOCTests.write("Iff5a81a27c8f11da9de6e47d6d5aa7a2,TocGuid3|TocGuid4|TocGuid5|".getBytes());
		outputStreamTOCTests.write("\n".getBytes());
		outputStreamTOCTests.write("Iff5a81a27c8f11da9de6e47d6d5aa7a3,TocGuid6|".getBytes());
		outputStreamTOCTests.flush();
		outputStreamTOCTests.close();
		
		Map<String, String[]> docToTocGuidMap = new HashMap<String, String[]>();
		
		htmlWrapperService.readTOCGuidList(testFile, docToTocGuidMap);
		
		assertTrue(docToTocGuidMap.containsKey("Iff5a5aa17c8f11da9de6e47d6d5aa7a1"));	
		assertTrue(docToTocGuidMap.containsKey("Iff5a81a27c8f11da9de6e47d6d5aa7a2"));	
		assertTrue(docToTocGuidMap.containsKey("Iff5a81a27c8f11da9de6e47d6d5aa7a3"));	
		
		String[] testValue = docToTocGuidMap.get("Iff5a81a27c8f11da9de6e47d6d5aa7a2");
		assertEquals(3, testValue.length);
		assertEquals("TocGuid3", testValue[0]);
		assertEquals("TocGuid4", testValue[1]);
		assertEquals("TocGuid5", testValue[2]);
	}
	
	@Test
	public void testMapGenerationFromTOCFileBadFile() throws IOException, EBookFormatException
	{	
		try
		{
			File testFile = new File("No file");
			
			Map<String, String[]> docToTocGuidMap = new HashMap<String, String[]>();
			
			htmlWrapperService.readTOCGuidList(testFile, docToTocGuidMap);
			
			fail("Expected EBookFormatException to be thrown since none existing file" +
					" was passed in.");
		}
		catch (EBookFormatException e)
		{
			
		}
	}
	
	@Test
	public void testAnchorCreationGuidNotFound()
	{
		String guid = "TestNotFound";
		try
		{
			Map<String, String[]> anchorMap = new HashMap<String, String[]>();
			anchorMap.put("Test123", new String[]{"Toc1"});
			anchorMap.put("Test222", new String[]{"Toc2", "Toc3"});
			
			StringBuffer anchorStr = new StringBuffer();
			
			htmlWrapperService.generateAnchorStream(anchorMap, guid, anchorStr);
			
			fail("Expected EBookFormatException to be thrown since document GUID that is being searched" +
					" for is not in the supplied map.");
		}
		catch (EBookFormatException e)
		{
			assertEquals("No TOC anchor references were found for the following GUID: " + guid, e.getMessage());
		}
	}
}
