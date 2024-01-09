package com.thomsonreuters.uscl.ereader.common.filesystem;

/**
 * Runtime wrapper around various file system exceptions
 */
public class FileSystemException extends RuntimeException {
    private static final long serialVersionUID = 374321634822533484L;

    public FileSystemException(final String message) {
        super(message);
    }

    public FileSystemException(final String message, final Throwable t) {
        super(message, t);
    }
}
