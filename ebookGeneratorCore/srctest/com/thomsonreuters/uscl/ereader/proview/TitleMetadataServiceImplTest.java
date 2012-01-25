/*
* Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.proview;

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
			InputStream inputStream = TitleMetadataServiceImplTest.class.getResourceAsStream("gathered-toc-test.xml");
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
	
	private TitleMetadata getTitleMetadata() {
		TitleMetadata titleMetadata = new TitleMetadata("yarr/pirates", "v1");
		titleMetadata.setCopyright("The High Seas Trading Company.");
		titleMetadata.setArtwork(new Artwork("swashbuckling.gif"));
		Doc pirates = new Doc("1", "pirates.htm");
		Doc scallywags = new Doc("2", "scallywags.htm");
		Doc landlubbers = new Doc("3", "landlubbers.htm");
		ArrayList<Doc> documents = new ArrayList<Doc>();
		documents.add(pirates);
		documents.add(scallywags);
		documents.add(landlubbers);
		titleMetadata.setDocuments(documents);
		titleMetadata.setDisplayName("YARR - The Comprehensive Guide to &amp;&lt;&gt;&apos; Plundering the Seven Seas.");
		ArrayList<Author> authors = new ArrayList<Author>();
		authors.add(new Author("Captain Jack Sparrow"));
		authors.add(new Author("Davey Jones"));
		titleMetadata.setAuthors(authors);
		Keyword publisher = new Keyword("publisher", "High Seas Trading Company");
		Keyword jurisdiction = new Keyword("jurisdiction", "International Waters");
		ArrayList<Keyword> keywords = new ArrayList<Keyword>();
		keywords.add(publisher);
		keywords.add(jurisdiction);
		titleMetadata.setKeywords(keywords);
		ArrayList<Asset> assets = new ArrayList<Asset>();
		assets.add(new Asset("123", "BlackPearl.png"));
		assets.add(new Asset("456", "PiratesCove.png"));
		assets.add(new Asset("789", "Tortuga.png"));
		titleMetadata.setAssets(assets);
		ArrayList<TocEntry> tocEntries = new ArrayList<TocEntry>();
		tocEntries.add(new TocEntry("1/heading", "All About Pirates"));
		TocEntry scallywagging = new TocEntry("2/heading", "Scallywagging for landlubbers");
		ArrayList<TocEntry> scallywaggingChildren = new ArrayList<TocEntry>();
		scallywaggingChildren.add(new TocEntry("3/heading", "Survival"));
		scallywaggingChildren.add(new TocEntry("3.1/heading", "Begging"));
		scallywaggingChildren.add(new TocEntry("3.2/heading", "The Plank"));
		scallywaggingChildren.add(new TocEntry("3.3/heading", "Swabbing"));
		scallywaggingChildren.add(new TocEntry("3.4/heading", "Brawling"));
		scallywaggingChildren.add(new TocEntry("3.5/heading", "Patroling"));
		scallywaggingChildren.add(new TocEntry("3.6/heading", "Plundering"));
		scallywaggingChildren.add(new TocEntry("3.7/heading", "Wenching"));
		scallywagging.setChildren(scallywaggingChildren);
		tocEntries.add(scallywagging);
		TableOfContents tableOfContents = new TableOfContents();
		tableOfContents.setTocEntries(tocEntries);
		titleMetadata.setTableOfContents(tableOfContents);
		titleMetadata.setMaterialId("Plunder2");
		return titleMetadata;
	}
}
