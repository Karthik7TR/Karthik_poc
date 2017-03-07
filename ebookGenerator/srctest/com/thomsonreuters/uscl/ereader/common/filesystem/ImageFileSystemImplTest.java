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
public class ImageFileSystemImplTest
{
    @InjectMocks
    private ImageFileSystemImpl fileSystem;

    @Mock
    private GatherFileSystemImpl gatherFileSystem;

    @Mock
    private BookStep step;

    @Before
    public void setUp()
    {
        given(gatherFileSystem.getGatherRootDirectory(step)).willReturn(new File("workDirectory/Gather"));
    }

    @Test
    public void shouldReturnImageRootDirectory()
    {
        //given
        //when
        final File file = fileSystem.getImageRootDirectory(step);
        //then
        assertThat(file, hasPath("workDirectory/Gather/Images"));
    }

    @Test
    public void shouldReturnImageDynamicDirectory()
    {
        //given
        //when
        final File file = fileSystem.getImageDynamicDirectory(step);
        //then
        assertThat(file, hasPath("workDirectory/Gather/Images/Dynamic"));
    }
}
