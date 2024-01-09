package com.thomsonreuters.uscl.ereader.gather.image.domain;

/**
 * Wrapper for image download or metatdata fetch problems.
 */
public class ImageException extends Exception {
    private static final long serialVersionUID = 2856995610793806521L;

    public ImageException(final Throwable cause) {
        super(cause);
    }

    public ImageException(final String message) {
        super(message);
    }

    public ImageException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
