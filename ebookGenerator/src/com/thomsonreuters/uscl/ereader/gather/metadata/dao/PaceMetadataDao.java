package com.thomsonreuters.uscl.ereader.gather.metadata.dao;

import java.util.List;

import com.thomsonreuters.uscl.ereader.gather.metadata.domain.PaceMetadata;
import org.springframework.dao.DataAccessException;

/**
 * @author <a href="mailto:ravi.nandikolla@thomsonreuters.com">Ravi Nandikolla</a> c139353
 *
 */
public interface PaceMetadataDao {
    /**
     * Query - findPaceMetadataByPrimaryKey
     */
    PaceMetadata findPaceMetadataByPrimaryKey(Long publicationId) throws DataAccessException;

    void remove(PaceMetadata toRemove) throws DataAccessException;

    void saveMetadata(PaceMetadata metadata);

    void updateMetadata(PaceMetadata metadata);

    /**
        * Query - findPaceMetadataByPubCode
        *
        */
    List<PaceMetadata> findPaceMetadataByPubCode(Long pubCode) throws DataAccessException;
}
