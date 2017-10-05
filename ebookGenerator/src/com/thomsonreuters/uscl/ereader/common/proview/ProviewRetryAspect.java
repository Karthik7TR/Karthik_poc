package com.thomsonreuters.uscl.ereader.common.proview;

import java.util.concurrent.TimeUnit;

import com.thomsonreuters.uscl.ereader.core.CoreConstants;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewRuntimeException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;

/**
 * Aspect to retry Proview API call if one failed
 */
@Aspect
public class ProviewRetryAspect {
    private static final Logger LOG = LogManager.getLogger(ProviewRetryAspect.class);

    @Value("2")
    private Integer maxNumberOfRetries;
    @Value("15")
    private Integer sleepTimeInMinutes;
    @Value("2")
    private Integer baseSleepTimeInMinutes;

    @Around("@annotation(com.thomsonreuters.uscl.ereader.common.proview.ProviewRetry)")
    public void around(final ProceedingJoinPoint jp) throws Throwable {
        waitProview(baseSleepTimeInMinutes);
        for (int i = 0; i <= maxNumberOfRetries; i++) {
            try {
                jp.proceed();
                return;
            } catch (final ProviewException e) {
                if (e.getMessage().equalsIgnoreCase(CoreConstants.TTILE_IN_QUEUE)) {
                    LOG.warn(
                        String.format(
                            "Retriable status received: waiting %d minutes (retryCount: %d)",
                            sleepTimeInMinutes,
                            i + 1));
                    waitProview(sleepTimeInMinutes);
                } else {
                    throw new ProviewRuntimeException(e.getMessage());
                }
            }
        }
        throw new ProviewRuntimeException(
            String
                .format(
                    "Tried %d times. Proview might be down "
                        + "or still in the process of loading the book. Please try again later.",
                    maxNumberOfRetries + 1));
    }

    void waitProview(final Integer timeInMinutes) {
        // Most of the books should finish in two minutes
        try {
            TimeUnit.MINUTES.sleep(timeInMinutes);
        } catch (final InterruptedException e) {
            LOG.error("InterruptedException during HTTP retry", e);
            Thread.currentThread().interrupt();
        }
    }

    void setMaxNumberOfRetries(final Integer maxNumberOfRetries) {
        this.maxNumberOfRetries = maxNumberOfRetries;
    }
}
