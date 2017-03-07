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
public class XppUnpackFileSystemImplTest
{
    @InjectMocks
    private XppUnpackFileSystemImpl fileSystem;

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
    public void shouldReturnXppUnpackRootDirectory()
    {
        //given
        //when
        final File file = fileSystem.getXppUnpackDirectory(step);
        //then
        assertThat(file, hasPath("workDirectory/Gather/XppUnpack"));
    }

    @Test
    public void shouldReturnXppAssetsDirectory()
    {
        //given
        //when
        final File file = new File(fileSystem.getXppAssetsDirectory(step));
        //then
        assertThat(file, hasPath("workDirectory/Gather/XppUnpack/assets"));
    }
}
