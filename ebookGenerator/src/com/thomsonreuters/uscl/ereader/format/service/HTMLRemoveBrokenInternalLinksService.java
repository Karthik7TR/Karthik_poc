package com.thomsonreuters.uscl.ereader.format.service;

import java.io.File;
import java.util.Collection;

import javax.mail.internet.InternetAddress;

import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;

/**
 * Applies any post transformation on the HTML that need to be done to cleanup or make
 * the HTML ProView complient.
 *
 * @author <a href="mailto:Kirsten.Gunn@thomsonreuters.com">Kirsten Gunn</a> u0076257
 */
public interface HTMLRemoveBrokenInternalLinksService {
    /**
     * This method applies multiple XMLFilters to the source HTML to apply various
     * post transformation rules to the HTML.
     *
     * @param srcDir source directory that contains the html files
     * @param targetDir target directory where the resulting post transformation files are written to
     * @param title title of the book being published
     * @param jobId the job instance identifier of the current transformation run
     * @param envName the current execution environment name, like ci or prod
     * @param emailRecipients who to notify when things go wrong
     * @return the number of documents that had post transformations run on them
     *
     * @throws if no source files are found or any parsing/transformation exception are encountered
     */
    int transformHTML(
        File srcDir,
        File targetDir,
        String title,
        Long jobId,
        String envName,
        Collection<InternetAddress> emailRecipients) throws EBookFormatException;
}
