/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.format.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;
import com.thomsonreuters.uscl.ereader.ioutil.FileExtensionFilter;
import com.thomsonreuters.uscl.ereader.ioutil.FileHandlingHelper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * JUnit test for the Transformer service.
 * 
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public class TransformerServiceTest 
{
    protected TransformerServiceImpl transService;
    
    protected File emptyXMLDir;
    protected File xmlDir;
    protected File transDir;
    
    protected File xmlFile;
    protected File xmlFile2;
    
    protected String titleId;
    
    protected Long jobId;
    
    /**
     * Create the Transformer Service and initialize test variables.
     * 
     * @throws Exception issues encountered during set up
     */
    @Before
    public void setUp() throws Exception 
    {
    	FileExtensionFilter filter = new FileExtensionFilter();
    	filter.setAcceptedFileExtensions(new String[]{".transformed"});
    	FileHandlingHelper ioHelper = new FileHandlingHelper();
    	ioHelper.setFilter(filter);
    	
    	//create instance of service
    	transService = new TransformerServiceImpl();
    	transService.setfileHandler(ioHelper);
    	
    	//setup static title and job identifies
    	titleId = "uscl/cr/unitTestTitle";
    	jobId = 23L;
    	
    	//set up XML directories
    	emptyXMLDir = new File("TransformerTestEmptyXMLDir");
    	emptyXMLDir.mkdir();
    	
    	xmlDir = new File("TransformerTestXML");
    	xmlDir.mkdir();
    	
    	transDir = new File("TransformerTestTransformed");
    	transDir.mkdir();
    	
    	String xmlStr = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><n-docbody>";
    	String xmlStr2 = "</n-docbody>";
    	
    	xmlFile = new File(xmlDir, "xmlFile1.xml");
    	OutputStream outputStream = new FileOutputStream(xmlFile);
		outputStream.write(xmlStr.getBytes());
		outputStream.write("1".getBytes());
		outputStream.write(xmlStr2.getBytes());
		outputStream.flush();
		outputStream.close();
		
    	File xmlFile2 = new File(xmlDir, "xmlFile2.xml");
    	OutputStream outputStream2 = new FileOutputStream(xmlFile2);
    	outputStream2.write(xmlStr.getBytes());
    	outputStream2.write("2".getBytes());
    	outputStream2.write(xmlStr2.getBytes());
    	outputStream2.flush();
    	outputStream2.close();
		
    	File txtFile = new File(xmlDir, "txtFile.txt");
    	txtFile.createNewFile();
    	File htmlFile = new File(xmlDir, "htmlFile.html");
    	htmlFile.createNewFile();
    }
    
    /**
     * Cleans up after the test any intermediate files that were created
     * 
     * @throws Exception issue encountered during tear down
     */
    @After
    public void tearDown() throws Exception
    {
    	FileUtils.deleteDirectory(xmlDir);
    	FileUtils.deleteDirectory(emptyXMLDir);
    	
    	FileUtils.deleteDirectory(transDir);
    }
    
    /**
     * Verifies that an IllegalArgumentException is thrown when no parameters are specified.
     * 
     */
    @Test(expected = IllegalArgumentException.class)
    public void testNullArgsTransformXMLDocuments()
    {
    	try
    	{
    		transService.transformXMLDocuments(null, null, null, null);
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
    public void testBadSourceDirTransformXMLDocuments()
    {
    	try
    	{
    		transService.transformXMLDocuments(xmlFile, transDir, titleId, jobId);
    	}
    	catch(EBookFormatException e)
    	{
    		fail("EBookFormatException raised instead of IllegalArgumentException " +
    				"when XML file instead of directory was passed in");
    	}
    }
    
    @Ignore
    @Test
    public void testTransformFileCreation()
    {
    	try
    	{
    		assertEquals(0, transDir.listFiles().length);
    		transService.transformFile(xmlFile, transDir, titleId, jobId);
    		assertEquals(1, transDir.listFiles().length);
    	}
    	catch(EBookFormatException e)
    	{
    		fail("EBookFormatException raised instead of transforming XML file");
    	}
    }
    
    @Ignore
    @Test
    public void testTransformAllXMLFiles()
    {
    	try
    	{
    		assertEquals(2, transService.transformXMLDocuments(xmlDir, transDir, titleId, jobId));
    	}
    	catch(EBookFormatException e)
    	{
    		fail("EBookFormatException raised instead of transforming XML file");
    	}
    }
}
