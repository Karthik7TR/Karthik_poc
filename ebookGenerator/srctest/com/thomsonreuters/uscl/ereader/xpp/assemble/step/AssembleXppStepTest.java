package com.thomsonreuters.uscl.ereader.xpp.assemble.step;

import static com.thomsonreuters.uscl.ereader.StepTestUtil.givenBook;
import static com.thomsonreuters.uscl.ereader.StepTestUtil.givenJobInstanceId;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import java.io.File;
import java.util.Collections;

import com.thomsonreuters.uscl.ereader.assemble.service.EBookAssemblyService;
import com.thomsonreuters.uscl.ereader.common.filesystem.AssembleFileSystem;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetadataService;
import com.thomsonreuters.uscl.ereader.request.domain.PrintComponent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.batch.core.scope.context.ChunkContext;

@RunWith(MockitoJUnitRunner.class)
public final class AssembleXppStepTest {
    @InjectMocks
    private AssembleXppStep step;
    @Mock
    private EBookAssemblyService assemblyService;
    @Mock
    private DocMetadataService docMetadataService;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ChunkContext chunkContext;
    @Mock
    private BookDefinition book;
    @Mock
    private AssembleFileSystem fileSystem;
    @Captor
    private ArgumentCaptor<File> captor;

    @Mock
    private File titleDirectory;
    @Mock
    private File assembledBookFile;

    @Before
    public void setUp() {
        givenBook(chunkContext, book);
        given(book.getTitleId()).willReturn("titleId");
        givenJobInstanceId(chunkContext, 1L);
    }

    @Test
    public void shouldAssembleSingleVolumeBook() throws Exception {
        //given
        given(book.isSplitBook()).willReturn(false);
        given(fileSystem.getAssembledBookFile(step)).willReturn(assembledBookFile);
        given(fileSystem.getTitleDirectory(step)).willReturn(titleDirectory);
        //when
        step.executeStep();
        //then
        then(assemblyService).should().assembleEBook(captor.capture(), captor.capture());
        final File assembleDir = captor.getAllValues().get(0);
        assertThat(assembleDir, is(titleDirectory));
        final File assembledFile = captor.getValue();
        assertThat(assembledFile, is(assembledBookFile));
    }

    @Test
    public void shouldAssembleSplitBook() throws Exception {
        //given
        given(book.isSplitBook()).willReturn(true);
        given(book.getFullyQualifiedTitleId()).willReturn("an/splitTitle");
        given(fileSystem.getAssembledSplitTitleFile(step, "an/splitTitle")).willReturn(assembledBookFile);
        given(fileSystem.getAssembledSplitTitleFile(step, "an/splitTitle_pt2")).willReturn(assembledBookFile);
        given(fileSystem.getSplitTitleDirectory(step, "an/splitTitle")).willReturn(titleDirectory);
        given(fileSystem.getSplitTitleDirectory(step, "an/splitTitle_pt2")).willReturn(titleDirectory);

        final PrintComponent printComponent = new PrintComponent();
        printComponent.setSplitter(true);
        printComponent.setComponentOrder(1);
        given(book.getPrintComponents()).willReturn(Collections.singleton(printComponent));

        //when
        step.executeStep();
        //then
        then(assemblyService).should(times(2)).assembleEBook(captor.capture(), captor.capture());
        File assembleDir = captor.getAllValues().get(0);
        assertThat(assembleDir, is(titleDirectory));
        File assembledFile = captor.getAllValues().get(1);
        assertThat(assembledFile, is(assembledBookFile));
        assembleDir = captor.getAllValues().get(2);
        assertThat(assembleDir, is(titleDirectory));
        assembledFile = captor.getAllValues().get(3);
        assertThat(assembledFile, is(assembledBookFile));
    }
}
