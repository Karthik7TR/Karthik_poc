package com.thomsonreuters.uscl.ereader.common.xslt;

/**
 * Runtime wrapper around various transformation exceptions
 */
public class XslTransformationException extends RuntimeException
{
    private static final long serialVersionUID = 3213596761856156288L;

    public XslTransformationException(final String message, final Throwable e)
    {
        super(message, e);
    }
}
