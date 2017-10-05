package com.thomsonreuters.uscl.ereader.gather.metadata.service;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.thomsonreuters.uscl.ereader.format.step.DocumentInfo;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadata;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocumentMetadataAuthority;

/**
 * Spring service that handles CRUD requests for DocMetadata entities
 *
 */
public interface DocMetadataService {
    /**
     * Save an existing DocMetadata entity
     *
     */
    void saveDocMetadata(DocMetadata docmetadata);

    /**
     * Update an existing DocMetadata entity
     *
     */
    void updateDocMetadata(DocMetadata docmetadata);

    /**
     * Delete an existing DocMetadata entity
     *
     */
    void deleteDocMetadata(DocMetadata docmetadata_1);

    /**
     */
    DocMetadata findDocMetadataByPrimaryKey(String titleId, Long jobInstanceId, String docUuid);

    /**
     */
    void parseAndStoreDocMetadata(String titleId, Long jobInstanceId, String collectionName, File metadataFile)
        throws Exception;

    /**
     * @return
     */
    int updateProviewFamilyUUIDDedupFields(Long jobInstanceId) throws Exception;

    /**
     */
    void updateSplitBookFields(Long jobInstanceId, Map<String, DocumentInfo> documentInfoMap) throws Exception;

    /**
     * Retrieves the full set of document metadata for a given title.
     *
     * <p>This method will return an empty {@link Set} in cases where there is no {@link DocMetadata} for a given job instance.</p>
     *
     * @param jobInstanceId the jobInstanceId of the publishing run.
     * @return the {@link Set} of {@link DocMetadata} for the documents contained in the title.
     */
    DocumentMetadataAuthority findAllDocMetadataForTitleByJobId(Long jobInstanceId);

    Map<String, String> findDistinctProViewFamGuidsByJobId(Long jobInstanceId);

    DocMetadata findDocMetadataMapByPartialCiteMatchAndJobId(Long jobInstanceId, String cite);

    List<String> findDistinctSplitTitlesByJobId(Long jobInstanceId);
}
