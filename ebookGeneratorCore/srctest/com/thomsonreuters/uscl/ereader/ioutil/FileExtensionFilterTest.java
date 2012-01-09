/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.ioutil;

import java.io.File;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.thomsonreuters.uscl.ereader.ioutil.FileExtensionFilter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Tests the FileExtensionFilter
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public class FileExtensionFilterTest {

    @Rule
    public TemporaryFolder testFiles = new TemporaryFolder();
    
	private FileExtensionFilter fileExtFilter;
	private final String testExtension = ".xml";
	private final String testExtension2 = ".txt";
	
    private File emptyDir;
    private File testDir;
    
    protected File xmlFile;
    protected File xmlFile2;
    
	@Before
	public void setUp() throws Exception
	{
		fileExtFilter = new FileExtensionFilter();
		
    	//set up Transformed directories
    	emptyDir = testFiles.newFolder("ExtFilterTestEmptyDir");
    	emptyDir.mkdir();
    	
    	testDir = testFiles.newFolder("ExtFilterTestDir");
    	testDir.mkdir();
    	
    	xmlFile = new File(testDir, "xmlFile1.xml");
    	xmlFile.createNewFile();
    	xmlFile2 = new File(testDir, "xmlFile2.xml");
    	xmlFile2.createNewFile();
    	
    	File txtFile = new File(testDir, "txtFile.txt");
    	txtFile.createNewFile();
    	File htmlFile = new File(testDir, "htmlFile.html");
    	htmlFile.createNewFile();
	}
	
	@Test(expected=RuntimeException.class)
	public void testNullAcceptableExtension()
	{
		fileExtFilter.accept(xmlFile);
	}
	
	@Test(expected=RuntimeException.class)
	public void testEmptyListAcceptableExtension()
	{
		fileExtFilter.setAcceptedFileExtensions(new String[]{});
		fileExtFilter.accept(xmlFile);
	}

	@Test
	public void testGetterAndSetter()
	{
		fileExtFilter.setAcceptedFileExtensions(new String[]{testExtension});
		String[] acceptableExt = fileExtFilter.getAcceptedFileExtensions();
		if (acceptableExt != null && acceptableExt.length == 1)
		{
			assertEquals(testExtension, acceptableExt[0]);
		}
		else
		{
			fail("Acceptable extension that was set did not get returned.");
		}
	}
	
	@Test
	public void testNoFileRetrieval()
	{
		fileExtFilter.setAcceptedFileExtensions(new String[]{testExtension});
		File[] fileList = emptyDir.listFiles(fileExtFilter);
		assertEquals(0, fileList.length);
	}
	
	@Test
	public void testXMLFileRetrieval()
	{
		fileExtFilter.setAcceptedFileExtensions(new String[]{testExtension});
		File[] noFilterList = testDir.listFiles();
		assertEquals(4, noFilterList.length);
		File[] filterFileList = testDir.listFiles(fileExtFilter);
		assertEquals(2, filterFileList.length);
	}
	
	@Test
	public void testMultipleExtFileRetrieval()
	{
		fileExtFilter.setAcceptedFileExtensions(new String[]{testExtension, testExtension2});
		File[] noFilterList = testDir.listFiles();
		assertEquals(4, noFilterList.length);
		File[] filterFileList = testDir.listFiles(fileExtFilter);
		assertEquals(3, filterFileList.length);
	}
}
