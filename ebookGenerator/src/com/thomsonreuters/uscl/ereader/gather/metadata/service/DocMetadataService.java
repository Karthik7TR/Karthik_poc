/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.gather.metadata.service;

import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadata;

import java.io.File;
import java.util.List;
import java.util.Map;

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
	 * Delete an existing DocMetadata entity
	 * 
	 */
	public void deleteDocMetadata(DocMetadata docmetadata_1);

	/**
	 */
	public DocMetadata findDocMetadataByPrimaryKey(String titleId,
			Integer jobInstanceId, String docUuid);
	/**
	 */
	public Map<String, String> findDistinctFamilyGuidsByJobId(Integer jobInstanceId);

	/**
	 */
	public void parseAndStoreDocMetadata(String titleId, Integer jobInstanceId,
			String collectionName, File metadataFile, String tocSeqNum);
}