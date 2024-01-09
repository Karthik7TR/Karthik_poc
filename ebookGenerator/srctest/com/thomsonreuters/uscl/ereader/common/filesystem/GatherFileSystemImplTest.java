package com.thomsonreuters.uscl.ereader.common.filesystem;

import static com.thomsonreuters.uscl.ereader.common.filesystem.FileSystemMatcher.hasPath;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyLong;

import java.io.File;

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
public final class GatherFileSystemImplTest {
    private static final String WORK_DIRECTORY_NAME = "workDirectory";
    private static final String EXPECTED_GATHER_PATH = String.format("%s/Gather", WORK_DIRECTORY_NAME);
    private static final String EXPECTED_GATHER_TOC_PATH = String.format("%s/Gather/Toc/toc.xml", WORK_DIRECTORY_NAME);
    private static final String EXPECTED_GATHER_DOCS_PATH = String.format("%s/Gather/Docs", WORK_DIRECTORY_NAME);
    private static final String EXPECTED_GATHER_DOCS_METADATA_PATH = String.format("%s/Gather/Docs/Metadata", WORK_DIRECTORY_NAME);
    @InjectMocks
    private GatherFileSystemImpl fileSystem;
    @Mock
    private BookFileSystem bookFileSystem;
    @Mock
    private BookStep step;
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Before
    public void setUp() {
        given(bookFileSystem.getWorkDirectory(step)).willReturn(new File(temporaryFolder.getRoot(), WORK_DIRECTORY_NAME));
        given(bookFileSystem.getWorkDirectoryByJobId(anyLong())).willReturn(new File(temporaryFolder.getRoot(), WORK_DIRECTORY_NAME));
    }

    @Test
    public void shouldReturnGatherRootDirectory() {
        //given
        //when
        final File fileByStep = fileSystem.getGatherRootDirectory(step);
        final File fileByJobId = fileSystem.getGatherRootDirectory(1L);
        //then
        assertThat(fileByStep, hasPath(EXPECTED_GATHER_PATH));
        assertThat(fileByJobId, hasPath(EXPECTED_GATHER_PATH));
    }

    @Test
    public void shouldReturnGatherTocFile() {
        //given
        //when
        final File file = fileSystem.getGatherTocFile(step);
        //then
        assertThat(file, hasPath(EXPECTED_GATHER_TOC_PATH));
    }

    @Test
    public void shouldReturnGatherDocsDirectory() {
        //given
        //when
        final File file = fileSystem.getGatherDocsDirectory(step);
        //then
        assertThat(file, hasPath(EXPECTED_GATHER_DOCS_PATH));
    }

    @Test
    public void shouldReturnGatherDocsMetadataDirectory() {
        //given
        //when
        final File file = fileSystem.getGatherDocsMetadataDirectory(step);
        //then
        assertThat(file, hasPath(EXPECTED_GATHER_DOCS_METADATA_PATH));
    }
}
