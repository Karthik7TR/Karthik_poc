package com.thomsonreuters.uscl.ereader.common.notification.service;

/**
 *  Factory to create {@link com.thomsonreuters.uscl.ereader.common.notification.service.EmailBuilder} depending on context
 */
public interface EmailBuilderFactory {
    /**
     * Returns EmailBuilder
     */
    EmailBuilder create();
}
