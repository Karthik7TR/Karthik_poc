package com.thomsonreuters.uscl.ereader.xpp.transformation.service;

import static com.thomsonreuters.uscl.ereader.common.filesystem.FileSystemMatcher.hasPath;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;

import java.io.File;

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
        assertThat(directory, hasPath("workDirectory/Format/1_Original"));
    }

    @Test
    public void shouldReturnOriginalFile()
    {
        //given
        //when
        final File file = fileSystem.getOriginalFile(step, "xppFileName.xml");
        //then
        assertThat(file, hasPath("workDirectory/Format/1_Original/xppFileName.original"));
    }

    @Test
    public void shouldReturnPageBreakesUpDirectory()
    {
        //given
        //when
        final File directory = fileSystem.getPagebreakesUpDirectory(step);
        //then
        assertThat(directory, hasPath("workDirectory/Format/2_PagebreakesUp"));
    }

    @Test
    public void shouldReturnPageBreakesUpFile()
    {
        //given
        //when
        final File file = fileSystem.getPagebreakesUpFile(step, "fileName.original");
        //then
        assertThat(file, hasPath("workDirectory/Format/2_PagebreakesUp/fileName.pagebreakesUp"));
    }

    @Test
    public void shouldReturnOriginalPartsDirectory()
    {
        //given
        //when
        final File directory = fileSystem.getOriginalPartsDirectory(step);
        //then
        assertThat(directory, hasPath("workDirectory/Format/3_OriginalParts"));
    }

    @Test
    public void shouldReturnOriginalPartsFile()
    {
        //given
        //when
        final File file = fileSystem.getOriginalPartsFile(step, "fileName.original", 1);
        //then
        assertThat(file, hasPath("workDirectory/Format/3_OriginalParts/fileName_1.part"));
    }

    @Test
    public void shouldReturnToHtmlDirectory()
    {
        //given
        //when
        final File directory = fileSystem.getToHtmlDirectory(step);
        //then
        assertThat(directory, hasPath("workDirectory/Format/4_ToHtml"));
    }

    @Test
    public void shouldReturnToHtmlFile()
    {
        //given
        //when
        final File file = fileSystem.getToHtmlFile(step, "fileName.part");
        //then
        assertThat(file, hasPath("workDirectory/Format/4_ToHtml/fileName.html"));
    }
}
