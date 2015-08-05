package com.thomsonreuters.uscl.ereader.assemble.service;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.tools.ant.util.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.item.ExecutionContext;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.assemble.step.MoveResourcesUtil;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;

public class MoveResourcesUtilTest {
	
	MoveResourcesUtil moveResourcesUtil;
	private File tempFile;
	private File tempRootDir;
	private File docToSplitBookFile;
	private static final String FILE_NAME = "doc-To-SplitBook.txt";
	private ExecutionContext jobExecutionContext;
	
	@Before
	public void setUp() throws Exception {

		moveResourcesUtil = new MoveResourcesUtil();
		//tempFile = File.createTempFile("pirate", "ship");
		this.tempRootDir = new File(System.getProperty("java.io.tmpdir"));
		URL url = this.getClass().getResource(FILE_NAME);
		docToSplitBookFile = new File(url.toURI());	
		this.jobExecutionContext = new ExecutionContext();
	}
	
	@After
	public void tearDown() throws Exception {
		 FileUtils.delete(tempRootDir);
	}

	@Test
	public void testcopyFilesToDir() throws Exception{
		boolean thrown = false;
		List<File> tempFileList = new ArrayList<File>();
		tempFileList.add(docToSplitBookFile);
		
		try{			
			moveResourcesUtil.copyFilesToDestination(tempFileList, tempRootDir);
		}
		catch (FileNotFoundException e){
			thrown = true;
		}
		Assert.assertTrue(tempRootDir.exists());
		assertTrue(!thrown);
	}
	
	@Test
	public void testSourceToDestination() throws Exception{
		boolean thrown = false;
		
		try{			
			moveResourcesUtil.copySourceToDestination(docToSplitBookFile.getParentFile(), tempRootDir);
		}
		catch (FileNotFoundException e){
			thrown = true;
		}
		Assert.assertTrue(tempRootDir.exists());
		assertTrue(!thrown);
	}
	
	@Test
	public void testcreateCoverArt() throws Exception {

		BookDefinition bookDefinition = new BookDefinition();
		jobExecutionContext.put(JobExecutionKey.EBOOK_DEFINITON, bookDefinition);
		moveResourcesUtil.createCoverArt(jobExecutionContext);
		String actual = StringUtils.substringAfterLast(jobExecutionContext.get(JobExecutionKey.COVER_ART_PATH)
				.toString(), "\\");
		Assert.assertEquals(actual,"coverArt.PNG");
	}
	
	@Test
	public void testfilterFiles() throws Exception {
		BookDefinition bookDefinition = new BookDefinition();
		bookDefinition.setFrontMatterTheme("AAJ");
		bookDefinition.setKeyciteToplineFlag(true);
		List<File> fileList = moveResourcesUtil.filterFiles(docToSplitBookFile.getParentFile(), bookDefinition);
		Assert.assertEquals(fileList.size(),0);
	}
	
	@Test
	public void testmoveFrontMatterImages() throws Exception {
		boolean thrown = false;
		BookDefinition bookDefinition = new BookDefinition();
		bookDefinition.setFrontMatterTheme("AAJ");
		bookDefinition.setKeyciteToplineFlag(true);
		jobExecutionContext.put(JobExecutionKey.EBOOK_DEFINITON, bookDefinition);
		try {
			moveResourcesUtil.moveFrontMatterImages(jobExecutionContext, tempRootDir, false);
		}

		catch (FileNotFoundException e) {
			thrown = true;
		}
		Assert.assertTrue(tempRootDir.exists());
		assertTrue(!thrown);
	}
	
	@Test
	public void testmoveCoverArt() throws Exception {
		BookDefinition bookDefinition = new BookDefinition();
		jobExecutionContext.put(JobExecutionKey.EBOOK_DEFINITON, bookDefinition);
		moveResourcesUtil.moveCoverArt(jobExecutionContext, tempRootDir);
		String actual = StringUtils.substringAfterLast(jobExecutionContext.get(JobExecutionKey.COVER_ART_PATH)
				.toString(), "\\");
		Assert.assertEquals(actual,"coverArt.PNG");
	}
	

}
