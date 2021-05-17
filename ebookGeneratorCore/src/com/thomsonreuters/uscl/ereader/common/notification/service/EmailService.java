package com.thomsonreuters.uscl.ereader.common.notification.service;

import com.thomsonreuters.uscl.ereader.common.notification.entity.NotificationEmail;
import org.jetbrains.annotations.NotNull;

import javax.mail.internet.InternetAddress;
import java.util.Collection;
import java.util.List;

public interface EmailService {
    /**
     * Send email
     * @param email
     */
    void send(@NotNull NotificationEmail email);

    void send(final Collection<InternetAddress> recipients, final String subject, final String body);

    void send(final String csvRecipients, final String subject, final String body);

    void sendWithAttachment(final Collection<InternetAddress> recipients, final String subject,
        final String body, final List<String> fileNames);

    boolean isUpAndRunning();
}
