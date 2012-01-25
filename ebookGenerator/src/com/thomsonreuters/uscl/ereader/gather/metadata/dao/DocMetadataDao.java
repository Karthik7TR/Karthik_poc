/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.gather.metadata.dao;

import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadata;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadataPK;

import java.util.Map;

import org.springframework.dao.DataAccessException;

/**
 * DAO to manage DocMetadata entities.
 * 
 */
public interface DocMetadataDao {

	/**
	 * Query - findDocMetadataByPrimaryKey
	 * 
	 */
	public DocMetadata findDocMetadataByPrimaryKey(DocMetadataPK docMetaPk)
			throws DataAccessException;

	/**
	 * Query - findDocMetadataByDocUuid
	 * 
	 */
	public Map<String, String> findDocMetadataMapByDocUuid(String docUuid)
			throws DataAccessException;

	public void remove(DocMetadata toRemove) throws DataAccessException;

	public void saveMetadata(DocMetadata metadata);;

}