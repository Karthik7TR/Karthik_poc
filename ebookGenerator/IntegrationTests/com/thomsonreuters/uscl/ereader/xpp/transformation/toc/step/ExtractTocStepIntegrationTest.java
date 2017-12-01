package com.thomsonreuters.uscl.ereader.xpp.transformation.toc.step;

import static com.thomsonreuters.uscl.ereader.common.filesystem.FileContentMatcher.hasSameContentAs;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.context.CommonTestContextConfiguration;
import com.thomsonreuters.uscl.ereader.request.domain.XppBundle;
import com.thomsonreuters.uscl.ereader.request.domain.XppBundleWebBuildProductType;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystem;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@ActiveProfiles("IntegrationTests")
public final class ExtractTocStepIntegrationTest {
    private static final String VOL_ONE_MATERIAL_NUMBER = "1111111";
    private static final String VOL_TWO_MATERIAL_NUMBER = "2222222";
    private static final String VOL_THREE_MATERIAL_NUMBER = "3333333";
    private static final String VOL_FOUR_MATERIAL_NUMBER = "4444444";

    @Resource(name = "extractTocTask")
    @InjectMocks
    private ExtractTocStep step;
    @Autowired
    private XppFormatFileSystem fileSystem;

    private File vol1File;
    private File vol1RutterWitkinFile;
    private File vol2File;
    private File vol2XppHierWithoutChildXppMetadataFile;
    private File expectedMainContentTocFile;
    private File expectedMainContentAdditionalTocFile;
    private File expectedMainContentXppHierWithoutChildXppMetadataFile;
    private File expectedTocFile;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ChunkContext chunkContext;

    @Before
    public void setUp() {
        org.mockito.MockitoAnnotations.initMocks(this);
    }

    private void init(final boolean multipleVolumes) throws Exception {
        initMocks(multipleVolumes);
        initFiles(multipleVolumes);
        prepareDirectories(multipleVolumes);
    }

    private void initMocks(final boolean multipleVolumes) {
        when(
            chunkContext.getStepContext()
                .getStepExecution()
                .getJobExecution()
                .getExecutionContext()
                .get(JobParameterKey.XPP_BUNDLES)).thenReturn(getBundlesList(multipleVolumes));
    }

    private void initFiles(final boolean multipleVolumes) throws Exception {
        vol1File =
            new File(ExtractTocStepIntegrationTest.class.getResource("mainContent1.DIVXML.main").toURI());
        vol1RutterWitkinFile =
            new File(ExtractTocStepIntegrationTest.class.getResource("mainContent1_1.DIVXML.main").toURI());
        vol2File =
            new File(ExtractTocStepIntegrationTest.class.getResource("mainContent2.DIVXML.main").toURI());
        vol2XppHierWithoutChildXppMetadataFile =
            new File(ExtractTocStepIntegrationTest.class.getResource("mainContent3.DIVXML.main").toURI());
        expectedMainContentTocFile =
            new File(ExtractTocStepIntegrationTest.class.getResource("expectedMainContent_1_TocFile.xml").toURI());
        expectedMainContentAdditionalTocFile =
            new File(ExtractTocStepIntegrationTest.class.getResource("expectedMainContent_2_TocFile.xml").toURI());
        expectedMainContentXppHierWithoutChildXppMetadataFile =
            new File(ExtractTocStepIntegrationTest.class.getResource("expectedMainContent_3_TocFile.xml").toURI());

        if (multipleVolumes) {
            expectedTocFile = new File(ExtractTocStepIntegrationTest.class.getResource("expectedTocMulti.xml").toURI());
        } else {
            expectedTocFile = new File(ExtractTocStepIntegrationTest.class.getResource("expectedToc.xml").toURI());
        }
    }

    private void prepareDirectories(final boolean multipleVolumes) throws Exception {
        final File bundleVolOneOriginalFilesDir = fileSystem.getSectionbreaksDirectory(step, VOL_ONE_MATERIAL_NUMBER);
        FileUtils.forceMkdir(bundleVolOneOriginalFilesDir);
        FileUtils.copyFileToDirectory(vol1File, bundleVolOneOriginalFilesDir);
        FileUtils.copyFileToDirectory(vol1RutterWitkinFile, bundleVolOneOriginalFilesDir);

        final File bundleVolTwoOriginalFilesDir = fileSystem.getSectionbreaksDirectory(step, VOL_TWO_MATERIAL_NUMBER);
        FileUtils.forceMkdir(bundleVolTwoOriginalFilesDir);
        FileUtils.copyFileToDirectory(vol2File, bundleVolTwoOriginalFilesDir);
        FileUtils.copyFileToDirectory(
            vol2XppHierWithoutChildXppMetadataFile,
            bundleVolTwoOriginalFilesDir);

        if (multipleVolumes) {
            final File bundleVolThreeOriginalFilesDir = fileSystem.getSectionbreaksDirectory(step, VOL_THREE_MATERIAL_NUMBER);
            FileUtils.forceMkdir(bundleVolThreeOriginalFilesDir);
            FileUtils.copyFileToDirectory(vol1RutterWitkinFile, bundleVolThreeOriginalFilesDir);

            final File bundleVolFourOriginalFilesDir = fileSystem.getSectionbreaksDirectory(step, VOL_FOUR_MATERIAL_NUMBER);
            FileUtils.forceMkdir(bundleVolFourOriginalFilesDir);
            FileUtils.copyFileToDirectory(vol1RutterWitkinFile, bundleVolFourOriginalFilesDir);
        }
    }

