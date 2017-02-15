package com.thomsonreuters.uscl.ereader.jms.exception;

public class MessageQueueException extends Exception
{
    private static final long serialVersionUID = 1L;

    public MessageQueueException(final String message, final Throwable cause)
    {
        super(message, cause);
    }

    public MessageQueueException(final String message)
    {
        super(message);
    }
}
