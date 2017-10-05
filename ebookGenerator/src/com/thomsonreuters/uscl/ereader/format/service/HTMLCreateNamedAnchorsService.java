package com.thomsonreuters.uscl.ereader.format.service;

import java.io.File;

import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;

/**
 * Applies any post transformation on the HTML that need to be done to cleanup or make
 * the HTML ProView compliant.
 *
 * @author <a href="mailto:Kirsten.Gunn@thomsonreuters.com">Kirsten Gunn</a> u0076257
 */
public interface HTMLCreateNamedAnchorsService {
    /**
     * This method applies multiple XMLFilters to the source HTML to apply various
     * post transformation rules to the HTML.
     *
     * @param srcDir source directory that contains the html files
     * @param targetDir target directory where the resulting post transformation files are written to
     * @param title title of the book being published
     * @param jobId the job identifier of the current transformation run
     * @param docToTocMap location of the file that contains the document to TOC mappings
     * @return the number of documents that had post transformations run on them
     *
     * @throws if no source files are found or any parsing/transformation exception are encountered
     */
    int transformHTML(File srcDir, File targetDir, String title, Long jobId, File docToTocMap)
        throws EBookFormatException;
}
