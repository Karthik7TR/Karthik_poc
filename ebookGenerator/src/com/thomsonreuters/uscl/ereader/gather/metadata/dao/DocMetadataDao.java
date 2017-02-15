package com.thomsonreuters.uscl.ereader.gather.metadata.dao;

import java.util.List;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadata;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadataPK;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocumentMetadataAuthority;
import org.springframework.dao.DataAccessException;

/**
 * DAO to manage DocMetadata entities.
 *
 */
public interface DocMetadataDao
{
    /**
     * Query - findDocMetadataByPrimaryKey
     *
     */
    DocMetadata findDocMetadataByPrimaryKey(DocMetadataPK docMetaPk) throws DataAccessException;

    /**
     * Query - findDocMetadataByDocUuid
     *
     */
    Map<String, String> findDocMetadataMapByDocUuid(String docUuid) throws DataAccessException;

    Map<String, String> findDistinctFamilyGuidsByJobId(Long jobInstanceId) throws DataAccessException;

    void remove(DocMetadata toRemove) throws DataAccessException;

    void saveMetadata(DocMetadata metadata);

    void updateMetadata(DocMetadata metadata);

    /**
     * Retrieves all document metadata records for a given jobId.
     *
     * @param jobInstanceId the job for which to retrieve the document metadata
     * @return the {@link DocumentMetadataAuthority} representing the full set of {@link DocMetadata} for the job.
     */
    DocumentMetadataAuthority findAllDocMetadataForTitleByJobId(Long jobInstanceId);

    /**
     * Query - findDocMetadataMapByPartialCiteMatch
     *
     */
    DocMetadata findDocMetadataMapByPartialCiteMatchAndJobId(Long jobInstanceId, String cite)
        throws DataAccessException;

    List<String> findDistinctSplitTitlesByJobId(Long jobInstanceId) throws DataAccessException;
}
