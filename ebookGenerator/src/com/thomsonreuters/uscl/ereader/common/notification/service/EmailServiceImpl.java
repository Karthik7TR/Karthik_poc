package com.thomsonreuters.uscl.ereader.common.notification.service;

import java.util.Collection;

import javax.mail.internet.InternetAddress;

import com.thomsonreuters.uscl.ereader.common.notification.entity.NotificationEmail;
import com.thomsonreuters.uscl.ereader.util.EmailNotification;
import org.jetbrains.annotations.NotNull;

public class EmailServiceImpl implements EmailService {
    @Override
    public void send(@NotNull final NotificationEmail email) {
        EmailNotification.send(email.getReceipents(), email.getSubject(), email.getBody(), email.isBodyContentHtmlType());
    }

    @Override
    public void send(
        @NotNull final Collection<InternetAddress> recipients,
        @NotNull final String subject,
        @NotNull final String body) {
        EmailNotification.send(recipients, subject, body, false);
    }
}
