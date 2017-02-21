package com.thomsonreuters.uscl.ereader.common.publishingstatus.service;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.PublishingStatusUpdateStep;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatus;
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
public final class PublishingStatusUpdateServiceFactoryTest
{
    @InjectMocks
    private PublishingStatusUpdateServiceFactory factory;
    @Mock
    private ApplicationContext applicationContext;
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void shouldReturnAppropriateService()
    {
        //given
        final InitializeTask step = new InitializeTask();
        final PublishingStatusUpdateService service = new InitializePublishingStatusUpdateService();
        final PublishingStatusUpdateService anotherService = new GeneralPublishingStatusUpdateService();
        final Map<String, Object> beans = new HashMap<>();
        beans.put("service1", service);
        beans.put("service2", anotherService);
        given(applicationContext.getBeansWithAnnotation(SavePublishingStatusService.class)).willReturn(beans);
        //when
        final List<PublishingStatusUpdateService<PublishingStatusUpdateStep>> services = factory.create(step);
        //then
        assertThat(services, contains(service));
    }

    @Test
    public void shouldThrowExceptionIfNoServiceFound()
    {
        thrown.expect(BeanCreationException.class);
        final InitializeTask step = new InitializeTask();
        final Map<String, Object> beans = new HashMap<>();
        beans.put("service2", new GeneralPublishingStatusUpdateService());
        given(applicationContext.getBeansWithAnnotation(SavePublishingStatus.class)).willReturn(beans);
        //when
        factory.create(step);
        //then
    }
}
