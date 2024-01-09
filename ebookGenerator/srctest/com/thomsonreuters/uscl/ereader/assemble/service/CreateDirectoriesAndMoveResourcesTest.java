package com.thomsonreuters.uscl.ereader.assemble.service;

import com.thomsonreuters.uscl.ereader.assemble.step.CreateDirectoriesAndMoveResources;
import com.thomsonreuters.uscl.ereader.assemble.step.MoveResourcesUtil;
import com.thomsonreuters.uscl.ereader.common.exception.EBookException;
import com.thomsonreuters.uscl.ereader.context.CommonTestContextConfiguration;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.service.PdfToImgConverter;
import com.thomsonreuters.uscl.ereader.format.step.StepIntegrationTestRunner;
import com.thomsonreuters.uscl.ereader.gather.image.service.ImageService;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetadataService;
import com.thomsonreuters.uscl.ereader.proview.Asset;
import com.thomsonreuters.uscl.ereader.proview.Doc;
import com.thomsonreuters.uscl.ereader.proview.TitleMetadata;
import com.thomsonreuters.uscl.ereader.util.FileUtilsFacade;
import com.thomsonreuters.uscl.ereader.util.FileUtilsFacadeImpl;
import com.thomsonreuters.uscl.ereader.util.UuidGenerator;
import org.apache.tools.ant.util.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.ASSEMBLE_ASSETS_DIR;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {CreateDirectoriesAndMoveResourcesTest.Config.class, StepIntegrationTestRunner.Config.class})
@ActiveProfiles("IntegrationTests")
public final class CreateDirectoriesAndMoveResourcesTest {
    private static final String DOC_UUID = "docUuid";
    private static final String PROVIEW_ID = "proviewId";
    @Autowired
    private CreateDirectoriesAndMoveResources createDirectoriesAndMoveResources;
    private Map<String, List<Doc>> docMap = new HashMap<>();
    private Map<String, List<String>> splitBookImgMap = new HashMap<>();
    private static final String FILE_NAME = "doc-To-SplitBook.txt";
    private File docToSplitBookFile;
    private File tempFile;

    private ExecutionContext jobExecutionContext;
    private File tempRootDir;

    @Autowired
    private StepIntegrationTestRunner runner;

    @Before
    public void setUp() throws Exception {
        tempRootDir = new File((Files.createTempDirectory("YarrMatey")).toString());
        tempFile = makeFile(tempRootDir, "pirate.ship", "don't crash");
        final URL url = this.getClass().getResource(FILE_NAME);
        docToSplitBookFile = new File(url.toURI());
    }

    @After
    public void tearDown() {
        FileUtils.delete(tempFile);
        FileUtils.delete(tempRootDir);
    }

    private File makeFile(final File directory, final String name, final String content) {
        final File file = new File(directory, name);
        try (FileOutputStream out = new FileOutputStream(file)) {
            out.write(content.getBytes());
            out.close();
            return file;
        } catch (final Exception e) {
            return null;
        }
    }

    @Ignore
    @Test
    public void testAddArtwork() {
        //add after making test for MoveResourcesUtil.java
    }

    @Ignore
    @Test
    public void testMovesResources() {
        jobExecutionContext = new ExecutionContext();
        final List<String> imglist = new ArrayList<>();
        final List<Doc> doclist = new ArrayList<>();
        final File tempImg = makeFile(tempRootDir, "img.png", "totally an image file");
        final File assetsDirectory = new File(tempRootDir, ASSEMBLE_ASSETS_DIR.getName());
        createDirectoriesAndMoveResources
            .moveResources(jobExecutionContext, tempRootDir, assetsDirectory, true, imglist, doclist, tempImg);
    }

    @Test
    public void testGetAssetsFromDir() {
        try {
            TitleMetadata.builder().assetFilesFromDirectory(null);
            fail("should have thrown IllegalArgumentException");
        } catch (final IllegalArgumentException e) {
            e.printStackTrace();
            final File temp2 = makeFile(tempRootDir, "ninja.star", "totally exists");
            final List<Asset> assets = TitleMetadata.builder().assetFilesFromDirectory(tempRootDir).build().getAssets();

            assertTrue("Extra assets was found", assets.size() == 2);
            final Asset expectedPiratAsset = new Asset("pirate", "pirate.ship");
            final Asset expectedNinjaAsset = new Asset("ninja", "ninja.star");
            assertTrue(assets.contains(expectedPiratAsset));
            assertTrue(assets.contains(expectedNinjaAsset));

            FileUtils.delete(temp2);
        }
    }

