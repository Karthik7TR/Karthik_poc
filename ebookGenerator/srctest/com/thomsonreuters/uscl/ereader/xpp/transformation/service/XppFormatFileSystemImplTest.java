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
        assertThat(directory, hasPath("workDirectory/Format/Original"));
    }

    @Test
    public void shouldReturnOriginalFile()
    {
        //given
        //when
        final File file = fileSystem.getOriginalFile(step, "xppFileName");
        //then
        assertThat(file, hasPath("workDirectory/Format/Original/xppFileName.original"));
    }
}
