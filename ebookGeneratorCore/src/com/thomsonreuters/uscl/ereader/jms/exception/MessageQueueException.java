package com.thomsonreuters.uscl.ereader.jms.exception;

public class MessageQueueException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public MessageQueueException(String message, Throwable cause) {
        super(message, cause);
    }

	public MessageQueueException(String message) {
		super(message);
	}
}