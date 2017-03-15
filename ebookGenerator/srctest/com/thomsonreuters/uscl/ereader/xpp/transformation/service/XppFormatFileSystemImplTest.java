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
import org.junit.Test;
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

    @Before
    public void setUp()
    {
        given(bookFileSystem.getWorkDirectory(step)).willReturn(new File("workDirectory"));
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
        assertThat(file, hasPath("workDirectory/Format/01_Original/xppFileName.original"));
    }

    @Test
    public void shouldReturnOriginalFiles() throws IOException
    {
        //given
        final File directory = fileSystem.getOriginalDirectory(step);
        directory.mkdirs();
        final File original = new File(directory, "temp.original");
        final File footnotes = new File(directory, "temp.footnotes");
        original.createNewFile();
        footnotes.createNewFile();
        //when
        final Collection<File> file = fileSystem.getOriginalFiles(step);
        //then
        assertThat(file, contains(original));
    }

    @Test
    public void shouldReturnFootnotesFiles() throws IOException
    {
        //given
        final File directory = fileSystem.getOriginalDirectory(step);
        directory.mkdirs();
        final File original = new File(directory, "temp.original");
        final File footnotes = new File(directory, "temp.footnotes");
        original.createNewFile();
        footnotes.createNewFile();
        //when
        final Collection<File> file = fileSystem.getFootnotesFiles(step);
        //then
        assertThat(file, contains(footnotes));
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
        final File file = fileSystem.getPagebreakesUpFile(step, "fileName.original");
        //then
        assertThat(file, hasPath("workDirectory/Format/02_PagebreakesUp/fileName_original.pagebreakesUp"));
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
        final File file = fileSystem.getOriginalPartsFile(step, "fileName", PartType.MAIN, 1);
        //then
        assertThat(file, hasPath("workDirectory/Format/03_OriginalParts/fileName_original_1.part"));
    }

    @Test
    public void shouldReturnToHtmlDirectory()
    {
        //given
        //when
        final File directory = fileSystem.getToHtmlDirectory(step);
        //then
        assertThat(directory, hasPath("workDirectory/Format/04_ToHtml"));
    }

    @Test
    public void shouldReturnToHtmlFile()
    {
        //given
        //when
        final File file = fileSystem.getToHtmlFile(step, "fileName.part");
        //then
        assertThat(file, hasPath("workDirectory/Format/04_ToHtml/fileName.html"));
    }
}
