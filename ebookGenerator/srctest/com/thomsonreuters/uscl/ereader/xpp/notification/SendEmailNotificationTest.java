package com.thomsonreuters.uscl.ereader.xpp.notification;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.batch.core.scope.context.ChunkContext;

@RunWith(MockitoJUnitRunner.class)
public final class SendEmailNotificationTest
{
    @InjectMocks
    @Spy
    private SendEmailNotification step;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ChunkContext chunkContext;
    @Mock
    private BookDefinition book;

    @Test
    public void subjectIsCorrect()
    {
        //given
        given(chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().get("bookDefn"))
            .willReturn(book);
        given(book.getFullyQualifiedTitleId()).willReturn("titleId");
        //when
        final String subject = step.getSubject(chunkContext);
        //then
        assertThat(subject, is("eBook Shell XPP job - titleId"));
    }

    @Test
    public void bodyIsCorrect()
    {
        //given
        given(chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().get("bookDefn"))
            .willReturn(book);
        given(book.getFullyQualifiedTitleId()).willReturn("titleId");
        given(book.getProviewDisplayName()).willReturn("proviewDisplayName");
        given(chunkContext.getStepContext().getStepExecution().getJobParameters().getString("environmentName"))
            .willReturn("env");
        given(chunkContext.getStepContext().getStepExecution().getJobExecution().getJobInstance().getId())
            .willReturn(1L);
        given(chunkContext.getStepContext().getStepExecution().getJobExecutionId()).willReturn(2L);
        //when
        final String body = step.getBody(chunkContext);
        //then
        assertThat(body, containsString("eBook Publishing Successful - titleId"));
        assertThat(body, containsString("Proview Display Name: proviewDisplayName"));
        assertThat(body, containsString("Title ID: titleId"));
        assertThat(body, containsString("Environment: env"));
        assertThat(body, containsString("Job Instance ID: 1"));
        assertThat(body, containsString("Job Execution ID: 2"));
    }
}
