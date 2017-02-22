package com.thomsonreuters.uscl.ereader.gather.step;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherImgRequest;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.image.domain.ImageException;
import com.thomsonreuters.uscl.ereader.gather.image.service.ImageService;
import com.thomsonreuters.uscl.ereader.gather.restclient.service.GatherService;
import com.thomsonreuters.uscl.ereader.gather.util.ImgMetadataInfo;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.item.ExecutionContext;

@RunWith(MockitoJUnitRunner.class)
public class GatherDynamicImagesTaskTest
{
    private static final String STATUS_FAILED = "Failed";

    @InjectMocks
    private GatherDynamicImagesTask gatherDynamicImagesTask;

    @Mock
    private ImageService imageService;

    @Mock
    private PublishingStatsService publishingStatsService;

    @Mock
    private GatherService gatherService;

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Before
    public void setUp()
    {
        when(gatherService.getImg((GatherImgRequest)any())).thenReturn(getGatherResponse());
    }

    private GatherResponse getGatherResponse()
    {
        final GatherResponse response = new GatherResponse();
        response.setImageMetadataList(Collections.singletonList(new ImgMetadataInfo()));
        return response;
    }

    @Test
    public void shouldSendRequestToGatherService() throws Exception {
        gatherDynamicImagesTask.executeStep(null, getChunkContext(false));

        final GatherImgRequest request = captureImageRequest();

        assertNotNull(request.getImgToDocManifestFile());
        assertNotNull(request.getDynamicImageDirectory());
        assertNull(request.getXppSourceImageDirectory());
        assertFalse(request.isXpp());

        verify(imageService).saveImageMetadata((ImgMetadataInfo)any(), anyLong(), (String)any());

        verify(publishingStatsService, times(1)).updatePublishingStats((PublishingStats)any(), (StatsUpdateTypeEnum)any());
    }

    @Test
    public void shouldSendRequestToGatherServiceXppPathway() throws Exception {
        gatherDynamicImagesTask.executeStep(null, getChunkContext(true));
        final GatherImgRequest request = captureImageRequest();
        assertNotNull(request.getXppSourceImageDirectory());
//        assertTrue(request.isXpp());
    }

    @Test(expected=ImageException.class)
    public void testHasMissingImages() throws Exception {
        final GatherResponse response = getGatherResponse();
        response.setMissingImgCount(1);
        when(gatherService.getImg((GatherImgRequest)any())).thenReturn(response);

        try
        {
            gatherDynamicImagesTask.executeStep(null, getChunkContext(true));
        }
        finally
        {
            final ArgumentCaptor<PublishingStats> argument = ArgumentCaptor.forClass(PublishingStats.class);
            verify(publishingStatsService).updatePublishingStats(argument.capture(), (StatsUpdateTypeEnum)any());
            assertTrue(argument.getValue().getPublishStatus().endsWith(STATUS_FAILED));
        }
    }

    private GatherImgRequest captureImageRequest()
    {
        final ArgumentCaptor<GatherImgRequest> argument = ArgumentCaptor.forClass(GatherImgRequest.class);
        verify(gatherService).getImg(argument.capture());
        final GatherImgRequest request = argument.getValue();
        return request;
    }

    private ChunkContext getChunkContext(final boolean isXpp) throws IOException
    {
        final JobParameters jobParameters = new JobParameters();
        final ChunkContext chunkContext = new ChunkContext(new StepContext(new StepExecution("stepName", new JobExecution(new JobInstance(0L, "jobName"), jobParameters))));
        final ExecutionContext context = chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext();

        context.put(JobExecutionKey.EBOOK_DEFINITION, new BookDefinition());
        context.put(JobExecutionKey.IMAGE_DYNAMIC_DEST_DIR, tempFolder.newFolder(JobExecutionKey.IMAGE_DYNAMIC_DEST_DIR).getAbsolutePath());
        context.put(JobExecutionKey.IMAGE_TO_DOC_MANIFEST_FILE, getManifestFile().getAbsolutePath());

        if (isXpp) {
            context.put(JobExecutionKey.XPP_IMAGES_UNPACK_DIR, tempFolder.newFolder(JobExecutionKey.XPP_IMAGES_UNPACK_DIR).getAbsolutePath());
        }

        return chunkContext;
    }

    private File getManifestFile() throws IOException
    {
        final File manifestFile = tempFolder.newFile(JobExecutionKey.IMAGE_TO_DOC_MANIFEST_FILE);
        FileUtils.writeStringToFile(manifestFile, "docId|I2943f88028b911e69ed7fcedf0a72426");
        return manifestFile;
    }
}
