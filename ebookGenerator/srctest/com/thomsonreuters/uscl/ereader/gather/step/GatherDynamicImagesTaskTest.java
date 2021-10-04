package com.thomsonreuters.uscl.ereader.gather.step;

import static com.thomsonreuters.uscl.ereader.gather.step.GatherDynamicImagesTestUtil.captureImageRequest;
import static com.thomsonreuters.uscl.ereader.gather.step.GatherDynamicImagesTestUtil.getChunkContext;
import static com.thomsonreuters.uscl.ereader.gather.step.GatherDynamicImagesTestUtil.getGatherResponse;
import static com.thomsonreuters.uscl.ereader.gather.step.GatherDynamicImagesTestUtil.getManifestFile;
import static org.junit.Assert.assertEquals;
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
import com.thomsonreuters.uscl.ereader.gather.domain.GatherImgRequest;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.image.domain.ImageException;
import com.thomsonreuters.uscl.ereader.gather.image.service.ImageService;
import com.thomsonreuters.uscl.ereader.gather.restclient.service.GatherService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class GatherDynamicImagesTaskTest {
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
    public void setUp() throws IOException {
        when(gatherService.getImg(any())).thenReturn(getGatherResponse());
        when(formatFileSystem.getImageToDocumentManifestFile(any())).thenReturn(getManifestFile(tempFolder));
        when(imageFileSystem.getImageDynamicDirectory(any()))
            .thenReturn(tempFolder.newFolder(JobExecutionKey.IMAGE_DYNAMIC_DEST_DIR));
    }

    @Test
    public void shouldSendRequestToGatherService() throws Exception {
        gatherDynamicImagesTask.execute(null, getChunkContext());

        final GatherImgRequest request = captureImageRequest(gatherService);

        assertNotNull(request.getImgToDocManifestFile());
        assertNotNull(request.getDynamicImageDirectory());
        assertNull(request.getXppSourceImageDirectory());
        assertFalse(request.isXpp());

        verify(imageService).saveImageMetadata(any(), anyLong(), any());

        assertEquals(1, gatherDynamicImagesTask.getJobExecutionPropertyInt(JobExecutionKey.IMAGE_GUID_NUM));
        assertEquals(1, gatherDynamicImagesTask.getJobExecutionPropertyInt(JobExecutionKey.RETRIEVED_IMAGES_COUNT));
    }

    @Test(expected = ImageException.class)
    public void testHasMissingImages() throws Exception {
        final GatherResponse response = getGatherResponse();
        response.setMissingImgCount(1);
        when(gatherService.getImg(any())).thenReturn(response);

        try {
            gatherDynamicImagesTask.execute(null, getChunkContext());
        } finally {
            assertEquals(1, gatherDynamicImagesTask.getJobExecutionPropertyInt(JobExecutionKey.IMAGE_GUID_NUM));
            assertEquals(0, gatherDynamicImagesTask.getJobExecutionPropertyInt(JobExecutionKey.RETRIEVED_IMAGES_COUNT));
        }
    }
}
