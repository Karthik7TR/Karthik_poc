package com.thomsonreuters.uscl.ereader.gather.step;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem;
import com.thomsonreuters.uscl.ereader.common.filesystem.ImageFileSystem;
import com.thomsonreuters.uscl.ereader.common.filesystem.XppUnpackFileSystem;
import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherImgRequest;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.image.domain.ImageException;
import com.thomsonreuters.uscl.ereader.gather.image.service.ImageService;
import com.thomsonreuters.uscl.ereader.gather.restclient.service.GatherService;
import com.thomsonreuters.uscl.ereader.gather.util.ImgMetadataInfo;
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
    private GatherService gatherService;

    @Mock
    private FormatFileSystem formatFileSystem;

    @Mock
    private ImageFileSystem imageFileSystem;

    @Mock
    private XppUnpackFileSystem xppUnpackFileSystem;

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Before
    public void setUp() throws IOException
    {
        when(gatherService.getImg((GatherImgRequest) any())).thenReturn(getGatherResponse());
        when(formatFileSystem.getImageToDocumentManifestFile((BookStep)any())).thenReturn(getManifestFile());
        when(imageFileSystem.getImageDynamicDirectory((BookStep)any())).thenReturn(tempFolder.newFolder(JobExecutionKey.IMAGE_DYNAMIC_DEST_DIR));
    }

    private GatherResponse getGatherResponse()
    {
        final GatherResponse response = new GatherResponse();
        response.setImageMetadataList(Collections.singletonList(new ImgMetadataInfo()));
        return response;
    }

    @Test
    public void shouldSendRequestToGatherService() throws Exception
    {
        gatherDynamicImagesTask.execute(null, getChunkContext(false));

        final GatherImgRequest request = captureImageRequest();

        assertNotNull(request.getImgToDocManifestFile());
        assertNotNull(request.getDynamicImageDirectory());
        assertNull(request.getXppSourceImageDirectory());
        assertFalse(request.isXpp());

        verify(imageService).saveImageMetadata((ImgMetadataInfo) any(), anyLong(), (String) any());
    }

    @Test
    public void shouldSendRequestToGatherServiceXppPathway() throws Exception
    {
        gatherDynamicImagesTask.execute(null, getChunkContext(true));
        final GatherImgRequest request = captureImageRequest();
        assertNotNull(request.getXppSourceImageDirectory());
    }

    @Test(expected = ImageException.class)
    public void testHasMissingImages() throws Exception
    {
        final GatherResponse response = getGatherResponse();
        response.setMissingImgCount(1);
        when(gatherService.getImg((GatherImgRequest) any())).thenReturn(response);

        gatherDynamicImagesTask.execute(null, getChunkContext(true));
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
        final ChunkContext chunkContext = new ChunkContext(
            new StepContext(
                new StepExecution("stepName", new JobExecution(new JobInstance(0L, "jobName"), jobParameters))));
        final ExecutionContext context =
            chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext();

        context.put(JobExecutionKey.EBOOK_DEFINITION, new BookDefinition());

        if (isXpp)
        {
            when(xppUnpackFileSystem.getXppAssetsDirectory((BookStep)any())).thenReturn(tempFolder.newFolder(JobExecutionKey.XPP_IMAGES_UNPACK_DIR).getAbsolutePath());
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
