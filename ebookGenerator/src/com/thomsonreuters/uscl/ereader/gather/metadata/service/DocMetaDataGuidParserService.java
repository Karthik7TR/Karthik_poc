/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.gather.metadata.service;

import java.io.File;

import com.thomsonreuters.uscl.ereader.gather.exception.EBookGatherException;

/**
 * Parses XML files and identifies the document guids.
 *
 * @author <a href="mailto:Nirupam.Chatterjee@thomsonreuters.com">Nirupam Chatterjee</a> u0072938
 */
public interface DocMetaDataGuidParserService 
{
	/**
	 * Reads through all the XML files found in the provided directory and parses out 
	 * a list of GUIDs for the referenced docs.
	 * @param tocFile the source "toc.xml" table of content file
	 * @param docGuidsFile a file of document guids, one per line used by gather service to fetch document content and metadata.
	 * @throws EBookGatherException if any fatal errors are encountered
	 */
	public void generateDocGuidList(final File tocFile, final File docsGuidsFile) throws EBookGatherException;
}
