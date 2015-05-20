/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.gather.metadata.service;

import java.io.File;
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
	public void saveDocMetadata(DocMetadata docmetadata);

	/**
	 * Update an existing DocMetadata entity
	 * 
	 */
	public void updateDocMetadata(DocMetadata docmetadata);
	
	/**
	 * Delete an existing DocMetadata entity
	 * 
	 */
	public void deleteDocMetadata(DocMetadata docmetadata_1);

	/**
	 */
	public DocMetadata findDocMetadataByPrimaryKey(String titleId,
			Long jobInstanceId, String docUuid);

	/**
	 */
	public void parseAndStoreDocMetadata(String titleId, Long jobInstanceId,
			String collectionName, File metadataFile) throws Exception;

	/**
	 * @return 
	 */
	public int updateProviewFamilyUUIDDedupFields(Long jobInstanceId) throws Exception;
	
	/**
	 */
	public void updateSplitBookFields(Long jobInstanceId, Map<String,DocumentInfo> documentInfoMap) throws Exception;
	
	/**
	 * Retrieves the full set of document metadata for a given title.
	 * 
	 * <p>This method will return an empty {@link Set} in cases where there is no {@link DocMetadata} for a given job instance.</p>
	 * 
	 * @param jobInstanceId the jobInstanceId of the publishing run.
	 * @return the {@link Set} of {@link DocMetadata} for the documents contained in the title.
	 */
	public DocumentMetadataAuthority findAllDocMetadataForTitleByJobId(final Long jobInstanceId);
	
	public Map<String, String> findDistinctProViewFamGuidsByJobId(Long jobInstanceId);
	
	public DocMetadata findDocMetadataMapByPartialCiteMatchAndJobId(Long jobInstanceId, String cite);	
}