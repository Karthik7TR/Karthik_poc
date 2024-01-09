package com.thomsonreuters.uscl.ereader.gather.img.util;

import static org.junit.Assert.assertNotNull;

import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URISyntaxException;

import com.thomsonreuters.uscl.ereader.gather.util.images.ImageConverterException;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public final class TiffReaderImplTest {
    private static final String IMAGEIO_EXT_TIFF_READER_CLASS =
        "it.geosolutions.imageioimpl.plugins.tiff.TIFFImageReader";
    private static final String TIFF_EXTENSION = ".tif";
    private static final String UNEXISTENT_FILE_NAME = "/fileNotFound";

    private TiffReaderImpl sut;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();
    @Rule
    public TestName name = new TestName();
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private File workDir;

    @Before
    public void setUp() {
        workDir = temporaryFolder.getRoot();
        sut = new TiffReaderImpl(IMAGEIO_EXT_TIFF_READER_CLASS);
    }

    @After
    public void tearDown() {
        FileUtils.deleteQuietly(workDir);
    }

    @Test
    public void convertUncompressed() throws Exception {
        //given
        final File file = getFile();
        //when
        final BufferedImage image = sut.readTiff(file);
        //then
        assertNotNull(image);
    }

    @Test
    public void convertNotImage() throws Exception {
        //given
        thrown.expect(ImageConverterException.class);
        thrown.expectMessage("Preferable TIFF reader not found: " + IMAGEIO_EXT_TIFF_READER_CLASS);
        final File file = getFile();
        //when
        sut.readTiff(file);
    }

    @Test
    public void fileNotFound() {
        //given
        thrown.expect(ImageConverterException.class);
        final File file = new File(UNEXISTENT_FILE_NAME);
        //when
        sut.readTiff(file);
    }

    private File getFile() throws URISyntaxException {
        return new File(TiffReaderImplTest.class.getResource(name.getMethodName() + TIFF_EXTENSION).toURI());
    }
}
