package com.thomsonreuters.uscl.ereader.request;

public class EBookRequestException extends Exception
{
    private static final long serialVersionUID = 643110191834508482L;

    public EBookRequestException(final String message, final Throwable cause)
    {
        super(message, cause);
    }

    /**
     * @param error message
     */
    public EBookRequestException(final String message)
    {
        super(message);
    }
}
