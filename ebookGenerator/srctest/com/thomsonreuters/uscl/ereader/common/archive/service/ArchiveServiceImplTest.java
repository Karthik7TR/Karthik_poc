package com.thomsonreuters.uscl.ereader.common.archive.service;

import static com.thomsonreuters.uscl.ereader.core.book.BookMatchers.version;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Arrays;

import com.thomsonreuters.uscl.ereader.common.archive.step.BaseArchiveStep;
import com.thomsonreuters.uscl.ereader.common.filesystem.ArchiveFileSystem;
import com.thomsonreuters.uscl.ereader.common.filesystem.AssembleFileSystem;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetadataService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public final class ArchiveServiceImplTest {
    @InjectMocks
    private ArchiveServiceImpl service;
    @Mock
    private DocMetadataService docMetadataService;
    @Mock
    private BaseArchiveStep step;
    @Mock
    private BookDefinition book;
    @Mock
    private AssembleFileSystem assembleFileSystem;
    @Mock
    private ArchiveFileSystem archivefileSystem;
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private File archiveDir;
    private File assembledBookFile;
    private File assembledSplitTitleFile;

    @Before
    public void setUp() throws URISyntaxException {
        archiveDir = temporaryFolder.getRoot();
        assembledBookFile = new File(ArchiveServiceImplTest.class.getResource("assembledBookFile").toURI());
        assembledSplitTitleFile = new File(ArchiveServiceImplTest.class.getResource("assembledSplitTitleFile").toURI());
        given(archivefileSystem.getArchiveVersionDirectory(step)).willReturn(archiveDir);
        given(assembleFileSystem.getAssembledBookFile(step)).willReturn(assembledBookFile);
        given(assembleFileSystem.getAssembledSplitTitleFile(step, "splitTitleId")).willReturn(assembledSplitTitleFile);

        given(step.getJobInstanceId()).willReturn(1L);
        given(step.getBookDefinition()).willReturn(book);
        given(step.getBookVersion()).willReturn(version("v1.1"));
    }

    @Test
    public void shouldArchiveSingleVolumeBook() {
        //given
        given(book.isSplitBook()).willReturn(false);
        final File copiedFile = new File(archiveDir, "assembledBookFile");
        //when
        service.archiveBook(step);
        //then
        assertThat(copiedFile.exists(), is(true));
    }

    @Test
    public void shouldArchiveSplitBook() {
        //given
        given(book.isSplitBook()).willReturn(true);
        given(step.getSplitTitles()).willReturn(Arrays.asList("splitTitleId"));
        final File copiedFile = new File(archiveDir, "assembledSplitTitleFile");
        //when
        service.archiveBook(step);
        //then
        assertThat(copiedFile.exists(), is(true));
    }
}
