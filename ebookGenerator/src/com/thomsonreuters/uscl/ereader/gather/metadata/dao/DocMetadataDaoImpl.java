/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.gather.metadata.dao;

import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadata;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.dao.DataAccessException;

import org.springframework.stereotype.Repository;

import org.springframework.transaction.annotation.Transactional;

/**
 * DAO to manage DocMetadata entities.
 * 
 */
@Repository("DocMetadataDAO")
@Transactional
public class DocMetadataDaoImpl extends AbstractJpaDao<DocMetadata> implements
		DocMetadataDao {

	/**
	 * Set of entity classes managed by this DAO.  Typically a DAO manages a single entity.
	 *
	 */
	private final static Set<Class<?>> dataTypes = new HashSet<Class<?>>(Arrays.asList(new Class<?>[] { DocMetadata.class }));

	/**
	 * EntityManager injected by Spring for persistence unit eBookBuilderDev
	 *
	 */
	@PersistenceContext(unitName = "DocMetaData")
	private EntityManager entityManager;

	/**
	 * Instantiates a new DocMetadataDAOImpl
	 *
	 */
	public DocMetadataDaoImpl() {
		super();
	}

	/**
	 * Get the entity manager that manages persistence unit 
	 *
	 */
	public EntityManager getEntityManager() {
		return entityManager;
	}

	/**
	 * Returns the set of entity classes managed by this DAO.
	 *
	 */
	public Set<Class<?>> getTypes() {
		return dataTypes;
	}

	/**
	 * JPQL Query - findDocMetadataByDocUuid
	 *
	 */
	@Transactional
	public Set<DocMetadata> findDocMetadataByDocUuid(String docUuid) throws DataAccessException {

		return findDocMetadataByDocUuid(docUuid, -1, -1);
	}

	/**
	 * JPQL Query - findDocMetadataByDocUuid
	 *
	 */

	@SuppressWarnings("unchecked")
	@Transactional
	public Set<DocMetadata> findDocMetadataByDocUuid(String docUuid, int startResult, int maxRows) throws DataAccessException {
		Query query = createNamedQuery("findDocMetadataByDocUuid", startResult, maxRows, docUuid);
		return new LinkedHashSet<DocMetadata>(query.getResultList());
	}

	/**
	 * JPQL Query - findDocMetadataByJobInstanceId
	 *
	 */
	@Transactional
	public Set<DocMetadata> findDocMetadataByJobInstanceId(Integer jobInstanceId) throws DataAccessException {

		return findDocMetadataByJobInstanceId(jobInstanceId, -1, -1);
	}

	/**
	 * JPQL Query - findDocMetadataByJobInstanceId
	 *
	 */

	@SuppressWarnings("unchecked")
	@Transactional
	public Set<DocMetadata> findDocMetadataByJobInstanceId(Integer jobInstanceId, int startResult, int maxRows) throws DataAccessException {
		Query query = createNamedQuery("findDocMetadataByJobInstanceId", startResult, maxRows, jobInstanceId);
		return new LinkedHashSet<DocMetadata>(query.getResultList());
	}

	/**
	 * JPQL Query - findDocMetadataByTitleIdContaining
	 *
	 */
	@Transactional
	public Set<DocMetadata> findDocMetadataByTitleIdContaining(String titleId) throws DataAccessException {

		return findDocMetadataByTitleIdContaining(titleId, -1, -1);
	}

	/**
	 * JPQL Query - findDocMetadataByTitleIdContaining
	 *
	 */

	@SuppressWarnings("unchecked")
	@Transactional
	public Set<DocMetadata> findDocMetadataByTitleIdContaining(String titleId, int startResult, int maxRows) throws DataAccessException {
		Query query = createNamedQuery("findDocMetadataByTitleIdContaining", startResult, maxRows, titleId);
		return new LinkedHashSet<DocMetadata>(query.getResultList());
	}

	/**
	 * JPQL Query - findDocMetadataByNormalizedFirstlineCiteContaining
	 *
	 */
	@Transactional
	public Set<DocMetadata> findDocMetadataByNormalizedFirstlineCiteContaining(String normalizedFirstlineCite) throws DataAccessException {

		return findDocMetadataByNormalizedFirstlineCiteContaining(normalizedFirstlineCite, -1, -1);
	}

	/**
	 * JPQL Query - findDocMetadataByNormalizedFirstlineCiteContaining
	 *
	 */

	@SuppressWarnings("unchecked")
	@Transactional
	public Set<DocMetadata> findDocMetadataByNormalizedFirstlineCiteContaining(String normalizedFirstlineCite, int startResult, int maxRows) throws DataAccessException {
		Query query = createNamedQuery("findDocMetadataByNormalizedFirstlineCiteContaining", startResult, maxRows, normalizedFirstlineCite);
		return new LinkedHashSet<DocMetadata>(query.getResultList());
	}

	/**
	 * JPQL Query - findDocMetadataByFindOrigContaining
	 *
	 */
	@Transactional
	public Set<DocMetadata> findDocMetadataByFindOrigContaining(String findOrig) throws DataAccessException {

		return findDocMetadataByFindOrigContaining(findOrig, -1, -1);
	}

	/**
	 * JPQL Query - findDocMetadataByFindOrigContaining
	 *
	 */

	@SuppressWarnings("unchecked")
	@Transactional
	public Set<DocMetadata> findDocMetadataByFindOrigContaining(String findOrig, int startResult, int maxRows) throws DataAccessException {
		Query query = createNamedQuery("findDocMetadataByFindOrigContaining", startResult, maxRows, findOrig);
		return new LinkedHashSet<DocMetadata>(query.getResultList());
	}

	/**
	 * JPQL Query - findDocMetadataByDocTypeContaining
	 *
	 */
	@Transactional
	public Set<DocMetadata> findDocMetadataByDocTypeContaining(String docType) throws DataAccessException {

		return findDocMetadataByDocTypeContaining(docType, -1, -1);
	}

	/**
	 * JPQL Query - findDocMetadataByDocTypeContaining
	 *
	 */

	@SuppressWarnings("unchecked")
	@Transactional
	public Set<DocMetadata> findDocMetadataByDocTypeContaining(String docType, int startResult, int maxRows) throws DataAccessException {
		Query query = createNamedQuery("findDocMetadataByDocTypeContaining", startResult, maxRows, docType);
		return new LinkedHashSet<DocMetadata>(query.getResultList());
	}

	/**
	 * JPQL Query - findDocMetadataBySerialNumber
	 *
	 */
	@Transactional
	public Set<DocMetadata> findDocMetadataBySerialNumber(Integer serialNumber) throws DataAccessException {

		return findDocMetadataBySerialNumber(serialNumber, -1, -1);
	}

	/**
	 * JPQL Query - findDocMetadataBySerialNumber
	 *
	 */

	@SuppressWarnings("unchecked")
	@Transactional
	public Set<DocMetadata> findDocMetadataBySerialNumber(Integer serialNumber, int startResult, int maxRows) throws DataAccessException {
		Query query = createNamedQuery("findDocMetadataBySerialNumber", startResult, maxRows, serialNumber);
		return new LinkedHashSet<DocMetadata>(query.getResultList());
	}

	/**
	 * JPQL Query - findDocMetadataByTitleId
	 *
	 */
	@Transactional
	public Set<DocMetadata> findDocMetadataByTitleId(String titleId) throws DataAccessException {

		return findDocMetadataByTitleId(titleId, -1, -1);
	}

	/**
	 * JPQL Query - findDocMetadataByTitleId
	 *
	 */

	@SuppressWarnings("unchecked")
	@Transactional
	public Set<DocMetadata> findDocMetadataByTitleId(String titleId, int startResult, int maxRows) throws DataAccessException {
		Query query = createNamedQuery("findDocMetadataByTitleId", startResult, maxRows, titleId);
		return new LinkedHashSet<DocMetadata>(query.getResultList());
	}

	/**
	 * JPQL Query - findDocMetadataByNormalizedFirstlineCite
	 *
	 */
	@Transactional
	public Set<DocMetadata> findDocMetadataByNormalizedFirstlineCite(String normalizedFirstlineCite) throws DataAccessException {

		return findDocMetadataByNormalizedFirstlineCite(normalizedFirstlineCite, -1, -1);
	}

	/**
	 * JPQL Query - findDocMetadataByNormalizedFirstlineCite
	 *
	 */

	@SuppressWarnings("unchecked")
	@Transactional
	public Set<DocMetadata> findDocMetadataByNormalizedFirstlineCite(String normalizedFirstlineCite, int startResult, int maxRows) throws DataAccessException {
		Query query = createNamedQuery("findDocMetadataByNormalizedFirstlineCite", startResult, maxRows, normalizedFirstlineCite);
		return new LinkedHashSet<DocMetadata>(query.getResultList());
	}

	/**
	 * JPQL Query - findDocMetadataByFindOrig
	 *
	 */
	@Transactional
	public Set<DocMetadata> findDocMetadataByFindOrig(String findOrig) throws DataAccessException {

		return findDocMetadataByFindOrig(findOrig, -1, -1);
	}

	/**
	 * JPQL Query - findDocMetadataByFindOrig
	 *
	 */

	@SuppressWarnings("unchecked")
	@Transactional
	public Set<DocMetadata> findDocMetadataByFindOrig(String findOrig, int startResult, int maxRows) throws DataAccessException {
		Query query = createNamedQuery("findDocMetadataByFindOrig", startResult, maxRows, findOrig);
		return new LinkedHashSet<DocMetadata>(query.getResultList());
	}

	/**
	 * JPQL Query - findDocMetadataByPrimaryKey
	 *
	 */
	@Transactional
	public DocMetadata findDocMetadataByPrimaryKey(String titleId, Integer jobInstanceId, String docUuid) throws DataAccessException {

		return findDocMetadataByPrimaryKey(titleId, jobInstanceId, docUuid, -1, -1);
	}

	/**
	 * JPQL Query - findDocMetadataByPrimaryKey
	 *
	 */

	@Transactional
	public DocMetadata findDocMetadataByPrimaryKey(String titleId, Integer jobInstanceId, String docUuid, int startResult, int maxRows) throws DataAccessException {
		try {
			Query query = createNamedQuery("findDocMetadataByPrimaryKey", startResult, maxRows, titleId, jobInstanceId, docUuid);
			return (com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadata) query.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}
	}

	/**
	 * JPQL Query - findDocMetadataByDocFamilyUuidContaining
	 *
	 */
	@Transactional
	public Set<DocMetadata> findDocMetadataByDocFamilyUuidContaining(String docFamilyUuid) throws DataAccessException {

		return findDocMetadataByDocFamilyUuidContaining(docFamilyUuid, -1, -1);
	}

	/**
	 * JPQL Query - findDocMetadataByDocFamilyUuidContaining
	 *
	 */

	@SuppressWarnings("unchecked")
	@Transactional
	public Set<DocMetadata> findDocMetadataByDocFamilyUuidContaining(String docFamilyUuid, int startResult, int maxRows) throws DataAccessException {
		Query query = createNamedQuery("findDocMetadataByDocFamilyUuidContaining", startResult, maxRows, docFamilyUuid);
		return new LinkedHashSet<DocMetadata>(query.getResultList());
	}

	/**
	 * JPQL Query - findDocMetadataByDocFamilyUuid
	 *
	 */
	@Transactional
	public Set<DocMetadata> findDocMetadataByDocFamilyUuid(String docFamilyUuid) throws DataAccessException {

		return findDocMetadataByDocFamilyUuid(docFamilyUuid, -1, -1);
	}

	/**
	 * JPQL Query - findDocMetadataByDocFamilyUuid
	 *
	 */

	@SuppressWarnings("unchecked")
	@Transactional
	public Set<DocMetadata> findDocMetadataByDocFamilyUuid(String docFamilyUuid, int startResult, int maxRows) throws DataAccessException {
		Query query = createNamedQuery("findDocMetadataByDocFamilyUuid", startResult, maxRows, docFamilyUuid);
		return new LinkedHashSet<DocMetadata>(query.getResultList());
	}

	/**
	 * JPQL Query - findDocMetadataByDocUuidContaining
	 *
	 */
	@Transactional
	public Set<DocMetadata> findDocMetadataByDocUuidContaining(String docUuid) throws DataAccessException {

		return findDocMetadataByDocUuidContaining(docUuid, -1, -1);
	}

	/**
	 * JPQL Query - findDocMetadataByDocUuidContaining
	 *
	 */

	@SuppressWarnings("unchecked")
	@Transactional
	public Set<DocMetadata> findDocMetadataByDocUuidContaining(String docUuid, int startResult, int maxRows) throws DataAccessException {
		Query query = createNamedQuery("findDocMetadataByDocUuidContaining", startResult, maxRows, docUuid);
		return new LinkedHashSet<DocMetadata>(query.getResultList());
	}

	/**
	 * JPQL Query - findDocMetadataByCollectionNameContaining
	 *
	 */
	@Transactional
	public Set<DocMetadata> findDocMetadataByCollectionNameContaining(String collectionName) throws DataAccessException {

		return findDocMetadataByCollectionNameContaining(collectionName, -1, -1);
	}

	/**
	 * JPQL Query - findDocMetadataByCollectionNameContaining
	 *
	 */

	@SuppressWarnings("unchecked")
	@Transactional
	public Set<DocMetadata> findDocMetadataByCollectionNameContaining(String collectionName, int startResult, int maxRows) throws DataAccessException {
		Query query = createNamedQuery("findDocMetadataByCollectionNameContaining", startResult, maxRows, collectionName);
		return new LinkedHashSet<DocMetadata>(query.getResultList());
	}

	/**
	 * JPQL Query - findAllDocMetadatas
	 *
	 */
	@Transactional
	public Set<DocMetadata> findAllDocMetadatas() throws DataAccessException {

		return findAllDocMetadatas(-1, -1);
	}

	/**
	 * JPQL Query - findAllDocMetadatas
	 *
	 */

	@SuppressWarnings("unchecked")
	@Transactional
	public Set<DocMetadata> findAllDocMetadatas(int startResult, int maxRows) throws DataAccessException {
		Query query = createNamedQuery("findAllDocMetadatas", startResult, maxRows);
		return new LinkedHashSet<DocMetadata>(query.getResultList());
	}

	/**
	 * JPQL Query - findDocMetadataByCollectionName
	 *
	 */
	@Transactional
	public Set<DocMetadata> findDocMetadataByCollectionName(String collectionName) throws DataAccessException {

		return findDocMetadataByCollectionName(collectionName, -1, -1);
	}

	/**
	 * JPQL Query - findDocMetadataByCollectionName
	 *
	 */

	@SuppressWarnings("unchecked")
	@Transactional
	public Set<DocMetadata> findDocMetadataByCollectionName(String collectionName, int startResult, int maxRows) throws DataAccessException {
		Query query = createNamedQuery("findDocMetadataByCollectionName", startResult, maxRows, collectionName);
		return new LinkedHashSet<DocMetadata>(query.getResultList());
	}

	/**
	 * JPQL Query - findDocMetadataByDocType
	 *
	 */
	@Transactional
	public Set<DocMetadata> findDocMetadataByDocType(String docType) throws DataAccessException {

		return findDocMetadataByDocType(docType, -1, -1);
	}

	/**
	 * JPQL Query - findDocMetadataByDocType
	 *
	 */

	@SuppressWarnings("unchecked")
	@Transactional
	public Set<DocMetadata> findDocMetadataByDocType(String docType, int startResult, int maxRows) throws DataAccessException {
		Query query = createNamedQuery("findDocMetadataByDocType", startResult, maxRows, docType);
		return new LinkedHashSet<DocMetadata>(query.getResultList());
	}

	/**
	 * Used to determine whether or not to merge the entity or persist the entity when calling Store
	 * @see store
	 * 
	 *
	 */
	public boolean canBeMerged(DocMetadata entity) {
		return true;
	}

	/**
	 * JPQL Query - findDocMetadataByDocUuid
	 *
	 */
	@Transactional
	public Map<String, String> findDocMetadataMapByDocUuid(String docUuid) throws DataAccessException {
		
		 Map<String, String> mp=new HashMap<String, String>();
	
		Query query = createNamedQuery("findDocMetadataMapByDocUuid", -1, -1, docUuid);
		
		List<String> docFamilyGuidList = query.getResultList();
		
		
		for (int i=0; i < docFamilyGuidList.size(); i++) {
			mp.put(docFamilyGuidList.get(i), docUuid);
		}
		   
		return mp;
	}
}
