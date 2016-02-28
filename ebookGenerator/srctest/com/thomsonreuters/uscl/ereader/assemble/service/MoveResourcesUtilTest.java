package com.thomsonreuters.uscl.ereader.assemble.service;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.batch.item.ExecutionContext;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.assemble.step.MoveResourcesUtil;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;

public class MoveResourcesUtilTest {
	
	MoveResourcesUtil moveResourcesUtil;
	private File tempRootDir;
	private File docToSplitBookFile;
	private static final String FILE_NAME = "doc-To-SplitBook.txt";
	private ExecutionContext jobExecutionContext;
	
	private File makeFile(File directory, String name, String content)
	{
		try{
			File file = new File(directory, name);
			FileOutputStream out = new FileOutputStream(file);
			out.write(content.getBytes());
			out.close();
			return file;
		}catch(Exception e){
			return null;
		}
	}
	
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
		 FileUtils.deleteQuietly(tempRootDir);
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
		jobExecutionContext.put(JobExecutionKey.EBOOK_DEFINITION, bookDefinition);
		moveResourcesUtil.createCoverArt(jobExecutionContext);
		String actual = StringUtils.substringAfterLast(jobExecutionContext.get(JobExecutionKey.COVER_ART_PATH)
				.toString(), "\\");
		Assert.assertEquals(actual,"coverArt.PNG");
	}
	
	@Test
	public void testFilterFiles() throws Exception {
		boolean thrown = false;
		BookDefinition bookDefinition = new BookDefinition();
		bookDefinition.setFrontMatterTheme("AAJ");
		bookDefinition.setKeyciteToplineFlag(true);
		List<File> fileList = moveResourcesUtil.filterFiles(docToSplitBookFile.getParentFile(), bookDefinition);
		Assert.assertEquals(fileList.size(),0);
		
		try{
			fileList = moveResourcesUtil.filterFiles(new File("DoesNotExist"), bookDefinition);
		}catch(FileNotFoundException e){
			//expected
			e.printStackTrace();
			thrown = true;
		}
		Assert.assertTrue(thrown);
	}
	
	@Test
	public void testFilterFilesMockDir() throws Exception {
		File testDir = new File(tempRootDir.getAbsolutePath()+"\\EvenMoreTemp");
		testDir.mkdir();
		File temp1 = makeFile(testDir, "AAJ.png", "totally a png");		// passes starts with "AAJ"
		File temp2 = makeFile(testDir, "keycite.xml", "Totally xml");	// passes starts with "keycite"
		File temp3 = makeFile(testDir, "AAj.csv","actually,a,csv");		// fails both
		
		BookDefinition bookDefinition = new BookDefinition();
		bookDefinition.setFrontMatterTheme("AAJ");
		bookDefinition.setKeyciteToplineFlag(false);
				
		List<File> fileList = moveResourcesUtil.filterFiles(testDir, bookDefinition);
		Assert.assertEquals(fileList.size(), 2);
		
		bookDefinition.setFrontMatterTheme("AAJ Press");
		fileList = moveResourcesUtil.filterFiles(testDir,  bookDefinition);
		Assert.assertEquals(fileList.size(), 1);
		
		bookDefinition.setKeyciteToplineFlag(true);
		fileList = moveResourcesUtil.filterFiles(testDir, bookDefinition);
		Assert.assertEquals(fileList.size(), 0);
		
		bookDefinition.setFrontMatterTheme("AAJ");
		fileList = moveResourcesUtil.filterFiles(testDir,  bookDefinition);
		Assert.assertEquals(fileList.size(), 1);
		FileUtils.deleteDirectory(testDir);
	}
	
	@Ignore
	@Test
	public void testmoveFrontMatterImages() throws Exception {
		boolean thrown = false;
		BookDefinition bookDefinition = new BookDefinition();
		bookDefinition.setFrontMatterTheme("AAJ");
		bookDefinition.setKeyciteToplineFlag(true);
		jobExecutionContext.put(JobExecutionKey.EBOOK_DEFINITION, bookDefinition);
		File canI = new File("/apps/eBookBuilder/coreStatic/images");
		File[] check = canI.listFiles();
		canI.mkdir();
		check = canI.listFiles();
		try {
			moveResourcesUtil.moveFrontMatterImages(jobExecutionContext, tempRootDir, false);
		}

		catch (NullPointerException e) {
			thrown = true;
		}
		Assert.assertTrue(tempRootDir.exists());
		assertTrue(!thrown);
	}
	
	@Ignore
	@Test
	public void testmoveCoverArt() throws Exception {
		boolean thrown = false;
		BookDefinition bookDefinition = new BookDefinition();
		jobExecutionContext.put(JobExecutionKey.EBOOK_DEFINITION, bookDefinition);
		try {
		moveResourcesUtil.moveCoverArt(jobExecutionContext, tempRootDir);
		}
		catch (FileNotFoundException e) {
			thrown = true;
		}
		assertTrue(!thrown);
	}
	

}
