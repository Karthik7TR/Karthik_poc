/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.gather.metadata.dao;

import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadata;

import java.util.Map;
import java.util.Set;

import org.springframework.dao.DataAccessException;

/**
 * DAO to manage DocMetadata entities.
 * 
 */
public interface DocMetadataDao  extends JpaDao<DocMetadata>{

	/**
	 * JPQL Query - findDocMetadataByDocUuid
	 *
	 */
	public Set<DocMetadata> findDocMetadataByDocUuid(String docUuid) throws DataAccessException;

	/**
	 * JPQL Query - findDocMetadataByDocUuid
	 *
	 */
	public Set<DocMetadata> findDocMetadataByDocUuid(String docUuid, int startResult, int maxRows) throws DataAccessException;

	/**
	 * JPQL Query - findDocMetadataByJobInstanceId
	 *
	 */
	public Set<DocMetadata> findDocMetadataByJobInstanceId(Integer jobInstanceId) throws DataAccessException;

	/**
	 * JPQL Query - findDocMetadataByJobInstanceId
	 *
	 */
	public Set<DocMetadata> findDocMetadataByJobInstanceId(Integer jobInstanceId, int startResult, int maxRows) throws DataAccessException;

	/**
	 * JPQL Query - findDocMetadataByTitleIdContaining
	 *
	 */
	public Set<DocMetadata> findDocMetadataByTitleIdContaining(String titleId) throws DataAccessException;

	/**
	 * JPQL Query - findDocMetadataByTitleIdContaining
	 *
	 */
	public Set<DocMetadata> findDocMetadataByTitleIdContaining(String titleId, int startResult, int maxRows) throws DataAccessException;

	/**
	 * JPQL Query - findDocMetadataByNormalizedFirstlineCiteContaining
	 *
	 */
	public Set<DocMetadata> findDocMetadataByNormalizedFirstlineCiteContaining(String normalizedFirstlineCite) throws DataAccessException;

	/**
	 * JPQL Query - findDocMetadataByNormalizedFirstlineCiteContaining
	 *
	 */
	public Set<DocMetadata> findDocMetadataByNormalizedFirstlineCiteContaining(String normalizedFirstlineCite, int startResult, int maxRows) throws DataAccessException;

	/**
	 * JPQL Query - findDocMetadataByFindOrigContaining
	 *
	 */
	public Set<DocMetadata> findDocMetadataByFindOrigContaining(String findOrig) throws DataAccessException;

	/**
	 * JPQL Query - findDocMetadataByFindOrigContaining
	 *
	 */
	public Set<DocMetadata> findDocMetadataByFindOrigContaining(String findOrig, int startResult, int maxRows) throws DataAccessException;

	/**
	 * JPQL Query - findDocMetadataByDocTypeContaining
	 *
	 */
	public Set<DocMetadata> findDocMetadataByDocTypeContaining(String docType) throws DataAccessException;

	/**
	 * JPQL Query - findDocMetadataByDocTypeContaining
	 *
	 */
	public Set<DocMetadata> findDocMetadataByDocTypeContaining(String docType, int startResult, int maxRows) throws DataAccessException;

	/**
	 * JPQL Query - findDocMetadataBySerialNumber
	 *
	 */
	public Set<DocMetadata> findDocMetadataBySerialNumber(Integer serialNumber) throws DataAccessException;

	/**
	 * JPQL Query - findDocMetadataBySerialNumber
	 *
	 */
	public Set<DocMetadata> findDocMetadataBySerialNumber(Integer serialNumber, int startResult, int maxRows) throws DataAccessException;

	/**
	 * JPQL Query - findDocMetadataByTitleId
	 *
	 */
	public Set<DocMetadata> findDocMetadataByTitleId(String titleId_1) throws DataAccessException;

	/**
	 * JPQL Query - findDocMetadataByTitleId
	 *
	 */
	public Set<DocMetadata> findDocMetadataByTitleId(String titleId_1, int startResult, int maxRows) throws DataAccessException;

	/**
	 * JPQL Query - findDocMetadataByNormalizedFirstlineCite
	 *
	 */
	public Set<DocMetadata> findDocMetadataByNormalizedFirstlineCite(String normalizedFirstlineCite_1) throws DataAccessException;

	/**
	 * JPQL Query - findDocMetadataByNormalizedFirstlineCite
	 *
	 */
	public Set<DocMetadata> findDocMetadataByNormalizedFirstlineCite(String normalizedFirstlineCite_1, int startResult, int maxRows) throws DataAccessException;

