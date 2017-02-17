package com.thomsonreuters.uscl.ereader.common.notification.service;

import java.util.Collection;

import javax.mail.internet.InternetAddress;

import org.jetbrains.annotations.NotNull;

/**
 * Service to send emails. This is a wrapper over
 * {@link com.thomsonreuters.uscl.ereader.util.EmailNotification} designed to avoid static methods.
 * In future all send email logic should be moved here and {@code EmailNotification} should be
 * removed.
 *
 * @author Ilia Bochkarev UC220946
 *
 */
public interface EmailService
{
    /**
     * Send email
     * @param recipients
     * @param subject
     * @param body
     */
    void send(@NotNull Collection<InternetAddress> recipients, @NotNull String subject, @NotNull String body);
}
