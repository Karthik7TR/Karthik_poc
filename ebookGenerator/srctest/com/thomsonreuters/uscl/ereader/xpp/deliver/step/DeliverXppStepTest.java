package com.thomsonreuters.uscl.ereader.xpp.deliver.step;

import static java.util.Arrays.asList;

import static com.thomsonreuters.uscl.ereader.StepTestUtil.givenBook;
import static com.thomsonreuters.uscl.ereader.StepTestUtil.givenBookVersion;
import static com.thomsonreuters.uscl.ereader.StepTestUtil.givenJobInstanceId;
import static com.thomsonreuters.uscl.ereader.core.book.BookMatchers.version;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;

import java.io.File;

import com.thomsonreuters.uscl.ereader.common.deliver.service.ProviewHandlerWithRetry;
import com.thomsonreuters.uscl.ereader.common.filesystem.AssembleFileSystem;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.model.Version;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewHandler;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetadataService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.batch.core.scope.context.ChunkContext;

@RunWith(MockitoJUnitRunner.class)
public final class DeliverXppStepTest {
    @InjectMocks
    private DeliverXppStep step;
    @Mock
    private DocMetadataService docMetadataService;
    @Mock
    private ProviewHandler proviewHandler;
    @Mock
    private ProviewHandlerWithRetry proviewHandlerWithRetry;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ChunkContext chunkContext;
    @Mock
    private BookDefinition book;
    @Mock
    private File assembledBookFile;
    @Mock
    private AssembleFileSystem fileSystem;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() {
        givenBookVersion(chunkContext, "1.1");
        givenJobInstanceId(chunkContext, 1L);

        givenBook(chunkContext, book);
        given(book.getTitleId()).willReturn("titleId");
        given(book.getFullyQualifiedTitleId()).willReturn("titleId");
    }

    @Test
    public void shouldPublishSingleVolumeBook() throws Exception {
        //given
        given(book.isSplitBook()).willReturn(false);
        given(fileSystem.getAssembledBookFile(step)).willReturn(assembledBookFile);
        //when
        step.executeStep();
        //then
        then(proviewHandler).should().publishTitle("titleId", version("v1.1"), assembledBookFile);
    }

    @Test
    public void shouldPublishSplitBook() throws Exception {
        //given
        given(book.isSplitBook()).willReturn(true);
        given(book.getFullyQualifiedTitleId()).willReturn("an/splitTitle");
        given(docMetadataService.findDistinctSplitTitlesByJobId(1L))
            .willReturn(asList("an/splitTitle", "an/splitTitle_pt2"));
        given(fileSystem.getAssembledSplitTitleFile(step, "an/splitTitle")).willReturn(assembledBookFile);
        given(fileSystem.getAssembledSplitTitleFile(step, "an/splitTitle_pt2")).willReturn(assembledBookFile);
        //when
        step.executeStep();
        //then
        then(proviewHandler).should().publishTitle("an/splitTitle", version("v1.1"), assembledBookFile);
        then(proviewHandler).should().publishTitle("an/splitTitle_pt2", version("v1.1"), assembledBookFile);
    }

    @Test
    public void shouldCallCleanupServiceAnThrowExceptionIfProviewFailed() throws Exception {
        //given
        thrown.expect(ProviewException.class);

        given(book.isSplitBook()).willReturn(true);
        given(book.getFullyQualifiedTitleId()).willReturn("an/splitTitle");
        given(docMetadataService.findDistinctSplitTitlesByJobId(1L))
            .willReturn(asList("an/splitTitle", "an/splitTitle_pt2"));

        doThrow(ProviewException.class).when(proviewHandler)
            .publishTitle(eq("an/splitTitle_pt2"), any(Version.class), any(File.class));
        //when
        step.executeStep();
        //then
        then(proviewHandlerWithRetry).should().removeTitle("an/splitTitle", version("v1.1"));
    }
}
