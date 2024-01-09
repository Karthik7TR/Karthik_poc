package com.thomsonreuters.uscl.ereader.assemble.service;

import java.io.OutputStream;
import java.util.List;

import com.thomsonreuters.uscl.ereader.assemble.exception.PlaceholderDocumentServiceException;

/**
 * Implementors of PlaceholderDocumentService are responsible for generating placeholder documents.
 *
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 *
 */
public interface PlaceholderDocumentService {
    /**
     * Writes a "placeholder" XHTML document containing the specified display text to the provided {@link OutputStream}.
     *
     * <p>
     * We do this to support rendering text in proview where there is no corresponding document text loaded to Novus.
     * </p>
     *
     * @param documentStream the {@link} to write the document to.
     * @param displayText the text to inject into the title and body of the document.
     * @param tocGuid the guid to use as an anchor.
     * @param anchors additional TOC guids that need to be cascaded into the missing document
     * @throws PlaceholderDocumentServiceException if the data could not be written or the document template was misconfigured.
     */
    void generatePlaceholderDocument(
        OutputStream documentStream,
        String displayText,
        String tocGuid,
        List<String> anchors) throws PlaceholderDocumentServiceException;
}
