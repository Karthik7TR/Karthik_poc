package com.thomsonreuters.uscl.ereader.gather.util;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class ImageConverterTest {

	String formatName = "PNG";
	File workDir;

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	@Before
	public void setUp() {

	}
	
	@After
	public void tearDown() throws Exception 
	{
		FileUtils.deleteQuietly(workDir);
	}

//	@Test
//	public void testConvertBadTiffImg() throws Exception {
//		workDir = temporaryFolder.getRoot();
//		File outputImg = new File (workDir,"testing.png");
//		URL url = ImageConverterTest.class.getResource("EBook_Generator_WestLogo.tif");
//		File tiffFile = new File(url.toURI());
//		String inputImage = tiffFile.getAbsolutePath();
//		FileInputStream fis = new FileInputStream(inputImage);
//
//		ByteArrayOutputStream bos = new ByteArrayOutputStream();
//		byte[] buf = new byte[1024];
//		for (int readNum; (readNum = fis.read(buf)) != -1;) {
//			bos.write(buf, 0, readNum);
//		}
//		boolean thrown = false;
//
//		byte[] bytes = bos.toByteArray();
//		try {
//			ImageConverter.convertByteImg(bytes, outputImg.getAbsolutePath(), formatName);
//		} catch (IOException ex) {
//			thrown = true;
//			assertTrue(ex.toString().contains("Bad endianness tag"));
//
//		} catch (Exception ex) {
//			thrown = true;
//
//		}
//
//		fis.close();
//
//		assertTrue(thrown);
//
//	}
	
	@Test
	public void testConvertTiffImg() throws Exception {
		File workDir = temporaryFolder.getRoot();
		File outputImg = new File (workDir,"testing.png");
		URL url = ImageConverterTest.class.getResource("testing.tif");
		File tiffFile = new File(url.toURI());
		String inputImage = tiffFile.getAbsolutePath();
		FileInputStream fis = new FileInputStream(inputImage);

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];
		for (int readNum; (readNum = fis.read(buf)) != -1;) {
			bos.write(buf, 0, readNum);
		}
		boolean thrown = false;

		byte[] bytes = bos.toByteArray();
		try {
			ImageConverter.convertByteImg(bytes, outputImg.getAbsolutePath(), formatName);
		} catch (IOException ex) {
			thrown = true;

		} catch (Exception ex) {
			thrown = true;

		}

		fis.close();
		assertTrue(outputImg.exists());
		assertTrue(!thrown);

	}


}