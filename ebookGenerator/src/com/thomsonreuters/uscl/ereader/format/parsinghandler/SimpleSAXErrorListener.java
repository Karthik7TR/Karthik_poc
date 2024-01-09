package com.thomsonreuters.uscl.ereader.format.parsinghandler;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;

import lombok.extern.slf4j.Slf4j;

/**
 * Error Listener that can be used by any SAX transformer.
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
@Slf4j
public class SimpleSAXErrorListener implements ErrorListener {

    @Override
    public void error(final TransformerException exception) throws TransformerException {
        log.debug("Recoverable error encountered during transformation.", exception);
    }

    @Override
    public void fatalError(final TransformerException exception) throws TransformerException {
        log.error("Fatal error encountered during transformation.", exception);
        throw exception;
    }

    @Override
    public void warning(final TransformerException exception) throws TransformerException {
        log.warn("Warning encountered during transformation.", exception);
    }
}
