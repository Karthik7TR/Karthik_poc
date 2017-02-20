package com.thomsonreuters.uscl.ereader.common.notification.service;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;

import com.thomsonreuters.uscl.ereader.common.notification.step.SendNotificationStep;
import com.thomsonreuters.uscl.ereader.notification.step.SendEmailNotificationStepImpl;
import com.thomsonreuters.uscl.ereader.xpp.initialize.InitializeTask;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;

@RunWith(MockitoJUnitRunner.class)
public final class StepFailureNotificationServiceFactoryTest
{
    @InjectMocks
    private StepFailureNotificationServiceFactory factory;
    @Mock
    private ApplicationContext applicationContext;
    @Mock
    private StepFailureNotificationService<SendNotificationStep> xppService;
    @Mock
    private StepFailureNotificationService<SendNotificationStep> generatorService;
    @Mock
    private StepFailureNotificationService<SendNotificationStep> defaultService;
    @Mock
    private SendNotificationStep sendNotificationStep;

    @Test
    public void shouldReturnXppServiceForXppStep()
    {
        //given
        given(applicationContext.getBean("xppStepFailureNotificationService")).willReturn(xppService);
        //when
        final StepFailureNotificationService<SendNotificationStep> service = factory.create(new InitializeTask());
        //then
        assertThat(service, is(xppService));
    }

    @Test
    public void shouldReturnGeneratorServiceForGeneratorStep()
    {
        //given
        given(applicationContext.getBean("generatorStepFailureNotificationService")).willReturn(generatorService);
        //when
        final StepFailureNotificationService<SendNotificationStep> service = factory.create(new SendEmailNotificationStepImpl());
        //then
        assertThat(service, is(generatorService));
    }

    @Test
    public void shouldReturnDefaultServiceByDefault()
    {
        //given
        given(applicationContext.getBean("defaultStepFailureNotificationService")).willReturn(defaultService);
        //when
        final StepFailureNotificationService<SendNotificationStep> service = factory.create(sendNotificationStep);
        //then
        assertThat(service, is(defaultService));
    }
}
