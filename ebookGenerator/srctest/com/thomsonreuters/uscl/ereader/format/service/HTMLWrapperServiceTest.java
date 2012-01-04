/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.format.service;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;


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
    
    protected File emptyTransDir;
    protected File transDir;
    
    protected File htmlDir;
    protected File nonExistHtmlDir;
    
    protected File transformedFile;
    protected File transformedFile2;
    
    
    /**
     * Create the Transformer Service and initialize test variables.
     * 
     * @throws Exception issues encountered during set up
     */
    @Before
    public void setUp() throws Exception 
    {
    	//create instance of service
    	htmlWrapperService = new HTMLWrapperServiceImpl();
    	
    	//set up Transformed directories
    	emptyTransDir = testFiles.newFolder("WrapperTestEmptyTransDir");
    	emptyTransDir.mkdir();
    	
    	transDir = testFiles.newFolder("WrapperTestTransformed");
    	transDir.mkdir();
    	
    	htmlDir = testFiles.newFolder("WrapperTestHTML");
    	htmlDir.mkdir();
    	
    	nonExistHtmlDir = new File("WrapperTestNonExistHTML");
    	
    	String htmStr = "<div class=\"co_documentHead\">";
    	String htmStr2 = "</div>";
    	
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
    
    /**
     * Verifies that an IllegalArgumentException is thrown when no parameters are specified.
     * 
     */
    @Test(expected = IllegalArgumentException.class)
    public void testNullArgsAddHTMLWrapper()
    {
    	try
    	{
        	htmlWrapperService.addHTMLWrappers(null, null);    		
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
        	htmlWrapperService.addHTMLWrappers(transformedFile, htmlDir);    		
    	}
    	catch(EBookFormatException e)
    	{
    		fail("EBookFormatException raised instead of IllegalArgumentException");
    	}
    }
    
    /**
     * Verify EBookFormatException is raised if no transformed files are found in the specified source directory.
     * 
     */
    @Test
    public void testGetFilesEmptyDir()
    {
    	try
    	{
        	ArrayList<File> transFiles = new ArrayList<File>();
        	htmlWrapperService.getTransformedFiles(transFiles, emptyTransDir);
    		fail("EBookFormatException was not raised when it was expected");
    	}
    	catch(EBookFormatException e)
    	{
    		//Expecting exception to be thrown
    	}
    }
    
    /**
     * Verifies only the two .transformed files are retrieved from the four file passed in directory.
     * 
     */
    @Test
    public void testGetTransformedFiles()
    {
    	try
    	{
        	ArrayList<File> transFiles = new ArrayList<File>();
        	assertEquals(4, transDir.listFiles().length);
        	htmlWrapperService.getTransformedFiles(transFiles, transDir);
        	assertEquals(2, transFiles.size());
    	}
    	catch(EBookFormatException e)
    	{
    		fail("EBookFormatException raised when it was not expected");
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
    		htmlWrapperService.addHTMLWrapperToFile(transformedFile, htmlDir);
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

    		htmlWrapperService.addHTMLWrapperToFile(test, htmlDir);

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
    public void testWrapAllFilesInDirectory()
    {
    	try
    	{
    		assertEquals(2, htmlWrapperService.addHTMLWrappers(transDir, htmlDir));
    	}
    	catch(EBookFormatException e)
    	{
    		fail("EBookFormatException raised when it was not expected");
    	}
    }
}
