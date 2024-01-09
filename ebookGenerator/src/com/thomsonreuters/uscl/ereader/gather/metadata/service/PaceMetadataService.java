package com.thomsonreuters.uscl.ereader.gather.metadata.service;

import java.util.List;
import java.util.Set;

import com.thomsonreuters.uscl.ereader.gather.metadata.domain.PaceMetadata;

/**
 * Spring service that handles CRUD requests for PaceMetadata entities
 * @author <a href="mailto:ravi.nandikolla@thomsonreuters.com">Ravi Nandikolla</a> c139353
 */
public interface PaceMetadataService {
    /**
     * Save an existing PaceMetadata entity
     *
     */
    void savePaceMetadata(PaceMetadata paceMetadata);

    /**
     * Update an existing PaceMetadata entity
     *
     */
    void updatePaceMetadata(PaceMetadata paceMetadata);

    /**
     * Delete an existing PaceMetadata entity
     *
     */
    void deletePaceMetadata(PaceMetadata paceMetadata);

    /**
     */
    PaceMetadata findPaceMetadataByPrimaryKey(Long pubId);

    /**
     * Retrieves the full set of pace metadata for a given publication code.
     *
     * <p>This method will return an empty {@link Set} in cases where there is no {@link PaceMetadata} for a given publication code instance.</p>
     *
     * @param jobInstanceId the jobInstanceId of the publishing run.
     * @return the {@link Set} of {@link PaceMetadata} for the documents contained in the title.
     */
    List<PaceMetadata> findAllPaceMetadataForPubCode(Long pubCode);
}
