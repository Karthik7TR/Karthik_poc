/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.gather.metadata.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Transactional;

import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadata;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadataPK;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocumentMetadataAuthority;

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
	public void update(DocMetadata toUpdate) {
		sessionFactory.getCurrentSession().update(toUpdate);
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

	@SuppressWarnings("unchecked")
	public Map<String, String> findDistinctFamilyGuidsByJobId(Long jobInstanceId) {
		Session session = sessionFactory.getCurrentSession();
		
		List<Object[]> docMetaList = session.createCriteria(DocMetadata.class)
				.setProjection(Projections.distinct( (Projections.projectionList()
						.add(Projections.property("docUuid"))
						.add(Projections.property("docFamilyUuid")))))
						.add( Restrictions.eq("jobInstanceId", jobInstanceId))
						.list();
//		List<DocMetadata> docMetaList = session.createCriteria(DocMetadata.class)
//	    .add( Restrictions.eq("jobInstanceId", instanceJobId))
//	    .list();
		
		Map<String, String> docMap = new HashMap<String, String>();

		for(Object[] arr : docMetaList)
		{
			if (arr[1] != null) // Xena content has no docFamilyGuid
			{
				docMap.put(arr[0].toString(), arr[1].toString());
			}
		}
		return docMap;
	}	
	
	@SuppressWarnings("unchecked")
	public List<String> findDistinctSplitTitlesByJobId(Long jobInstanceId) {
		Session session = sessionFactory.getCurrentSession();
		
		List<String> docMetaList = session.createCriteria(DocMetadata.class)
				.setProjection(Projections.distinct( (Projections.projectionList()
						.add(Projections.property("splitBookTitleId")))))
						.add( Restrictions.eq("jobInstanceId", jobInstanceId))
						.addOrder(Order.asc("splitBookTitleId"))
						.list();
		
		List<String> splitTitleIdList = new ArrayList<String>();
		
		if(docMetaList.size() > 0){
			splitTitleIdList.addAll(docMetaList);
		}
		
		return splitTitleIdList;
	}
	
	@Override
	@Transactional
	public void saveMetadata(DocMetadata metadata) {
		Session session = sessionFactory.getCurrentSession();
		session.save(metadata);
		session.flush();
	}
	
	@Override
	@Transactional
	public void updateMetadata(DocMetadata metadata) {
		Session session = sessionFactory.getCurrentSession();
		session.update(metadata);
		session.flush();
	}

	@Override
	public DocumentMetadataAuthority findAllDocMetadataForTitleByJobId(final Long jobInstanceId) {
		Session session = sessionFactory.getCurrentSession();
		
		// Using LinkedHashSet to preserve insertion order based on what is returned from DB
		Set<DocMetadata> documentMetadataSet = new LinkedHashSet<DocMetadata>();
		
		@SuppressWarnings("unchecked")
		List<DocMetadata> docMetaList = session.createCriteria(DocMetadata.class)
	    .add( Restrictions.eq("jobInstanceId", jobInstanceId))
	    .addOrder(Order.asc("docUuid"))
	    .list();
		
		documentMetadataSet.addAll(docMetaList);
		DocumentMetadataAuthority documentMetadataAuthority = new DocumentMetadataAuthority(documentMetadataSet);
		return documentMetadataAuthority;
	}

	/**
	 * Query - findDocMetadataMapByPartialCiteMatchAndJobId
	 * 
	 * @param jobInstanceId
	 *            jobinstanceId from the run
	 * @param cite
	 *            from the document           
	 * @returns a documentMetadata
	 * 
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public DocMetadata findDocMetadataMapByPartialCiteMatchAndJobId(Long jobInstanceId, String cite)
			throws DataAccessException {
	
		Query query = createNamedQuery("findDocumentMetaDataByCiteAndJobId");
		query.setParameter("jobInstaneId", jobInstanceId);
		query.setParameter("normalizedCite", "%" + cite);		
	
		List<DocMetadata> docMetaDataList = query.list();
		
		if (docMetaDataList.size() > 0) {
			return (DocMetadata)docMetaDataList.get(0);
		} else {
			return null;
		}
	}
}
