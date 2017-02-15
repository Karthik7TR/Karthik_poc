package com.thomsonreuters.uscl.ereader.format.service;

import java.io.File;

import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;

/**
 * Updates all the anchor references to include proper document prefixes.
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public interface TitleMetadataAnchorUpdateService
{
    /**
     * Update all the anchor references to match the the format docFamGuid/anchorName.
     *
     * @param srcTitleXML the source title.xml file to be updated.
     * @param trgTitleXML location where the updated file should be generated to.
     * @param docToToc the file that contains mappings of DOC to TOC Guids
     */
    void updateAnchors(File srcTitleXML, File trgTitleXML, File docToToc) throws EBookFormatException;
}
