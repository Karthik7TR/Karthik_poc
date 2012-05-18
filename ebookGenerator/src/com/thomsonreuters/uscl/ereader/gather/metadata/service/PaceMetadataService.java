/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

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
	public void savePaceMetadata(PaceMetadata paceMetadata);

	/**
	 * Update an existing PaceMetadata entity
	 * 
	 */
	public void updatePaceMetadata(PaceMetadata paceMetadata);
	
	/**
	 * Delete an existing PaceMetadata entity
	 * 
	 */
	public void deletePaceMetadata(PaceMetadata paceMetadata);

	/**
	 */
	public PaceMetadata findPaceMetadataByPrimaryKey(Long pubId);

	
	/**
	 * Retrieves the full set of pace metadata for a given publication code.
	 * 
	 * <p>This method will return an empty {@link Set} in cases where there is no {@link PaceMetadata} for a given publication code instance.</p>
	 * 
	 * @param jobInstanceId the jobInstanceId of the publishing run.
	 * @return the {@link Set} of {@link PaceMetadata} for the documents contained in the title.
	 */
	public List<PaceMetadata> findAllPaceMetadataForPubCode(final Long pubCode);
	
	
}