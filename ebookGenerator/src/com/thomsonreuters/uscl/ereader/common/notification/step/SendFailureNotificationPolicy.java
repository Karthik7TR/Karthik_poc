package com.thomsonreuters.uscl.ereader.common.notification.step;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark step with this annotation to chose send failure notification strategy
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SendFailureNotificationPolicy {
    FailureNotificationType value();
}
