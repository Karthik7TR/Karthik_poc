package com.thomsonreuters.uscl.ereader.gather.img.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.exception.GatherException;
import com.thomsonreuters.uscl.ereader.gather.img.model.ImageRequestParameters;
import com.thomsonreuters.uscl.ereader.gather.util.ImgMetadataInfo;
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
public final class XppImageServiceIntegrationTest {
    private static final String UNPACKED_IMAGES_DIR1 =
        "com/thomsonreuters/uscl/ereader/gather/img/service/images/bundle1";
    private static final String UNPACKED_IMAGES_DIR2 =
        "com/thomsonreuters/uscl/ereader/gather/img/service/images/bundle2";
    private static final String DOC_TO_IMAGE_FILE =
        "com/thomsonreuters/uscl/ereader/gather/img/service/doc-to-image-manifest.txt";
    private static final String TIF_IMAGE_ID = "I2943f88028b911e69ed7fcedf0a72426";
    private static final String TIFF_IMAGE_ID = "I3749e7f028b911e69ed7fcedf0a72426";
    private static final String TIFF_IMAGE_ID_2 = "tiffImage";
    private static final String PNG_IMAGE_ID = "I3831d6f128b911e69ed7fcedf0a72426";
    private static final String NO_EXTENSION_TIFF = "noExtensionTiff";
    private static final String NO_EXTENSION_PNG = "noExtensionPng";

    @Autowired
    private XppImageService service;

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void shouldCopyImagesAndReturnMetadata() throws GatherException, IOException {
        final GatherResponse response = service.getImages(getImageRequestParameters());

        final File destinationImageTifFile = new File(tempFolder.getRoot(), TIF_IMAGE_ID + ".png");
        final File destinationImageTiffFile = new File(tempFolder.getRoot(), TIFF_IMAGE_ID + ".png");
        final File destinationImageTiffFile2 = new File(tempFolder.getRoot(), TIFF_IMAGE_ID_2 + ".png");
        final File destinationImagePngFile = new File(tempFolder.getRoot(), PNG_IMAGE_ID + ".png");
        final File noExstensionPngFile = new File(tempFolder.getRoot(), NO_EXTENSION_TIFF + ".png");
        final File noExstensionPngFile2 = new File(tempFolder.getRoot(), NO_EXTENSION_PNG);

        assertTrue(destinationImageTifFile.exists());
        assertTrue(destinationImageTiffFile.exists());
        assertTrue(destinationImageTiffFile2.exists());
        assertTrue(destinationImagePngFile.exists());
        assertTrue(noExstensionPngFile.exists());
        assertTrue(noExstensionPngFile2.exists());

        for (final ImgMetadataInfo info : response.getImageMetadataList()) {
            if (TIF_IMAGE_ID.equals(info.getImgGuid())) {
                assertEquals(1733, info.getWidth().longValue());
                assertEquals(765, info.getHeight().longValue());
                assertEquals("image/png", info.getMimeType());
                return;
            }
        }
        assertTrue(false);
    }

    private ImageRequestParameters getImageRequestParameters() throws IOException {
        final ImageRequestParameters parameters = new ImageRequestParameters();

        parameters.setXppSourceImageDirectory(
            Arrays.asList(
                new PathMatchingResourcePatternResolver().getResource(UNPACKED_IMAGES_DIR1).getFile().getAbsolutePath(),
                new PathMatchingResourcePatternResolver().getResource(UNPACKED_IMAGES_DIR2)
                    .getFile()
                    .getAbsolutePath()));
        parameters.setDynamicImageDirectory(tempFolder.getRoot());

        parameters.setDocToImageManifestFile(
            new PathMatchingResourcePatternResolver().getResource(DOC_TO_IMAGE_FILE).getFile());

        return parameters;
    }
}
