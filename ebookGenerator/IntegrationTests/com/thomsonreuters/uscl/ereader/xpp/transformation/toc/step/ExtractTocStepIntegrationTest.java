package com.thomsonreuters.uscl.ereader.xpp.transformation.toc.step;

import static com.thomsonreuters.uscl.ereader.common.filesystem.FileContentMatcher.hasSameContentAs;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
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
    public void setUp() throws Exception {
        initMocks();
        initFiles();
        prepareDirectories();
    }

    private void initMocks() {
        org.mockito.MockitoAnnotations.initMocks(this);
        when(
            chunkContext.getStepContext()
                .getStepExecution()
                .getJobExecution()
                .getExecutionContext()
                .get(JobParameterKey.XPP_BUNDLES)).thenReturn(getBundlesList());
    }

    private void initFiles() throws Exception {
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
        expectedTocFile = new File(ExtractTocStepIntegrationTest.class.getResource("expectedToc.xml").toURI());
    }

    private void prepareDirectories() throws Exception {
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
    }

    private List<XppBundle> getBundlesList() {
        final XppBundle volumeOneBundle = new XppBundle();
        volumeOneBundle.setMaterialNumber(VOL_TWO_MATERIAL_NUMBER);
        volumeOneBundle.setOrderedFileList(Arrays.asList("mainContent2.DIVXML.xml", "mainContent3.DIVXML.xml"));
        volumeOneBundle.setProductType("bound");

        final XppBundle volumeTwoBundle = new XppBundle();
        volumeTwoBundle.setMaterialNumber(VOL_ONE_MATERIAL_NUMBER);
        volumeTwoBundle.setOrderedFileList(Arrays.asList("mainContent1.DIVXML.xml", "mainContent1_1.DIVXML.xml"));
        volumeTwoBundle.setProductType("supp");
        volumeTwoBundle.setWebBuildProductType(XppBundleWebBuildProductType.SUPPLEMENTARY_PAMPHLET);

        return Arrays.asList(volumeOneBundle, volumeTwoBundle);
    }

    @After
    public void clean() throws IOException {
        FileUtils.cleanDirectory(fileSystem.getFormatDirectory(step));
    }

    @Test
    public void shouldCreateTocFileBasedBundleMainContentOriginalFile() throws Exception {
        step.executeStep();
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
