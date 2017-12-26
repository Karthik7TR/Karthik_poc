package com.thomsonreuters.uscl.ereader.common.retry.infrastructure;

import java.util.concurrent.TimeUnit;

/**
 * Metadata for retrieble method.
 */
public class RetryMetadata {
    private final String methodName;
    private final int retriesCount;
    private final Class<? extends Exception>[] exceptions;
    private final long delay;
    private final TimeUnit timeUnit;

    public RetryMetadata(final String methodName, final int retriesCount,
                         final Class<? extends Exception>[] exceptions, final long delay,
                         final TimeUnit timeUnit) {
        this.methodName = methodName;
        this.retriesCount = retriesCount;
        this.exceptions = exceptions;
        this.delay = delay;
        this.timeUnit = timeUnit;
    }

    public String getMethodName() {
        return methodName;
    }

    public int getRetriesCount() {
        return retriesCount;
    }

    public Class<? extends Exception>[] getExceptions() {
        return exceptions;
    }

    public long getDelay() {
        return delay;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }
}
