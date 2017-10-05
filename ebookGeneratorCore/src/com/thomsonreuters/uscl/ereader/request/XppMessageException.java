package com.thomsonreuters.uscl.ereader.request;

public class XppMessageException extends Exception {
    private static final long serialVersionUID = 643110191834508482L;

    public XppMessageException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * @param error message
     */
    public XppMessageException(final String message) {
        super(message);
    }
}
