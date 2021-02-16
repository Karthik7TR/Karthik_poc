package com.thomsonreuters.uscl.ereader.format.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadata;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetadataServiceImpl;
import com.thomsonreuters.uscl.ereader.ioutil.FileExtensionFilter;
import com.thomsonreuters.uscl.ereader.ioutil.FileHandlingHelper;
import org.apache.commons.io.FileUtils;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

/**
 * JUnit test for the Transformer service.
 *
 * @author <a href="mailto:zack.farrell@thomsonreuters.com">Zack Farrell</a> uc209819
 */
public final class TransformerServiceTest {
    private static final String STATIC_CONTENT_DIR = "com/thomsonreuters/uscl/ereader/format/service/staticContent";
    private TransformerServiceImpl transformerService;
    private File tempRootDir; // root directory for all test files
    private String guid;

    /* arguments */
    private File srcDir; // input, contains .preprocess files
    private File metaDir;
    private File imgMetaDir;
    private File targetDir; // output, contains .transformed files
    private String titleID;
    private long jobID;
    private BookDefinition bookDefinition;
    private File staticContentDir;

    /* service mocks and return values */
    private DocMetadataServiceImpl metadataMoc;
    private DocMetadata docMeta;
    private GenerateDocumentDataBlockServiceImpl dataBlockService;
    private InputStream dataStream;

    /**
     * makeFile( File directory, String name, String content ) helper method to streamline file creation
     *
     * @param directory Location the new file will be created in
     * @param name Name of the new file
     * @param content Content to be written into the new file
     * @return returns a File object directing to the new file returns null if any errors occur
     */
    private File makeFile(final File directory, final String name, final String content) {
        final File file = new File(directory, name);
        try (FileOutputStream out = new FileOutputStream(file)) {
            file.createNewFile();
            out.write(content.getBytes());
            out.flush();
            out.close();
            return file;
        } catch (final Exception e) {
            return null;
        }
    }

    @Before
    public void setUp() throws IOException {
        transformerService = new TransformerServiceImpl();

        tempRootDir = new File(System.getProperty("java.io.tmpdir") + "/EvenMoreTemp");
        tempRootDir.mkdir();
        guid = "ebook_source_test";

        /* TransformXMLDocuments arguments */
        staticContentDir = new PathMatchingResourcePatternResolver().getResource(STATIC_CONTENT_DIR).getFile();
        srcDir = staticContentDir;

        metaDir = new File(tempRootDir.getAbsolutePath(), "MetaDirectory");
        metaDir.mkdir();
        makeFile(metaDir, guid + ".xml", "");
        imgMetaDir = new File(tempRootDir.getAbsolutePath(), "imageMetaDirectory");
        imgMetaDir.mkdir();
        makeFile(imgMetaDir, guid + ".imgMeta", "");
        targetDir = new File(tempRootDir.getAbsolutePath(), "TransformedDirectory");
        targetDir.mkdir();
        jobID = 987654321;

        bookDefinition = new BookDefinition();
        bookDefinition.setFullyQualifiedTitleId("yarr/pirates");
        bookDefinition.setIncludeAnnotations(true);
        bookDefinition.setIncludeNotesOfDecisions(true);

        titleID = bookDefinition.getTitleId();

        /* service mocks and return values */
        final FileExtensionFilter filter = new FileExtensionFilter();
        filter.setAcceptedFileExtensions(new String[] {"preprocess"});
        final FileHandlingHelper helper = new FileHandlingHelper();
        helper.setFilter(filter);
        transformerService.setfileHandlingHelper(helper);

        metadataMoc = EasyMock.createMock(DocMetadataServiceImpl.class);
        transformerService.setDocMetadataService(metadataMoc);

        dataBlockService = EasyMock.createMock(GenerateDocumentDataBlockServiceImpl.class);
        transformerService.setGenerateDocumentDataBlockService(dataBlockService);

        docMeta = new DocMetadata();
        docMeta.setTitleId(titleID);
        docMeta.setJobInstanceId(jobID);
        docMeta.setDocUuid(guid);
        docMeta.setCollectionName("test");

        final String currentDate = "20160703113830";
        final StringBuffer documentDataBlocks = new StringBuffer();
        documentDataBlocks.append("<document-data>");
        documentDataBlocks.append("<collection>");
        documentDataBlocks.append(docMeta.getCollectionName());
        documentDataBlocks.append("</collection>");
        documentDataBlocks.append("<datetime>" + currentDate + "</datetime>");
        documentDataBlocks.append("<versioned>");
        documentDataBlocks.append("False");
        documentDataBlocks.append("</versioned>");
        documentDataBlocks.append("<doc-type></doc-type>");
        documentDataBlocks.append("<cite></cite>");
        documentDataBlocks.append("</document-data>");
        dataStream = new ByteArrayInputStream(documentDataBlocks.toString().getBytes());
    }

