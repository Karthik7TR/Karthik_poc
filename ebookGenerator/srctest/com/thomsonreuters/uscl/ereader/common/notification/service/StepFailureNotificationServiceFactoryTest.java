package com.thomsonreuters.uscl.ereader.common.notification.service;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;

import com.thomsonreuters.uscl.ereader.common.notification.step.SendNotificationStep;
import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import com.thomsonreuters.uscl.ereader.xpp.initialize.InitializeTask;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.batch.core.ExitStatus;
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
    private StepFailureNotificationService<SendNotificationStep> defaultService;

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
    public void shouldReturnDefaultServiceByDefault()
    {
        //given
        given(applicationContext.getBean("defaultStepFailureNotificationService")).willReturn(defaultService);
        //when
        final StepFailureNotificationService<SendNotificationStep> service = factory.create(new BookStep()
        {
            @Override
            protected ExitStatus executeStep() throws Exception
            {
                return null;
            }
        });
        //then
        assertThat(service, is(defaultService));
    }
}
