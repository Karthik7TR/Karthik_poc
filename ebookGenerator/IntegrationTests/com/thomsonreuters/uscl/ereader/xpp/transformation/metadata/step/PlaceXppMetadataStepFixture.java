package com.thomsonreuters.uscl.ereader.xpp.transformation.metadata.step;

import static com.thomsonreuters.uscl.ereader.common.filesystem.FileContentMatcher.hasSameContentAs;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.request.domain.XppBundle;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystem;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.After;
import org.junit.Before;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class PlaceXppMetadataStepFixture
{
    protected static final String VOL_MATERIAL_NUMBER = "1111111";

    @Resource(name = "placeXppMetadataTask")
    @InjectMocks
    protected PlaceXppMetadataStep step;
    @Autowired
    protected XppFormatFileSystem fileSystem;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    protected ChunkContext chunkContext;

    protected File expected;
    protected File source;

    PlaceXppMetadataStepFixture(@NotNull final String sourcePath, @NotNull final String expectedPath)
        throws URISyntaxException
    {
        source = new File(PlaceXppMetadataStepFixture.class.getResource(sourcePath).toURI());
        expected = new File(PlaceXppMetadataStepFixture.class.getResource(expectedPath).toURI());
    }

    @Before
    public void setUp() throws Exception
    {
        initMocks();
        prepareDirectories();
    }

    @After
    public void clean() throws IOException
    {
        FileUtils.cleanDirectory(fileSystem.getFormatDirectory(step));
    }

    protected void testPlacedMetadata() throws Exception
    {
        //given
        //when
        step.executeStep();
        //then
        final File actual = fileSystem
            .getStructureWithMetadataFile(step, VOL_MATERIAL_NUMBER, source.getName());
        assertThat(expected, hasSameContentAs(actual));
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

    protected List<XppBundle> getBundlesList()
    {
        final XppBundle volumeOneBundle = new XppBundle();
        volumeOneBundle.setMaterialNumber(VOL_MATERIAL_NUMBER);
        volumeOneBundle.setOrderedFileList(Arrays.asList("0-CHALSource_Front_vol_1.DIVXML.main"));
        return Arrays.asList(volumeOneBundle);
    }

    private void prepareDirectories() throws IOException
    {
        final File originalDir = fileSystem.getOriginalDirectory(step, VOL_MATERIAL_NUMBER);
        FileUtils.forceMkdir(originalDir);
        FileUtils.copyFileToDirectory(source, originalDir);
    }
}
