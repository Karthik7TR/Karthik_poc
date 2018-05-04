package com.thomsonreuters.uscl.ereader.common.exception;

/**
 * General exception for eBook.
 */
public class EBookException extends RuntimeException {
    private static final long serialVersionUID = 5137352032343139022L;

    public EBookException(final String cause) {
        super(cause);
    }

    public EBookException(final Throwable cause) {
        super(cause);
    }

    public EBookException(final String causeString, final Exception causeException) {
        super(causeString, causeException);
    }
}
