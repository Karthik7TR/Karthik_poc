package com.thomsonreuters.uscl.ereader.common.outage.step;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.core.CoreConstants;
import com.thomsonreuters.uscl.ereader.core.outage.domain.PlannedOutage;
import com.thomsonreuters.uscl.ereader.core.outage.domain.PlannedOutageException;
import com.thomsonreuters.uscl.ereader.core.outage.service.OutageProcessor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;

@Aspect
@Order(1)
@Slf4j
public class OutageStepAspect {

    @Resource(name = "outageProcessor")
    private OutageProcessor outageProcessor;

    @Around("execution(* com.thomsonreuters.uscl.ereader.common.outage.step.OutageAwareStep.execute(..))")
    public void around(final ProceedingJoinPoint jp) throws Throwable {
        final PlannedOutage plannedOutage = outageProcessor.processPlannedOutages();
        if (plannedOutage != null) {
            log.debug("Failing job step at start due to planned outage: " + plannedOutage);
            final DateFormat logDf = new SimpleDateFormat(CoreConstants.DATE_TIME_FORMAT_PATTERN);
            throw new PlannedOutageException(
                String.format(
                    "Planned service outage in effect from %s to %s",
                    logDf.format(plannedOutage.getStartTime()),
                    logDf.format(plannedOutage.getEndTime())));
        } else {
            jp.proceed();
        }
    }
}
