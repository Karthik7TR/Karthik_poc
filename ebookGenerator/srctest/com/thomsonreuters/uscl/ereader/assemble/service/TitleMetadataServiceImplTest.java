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

import com.thomsonreuters.uscl.ereader.gather.image.service.ImageService;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetadataService;
import com.thomsonreuters.uscl.ereader.proview.Artwork;
import com.thomsonreuters.uscl.ereader.proview.Asset;
import com.thomsonreuters.uscl.ereader.proview.Doc;
import com.thomsonreuters.uscl.ereader.proview.TitleMetadata;
import com.thomsonreuters.uscl.ereader.util.FileUtilsFacade;
import com.thomsonreuters.uscl.ereader.util.UuidGenerator;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
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

/**
 * Tests for the TitleMetadataServiceImpl
 *
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris
 *         Schwartz</a> u0081674
 */
public final class TitleMetadataServiceImplTest extends TitleMetadataTestBase {
    private TitleMetadataServiceImpl titleMetadataService;

    private File assetsDirectory;
    private File documentsDiretory;
    private File tocXml;
    private File tempDir;

    private File asset1;
    private File asset2;
    private File asset3;
    private ByteArrayOutputStream resultStream;
    private File altIdFile;
    private TitleMetadata titleMetadata;
    private UuidGenerator uuidGenerator;
    private FileUtilsFacade mockFileUtilsFacade;
    private PlaceholderDocumentService mockPlaceholderDocumentService;

