package com.thomsonreuters.uscl.ereader.xpp.initialize;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.any;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.core.CoreConstants;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.book.service.EBookAuditService;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;

@RunWith(MockitoJUnitRunner.class)
public final class InitializeTaskTest
{
    @InjectMocks
    private InitializeTask step;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ChunkContext chunkContext;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private StepContribution contribution;
    @Mock
    private BookDefinition book;

    @Mock
    private PublishingStatsService publishingStatsService;
    @Mock
    private EBookAuditService eBookAuditService;
    @Mock
    private BookDefinitionService bookDefnService;
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();
    @Mock
    private ExecutionContext jobExecutionContext;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp()
    {
        step.setEnvironmentName("env");
        step.setRootWorkDirectory(temporaryFolder.getRoot());
    }

    @Test
    public void shouldSetBookDefinition() throws Exception
    {
        // given
        givenAll();
        // when
        step.executeStep(contribution, chunkContext);
        // then
        then(jobExecutionContext).should().put(JobExecutionKey.EBOOK_DEFINITION, book);
    }

    @Test
    public void shouldCreateAndSetWorkDir() throws Exception
    {
        // given
        givenAll();
        final String dateStr = new SimpleDateFormat(CoreConstants.DIR_DATE_FORMAT).format(new Date());
        final File workDir = new File(temporaryFolder.getRoot(), "env/data/" + dateStr + "/titleId/1");
        // when
        step.executeStep(contribution, chunkContext);
        // then
        assertThat(workDir.exists(), is(true));
        then(jobExecutionContext).should().putString(JobExecutionKey.WORK_DIRECTORY, workDir.getAbsolutePath());
    }

    @Test
    public void shouldPublishStats() throws Exception
    {
        // given
        givenAll();
        // when
        step.executeStep(contribution, chunkContext);
        // then
        then(publishingStatsService).should().savePublishingStats(any(PublishingStats.class));
    }

    @Test
    public void shouldThrowExceptionIfFailed() throws Exception
    {
        // given
        givenAll();
        step.setBookDefnService(null);
        thrown.expect(NullPointerException.class);
        // when // then
        step.executeStep(contribution, chunkContext);
    }

    private void givenAll()
    {
        given(bookDefnService.findBookDefinitionByEbookDefId(1L)).willReturn(book);
        given(eBookAuditService.findEbookAuditByEbookDefId(1L)).willReturn(2L);

        given(chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext())
            .willReturn(jobExecutionContext);
        given(
            chunkContext.getStepContext()
                .getStepExecution()
                .getJobExecution()
                .getJobParameters()
                .getLong(JobParameterKey.BOOK_DEFINITION_ID)).willReturn(1L);

        given(book.getFullyQualifiedTitleId()).willReturn("titleId");
        given(book.getTitleId()).willReturn("titleId");
        given(book.getProviewDisplayName()).willReturn("proviewDisplayName");
        given(chunkContext.getStepContext().getStepExecution().getJobParameters().getString("environmentName"))
            .willReturn("env");
        given(chunkContext.getStepContext().getStepExecution().getJobExecution().getJobInstance().getId())
            .willReturn(1L);
        given(chunkContext.getStepContext().getStepExecution().getJobExecutionId()).willReturn(2L);
    }
}
