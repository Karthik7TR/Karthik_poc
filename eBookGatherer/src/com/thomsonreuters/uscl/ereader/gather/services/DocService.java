package com.thomsonreuters.uscl.ereader.gather.services;

import java.io.File;
import java.util.Collection;

import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.exception.GatherException;

public interface DocService {
    /**
     * Fetch documents from novus and create separate content and metadata files in separate directories.
     * @param docGuid the primary key of the document
     * @param collectionName can be null for NORT
     * @param contentDestinationDirectory the filesystem directory where the content files will be created.
     * @param metadataDestinationDirectory the filesystem directory where the metadata files will be created.
     * Two files are created per docGuid, a content file, and a meta-data file.
     * @param isFinalStage determines to retrieve content from Final or Review stage
     * @param useReloadContent determines to retrieve content from the Reload stage
     * @return a document object, or null if not found
     */
    GatherResponse fetchDocuments(
        Collection<String> docGuids,
        String collectionName,
        File contentDestinationDirectory,
        File metadataDestinationDirectory,
        boolean isFinalStage,
        boolean useReloadContent) throws GatherException;
}
