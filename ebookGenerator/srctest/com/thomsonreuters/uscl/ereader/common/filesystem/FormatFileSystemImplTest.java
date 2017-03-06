package com.thomsonreuters.uscl.ereader.common.filesystem;

import static com.thomsonreuters.uscl.ereader.common.filesystem.FileSystemMatcher.hasPath;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;

import java.io.File;

import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public final class FormatFileSystemImplTest
{
    @InjectMocks
    private FormatFileSystemImpl fileSystem;
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
    public void shouldReturnFormatDirectory()
    {
        //given
        //when
        final File directory = fileSystem.getFormatDirectory(step);
        //then
        assertThat(directory, hasPath("workDirectory/Format"));
    }

    @Test
    public void shouldReturnSplitBookDirectory()
    {
        //given
        //when
        final File directory = fileSystem.getSplitBookDirectory(step);
        //then
        assertThat(directory, hasPath("workDirectory/Format/splitEbook"));
    }

    @Test
    public void shouldReturnSplitBookInfoFile()
    {
        //given
        //when
        final File file = fileSystem.getSplitBookInfoFile(step);
        //then
        assertThat(file, hasPath("workDirectory/Format/splitEbook/splitNodeInfo.txt"));
    }
}
