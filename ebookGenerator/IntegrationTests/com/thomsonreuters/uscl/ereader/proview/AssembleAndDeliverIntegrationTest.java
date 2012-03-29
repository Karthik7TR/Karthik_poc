/*
* Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.proview;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.io.IOUtils;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.apache.tools.ant.util.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.thomsonreuters.uscl.ereader.assemble.service.EBookAssemblyService;
import com.thomsonreuters.uscl.ereader.assemble.service.EBookAssemblyServiceImpl;
import com.thomsonreuters.uscl.ereader.assemble.service.TitleMetadataService;
import com.thomsonreuters.uscl.ereader.assemble.service.TitleMetadataServiceImpl;
import com.thomsonreuters.uscl.ereader.deliver.rest.ProviewHttpResponseErrorHandler;
import com.thomsonreuters.uscl.ereader.deliver.rest.ProviewMessageConverter;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewClientImpl;

/**
 * This class is responsible for end-to-end testing of the assembly and deliver services.
 * 
 * <p>These tests should only be run when all of the component-level assembly and deliver services pass, otherwise all bets are off.</p>
 * <em>This test will fail if:</em>
 * <ul>
 * <li>ProView QED is down or otherwise unavailable</li>
 * <li>A network connection is unavailable (timeout)</li>
 * <li>The artifacts could not be generated correctly, possibly due to resource exhaustion (Disk, Memory, etc)</li>
 * </ul>
 * <p>If this test does not pass, run the full suite of component tests to ensure that no breaking changes have occurred.</p>
 * <p><i>If this test passes, publishing to ProView QED was a success and the integration test book can be viewed here: <a href="http://proview.qed.int.thomsonreuters.com/">Proview QED (Browser)</a> ... after a short wait.</i></p>
 * 
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 */
public class AssembleAndDeliverIntegrationTest {
	private static final Logger LOG = Logger.getLogger(AssembleAndDeliverIntegrationTest.class);
	
	private EBookAssemblyService eBookAssemblyService;
	private TitleMetadataService titleMetadataService;
	private ProviewClientImpl proviewClient;
	private DefaultHttpClient defaultHttpClient;
	private TitleMetadata titleMetadata;
	private long timestamp;
	private String titleId;
	private String titleIdFullyQualified;
	private static final String publishTitleUriTemplate = "/v1/title/{titleId}/{eBookVersionNumber}";

	private static final String PROVIEW_DOMAIN_PREFIX = "proviewpublishing.int.qed.thomsonreuters.com";
	private static final String PROVIEW_USERNAME = "publisher";
	private static final String PROVIEW_PASSWORD = "f9R_zBq37a";
	private static final String TITLE_ID_PREFIX = "uscl/cr/";
	
	private static final String DOCUMENT_ONE_ID = "I11111111111111111111111111111111";
	private static final String DOCUMENT_TWO_ID = "I22222222222222222222222222222222";
	private static final String DOCUMENT_THREE_ID = "I33333333333333333333333333333333";
	private static final String CSS_FILENAME = "document.css";
	
	private File tempFile;
	private File eBook;
	private File eBookDirectory;
	private File titleXml;
	private File documentsDirectory;
	private File assetsDirectory;
	private File artworkDirectory;
	
	Doc codeOfConduct = new Doc(DOCUMENT_ONE_ID, "codeOfConduct.htm");
	Doc plundering = new Doc(DOCUMENT_TWO_ID, "plundering.htm");
	Doc landlubbers = new Doc(DOCUMENT_THREE_ID, "landlubbers.htm");
	Artwork artwork = new Artwork("yarrCoverArt.gif");
	
	@Before
	public void setUp() throws Exception {
		timestamp = System.currentTimeMillis(); 
		titleId = "regression_test";
		titleIdFullyQualified = TITLE_ID_PREFIX + titleId;
		setUpProviewClient();
		titleMetadataService = new TitleMetadataServiceImpl();
		eBookAssemblyService = new EBookAssemblyServiceImpl();
		setUpTitleMetadata();
		bootstrapEbookDirectory();
	}
	
	@After
	public void tearDown() throws Exception {
		FileUtils.delete(eBookDirectory);
		FileUtils.delete(tempFile);
		//FileUtils.delete(eBook);
	}

