package com.thomsonreuters.uscl.ereader.common.notification.service;

import com.thomsonreuters.uscl.ereader.common.notification.entity.NotificationEmail;
import com.thomsonreuters.uscl.ereader.util.EmailNotification;
import org.jetbrains.annotations.NotNull;

public class EmailServiceImpl implements EmailService {
    @Override
    public void send(@NotNull final NotificationEmail email) {
        EmailNotification.send(email.getRecipients(), email.getSubject(), email.getBody(), email.isBodyContentHtmlType());
    }
}
