package com.thomsonreuters.uscl.ereader.assemble.service;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.core.service.CoverArtUtil;
import com.thomsonreuters.uscl.ereader.assemble.step.MoveResourcesUtil;
import com.thomsonreuters.uscl.ereader.common.exception.EBookException;
import com.thomsonreuters.uscl.ereader.common.step.BookStepImpl;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.batch.item.ExecutionContext;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class MoveResourcesUtilTest {
    private MoveResourcesUtil moveResourcesUtil;
    private File tempRootDir;
    private File docToSplitBookFile;
    private static final String FILE_NAME = "doc-To-SplitBook.txt";
    private ExecutionContext jobExecutionContext;

    private File makeFile(final File directory, final String name, final String content) {
        final File file = new File(directory, name);
        try (FileOutputStream out = new FileOutputStream(file)) {
            out.write(content.getBytes());
            out.close();
            return file;
        } catch (final Exception e) {
            return null;
        }
    }

    @Before
    public void setUp() throws Exception {
        final CoverArtUtil coverArtUtil = mock(CoverArtUtil.class);
        when(coverArtUtil.getCoverArt(any())).thenReturn(new File("/apps/eBookBuilder/generator/images/cover/coverArt.PNG"));

        moveResourcesUtil = new MoveResourcesUtil();
        moveResourcesUtil.setCoverArtUtil(coverArtUtil);
        tempRootDir = new File(System.getProperty("java.io.tmpdir"));
        final URL url = this.getClass().getResource(FILE_NAME);
        docToSplitBookFile = new File(url.toURI());
        jobExecutionContext = new ExecutionContext();
    }

    @After
    public void tearDown() {
        FileUtils.deleteQuietly(tempRootDir);
    }

    @Test
    public void testcopyFilesToDir() {
        boolean thrown = false;
        final List<File> tempFileList = new ArrayList<>();
        tempFileList.add(docToSplitBookFile);

        try {
            moveResourcesUtil.copyFilesToDestination(tempFileList, tempRootDir);
        } catch (final EBookException e) {
            thrown = true;
        }
        Assert.assertTrue(tempRootDir.exists());
        assertTrue(!thrown);
    }

    @Test
    public void testSourceToDestination() {
        boolean thrown = false;

        try {
            moveResourcesUtil.copySourceToDestination(docToSplitBookFile.getParentFile(), tempRootDir);
        } catch (final EBookException e) {
            thrown = true;
        }
        Assert.assertTrue(tempRootDir.exists());
        assertTrue(!thrown);
    }

    @Test
    public void testFilterFiles() {
        boolean thrown = false;
        final BookDefinition bookDefinition = new BookDefinition();
        bookDefinition.setFrontMatterTheme("AAJ");
        bookDefinition.setKeyciteToplineFlag(true);
        List<File> fileList = moveResourcesUtil.filterFiles(docToSplitBookFile.getParentFile(), bookDefinition);
        Assert.assertEquals(fileList.size(), 0);

        try {
            fileList = moveResourcesUtil.filterFiles(new File("DoesNotExist"), bookDefinition);
        } catch (final EBookException e) {
            //expected
            e.printStackTrace();
            thrown = true;
        }
        Assert.assertTrue(thrown);
    }

    @Test
    public void testFilterFilesMockDir() throws Exception {
        final File testDir = new File(tempRootDir.getAbsolutePath() + "\\EvenMoreTemp");
        testDir.mkdir();
        final File temp1 = makeFile(testDir, "AAJ.png", "totally a png"); // passes starts with "AAJ"
        final File temp2 = makeFile(testDir, "keycite.xml", "Totally xml"); // passes starts with "keycite"
        final File temp3 = makeFile(testDir, "AAj.csv", "actually,a,csv"); // fails both

        final BookDefinition bookDefinition = new BookDefinition();
        bookDefinition.setFrontMatterTheme("AAJ");
        bookDefinition.setKeyciteToplineFlag(false);

        List<File> fileList = moveResourcesUtil.filterFiles(testDir, bookDefinition);
        Assert.assertEquals(fileList.size(), 2);

        bookDefinition.setFrontMatterTheme("AAJ Press");
        fileList = moveResourcesUtil.filterFiles(testDir, bookDefinition);
        Assert.assertEquals(fileList.size(), 1);

        bookDefinition.setKeyciteToplineFlag(true);
        fileList = moveResourcesUtil.filterFiles(testDir, bookDefinition);
        Assert.assertEquals(fileList.size(), 0);

        bookDefinition.setFrontMatterTheme("AAJ");
        fileList = moveResourcesUtil.filterFiles(testDir, bookDefinition);
        Assert.assertEquals(fileList.size(), 1);
        FileUtils.deleteDirectory(testDir);
    }

    @Ignore
    @Test
    public void testmoveFrontMatterImages() {
        boolean thrown = false;
        final BookDefinition bookDefinition = new BookDefinition();
        bookDefinition.setFrontMatterTheme("AAJ");
        bookDefinition.setKeyciteToplineFlag(true);
        jobExecutionContext.put(JobExecutionKey.EBOOK_DEFINITION, bookDefinition);
        final File canI = new File("/apps/eBookBuilder/coreStatic/images");
        File[] check = canI.listFiles();
        canI.mkdir();
        check = canI.listFiles();
        BookStepImpl step = mock(BookStepImpl.class);
        when(step.getBookDefinition()).thenReturn(bookDefinition);
        try {
            moveResourcesUtil.moveFrontMatterImages(step, tempRootDir, false);
        }

        catch (final NullPointerException e) {
            thrown = true;
        }
        Assert.assertTrue(tempRootDir.exists());
        assertTrue(!thrown);
    }

    @Ignore
    @Test
    public void testmoveCoverArt() {
        boolean thrown = false;
        final BookDefinition bookDefinition = new BookDefinition();
        jobExecutionContext.put(JobExecutionKey.EBOOK_DEFINITION, bookDefinition);
        try {
            moveResourcesUtil.moveCoverArt(jobExecutionContext, tempRootDir);
        } catch (final EBookException e) {
            thrown = true;
        }
        assertTrue(!thrown);
    }
}
