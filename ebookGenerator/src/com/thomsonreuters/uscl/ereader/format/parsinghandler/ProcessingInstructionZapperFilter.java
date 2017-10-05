package com.thomsonreuters.uscl.ereader.format.parsinghandler;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * Wipes out any processing instructions that may be in the XML document that is parsed by a parser that uses this filter.
 *
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 */
public class ProcessingInstructionZapperFilter extends XMLFilterImpl {
    @Override
    public void processingInstruction(final String target, final String data) throws SAXException {
        //Do not process PIs.
    }
}
