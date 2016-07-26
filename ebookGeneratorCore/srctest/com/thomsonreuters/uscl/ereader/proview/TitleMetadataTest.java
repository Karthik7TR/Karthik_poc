/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.proview;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;

import org.junit.Before;
import org.junit.Test;

import com.thomsonreuters.uscl.ereader.core.book.domain.Author;

public class TitleMetadataTest {
	
	private String titleId = "yarr/pirates";
	private String titleVersion = "v1";
	private String displayName = "YARR! The Comprehensive Guide to Plundering the Seven Seas.";
	private String materialId = "Plunder2";
	private String copyright = "The High Seas Trading Company.";
	private Artwork artwork = new Artwork("swashbuckling.gif");

	private TableOfContents tableOfContents = new TableOfContents();
	private ArrayList<String> authorNames = new ArrayList<String>();
	private ArrayList<Doc> documents = new ArrayList<Doc>();
	private ArrayList<Asset> assets = new ArrayList<Asset>();
	private ArrayList<Feature> proviewFeatures;
	private ArrayList<Keyword> keywords = new ArrayList<Keyword>();
	private boolean isPilotBook;
	
	@Before
	public void setUp() {
		
	}

	
	@Test
	public void testTitleMetadataDefaultConstructorWithExpectedDefaults() throws Exception {
		TitleMetadata metadata = new TitleMetadata();
		List<Keyword> keywords = metadata.getKeywords();
		Keyword publisher = keywords.get(0);
		Keyword jurisdiction = keywords.get(1);
		Assert.assertTrue("Metadata should contain two keywords, publisher and jurisdiction", keywords.size() == 2);
		Assert.assertTrue("publisher should be Thomson Reuters, but was: " + publisher, "Thomson Reuters".equals(publisher.getText()));
		Assert.assertTrue("jurisdiction should be a period character, but was: " + jurisdiction, ".".equals(jurisdiction.getText()));
	}
	
	@Test
	public void testTitleMetadataFullConstructorWithExpectedDefaults() throws Exception {
		TitleMetadata metadata = new TitleMetadata("1337/b00k", "v1337");
		List<Keyword> keywords = metadata.getKeywords();
		Keyword publisher = keywords.get(0);
		Keyword jurisdiction = keywords.get(1);
		Assert.assertTrue("Metadata should contain two keywords, publisher and jurisdiction", keywords.size() == 2);
		Assert.assertTrue("publisher should be Thomson Reuters, but was: " + publisher, "Thomson Reuters".equals(publisher.getText()));
		Assert.assertTrue("jurisdiction should be a period character, but was: " + jurisdiction, ".".equals(jurisdiction.getText()));
	}
	
	@Test
	public void testEqualsMethod() {
		TitleMetadata one = getTitleMetadata();
		TitleMetadata two = new TitleMetadata(titleId, titleVersion, proviewFeatures, keywords, new ArrayList<Author>(), isPilotBook, copyright );
		two.setAuthors(authorNames);
		Assert.assertTrue(one.equals(two));
		Assert.assertTrue(!one.equals(new TitleMetadata()));
	}
	
	private TitleMetadata getTitleMetadata() {
		TitleMetadata titleMetadata = new TitleMetadata(titleId, titleVersion);
		titleMetadata.setCopyright(copyright);
		titleMetadata.setArtwork(artwork);
		Doc pirates = new Doc("1", "pirates.htm", 0, null);
		Doc scallywags = new Doc("2", "scallywags.htm", 0, null);
		Doc landlubbers = new Doc("3", "landlubbers.htm", 0, null);
		documents.add(pirates);
		documents.add(scallywags);
		documents.add(landlubbers);
		titleMetadata.setDocuments(documents);
		titleMetadata.setDisplayName(displayName);
		authorNames.add(new String("Captain Jack Sparrow"));
		authorNames.add(new String("Davey Jones"));
		titleMetadata.setAuthors(authorNames);
		Keyword publisher = new Keyword("publisher", "High Seas Trading Company");
		Keyword jurisdiction = new Keyword("jurisdiction", "International Waters");
		keywords.add(publisher);
		keywords.add(jurisdiction);
		titleMetadata.setKeywords(keywords);
		assets.add(new Asset("123", "BlackPearl.png"));
		assets.add(new Asset("456", "PiratesCove.png"));
		assets.add(new Asset("789", "Tortuga.png"));
		titleMetadata.setAssets(assets);
		List<TocNode> tocEntries = new ArrayList<TocNode>();
		tocEntries.add(new TocEntry("1", "heading", "All About Pirates", 1));
		TocNode scallywagging = new TocEntry("2", "heading", "Scallywagging for landlubbers", 1);
		List<TocNode> scallywaggingChildren = new ArrayList<TocNode>();
		scallywaggingChildren.add(new TocEntry("3", "heading", "Survival", 2));
		scallywaggingChildren.add(new TocEntry("3.1", "heading", "Begging", 2));
		scallywaggingChildren.add(new TocEntry("3.2", "heading", "The Plank", 2));
		scallywaggingChildren.add(new TocEntry("3.3", "heading", "Swabbing", 2));
		scallywaggingChildren.add(new TocEntry("3.4", "heading", "Brawling", 2));
		scallywaggingChildren.add(new TocEntry("3.5", "heading", "Patroling", 2));
		scallywaggingChildren.add(new TocEntry("3.6", "heading", "Plundering", 2));
		scallywaggingChildren.add(new TocEntry("3.7", "heading", "Wenching", 2));
		for (TocNode child : scallywaggingChildren) {
			scallywagging.addChild(child);
		}
		tocEntries.add(scallywagging);
		tableOfContents.setChildren(tocEntries);
		titleMetadata.setTableOfContents(tableOfContents);
		titleMetadata.setMaterialId(materialId);
		return titleMetadata;
	}
}
