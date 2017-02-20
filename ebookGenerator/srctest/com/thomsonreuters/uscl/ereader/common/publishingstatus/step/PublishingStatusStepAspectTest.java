package com.thomsonreuters.uscl.ereader.common.publishingstatus.step;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doThrow;

import com.thomsonreuters.uscl.ereader.common.publishingstatus.service.PublishingStatusUpdateServiceImpl;
import com.thomsonreuters.uscl.ereader.common.step.BookStepImpl;
import com.thomsonreuters.uscl.ereader.stats.PublishingStatus;
import org.aspectj.lang.ProceedingJoinPoint;
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
    private PublishingStatusUpdateServiceImpl publishingStatusUpdateService;
    @Mock
    private ProceedingJoinPoint jp;
    @Mock
    private BookStepImpl step;
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void shouldSavePublishStatus() throws Throwable
    {
        //given
        given(jp.getTarget()).willReturn(step);
        //when
        aspect.around(jp);
        //then
        then(publishingStatusUpdateService).should().savePublishingStats(step, PublishingStatus.COMPLETED);
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
        then(publishingStatusUpdateService).should().savePublishingStats(step, PublishingStatus.COMPLETED);
    }
}
