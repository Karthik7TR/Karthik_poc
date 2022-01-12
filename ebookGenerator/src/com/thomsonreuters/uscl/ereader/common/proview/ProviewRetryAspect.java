package com.thomsonreuters.uscl.ereader.common.proview;

import java.util.concurrent.TimeUnit;

import com.thomsonreuters.uscl.ereader.core.CoreConstants;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewRuntimeException;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;

/**
 * Aspect to retry Proview API call if one failed
 */
@Aspect
@Setter
@Slf4j
public class ProviewRetryAspect {

    @Value("6")
    private Integer maxNumberOfRetries;
    @Value("15")
    private Integer baseSleepTimeInSeconds;

    @Around("@annotation(com.thomsonreuters.uscl.ereader.common.proview.ProviewRetry)")
    public void around(final ProceedingJoinPoint jp) throws Throwable {
        long currentSleepTime = baseSleepTimeInSeconds;
        waitProview(currentSleepTime);
        for (int i = 0; i <= maxNumberOfRetries; i++) {
            try {
                jp.proceed();
                return;
            } catch (final ProviewException e) {
                if (e.getMessage().equalsIgnoreCase(CoreConstants.TTILE_IN_QUEUE)
                        || (e.getCause() != null && e.getCause().getMessage().contains(CoreConstants.GROUP_METADATA_EXCEPTION))) {
                    currentSleepTime = Math.round(baseSleepTimeInSeconds * Math.pow(2, i + 1));
                    log.warn(
                        String.format(
                            "Retriable status received: waiting %d seconds (retryCount: %d)",
                            currentSleepTime,
                            i + 1));
                    waitProview(currentSleepTime);
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

    void waitProview(final long timeInSeconds) {
        // Most of the books should finish in two minutes
        try {
            TimeUnit.SECONDS.sleep(timeInSeconds);
        } catch (final InterruptedException e) {
            log.error("InterruptedException during HTTP retry", e);
            Thread.currentThread().interrupt();
        }
    }
}
