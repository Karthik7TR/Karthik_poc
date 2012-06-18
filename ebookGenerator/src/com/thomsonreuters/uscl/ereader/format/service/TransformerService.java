/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.format.service;

import java.io.File;

import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;

/**
 * The TransformerService iterates through a directory of XML files, retrieves the appropriate XSLT stylesheets, 
 * compiles them and produces intermediate HTML files that do not yet have all the proper HTML document wrappers 
 * and ProView mark up. 
 * 
 * @author u0095869
 */
public interface TransformerService 
{
	/**
     * Transforms all XML files found in the passed in XML directory and writes the
     * transformed HTML files to the specified target directory. If the directory does not exist
     * the service creates it.
     *
     * @param xmlDir the directory that contains all the Novus extracted XML files for this eBook.
     * @param metaDir the directory that contains all the Novus document metadata files for this eBook.
     * @param imgMetaDir the directory that contains all the ImageMetadata built files for this eBook.
     * @param transDir the target directory to which all the intermediate HTML files will be written out to.
     * @param titleID the identifier of book currently being published, used to lookup appropriate document metadata
     * @param jobID the identifier of the job currently running, used to lookup appropriate document metadata
     * @param includeAnnotations flag that is used to allow annotations to flow through into the documents of the book.
     *
     * @return number of documents that were transformed
     *
     * @throws EBookFormatException if an error occurs during the process.
	 */
	public int transformXMLDocuments(final File xmlDir, final File metaDir, final File imgMetaDir,
			final File transDir, final String titleID, final Long jobID, final boolean includeAnnotations) 
					throws EBookFormatException;
}
