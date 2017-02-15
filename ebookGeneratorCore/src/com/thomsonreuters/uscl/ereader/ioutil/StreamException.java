package com.thomsonreuters.uscl.ereader.ioutil;

import java.io.IOException;

public class StreamException extends IOException
{
    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     *
     * @param e a Throwable object.
     */
    public StreamException(final Throwable e)
    {
        super(e);
    }
}
