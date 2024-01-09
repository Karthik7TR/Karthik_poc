package com.thomsonreuters.uscl.ereader.xpp.common;

import static java.util.Arrays.asList;

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
import com.thomsonreuters.uscl.ereader.common.step.BookStepImpl;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.service.EmailUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public final class XppStepFailureNotificationServiceImplTest {
    @InjectMocks
    private XppStepFailureNotificationServiceImpl service;
    @Mock
    private EmailUtil emailUtil;
    @Mock
    private EmailService emailService;
    @Mock
    private BookStepImpl step;
    @Mock
    private BookDefinition book;
    @Captor
    private ArgumentCaptor<NotificationEmail> captor;

    @Test
    public void recipientsAreCorrect() throws Exception {
        // given
        final Exception e = new Exception();
        givenAll();

        final InternetAddress[] expectedRecipients = new InternetAddress[] {new InternetAddress("address")};
        given(emailUtil.getEmailRecipientsByUsername("user")).willReturn(asList(expectedRecipients));
        // when
        service.sendFailureNotification(step, e);
        // then
        then(emailService).should().send(captor.capture());
        final Collection<InternetAddress> recipients = captor.getValue().getRecipients();
        assertThat(recipients, contains(expectedRecipients));
    }

    @Test
    public void subjectIsCorrect() {
        // given
        final Exception e = new Exception();
        givenAll();
        // when
        service.sendFailureNotification(step, e);
        // then
        then(emailService).should().send(captor.capture());
        assertThat(captor.getValue().getSubject(), is("eBook Publishing Failure: env  id  name  1  2"));
    }

    @Test
    public void bodyIsCorrect() {
        // given
        final Exception e = new Exception("msg");
        givenAll();
        // when
        service.sendFailureNotification(step, e);
        // then
        then(emailService).should().send(captor.capture());
        final String body = captor.getValue().getBody();
        assertThat(body, containsString("eBook Publishing Failure: env  id  name  1  2"));
        assertThat(body, containsString("Error Message : msg"));
    }

    private void givenAll() {
        given(step.getUserName()).willReturn("user");
        given(step.getEnvironment()).willReturn("env");
        given(step.getBookDefinition()).willReturn(book);
        given(step.getJobInstanceId()).willReturn(1L);
        given(step.getJobExecutionId()).willReturn(2L);
        given(book.getFullyQualifiedTitleId()).willReturn("id");
        given(book.getProviewDisplayName()).willReturn("name");
    }
}
