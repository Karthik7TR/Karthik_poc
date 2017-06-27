package com.thomsonreuters.uscl.ereader.xpp.transformation.place.xpp.metadata.step;

import static com.thomsonreuters.uscl.ereader.common.filesystem.FileContentMatcher.hasSameContentAs;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.request.domain.XppBundle;
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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public final class PlaceXppMetadataStepIntegrationTest
{
    private static final String VOL_MATERIAL_NUMBER = "1111111";

    @Resource(name = "placeXppMetadataTask")
    @InjectMocks
    private PlaceXppMetadataStep step;
    @Autowired
    private XppFormatFileSystem fileSystem;

    private File expectedFrontMatterWithMetadataFile;
    private File sourceFrontMatterFile;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ChunkContext chunkContext;

    @Before
    public void setUp() throws Exception
    {
        initMocks();
        initFiles();
        prepareDirectories();
    }

    private void initMocks()
    {
        org.mockito.MockitoAnnotations.initMocks(this);
        when(
            chunkContext.getStepContext()
                .getStepExecution()
                .getJobExecution()
                .getExecutionContext()
                .get(JobParameterKey.XPP_BUNDLES)).thenReturn(getBundlesList());
    }

    private void initFiles() throws Exception
    {
        sourceFrontMatterFile =
            new File(PlaceXppMetadataStepIntegrationTest.class.getResource("0-CHALSource_Front_vol_1.DIVXML.main").toURI());
        expectedFrontMatterWithMetadataFile =
            new File(PlaceXppMetadataStepIntegrationTest.class.getResource("0-CHALExpected_Front_vol_1.DIVXML.main").toURI());
    }

    private void prepareDirectories() throws Exception
    {
        final File originalDir = fileSystem.getOriginalDirectory(step, VOL_MATERIAL_NUMBER);

        FileUtils.forceMkdir(originalDir);
        FileUtils.copyFileToDirectory(sourceFrontMatterFile, originalDir);
    }

    private List<XppBundle> getBundlesList()
    {
        final XppBundle volumeOneBundle = new XppBundle();
        volumeOneBundle.setMaterialNumber(VOL_MATERIAL_NUMBER);
        volumeOneBundle.setOrderedFileList(Arrays.asList("0-CHALSource_Front_vol_1.DIVXML.main"));

        return Arrays.asList(volumeOneBundle);
    }

    @After
    public void clean() throws IOException
    {
        FileUtils.cleanDirectory(fileSystem.getFormatDirectory(step));
    }

    @Test
    public void shouldInsertCorrectMetadataAndHierToOriginalFile() throws Exception
    {
        //given
        //when
        step.executeStep();
        //then
        assertThat(
            expectedFrontMatterWithMetadataFile,
            hasSameContentAs(
                fileSystem.getStructureWithMetadataFile(step, VOL_MATERIAL_NUMBER, "0-CHALSource_Front_vol_1.DIVXML.main")));
    }
}
