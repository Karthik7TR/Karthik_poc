package com.thomsonreuters.uscl.ereader.frontmatter.exception;

/**
 * Generic Front Matter Generation exceptions that is thrown when
 * any HTML generation anomalies are encountered.
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public class EBookFrontMatterGenerationException extends Exception
{
    private static final long serialVersionUID = 1L;

    public EBookFrontMatterGenerationException(final String message)
    {
        super(message);
    }

    public EBookFrontMatterGenerationException(final String message, final Throwable cause)
    {
        super(message, cause);
    }
}
