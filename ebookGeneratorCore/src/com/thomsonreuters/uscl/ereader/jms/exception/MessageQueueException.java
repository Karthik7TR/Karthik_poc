package com.thomsonreuters.uscl.ereader.jms.exception;

/**
 * Represents exceptions thrown by issues connecting to or with information from
 * the message queue for Phoenix ebook requests
 *
 * @author uc209819
 */
public class MessageQueueException extends Exception {
    private static final long serialVersionUID = 1L;

    public MessageQueueException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public MessageQueueException(final String message) {
        super(message);
    }
}
