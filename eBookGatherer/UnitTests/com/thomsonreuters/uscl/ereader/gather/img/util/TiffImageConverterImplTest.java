/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.gather.img.util;

import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;

import org.apache.commons.io.FileUtils;
import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestName;

import com.thomsonreuters.uscl.ereader.gather.util.images.ImageConverterException;
import com.thomsonreuters.uscl.ereader.gather.img.util.TiffImageConverterImpl;

public class TiffImageConverterImplTest {
	private static final String INCORRECT_PATH = "\\***\\\\\\\\\\\\";
	private static final String PNG_EXTENSION = ".png";
	private static final String TIFF_EXTENSION = ".tif";
	private static final String PNG = "PNG";
	private static final String IMAGEIO_EXT_TIFF_READER_CLASS = "it.geosolutions.imageioimpl.plugins.tiff.TIFFImageReader";

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();
	@Rule
	public TestName name = new TestName();
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private File workDir;
	private TiffImageConverterImpl converter;

	@Before
	public void setUp() throws Exception {
		workDir = temporaryFolder.getRoot();
		converter = new TiffImageConverterImpl();
		converter.init();
		converter.setTiffReaderClass(IMAGEIO_EXT_TIFF_READER_CLASS);
	}

	@After
	public void tearDown() throws Exception {
		FileUtils.deleteQuietly(workDir);
	}

	@Test
	public void convertUncompressed() throws Exception {
		doTest();
	}

	@Test
	public void convertGroup4() throws Exception {
		doTest();
	}

	@Test
	public void convertBadGroup4() throws Exception {
		doTest();
	}

	@Test
	public void convertNotTiff() throws Exception {
		doTest();
	}

	@Test
	public void convertNotImage() throws Exception {
		thrown.expect(ImageConverterException.class);
		thrown.expectMessage("No TIFF reader found");
		doTest();
	}

	@Test
	public void writingError() throws Exception {
		thrown.expect(ImageConverterException.class);
		thrown.expectCause(CoreMatchers.<Throwable>instanceOf(IOException.class));
		doTest(true);
	}
	
	@Test
	public void readingError() throws Exception {
		thrown.expect(ImageConverterException.class);
		doTest();
	}

	private void doTest() throws ImageConverterException, IOException, URISyntaxException {
		doTest(false);
	}

	private void doTest(boolean withWriteError)
			throws ImageConverterException, IOException, URISyntaxException {
		String testName = name.getMethodName();
		URL url = TiffImageConverterImplTest.class.getResource(testName + TIFF_EXTENSION);
		File tiff = new File(url.toURI());
		byte[] imgBytes = Files.readAllBytes(tiff.toPath());
		String outputImagePath = withWriteError ? INCORRECT_PATH : new File(workDir, testName + PNG_EXTENSION).getAbsolutePath();
		converter.convertByteImg(imgBytes, outputImagePath, PNG);

		File outputFile = new File(outputImagePath);
		assertTrue(outputFile.exists());
		assertThat(outputFile.length(), not(0l));
	}

}
