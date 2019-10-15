package com.thomsonreuters.uscl.ereader.format.service;

import java.io.File;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;

/**
 * The TransformerService iterates through a directory of XML files, retrieves the appropriate XSLT stylesheets,
 * compiles them and produces intermediate HTML files that do not yet have all the proper HTML document wrappers
 * and ProView mark up.
 *
 * @author u0095869
 */
public interface TransformerService {
    /**
     * Transforms all XML files found in the passed in XML directory and writes the
     * transformed HTML files to the specified target directory. If the directory does not exist
     * the service creates it.
     *
     * @param processAnnotationsDir the directory that contains all the preprocessed XML files with updated annotations for this eBook.
     * @param metaDir the directory that contains all the Novus document metadata files for this eBook.
     * @param imgMetaDir the directory that contains all the ImageMetadata built files for this eBook.
     * @param transDir the target directory to which all the intermediate HTML files will be written out to.
     * @param jobID the identifier of the job currently running, used to lookup appropriate document metadata
     * @param bookDefinition contains book related job controls
     *
     * @return number of documents that were transformed
     *
     * @throws EBookFormatException if an error occurs during the process.
     */
    int transformXMLDocuments(
        File processAnnotationsDir,
        File metaDir,
        File imgMetaDir,
        File transDir,
        Long jobID,
        BookDefinition bookDefinition,
        File staticContentDir) throws EBookFormatException;
}
