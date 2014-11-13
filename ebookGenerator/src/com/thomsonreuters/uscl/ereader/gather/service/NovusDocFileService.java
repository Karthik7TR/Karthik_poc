/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/

package com.thomsonreuters.uscl.ereader.gather.service;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.domain.NortFileLocation;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.exception.GatherException;

public interface NovusDocFileService {
	
	/**
	 * Fetch documents from novus and create separate content and metadata files in separate directories.
	 * @param docGuidsMap the primary key of the document
	 * @param contentDestinationDirectory the filesystem directory where the content files will be created.
	 * @param metadataDestinationDirectory the filesystem directory where the metadata files will be created.
	 * Two files are created per docGuid, a content file, and a meta-data file.
	 * @return a document object, or null if not found
	 */
	public GatherResponse fetchDocuments(HashMap<String, Integer> docGuidsMap, File rootCodesWorkbenchLandingStrip, 
			String cwbBookName,  List<NortFileLocation> fileLocations, File contentDestinationDirectory, 
			File metadataDestinationDirectory) 
					throws GatherException;	
}
