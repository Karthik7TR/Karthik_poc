package com.thomsonreuters.uscl.ereader.gather.util.images;

import java.io.IOException;

public class ImageConverterException extends RuntimeException {
    private static final long serialVersionUID = 7270462432999665730L;

    public ImageConverterException(final String message) {
        super(message);
    }

    public ImageConverterException(final Throwable t) {
        super(t);
    }

    public ImageConverterException(final String message, final IOException e) {
        super(message, e);
    }
}