    @Test
    public void testGetAssetsfromFileException() {
        try {
            TitleMetadata.builder().assetFile(null);
            fail("should have thrown IllegalArgumentException");
        } catch (final IllegalArgumentException e) {
            //expected exception
            e.printStackTrace();
        }
    }

    @Test
    public void testGetAssetsfromFile() {
        final Asset asset = TitleMetadata.builder().assetFile(tempFile).build().getAssets().get(0);
        assertTrue(asset.getId().contains("pirate"));
    }

    @Test
    public void testReadDocImgFile() {
        createDirectoriesAndMoveResources.readDocImgFile(docToSplitBookFile, docMap, splitBookImgMap);
        List<Doc> docList = null;

        // Doc List
        final Iterator<Map.Entry<String, List<Doc>>> itr = docMap.entrySet().iterator();
        while (itr.hasNext()) {
            final Map.Entry<String, List<Doc>> pair = itr.next();

            if (pair.getKey().equals(new String("1"))) {
                docList = pair.getValue();
                Assert.assertEquals(docList.size(), 5);
            }
        }

        // Img List
        List<String> imgList = null;
        final Iterator<Map.Entry<String, List<String>>> it = splitBookImgMap.entrySet().iterator();
        while (it.hasNext()) {
            final Map.Entry<String, List<String>> pair = it.next();

            if (pair.getKey().equals(new String("1"))) {
                imgList = pair.getValue();
                Assert.assertEquals(2, imgList.size());
            }
            if (pair.getKey().equals(new String("3"))) {
                Assert.assertNotNull(imgList);
                Assert.assertEquals(0, imgList.size());
            }
        }
    }

    @Test
    public void testMoveResourcesNotFound() throws Exception {
        runner.setUp(createDirectoriesAndMoveResources, false);

        final List<String> imgList = new ArrayList<>();
        final List<Doc> docList = new ArrayList<>();
        final File assetsDirectory = new File(tempRootDir, ASSEMBLE_ASSETS_DIR.getName());

        boolean thrown = false;
        try {
            createDirectoriesAndMoveResources
                .moveResources(createDirectoriesAndMoveResources.getJobExecutionContext(), tempRootDir, assetsDirectory,
                        false, imgList, docList, tempFile);
        } catch (final EBookException e) {
            thrown = true;
        }
        assertTrue(thrown);
    }

    @Test
    public void testGetAssetsNullInput() {
        boolean thrown = false;
        try {
            TitleMetadata.builder().assetFilesFromDirectory(null);
        } catch (final IllegalArgumentException e) {
            thrown = true;
        }
        assertTrue(thrown);
    }

    @Test
    public void testGetAsset() {
        Asset asset = new Asset();
        asset = TitleMetadata.builder().assetFile(tempFile).build().getAssets().get(0);
        System.out.println(asset.toString());
        assertTrue(asset != null);
        assertTrue(asset.getId().equals("pirate"));
        assertTrue(asset.getSrc().equals("pirate.ship"));
    }

    @Configuration
    @Profile("IntegrationTests")
    @Import(CommonTestContextConfiguration.class)
    public static class Config {
        @Bean
        public CreateDirectoriesAndMoveResources createDirectoriesAndMoveResources() {
            return new CreateDirectoriesAndMoveResources();
        }
        @Bean
        public PdfToImgConverter pdfToImgConverter() {
            return new PdfToImgConverter();
        }
        @Bean
        public BookDefinitionService bookDefinitionService() {
            BookDefinitionService bookDefinitionService = mock(BookDefinitionService.class);
            when(bookDefinitionService.getSplitPartsForEbook(any())).thenReturn(2);
            return bookDefinitionService;
        }
        @Bean
        public TitleMetadataService titleMetadataService() {
            return new TitleMetadataServiceImpl();
        }
        @Bean
        public DocMetadataService docMetadataService() {
            DocMetadataService docMetadataService = mock(DocMetadataService.class);
            Map<String, String> familyGuidMap = new HashMap<>();
            familyGuidMap.put(DOC_UUID, PROVIEW_ID);
            when(docMetadataService.findDistinctProViewFamGuidsByJobId(any())).thenReturn(familyGuidMap);
            return docMetadataService;
        }
        @Bean
        public FileUtilsFacade fileUtilsFacade() {
            return new FileUtilsFacadeImpl();
        }
        @Bean
        public UuidGenerator uuidGenerator() {
            return new UuidGenerator();
        }
        @Bean
        public MoveResourcesUtil moveResourcesUtil() {
            return new MoveResourcesUtil();
        }
        @Bean
        public ImageService imageService() {
            ImageService imageService = mock(ImageService.class);
            when(imageService.getDocImageListMap(any())).thenReturn(Collections.emptyMap());
            return imageService;
        }
    }
}