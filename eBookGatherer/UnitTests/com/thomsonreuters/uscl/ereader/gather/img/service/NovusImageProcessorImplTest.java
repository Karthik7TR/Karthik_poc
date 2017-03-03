package com.thomsonreuters.uscl.ereader.gather.img.service;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;

import java.io.FileNotFoundException;
import java.util.List;

import com.thomsonreuters.uscl.ereader.gather.img.model.NovusImage;
import com.thomsonreuters.uscl.ereader.gather.img.util.ImageConverter;
import com.thomsonreuters.uscl.ereader.gather.util.ImgMetadataInfo;
import com.thomsonreuters.uscl.ereader.gather.util.images.ImageConverterException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;

@RunWith(MockitoJUnitRunner.class)
public final class NovusImageProcessorImplTest
{
    @InjectMocks
    private NovusImageProcessorImpl processor;
    @Mock
    private NovusImageFinder finder;
    @Mock
    private ImageConverter imageConverter;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Before
    public void setUp() throws FileNotFoundException
    {
        processor.setDynamicImageDirectory(temporaryFolder.getRoot());
        processor.setMissingImageGuidsFileBasename("temp.txt");
        processor.init();
    }

    @Test
    public void shouldProcessCorrectly()
    {
        // given
        final ImgMetadataInfo metadata = new ImgMetadataInfo();
        given(finder.getImage("imageId")).willReturn(new NovusImage(MediaType.IMAGE_PNG, metadata, new byte[0]));
        // when
        processor.process("imageId", "docId");
        final boolean processed = processor.isProcessed("imageId", "docId");
        final List<ImgMetadataInfo> imagesMetadata = processor.getImagesMetadata();
        final int missingImageCount = processor.getMissingImageCount();
        // then
        assertThat(processed, is(true));
        assertThat(imagesMetadata, contains(metadata));
        assertThat(missingImageCount, is(0));
        then(imageConverter).should(never()).convertByteImg((byte[]) any(), anyString(), anyString());
    }

    @Test
    public void shouldProcessSameImageInDifferentDocs()
    {
        // given
        final ImgMetadataInfo metadata = new ImgMetadataInfo();
        given(finder.getImage("imageId")).willReturn(new NovusImage(MediaType.IMAGE_PNG, metadata, new byte[0]));
        // when
        processor.process("imageId", "docId1");
        processor.process("imageId", "docId2");
        processor.process("imageId2", "docId2");
        final boolean processed1 = processor.isProcessed("imageId", "docId1");
        final boolean processed2 = processor.isProcessed("imageId", "docId2");
        final boolean processed3 = processor.isProcessed("imageId2", "docId2");
        // then
        assertThat(processed1, is(true));
        assertThat(processed2, is(true));
        assertThat(processed3, is(true));
    }

    @Test
    public void isProcessedShouldReturnFalseIfDocNotProcessed()
    {
        // given
        final ImgMetadataInfo metadata = new ImgMetadataInfo();
        given(finder.getImage("imageId")).willReturn(new NovusImage(MediaType.IMAGE_PNG, metadata, new byte[0]));
        // when
        processor.process("imageId", "docId1");
        final boolean processed = processor.isProcessed("imageId", "docId");
        // then
        assertThat(processed, is(false));
    }

    @Test
    public void shouldCountFailsWhenCannotGetImage()
    {
        // given
        given(finder.getImage("imageId")).willReturn(null);
        // when
        processor.process("imageId", "docId");
        final boolean processed = processor.isProcessed("imageId", "docId");
        final List<ImgMetadataInfo> imagesMetadata = processor.getImagesMetadata();
        final int missingImageCount = processor.getMissingImageCount();
        // then
        assertThat(processed, is(true));
        assertThat(imagesMetadata, empty());
        assertThat(missingImageCount, is(1));
        then(imageConverter).should(never()).convertByteImg((byte[]) any(), anyString(), anyString());
    }

    @Test
    public void shouldConvertTiffs()
    {
        // given
        final ImgMetadataInfo metadata = new ImgMetadataInfo();
        given(finder.getImage("imageId"))
            .willReturn(new NovusImage(MediaType.valueOf("image/tif"), metadata, new byte[0]));
        // when
        processor.process("imageId", "docId");
        // then
        then(imageConverter).should().convertByteImg((byte[]) any(), anyString(), anyString());
    }

    @Test
    public void shouldCountFailsWhenCannotConvertImage()
    {
        // given
        final ImgMetadataInfo metadata = new ImgMetadataInfo();
        given(finder.getImage("imageId"))
            .willReturn(new NovusImage(MediaType.valueOf("image/tif"), metadata, new byte[0]));
        doThrow(new ImageConverterException("Cannot convert")).when(imageConverter)
            .convertByteImg((byte[]) any(), anyString(), anyString());
        // when
        processor.process("imageId", "docId");
        final List<ImgMetadataInfo> imagesMetadata = processor.getImagesMetadata();
        final int missingImageCount = processor.getMissingImageCount();
        // then
        assertThat(imagesMetadata, empty());
        assertThat(missingImageCount, is(1));
    }

    @Test
    public void shouldProcessUnknownFormat()
    {
        // given
        final ImgMetadataInfo metadata = new ImgMetadataInfo();
        given(finder.getImage("imageId"))
            .willReturn(new NovusImage(MediaType.valueOf("image/ico"), metadata, new byte[0]));
        // when
        processor.process("imageId", "docId");
        final boolean processed = processor.isProcessed("imageId", "docId");
        final List<ImgMetadataInfo> imagesMetadata = processor.getImagesMetadata();
        final int missingImageCount = processor.getMissingImageCount();
        // then
        assertThat(processed, is(true));
        assertThat(imagesMetadata, contains(metadata));
        assertThat(missingImageCount, is(0));
    }
}
