package com.thomsonreuters.uscl.ereader.common.filesystem;

import static com.thomsonreuters.uscl.ereader.common.filesystem.FileSystemMatcher.hasPath;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;

import java.io.File;

import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public final class AssembleFileSystemImplTest {
    @InjectMocks
    private AssembleFileSystemImpl fileSystem;
    @Mock
    private BookFileSystem bookFileSystem;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private BookStep step;
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Before
    public void setUp() {
        given(bookFileSystem.getWorkDirectory(step)).willReturn(new File(temporaryFolder.getRoot(), "workDirectory"));
    }

    @Test
    public void shouldReturnAssembleDirectory() {
        //given
        //when
        final File directory = fileSystem.getAssembleDirectory(step);
        //then
        assertThat(directory, hasPath("workDirectory/Assemble"));
    }

    @Test
    public void shouldReturnTitleDirectory() {
        //given
        given(step.getBookDefinition().getTitleId()).willReturn("titleId");
        //when
        final File directory = fileSystem.getTitleDirectory(step);
        //then
        assertThat(directory, hasPath("workDirectory/Assemble/titleId"));
    }

    @Test
    public void shouldReturnTitleXml() {
        //given
        given(step.getBookDefinition().getTitleId()).willReturn("titleId");
        //when
        final File file = fileSystem.getTitleXml(step);
        //then
        assertThat(file, hasPath("workDirectory/Assemble/titleId/title.xml"));
    }

    @Test
    public void shouldReturnSplitTitleDirectory() {
        //given
        //when
        final File directory = fileSystem.getSplitTitleDirectory(step, "an/splitTitleId");
        //then
        assertThat(directory, hasPath("workDirectory/Assemble/splitTitleId"));
    }

    @Test
    public void shouldReturnAssetsDirectory() {
        //given
        given(step.getBookDefinition().getTitleId()).willReturn("titleId");
        //when
        final File directory = fileSystem.getAssetsDirectory(step);
        //then
        assertThat(directory, hasPath("workDirectory/Assemble/titleId/assets"));
    }

    @Test
    public void shouldReturnDocumentsDirectory() {
        //given
        given(step.getBookDefinition().getTitleId()).willReturn("titleId");
        //when
        final File directory = fileSystem.getDocumentsDirectory(step);
        //then
        assertThat(directory, hasPath("workDirectory/Assemble/titleId/documents"));
    }

    @Test
    public void shouldReturnAssembledBookFile() {
        //given
        given(step.getBookDefinition().getTitleId()).willReturn("titleId");
        given(step.getBookVersion().getVersionForFilePattern()).willReturn("_1_1");
        //when
        final File file = fileSystem.getAssembledBookFile(step);
        //then
        assertThat(file, hasPath("workDirectory/titleId_1_1.gz"));
    }

    @Test
    public void testGetAssembledSplitTitleFile() {
        //given
        given(step.getBookDefinition().getTitleId()).willReturn("titleId");
        given(step.getBookVersion().getVersionForFilePattern()).willReturn("_1_1");
        //when
        final File file = fileSystem.getAssembledSplitTitleFile(step, "an/splitTitleId");
        //then
        assertThat(file, hasPath("workDirectory/splitTitleId_1_1.gz"));
    }

    @Test
    public void testGetArtworkFile() {
        //given
        given(step.getBookDefinition().getTitleId()).willReturn("titleId");
        //when
        final File file = fileSystem.getArtworkFile(step);
        //then
        assertThat(file, hasPath("Assemble/titleId/artwork/coverArt.PNG"));
    }
}
