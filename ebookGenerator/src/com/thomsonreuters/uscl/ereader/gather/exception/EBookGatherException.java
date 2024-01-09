package com.thomsonreuters.uscl.ereader.gather.exception;

/**
 * Generic gather exceptions that is thrown when I/O issues or other formatting anomalies have been encountered.
 *
 * @author u0072938
 */
public class EBookGatherException extends Exception {
    private static final long serialVersionUID = 1L;

    public EBookGatherException(final String message) {
        super(message);
    }

    public EBookGatherException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
