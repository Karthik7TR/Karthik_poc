package com.thomsonreuters.uscl.ereader.common.notification.service;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.HashMap;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.common.notification.step.SendNotificationStep;
import com.thomsonreuters.uscl.ereader.generator.common.GeneratorStepFailureNotificationServiceImpl;
import com.thomsonreuters.uscl.ereader.xpp.common.XppStepFailureNotificationServiceImpl;
import com.thomsonreuters.uscl.ereader.xpp.initialize.InitializeTask;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.ApplicationContext;

@RunWith(MockitoJUnitRunner.class)
public final class StepFailureNotificationServiceFactoryTest
{
    @InjectMocks
    private StepFailureNotificationServiceFactory factory;
    @Mock
    private ApplicationContext applicationContext;
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void shouldReturnAppropriateService()
    {
        //given
        final InitializeTask step = new InitializeTask();
        final StepFailureNotificationService service = new GeneratorStepFailureNotificationServiceImpl();
        final StepFailureNotificationService anotherService = new XppStepFailureNotificationServiceImpl();
        final Map<String, Object> beans = new HashMap<>();
        beans.put("service1", service);
        beans.put("service2", anotherService);
        given(applicationContext.getBeansWithAnnotation(SendFailureNotificationStrategy.class)).willReturn(beans);
        //when
        final StepFailureNotificationService<SendNotificationStep> notificationService = factory.create(step);
        //then
        assertThat(notificationService, is(anotherService));
    }

    @Test
    public void shouldThrowExceptionIfNoServiceFound()
    {
        thrown.expect(BeanCreationException.class);
        final InitializeTask step = new InitializeTask();
        final Map<String, Object> beans = new HashMap<>();
        beans.put("service2", new GeneratorStepFailureNotificationServiceImpl());
        given(applicationContext.getBeansWithAnnotation(SendFailureNotificationStrategy.class)).willReturn(beans);
        //when
        factory.create(step);
        //then
    }
}
