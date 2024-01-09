package com.thomsonreuters.uscl.ereader.format.service;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.thomsonreuters.uscl.ereader.context.CommonTestContextConfiguration;
import com.thomsonreuters.uscl.ereader.core.book.domain.TableViewer;
import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;
import com.thomsonreuters.uscl.ereader.gather.image.service.ImageService;
import com.thomsonreuters.uscl.ereader.gather.image.service.ImageServiceImpl;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadata;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocumentMetadataAuthority;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetadataService;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.PaceMetadataService;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.PaceMetadataServiceImpl;
import com.thomsonreuters.uscl.ereader.ioutil.FileExtensionFilter;
import com.thomsonreuters.uscl.ereader.ioutil.FileHandlingHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * JUnit test for HTMLTransformerserviceImpl.java
 *
 * @author <a href="mailto:zack.farrell@thomsonreuters.com">Zack Farrell</a> uc209819
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = HTMLTransformerServiceTest.Config.class)
@ActiveProfiles("IntegrationTests")
public final class HTMLTransformerServiceTest {
    @Autowired
    private HTMLTransformerService transformerService;
    @Autowired
    private FileHandlingHelper helper;
    @Autowired
    private DocMetadataService metadataMoc;
    private DocumentMetadataAuthority docMetaAuthority;
    private DocMetadata docMeta;
    private File tempRootDir; // root directory for all test files

    private File srcDir;
    private File targetDir;
    private File staticImgList;
    private List<TableViewer> tableViewers;
    private boolean isProviewTableFlag;
    private String title = "ebook_source_test";
    private Long jobId;
    private Map<String, Set<String>> targetAnchors;
    private File docsGuidFile;
    private File deDuppingFile;
    private boolean isHighlight;
    private boolean isStrikethrough;
    private boolean delEditorNodeHeading;
    private String version = "test";

    /*** makeFile( File directory, String name, String content ) helper method to streamline file creation
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
    public void setUp() {
        /* initialize arguments */
        tempRootDir = new File(System.getProperty("java.io.tmpdir") + "/EvenMoreTemp");
        tempRootDir.mkdir();
        srcDir = new File("srctest/com/thomsonreuters/uscl/ereader/format/service/staticContent");
        targetDir = new File(tempRootDir.getAbsolutePath(), "PostTransformDirectory");
        isProviewTableFlag = false;
        targetDir.mkdir();
        staticImgList = makeFile(tempRootDir, "StaticImageList", "");
        jobId = Long.valueOf(127);
        targetAnchors = new HashMap<>();
        docsGuidFile = makeFile(tempRootDir, "docsGuidFile", "");
        deDuppingFile = makeFile(tempRootDir, "deDuppingFile", "");

        /* service mocks and return values */
        final FileExtensionFilter filter = new FileExtensionFilter();
        filter.setAcceptedFileExtensions(new String[] {"transformed"});
        helper.setFilter(filter);

        final Set<DocMetadata> docMetadataSet = new LinkedHashSet<>();
        docMetaAuthority = new DocumentMetadataAuthority(docMetadataSet);

