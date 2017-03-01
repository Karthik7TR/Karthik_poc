package com.thomsonreuters.uscl.ereader.gather.img.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.exception.GatherException;
import com.thomsonreuters.uscl.ereader.gather.img.model.ImageRequestParameters;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public final class XppImageServiceIntegrationTest
{
    private static final String UNPACKED_IMAGES_DIR = "com/thomsonreuters/uscl/ereader/gather/img/service/images";
    private static final String DOC_TO_IMAGE_FILE =
        "com/thomsonreuters/uscl/ereader/gather/img/service/doc-to-image-manifest.txt";
    private static final String IMAGE_ID = "I2943f88028b911e69ed7fcedf0a72426";

    @Autowired
    private XppImageService service;

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void shouldCopyImagesAndReturnMetadata() throws GatherException, IOException
    {
        final GatherResponse response = service.getImages(getImageRequestParameters());

        final File destinationImageFile = new File(tempFolder.getRoot(), IMAGE_ID + ".png");

        assertTrue(destinationImageFile.exists());
        assertEquals(IMAGE_ID, response.getImageMetadataList().get(0).getImgGuid());
        assertEquals(1733, response.getImageMetadataList().get(0).getWidth().longValue());
        assertEquals(765, response.getImageMetadataList().get(0).getHeight().longValue());
    }

    private ImageRequestParameters getImageRequestParameters() throws IOException
    {
        final ImageRequestParameters parameters = new ImageRequestParameters();

        parameters.setXppSourceImageDirectory(
            new PathMatchingResourcePatternResolver().getResource(UNPACKED_IMAGES_DIR).getFile().getAbsolutePath());
        parameters.setDynamicImageDirectory(tempFolder.getRoot());

        parameters.setDocToImageManifestFile(
            new PathMatchingResourcePatternResolver().getResource(DOC_TO_IMAGE_FILE).getFile());

        return parameters;
    }
}