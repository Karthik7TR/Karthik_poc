/*
 * Copyright 2017: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.xpp.notification;

import static java.util.Arrays.asList;

import static com.thomsonreuters.uscl.ereader.StepTestUtil.givenBook;
import static com.thomsonreuters.uscl.ereader.StepTestUtil.givenEnvironment;
import static com.thomsonreuters.uscl.ereader.StepTestUtil.givenJobExecutionId;
import static com.thomsonreuters.uscl.ereader.StepTestUtil.givenJobInstanceId;
import static com.thomsonreuters.uscl.ereader.StepTestUtil.givenUserName;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.any;

import java.util.Collection;

import javax.mail.internet.InternetAddress;

import com.thomsonreuters.uscl.ereader.common.notification.service.EmailService;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.service.CoreService;
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
public final class SendEmailNotificationTest
{
    @InjectMocks
    private SendEmailNotification step;
    @Mock
    private EmailService emailService;
    @Mock
    private CoreService coreService;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ChunkContext chunkContext;
    @Mock
    private BookDefinition book;
    @Captor
    private ArgumentCaptor<String> captor;
    @Captor
    private ArgumentCaptor<Collection<InternetAddress>> captorRecipients;

    @Test
    public void recipientsAreCorrect() throws Exception
    {
        // given
        givenAll();
        final InternetAddress[] expectedRecipients = new InternetAddress[] {new InternetAddress("address")};
        given(coreService.getEmailRecipientsByUsername("user")).willReturn(asList(expectedRecipients));
        // when
        step.executeStep();
        // then
        then(emailService).should().send(captorRecipients.capture(), any(String.class), any(String.class));
        final Collection<InternetAddress> recipients = captorRecipients.getValue();
        assertThat(recipients, contains(expectedRecipients));
    }

    @Test
    public void subjectIsCorrect() throws Exception
    {
        // given
        givenAll();
        // when
        step.executeStep();
        // then
        then(emailService).should().send(any(Collection.class), captor.capture(), any(String.class));
        assertThat(captor.getValue(), is("eBook Shell XPP job - titleId"));
    }

    @Test
    public void bodyIsCorrect() throws Exception
    {
        // given
        givenAll();
        // when
        step.executeStep();
        // then
        then(emailService).should().send(any(Collection.class), any(String.class), captor.capture());
        assertThat(captor.getValue(), containsString("eBook Publishing Successful - titleId"));
        assertThat(captor.getValue(), containsString("Proview Display Name: proviewDisplayName"));
        assertThat(captor.getValue(), containsString("Title ID: titleId"));
        assertThat(captor.getValue(), containsString("Environment: env"));
        assertThat(captor.getValue(), containsString("Job Instance ID: 1"));
        assertThat(captor.getValue(), containsString("Job Execution ID: 2"));
    }

    private void givenAll()
    {
        givenBook(chunkContext, book);
        given(book.getFullyQualifiedTitleId()).willReturn("titleId");
        given(book.getProviewDisplayName()).willReturn("proviewDisplayName");
        givenEnvironment(chunkContext, "env");
        givenUserName(chunkContext, "user");
        givenJobInstanceId(chunkContext, 1L);
        givenJobExecutionId(chunkContext, 2L);
    }
}
