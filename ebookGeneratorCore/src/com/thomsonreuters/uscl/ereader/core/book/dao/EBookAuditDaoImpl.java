/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.core.book.dao;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Transactional;

import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;

/**
 * DAO to manage EBookAudit entities.
 * 
 */

public class EBookAuditDaoImpl implements EbookAuditDao {

	private SessionFactory sessionFactory;

	public EBookAuditDaoImpl(SessionFactory hibernateSessionFactory) {
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
	public boolean canBeMerged(EbookAudit entity) {
		return true;
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
	public EbookAudit persist(EbookAudit toPersist) {
		sessionFactory.getCurrentSession().save(toPersist);
		flush();
		return toPersist;
	}

	/*
	 * (non-Javadoc)
	 */
	@Transactional
	public void remove(EbookAudit toRemove) {
		toRemove = (EbookAudit) sessionFactory.getCurrentSession().merge(
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

	@Override
	@Transactional
	public void saveAudit(EbookAudit audit) {
		Session session = sessionFactory.getCurrentSession();
		
		if (audit.getAuditId() != null) {
			audit = (EbookAudit) session.merge(audit);
		} else {
			session.save(audit);
		}
		
		session.flush();
	}

	@Override
	@Transactional
	public EbookAudit findEbookAuditByPrimaryKey(Long auditId)
			throws DataAccessException {
		Query query = createNamedQuery("findEbookAuditByPrimaryKey");
		query.setLong("auditId", auditId);
		return (EbookAudit)query.uniqueResult();
	}
	
	@Transactional
	public Long findEbookAuditIdByEbookDefId(Long ebookDefId)
	throws DataAccessException {
		Session session = sessionFactory.getCurrentSession();

		Long ebookAuditMax = (Long) session.createCriteria(EbookAudit.class)
		.setProjection( Projections.projectionList()
				.add(  Projections.max("auditId"))  )
		.add( Restrictions.eq("ebookDefinitionId", ebookDefId))
	    .uniqueResult();
		return(ebookAuditMax);
		
	}
}
