package com.thomsonreuters.uscl.ereader.gather.img.util;

import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;

import javax.imageio.ImageIO;

import com.thomsonreuters.uscl.ereader.gather.util.images.ImageConverterException;
import org.apache.commons.io.FileUtils;
import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public final class TiffImageConverterTest {
    private static final String INCORRECT_PATH = "\\***\\\\\\\\\\\\";
    private static final String PNG_EXTENSION = ".png";
    private static final String TIFF_EXTENSION = ".tif";
    private static final String PNG = "PNG";

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();
    @Rule
    public TestName name = new TestName();
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private File workDir;
    private File substituteImagesDir;

    private TiffImageConverter converter;
    @Mock
    private TiffReader tiffReader;

    @Before
    public void setUp() {
        workDir = temporaryFolder.getRoot();
        substituteImagesDir = new File(workDir, "substituteImages");
        substituteImagesDir.mkdirs();
        converter = new TiffImageConverter(tiffReader, substituteImagesDir);
    }

    @After
    public void tearDown() {
        FileUtils.deleteQuietly(workDir);
    }

    @Test
    public void convertUncompressed() throws Exception {
        doTest();
    }

    @Test
    public void convertGroup4() throws Exception {
        doTest();
    }

    @Test
    public void convertBadGroup4() throws Exception {
        doTest();
    }

    @Test
    public void convertNotTiff() throws Exception {
        doTest();
    }

    @Test
    public void convertWithSubstitute() throws Exception {
        //given
        final File substituteImageFile =
            new File(TiffImageConverterTest.class.getResource("convertWithSubstitute.png").toURI());
        FileUtils.copyFileToDirectory(substituteImageFile, substituteImagesDir);
        //when //then
        doTest();
    }

    @Test
    public void writingError() throws Exception {
        thrown.expect(ImageConverterException.class);
        thrown.expectCause(CoreMatchers.<Throwable>instanceOf(IOException.class));
        doTest(true);
    }

    private void doTest() throws ImageConverterException, IOException, URISyntaxException {
        doTest(false);
    }

    private void doTest(final boolean withWriteError) throws ImageConverterException, IOException, URISyntaxException {
        //given
        final File tiff = getFile(name.getMethodName());
        final byte[] imgBytes = Files.readAllBytes(tiff.toPath());
        final String outputImagePath =
            withWriteError ? INCORRECT_PATH : new File(workDir, name.getMethodName() + PNG_EXTENSION).getAbsolutePath();
        final BufferedImage bimage = ImageIO.read(getFile("convertUncompressed"));
        given(tiffReader.readTiff(imgBytes)).willReturn(bimage);
        //when
        converter.convertByteImg(imgBytes, outputImagePath, PNG);
        //then
        final File outputFile = new File(outputImagePath);
        assertTrue(outputFile.exists());
        assertThat(outputFile.length(), not(0L));
    }

    private File getFile(final String fileName) throws URISyntaxException {
        return new File(TiffImageConverterTest.class.getResource(fileName + TIFF_EXTENSION).toURI());
    }
}
