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
    void generateDocGuidList(File tocFile, File docsGuidsFile) throws EBookGatherException;
}
