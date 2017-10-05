package com.thomsonreuters.uscl.ereader.deliver.exception;

/**
 * Thrown when error responses are received from the ProviewClient.
 *
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 *
 */
public class ProviewRuntimeException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private String statusCode;

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(final String statusCode) {
        this.statusCode = statusCode;
    }

    public ProviewRuntimeException(final String message) {
        super(message);
    }

    public ProviewRuntimeException(final String statusCode, final String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public ProviewRuntimeException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