    @After
    public void tearDown() throws Exception {
        /* recursively deletes the root directory, and all its subdirectories and files */
        FileUtils.deleteDirectory(tempRootDir);
    }

    /**
     * TransformerService should take a source directory with ".preprocess" files generated by XMLPreprocessService and
     * perform the second step necessary to transform them into html files. The resulting ".transformed" files are
     * created in the target directory
     * @throws IOException
     */
    @Test
    public void TestTransformerServiceHappyPathWithNotesOfDecisions() throws IOException {
        bookDefinition.setIncludeNotesOfDecisions(true);
        final File preprocess = testTransformerServiceHappyPath();
        assertTrue(notesOfDecisionsExist(preprocess));
    }

    @Test
    public void TestTransformerServiceHappyPathNoNotesOfDecisions() throws IOException {
        bookDefinition.setIncludeNotesOfDecisions(false);
        final File preprocess = testTransformerServiceHappyPath();
        assertFalse(notesOfDecisionsExist(preprocess));
    }

    private File testTransformerServiceHappyPath() {
        int numDocs = -1;

        try {
            EasyMock.expect(metadataMoc.findDocMetadataByPrimaryKey(titleID, jobID, guid)).andReturn(docMeta);
            EasyMock.replay(metadataMoc);

            EasyMock.expect(dataBlockService.getDocumentDataBlockAsStream(titleID, jobID, guid)).andReturn(dataStream);
            EasyMock.replay(dataBlockService);

            numDocs = transformerService
                .transformXMLDocuments(srcDir, metaDir, imgMetaDir, targetDir, jobID, bookDefinition, staticContentDir);
        } catch (final Exception e) {
            fail();
        }
        assertEquals(1, numDocs);

        final File preprocess = new File(targetDir.getAbsolutePath(), "ebook_source_test.transformed");
        assertTrue(preprocess.exists());
        return preprocess;
    }

    private boolean notesOfDecisionsExist(final File file) throws IOException {
        final String content = FileUtils.readFileToString(file);
        return content.contains("NotesOfDecisionsOutput");
    }

    @Test
    public void TestBadStaticDir() {
        boolean thrown = false;
        staticContentDir = targetDir; // does not contain ContentTypeMapData.xml
        try {
            EasyMock.expect(metadataMoc.findDocMetadataByPrimaryKey(titleID, jobID, guid)).andReturn(docMeta);
            EasyMock.replay(metadataMoc);

            EasyMock.expect(dataBlockService.getDocumentDataBlockAsStream(titleID, jobID, guid)).andReturn(dataStream);
            EasyMock.replay(dataBlockService);

            transformerService
                .transformXMLDocuments(srcDir, metaDir, imgMetaDir, targetDir, jobID, bookDefinition, staticContentDir);
        } catch (final EBookFormatException e) {
            // e.printStackTrace();
            thrown = true;
        }
        assertTrue(thrown);
    }

    @Test
    public void TestNullsrcDir() throws EBookFormatException {
        boolean thrown = false;
        srcDir = null;
        try {
            EasyMock.expect(metadataMoc.findDocMetadataByPrimaryKey(titleID, jobID, guid)).andReturn(docMeta);
            EasyMock.replay(metadataMoc);

            EasyMock.expect(dataBlockService.getDocumentDataBlockAsStream(titleID, jobID, guid)).andReturn(dataStream);
            EasyMock.replay(dataBlockService);

            transformerService
                .transformXMLDocuments(srcDir, metaDir, imgMetaDir, targetDir, jobID, bookDefinition, staticContentDir);
        } catch (final IllegalArgumentException e) {
            //e.printStackTrace();
            thrown = true;
        }
        assertTrue(thrown);
    }

