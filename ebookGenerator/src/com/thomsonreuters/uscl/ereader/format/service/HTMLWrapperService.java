/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.format.service;

import java.io.File;

import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;

/**
 * The HTMLWrapperService iterates through a directory of transformed raw HTML files and 
 * wraps the raw files with proper HTML header and container tags.
 * 
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public interface HTMLWrapperService 
{
	/**
     * Wraps all transformed files found in the passed in transformation directory and writes the
     * properly marked up HTML files to the specified target directory. If the directory does not exist
     * the service creates it.
     *
     * @param transDir the directory that contains all the intermediate generated HTML files generated
     * by the Transformer Service for this eBook.
     * @param htmlDir the target directory to which all the properly marked up HTML files will be written out to.
     * @param docToTocMapping location of the file that contains the document to TOC mappings that
     * will be used to generate anchors for the TOC references
     * @param titleId
     * @param jobId
     * @param keyciteToplineFlag
     * 
     * @return The number of documents that had wrappers added
     * 
     * @throws EBookFormatException if an error occurs during the process.
	 */
	public int addHTMLWrappers(final File transDir, final File htmlDir, final File docToTocMapping, String titleId, long jobId, boolean keyciteToplineFlag) 
			throws EBookFormatException;
}
