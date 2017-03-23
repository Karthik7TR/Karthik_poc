package com.thomsonreuters.uscl.ereader.common.filesystem;

import static com.thomsonreuters.uscl.ereader.common.filesystem.FileSystemMatcher.hasPath;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;

import java.io.File;

import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public final class XppUnpackFileSystemImplTest
{
    private static final String gatherDir = "/apps/eBookBuilder/xppTemp"; //"workDirectory/Gather"

    @InjectMocks
    private XppUnpackFileSystemImpl fileSystem;
    @Mock
    private GatherFileSystemImpl gatherFileSystem;
    @Mock
    private BookStep step;
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Before
    public void setUp() throws IllegalAccessException
    {
        given(gatherFileSystem.getGatherRootDirectory(step))
            .willReturn(new File(temporaryFolder.getRoot(), "workDirectory/Gather"));

        FieldUtils.writeField(fileSystem, "xppTempDirectory", "/apps/eBookBuilder/xppTemp/", true);
    }

    @Test
    public void shouldReturnXppUnpackRootDirectory()
    {
        //given
        //when
        final File file = fileSystem.getXppUnpackDirectory(step);
        //then
        assertThat(file, hasPath(gatherDir + "/XppUnpack"));
    }

    @Test
    public void shouldReturnXppAssetsDirectory()
    {
        //given
        //when
        final File file = new File(fileSystem.getXppAssetsDirectory(step));
        //then
        assertThat(file, hasPath(gatherDir + "/XppUnpack/assets"));
    }
}
