/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.assemble.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.xml.serializer.Serializer;
import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Difference;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.xml.sax.InputSource;

import com.thomsonreuters.uscl.ereader.gather.image.service.ImageService;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetadataService;
import com.thomsonreuters.uscl.ereader.proview.Artwork;
import com.thomsonreuters.uscl.ereader.proview.Asset;
import com.thomsonreuters.uscl.ereader.proview.Doc;
import com.thomsonreuters.uscl.ereader.proview.TitleMetadata;
import com.thomsonreuters.uscl.ereader.util.FileUtilsFacade;
import com.thomsonreuters.uscl.ereader.util.UuidGenerator;

/**
 * Tests for the TitleMetadataServiceImpl
 * 
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris
 *         Schwartz</a> u0081674
 */
public class TitleMetadataServiceImplTest extends TitleMetadataTestBase {

	private TitleMetadataServiceImpl titleMetadataService;

	File assetsDirectory;
	File documentsDiretory;
	File tocXml;
	File artwork;
	File tempDir;

	File asset1;
	File asset2;
	File asset3;
	Serializer serializer;
	ByteArrayOutputStream resultStream;
	File altIdFile;
	TitleMetadata titleMetadata;
	UuidGenerator uuidGenerator;
	FileUtilsFacade mockFileUtilsFacade;
	PlaceholderDocumentService mockPlaceholderDocumentService;

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

	private void createAsset(File asset) throws Exception {
		FileOutputStream fileOutputStream = new FileOutputStream(asset);
		fileOutputStream.write("YARR!".getBytes());
		fileOutputStream.flush();
		IOUtils.closeQuietly(fileOutputStream);
	}

	@After
	public void tearDown() {
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
		assertTrue("Expected cover art name to match: " + coverSrc + ", but was: " + artwork.getSrc(),
				artwork.getSrc().equals(coverSrc));
	}

	@Test
	public void testAddArtworkFailsDueToNullFile() throws Exception {
		boolean thrown = false;
		try {
			titleMetadataService.createArtwork(null);
		} catch (IllegalArgumentException e) {
			thrown = true;
		}
		assertTrue(thrown);
	}

	@Test
	public void testAddAssetsHappyPath() throws Exception {
		createAssets();
		ArrayList<Asset> actualAssets = titleMetadataService.createAssets(assetsDirectory);
		assertTrue("Expected 3 assets, but was: " + actualAssets.size(), actualAssets.size() == 3);
	}

	@Test
	public void testGenerateTitleXML() throws Exception {
		resultStream = new ByteArrayOutputStream(1024);

		URL pathToClass = this.getClass().getResource("yarr_pirates.csv");
		altIdFile = new File(pathToClass.toURI());
		List<Doc> docList = new ArrayList<Doc>();
		titleMetadata = getTitleMetadata();
		InputStream splitTitleXMLStream = TitleMetadataServiceImplTest.class.getResourceAsStream("SPLIT_TITLE.xml");

		titleMetadataService.generateTitleXML(titleMetadata, docList, splitTitleXMLStream, resultStream,
				altIdFile.getParent());
		InputSource expected = new InputSource(
				TitleMetadataServiceImplTest.class.getResourceAsStream("SPLIT_TITLE_MANIFEST.xml"));
		// System.out.println("resultStream
		// "+resultStreamToString(resultStream));
		InputSource result = new InputSource(new ByteArrayInputStream(resultStream.toByteArray()));
		DetailedDiff diff = new DetailedDiff(compareXML(expected, result));
		List<Difference> differences = diff.getAllDifferences();
		// the only thing that should be different between the control file and
		// this run is the last updated date.
		Assert.assertTrue(differences.size() == 1);
		Difference difference = differences.iterator().next();
		String actualDifferenceLocation = difference.getTestNodeDetail().getXpathLocation();
		String expectedDifferenceLocation = "/title[1]/@lastupdated";
		Assert.assertEquals(expectedDifferenceLocation, actualDifferenceLocation);
	}

	@Rule
	public TemporaryFolder tempDirectory = new TemporaryFolder();

