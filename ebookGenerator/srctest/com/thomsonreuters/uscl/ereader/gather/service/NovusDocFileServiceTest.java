package com.thomsonreuters.uscl.ereader.gather.service;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.core.EBConstants;
import com.thomsonreuters.uscl.ereader.core.book.domain.NortFileLocation;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public final class NovusDocFileServiceTest {
    private static final String COLLECTION_NAME = "w_an_rcc_cajur_toc";
    private static final String GUID_1 = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";
    private static final String GUID_2 = "CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC";

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();
    private NovusDocFileServiceImpl novusDocFileService;

    @Before
    public void setUp() {
        novusDocFileService = new NovusDocFileServiceImpl();
    }

    @Test
    public void testNullDocumentError() {
        final String bookTitle = "book_title";
        final File workDir = temporaryFolder.getRoot();
        final File contentDir = new File(workDir, "junit_content");
        final File metadataDir = new File(workDir, "junit_metadata");

        final List<NortFileLocation> fileLocations = new ArrayList<>();
        final NortFileLocation location = new NortFileLocation();

        location.setLocationName("contentDir");
        location.setSequenceNum(1);
        fileLocations.add(location);

        final File cwbDir = new File(workDir, "cwb_dir");
        final File bookTitleDir = new File(cwbDir, bookTitle);
        final File contentTypeDir = new File(bookTitleDir, location.getLocationName());
        final File novusDoc = new File(contentTypeDir, "collectionName-12345_doc.xml");

        try {
            // Invoke the object under test
            contentDir.mkdirs();
            metadataDir.mkdirs();
            cwbDir.mkdir();
            bookTitleDir.mkdir();
            contentTypeDir.mkdir();
            novusDoc.createNewFile();

            addContentToFile(
                novusDoc,
                "<n-document guid=\"N29E94310C13311DF9DBEA900468B10B3\" control=\"ADD\"><n-metadata>"
                    + "</n-metadata><n-docbody></n-docbody></n-document>");

            final Map<String, Integer> guids = new HashMap<>();
            guids.put(GUID_1, 1);
            novusDocFileService.fetchDocuments(guids, cwbDir, "book_title", fileLocations, contentDir, metadataDir);

            fail("Should throw null documents error");
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.assertEquals("Null documents are found for the current ebook ", e.getMessage());
        } finally {
            temporaryFolder.delete();
        }
    }

    @Test
    public void testOneDocument() {
        final String bookTitle = "book_title";
        final File workDir = temporaryFolder.getRoot();
        final File contentDir = new File(workDir, "junit_content");
        final File metadataDir = new File(workDir, "junit_metadata");

        final List<NortFileLocation> fileLocations = new ArrayList<>();
        final NortFileLocation location = new NortFileLocation();

        location.setLocationName("contentDir");
        location.setSequenceNum(1);
        fileLocations.add(location);

        final File cwbDir = new File(workDir, "cwb_dir");
        final File bookTitleDir = new File(cwbDir, bookTitle);
        final File contentTypeDir = new File(bookTitleDir, location.getLocationName());
        final File novusDoc = new File(contentTypeDir, COLLECTION_NAME + "-12345_doc.xml");

        final File contentFile = new File(contentDir, GUID_1 + EBConstants.XML_FILE_EXTENSION);
        final File metadataFile =
            new File(metadataDir, "1-" + COLLECTION_NAME + "-" + GUID_1 + EBConstants.XML_FILE_EXTENSION);

        try {
            // Invoke the object under test
            contentDir.mkdirs();
            metadataDir.mkdirs();
            cwbDir.mkdir();
            bookTitleDir.mkdir();
            contentTypeDir.mkdir();
            novusDoc.createNewFile();

            addContentToFile(
                novusDoc,
                "<n-document guid=\""
                    + GUID_1
                    + "\" control=\"ADD\"><n-metadata>"
                    + "</n-metadata><n-docbody></n-docbody></n-document>");

            final Map<String, Integer> guids = new HashMap<>();
            guids.put(GUID_1, 1);
            novusDocFileService.fetchDocuments(guids, cwbDir, "book_title", fileLocations, contentDir, metadataDir);

            // Verify created files and directories
            assertTrue(contentFile.exists());
            assertTrue(metadataFile.exists());
            assertTrue(cwbDir.exists());
            assertTrue(bookTitleDir.exists());
            assertTrue(contentTypeDir.exists());
            assertTrue(novusDoc.exists());
        } catch (final Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        } finally {
            temporaryFolder.delete();
        }
    }

    @Test
    public void testTwoDocuments() {
        final String bookTitle = "book_title";
        final File workDir = temporaryFolder.getRoot();
        final File contentDir = new File(workDir, "junit_content");
        final File metadataDir = new File(workDir, "junit_metadata");

        final List<NortFileLocation> fileLocations = new ArrayList<>();
        final NortFileLocation location = new NortFileLocation();

        location.setLocationName("contentDir");
        location.setSequenceNum(1);
        fileLocations.add(location);

        final File cwbDir = new File(workDir, "cwb_dir");
        final File bookTitleDir = new File(cwbDir, bookTitle);
        final File contentTypeDir = new File(bookTitleDir, location.getLocationName());
        final File novusDoc = new File(contentTypeDir, COLLECTION_NAME + "-12345_doc.xml");

        final File contentFile = new File(contentDir, GUID_1 + EBConstants.XML_FILE_EXTENSION);
        final File metadataFile =
            new File(metadataDir, "1-" + COLLECTION_NAME + "-" + GUID_1 + EBConstants.XML_FILE_EXTENSION);
        final File contentFile2 = new File(contentDir, GUID_2 + EBConstants.XML_FILE_EXTENSION);
        final File metadataFile2 =
            new File(metadataDir, "2-" + COLLECTION_NAME + "-" + GUID_2 + EBConstants.XML_FILE_EXTENSION);

        try {
            // Invoke the object under test
            contentDir.mkdirs();
            metadataDir.mkdirs();
            cwbDir.mkdir();
            bookTitleDir.mkdir();
            contentTypeDir.mkdir();
            novusDoc.createNewFile();

            addContentToFile(
                novusDoc,
                "<n-load loadcontenttype=\"SECTIONAL\"><n-document guid=\""
                    + GUID_1
                    + "\" "
                    + "control=\"ADD\"><n-metadata></n-metadata><n-docbody></n-docbody></n-document><n-document "
                    + "guid=\""
                    + GUID_2
                    + "\" control=\"ADD\"><n-metadata></n-metadata><n-docbody></n-docbody>"
                    + "</n-document></n-load>");

            final Map<String, Integer> guids = new HashMap<>();
            guids.put(GUID_1, 1);
            guids.put(GUID_2, 1);
            novusDocFileService.fetchDocuments(guids, cwbDir, "book_title", fileLocations, contentDir, metadataDir);

            // Verify created files and directories
            assertTrue(contentFile.exists());
            assertTrue(metadataFile.exists());
            assertTrue(contentFile2.exists());
            assertTrue(metadataFile2.exists());
            assertTrue(cwbDir.exists());
            assertTrue(bookTitleDir.exists());
            assertTrue(contentTypeDir.exists());
            assertTrue(novusDoc.exists());
        } catch (final Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        } finally {
            temporaryFolder.delete();
        }
    }

    @Test
    public void testTwoContentTypes() {
        final String bookTitle = "book_title";
        final File workDir = temporaryFolder.getRoot();
        final File contentDir = new File(workDir, "junit_content");
        final File metadataDir = new File(workDir, "junit_metadata");

        final List<NortFileLocation> fileLocations = new ArrayList<>();
        final NortFileLocation location = new NortFileLocation();
        location.setLocationName("contentDir");
        location.setSequenceNum(1);
        fileLocations.add(location);

        final NortFileLocation location2 = new NortFileLocation();
        location2.setLocationName("contentDir2");
        location2.setSequenceNum(2);
        fileLocations.add(location2);

        final File cwbDir = new File(workDir, "cwb_dir");
        final File bookTitleDir = new File(cwbDir, bookTitle);
        final File contentTypeDir = new File(bookTitleDir, location.getLocationName());
        final File contentTypeDir2 = new File(bookTitleDir, location2.getLocationName());
        final File novusDoc = new File(contentTypeDir, COLLECTION_NAME + "-12345_doc.xml");
        final File novusDoc2 = new File(contentTypeDir2, COLLECTION_NAME + "-ABCDE_doc.xml");

        final File contentFile = new File(contentDir, GUID_1 + EBConstants.XML_FILE_EXTENSION);
        final File metadataFile =
            new File(metadataDir, "1-" + COLLECTION_NAME + "-" + GUID_1 + EBConstants.XML_FILE_EXTENSION);
        final File contentFile2 = new File(contentDir, GUID_2 + EBConstants.XML_FILE_EXTENSION);
        final File metadataFile2 =
            new File(metadataDir, "2-" + COLLECTION_NAME + "-" + GUID_2 + EBConstants.XML_FILE_EXTENSION);

        try {
            // Invoke the object under test
            contentDir.mkdirs();
            metadataDir.mkdirs();
            cwbDir.mkdir();
            bookTitleDir.mkdir();
            contentTypeDir.mkdir();
            contentTypeDir2.mkdir();
            novusDoc.createNewFile();
            novusDoc2.createNewFile();

            addContentToFile(
                novusDoc,
                "<n-load loadcontenttype=\"SECTIONAL\"><n-document guid=\""
                    + GUID_1
                    + "\" "
                    + "control=\"ADD\"><n-metadata></n-metadata><n-docbody></n-docbody></n-document></n-load>");
            addContentToFile(
                novusDoc2,
                "<n-load loadcontenttype=\"SECTIONAL\"><n-document guid=\""
                    + GUID_2
                    + "\" "
                    + "control=\"ADD\"><n-metadata></n-metadata><n-docbody></n-docbody></n-document></n-load>");

            final Map<String, Integer> guids = new HashMap<>();
            guids.put(GUID_1, 1);
            guids.put(GUID_2, 1);
            novusDocFileService.fetchDocuments(guids, cwbDir, "book_title", fileLocations, contentDir, metadataDir);

            // Verify created files and directories
            assertTrue(cwbDir.exists());
            assertTrue(bookTitleDir.exists());
            assertTrue(contentTypeDir.exists());
            assertTrue(contentTypeDir2.exists());
            assertTrue(novusDoc.exists());
            assertTrue(novusDoc2.exists());
            assertTrue(contentFile.exists());
            assertTrue(metadataFile.exists());
            assertTrue(contentFile2.exists());
            assertTrue(metadataFile2.exists());
        } catch (final Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        } finally {
            temporaryFolder.delete();
        }
    }

    private void addContentToFile(final File file, final String text) {
        try (FileWriter fileOut = new FileWriter(file)) {
            fileOut.write(text);
            fileOut.close();
        } catch (final FileNotFoundException e) {
            e.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }
}
