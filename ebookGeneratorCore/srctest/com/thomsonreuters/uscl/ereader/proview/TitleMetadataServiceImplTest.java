/*
* Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.proview;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
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
	
	@Before
	public void setUp(){
		titleMetadataService = new TitleMetadataServiceImpl();
	}
	
	@After
	public void tearDown(){
		
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
		titleMetadata.setDisplayName("YARR! The Comprehensive Guide to Plundering the Seven Seas.");
		ArrayList<String> authors = new ArrayList<String>();
		authors.add("Captain Jack Sparrow");
		authors.add("Davey Jones");
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
		titleMetadata.setTocEntries(tocEntries);
		titleMetadata.setMaterialId("Plunder2");
		return titleMetadata;
	}
}
