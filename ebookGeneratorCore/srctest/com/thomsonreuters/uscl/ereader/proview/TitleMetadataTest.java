package com.thomsonreuters.uscl.ereader.proview;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.apache.activemq.util.ByteArrayOutputStream;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.junit.Before;
import org.junit.Test;

public class TitleMetadataTest {
	
	private static final Logger LOG = Logger.getLogger(TitleMetadataTest.class);
	
	@Before
	public void setUp() {
		
	}
	
	@Test
	public void testTitleMetadataMarshallsCorrectly() throws Exception {
		TitleMetadata titleMetadata = getTitleMetadata();
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		
		IBindingFactory bfact = 
			        BindingDirectory.getFactory(TitleMetadata.class);
		IMarshallingContext mctx = bfact.createMarshallingContext();
		mctx.setIndent(2);
	    mctx.marshalDocument(titleMetadata, "UTF-8", null, byteArrayOutputStream);
	    LOG.info(new String(byteArrayOutputStream.toByteArray()));
	}
	
	@Test
	public void testTitleMetadataDoesNotContainKeywordsXmlBlockWhenNoKeywordsExist () throws Exception {
		TitleMetadata metadata = getTitleMetadata();
		metadata.setKeywords(null);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		IBindingFactory bfact = 
			        BindingDirectory.getFactory(TitleMetadata.class);
		IMarshallingContext mctx = bfact.createMarshallingContext();
		mctx.setIndent(2);
	    mctx.marshalDocument(metadata, "UTF-8", null, outputStream);
	    String titleMetadataXml = IOUtils.toString(outputStream.toByteArray(), "UTF-8");
	    System.out.println(titleMetadataXml);
	    Assert.assertFalse( "The marshalled XML should not contain a keywords element.", titleMetadataXml.contains("<keywords/>"));
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
