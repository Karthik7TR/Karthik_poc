package com.thomsonreuters.uscl.ereader.util;

/**
 * A subclass of Exception intended to be thrown by Ebook Server access Service in cases if it fails to connect to server with given credentials .
 *
 *  @author Mahendra Survase (u0105927)
 */
public class EBookServerException extends Exception
{
    private static final long serialVersionUID = 1L;

    public EBookServerException(final String message)
    {
        super(message);
    }

    public EBookServerException(final String message, final Throwable cause)
    {
        super(message, cause);
    }
}
