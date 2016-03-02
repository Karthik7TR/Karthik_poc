/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.format.service;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentCopyright;
import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentCurrency;
import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;
import com.thomsonreuters.uscl.ereader.ioutil.FileExtensionFilter;
import com.thomsonreuters.uscl.ereader.ioutil.FileHandlingHelper;

/**
 * Test various XML preprocessing test scenarios.
 *
 * @author <a href="mailto:zack.farrell@thomsonreuters.com">Zack Farrell</a> uc209819
 */
public class XMLPreprocessServiceTest {
	
	private XMLPreprocessServiceImpl preprocessService;
	private File tempRootDir;
	private File tempXMLfile;
	
	private File srcDir;
	private File targetDir;
	private boolean isFinalStage;
	private List<DocumentCopyright> copyrights;
	private List<DocumentCurrency> currencies;

	
	@Before
	public void setup(){
		this.preprocessService = new XMLPreprocessServiceImpl();
    	FileExtensionFilter filter = new FileExtensionFilter();
    	filter.setAcceptedFileExtensions(new String[]{"xml"});
		FileHandlingHelper helper= new FileHandlingHelper();
		helper.setFilter(filter);
		this.preprocessService.setfileHandlingHelper(helper);
		
		this.tempRootDir = new File(System.getProperty("java.io.tmpdir")+"\\EvenMoreTemp");
		this.tempRootDir.mkdir();
		
		this.srcDir = new File(tempRootDir.getAbsolutePath(), "Source_Directory");
		this.srcDir.mkdir();
		makeFile(srcDir, "temp1.xml",
					"<test><include.currency n-include_guid=\"123456789\">This is a currency</include.currency></test>");
		makeFile(srcDir, "temp2.html",
					"HTML stream goes here");		
		this.tempXMLfile = makeFile(srcDir, "temp3.xml",
					"<body><test><include.copyright n-include_guid=\"987654321\">This is a copyright</include.copyright></test>" +
					"<test><include.currency n-include_guid=\"123456789\">This is a currency</include.currency></test></body>");
		
		this.targetDir = new File(tempRootDir.getAbsolutePath(), "Target_Directory");
		this.targetDir.mkdir();
		
		this.isFinalStage = true;
		
		DocumentCopyright copyright = new DocumentCopyright();
		copyright.setCopyrightGuid("987654321");
		copyright.setNewText("Copyright");
		this.copyrights = new ArrayList<DocumentCopyright>();
		this.copyrights.add(copyright);
		
		DocumentCurrency currency = new DocumentCurrency();
		currency.setCurrencyGuid("123456789");
		currency.setNewText("Currency");
		this.currencies = new ArrayList<DocumentCurrency>();
		this.currencies.add(currency);
		

	}
	
	@After
	public void tearDown() throws Exception{
	/*  recursively deletes the root directory, and all its subdirectories and files  */
		FileUtils.deleteDirectory(tempRootDir);
	}
	
	/** makeFile( File directory, String name, String content )
	 * 		helper method to streamline file creation
	 * @param directory		Location the new file will be created in
	 * @param name			Name of the new file
	 * @param content		Content to be written into the new file
	 * @return			returns a File object directing to the new file
	 * 					returns null if any errors occur
	 */
	private File makeFile(File directory, String name, String content)
	{
		try{
			File file = new File(directory, name);
			file.createNewFile();
			FileOutputStream out = new FileOutputStream(file);
			out.write(content.getBytes());
			out.flush();
			out.close();
			return file;
		}catch(Exception e){
			return null;
		}
	}
	
	/**
	 * XMLPreprocesService should take a source directory with xml files and perform the first 
	 * step necessary to transform them into html files. The resulting "preprocess" files are
	 * created in the target directory
	 */
	@Test
	public void TestXMLPreprocessServiceHappyPath()
	{
		int numDocs = -1;
		boolean thrown = false;
		
		try{
			numDocs = preprocessService.transformXML(srcDir, targetDir, isFinalStage, copyrights, currencies);
		}catch(Exception e){
			//e.printStackTrace();
			thrown = true;
		}
		assertTrue(!thrown);
		assertTrue(numDocs == 2);
		
		File preprocess1 = new File(targetDir.getAbsolutePath(), "temp1.preprocess");
		File preprocess2 = new File(targetDir.getAbsolutePath(), "temp3.preprocess");
		assertTrue(preprocess1.exists());
		assertTrue(preprocess2.exists());
	}
	
