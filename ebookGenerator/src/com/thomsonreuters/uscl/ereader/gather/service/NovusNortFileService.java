/*
* Copyright 2014: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/

package com.thomsonreuters.uscl.ereader.gather.service;

import java.io.File;
import java.util.Date;
import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.domain.ExcludeDocument;
import com.thomsonreuters.uscl.ereader.core.book.domain.RenameTocEntry;
import com.thomsonreuters.uscl.ereader.gather.codesworkbench.domain.RelationshipNode;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.exception.GatherException;

/**
 * Get nort toc from NORM and write results into toc.xml file.
 * @param domainName the domain in NORT 
 * @param expressionFilter named slice filter
 * @param nortXmlFile the files that contains the toc hierarchy and document guids.
 * @param cutoffDate the date from the ebook definition to be used to filter based on start/end dates in payload.
 * @param isFinalStage determines to retrieve content from Final or Review stage
 * @param useReloadContent determines to retrieve content from the Reload stage
 */
public interface NovusNortFileService {
	
	public GatherResponse findTableOfContents(List<RelationshipNode> rootNodes, File nortXmlFile, Date cutoffDate, 
			List<ExcludeDocument> excludeDocuments, List<RenameTocEntry> renameTocEntries) throws GatherException; 

}
