package com.thomsonreuters.uscl.ereader.common.timelogging;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import com.thomsonreuters.uscl.ereader.TestUtils;
import lombok.SneakyThrows;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.springframework.batch.repeat.RepeatStatus;

@PrepareForTest({StepExecutionTimeLoggingAspect.class, System.class})
@RunWith(PowerMockRunner.class)
public final class StepExecutionTimeLoggingAspectTest {

    private StepExecutionTimeLoggingAspect timeLoggingAspect;

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Mock
    private LogExecutionTime logExecutionTime;

    @Mock
    private Logger log;

    private Object target;

    private Long startTime;
    private Long endTime;

    @Before
    public void setUp() {
        timeLoggingAspect = spy(new StepExecutionTimeLoggingAspect());
        mockStatic(System.class);
        target = new Object();
        TestUtils.setLogger(StepExecutionTimeLoggingAspect.class, log);
        startTime = 10L;
        endTime = 20L;
    }

    @SneakyThrows
    @Test
    public void shouldLogExecutionTime() {
        when(System.currentTimeMillis())
            .thenReturn(startTime)
            .thenReturn(endTime);
        when(joinPoint.proceed()).thenReturn(RepeatStatus.FINISHED);
        when(joinPoint.getTarget()).thenReturn(target);

        timeLoggingAspect.around(joinPoint, logExecutionTime);

        verify(joinPoint).proceed();
        verify(log).debug("{} executed in {} milliseconds", target.getClass().getSimpleName(), endTime - startTime);
    }
}
