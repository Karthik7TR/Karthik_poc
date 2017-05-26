package com.thomsonreuters.uscl.ereader.gather.step;

import static com.thomsonreuters.uscl.ereader.gather.step.GatherDynamicImagesTestUtil.captureImageRequest;
import static com.thomsonreuters.uscl.ereader.gather.step.GatherDynamicImagesTestUtil.getChunkContext;
import static com.thomsonreuters.uscl.ereader.gather.step.GatherDynamicImagesTestUtil.getGatherResponse;
import static com.thomsonreuters.uscl.ereader.gather.step.GatherDynamicImagesTestUtil.getManifestFile;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem;
import com.thomsonreuters.uscl.ereader.common.filesystem.ImageFileSystem;
import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherImgRequest;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.image.domain.ImageException;
import com.thomsonreuters.uscl.ereader.gather.image.service.ImageService;
import com.thomsonreuters.uscl.ereader.gather.restclient.service.GatherService;
import com.thomsonreuters.uscl.ereader.gather.util.ImgMetadataInfo;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class GatherDynamicImagesTaskTest
{
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

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Before
    public void setUp() throws IOException
    {
        when(gatherService.getImg((GatherImgRequest) any())).thenReturn(getGatherResponse());
        when(formatFileSystem.getImageToDocumentManifestFile((BookStep)any())).thenReturn(getManifestFile(tempFolder));
        when(imageFileSystem.getImageDynamicDirectory((BookStep)any())).thenReturn(tempFolder.newFolder(JobExecutionKey.IMAGE_DYNAMIC_DEST_DIR));
    }

    @Test
    public void shouldSendRequestToGatherService() throws Exception
    {
        gatherDynamicImagesTask.execute(null, getChunkContext());

        final GatherImgRequest request = captureImageRequest(gatherService);

        assertNotNull(request.getImgToDocManifestFile());
        assertNotNull(request.getDynamicImageDirectory());
        assertNull(request.getXppSourceImageDirectory());
        assertFalse(request.isXpp());

        verify(imageService).saveImageMetadata((ImgMetadataInfo) any(), anyLong(), (String) any());
    }

    @Test(expected = ImageException.class)
    public void testHasMissingImages() throws Exception
    {
        final GatherResponse response = getGatherResponse();
        response.setMissingImgCount(1);
        when(gatherService.getImg((GatherImgRequest) any())).thenReturn(response);

        gatherDynamicImagesTask.execute(null, getChunkContext());
    }
}
