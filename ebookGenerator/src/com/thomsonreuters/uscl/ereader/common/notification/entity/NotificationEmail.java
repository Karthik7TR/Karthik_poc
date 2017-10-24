package com.thomsonreuters.uscl.ereader.common.notification.entity;

import java.util.Collection;

import javax.mail.internet.InternetAddress;

public class NotificationEmail {
    private Collection<InternetAddress> receipents;
    private String subject;
    private String body;
    private boolean isBodyContentHtmlType;
    /**
     * @return the receipents
     */
    public Collection<InternetAddress> getReceipents() {
        return receipents;
    }
    /**
     * @param receipents the receipents to set
     */
    public void setReceipents(final Collection<InternetAddress> receipents) {
        this.receipents = receipents;
    }
    /**
     * @return the subject
     */
    public String getSubject() {
        return subject;
    }
    /**
     * @param subject the subject to set
     */
    public void setSubject(final String subject) {
        this.subject = subject;
    }
    /**
     * @return the body
     */
    public String getBody() {
        return body;
    }
    /**
     * @param body the body to set
     */
    public void setBody(final String body) {
        this.body = body;
    }
    /**
     * @return the isBodyContentHtmlType
     */
    public boolean isBodyContentHtmlType() {
        return isBodyContentHtmlType;
    }
    /**
     * @param isBodyContentHtmlType the isBodyContentHtmlType to set
     */
    public void setBodyContentHtmlType(final boolean isBodyContentHtmlType) {
        this.isBodyContentHtmlType = isBodyContentHtmlType;
    }

    public NotificationEmail(
        final Collection<InternetAddress> receipents,
        final String subject,
        final String body,
        final boolean isBodyContentHtmlType) {
        super();
        this.receipents = receipents;
        this.subject = subject;
        this.body = body;
        this.isBodyContentHtmlType = isBodyContentHtmlType;
    }

    public NotificationEmail(final Collection<InternetAddress> receipents, final String subject, final String body) {
        super();
        this.receipents = receipents;
        this.subject = subject;
        this.body = body;
        isBodyContentHtmlType = false;
    }
}
