/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/

package com.thomsonreuters.uscl.ereader.gather.services;

import java.io.File;
import java.util.List;

import com.thomsonreuters.uscl.ereader.gather.exception.GatherException;

public interface TocService {
	
	public void findTableOfContents(String guid, String collectionName, File tocFile) throws GatherException; 

}
