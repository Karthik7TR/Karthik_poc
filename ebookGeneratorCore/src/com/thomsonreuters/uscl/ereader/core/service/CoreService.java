package com.thomsonreuters.uscl.ereader.core.service;

import java.util.Collection;

import javax.mail.internet.InternetAddress;

import org.jetbrains.annotations.NotNull;

public interface CoreService {
    /**
     * Lookup all the email addresses (recipients) for the various email notification events.
     * These are the union of the user preference email address for the specified user (the dynamic set) and by the
     * ${job.owner.email.group} Spring property (the static set).
     * @param username the user to find
     * @return the collection of email addresses to send to
     */
    @NotNull
    Collection<InternetAddress> getEmailRecipientsByUsername(String username);

    /**
     * Take a list of user recipients and tack on the group (static) email address as
     * specifed by the job.owner.group.email property.
     * Needed because outages are send to all user preference emails addresses, not just a single user
     * and this provides the means to union the user set along with the static group email address(es).
     * @param recipients union of the static and dynamic set of email addresses
     */
    Collection<InternetAddress> createEmailRecipients(Collection<InternetAddress> recipients);
}
