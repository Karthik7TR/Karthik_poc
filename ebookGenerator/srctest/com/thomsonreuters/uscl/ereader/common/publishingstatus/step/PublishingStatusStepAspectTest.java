package com.thomsonreuters.uscl.ereader.common.publishingstatus.step;

import static java.util.Arrays.asList;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doThrow;

import com.thomsonreuters.uscl.ereader.common.publishingstatus.service.PublishingStatusUpdateService;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.service.PublishingStatusUpdateServiceFactory;
import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import com.thomsonreuters.uscl.ereader.stats.PublishingStatus;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public final class PublishingStatusStepAspectTest
{
    @InjectMocks
    private PublishingStatusStepAspect aspect;
    @Mock
    private PublishingStatusUpdateServiceFactory factory;
    @Mock
    private PublishingStatusUpdateService<PublishingStatusUpdateStep> service;
    @Mock
    private PublishingStatusUpdateService<PublishingStatusUpdateStep> anotherService;
    @Mock
    private ProceedingJoinPoint jp;
    @Mock
    private BookStep step;
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp()
    {
        given(factory.create(step)).willReturn(asList(service, anotherService));
    }

    @Test
    public void shouldSavePublishStatus() throws Throwable
    {
        //given
        given(jp.getTarget()).willReturn(step);
        //when
        aspect.around(jp);
        //then
        then(service).should().savePublishingStats(step, PublishingStatus.COMPLETED);
        then(anotherService).should().savePublishingStats(step, PublishingStatus.COMPLETED);
    }

    @Test
    public void shouldSavePublishStatusAndThrowExceptionIfFailed() throws Throwable
    {
        //given
        thrown.expect(RuntimeException.class);
        given(jp.getTarget()).willReturn(step);
        doThrow(new RuntimeException()).when(jp).proceed();
        //when
        aspect.around(jp);
        //then
        then(service).should().savePublishingStats(step, PublishingStatus.COMPLETED);
        then(anotherService).should().savePublishingStats(step, PublishingStatus.COMPLETED);
    }
}
