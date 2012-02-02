/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/

package com.thomsonreuters.uscl.ereader.gather.services;

import java.io.File;
import com.thomsonreuters.uscl.ereader.gather.exception.GatherException;

/**
 * Get nort toc from NORM and write results into toc.xml file.
 * @param domainName the domain in NORT 
 * @param expressionFilter named slice filter
 * @param nortXmlFile the files that contains the toc hierarchy and document guids.
 */
public interface NortService {
	
	public void findTableOfContents(String domainName, String expressionFilter, File nortXmlFile) throws GatherException; 

}
