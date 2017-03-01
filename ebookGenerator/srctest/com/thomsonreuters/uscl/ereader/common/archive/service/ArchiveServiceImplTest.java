package com.thomsonreuters.uscl.ereader.common.archive.service;

import static java.util.Arrays.asList;

import static com.thomsonreuters.uscl.ereader.core.book.BookMatchers.version;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;

import java.io.File;
import java.net.URISyntaxException;

import com.thomsonreuters.uscl.ereader.common.archive.step.BaseArchiveStep;
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
public final class ArchiveServiceImplTest
{
    @InjectMocks
    private ArchiveServiceImpl service;
    @Mock
    private DocMetadataService docMetadataService;
    @Mock
    private BaseArchiveStep step;
    @Mock
    private BookDefinition book;
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private File archiveDir;
    private File assembledBookFile;
    private File assembledSplitTitleFile;

    @Before
    public void setUp() throws URISyntaxException
    {
        archiveDir = temporaryFolder.getRoot();
        assembledBookFile = new File(ArchiveServiceImplTest.class.getResource("assembledBookFile").toURI());
        assembledSplitTitleFile = new File(ArchiveServiceImplTest.class.getResource("assembledSplitTitleFile").toURI());
        given(step.getArchiveDirectory()).willReturn(archiveDir);
        given(step.getAssembledBookFile()).willReturn(assembledBookFile);
        given(step.getAssembledSplitTitleFile("splitTitleId")).willReturn(assembledSplitTitleFile);

        given(step.getJobInstanceId()).willReturn(1L);
        given(step.getBookDefinition()).willReturn(book);
        given(step.getBookVersion()).willReturn(version("v1.1"));
    }

    @Test
    public void shouldArchiveSingleVolumeBook()
    {
        //given
        given(book.isSplitBook()).willReturn(false);
        final File copiedFile = new File(archiveDir, "assembledBookFile");
        //when
        service.archiveBook(step);
        //then
        assertThat(copiedFile.exists(), is(true));
    }

    @Test
    public void shouldArchiveSplitBook()
    {
        //given
        given(book.isSplitBook()).willReturn(true);
        given(docMetadataService.findDistinctSplitTitlesByJobId(1L)).willReturn(asList("splitTitleId"));
        final File copiedFile = new File(archiveDir, "assembledSplitTitleFile");
        //when
        service.archiveBook(step);
        //then
        assertThat(copiedFile.exists(), is(true));
    }
}