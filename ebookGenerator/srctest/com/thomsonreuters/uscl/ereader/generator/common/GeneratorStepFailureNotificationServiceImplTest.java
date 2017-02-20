package com.thomsonreuters.uscl.ereader.generator.common;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;

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
public final class GeneratorStepFailureNotificationServiceImplTest
{
    @InjectMocks
    private GeneratorStepFailureNotificationServiceImpl service;
    @Mock
    private NotificationService notificationService;
    @Mock
    private BookStepImpl step;
    @Captor
    private ArgumentCaptor<String> captor;

    @Test
    public void shouldSendEmailWithCorrectMessage()
    {
        //given
        //when
        service.emailFailure(step, new Exception("msg"));
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
}
