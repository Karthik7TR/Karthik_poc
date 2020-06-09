package com.thomsonreuters.uscl.ereader.assemble.service;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.assemble.step.CreateDirectoriesAndMoveResources;
import com.thomsonreuters.uscl.ereader.assemble.step.MoveResourcesUtil;
import com.thomsonreuters.uscl.ereader.context.CommonTestContextConfiguration;
import com.thomsonreuters.uscl.ereader.core.service.JsoupService;
import com.thomsonreuters.uscl.ereader.core.service.PdfToImgConverter;
import com.thomsonreuters.uscl.ereader.format.step.AddThesaurusToDocumentsStep;
import com.thomsonreuters.uscl.ereader.format.step.AddThesaurusToDocumentsStepIntegrationTest;
import com.thomsonreuters.uscl.ereader.format.step.StepIntegrationTestRunner;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.CanadianTopicCode;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.CanadianTopicCodeService;
import com.thomsonreuters.uscl.ereader.proview.Asset;
import com.thomsonreuters.uscl.ereader.proview.Doc;
import com.thomsonreuters.uscl.ereader.proview.TitleMetadata;
import org.apache.commons.lang3.StringUtils;
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.thomsonreuters.uscl.ereader.StepTestUtil.whenJobExecutionPropertyString;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {CreateDirectoriesAndMoveResourcesTest.Config.class, StepIntegrationTestRunner.Config.class})
@ActiveProfiles("IntegrationTests")
public final class CreateDirectoriesAndMoveResourcesTest {
    private CreateDirectoriesAndMoveResources createDirectoriesAndMoveResources;
    private Map<String, List<Doc>> docMap = new HashMap<>();
    private Map<String, List<String>> splitBookImgMap = new HashMap<>();
    private static final String FILE_NAME = "doc-To-SplitBook.txt";
    private File docToSplitBookFile;
    private File tempFile;

    private ExecutionContext jobExecutionContext;
    private File tempRootDir;
    private MoveResourcesUtil moveResourcesUtil;

    @Autowired
    private StepIntegrationTestRunner runner;

    @Before
    public void setUp() throws Exception {
        createDirectoriesAndMoveResources = new CreateDirectoriesAndMoveResources();
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
    public void testMovesResources() throws Exception {
        jobExecutionContext = new ExecutionContext();
        jobExecutionContext.put(JobExecutionKey.IMAGE_STATIC_DEST_DIR, tempRootDir.getAbsolutePath());
        final List<String> imglist = new ArrayList<>();
        final List<Doc> doclist = new ArrayList<>();
        final File tempImg = makeFile(tempRootDir, "img.png", "totally an image file");
        createDirectoriesAndMoveResources
            .moveResources(jobExecutionContext, tempRootDir, true, imglist, doclist, tempImg);
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
    public void testDuplicateAssets() {
        splitBookImgMap = new HashMap<>();
        final List<String> imgList1 = new ArrayList<>();
        imgList1.add("img1.xml");
        imgList1.add("img2.xml");

        splitBookImgMap.put("Doc1", imgList1);

        final List<String> imgList2 = new ArrayList<>();
        imgList2.add("img0.xml");
        imgList2.add("img2.xml");
        splitBookImgMap.put("Doc2", imgList2);

        final List<Asset> assetsForSplitBook = new ArrayList<>();

        for (final Map.Entry<String, List<String>> entry : splitBookImgMap.entrySet()) {
            for (final String imgFileName : entry.getValue()) {
                final Asset asset = new Asset(StringUtils.substringBeforeLast(imgFileName, "."), imgFileName);
                //To avoid duplicate asset
                if (!assetsForSplitBook.contains(asset)) {
                    assetsForSplitBook.add(asset);
                }
            }
        }

        assertTrue(assetsForSplitBook.size() == 3);
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
        runner.setUp(createDirectoriesAndMoveResources);
        whenJobExecutionPropertyString(createDirectoriesAndMoveResources.getJobExecutionContext(), JobExecutionKey.IMAGE_STATIC_DEST_DIR, "dir");

        final List<String> imgList = new ArrayList<>();
        final List<Doc> docList = new ArrayList<>();
        moveResourcesUtil = new MoveResourcesUtil();

        boolean thrown = false;
        try {
            createDirectoriesAndMoveResources.setMoveResourcesUtil(moveResourcesUtil);
            createDirectoriesAndMoveResources
                .moveResources(createDirectoriesAndMoveResources.getJobExecutionContext(), tempRootDir, false, imgList, docList, tempFile);
        } catch (final FileNotFoundException e) {
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
        public PdfToImgConverter pdfToImgConverter() {
            return new PdfToImgConverter();
        }
    }
}
