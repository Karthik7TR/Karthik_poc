package com.thomsonreuters.uscl.ereader.gather.img.util;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doThrow;

import java.io.File;

import com.thomsonreuters.uscl.ereader.gather.util.images.ImageConverterException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public final class ImageTypeResolverImplTest
{
    @InjectMocks
    private ImageTypeResolverImpl sut;
    @Mock
    private TiffReader tiffReader;

    @Test
    public void shouldHaveTiffExtension()
    {
        //given
        final File imageTif = new File("/image.tif");
        final File imageTiff = new File("/image.tiff");
        //when
        final boolean hasTifExtension = sut.hasTiffExtension(imageTif);
        final boolean hasTiffExtension = sut.hasTiffExtension(imageTiff);
        //then
        assertThat(hasTifExtension, is(true));
        assertThat(hasTiffExtension, is(true));
    }

    @Test
    public void shouldNotHaveTiffExtension()
    {
        //given
        final File image = new File("/image");
        //when
        final boolean hasTiffExtension = sut.hasTiffExtension(image);
        //then
        assertThat(hasTiffExtension, is(false));
    }

    @Test
    public void shouldntBeTiffAsPng()
    {
        //given
        final File image = new File("/notTiff.png");
        doThrow(ImageConverterException.class).when(tiffReader).readTiff(image);
        //when
        final boolean isTiff = sut.isTiff(image);
        //then
        assertThat(isTiff, is(false));
    }

    @Test
    public void shouldBeTiff()
    {
        //given
        final File image = new File("/tiff");
        //when
        final boolean isTiff = sut.isTiff(image);
        //then
        assertThat(isTiff, is(true));
    }

    @Test
    public void shouldBeTiffasTiff()
    {
        //given
        final File image = new File("/tiff.tif");
        //when
        final boolean isTiff = sut.isTiff(image);
        //then
        assertThat(isTiff, is(true));
    }
}
