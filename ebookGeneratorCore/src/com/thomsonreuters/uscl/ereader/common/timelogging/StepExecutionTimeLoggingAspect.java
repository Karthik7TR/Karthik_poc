package com.thomsonreuters.uscl.ereader.common.timelogging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
public class StepExecutionTimeLoggingAspect {
    @Around("execution(* *(..)) && @annotation(logExecutionTime)")
    public Object around(final ProceedingJoinPoint joinPoint, LogExecutionTime logExecutionTime) throws Throwable {
        final long startTime = System.currentTimeMillis();
        final Object result = joinPoint.proceed();
        final long endTime = System.currentTimeMillis();
        log.debug("{} executed in {} milliseconds", getStepName(joinPoint), endTime - startTime);
        return result;
    }

    private String getStepName(JoinPoint joinPoint) {
        return joinPoint.getTarget().getClass().getSimpleName();
    }
}
