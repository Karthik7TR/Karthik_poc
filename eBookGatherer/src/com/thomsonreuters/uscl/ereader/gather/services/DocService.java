/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/

package com.thomsonreuters.uscl.ereader.gather.services;

import java.io.File;
import java.util.Collection;

import com.thomsonreuters.uscl.ereader.gather.exception.GatherException;

public interface DocService {
	
	/**
	 * Fetch documents from novus and create separate content and metadata files in separate directories.
	 * @param docGuid the primary key of the document
	 * @param collectionName
	 * @param contentDestinationDirectory the filesystem directory where the content files will be created.
	 * @param metadataDestinationDirectory the filesystem directory where the metadata files will be created.
	 * Two files are created per docGuid, a content file, and a meta-data file.
	 * @return a document object, or null if not found
	 */
	public void fetchDocuments(Collection<String> docGuids, String collectionName,
							   File contentDestinationDirectory,
							   File metadataDestinationDirectory) throws GatherException;
}
