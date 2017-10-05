package com.thomsonreuters.uscl.ereader.request.service;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.thomsonreuters.uscl.ereader.common.service.compress.GZIPService;
import com.thomsonreuters.uscl.ereader.request.XPPConstants;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public final class GZIPServiceTest {
    private GZIPService service;
    private File tmpRootDir;
    private File destDir;
    private File tarball;

    @Before
    public void setUp() {
        service = new GZIPService();
        tmpRootDir = new File(System.getProperty("java.io.tmpdir") + "/" + this.getClass().getName());
        tmpRootDir.mkdir();

        destDir = new File(tmpRootDir, "target");
        tarball = new File("srctest/com/thomsonreuters/uscl/ereader/request/service/test.tar.gz");
    }

    @After
    public void tearDown() throws IOException {
        FileUtils.deleteDirectory(tmpRootDir);
    }

    @Test
    public void testHappyPath() {
        try {
            service.decompress(tarball, destDir);
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail("exception thrown: " + e.getMessage());
        }
        Assert.assertTrue(destDir.isDirectory());
        Assert.assertEquals(1, destDir.list().length);
        final File unzipDir = destDir.listFiles()[0];
        final List<String> files = Arrays.asList(unzipDir.list());
        // verify directories
        Assert.assertTrue(files.contains("assets"));
        Assert.assertTrue(files.contains("pdf"));
        Assert.assertTrue(files.contains("xpp"));
        // verify files
        Assert.assertTrue(files.contains("bundle.xml"));
        Assert.assertTrue(new File(unzipDir, "assets/test.txt").exists());
        Assert.assertTrue(new File(unzipDir, "pdf/test.txt").exists());
        Assert.assertTrue(new File(unzipDir, "xpp/test.txt").exists());
    }

    @Test
    public void testMissingTarball() {
        tarball = new File("definitely_not_real.bad");
        final String expectedError = String.format(XPPConstants.ERROR_TARBALL_NOT_FOUND, tarball.getAbsolutePath());
        String msg = null;
        try {
            service.decompress(tarball, destDir);
        } catch (final Exception e) {
            msg = e.getMessage();
        }
        Assert.assertEquals(expectedError, msg);
    }
}