    @Test
    public void TestNonexistentTargetDir() {
        int numDocs = -1;
        boolean thrown = false;
        FileUtils.deleteQuietly(targetDir);

        try {
            EasyMock.expect(metadataMoc.findDocMetadataByPrimaryKey(titleID, jobID, guid)).andReturn(docMeta);
            EasyMock.replay(metadataMoc);

            EasyMock.expect(dataBlockService.getDocumentDataBlockAsStream(titleID, jobID, guid)).andReturn(dataStream);
            EasyMock.replay(dataBlockService);

            numDocs = transformerService
                .transformXMLDocuments(srcDir, metaDir, imgMetaDir, targetDir, jobID, bookDefinition, staticContentDir);
        } catch (final Exception e) {
            // e.printStackTrace();
            thrown = true;
        }
        assertTrue(!thrown); // should be able to create target directory on the fly and continue running
        assertTrue(numDocs == 1);

        final File preprocess1 = new File(targetDir.getAbsolutePath(), "ebook_source_test.transformed");
        assertTrue(preprocess1.exists());
    }

    @Test
    public void TestBadsrcDir() {
        boolean thrown = false;
        srcDir = targetDir; // contains no preprocessed files
        try {
            EasyMock.expect(metadataMoc.findDocMetadataByPrimaryKey(titleID, jobID, guid)).andReturn(docMeta);
            EasyMock.replay(metadataMoc);

            EasyMock.expect(dataBlockService.getDocumentDataBlockAsStream(titleID, jobID, guid)).andReturn(dataStream);
            EasyMock.replay(dataBlockService);

            transformerService
                .transformXMLDocuments(srcDir, metaDir, imgMetaDir, targetDir, jobID, bookDefinition, staticContentDir);
        } catch (final EBookFormatException e) {
            // e.printStackTrace();
            thrown = true;
        }
        assertTrue(thrown);
    }

    @Test
    public void TestNullDocMetadata() {
        boolean thrown = false;

        try {
            EasyMock.expect(metadataMoc.findDocMetadataByPrimaryKey(titleID, jobID, guid)).andReturn(null);
            EasyMock.replay(metadataMoc);

            EasyMock.expect(dataBlockService.getDocumentDataBlockAsStream(titleID, jobID, guid)).andReturn(dataStream);
            EasyMock.replay(dataBlockService);

            transformerService
                .transformXMLDocuments(srcDir, metaDir, imgMetaDir, targetDir, jobID, bookDefinition, staticContentDir);
        } catch (final EBookFormatException e) {
            // e.printStackTrace();
            thrown = true;
        }
        assertTrue(thrown);
    }

    @Test
    public void TestBadDocMetadata() {
        boolean thrown = false;

        try {
            docMeta.setDocType("not_Real");
            docMeta.setCollectionName("not_Real");
            EasyMock.expect(metadataMoc.findDocMetadataByPrimaryKey(titleID, jobID, guid)).andReturn(docMeta);
            EasyMock.replay(metadataMoc);

            EasyMock.expect(dataBlockService.getDocumentDataBlockAsStream(titleID, jobID, guid)).andReturn(dataStream);
            EasyMock.replay(dataBlockService);

            transformerService
                .transformXMLDocuments(srcDir, metaDir, imgMetaDir, targetDir, jobID, bookDefinition, staticContentDir);
        } catch (final EBookFormatException e) {
            // e.printStackTrace();
            thrown = true;
        }
        assertTrue(thrown);
    }

    @Test
    public void TestNullMetadataFile() {
        boolean thrown = false;

        try {
            EasyMock.expect(metadataMoc.findDocMetadataByPrimaryKey(titleID, jobID, guid)).andReturn(docMeta);
            EasyMock.replay(metadataMoc);

            EasyMock.expect(dataBlockService.getDocumentDataBlockAsStream(titleID, jobID, guid)).andReturn(dataStream);
            EasyMock.replay(dataBlockService);

            transformerService.transformXMLDocuments(
                srcDir,
                targetDir,
                imgMetaDir,
                targetDir,
                jobID,
                bookDefinition,
                staticContentDir);
        } catch (final EBookFormatException e) {
            // e.printStackTrace();
            thrown = true;
        }
        assertTrue(thrown);
    }
}
