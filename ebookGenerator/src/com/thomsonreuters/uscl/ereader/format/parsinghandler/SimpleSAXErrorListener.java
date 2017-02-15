package com.thomsonreuters.uscl.ereader.format.parsinghandler;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * Error Listener that can be used by any SAX transformer.
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public class SimpleSAXErrorListener implements ErrorListener
{
    private static final Logger LOG = LogManager.getLogger(SimpleSAXErrorListener.class);

    @Override
    public void error(final TransformerException exception) throws TransformerException
    {
        LOG.debug("Recoverable error encountered during transformation.", exception);
    }

    @Override
    public void fatalError(final TransformerException exception) throws TransformerException
    {
        LOG.error("Fatal error encountered during transformation.", exception);
        throw exception;
    }

    @Override
    public void warning(final TransformerException exception) throws TransformerException
    {
        LOG.warn("Warning encountered during transformation.", exception);
    }
}
