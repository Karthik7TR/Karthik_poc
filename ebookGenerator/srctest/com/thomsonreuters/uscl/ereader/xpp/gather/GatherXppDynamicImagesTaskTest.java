package com.thomsonreuters.uscl.ereader.xpp.gather;

import static com.thomsonreuters.uscl.ereader.gather.step.GatherDynamicImagesTestUtil.captureImageRequest;
import static com.thomsonreuters.uscl.ereader.gather.step.GatherDynamicImagesTestUtil.getChunkContext;
import static com.thomsonreuters.uscl.ereader.gather.step.GatherDynamicImagesTestUtil.getGatherResponse;
import static com.thomsonreuters.uscl.ereader.gather.step.GatherDynamicImagesTestUtil.getManifestFile;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Collections;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem;
import com.thomsonreuters.uscl.ereader.common.filesystem.ImageFileSystem;
import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherImgRequest;
import com.thomsonreuters.uscl.ereader.gather.image.service.ImageService;
import com.thomsonreuters.uscl.ereader.gather.restclient.service.GatherService;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppGatherFileSystem;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public final class GatherXppDynamicImagesTaskTest
{
    @InjectMocks
    private GatherXppDynamicImagesTask gatherDynamicImagesTask;
    @Mock
    private ImageService imageService;
    @Mock
    private GatherService gatherService;
    @Mock
    private FormatFileSystem formatFileSystem;
    @Mock
    private ImageFileSystem imageFileSystem;
    @Mock
    private XppGatherFileSystem xppGatherFileSystem;

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Before
    public void setUp() throws IOException
    {
        when(gatherService.getImg((GatherImgRequest) any())).thenReturn(getGatherResponse());
        when(formatFileSystem.getImageToDocumentManifestFile((BookStep) any())).thenReturn(getManifestFile(tempFolder));
        when(imageFileSystem.getImageDynamicDirectory((BookStep) any()))
            .thenReturn(tempFolder.newFolder(JobExecutionKey.IMAGE_DYNAMIC_DEST_DIR));

        when(xppGatherFileSystem.getXppAssetsDirectories((BookStep) any())).thenReturn(
            Collections.singletonList(tempFolder.newFolder(JobExecutionKey.XPP_IMAGES_UNPACK_DIR).getAbsolutePath()));
    }

    @Test
    public void shouldSendRequestToGatherServiceXppPathway() throws Exception
    {
        gatherDynamicImagesTask.execute(null, getChunkContext());
        final GatherImgRequest request = captureImageRequest(gatherService);
        assertNotNull(request.getXppSourceImageDirectory());
    }
}
