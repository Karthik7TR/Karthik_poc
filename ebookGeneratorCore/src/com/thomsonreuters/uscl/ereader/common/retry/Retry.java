package com.thomsonreuters.uscl.ereader.common.retry;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

/**
 * Annotation provides an ability to retry method execution
 * Spring @Retrieble for some reason doesn't work for our project
 * Properties keys for retries count and delay can be provided instead of hardcoded values.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Retry {
    int value() default 1;
    Class<? extends Exception>[] exceptions() default {};
    long delay() default 500;
    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;
    String propertyValue() default StringUtils.EMPTY;
    String delayProperty() default StringUtils.EMPTY;
}
