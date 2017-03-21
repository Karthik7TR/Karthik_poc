package com.thomsonreuters.uscl.ereader.xpp.transformation.service;

import static com.thomsonreuters.uscl.ereader.common.filesystem.FileSystemMatcher.hasPath;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem;
import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public final class XppFormatFileSystemImplTest
{
    @InjectMocks
    private XppFormatFileSystemImpl fileSystem;
    @Mock
    private BookFileSystem bookFileSystem;
    @Mock
    private BookStep step;
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Before
    public void setUp()
    {
        given(bookFileSystem.getWorkDirectory(step)).willReturn(new File(temporaryFolder.getRoot(), "workDirectory"));
    }

    @Test
    public void shouldReturnOriginalDirectory()
    {
        //given
        //when
        final File directory = fileSystem.getOriginalDirectory(step);
        //then
        assertThat(directory, hasPath("workDirectory/Format/01_Original"));
    }

    @Test
    public void shouldReturnOriginalFile()
    {
        //given
        //when
        final File file = fileSystem.getOriginalFile(step, "xppFileName.xml");
        //then
        assertThat(file, hasPath("workDirectory/Format/01_Original/xppFileName.main"));
    }

    @Test
    public void shouldReturnOriginalFiles() throws IOException
    {
        //given
        final File directory = fileSystem.getOriginalDirectory(step);
        directory.mkdirs();
        final File original = new File(directory, "temp.main");
        final File footnotes = new File(directory, "temp.footnotes");
        original.createNewFile();
        footnotes.createNewFile();
        //when
        final Collection<File> files = fileSystem.getOriginalFiles(step);
        //then
        assertThat(files, contains(original));
    }

    @Test
    public void shouldReturnFootnotesFiles() throws IOException
    {
        //given
        final File directory = fileSystem.getOriginalDirectory(step);
        directory.mkdirs();
        final File original = new File(directory, "temp.main");
        final File footnotes = new File(directory, "temp.footnotes");
        original.createNewFile();
        footnotes.createNewFile();
        //when
        final Collection<File> files = fileSystem.getFootnotesFiles(step);
        //then
        assertThat(files, contains(footnotes));
    }

    @Test
    public void shouldReturnFootnotesFile()
    {
        //given
        //when
        final File file = fileSystem.getFootnotesFile(step, "xppFileName.xml");
        //then
        assertThat(file, hasPath("workDirectory/Format/01_Original/xppFileName.footnotes"));
    }

    @Test
    public void shouldReturnPageBreakesUpDirectory()
    {
        //given
        //when
        final File directory = fileSystem.getPagebreakesUpDirectory(step);
        //then
        assertThat(directory, hasPath("workDirectory/Format/02_PagebreakesUp"));
    }

    @Test
    public void shouldReturnPageBreakesUpFile()
    {
        //given
        //when
        final File file = fileSystem.getPagebreakesUpFile(step, "fileName.main");
        //then
        assertThat(file, hasPath("workDirectory/Format/02_PagebreakesUp/fileName.main"));
    }

    @Test
    public void shouldReturnOriginalPartsDirectory()
    {
        //given
        //when
        final File directory = fileSystem.getOriginalPartsDirectory(step);
        //then
        assertThat(directory, hasPath("workDirectory/Format/03_OriginalParts"));
    }

    @Test
    public void shouldReturnOriginalPartsFile()
    {
        //given
        //when
        final File file = fileSystem.getOriginalPartsFile(step, "fileName", 1, PartType.MAIN);
        //then
        assertThat(file, hasPath("workDirectory/Format/03_OriginalParts/fileName_1_main.part"));
    }

    @Test
    public void shouldReturnOriginalPagesDirectory()
    {
        //given
        //when
        final File directory = fileSystem.getOriginalPagesDirectory(step);
        //then
        assertThat(directory, hasPath("workDirectory/Format/04_OriginalPages"));
    }

    @Test
    public void shouldReturnOriginalPageFile()
    {
        //given
        //when
        final File file = fileSystem.getOriginalPageFile(step, "fileName.original", 1);
        //then
        assertThat(file, hasPath("workDirectory/Format/04_OriginalPages/fileName_1.page"));
    }

    @Test
    public void shouldReturnHtmlPagesDirectory()
    {
        //given
        //when
        final File directory = fileSystem.getHtmlPagesDirectory(step);
        //then
        assertThat(directory, hasPath("workDirectory/Format/05_HtmlPages"));
    }

    @Test
    public void shouldReturnHtmlPageFile()
    {
        //given
        //when
        final File file = fileSystem.getHtmlPageFile(step, "fileName.part");
        //then
        assertThat(file, hasPath("workDirectory/Format/05_HtmlPages/fileName.html"));
    }

    @Test
    public void shouldReturnAnchorsMapFile()
    {
        //given
        //when
        final File file = fileSystem.getAnchorToDocumentIdMapFile(step);
        //then
        assertThat(file, hasPath("workDirectory/Format/anchorToDocumentIdMapFile.txt"));
    }

    @Test
    public void shouldReturnDocToImagesMapFile()
    {
        //given
        //when
        final File file = fileSystem.getDocToImageMapFile(step);
        //then
        assertThat(file, hasPath("workDirectory/Format/doc-to-image-manifest.txt"));
    }
}
