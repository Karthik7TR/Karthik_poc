package com.thomsonreuters.uscl.ereader.common.proview;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark method with ProView API call to apply retry logic
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ProviewRetry {
    //annotation without parameters
}
