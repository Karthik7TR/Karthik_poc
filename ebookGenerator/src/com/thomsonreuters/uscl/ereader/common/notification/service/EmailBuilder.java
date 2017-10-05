package com.thomsonreuters.uscl.ereader.common.notification.service;

/**
 * Creates email subject and body
 */
public interface EmailBuilder {
    /**
     * Returns email subject
     */
    String getSubject();

    /**
     * Returns email body
     */
    String getBody();
}
