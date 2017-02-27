package com.thomsonreuters.uscl.ereader.gather.img.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.exception.GatherException;
import com.thomsonreuters.uscl.ereader.gather.img.model.ImageRequestParameters;
import com.thomsonreuters.uscl.ereader.gather.img.util.DocToImageManifestUtil;
import com.thomsonreuters.uscl.ereader.gather.img.util.ImageConverter;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

/**
 * Unit test for XppImageService.
 */
@RunWith(MockitoJUnitRunner.class)
public final class XppImageServiceTest
{
    private static final String UNPACKED_IMAGES_DIR = "com/thomsonreuters/uscl/ereader/gather/img/service/images";
    private static final String DOC_ID = "docId";
    private static final String IMAGE_ID = "I2943f88028b911e69ed7fcedf0a72426";

    @InjectMocks
    private XppImageService service;

    @Mock
    private DocToImageManifestUtil docToImageManifestUtil;

    @Mock
    private ImageConverter imageConverter;

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Before
    public void init()
    {
        when(docToImageManifestUtil.getDocsWithImages((File) any())).thenReturn(getDocsWithImages());
        when(imageConverter.convertByteImg((byte[]) any(), (String) any(), (String) any()))
            .thenReturn(mock(BufferedImage.class));
    }

    @Test
    public void shouldCopyImagesAndReturnMetadata() throws GatherException, IOException
    {
        final GatherResponse response = service.getImages(getImageRequestParameters());

        final File destinationImageFile = new File(tempFolder.getRoot(), IMAGE_ID + ".png");

        final ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        verify(imageConverter).convertByteImg((byte[]) any(), argument.capture(), (String) any());
        assertTrue(destinationImageFile.getAbsolutePath().equalsIgnoreCase(argument.getValue()));

        assertEquals(DOC_ID, response.getImageMetadataList().get(0).getDocGuid());
        assertEquals(IMAGE_ID, response.getImageMetadataList().get(0).getImgGuid());
    }

    private Map<String, List<String>> getDocsWithImages()
    {
        return Collections.singletonMap(DOC_ID, Collections.singletonList(IMAGE_ID));
    }

    private ImageRequestParameters getImageRequestParameters() throws IOException
    {
        final ImageRequestParameters parameters = new ImageRequestParameters();

        parameters.setXppSourceImageDirectory(
            new PathMatchingResourcePatternResolver().getResource(UNPACKED_IMAGES_DIR).getFile().getAbsolutePath());
        parameters.setDynamicImageDirectory(tempFolder.getRoot());

        return parameters;
    }
}
