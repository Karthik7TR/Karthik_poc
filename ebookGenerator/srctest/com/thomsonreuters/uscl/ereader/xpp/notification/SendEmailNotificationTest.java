/*
 * Copyright 2017: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.xpp.notification;

import static java.util.Arrays.asList;

import static com.thomsonreuters.uscl.ereader.StepTestUtil.givenBook;
import static com.thomsonreuters.uscl.ereader.StepTestUtil.givenEnvironment;
import static com.thomsonreuters.uscl.ereader.StepTestUtil.givenHostName;
import static com.thomsonreuters.uscl.ereader.StepTestUtil.givenJobExecutionId;
import static com.thomsonreuters.uscl.ereader.StepTestUtil.givenJobInstanceId;
import static com.thomsonreuters.uscl.ereader.StepTestUtil.givenUserName;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.mail.internet.InternetAddress;

import com.thomsonreuters.uscl.ereader.common.notification.entity.NotificationEmail;
import com.thomsonreuters.uscl.ereader.common.notification.service.EmailService;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.service.CoreService;
import com.thomsonreuters.uscl.ereader.request.domain.PrintComponent;
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
    private static final String CHAL_1_DOWNLOAD_LINK = "<a href=\"http://ebookGenerator.host.com:9002/ebookGenerator/pdfs/1/41894001/CHAL+vol1_bookTitleId_41894001.zip\">download</a>";
    private static final String CHAL_2_DOWNLOAD_LINK = "<a href=\"http://ebookGenerator.host.com:9002/ebookGenerator/pdfs/1/41894002/CHAL+vol2_bookTitleId_41894002.zip\">download</a>";

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
        assertThat(tempBodyTemplate, containsString("<br><table border = '1'><th colspan = '3'>Print Components</th>"));
        assertThat(tempBodyTemplate, containsString("<tr><td>Material Number</td><td>SAP Description</td><td></td></tr>"));
        assertThat(tempBodyTemplate, containsString("</table>"));
    }

    @Test
    public void bodyIsCorrectWithNullPreviousStats() throws Exception {
        // given
        givenAll();
        given(publishingStatsService.getPreviousPublishingStatsForSameBook(1L)).willReturn(null);
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
        assertThat(tempBodyTemplate, containsString("<tr><td>Job Instance ID</td><td>1</td><td>—</td></tr>"));
        assertThat(tempBodyTemplate, containsString("<tr><td>Doc Count</td><td>100</td><td>—</td></tr>"));
        assertThat(tempBodyTemplate, containsString("<tr><td>Book Size</td><td>1.0 MiB</td><td>—</td></tr>"));
        assertThat(tempBodyTemplate, containsString("</table>"));
        assertThat(tempBodyTemplate, containsString("<br><table border = '1'><th colspan = '3'>Print Components</th>"));
        assertThat(tempBodyTemplate, containsString("<tr><td>Material Number</td><td>SAP Description</td><td></td></tr>"));
        assertThat(tempBodyTemplate, containsString("</table>"));
    }

    @Test
    public void printComponentsTableShouldContainLiknks() throws Exception {
        // given
        givenAll();
        given(book.getPrintComponents()).willReturn(getPrintComponents());
        // when
        step.executeStep();
        // then
        then(emailService).should().send(captorNotificationEmail.capture());

        final String tempBodyTemplate = captorNotificationEmail.getValue().getBody();

        assertThat(tempBodyTemplate, containsString(CHAL_1_DOWNLOAD_LINK));
        assertThat(tempBodyTemplate, containsString(CHAL_2_DOWNLOAD_LINK));
    }

    @Test
    public void printComponentsTableShouldContainLiknksBasedOnBundleDescriptionWithSpecialCharacters() throws Exception {
        // given
        givenAll();
        given(book.getPrintComponents()).willReturn(new HashSet<>(Arrays.asList(
            getPrintComponent("http://server:8080/path", "41894001"),
            getPrintComponent("\\\\ftp\\folder", "41894002"),
            getPrintComponent("vol.1 //", "41894003"),
            getPrintComponent("q#q", "41894004"),
            getPrintComponent("q1\"+\"3\"+\"4", "41894005"),
            getPrintComponent("?var=<script>alert('Hello World');</script>", "41894006"),
            getPrintComponent("<script></script>", "41894007"),
            getPrintComponent("1.&lt;script&gt;&lt;/script&gt; 2.var=&lt;script&gt;alert('xss');&lt;/script&gt; 3.ff &amp;lt;script&amp;gt;&amp;lt;script&amp;gt;", "41894008")
        )));
        // when
        step.executeStep();
        // then
        then(emailService).should().send(captorNotificationEmail.capture());

        final String tempBodyTemplate = captorNotificationEmail.getValue().getBody();

        assertThat(tempBodyTemplate, containsString("<a href=\"http://ebookGenerator.host.com:9002/ebookGenerator/pdfs/1/41894001/httpserver8080path_bookTitleId_41894001.zip\">download</a>"));
        assertThat(tempBodyTemplate, containsString("<a href=\"http://ebookGenerator.host.com:9002/ebookGenerator/pdfs/1/41894002/ftpfolder_bookTitleId_41894002.zip\">download</a>"));
        assertThat(tempBodyTemplate, containsString("<a href=\"http://ebookGenerator.host.com:9002/ebookGenerator/pdfs/1/41894003/vol1+_bookTitleId_41894003.zip\">download</a>"));
        assertThat(tempBodyTemplate, containsString("<a href=\"http://ebookGenerator.host.com:9002/ebookGenerator/pdfs/1/41894004/qq_bookTitleId_41894004.zip\">download</a>"));
        assertThat(tempBodyTemplate, containsString("<a href=\"http://ebookGenerator.host.com:9002/ebookGenerator/pdfs/1/41894005/q134_bookTitleId_41894005.zip\">download</a>"));
        assertThat(tempBodyTemplate, containsString("<a href=\"http://ebookGenerator.host.com:9002/ebookGenerator/pdfs/1/41894006/varscriptalertHello+Worldscript_bookTitleId_41894006.zip\">download</a>"));
        assertThat(tempBodyTemplate, containsString("<a href=\"http://ebookGenerator.host.com:9002/ebookGenerator/pdfs/1/41894007/scriptscript_bookTitleId_41894007.zip\">download</a>"));
        assertThat(tempBodyTemplate, containsString("<a href=\"http://ebookGenerator.host.com:9002/ebookGenerator/pdfs/1/41894008/1scriptscript+2varscriptalertxssscript+3ff+ltscriptgtltscriptgt_bookTitleId_41894008.zip\">download</a>"));
    }

    @Test
    public void printComponentsTableSplitRow() throws Exception {
        // given
        givenAll();
        given(book.getPrintComponents()).willReturn(new HashSet<>(Arrays.asList(
            getPrintComponent("CHAL vol1", "41894001"),
            getPrintComponent("SPLITTER", "-------------"),
            getPrintComponent("CHAL vol2", "41894002")
        )));
        // when
        step.executeStep();
        // then
        then(emailService).should().send(captorNotificationEmail.capture());

        final String tempBodyTemplate = captorNotificationEmail.getValue().getBody();

        assertThat(tempBodyTemplate, containsString(CHAL_1_DOWNLOAD_LINK));
        assertThat(tempBodyTemplate, containsString(SendEmailNotification.PRINT_COMPONENTS_SPLITTER));
        assertThat(tempBodyTemplate, containsString(CHAL_2_DOWNLOAD_LINK));
    }

    private void givenAll() {
        givenBook(chunkContext, book);
        given(book.getFullyQualifiedTitleId()).willReturn("titleId");
        given(book.getProviewDisplayName()).willReturn("proviewDisplayName");
        given(book.getTitleId()).willReturn("bookTitleId");
        givenHostName(chunkContext, "ebookGenerator.host.com");
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

    private Set<PrintComponent> getPrintComponents() {
        return new HashSet<>(Arrays.asList(
            getPrintComponent("CHAL vol1", "41894001"),
            getPrintComponent("CHAL vol2", "41894002")
        ));
    }

    private PrintComponent getPrintComponent(final String name, final String materialNumber) {
        final PrintComponent printComponent = new PrintComponent();
        printComponent.setComponentName(name);
        printComponent.setMaterialNumber(materialNumber);
        printComponent.setBookDefinition(book);
        return printComponent;
    }
}
