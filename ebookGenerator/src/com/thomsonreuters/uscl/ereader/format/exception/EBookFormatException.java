package com.thomsonreuters.uscl.ereader.format.exception;

/**
 * Generic Format exceptions that is thrown when I/O issues or other formatting anomalies have been encountered.
 *
 * @author u0095869
 */
public class EBookFormatException extends Exception {
    private static final long serialVersionUID = 1L;

    public EBookFormatException(final String message) {
        super(message);
    }

    public EBookFormatException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
