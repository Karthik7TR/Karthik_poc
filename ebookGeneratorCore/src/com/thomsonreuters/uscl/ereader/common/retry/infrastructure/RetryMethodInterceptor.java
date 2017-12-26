package com.thomsonreuters.uscl.ereader.common.retry.infrastructure;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RetryMethodInterceptor implements MethodInterceptor {
    private static final Logger LOG = LoggerFactory.getLogger(RetryMethodInterceptor.class);

    private final Map<String, RetryMetadata> methodsMetadata;

    public RetryMethodInterceptor(final List<RetryMetadata> methodsMetadataList) {
        methodsMetadata = methodsMetadataList.stream()
            .collect(Collectors.toMap(RetryMetadata::getMethodName, Function.identity()));
    }

    @Override
    public Object invoke(final MethodInvocation invocation) throws Throwable {
        final RetryMetadata methodData = methodsMetadata.get(invocation.getMethod().getName());
        return methodData == null ? invocation.proceed() : invokeWithRetries(invocation, methodData);
    }

    private Object invokeWithRetries(final MethodInvocation invocation, final RetryMetadata retryMetadata) throws Throwable {
        Exception caughtException;

        int tryCount = 0;
        do {
            try {
                return invocation.proceed();
            } catch (final Exception e) {
                caughtException = e;
                if (isExceptionShouldBeHandled(e, retryMetadata.getExceptions())) {
                    LOG.error(e.getMessage(), e);
                    tryCount++;
                    delay(retryMetadata.getDelay(), retryMetadata.getTimeUnit());
                    continue;
                }
                throw e;
            }
        } while (tryCount < retryMetadata.getRetriesCount());
        throw caughtException;
    }

    private boolean isExceptionShouldBeHandled(final Exception e, final Class<? extends Exception>[] exceptions) {
        return exceptions.length == 0
            || Stream.of(exceptions).anyMatch(exceptionClass -> exceptionClass.equals(e.getClass()));
    }

    private void delay(final long duration, final TimeUnit timeUnit) {
        try {
            timeUnit.sleep(duration);
        } catch (final InterruptedException e) {
            LOG.error(e.getMessage(), e);
        }
    }
}
