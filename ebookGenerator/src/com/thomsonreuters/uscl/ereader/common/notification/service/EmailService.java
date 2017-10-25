package com.thomsonreuters.uscl.ereader.common.notification.service;

import com.thomsonreuters.uscl.ereader.common.notification.entity.NotificationEmail;
import org.jetbrains.annotations.NotNull;

/**
 * Service to send emails. This is a wrapper over
 * {@link com.thomsonreuters.uscl.ereader.util.EmailNotification} designed to avoid static methods.
 * In future all send email logic should be moved here and {@code EmailNotification} should be
 * removed.
 */
public interface EmailService {
    /**
     * Send email
     * @param email
     */
    void send(@NotNull NotificationEmail email);
}
