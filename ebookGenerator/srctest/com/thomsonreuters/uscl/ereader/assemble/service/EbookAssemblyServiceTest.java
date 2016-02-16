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
		File writeProtected = new File(eBookDirectory, "WriteProtected");
		writeProtected.setReadOnly();
		try{
			assemblyService.assembleEBook(eBookDirectory, writeProtected);
			fail("Should throw EBookAssemblyException");
		} catch (EBookAssemblyException e){
			//expected exception
			e.printStackTrace();
		} finally {
			FileUtils.deleteQuietly(writeProtected);
		}
	}
	
	@Test
	public void testGetLargestContent() throws Exception {
		String loremIpsum = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed"
				+ " do eiusmod tempor incididunt ut labore et dolore magna aliqua."
				+ " Ut enim ad minim veniam, quis nostrud exercitation ullamco"
				+ " laboris nisi ut aliquip ex ea commodo consequat. Duis aute"
				+ " irure dolor in reprehenderit in voluptate velit esse cillum"
				+ " dolore eu fugiat nulla pariatur. Excepteur sint occaecat"
				+ " cupidatat non proident, sunt in culpa qui officia deserunt"
				+ " mollit anim id est laborum.";
		File temp4 = new File(eBookDirectory, "temp4.txt");
		FileOutputStream out = new FileOutputStream(temp4);
		out.write((loremIpsum+loremIpsum).getBytes());
		out.close();
		File temp2 = new File(eBookDirectory, "temp2.txt");
		out = new FileOutputStream(temp2);
		out.write(loremIpsum.getBytes());
		out.close();
		File temp3 = new File(eBookDirectory, "temp3.dat");
		out = new FileOutputStream(temp3);
		out.write(loremIpsum.getBytes());
		out.close();
		assertTrue(temp4.length() == (assemblyService.getLargestContent(eBookDirectory.getAbsolutePath(), ".txt")));
		//assertTrue(temp4.length() == (assemblyService.getLargestContent(eBookDirectory.getAbsolutePath(), ".dat,.txt")));
		
		File temp1 = new File(eBookDirectory, "temp1.png");
		out = new FileOutputStream(temp1);
		out.write((loremIpsum+loremIpsum+loremIpsum).getBytes());
		out.flush();
		out.close();
		out = null;
		assertTrue(temp1.length() == (assemblyService.getLargestContent(eBookDirectory.getAbsolutePath(), ".txt,.png")));
		// the program is very resistant to actually deleting these files.
		// not fully deleting these files here causes testAssembleHappyPath() to fail
		
		//temp1.delete();
		//temp2.delete();
		//temp3.delete();
		//temp4.delete();
		FileUtils.deleteQuietly(temp1);
		FileUtils.deleteQuietly(temp2);
		FileUtils.deleteQuietly(temp3);
		FileUtils.deleteQuietly(temp4);
		//temp1 = null;
		//temp2 = null;
		//temp3 = null;
		//temp4 = null;
		//System.gc();
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
