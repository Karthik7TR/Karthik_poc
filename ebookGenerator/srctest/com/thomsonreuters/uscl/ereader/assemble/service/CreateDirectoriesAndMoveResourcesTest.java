/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.assemble.service;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.tools.ant.util.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
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
		tempRootDir = new File((Files.createTempDirectory("YarrMatey")).toString());
		tempFile =  makeFile(tempRootDir, "pirate.ship", "don't crash");
		URL url = this.getClass().getResource(FILE_NAME);
		docToSplitBookFile = new File(url.toURI());			

	}

	@After
	public void tearDown() throws Exception {
		 FileUtils.delete(tempFile);
		 FileUtils.delete(tempRootDir);
	}
	
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
	
	@Ignore
	@Test
	public void testAddArtwork(){
		//add after making test for MoveResourcesUtil.java
	}
	
	@Ignore
	@Test
	public void testMovesResources() throws Exception{
		jobExecutionContext = new ExecutionContext();
		jobExecutionContext.put(JobExecutionKey.IMAGE_STATIC_DEST_DIR, tempRootDir.getAbsolutePath());
		List<String> imglist = new ArrayList<String>();
		List<Doc> doclist = new ArrayList<Doc>();
		File tempImg = makeFile(tempRootDir, "img.png", "totally an image file");
		createDirectoriesAndMoveResources.moveResources(jobExecutionContext, tempRootDir, true, imglist, doclist, tempImg);
	}

	@Test
	public void testGetAssetsFromDir() throws Exception {
		try{
			createDirectoriesAndMoveResources.getAssetsfromDirectories(null);
			fail("should have thrown IllegalArgumentException");
		}catch ( IllegalArgumentException e){
			e.printStackTrace();
			File temp2 = makeFile(tempRootDir, "ninja.star", "totally exists");
			List<Asset> assets = createDirectoriesAndMoveResources.getAssetsfromDirectories(tempRootDir);
			//Asset constructor format: Asset( [file name w/o extension], [file name] )
			
			Asset check = assets.get(1);
			assertTrue(check.getId().equals("pirate"));
			assertTrue(check.getSrc().equals("pirate.ship"));
			
			check = assets.get(0);
			assertTrue(check.getId().equals("ninja"));
			assertTrue(check.getSrc().equals("ninja.star"));
			
			FileUtils.delete(temp2);
		}
	}
	
	@Test
	public void testDuplicateAssets() {
		Map<String, List<String>> splitBookImgMap = new HashMap<String, List<String>>();
		List<String> imgList1 = new ArrayList<String>();
		imgList1.add("img1.xml");
		imgList1.add("img2.xml");
		
		splitBookImgMap.put("Doc1",imgList1);
		
		List<String> imgList2 = new ArrayList<String>();
		imgList2.add("img0.xml");
		imgList2.add("img2.xml");
		splitBookImgMap.put("Doc2",imgList2);
		
		ArrayList<Asset> assetsForSplitBook = new ArrayList<Asset>();
		
		for (Map.Entry<String, List<String>> entry : splitBookImgMap.entrySet()) {
			for (String imgFileName : entry.getValue()) {
				Asset asset = new Asset(StringUtils.substringBeforeLast(imgFileName, "."), imgFileName);
				//To avoid duplicate asset
				if(!assetsForSplitBook.contains(asset)){
					assetsForSplitBook.add(asset);
				}
			}
		}
		
		assertTrue(assetsForSplitBook.size()==3);
	}

	@Test
	public void testGetAssetsfromFileException() {
		try{
			createDirectoriesAndMoveResources.getAssetsfromFile(null);
			fail("should have thrown IllegalArgumentException");
		}catch(IllegalArgumentException e){
			//expected exception
			e.printStackTrace();
		}
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
	public void testMoveResourcesNotFound() throws Exception {	
		
		List<String> imgList = new ArrayList<String>();
		List<Doc> docList = new ArrayList<Doc>();
		moveResourcesUtil = new MoveResourcesUtil();
		
		boolean thrown = false;
		try{
			this.jobExecutionContext = new ExecutionContext();;
			jobExecutionContext.put(JobExecutionKey.IMAGE_STATIC_DEST_DIR,"dir");
			createDirectoriesAndMoveResources.setMoveResourcesUtil(moveResourcesUtil);
			createDirectoriesAndMoveResources.moveResources(jobExecutionContext, tempRootDir, false, imgList, docList, tempFile);
		}
		catch (FileNotFoundException e){
			thrown = true;
		}
		assertTrue(thrown);
	}
	
	@Test
	public void testGetAssetsNullInput(){
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
	public void testGetAsset(){
		Asset asset = new Asset();
		asset = createDirectoriesAndMoveResources.getAssetsfromFile(tempFile);
		System.out.println(asset.toString());
		assertTrue(asset !=null);
		assertTrue(asset.getId().equals("pirate"));
		assertTrue(asset.getSrc().equals("pirate.ship"));
	}
	

}
