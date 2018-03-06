package com.thomsonreuters.uscl.ereader.xpp.transformation.volumesmap.step;

import static com.thomsonreuters.uscl.ereader.common.filesystem.FileContentMatcher.hasSameContentAs;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.request.domain.XppBundle;
import com.thomsonreuters.uscl.ereader.request.domain.XppBundleWebBuildProductType;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystem;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystemDir;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppGatherFileSystem;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = CreateVolumesMapStepIntegrationTestConfiguration.class)
@ActiveProfiles("IntegrationTests")
public final class CreateVolumesMapStepIntegrationTest {
    private static final String FIRST_MATERIAL = "111111111";
    private static final String SECOND_MATERIAL = "222222222";
    private static final String EXPECTED_VOL_1_FILE = "volumesMap_with_vol1.xml";
    private static final String EXPECTED_VOL_3_FILE = "volumesMap_with_vol3.xml";
    private static final String EXPECTED_MAIN_VOL_SEGOUTLINE_FILE = "volumesMap_main_with_segoutline.xml";
    private static final String EXPECTED_MAIN_VOL_DEFAULT = "volumesMap_main_default.xml";
    private static final String EXPECTED_FULL_SEGOUTLINE_FILE = "volumesMap_full_with_segoutline.xml";
    private static final String EXPECTED_FULL_VOL_DEFAULT_FILE = "volumesMap_full_default.xml";

    @Resource(name = "createVolumesMapTask")
    @InjectMocks
    private CreateVolumesMapStep step;
    @Autowired
    private XppFormatFileSystem fileSystem;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ChunkContext chunkContext;
    @Mock
    private XppGatherFileSystem xppGatherFileSystem;

    @Before
    public void onTestSetUp() {
        org.mockito.MockitoAnnotations.initMocks(this);
    }

    @After
    public void onTestComplete() throws Exception {
        FileUtils.cleanDirectory(fileSystem.getFormatDirectory(step));
    }

    @Test
    public void shouldDefineVolumeNumberAccordingToFileName() throws Exception {
        //given
        initMockData(true, FIRST_MATERIAL);
        //when
        step.executeStep();
        //then
        checkTestResults(FIRST_MATERIAL, EXPECTED_VOL_3_FILE, 4);
    }

    @Test
    public void shouldDefineVolumeNumberAccordingToBundlesCount() throws Exception {
        //given
        initMockData(false, FIRST_MATERIAL);
        //when
        step.executeStep();
        //then
        checkTestResults(FIRST_MATERIAL, EXPECTED_VOL_1_FILE, 4);
    }

    @Test
    public void shouldDefineVolumeNumberInMainContentAccordingToSegOuline() throws Exception {
        //given
        initMockData(true, SECOND_MATERIAL);
        //when
        step.executeStep();
        //then
        checkTestResults(SECOND_MATERIAL, EXPECTED_MAIN_VOL_SEGOUTLINE_FILE, 1);
    }

    @Test
    public void shouldDefineDefaultVolumeNumberInMain() throws Exception {
        //given
        initMockData(false, SECOND_MATERIAL);
        //when
        step.executeStep();
        //then
        checkTestResults(SECOND_MATERIAL, EXPECTED_MAIN_VOL_DEFAULT, 1);
    }

    @Test
    public void shouldCreateFullMapAccordingToSegOutline() throws Exception {
        //given
        initMockData(true, FIRST_MATERIAL, SECOND_MATERIAL);
        //when
        step.executeStep();
        //then
        checkTestResults(FIRST_MATERIAL, EXPECTED_FULL_SEGOUTLINE_FILE, 4);
        checkTestResults(SECOND_MATERIAL, EXPECTED_FULL_SEGOUTLINE_FILE, 1);
    }

    @Test
    public void shouldCreateDefaultFullMap() throws Exception {
        //given
        initMockData(false, FIRST_MATERIAL, SECOND_MATERIAL);
        //when
        step.executeStep();
        //then
        checkTestResults(FIRST_MATERIAL, EXPECTED_FULL_VOL_DEFAULT_FILE, 4);
        checkTestResults(SECOND_MATERIAL, EXPECTED_FULL_VOL_DEFAULT_FILE, 1);
    }

    private void initMockData(final boolean withSegOutline, final String ... materials) throws Exception {
        final List<String> materialNumbers = Optional.ofNullable(materials)
            .map(Stream::of)
            .orElseGet(Stream::empty)
            .collect(Collectors.toList());

        for (final String material : materialNumbers) {
            FileUtils.copyDirectory(getTestFilesDir(material), fileSystem.getDirectory(step, XppFormatFileSystemDir.SOURCE_DIR, material));

            if (withSegOutline) {
                given(xppGatherFileSystem.getSegOutlineFile(step, material))
                    .willReturn(getContentDir().resolve("testdata").resolve("SegOutline.xml").toFile());
            }
        }

        final List<XppBundle> bundles = materialNumbers.stream()
            .map(material -> {
                final XppBundle bundle = new XppBundle();
                bundle.setMaterialNumber(material);
                bundle.setWebBuildProductType(XppBundleWebBuildProductType.PAMPHLET);
                return bundle;
            }).collect(Collectors.toList());

        given(chunkContext.getStepContext()
            .getStepExecution()
            .getJobExecution()
            .getExecutionContext()
            .get(JobParameterKey.XPP_BUNDLES))
        .willReturn(bundles);
    }

    private void checkTestResults(final String material, final String expectedFile, final int arrayLength) {
        assertThat(fileSystem.getDirectory(step, XppFormatFileSystemDir.VOLUMES_MAP_DIR, material).listFiles(), arrayWithSize(arrayLength));
        assertThat(fileSystem.getVolumesMapFile(step), hasSameContentAs(getExpectedFile(expectedFile)));
    }

    private File getTestFilesDir(final String material) {
        return getContentDir().resolve("testdata").resolve(material).toFile();
    }

    private File getExpectedFile(final String fileName) {
        return getContentDir().resolve("expecteddata").resolve(fileName).toFile();
    }

    private Path getContentDir() {
        return Paths.get("IntegrationTests", "com", "thomsonreuters", "uscl", "ereader", "xpp", "transformation", "volumesmap");
    }
}
