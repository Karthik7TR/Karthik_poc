/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.gather.metadata.dao;

import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadata;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadataPK;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Transactional;

/**
 * DAO to manage DocMetadata entities.
 * 
 */

public class DocMetadataDaoImpl implements DocMetadataDao {

	private SessionFactory sessionFactory;

	public DocMetadataDaoImpl(SessionFactory hibernateSessionFactory) {
		this.sessionFactory = hibernateSessionFactory;
	}

	/**
	 * Used to determine whether or not to merge the entity or persist the
	 * entity when calling Store
	 * 
	 * @see store
	 * 
	 * 
	 */
	public boolean canBeMerged(DocMetadata entity) {
		return true;
	}

	/**
	 * Query - findDocMetadataMapByDocUuid
	 * 
	 * @param docUuid
	 *            from the document
	 * @returns a map of the document family guids associated with the document
	 *          uuid
	 * 
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public Map<String, String> findDocMetadataMapByDocUuid(String docUuid)
			throws DataAccessException {

		Map<String, String> mp = new HashMap<String, String>();

		Query query = createNamedQuery("findDocMetadataMapByDocUuid");
		query.setString("doc_uuid", docUuid);

		List<String> docFamilyGuidList = query.list();

		for (int i = 0; i < docFamilyGuidList.size(); i++) {
			mp.put(docFamilyGuidList.get(i), docUuid);
		}

		return mp;
	}

	/*
	 * (non-Javadoc)
	 */
	@Transactional
	public Query createNamedQuery(String queryName) {
		Query query = sessionFactory.getCurrentSession().getNamedQuery(
				queryName);
		return query;
	}

	/*
	 * (non-Javadoc)
	 */
	@Transactional
	public DocMetadata persist(DocMetadata toPersist) {
		sessionFactory.getCurrentSession().save(toPersist);
		flush();
		return toPersist;
	}

	/*
	 * (non-Javadoc)
	 */
	@Transactional
	public void remove(DocMetadata toRemove) {
		toRemove = (DocMetadata) sessionFactory.getCurrentSession().merge(
				toRemove);
		sessionFactory.getCurrentSession().delete(toRemove);
		flush();
	}

	/*
	 * (non-Javadoc)
	 */
	@Transactional
	public void flush() {
		sessionFactory.getCurrentSession().flush();
	}

	public DocMetadata findDocMetadataByPrimaryKey(DocMetadataPK pk) {
		Session session = sessionFactory.getCurrentSession();
		return (DocMetadata) session.get(DocMetadata.class, pk);
	}

	@Override
	@Transactional
	public void saveMetadata(DocMetadata metadata) {
		Session session = sessionFactory.getCurrentSession();
		session.save(metadata);
		session.flush();
	}
}
