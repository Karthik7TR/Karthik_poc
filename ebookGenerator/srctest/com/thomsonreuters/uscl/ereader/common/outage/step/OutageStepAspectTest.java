package com.thomsonreuters.uscl.ereader.common.outage.step;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import java.util.Date;

import com.thomsonreuters.uscl.ereader.core.outage.domain.PlannedOutage;
import com.thomsonreuters.uscl.ereader.core.outage.domain.PlannedOutageException;
import com.thomsonreuters.uscl.ereader.core.outage.service.OutageProcessor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public final class OutageStepAspectTest
{
    @InjectMocks
    private OutageStepAspect aspect;
    @Mock
    private OutageProcessor outageProcessor;
    @Mock
    private ProceedingJoinPoint jp;
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void shouldExecuteStepIfNoOutage() throws Throwable
    {
        //given
        given(outageProcessor.processPlannedOutages()).willReturn(null);
        //when
        aspect.around(jp);
        //then
        then(jp).should().proceed();
    }

    @Test
    public void shouldThrowExceptionIfOutage() throws Throwable
    {
        //given
        thrown.expect(PlannedOutageException.class);
        given(outageProcessor.processPlannedOutages()).willReturn(outage());
        //when
        aspect.around(jp);
        //then
    }

    private PlannedOutage outage()
    {
        final PlannedOutage outage = new PlannedOutage();
        outage.setStartTime(new Date());
        outage.setEndTime(new Date());
        return outage;
    }
}
