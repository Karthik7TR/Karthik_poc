package com.thomsonreuters.uscl.ereader.format.service;

import java.io.File;
import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentCopyright;
import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentCurrency;
import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;

/**
 * Applies any preprocess transformation on the XML that need to be done to add mark ups
 * or content to the documents
 *
 * @author <a href="mailto:Dong.Kim@thomsonreuters.com">Dong Kim</a> u0155568
 */
public interface XMLPreprocessService {
    /**
     * This method applies XMLFilters to the source XML to apply various
     * preprocess rules to the XML.
     *
     * @param srcDir source directory that contains the html files
     * @param targetDir target directory where the resulting post transformation files are written to
     * @param isFinalStage detemines if content is from Final or Review stage
     * @param copyright list of DocumentCopyright used in filter to change copyright message
     * @param currencies list of DocumentCurrency used in filter to change currency message
     *
     * @return the number of documents that were preprocessed
     *
     * @throws if no source files are found or any parsing/transformation exception are encountered
     */
    int transformXML(
        File srcDir,
        File targetDir,
        boolean isFinalStage,
        List<DocumentCopyright> copyrights,
        List<DocumentCurrency> currencies) throws EBookFormatException;
}
