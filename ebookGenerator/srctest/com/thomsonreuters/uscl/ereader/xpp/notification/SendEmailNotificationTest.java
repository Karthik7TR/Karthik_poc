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

import java.util.Collection;

import javax.mail.internet.InternetAddress;

import com.thomsonreuters.uscl.ereader.common.notification.entity.NotificationEmail;
import com.thomsonreuters.uscl.ereader.common.notification.service.EmailService;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.service.CoreService;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;
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
public final class SendEmailNotificationTest {
    @InjectMocks
    private SendEmailNotification step;
    @Mock
    private EmailService emailService;
    @Mock
    private CoreService coreService;
    @Mock
    private PublishingStatsService publishingStatsService;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ChunkContext chunkContext;
    @Mock
    private BookDefinition book;
    @Captor
    private ArgumentCaptor<String> captor;
    @Captor
    private ArgumentCaptor<Collection<InternetAddress>> captorRecipients;
    @Captor
    private ArgumentCaptor<NotificationEmail> captorNotificationEmail;

    @Test
    public void recipientsAreCorrect() throws Exception {
        // given
        givenAll();
        final InternetAddress[] expectedRecipients = new InternetAddress[] {new InternetAddress("address")};
        given(coreService.getEmailRecipientsByUsername("user")).willReturn(asList(expectedRecipients));
        // when
        step.executeStep();
        // then
        then(emailService).should().send(captorNotificationEmail.capture());
        final Collection<InternetAddress> recipients = captorNotificationEmail.getValue().getRecipients();
        assertThat(recipients, contains(expectedRecipients));
    }

    @Test
    public void subjectIsCorrect() throws Exception {
        // given
        givenAll();
        // when
        step.executeStep();
        // then
        then(emailService).should().send(captorNotificationEmail.capture());
        assertThat(captorNotificationEmail.getValue().getSubject(), is("eBook XPP Publishing Successful - titleId"));
    }

    @Test
    public void bodyIsCorrect() throws Exception {
        // given
        givenAll();
        // when
        step.executeStep();
        // then
        then(emailService).should().send(captorNotificationEmail.capture());

        final String tempBodyTemplate = captorNotificationEmail.getValue().getBody();
        assertThat(tempBodyTemplate, containsString("eBook Publishing Successful - titleId"));
        assertThat(tempBodyTemplate, containsString("<br>Proview Display Name: proviewDisplayName"));
        assertThat(tempBodyTemplate, containsString("<br>Title ID: titleId"));
        assertThat(tempBodyTemplate, containsString("<br>Environment: env"));
        assertThat(tempBodyTemplate, containsString("<br>Job Execution ID: 2"));
        assertThat(tempBodyTemplate, containsString("<br><br><table border = '1'><tr><td></td><td>Current version</td><td>Previous version</td>"));
        assertThat(tempBodyTemplate, containsString("<tr><td>Job Instance ID</td><td>1</td><td>3</td></tr>"));
        assertThat(tempBodyTemplate, containsString("<tr><td>Doc Count</td><td>100</td><td>100</td></tr>"));
        assertThat(tempBodyTemplate, containsString("<tr><td>Book Size</td><td>1.0 MiB</td><td>1.0 MiB</td></tr>"));
        assertThat(tempBodyTemplate, containsString("</table>"));
        assertThat(tempBodyTemplate, containsString("<br><table border = '1'><th colspan = '2'>Print Components</th>"));
        assertThat(tempBodyTemplate, containsString("<tr><td>Material Number</td><td>SAP Description</td></tr>"));
        assertThat(tempBodyTemplate, containsString("</table>"));
    }

    private void givenAll() {
        givenBook(chunkContext, book);
        given(book.getFullyQualifiedTitleId()).willReturn("titleId");
        given(book.getProviewDisplayName()).willReturn("proviewDisplayName");
        givenEnvironment(chunkContext, "env");
        givenUserName(chunkContext, "user");
        givenJobInstanceId(chunkContext, 1L);
        givenJobExecutionId(chunkContext, 2L);
        given(publishingStatsService.findPublishingStatsByJobId(1L)).willReturn(getCurrentVersion());
        given(publishingStatsService.getPreviousPublishingStatsForSameBook(1L)).willReturn(getPreviousVersion());
    }

    private PublishingStats getPreviousVersion() {
        final PublishingStats previousVersionPublishingStats = new PublishingStats();
        previousVersionPublishingStats.setAssembleDocCount(100);
        previousVersionPublishingStats.setBookSize(1048576L);
        previousVersionPublishingStats.setJobInstanceId(3L);
        return previousVersionPublishingStats;
    }

    private PublishingStats getCurrentVersion() {
        final PublishingStats currentVersionPublishingStats = new PublishingStats();
        currentVersionPublishingStats.setAssembleDocCount(100);
        currentVersionPublishingStats.setBookSize(1048576L);
        currentVersionPublishingStats.setJobInstanceId(1L);
        return currentVersionPublishingStats;
    }
}