	@Test
	public void TestXMLPreprocessBadSourceDir() throws Exception
	{
		boolean thrown = false;
		
		try{	/*  null source directory  */
			preprocessService.transformXML(null, targetDir, isFinalStage, copyrights, currencies);
		}catch(IllegalArgumentException e){
			/*  expected exception  */
			//e.printStackTrace();
			thrown = true;
		}
		assertTrue(thrown);
		thrown = false;
		
		try{	/*  file as a source directory  */
			preprocessService.transformXML(tempXMLfile, targetDir, isFinalStage, copyrights, currencies);
		}catch(IllegalArgumentException e){
			/*  expected exception  */
			//e.printStackTrace();
			thrown = true;
		}
		assertTrue(thrown);
		thrown = false;
	}
	
	/**
	 * the method should be able to handle a non-existent target directory by
	 * creating it on the fly, assuming its parent directory is real
	 */
	@Test
	public void TestXMLPreprocessBadTargetDir()
	{
		int numDocs = -1;
		boolean thrown = false;
		try{	/*  targetDir does not exist  */
			targetDir = new File(tempRootDir.getAbsolutePath(), "not_real");
			numDocs = preprocessService.transformXML(srcDir, targetDir, isFinalStage, copyrights, currencies);
		}catch(Exception e){
			//e.printStackTrace();
			thrown = true;
		}
		assertTrue(!thrown);
		assertTrue(numDocs == 2);
		thrown = false;
		
	}
	
	@Test
	public void TestXMLPreprocessBadXML()
	{
		boolean thrown = false;
		srcDir = new File(tempRootDir.getAbsolutePath(), "badXML");
		srcDir.mkdir();
		
		try{	/*  source directory with no xml files  */
			preprocessService.transformXML(srcDir, targetDir, isFinalStage, copyrights, currencies);
		}catch(EBookFormatException e){
			/*  expected exception  */
			//e.printStackTrace();
			thrown = true;
		}
		assertTrue(thrown);
		thrown = false;
		
		makeFile(srcDir, "bad1.xml", "This is totally an file with xml format");
		makeFile(srcDir, "bad2.xml", "Excellent xml found in here");
		makeFile(srcDir, "bad3.html", "okay, this isn't even an xml file");
		
		try{	/*  source directory with bad xml files  */
			preprocessService.transformXML(srcDir, targetDir, isFinalStage, copyrights, currencies);
		}catch(EBookFormatException e){
			/*  expected exception  */
			//e.printStackTrace();
			thrown = true;
		}
		assertTrue(thrown);
	}
	
	@Test
	public void TestExtraCopyrightCurrencyInfo()
	{
		boolean thrown = false;
		
		DocumentCurrency currency = new DocumentCurrency();
		currency.setCurrencyGuid("111111111");
		currency.setNewText("Monay");
		this.currencies.add(currency);
		
		
		try{	/*  extra currency info  */
			preprocessService.transformXML(srcDir, targetDir, isFinalStage, copyrights, currencies);
		}catch(EBookFormatException e){
			/*  expected exception  */
			//e.printStackTrace();
			thrown = true;
		}
		assertTrue(thrown);
		thrown = false;
		
		DocumentCopyright copyright = new DocumentCopyright();
		copyright.setCopyrightGuid("222222222");
		copyright.setNewText("Copyleft");
		this.copyrights.add(copyright);
		
		try{	/*  extra copyright info  */
			preprocessService.transformXML(srcDir, targetDir, isFinalStage, copyrights, currencies);
		}catch(EBookFormatException e){
			/*  expected exception  */
			//e.printStackTrace();
			thrown = true;
		}
	}
}