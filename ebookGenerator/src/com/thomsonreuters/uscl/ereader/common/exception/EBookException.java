package com.thomsonreuters.uscl.ereader.common.exception;

/**
 * General exception for eBook.
 */
public class EBookException extends RuntimeException {

    public EBookException(final String cause) {
        super(cause);
    }

    public EBookException(final Throwable cause) {
        super(cause);
    }
}
