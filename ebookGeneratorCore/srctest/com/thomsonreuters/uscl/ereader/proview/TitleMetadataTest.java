package com.thomsonreuters.uscl.ereader.proview;

import java.util.ArrayList;

import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.junit.Before;
import org.junit.Test;

public class TitleMetadataTest {
	private TitleMetadata titleMetadata;
	
	@Before
	public void setUp() {
		titleMetadata = new TitleMetadata("yarr/pirates", "v1");
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
	}
	
	@Test
	public void testTitleMetadataMarshallsCorrectly() throws Exception {
		 IBindingFactory bfact = 
			        BindingDirectory.getFactory(TitleMetadata.class);
		IMarshallingContext mctx = bfact.createMarshallingContext();
		mctx.setIndent(2);
	    mctx.marshalDocument(titleMetadata, "UTF-8", null, System.out);
	}
}