    @Override
    @Before
    public void setUp() throws Exception {
        titleMetadataService = new TitleMetadataServiceImpl();
        final File tempFile = File.createTempFile("boot", "strap");
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

    private void createAsset(final File asset) throws Exception {
        try (FileOutputStream fileOutputStream = new FileOutputStream(asset)) {
            fileOutputStream.write("YARR!".getBytes());
            fileOutputStream.flush();
            IOUtils.closeQuietly(fileOutputStream);
        }
    }

    @Override
    @After
    public void tearDown() {
        FileUtils.deleteQuietly(assetsDirectory);
        FileUtils.deleteQuietly(documentsDiretory);
        FileUtils.deleteQuietly(tocXml);
    }

    @Test
    public void testCreateArtworkHappyPath() throws Exception {
        final File coverArt = File.createTempFile("cover", ".png");
        final Artwork artwork = TitleMetadata.builder().artworkFile(coverArt).build().getArtwork();
        final String coverSrc = coverArt.getName();
        FileUtils.deleteQuietly(coverArt);
        assertTrue(
            "Expected cover art name to match: " + coverSrc + ", but was: " + artwork.getSrc(),
            artwork.getSrc().equals(coverSrc));
    }

    @Test
    public void testAddArtworkFailsDueToNullFile() {
        boolean thrown = false;
        try {
            TitleMetadata.builder().artworkFile(null).build();
        } catch (final IllegalArgumentException e) {
            thrown = true;
        }
        assertTrue(thrown);
    }

    @Test
    public void testAddAssetsHappyPath() throws Exception {
        createAssets();
        final List<Asset> actualAssets =
            TitleMetadata.builder().assetFilesFromDirectory(assetsDirectory).build().getAssets();
        assertTrue("Expected 3 assets, but was: " + actualAssets.size(), actualAssets.size() == 3);
    }

    @Test
    public void testGenerateTitleXML() throws Exception {
        resultStream = new ByteArrayOutputStream(1024);

        final URL pathToClass = this.getClass().getResource("yarr_pirates.csv");
        altIdFile = new File(pathToClass.toURI());
        final List<Doc> docList = new ArrayList<>();
        titleMetadata = getTitleMetadata();
        final InputStream splitTitleXMLStream =
            TitleMetadataServiceImplTest.class.getResourceAsStream("SPLIT_TITLE.xml");

        titleMetadataService
            .generateTitleXML(titleMetadata, docList, splitTitleXMLStream, resultStream, altIdFile.getParent());
        final InputSource expected =
            new InputSource(TitleMetadataServiceImplTest.class.getResourceAsStream("SPLIT_TITLE_MANIFEST.xml"));
        // System.out.println("resultStream
        // "+resultStreamToString(resultStream));
        final InputSource result = new InputSource(new ByteArrayInputStream(resultStream.toByteArray()));
        final DetailedDiff diff = new DetailedDiff(compareXML(expected, result));
        final List<Difference> differences = diff.getAllDifferences();
        // the only thing that should be different between the control file and
        // this run is the last updated date.
        Assert.assertTrue(differences.size() == 1);
        final Difference difference = differences.iterator().next();
        final String actualDifferenceLocation = difference.getTestNodeDetail().getXpathLocation();
        final String expectedDifferenceLocation = "/title[1]/@lastupdated";
        Assert.assertEquals(expectedDifferenceLocation, actualDifferenceLocation);
    }

    @Rule
    public TemporaryFolder tempDirectory = new TemporaryFolder();

    @Test
    public void testGenerateSplitTitleManifest() throws Exception {
        resultStream = new ByteArrayOutputStream(1024);

        titleMetadata = getTitleMetadata();
        titleMetadata.setTitleId("uscl/an/book_splittitletest");
        final InputStream splitTitleXMLStream = TitleMetadataServiceImplTest.class.getResourceAsStream("SPLIT_TOC.xml");

        final File transformedDirectory = new File(tempDir, "transformed");

        transformedDirectory.mkdirs();
        final File docToSplitBook = new File(transformedDirectory, "doc-To-SplitBook.txt");
        final File splitNodeInfoFile = new File(transformedDirectory, "splitNodeInfo.txt");

        final Map<String, String> familyGuidMap = new HashMap<>();
        final DocMetadataService mockDocMetadataService = EasyMock.createMock(DocMetadataService.class);
        titleMetadataService.setDocMetadataService(mockDocMetadataService);
        EasyMock.expect(mockDocMetadataService.findDistinctProViewFamGuidsByJobId(Long.valueOf(1)))
            .andReturn(familyGuidMap);
        EasyMock.replay(mockDocMetadataService);

        final ImageService mockImgService = EasyMock.createMock(ImageService.class);
        titleMetadataService.setImageService(mockImgService);

        final Map<String, List<String>> mapping = new HashMap<>();
        final List<String> imgFileNames = new ArrayList<>();
        imgFileNames.add("img1.html");
        imgFileNames.add("img2.html");
        final List<String> imgFileNames2 = new ArrayList<>();
        imgFileNames2.add("img5.html");
        mapping.put("N0B264080BFE211D8AE2C9667069B36F5", imgFileNames);
        mapping.put("NBAB9EEC1AFF711D8803AE0632FEDDFBF", imgFileNames2);

        EasyMock.expect(mockImgService.getDocImageListMap(Long.valueOf(1))).andReturn(mapping);
        EasyMock.replay(mockImgService);

        uuidGenerator = new UuidGenerator();
        titleMetadataService.setUuidGenerator(uuidGenerator);

        mockFileUtilsFacade = EasyMock.createMock(FileUtilsFacade.class);
        mockPlaceholderDocumentService = EasyMock.createMock(PlaceholderDocumentService.class);
        titleMetadataService.setPlaceholderDocumentService(mockPlaceholderDocumentService);
        titleMetadataService.setFileUtilsFacade(mockFileUtilsFacade);
        EasyMock.replay(mockPlaceholderDocumentService);
        EasyMock.replay(mockFileUtilsFacade);
        titleMetadataService.generateSplitTitleManifest(
            resultStream,
            splitTitleXMLStream,
            titleMetadata,
            Long.valueOf(1),
            transformedDirectory,
            docToSplitBook,
            splitNodeInfoFile.getAbsolutePath());
        // System.out.println("resultStream
        // "+resultStreamToString(resultStream));

        final InputSource result = new InputSource(new ByteArrayInputStream(resultStream.toByteArray()));
        final InputSource expected =
            new InputSource(SplitTocManifestFilterTest.class.getResourceAsStream("SPLIT_TITLE.xml"));
        final DetailedDiff diff = new DetailedDiff(compareXML(expected, result));
        final List<Difference> differences = diff.getAllDifferences();
        Assert.assertTrue(differences.size() == 0);

        URL pathToClass = this.getClass().getResource("doc-To-SplitBook_Expected.txt");
        final File expectedDocFile = new File(pathToClass.toURI());
        pathToClass = this.getClass().getResource("splitNodeInfo.txt");
        final File expectedsplitNodeInfoFile = new File(pathToClass.toURI());
        // System.out.println("splitNodeInfoFile.getAbsolutePath()
        // "+splitNodeInfoFile.getAbsolutePath());
        assertTrue("The files differ!", FileUtils.contentEquals(docToSplitBook, expectedDocFile));
        assertTrue("The files differ!", FileUtils.contentEquals(splitNodeInfoFile, expectedsplitNodeInfoFile));
        FileUtils.deleteQuietly(transformedDirectory);
    }

    @Test
    public void testWriteDocumentsToFile() throws Exception {
        tempDir = new File(System.getProperty("java.io.tmpdir"));
        final File transformedDirectory = new File(tempDir, "transformed");
        transformedDirectory.mkdirs();
        final File docToSplitBook = new File(transformedDirectory, "doc-To-SplitBook.txt");

        final List<Doc> orderedDocuments = new ArrayList<>();
        final List<String> imgFileNames = new ArrayList<>();
        imgFileNames.add("img1.html");
        imgFileNames.add("img2.html");

        final Doc doc1 = new Doc("DocId1", "Src1", 1, imgFileNames);
        orderedDocuments.add(doc1);
        final List<String> imgFileNames2 = new ArrayList<>();
        imgFileNames2.add("img5.html");
        final Doc doc2 = new Doc("DocId2", "Src2", 2, imgFileNames2);
        orderedDocuments.add(doc2);
        final Doc doc3 = new Doc("DocId3", "Src3", 3, null);
        orderedDocuments.add(doc3);

        titleMetadataService.writeDocumentsToFile(orderedDocuments, docToSplitBook);
        final URL pathToClass = this.getClass().getResource("doc-To-SplitBook_Ex.txt");
        final File expectedDocFile = new File(pathToClass.toURI());
        assertTrue("The files differ!", FileUtils.contentEquals(docToSplitBook, expectedDocFile));
        FileUtils.deleteQuietly(transformedDirectory);
    }
}
