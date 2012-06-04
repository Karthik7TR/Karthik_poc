/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
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
	 * @param jobId the job identifier of the current transformation run
	 * @param emailRecipients who to notify when things go wrong
	 * @return the number of documents that had post transformations run on them
	 * 
	 * @throws if no source files are found or any parsing/transformation exception are encountered
	 */
	public int transformHTML(final File srcDir, final File targetDir, 
			final String title, final Long jobId, Collection<InternetAddress> emailRecipients) throws EBookFormatException;
}