    private List<XppBundle> getBundlesList(final boolean multipleVolumes) {
        final XppBundle volumeOneBundle = new XppBundle();
        volumeOneBundle.setMaterialNumber(VOL_TWO_MATERIAL_NUMBER);
        volumeOneBundle.setOrderedFileList(Arrays.asList("mainContent2.DIVXML.xml", "mainContent3.DIVXML.xml"));
        volumeOneBundle.setProductType("bound");

        final XppBundle volumeTwoBundle = new XppBundle();
        volumeTwoBundle.setMaterialNumber(VOL_ONE_MATERIAL_NUMBER);
        volumeTwoBundle.setOrderedFileList(Arrays.asList("mainContent1.DIVXML.xml", "mainContent1_1.DIVXML.xml"));
        volumeTwoBundle.setProductType("supp");
        volumeTwoBundle.setWebBuildProductType(XppBundleWebBuildProductType.SUPPLEMENTARY_PAMPHLET);

        final List<XppBundle> bundles = new ArrayList<>(Arrays.asList(volumeOneBundle, volumeTwoBundle));

        if (multipleVolumes) {
            final XppBundle volumeThreeBundle = new XppBundle();
            volumeThreeBundle.setMaterialNumber(VOL_THREE_MATERIAL_NUMBER);
            volumeThreeBundle.setOrderedFileList(Arrays.asList("mainContent1_1.DIVXML.xml"));
            volumeThreeBundle.setProductType("bound");

            final XppBundle volumeFourBundle = new XppBundle();
            volumeFourBundle.setMaterialNumber(VOL_FOUR_MATERIAL_NUMBER);
            volumeFourBundle.setOrderedFileList(Arrays.asList("mainContent1_1.DIVXML.xml"));
            volumeFourBundle.setProductType("supp");
            volumeFourBundle.setWebBuildProductType(XppBundleWebBuildProductType.SUPPLEMENTARY_PAMPHLET);

            bundles.add(volumeThreeBundle);
            bundles.add(volumeFourBundle);
        }

        return bundles;
    }

    @After
    public void clean() throws IOException {
        FileUtils.cleanDirectory(fileSystem.getFormatDirectory(step));
    }

    @Test
    public void shouldCreateTocFileBasedBundleMainContentOriginalFile() throws Exception {
        //given
        init(false);
        //when
        step.executeStep();
        //then
        assertThat(
            expectedMainContentTocFile,
            hasSameContentAs(
                fileSystem.getBundlePartTocFile("mainContent1.DIVXML.xml", VOL_ONE_MATERIAL_NUMBER, step)));
        assertThat(
            expectedMainContentAdditionalTocFile,
            hasSameContentAs(
                fileSystem.getBundlePartTocFile("mainContent2.DIVXML.xml", VOL_TWO_MATERIAL_NUMBER, step)));
        assertThat(
            expectedMainContentXppHierWithoutChildXppMetadataFile,
            hasSameContentAs(
                fileSystem.getBundlePartTocFile("mainContent3.DIVXML.xml", VOL_TWO_MATERIAL_NUMBER, step)));
        assertThat(expectedTocFile, hasSameContentAs(fileSystem.getTocFile(step)));
    }

    @Test
    public void shouldCreateTocFileBasedBundleMainContentOriginalFileForMultipleVolumes() throws Exception {
        //given
        init(true);
        //when
        step.executeStep();
        //then
        final File toc = fileSystem.getTocFile(step);
        assertThat(expectedTocFile, hasSameContentAs(toc));
    }

    @Configuration
    @Profile("IntegrationTests")
    @Import(CommonTestContextConfiguration.class)
    public static class ExtractTocStepIntegrationTestConfiguration {
        @Bean(name = "extractTocTask")
        public ExtractTocStep extractTocTask() {
            return new ExtractTocStep();
        }
    }
}
