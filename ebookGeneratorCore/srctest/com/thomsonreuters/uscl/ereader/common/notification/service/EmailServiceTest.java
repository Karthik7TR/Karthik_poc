package com.thomsonreuters.uscl.ereader.common.notification.service;

import com.thomsonreuters.uscl.ereader.TestUtils;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.springframework.test.util.ReflectionTestUtils;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;

import java.util.Collections;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

@PrepareForTest({EmailService.class, Transport.class})
@RunWith(PowerMockRunner.class)
public class EmailServiceTest {
    private static final String EMAIL = "email@thomsonreuters.com";
    private static final String SUBJECT = "Subject";
    private static final String EMAIL_BODY = "Email body";
    private static final String FILE_NAME = "file_name.txt";
    private static final String HOST = "host";
    private static final Integer TIMEOUT = 50000;
    private static final String NO_SUBJECT_ERROR_MESSAGE = "No subject provided";
    private static final String NO_RECIPIENTS_ERROR_MESSAGE = "No recipients provided";

    private EmailServiceImpl emailService;

    @Mock
    private Logger log;

    private InternetAddress internetAddress;
    private List<String> fileNames;

    @SneakyThrows
    @Before
    public void setUp() {
        emailService = new EmailServiceImpl();
        setProperties();
        mockStatic(Transport.class);
        TestUtils.setLogger(EmailServiceImpl.class, log);
        internetAddress = new InternetAddress(EMAIL);
        fileNames = Collections.singletonList(FILE_NAME);
    }

    @SneakyThrows
    @Test
    public void testSend() {
        Transport.send(any(Message.class));
        emailService.send(Collections.singleton(internetAddress), SUBJECT, EMAIL_BODY);
        verifyStatic();
    }

    @Test
    public void testSendNoSubject() {
        emailService.send(Collections.singleton(internetAddress), null, EMAIL_BODY);
        verify(log).error(eq(NO_SUBJECT_ERROR_MESSAGE), any(MessagingException.class));
    }

    @Test
    public void testSendEmptySubject() {
        emailService.send(Collections.singleton(internetAddress), "", EMAIL_BODY);
        verify(log).error(eq(NO_SUBJECT_ERROR_MESSAGE), any(MessagingException.class));
    }

    @Test
    @SneakyThrows
    public void testSendBlankRecipients() {
        emailService.send(" ", SUBJECT, EMAIL_BODY);
        verify(log).error(eq(NO_RECIPIENTS_ERROR_MESSAGE), any(MessagingException.class));
    }

    @SneakyThrows
    @Test
    public void testSendWithAttachment() {
        Transport.send(any(Message.class));
        emailService.sendWithAttachment(Collections.singleton(internetAddress), SUBJECT, EMAIL_BODY,
            fileNames);
        verifyStatic();
    }

    @Test
    public void testSendWithAttachmentNoSubject() {
        emailService.sendWithAttachment(Collections.singleton(internetAddress), null, EMAIL_BODY, fileNames);
        verify(log).error(eq(NO_SUBJECT_ERROR_MESSAGE), any(MessagingException.class));
    }

    @Test
    public void testSendWithAttachmentEmptySubject() {
        emailService.sendWithAttachment(Collections.singleton(internetAddress), "", EMAIL_BODY, fileNames);
        verify(log).error(eq(NO_SUBJECT_ERROR_MESSAGE), any(MessagingException.class));
    }

    private void setProperties() {
        ReflectionTestUtils.setField(emailService, "host", HOST);
        ReflectionTestUtils.setField(emailService, "timeout", TIMEOUT);
    }
}
