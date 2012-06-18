/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.format.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Transformer;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

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
    protected File metaDir;
    protected File imgMetaDir;
    protected File transDir;
    
    protected File xmlFile;
    protected File xmlFile2;
    protected File metaFile;
    protected File metaFile2;
    protected File imgMetaFile;
    protected File imgMetaFile2;
    
    protected String titleId;
    
    protected Long jobId;
    
    @Rule
    public TemporaryFolder testFiles = new TemporaryFolder();
    
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
    	transService.setfileHandlingHelper(ioHelper);
    	
    	//setup static title and job identifies
    	titleId = "uscl/cr/unitTestTitle";
    	jobId = 23L;
    	
    	//set up XML directories
    	emptyXMLDir = testFiles.newFolder("TransformerTestEmptyXMLDir");
    	
    	xmlDir = testFiles.newFolder("TransformerTestXML");
    	
    	metaDir = testFiles.newFolder("TransformerTestMetadata");
    	imgMetaDir = testFiles.newFolder("TransformerTestImageMetadata");
    	
    	transDir = testFiles.newFolder("TransformerTestTransformed");
    	
    	String xmlStr = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><n-docbody>";
    	String xmlStr2 = "</n-docbody>";
    	
    	xmlFile = new File(xmlDir, "xmlFile1.xml");
    	OutputStream outputStream = new FileOutputStream(xmlFile);
		outputStream.write(xmlStr.getBytes());
		outputStream.write("1".getBytes());
		outputStream.write(xmlStr2.getBytes());
		outputStream.flush();
		outputStream.close();
		
    	xmlFile2 = new File(xmlDir, "xmlFile2.xml");
    	OutputStream outputStream2 = new FileOutputStream(xmlFile2);
    	outputStream2.write(xmlStr.getBytes());
    	outputStream2.write("2".getBytes());
    	outputStream2.write(xmlStr2.getBytes());
    	outputStream2.flush();
    	outputStream2.close();
    	
    	metaFile = new File(metaDir, "collection-xmlFile1.xml");
    	OutputStream outputMeta = new FileOutputStream(metaFile);
    	outputMeta.write("<n-metadata></n-metadata>".getBytes());
    	outputMeta.flush();
    	outputMeta.close();
    	
    	metaFile2 = new File(metaDir, "collection-xmlFile2.xml");
    	OutputStream outputMeta2 = new FileOutputStream(metaFile2);
    	outputMeta2.write("<n-metadata></n-metadata>".getBytes());
    	outputMeta2.flush();
    	outputMeta2.close();
    	
    	imgMetaFile = new File(imgMetaDir, "xmlFile1.imgMeta");
    	OutputStream outputImgMeta = new FileOutputStream(imgMetaFile);
    	outputImgMeta.write("<ImageMetadata></ImageMetadata>".getBytes());
    	outputImgMeta.flush();
    	outputImgMeta.close();
    	imgMetaFile2 = new File(imgMetaDir, "xmlFile2.imgMeta");
    	OutputStream outputImgMeta2 = new FileOutputStream(imgMetaFile2);
    	outputImgMeta2.write("<ImageMetadata></ImageMetadata>".getBytes());
    	outputImgMeta2.flush();
    	outputImgMeta2.close();
		
    	File txtFile = new File(xmlDir, "txtFile.txt");
    	txtFile.createNewFile();
    	File htmlFile = new File(xmlDir, "htmlFile.html");
    	htmlFile.createNewFile();
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
    		transService.transformXMLDocuments(null, null, null, null, null, null, false);
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
    		transService.transformXMLDocuments(xmlFile, metaDir, imgMetaDir, transDir, titleId, jobId, false);
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
    		Map<String, Transformer> xsltCache = new HashMap<String, Transformer>();
    		transService.transformFile(xmlFile, metaDir, imgMetaDir, transDir, titleId, jobId, xsltCache, false);
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
    		assertEquals(2, transService.transformXMLDocuments(xmlDir, metaDir, imgMetaDir, transDir, titleId, jobId, false));
    	}
    	catch(EBookFormatException e)
    	{
    		fail("EBookFormatException raised instead of transforming XML file");
    	}
    }
}
