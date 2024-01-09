package com.thomsonreuters.uscl.ereader.xpp.archive.step;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.common.archive.service.ArchiveAuditService;
import com.thomsonreuters.uscl.ereader.common.archive.service.ArchiveService;
import com.thomsonreuters.uscl.ereader.common.service.environment.EnvironmentUtil;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.batch.core.scope.context.ChunkContext;

@RunWith(MockitoJUnitRunner.class)
public final class ArchiveXppStepTest {
    @InjectMocks
    private ArchiveXppStep step;
    @Mock
    private ArchiveAuditService archiveAuditService;
    @Mock
    private ArchiveService archiveService;
    @Mock
    private EnvironmentUtil environmentUtil;
    @Mock
    private BookDefinitionService bookDefinitionService;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ChunkContext chunkContext;
    @Mock
    private BookDefinition bookDefinition;

    @Before
    public void setUp() {
        given(chunkContext.getStepContext()
                        .getStepExecution()
                        .getJobExecution()
                        .getExecutionContext()
                        .get(JobParameterKey.EBOOK_DEFINITON)).willReturn(bookDefinition);
    }

    @Test
    public void shouldSaveAudit() throws Exception {
        //given
        //when
        step.executeStep();
        //then
        then(archiveAuditService).should().saveAudit(step);
    }

    @Test
    public void shouldStoreArchiveForProd() throws Exception {
        //given
        given(environmentUtil.isProd()).willReturn(true);
        //when
        step.executeStep();
        //then
        then(archiveService).should().archiveBook(step);
    }
}
