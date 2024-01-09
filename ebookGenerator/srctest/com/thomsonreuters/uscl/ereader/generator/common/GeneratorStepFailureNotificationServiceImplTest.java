package com.thomsonreuters.uscl.ereader.generator.common;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.common.step.BookStepImpl;
import com.thomsonreuters.uscl.ereader.orchestrate.core.service.NotificationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.item.ExecutionContext;

@RunWith(MockitoJUnitRunner.class)
public final class GeneratorStepFailureNotificationServiceImplTest {
    private static final String GROUP_STEP_FAILURE_MESSAGE = "The book was published successfully with groupBook step error.";
    @InjectMocks
    private GeneratorStepFailureNotificationServiceImpl service;
    @Mock
    private NotificationService notificationService;
    @Mock
    private BookStepImpl step;
    @Captor
    private ArgumentCaptor<String> captor;

    @Test
    public void shouldSendEmailWithCorrectMessage() {
        //given
        //when
        service.sendFailureNotification(step, new Exception("msg"));
        //then
        then(notificationService).should().sendNotification(
            any(ExecutionContext.class),
            any(JobParameters.class),
            captor.capture(),
            anyLong(),
            anyLong());
        final String message = captor.getValue();
        assertThat(message, containsString("Error Message : msg"));
    }

    @Test
    public void shouldSendEmailWithGroupStepFailureMessage() {
        given(step.getJobExecutionPropertyBoolean(JobExecutionKey.EXCEPTION_ON_GROUP_STEP_OCCURRED)).willReturn(true);

        service.sendFailureNotification(step, new Exception());

        then(notificationService).should().sendNotification(any(ExecutionContext.class), any(JobParameters.class),
                captor.capture(), anyLong(), anyLong());
        final String emailBody = captor.getValue();
        assertThat(emailBody, containsString(GROUP_STEP_FAILURE_MESSAGE));
    }
}
