package com.thomsonreuters.uscl.ereader.gather.img.service;

import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.thomsonreuters.uscl.ereader.gather.exception.GatherException;
import com.thomsonreuters.uscl.ereader.gather.img.model.ImageRequestParameters;
import com.thomsonreuters.uscl.ereader.gather.img.util.DocToImageManifestUtil;
import com.thomsonreuters.uscl.ereader.gather.img.util.ImageConverter;
import com.thomsonreuters.uscl.ereader.gather.img.util.ImageTypeResolver;
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
public final class XppImageServiceTest {
    private static final String UNPACKED_IMAGES_DIR1 =
        "com/thomsonreuters/uscl/ereader/gather/img/service/images/bundle1";
    private static final String UNPACKED_IMAGES_DIR2 =
        "com/thomsonreuters/uscl/ereader/gather/img/service/images/bundle2";
    private static final String DOC_ID = "docId";
    private static final String TIF_IMAGE_ID = "I2943f88028b911e69ed7fcedf0a72426";
    private static final String TIFF_IMAGE_ID = "I3749e7f028b911e69ed7fcedf0a72426";
    private static final String PNG_IMAGE_ID = "I3831d6f128b911e69ed7fcedf0a72426";

    @InjectMocks
    private XppImageService service;
    @Mock
    private DocToImageManifestUtil docToImageManifestUtil;
    @Mock
    private ImageConverter imageConverter;
    @Mock
    private ImageTypeResolver imageTypeResolver;

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Before
    public void init() {
        when(docToImageManifestUtil.getDocsWithImages((File) any())).thenReturn(getDocsWithImages());
        when(imageConverter.convertByteImg((byte[]) any(), (String) any(), (String) any()))
            .thenReturn(mock(BufferedImage.class));
        given(imageTypeResolver.hasTiffExtension((File) any())).willReturn(true, true, false, true, true, false);
        given(imageTypeResolver.isTiff((File) any())).willReturn(true, true, false, true, true, false);
    }

    @Test
    public void shouldCopyImagesAndReturnMetadata() throws GatherException, IOException {
        service.getImages(getImageRequestParameters());

        final ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        verify(imageConverter, times(4)).convertByteImg((byte[]) any(), argument.capture(), (String) any());

        assertThat(
            argument.getAllValues(),
            hasItem(new File(tempFolder.getRoot(), TIF_IMAGE_ID + ".PNG").getAbsolutePath()));
        assertThat(
            argument.getAllValues(),
            hasItem(new File(tempFolder.getRoot(), TIFF_IMAGE_ID + ".PNG").getAbsolutePath()));
    }

    private Map<String, Set<String>> getDocsWithImages() {
        final Set<String> set = new HashSet<>();
        set.add(TIF_IMAGE_ID);
        set.add(TIFF_IMAGE_ID);
        set.add(PNG_IMAGE_ID);
        return Collections.singletonMap(DOC_ID, set);
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

        return parameters;
    }
}
