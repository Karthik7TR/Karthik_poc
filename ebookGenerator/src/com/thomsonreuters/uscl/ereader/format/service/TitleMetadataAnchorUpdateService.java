/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.format.service;

import java.io.File;

import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;

/**
 * Updates all the anchor references to include proper document prefixes.
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public interface TitleMetadataAnchorUpdateService {

	/**
	 * Update all the anchor references to match the the format docFamGuid/anchorName.
	 * 
	 * @param srcTitleXML the source title.xml file to be updated.
	 * @param trgTitleXML location where the updated file should be generated to.
	 * @param docToToc the file that contains mappings of DOC to TOC Guids
	 */
	public void updateAnchors(final File srcTitleXML, final File trgTitleXML, final File docToToc)
		throws EBookFormatException;
}
