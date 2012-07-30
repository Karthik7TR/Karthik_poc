/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/

package com.thomsonreuters.uscl.ereader.gather.services;

import java.io.File;
import java.util.ArrayList;

import com.thomsonreuters.uscl.ereader.core.book.domain.ExcludeDocument;
import com.thomsonreuters.uscl.ereader.core.book.domain.RenameTocEntry;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.exception.GatherException;

public interface TocService {
	
	public GatherResponse findTableOfContents(String guid, String collectionName, File tocFile, ArrayList<ExcludeDocument> excludeDocuments, 
			ArrayList<RenameTocEntry> renameTocEntries, boolean isFinalStage) throws GatherException; 

}
