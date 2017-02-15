package com.thomsonreuters.uscl.ereader.gather.exception;

public class GatherException extends Exception
{
    private static final long serialVersionUID = -4259100376867069612L;

    private int errorCode = 0;

    public GatherException(final String message, final Throwable cause)
    {
        super(message, cause);
    }

    public GatherException(final String message, final Throwable cause, final int errorCode)
    {
        super(message, cause);
        setErrorCode(errorCode);
    }

    public GatherException(final String message)
    {
        super(message);
    }

    public GatherException(final String message, final int errorCode)
    {
        super(message);
        setErrorCode(errorCode);
    }

    public int getErrorCode()
    {
        return errorCode;
    }

    private void setErrorCode(final int errorCode)
    {
        this.errorCode = errorCode;
    }
}