	private void bootstrapEbookDirectory() throws IOException {
		tempFile = File.createTempFile("pirate", "ship");
		eBookDirectory = new File(tempFile.getParentFile(), "eBook");
		eBookDirectory.mkdirs();
		titleXml = new File(eBookDirectory, "title.xml");
		documentsDirectory = new File(eBookDirectory, "documents");
		artworkDirectory = new File(eBookDirectory, "artwork");
		assetsDirectory = new File(eBookDirectory, "assets");
		documentsDirectory.mkdirs();
		artworkDirectory.mkdirs();
		assetsDirectory.mkdirs();
		
		eBook = new File(tempFile.getParentFile(), titleId + ".gz");
		
		FileOutputStream codeOfConductOutputStream = new FileOutputStream(new File(documentsDirectory, codeOfConduct.getSrc()));
		FileOutputStream plunderingOutputStream = new FileOutputStream(new File(documentsDirectory, plundering.getSrc()));
		FileOutputStream landlubbersOutputStream = new FileOutputStream(new File(documentsDirectory, landlubbers.getSrc()));
		FileOutputStream artworkOutputStream = new FileOutputStream(new File(artworkDirectory, artwork.getSrc()));
		FileOutputStream styleSheetOutputStream = new FileOutputStream(new File(assetsDirectory, CSS_FILENAME));
		
		IOUtils.copy(AssembleAndDeliverIntegrationTest.class.getResourceAsStream(codeOfConduct.getSrc()), codeOfConductOutputStream);
		IOUtils.copy(AssembleAndDeliverIntegrationTest.class.getResourceAsStream(plundering.getSrc()), plunderingOutputStream);
		IOUtils.copy(AssembleAndDeliverIntegrationTest.class.getResourceAsStream(landlubbers.getSrc()), landlubbersOutputStream);
		IOUtils.copy(AssembleAndDeliverIntegrationTest.class.getResourceAsStream(artwork.getSrc()), artworkOutputStream);
		IOUtils.copy(AssembleAndDeliverIntegrationTest.class.getResourceAsStream(CSS_FILENAME), styleSheetOutputStream);
		
		IOUtils.closeQuietly(codeOfConductOutputStream);
		IOUtils.closeQuietly(plunderingOutputStream);
		IOUtils.closeQuietly(landlubbersOutputStream);
		IOUtils.closeQuietly(artworkOutputStream);
		IOUtils.closeQuietly(styleSheetOutputStream);
	}
	
	private void setUpProviewClient() {
		proviewClient = new ProviewClientImpl();
		proviewClient.setPublishTitleUriTemplate("http://" + PROVIEW_DOMAIN_PREFIX + publishTitleUriTemplate);
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		
		defaultHttpClient = new DefaultHttpClient();
		defaultHttpClient.getCredentialsProvider().setCredentials(
				new AuthScope(PROVIEW_DOMAIN_PREFIX, AuthScope.ANY_PORT),
				new UsernamePasswordCredentials(PROVIEW_USERNAME, PROVIEW_PASSWORD));
		requestFactory.setHttpClient(defaultHttpClient);
		RestTemplate restTemplate = new RestTemplate(requestFactory);
		restTemplate.getMessageConverters().add(new ProviewMessageConverter());
		restTemplate.setErrorHandler(new ProviewHttpResponseErrorHandler());
		proviewClient.setRestTemplate(restTemplate);
		
	}

	private void setUpTitleMetadata() {
		titleMetadata = new TitleMetadata(titleIdFullyQualified, "v" + timestamp);
		titleMetadata.setCopyright("The High Seas Trading Company");
		titleMetadata.setArtwork(artwork);
		ArrayList<Asset> assets = new ArrayList<Asset>();
		assets.add(new Asset("css", CSS_FILENAME));
		titleMetadata.setAssets(assets);
		ArrayList<Doc> documents = new ArrayList<Doc>();
		documents.add(codeOfConduct);
		documents.add(plundering);
		documents.add(landlubbers);
		titleMetadata.setDocuments(documents);
		titleMetadata.setDisplayName("YARR - The Comprehensive Guide to Plundering the Seven Seas");
		ArrayList<Author> authors = new ArrayList<Author>();
		authors.add(new Author("Christopher Schwartz"));
		titleMetadata.setAuthors(authors);
		Keyword publisher = new Keyword("publisher", "High Seas Trading Company");
		Keyword jurisdiction = new Keyword("jurisdiction", "International Waters");
		ArrayList<Keyword> keywords = new ArrayList<Keyword>();
		keywords.add(publisher);
		keywords.add(jurisdiction);
		titleMetadata.setKeywords(keywords);
		ArrayList<TocNode> tocEntries = new ArrayList<TocNode>();
		tocEntries.add(new TocEntry(DOCUMENT_ONE_ID, "codeOfConduct", "Pirate Code of Conduct", 1));
		tocEntries.add(new TocEntry(DOCUMENT_TWO_ID, "plundering", "Plundering", 1));
		tocEntries.add(new TocEntry(DOCUMENT_THREE_ID, "landlubbers", "Landlubbers", 1));
		TableOfContents tableOfContents = new TableOfContents();
		for (TocNode child : tocEntries) {
			tableOfContents.addChild(child);
		}
		titleMetadata.setTableOfContents(tableOfContents);
		titleMetadata.setMaterialId("1234");
	}
	
	@Test
	public void testPublishGoldDataToProView() throws Exception {
		
	}
	
}
