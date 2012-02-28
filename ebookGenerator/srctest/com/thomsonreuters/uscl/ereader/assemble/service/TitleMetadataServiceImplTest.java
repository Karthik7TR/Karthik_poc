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
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.jibx.runtime.JiBXException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.thomsonreuters.uscl.ereader.assemble.service.TitleMetadataServiceImpl;
import com.thomsonreuters.uscl.ereader.proview.Artwork;
import com.thomsonreuters.uscl.ereader.proview.Asset;
import com.thomsonreuters.uscl.ereader.proview.Author;
import com.thomsonreuters.uscl.ereader.proview.Doc;
import com.thomsonreuters.uscl.ereader.proview.Keyword;
import com.thomsonreuters.uscl.ereader.proview.TableOfContents;
import com.thomsonreuters.uscl.ereader.proview.TitleMetadata;
import com.thomsonreuters.uscl.ereader.proview.TocEntry;

/**
 * Tests for the TitleMetadataServiceImpl
 * 
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 */
public class TitleMetadataServiceImplTest extends TitleMetadataBaseTest {

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
	
	@Test(expected=IllegalArgumentException.class)
	public void testTitleMetadataServiceThrowsExceptionWhenNullMetadataPassed() throws Exception {
		titleMetadataService.writeToStream(null, System.out);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testTitleMetadataServiceThrowsExceptionWhenNullOutputStreamPassed() throws Exception {
		titleMetadataService.writeToStream(new TitleMetadata(), null);
	}
	
	@Test
	public void testTitleMetadataServiceFailsToMarshalWhenRequiredInformationHasNotBeenSupplied() throws Exception {
		try {
			titleMetadataService.writeToStream(new TitleMetadata(), System.out);
			fail("Expected a RuntimeException to be thrown, but none was!");
		}
		catch (RuntimeException e){
			assertTrue("Expected a JiBXException to be the cause of the RuntimeException thrown when the title metadata is incomplete.", e.getCause().getClass().equals(JiBXException.class));
		}
	}
	
	@Test
	public void testTitleMetadataServiceHappyPath() throws Exception {
		
		File tempFile = File.createTempFile("titleMetadataService", ".test");
		
		TitleMetadata titleMetadata = getTitleMetadata();
		titleMetadataService.writeToFile(titleMetadata, tempFile);
		TitleMetadata serializedMetadata = titleMetadataService.readFromFile(tempFile);
		
		FileUtils.deleteQuietly(tempFile);
		
		LOG.debug(" Input: " + titleMetadata.toString());
		LOG.debug("Output: " + serializedMetadata.toString());
		
		assertTrue("Title metadata objects should be equal, but weren't!", titleMetadata.equals(serializedMetadata));
	}
	
	@Test
	public void testCreateArtworkHappyPath() throws Exception {
		File coverArt = File.createTempFile("cover", ".png");
		Artwork artwork = titleMetadataService.createArtwork(coverArt);
		String coverSrc = coverArt.getName();
		FileUtils.deleteQuietly(coverArt);
		assertTrue("Expected cover art name to match: " + coverSrc + ", but was: " + artwork.getSrc(), artwork.getSrc().equals(coverSrc));
	}
	
	@Test(expected=IllegalArgumentException.class)
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

	@Test
	public void testCreateTableOfContentsFromGatheredTocHappyPath() throws Exception  {
		File titleXml = null;
		try {
			TitleMetadata titleMetadata = getTitleMetadata();
			TableOfContents tableOfContents = new TableOfContents();
			InputStream inputStream = TitleMetadataServiceImplTest.class.getResourceAsStream("gathered_toc_test.xml");
			tableOfContents.setTocEntries(titleMetadataService.createTableOfContents(inputStream));
			assertTrue(tableOfContents.getTocEntries().size() > 0);
			titleMetadata.setTableOfContents(tableOfContents);
			titleXml = File.createTempFile("titleMetadata", ".xml");
			titleMetadataService.writeToFile(titleMetadata, titleXml);			
		}
		finally {
			FileUtils.deleteQuietly(titleXml);
		}
		
	}
	
	@Test
	public void testTitleMetadataRoundTripPreservesEntities() throws Exception {
		File titleXml = null;
		try {
			TitleMetadata titleMetadata = getTitleMetadata();
			TableOfContents tableOfContents = new TableOfContents();
			InputStream inputStream = TitleMetadataServiceImplTest.class.getResourceAsStream("gathered_nort2_test.xml");
			tableOfContents.setTocEntries(titleMetadataService.createTableOfContents(inputStream));
			assertTrue(tableOfContents.getTocEntries().size() > 0);
			titleMetadata.setTableOfContents(tableOfContents);
			titleXml = File.createTempFile("titleMetadata", ".xml");
			titleMetadataService.writeToFile(titleMetadata, titleXml);
			TitleMetadata unmarshalledTitleMetadata = titleMetadataService.readFromFile(titleXml);
			String expectedText = "CA ORGANIC ACTS TREATY OF GUADALUPE HIDALGO, Refs & Annos";
			boolean foundMatchingChildInTitleMetadataBasedOnText = findTableOfContentsNodeContainingText(expectedText, unmarshalledTitleMetadata.getTableOfContents());
			
			String secondText = "West's ANNOTATED CALIFORNIA CODES";
			boolean foundFirstAposText = findTableOfContentsNodeContainingText(secondText, tableOfContents);
			
			if (!foundMatchingChildInTitleMetadataBasedOnText){
				throw new RuntimeException("Expected to find a node in the unmarshalled title metadata with title: " + expectedText);
			}
			
			if (!foundFirstAposText){
				throw new RuntimeException("Expected to find a node in the unmarshalled title metadata with title: " + secondText);
			}
		}
		finally {
			FileUtils.deleteQuietly(titleXml);
		}
	}
	
	private boolean findTableOfContentsNodeContainingText(String expectedText,
			TableOfContents tableOfContents) {
		boolean foundChildMatchingText = Boolean.FALSE;
		tableOfContents.getTocEntries();
		for (TocEntry tocEntry : tableOfContents.getTocEntries()) {
			foundChildMatchingText = checkTocEntryForMatchingText(tocEntry, expectedText);
			if (foundChildMatchingText) {
				return Boolean.TRUE;
			}
		}
		return foundChildMatchingText;
	}
	
	private boolean checkTocEntryForMatchingText(TocEntry tocEntry, String textToFind) {
		boolean matchedText = Boolean.FALSE;
		if (textToFind.equals(tocEntry.getText())){ //did we match the current node in the traversal?
			System.out.println("Found expected text: [" + tocEntry.getText() + "]");
			return Boolean.TRUE;
		}
		else if (tocEntry.getChildren() != null && tocEntry.getChildren().size() > 0){ //if we have child nodes, check those too.
			for (TocEntry child : tocEntry.getChildren()){
				matchedText = checkTocEntryForMatchingText(child, textToFind);
				if (matchedText){
					LOG.debug("Found expected text: [" + child.getText() + "]");
					return Boolean.TRUE;
				}
			}
		}
		return matchedText;
	}

}
