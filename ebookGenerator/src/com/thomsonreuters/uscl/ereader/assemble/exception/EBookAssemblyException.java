package com.thomsonreuters.uscl.ereader.assemble.exception;

/**
 * A subclass of Exception intended to be thrown by EbookAssemblyService in cases where low-level file-system resources weren't available, or when the archive itself is corrupt.
 * @author u0081674
 *
 */
public class EBookAssemblyException extends Exception
{
    private static final long serialVersionUID = 1L;

    public EBookAssemblyException(final String message, final Throwable cause)
    {
        super(message, cause);
    }
}
