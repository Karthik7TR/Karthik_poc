/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.gather.metadata.service;

import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadata;

import java.util.List;
import java.util.Set;

/**
 * Spring service that handles CRUD requests for DocMetadata entities
 * 
 */
public interface DocMetadataService {

	/**
	 * Return all DocMetadata entity
	 * 
	 */
	public List<DocMetadata> findAllDocMetadatas(Integer startResult, Integer maxRows);

	/**
	 * Save an existing DocMetadata entity
	 * 
	 */
	public void saveDocMetadata(DocMetadata docmetadata);

	/**
	 * Load an existing DocMetadata entity
	 * 
	 */
	public Set<DocMetadata> loadDocMetadatas();

	/**
	 * Return a count of all DocMetadata entity
	 * 
	 */
	public Integer countDocMetadatas();

	/**
	 * Delete an existing DocMetadata entity
	 * 
	 */
	public void deleteDocMetadata(DocMetadata docmetadata_1);

	/**
	 */
	public DocMetadata findDocMetadataByPrimaryKey(String titleId, Integer jobInstanceId, String docUuid);

	/**
	 */
	public void parseAndStoreDocMetadata(String titleId, Integer jobInstanceId, String docUuid);
}