	@Test
	public void testGenerateSplitTitleManifest() throws Exception {
		resultStream = new ByteArrayOutputStream(1024);

		titleMetadata = getTitleMetadata();
		titleMetadata.setTitleId("uscl/an/book_splittitletest");
		InputStream splitTitleXMLStream = TitleMetadataServiceImplTest.class.getResourceAsStream("SPLIT_TOC.xml");

		File transformedDirectory = new File(tempDir, "transformed");

		transformedDirectory.mkdirs();
		File docToSplitBook = new File(transformedDirectory, "doc-To-SplitBook.txt");
		File splitNodeInfoFile = new File(transformedDirectory, "splitNodeInfo.txt");

		Map<String, String> familyGuidMap = new HashMap<String, String>();
		DocMetadataService mockDocMetadataService = EasyMock.createMock(DocMetadataService.class);
		titleMetadataService.setDocMetadataService(mockDocMetadataService);
		EasyMock.expect(mockDocMetadataService.findDistinctProViewFamGuidsByJobId(new Long(1)))
				.andReturn(familyGuidMap);
		EasyMock.replay(mockDocMetadataService);

		ImageService mockImgService = EasyMock.createMock(ImageService.class);
		titleMetadataService.setImageService(mockImgService);

		Map<String, List<String>> mapping = new HashMap<String, List<String>>();
		List<String> imgFileNames = new ArrayList<String>();
		imgFileNames.add("img1.html");
		imgFileNames.add("img2.html");
		List<String> imgFileNames2 = new ArrayList<String>();
		imgFileNames2.add("img5.html");
		mapping.put("N0B264080BFE211D8AE2C9667069B36F5", imgFileNames);
		mapping.put("NBAB9EEC1AFF711D8803AE0632FEDDFBF", imgFileNames2);

		EasyMock.expect(mockImgService.getDocImageListMap(new Long(1))).andReturn(mapping);
		EasyMock.replay(mockImgService);

		uuidGenerator = new UuidGenerator();
		titleMetadataService.setUuidGenerator(uuidGenerator);

		mockFileUtilsFacade = EasyMock.createMock(FileUtilsFacade.class);
		mockPlaceholderDocumentService = EasyMock.createMock(PlaceholderDocumentService.class);
		titleMetadataService.setPlaceholderDocumentService(mockPlaceholderDocumentService);
		titleMetadataService.setFileUtilsFacade(mockFileUtilsFacade);
		EasyMock.replay(mockPlaceholderDocumentService);
		EasyMock.replay(mockFileUtilsFacade);
		titleMetadataService.generateSplitTitleManifest(resultStream, splitTitleXMLStream, titleMetadata, new Long(1),
				transformedDirectory, docToSplitBook.getAbsolutePath(), splitNodeInfoFile.getAbsolutePath());
		// System.out.println("resultStream
		// "+resultStreamToString(resultStream));

		InputSource result = new InputSource(new ByteArrayInputStream(resultStream.toByteArray()));
		InputSource expected = new InputSource(SplitTocManifestFilterTest.class.getResourceAsStream("SPLIT_TITLE.xml"));
		DetailedDiff diff = new DetailedDiff(compareXML(expected, result));
		List<Difference> differences = diff.getAllDifferences();
		Assert.assertTrue(differences.size() == 0);

		URL pathToClass = this.getClass().getResource("doc-To-SplitBook_Expected.txt");
		File expectedDocFile = new File(pathToClass.toURI());
		pathToClass = this.getClass().getResource("splitNodeInfo.txt");
		File expectedsplitNodeInfoFile = new File(pathToClass.toURI());
		// System.out.println("splitNodeInfoFile.getAbsolutePath()
		// "+splitNodeInfoFile.getAbsolutePath());
		assertTrue("The files differ!", FileUtils.contentEquals(docToSplitBook, expectedDocFile));
		assertTrue("The files differ!", FileUtils.contentEquals(splitNodeInfoFile, expectedsplitNodeInfoFile));
		FileUtils.deleteQuietly(transformedDirectory);
	}

	@Test
	public void testWriteDocumentsToFile() throws Exception {

		File tempDir = new File(System.getProperty("java.io.tmpdir"));
		File transformedDirectory = new File(tempDir, "transformed");
		transformedDirectory.mkdirs();
		File docToSplitBook = new File(transformedDirectory, "doc-To-SplitBook.txt");

		List<Doc> orderedDocuments = new ArrayList<Doc>();
		List<String> imgFileNames = new ArrayList<String>();
		imgFileNames.add("img1.html");
		imgFileNames.add("img2.html");

		Doc doc1 = new Doc("DocId1", "Src1", 1, imgFileNames);
		orderedDocuments.add(doc1);
		List<String> imgFileNames2 = new ArrayList<String>();
		imgFileNames2.add("img5.html");
		Doc doc2 = new Doc("DocId2", "Src2", 2, imgFileNames2);
		orderedDocuments.add(doc2);
		Doc doc3 = new Doc("DocId3", "Src3", 3, null);
		orderedDocuments.add(doc3);

		titleMetadataService.writeDocumentsToFile(orderedDocuments, docToSplitBook.getAbsolutePath());
		URL pathToClass = this.getClass().getResource("doc-To-SplitBook_Ex.txt");
		File expectedDocFile = new File(pathToClass.toURI());
		assertTrue("The files differ!", FileUtils.contentEquals(docToSplitBook, expectedDocFile));
		FileUtils.deleteQuietly(transformedDirectory);
	}
}
