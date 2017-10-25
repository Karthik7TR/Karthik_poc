package com.thomsonreuters.uscl.ereader.common.notification.entity;

import java.util.Collection;

import javax.mail.internet.InternetAddress;

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

    public Collection<InternetAddress> getRecipients() {
        return recipients;
    }

    public void setRecipients(final Collection<InternetAddress> recipients) {
        this.recipients = recipients;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(final String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(final String body) {
        this.body = body;
    }

    public boolean isBodyContentHtmlType() {
        return isBodyContentHtmlType;
    }

    public void setBodyContentHtmlType(final boolean isBodyContentHtmlType) {
        this.isBodyContentHtmlType = isBodyContentHtmlType;
    }
}
