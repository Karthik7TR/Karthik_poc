package com.thomsonreuters.uscl.ereader.xpp.assemble.step;

import static java.util.Arrays.asList;

import static com.thomsonreuters.uscl.ereader.StepTestUtil.givenBook;
import static com.thomsonreuters.uscl.ereader.StepTestUtil.givenJobInstanceId;
import static com.thomsonreuters.uscl.ereader.StepTestUtil.givenWorkDir;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import java.io.File;

import com.thomsonreuters.uscl.ereader.assemble.service.EBookAssemblyService;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetadataService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.batch.core.scope.context.ChunkContext;

@RunWith(MockitoJUnitRunner.class)
public final class AssembleXppStepTest
{
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
    @Captor
    private ArgumentCaptor<File> captor;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private File workDir;

    @Before
    public void setUp()
    {
        workDir = temporaryFolder.getRoot();
        givenBook(chunkContext, book);
        givenWorkDir(chunkContext, workDir);
        given(book.getTitleId()).willReturn("titleId");
        givenJobInstanceId(chunkContext, 1L);
    }

    @Test
    public void shouldAssembleSingleVolumeBook() throws Exception
    {
        //given
        given(book.isSplitBook()).willReturn(false);
        //when
        step.executeStep();
        //then
        then(assemblyService).should().assembleEBook(captor.capture(), captor.capture());
        final File assembleDir = captor.getAllValues().get(0);
        assertThat(assembleDir.getAbsolutePath(), containsString("Assemble" + File.separator + "titleId"));
        final File assembledFile = captor.getValue();
        assertThat(assembledFile.getAbsolutePath(), containsString("titleId.gz"));
    }

    @Test
    public void shouldAssembleSplitBook() throws Exception
    {
        //given
        given(book.isSplitBook()).willReturn(true);
        given(docMetadataService.findDistinctSplitTitlesByJobId(1L)).willReturn(asList("/splitTitle", "/splitTitle_pt2"));
        //when
        step.executeStep();
        //then
        then(assemblyService).should(times(2)).assembleEBook(captor.capture(), captor.capture());
        File assembleDir = captor.getAllValues().get(0);
        assertThat(assembleDir.getAbsolutePath(), containsString("Assemble" + File.separator + "splitTitle"));
        File assembledFile = captor.getAllValues().get(1);
        assertThat(assembledFile.getAbsolutePath(), containsString("splitTitle.gz"));
        assembleDir = captor.getAllValues().get(2);
        assertThat(assembleDir.getAbsolutePath(), containsString("Assemble" + File.separator + "splitTitle_pt2"));
        assembledFile = captor.getAllValues().get(3);
        assertThat(assembledFile.getAbsolutePath(), containsString("splitTitle_pt2.gz"));
    }
}
