package com.thomsonreuters.uscl.ereader.common.notification.service;

import com.thomsonreuters.uscl.ereader.common.notification.step.SendNotificationStep;

/**
 * Notifies about exception thrown on a step
 */
public interface StepFailureNotificationService<T extends SendNotificationStep> {
    /**
     * Send notification about exception was thrown
     * @param e exception
     */
    void sendFailureNotification(T step, Exception e);
}
