package com.thomsonreuters.uscl.ereader.orchestrate.engine.service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.common.notification.service.EmailService;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.service.EmailUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.item.ExecutionContext;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public final class GeneratorNotificationServiceImplTest {
    private static final String TEST_ENV = "testcontent";
    private static final String PROVIEW_DISPLAY_NAME = "Test Book";
    private static final String TITLE_ID = "uscl/an/test";
    private static final String USERNAME = "username";
    private static final String BODY_MESSAGE = "Error message: error";
    private static final String EMAIL = "nick@domain.com";
    private static final Long JOB_INSTANCE_ID = 1L;
    private static final Long JOB_EXECUTION_ID = 2L;
    private static final String EXPECTED_SUBJECT = "eBook Publishing Failure:  " + TEST_ENV + "  " + TITLE_ID + "  "
            + PROVIEW_DISPLAY_NAME + "  " + JOB_INSTANCE_ID + "  " + JOB_EXECUTION_ID;
    private static final String EXPECTED_BODY = "eBook Publishing Failure - " + TITLE_ID + "\t\nProview Display Name: " +
            PROVIEW_DISPLAY_NAME + "\t\nEnvironment: " + TEST_ENV + "\t\nJob Instance ID: " + JOB_INSTANCE_ID +
            "\t\nJob Execution ID: " + JOB_EXECUTION_ID + "\t\n" + BODY_MESSAGE;

    @InjectMocks
    private GeneratorNotificationServiceImpl generatorNotificationService;
    @Mock
    private EmailService emailService;
    @Mock
    private EmailUtil emailUtil;

    @Test
    public void shouldCreateCorrectlyFormattedEmail() throws AddressException {
        JobParameters jobParameters = initJobParameters();
        ExecutionContext executionContext = new ExecutionContext();
        executionContext.put(JobParameterKey.EBOOK_DEFINITON, initBookDefinition());
        Collection<InternetAddress> emailRecipients = Collections.singleton(new InternetAddress(EMAIL));
        when(emailUtil.getEmailRecipientsByUsername(USERNAME)).thenReturn(emailRecipients);

        generatorNotificationService.sendNotification(executionContext, jobParameters, BODY_MESSAGE, JOB_INSTANCE_ID, JOB_EXECUTION_ID);

        verify(emailService).send(emailRecipients, EXPECTED_SUBJECT, EXPECTED_BODY);
    }

    private JobParameters initJobParameters() {
        Map<String, JobParameter> parameters = new HashMap<>();
        parameters.put(JobParameterKey.ENVIRONMENT_NAME, new JobParameter(TEST_ENV));
        parameters.put(JobParameterKey.USER_NAME, new JobParameter(USERNAME));
        return new JobParameters(parameters);
    }

    private BookDefinition initBookDefinition() {
        BookDefinition bookDefinition = new BookDefinition();
        bookDefinition.setFullyQualifiedTitleId(TITLE_ID);
        bookDefinition.setProviewDisplayName(PROVIEW_DISPLAY_NAME);
        return bookDefinition;
    }
}