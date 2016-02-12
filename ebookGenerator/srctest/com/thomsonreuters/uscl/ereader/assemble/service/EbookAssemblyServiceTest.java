/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.assemble.service;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarInputStream;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.thomsonreuters.uscl.ereader.assemble.exception.EBookAssemblyException;

/**
 * Component tests for the eBookAssemblyService.
 * 
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 */
public class EbookAssemblyServiceTest 
{
	private File eBookDirectory;
	private File titleXml;
	private File eBook;

	private EBookAssemblyService assemblyService;
	
	@Before
	public void setUp() throws Exception 
	{
		this.assemblyService = new EBookAssemblyServiceImpl();
		
		eBook = File.createTempFile("pirate", "ship");
		eBookDirectory = new File(eBook.getParentFile(), "eBookDirectory");
		eBookDirectory.mkdirs();
		titleXml = new File(eBookDirectory, "title.xml");
				
		OutputStream outputStream = new FileOutputStream(titleXml);
		outputStream.write("<title/>".getBytes());
		outputStream.flush();
		outputStream.close();
	}
	
	@After
	public void tearDown() throws Exception 
	{
		FileUtils.deleteQuietly(eBookDirectory);
		FileUtils.deleteQuietly(eBook);
	}
	
	@Test
	public void testAssembleEBookProtectedFile() throws Exception {
		File writeProtected = new File("WriteProtected");
		writeProtected.setWritable(false);
		try{
			assemblyService.assembleEBook(eBookDirectory, writeProtected);
			FileUtils.deleteQuietly(writeProtected);
			//fail("Should throw EBookAssemblyException");
		} catch (EBookAssemblyException e){
			FileUtils.deleteQuietly(writeProtected);
			//expected exception
			e.printStackTrace();
		}
	}
	
	@Test
	public void testAssembleHappyPath () throws Exception 
	{	
		assemblyService.assembleEBook(eBookDirectory, eBook);
		
		TarInputStream tarInputStream = new TarInputStream (new GZIPInputStream( new FileInputStream(eBook)));
		
		try 
		{
			TarEntry entry = tarInputStream.getNextEntry();
			assertTrue("eBookDirectory".equals(entry.getName()));
			entry = tarInputStream.getNextEntry();
			assertTrue("eBookDirectory/title.xml".equals(entry.getName()));
		}
		finally 
		{
			tarInputStream.close();
		}
	}

	@Test
	public void testAssembleFailsDueToNullDirectory () throws Exception 
	{	
		try 
		{
			assemblyService.assembleEBook(null, eBook);
			fail("An IllegalArgumentException should have been thrown!");
		}
		catch (IllegalArgumentException e)
		{
			//expected result
		}
	}

	@Test
	public void testAssembleFailsDueToFilePassedAsInputDirectory () throws Exception 
	{	
		File mockFile = EasyMock.createMock(File.class);
		EasyMock.expect(mockFile.isDirectory()).andReturn(Boolean.FALSE);
		EasyMock.replay(mockFile);
		try
		{
			assemblyService.assembleEBook(mockFile, eBook);
			fail("An IllegalArgumentException should have been thrown!");
		}
		catch (IllegalArgumentException e)
		{
			//expected result
		}
		EasyMock.verify(mockFile);
	}
	
	@Test
	public void testAssembleFailsDueToNullOutputFile () throws Exception 
	{	
		try 
		{
			assemblyService.assembleEBook(eBookDirectory, null);
			fail("An IllegalArgumentException should have been thrown!");
		}
		catch (IllegalArgumentException e)
		{
			//expected result
		}
	}

	@Test
	public void testAssembleFailsDueToDirectoryPassedAsOutputFile () throws Exception 
	{	
		File mockFile = EasyMock.createMock(File.class);
		EasyMock.expect(mockFile.isDirectory()).andReturn(Boolean.TRUE);
		EasyMock.replay(mockFile);
		try
		{
			assemblyService.assembleEBook(eBookDirectory, mockFile);
			fail("An IllegalArgumentException should have been thrown!");
		}
		catch (IllegalArgumentException e)
		{
			//expected result
		}
		EasyMock.verify(mockFile);
	}
}
