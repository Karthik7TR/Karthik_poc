package com.thomsonreuters.uscl.ereader.common.notification.step;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;

import com.thomsonreuters.uscl.ereader.common.notification.service.StepFailureNotificationService;
import com.thomsonreuters.uscl.ereader.common.notification.service.StepFailureNotificationServiceFactory;
import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public final class SendNotificationStepAspectTest
{
    @InjectMocks
    private SendNotificationStepAspect aspect;
    @Mock
    private StepFailureNotificationServiceFactory stepFailureNotificationServiceFactory;
    @Mock
    private ProceedingJoinPoint jp;
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    @Mock
    private BookStep step;
    @Mock
    private StepFailureNotificationService<SendNotificationStep> service;

    @Test
    public void shouldDoNothingIfNoException() throws Throwable
    {
        //give
        given(jp.getTarget()).willReturn(step);
        //when
        aspect.around(jp);
        //then
        then(stepFailureNotificationServiceFactory).should(never()).create(step);
    }

    @Test
    public void shouldSendNotificationIfException() throws Throwable
    {
        //give
        thrown.expect(RuntimeException.class);
        given(jp.getTarget()).willReturn(step);
        doThrow(RuntimeException.class).when(jp).proceed();
        given(stepFailureNotificationServiceFactory.create(step)).willReturn(service);
        //when
        aspect.around(jp);
        //then
        then(service).should().sendFailureNotification(eq(step), any(RuntimeException.class));
    }
}
