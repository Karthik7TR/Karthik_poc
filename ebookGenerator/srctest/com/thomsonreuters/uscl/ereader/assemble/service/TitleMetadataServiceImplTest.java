/*
* Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.assemble.service;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.thomsonreuters.uscl.ereader.proview.Artwork;
import com.thomsonreuters.uscl.ereader.proview.Asset;
import com.thomsonreuters.uscl.ereader.proview.TitleMetadata;

/**
 * Tests for the TitleMetadataServiceImpl
 * 
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 */
public class TitleMetadataServiceImplTest {

	private static final Logger LOG = Logger.getLogger(TitleMetadataServiceImplTest.class);
	
	private TitleMetadataServiceImpl titleMetadataService;
	
	File assetsDirectory;
	File documentsDiretory;
	File tocXml;
	File artwork;
	File tempDir;
	
	File asset1;
	File asset2;
	File asset3;
	
	@Before
	public void setUp() throws Exception {
		titleMetadataService = new TitleMetadataServiceImpl();
		File tempFile = File.createTempFile("boot", "strap");
		tempDir = tempFile.getParentFile();
	}
	
	private void createAssets() throws Exception {
		assetsDirectory = new File(tempDir, "assets");
		assetsDirectory.mkdirs();
		asset1 = new File(assetsDirectory, "I1111111111111111.png");
		asset2 = new File(assetsDirectory, "I2222222222222222.svg");
		asset3 = new File(assetsDirectory, "I3333333333333333.png");
		createAsset(asset1);
		createAsset(asset2);
		createAsset(asset3);
	}

	private void createAsset(File asset) throws Exception{
		FileOutputStream fileOutputStream = new FileOutputStream(asset);
		fileOutputStream.write("YARR!".getBytes());
		fileOutputStream.flush();
		IOUtils.closeQuietly(fileOutputStream);
	}

	@After
	public void tearDown(){
		FileUtils.deleteQuietly(assetsDirectory);
		FileUtils.deleteQuietly(documentsDiretory);
		FileUtils.deleteQuietly(tocXml);
		FileUtils.deleteQuietly(artwork);
	}
	
	@Test
	public void testCreateArtworkHappyPath() throws Exception {
		File coverArt = File.createTempFile("cover", ".png");
		Artwork artwork = titleMetadataService.createArtwork(coverArt);
		String coverSrc = coverArt.getName();
		FileUtils.deleteQuietly(coverArt);
		assertTrue("Expected cover art name to match: " + coverSrc + ", but was: " + artwork.getSrc(), artwork.getSrc().equals(coverSrc));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testAddArtworkFailsDueToNullFile() throws Exception {		
		TitleMetadata titleMetadata = new TitleMetadata();
		titleMetadataService.createArtwork(null);
	}
	
	@Test
	public void testAddAssetsHappyPath() throws Exception {
		createAssets();
		TitleMetadata titleMetadata = new TitleMetadata();
		ArrayList<Asset> actualAssets = titleMetadataService.createAssets(assetsDirectory);
		System.out.println(titleMetadata.toString());
		System.out.println("Assets Directory Contains: " + assetsDirectory.listFiles());
		assertTrue("Expected 3 assets, but was: " + actualAssets.size(), actualAssets.size() == 3);
	}
}
