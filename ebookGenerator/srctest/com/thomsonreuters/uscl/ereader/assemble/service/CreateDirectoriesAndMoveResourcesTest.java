package com.thomsonreuters.uscl.ereader.assemble.service;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.tools.ant.util.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.item.ExecutionContext;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.assemble.step.CreateDirectoriesAndMoveResources;
import com.thomsonreuters.uscl.ereader.assemble.step.MoveResourcesUtil;
import com.thomsonreuters.uscl.ereader.proview.Asset;
import com.thomsonreuters.uscl.ereader.proview.Doc;

public class CreateDirectoriesAndMoveResourcesTest {

	CreateDirectoriesAndMoveResources createDirectoriesAndMoveResources;
	Map<String, List<Doc>> docMap = new HashMap<String, List<Doc>>();
	Map<String, List<String>> splitBookImgMap = new HashMap<String, List<String>>();
	private static final String FILE_NAME = "doc-To-SplitBook.txt";
	private File docToSplitBookFile;
	private File tempFile;
		
	private ExecutionContext jobExecutionContext;
	private File tempRootDir;
	private MoveResourcesUtil moveResourcesUtil;
	
	
	@Before
	public void setUp() throws Exception {

		createDirectoriesAndMoveResources = new CreateDirectoriesAndMoveResources();
		tempFile = File.createTempFile("pirate", "ship");
		tempRootDir = new File(tempFile.getParentFile(), "ebookDir");
		tempRootDir.mkdirs();
		URL url = this.getClass().getResource(FILE_NAME);
		docToSplitBookFile = new File(url.toURI());			

	}

	@After
	public void tearDown() throws Exception {
		 FileUtils.delete(tempFile);
		 FileUtils.delete(tempRootDir);
	}

	@Test
	public void testGetassetsFromDir() throws Exception {
		try{
			createDirectoriesAndMoveResources.getAssetsfromDirectories( null);
			fail("should have thrown IllegalArgumentException");
		}catch ( IllegalArgumentException e){
			e.printStackTrace();
			List<Asset> assets = createDirectoriesAndMoveResources.getAssetsfromDirectories(tempRootDir);
			boolean keepGoing = true;
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetAssetsfromFileException() {
		createDirectoriesAndMoveResources.getAssetsfromFile(null);
	}

	@Test
	public void testGetAssetsfromFile() {
		Asset asset = createDirectoriesAndMoveResources.getAssetsfromFile(tempFile);
		assertTrue(asset.getId().contains("pirate"));
	}

	@Test
	public void testReadDocImgFile() throws Exception {
		createDirectoriesAndMoveResources.readDocImgFile(docToSplitBookFile, docMap, splitBookImgMap);
		List<Doc> docList = null;

		// Doc List
		Iterator<Map.Entry<String, List<Doc>>> itr = docMap.entrySet().iterator();
		while (itr.hasNext()) {
			Map.Entry<String, List<Doc>> pair = (Map.Entry<String, List<Doc>>) itr.next();

			if (pair.getKey().equals(new String("1"))) {
				docList = pair.getValue();
				Assert.assertEquals(docList.size(), 5);
			}
		}

		// Img List
		List<String> imgList = null;
		Iterator<Map.Entry<String, List<String>>> it = splitBookImgMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, List<String>> pair = (Map.Entry<String, List<String>>) it.next();

			if (pair.getKey().equals(new String("1"))) {
				imgList = pair.getValue();
				Assert.assertEquals(2, imgList.size());
			}
			if (pair.getKey().equals(new String("3"))) {

				Assert.assertEquals(0, imgList.size());
			}
		}
	}
	
	
	
	@Test
	public void testMoveResources() throws Exception {	
		
		List<String> imgList = new ArrayList<String>();
		List<Doc> docList = new ArrayList<Doc>();
		moveResourcesUtil = new MoveResourcesUtil();
		
		boolean thrown = false;
		try{
			this.jobExecutionContext = new ExecutionContext();;
			jobExecutionContext.put(JobExecutionKey.IMAGE_STATIC_DEST_DIR,"dir");
			createDirectoriesAndMoveResources.setMoveResourcesUtil(moveResourcesUtil);
			createDirectoriesAndMoveResources.moveResouces(jobExecutionContext, tempRootDir, false, imgList, docList, tempFile);
		}
		catch (FileNotFoundException e){
			thrown = true;
		}
		assertTrue(thrown);
	}
	
	@Test
	public void testgetAssets(){
		boolean thrown = false;
		try{
			createDirectoriesAndMoveResources.getAssetsfromDirectories(null);
		}
		catch (IllegalArgumentException e){
			thrown = true;
		}
		assertTrue(thrown);
	}
	
	
	@Test
	public void testgetasset(){
		Asset asset = new Asset();
		asset = createDirectoriesAndMoveResources.getAssetsfromFile(tempFile);
		
		//System.out.println(asset.toString());
		assertTrue(asset !=null);
		assertTrue(asset.getId().startsWith("pirate"));
		assertTrue(asset.getSrc().startsWith("pirate"));
	}
	

}