	/**
	 * JPQL Query - findDocMetadataByFindOrig
	 *
	 */
	public Set<DocMetadata> findDocMetadataByFindOrig(String findOrig_1) throws DataAccessException;

	/**
	 * JPQL Query - findDocMetadataByFindOrig
	 *
	 */
	public Set<DocMetadata> findDocMetadataByFindOrig(String findOrig_1, int startResult, int maxRows) throws DataAccessException;

	/**
	 * JPQL Query - findDocMetadataByPrimaryKey
	 *
	 */
	public DocMetadata findDocMetadataByPrimaryKey(String titleId_2, Integer jobInstanceId_1, String docUuid_1) throws DataAccessException;

	/**
	 * JPQL Query - findDocMetadataByPrimaryKey
	 *
	 */
	public DocMetadata findDocMetadataByPrimaryKey(String titleId_2, Integer jobInstanceId_1, String docUuid_1, int startResult, int maxRows) throws DataAccessException;

	/**
	 * JPQL Query - findDocMetadataByDocFamilyUuidContaining
	 *
	 */
	public Set<DocMetadata> findDocMetadataByDocFamilyUuidContaining(String docFamilyUuid) throws DataAccessException;

	/**
	 * JPQL Query - findDocMetadataByDocFamilyUuidContaining
	 *
	 */
	public Set<DocMetadata> findDocMetadataByDocFamilyUuidContaining(String docFamilyUuid, int startResult, int maxRows) throws DataAccessException;

	/**
	 * JPQL Query - findDocMetadataByDocFamilyUuid
	 *
	 */
	public Set<DocMetadata> findDocMetadataByDocFamilyUuid(String docFamilyUuid_1) throws DataAccessException;

	/**
	 * JPQL Query - findDocMetadataByDocFamilyUuid
	 *
	 */
	public Set<DocMetadata> findDocMetadataByDocFamilyUuid(String docFamilyUuid_1, int startResult, int maxRows) throws DataAccessException;

	/**
	 * JPQL Query - findDocMetadataByDocUuidContaining
	 *
	 */
	public Set<DocMetadata> findDocMetadataByDocUuidContaining(String docUuid_2) throws DataAccessException;

	/**
	 * JPQL Query - findDocMetadataByDocUuidContaining
	 *
	 */
	public Set<DocMetadata> findDocMetadataByDocUuidContaining(String docUuid_2, int startResult, int maxRows) throws DataAccessException;

	/**
	 * JPQL Query - findDocMetadataByCollectionNameContaining
	 *
	 */
	public Set<DocMetadata> findDocMetadataByCollectionNameContaining(String collectionName) throws DataAccessException;

	/**
	 * JPQL Query - findDocMetadataByCollectionNameContaining
	 *
	 */
	public Set<DocMetadata> findDocMetadataByCollectionNameContaining(String collectionName, int startResult, int maxRows) throws DataAccessException;

	/**
	 * JPQL Query - findAllDocMetadatas
	 *
	 */
	public Set<DocMetadata> findAllDocMetadatas() throws DataAccessException;

	/**
	 * JPQL Query - findAllDocMetadatas
	 *
	 */
	public Set<DocMetadata> findAllDocMetadatas(int startResult, int maxRows) throws DataAccessException;

	/**
	 * JPQL Query - findDocMetadataByCollectionName
	 *
	 */
	public Set<DocMetadata> findDocMetadataByCollectionName(String collectionName_1) throws DataAccessException;

	/**
	 * JPQL Query - findDocMetadataByCollectionName
	 *
	 */
	public Set<DocMetadata> findDocMetadataByCollectionName(String collectionName_1, int startResult, int maxRows) throws DataAccessException;

	/**
	 * JPQL Query - findDocMetadataByDocType
	 *
	 */
	public Set<DocMetadata> findDocMetadataByDocType(String docType_1) throws DataAccessException;

	/**
	 * JPQL Query - findDocMetadataByDocType
	 *
	 */
	public Set<DocMetadata> findDocMetadataByDocType(String docType_1, int startResult, int maxRows) throws DataAccessException;

	/**
	 * JPQL Query - findDocMetadataByDocUuid
	 *
	 */
	public Map<String, String> findDocMetadataMapByDocUuid(String docUuid) throws DataAccessException;

}