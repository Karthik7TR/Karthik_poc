/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.gather.metadata.service;

import java.io.File;
import java.util.List;

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
	 *  a list of GUIDs for the referenced docs.
	 * 
	 * @param xmlDir directory that contains the XML files to be parsed
	 * 
	 * @return number of documents processed to generate lists
	 * @throws EBookGatherException if any fatal errors are encountered
	 */
	public void generateDocGuidList(final File xmlDir, final File docsGuidsDir) throws EBookGatherException;
}