        docMeta = new DocMetadata();
        docMeta.setTitleId(title);
        docMeta.setJobInstanceId(jobId);
        docMeta.setDocUuid(title);
        docMeta.setCollectionName("test");
    }

    @After
    public void tearDown() throws Exception {
        /* recursively deletes the root directory, and all its subdirectories and files */
        FileUtils.deleteDirectory(tempRootDir);
        EasyMock.reset(metadataMoc);
    }

    /**
     * TransformHTML should take a source directory with ".transformed" files generated by TransformerService and
     * perform the third step necessary to transform them into html files. The resulting ".postTransform" files are
     * created in the target directory
     */
    @Test
    public void testTransformerServiceHappyPath() {
        int numDocs = -1;
        boolean thrown = false;

        try {
            EasyMock.expect(metadataMoc.findAllDocMetadataForTitleByJobId(jobId)).andReturn(docMetaAuthority);
            EasyMock.expect(metadataMoc.findDocMetadataByPrimaryKey(title, jobId, title)).andReturn(docMeta);
            EasyMock.replay(metadataMoc);

            numDocs = transformerService.transformHTML(
                srcDir,
                targetDir,
                staticImgList,
                tableViewers,
                title,
                jobId,
                targetAnchors,
                docsGuidFile,
                deDuppingFile,
                isHighlight,
                isStrikethrough,
                delEditorNodeHeading,
                version,
                isProviewTableFlag);
        } catch (final Exception e) {
            // e.printStackTrace();
            thrown = true;
        }
        assertTrue(!thrown);
        assertTrue(numDocs == 1);

        final File postTransform = new File(targetDir.getAbsolutePath(), title + ".posttransform");
        assertTrue(postTransform.exists());
    }

    /**
     * Test TransformHTML and various logical branches not taken by the happy path test above
     */
    @Test
    public void testAltConditions() {
        int numDocs = -1;
        boolean thrown = false;

        /* test miscellaneous IF branches not taken by happy path */
        tableViewers = new ArrayList<>();
        //final TableViewer table = new TableViewer();
        //table.setDocumentGuid(title);
        //tableViewers.add(table);
        isProviewTableFlag = true;
        version = "test.test";
        docMeta.setProviewFamilyUUIDDedup(Integer.valueOf(1));
        docMeta.setDocFamilyUuid("hello test!");
        FileUtils.deleteQuietly(targetDir);

        try {
            EasyMock.expect(metadataMoc.findAllDocMetadataForTitleByJobId(jobId)).andReturn(docMetaAuthority);
            EasyMock.expect(metadataMoc.findDocMetadataByPrimaryKey(title, jobId, title)).andReturn(docMeta);
            EasyMock.replay(metadataMoc);

            numDocs = transformerService.transformHTML(
                srcDir,
                targetDir,
                staticImgList,
                tableViewers,
                title,
                jobId,
                targetAnchors,
                docsGuidFile,
                deDuppingFile,
                isHighlight,
                isStrikethrough,
                delEditorNodeHeading,
                version,
                isProviewTableFlag);
        } catch (final Exception e) {
            // e.printStackTrace();
            thrown = true;
        }
        assertTrue(!thrown);
        assertTrue(numDocs == 1);

        final File postTransform = new File(targetDir.getAbsolutePath(), title + ".posttransform");
        assertTrue(postTransform.exists());
    }


    /**
     * test miscellaneous exceptions that may be thrown due to bad arguments
     */
    @Test
    public void testExceptions() {
        boolean thrown = false;
        boolean expect = false;
        try {
            transformerService.transformHTML(
                null,
                targetDir,
                staticImgList,
                tableViewers,
                title,
                jobId,
                targetAnchors,
                docsGuidFile,
                deDuppingFile,
                isHighlight,
                isStrikethrough,
                delEditorNodeHeading,
                version,
                isProviewTableFlag);
        } catch (final IllegalArgumentException e) {
            // e.printStackTrace();
            expect = true;
        } catch (final Exception e) {
            // e.printStackTrace();
            thrown = true;
        }
        assertTrue(expect);
        assertTrue(!thrown);

        thrown = false;
        expect = false;

        try {
            transformerService.transformHTML(
                targetDir,
                targetDir,
                staticImgList,
                tableViewers,
                title,
                jobId,
                targetAnchors,
                docsGuidFile,
                deDuppingFile,
                isHighlight,
                isStrikethrough,
                delEditorNodeHeading,
                version,
                isProviewTableFlag);
        } catch (final EBookFormatException e) {
            // e.printStackTrace();
            expect = true;
        } catch (final Exception e) {
            // e.printStackTrace();
            thrown = true;
        }
        assertTrue(expect);
        assertTrue(!thrown);
    }

    /**
     * test handling of exceptions thrown by the xml parser
     */
    @Test
    public void testSaxParserException() {
        boolean thrown = false;
        boolean expect = false;
        makeFile(targetDir, title + ".transformed", "<?xml version=\"1.0\" encoding=\"UTF-8\"?>");

        try {
            EasyMock.expect(metadataMoc.findAllDocMetadataForTitleByJobId(jobId)).andReturn(docMetaAuthority);
            EasyMock.expect(metadataMoc.findDocMetadataByPrimaryKey(title, jobId, title)).andReturn(docMeta);
            EasyMock.replay(metadataMoc);

            transformerService.transformHTML(
                targetDir,
                targetDir,
                staticImgList,
                tableViewers,
                title,
                jobId,
                targetAnchors,
                docsGuidFile,
                deDuppingFile,
                isHighlight,
                isStrikethrough,
                delEditorNodeHeading,
                version,
                isProviewTableFlag);
        } catch (final EBookFormatException e) {
            // e.printStackTrace();
            expect = true;
        } catch (final Exception e) {
            // e.printStackTrace();
            thrown = true;
        }

        assertTrue(expect);
        assertTrue(!thrown);
    }

    /**
     * extend coverage to the function includeDeduppingAnchorRecords( .. )
     */
    @Test
    public void testDeduppingAnchorRecords() {
        int numDocs = -1;
        boolean thrown = false;

        final File srcFile =
            makeFile(targetDir, title + ".transformed", "<title><src id=\"1234\"/><src id=\"1234\"/></title>");

        try {
            EasyMock.expect(metadataMoc.findAllDocMetadataForTitleByJobId(jobId)).andReturn(docMetaAuthority);
            EasyMock.expect(metadataMoc.findDocMetadataByPrimaryKey(title, jobId, title)).andReturn(docMeta);
            EasyMock.replay(metadataMoc);

            numDocs = transformerService.transformHTML(
                targetDir,
                targetDir,
                staticImgList,
                tableViewers,
                title,
                jobId,
                targetAnchors,
                docsGuidFile,
                deDuppingFile,
                isHighlight,
                isStrikethrough,
                delEditorNodeHeading,
                version,
                isProviewTableFlag);
        } catch (final Exception e) {
            // e.printStackTrace();
            thrown = true;
        } finally {
            FileUtils.deleteQuietly(srcFile);
        }
        assertTrue(!thrown);
        assertTrue(numDocs == 1);

        final File postTransform = new File(targetDir.getAbsolutePath(), title + ".posttransform");
        assertTrue(postTransform.exists());
    }

    @Configuration
    @Profile("IntegrationTests")
    @Import(CommonTestContextConfiguration.class)
    public static class Config {
        @Bean
        public HTMLTransformerService htmlTransformerService() {
            return new HTMLTransformerServiceImpl();
        }
        @Bean
        public FileHandlingHelper transformedFileHandlingHelper() {
            return new FileHandlingHelper();
        }
        @Bean
        public ImageService imageService() {
            return new ImageServiceImpl();
        }
        @Bean
        public DocMetadataService docMetadataService() {
            return EasyMock.createMock(DocMetadataService.class);
        }
        @Bean
        public InternalLinkResolverService internalLinkResolverService() {
            return new InternalLinkResolverService();
        }
        @Bean
        public PaceMetadataService paceMetadataService() {
            return EasyMock.createMock(PaceMetadataServiceImpl.class);
        }
    }
}
