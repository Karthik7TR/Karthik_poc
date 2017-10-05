package com.thomsonreuters.uscl.ereader.common.notification.service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;

/**
 * Mark step with this annotation to chose send failure notification strategy
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SendFailureNotificationStrategy {
    FailureNotificationType value();
}
