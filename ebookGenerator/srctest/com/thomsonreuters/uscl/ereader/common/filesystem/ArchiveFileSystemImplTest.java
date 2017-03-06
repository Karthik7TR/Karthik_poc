package com.thomsonreuters.uscl.ereader.common.filesystem;

import static com.thomsonreuters.uscl.ereader.common.filesystem.FileSystemMatcher.hasPath;
import static com.thomsonreuters.uscl.ereader.core.book.BookMatchers.version;
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
public final class ArchiveFileSystemImplTest
{
    @InjectMocks
    private ArchiveFileSystemImpl fileSystem;
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
    public void shouldReturnArchiveDirectory()
    {
        //given
        //when
        final File directory = fileSystem.getArchiveDirectory(step);
        //then
        assertThat(directory, hasPath("workDirectory/archive"));
    }

    @Test
    public void shouldReturnArchiveVersionDirectoryForMajorVerion()
    {
        //given
        given(step.getBookVersion()).willReturn(version("v2.0"));
        //when
        final File directory = fileSystem.getArchiveVersionDirectory(step);
        //then
        assertThat(directory, hasPath("workDirectory/archive/major"));
    }

    @Test
    public void shouldReturnArchiveVersionDirectoryForMinorVerion()
    {
        //given
        given(step.getBookVersion()).willReturn(version("v2.1"));
        //when
        final File directory = fileSystem.getArchiveVersionDirectory(step);
        //then
        assertThat(directory, hasPath("workDirectory/archive/minor"));
    }
}
