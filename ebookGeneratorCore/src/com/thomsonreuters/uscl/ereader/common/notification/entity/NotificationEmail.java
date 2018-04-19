package com.thomsonreuters.uscl.ereader.common.notification.entity;

import java.util.Collection;

import javax.mail.internet.InternetAddress;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationEmail {
    private Collection<InternetAddress> recipients;
    private String subject;
    private String body;
    private boolean isBodyContentHtmlType;

    public NotificationEmail(
        final Collection<InternetAddress> recipients,
        final String subject,
        final String body,
        final boolean isBodyContentHtmlType) {
        this.recipients = recipients;
        this.subject = subject;
        this.body = body;
        this.isBodyContentHtmlType = isBodyContentHtmlType;
    }

    public NotificationEmail(final Collection<InternetAddress> recipients, final String subject, final String body) {
        this(recipients, subject, body, false);
    }
}
