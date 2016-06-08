/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.core.book.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Transactional;

import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAuditFilter;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAuditSort;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAuditSort.SortProperty;

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
	
	@Override
	@Transactional(readOnly = true)
	public Long findMaxAuditId(){
		Session session = sessionFactory.getCurrentSession();

		Long ebookAuditMax = (Long) session.createCriteria(EbookAudit.class)
		.setProjection( Projections.projectionList()
				.add(  Projections.max("auditId"))  )
	    .uniqueResult();
		return(ebookAuditMax);
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
	public void updateSpliDocumentsAudit(EbookAudit audit, String splitDocumentsConcat, int parts) {
		Session session = sessionFactory.getCurrentSession();
		
		if (audit.getAuditId() != null) {
			audit.setSplitDocumentsConcat(splitDocumentsConcat);
			audit.setSplitEBookParts(new Integer(parts));
			audit = (EbookAudit) session.merge(audit);
		} 
		
		session.flush();		
	}

	@Override
	@Transactional(readOnly=true)
	public EbookAudit findEbookAuditByPrimaryKey(Long auditId)
			throws DataAccessException {
		Query query = createNamedQuery("findEbookAuditByPrimaryKey");
		query.setLong("auditId", auditId);
		return (EbookAudit)query.uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly=true)
	public List<EbookAudit> findEbookAuditByTitleIdAndIsbn(String titleId, String isbn) {
		EbookAuditFilter filter = new EbookAuditFilter(titleId, null, isbn);
		Criteria criteria = addFilters(filter);
		
		return criteria.list();
	}
	
	@Transactional(readOnly=true)
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
	
	@Transactional(readOnly=true)
	public EbookAudit findEbookAuditIdByTtileId(String titleId)
	throws DataAccessException {
				
		StringBuffer hql = new StringBuffer(
				"select pa from EbookAudit pa where (pa.auditId) in ");
		hql.append("(select max(pa2.auditId) from EbookAudit pa2 where");
		hql.append(" pa2.titleId = :titleId)");
		
		// Create query and populate it with where clause values
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery(hql.toString());
		query.setParameter("titleId", titleId);

		EbookAudit audit = (EbookAudit) query.uniqueResult();
		if(audit == null){
			return null;
		}
		return audit;
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true)
	public List<EbookAudit> findEbookAudits(EbookAuditFilter filter, EbookAuditSort sort) {

		Criteria criteria = addFilters(filter);

		String orderByColumn = getOrderByColumnName(sort.getSortProperty());
		if(sort.isAscending()) {
			criteria.addOrder(Order.asc(orderByColumn));
		} else {
			criteria.addOrder(Order.desc(orderByColumn));
		}
		
		if(sort.getSortProperty() != EbookAuditSort.SortProperty.SUBMITTED_DATE) {
			criteria.addOrder(Order.desc(getOrderByColumnName(EbookAuditSort.SortProperty.SUBMITTED_DATE)));
		}
		
		int itemsPerPage = sort.getItemsPerPage();
		criteria.setFirstResult((sort.getPageNumber()-1)*(itemsPerPage));
		criteria.setMaxResults(itemsPerPage);
		
		return criteria.list();
	}
	
	
	@Override
	@Transactional(readOnly = true)
	public int numberEbookAudits(EbookAuditFilter filter) {
		Criteria criteria = addFilters(filter);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("auditId"), "auditId"));
		
		return criteria.list().size();
	}
	
	private Criteria addFilters(EbookAuditFilter filter) {
		Session session = sessionFactory.getCurrentSession();

		Criteria criteria = session.createCriteria(EbookAudit.class);

		if (filter.getFrom() != null) {
			criteria.add(Restrictions.ge("lastUpdated", filter.getFrom()));
		}
		if (filter.getTo() != null) {
			criteria.add(Restrictions.le("lastUpdated", filter.getTo()));
		}
		if (StringUtils.isNotBlank(filter.getAction())) {
			criteria.add(Restrictions.eq("auditType", filter.getAction()));
		}
		if (StringUtils.isNotBlank(filter.getBookName())) {
			criteria.add(Restrictions.like("proviewDisplayName", filter.getBookName()).ignoreCase());
		}
		if (StringUtils.isNotBlank(filter.getTitleId())) {
			criteria.add(Restrictions.like("titleId", filter.getTitleId()).ignoreCase());
		}
		if (StringUtils.isNotBlank(filter.getSubmittedBy())) {
			criteria.add(Restrictions.like("updatedBy", filter.getSubmittedBy()).ignoreCase());
		}
		if (filter.getBookDefinitionId() != null) {
			criteria.add(Restrictions.eq("ebookDefinitionId", filter.getBookDefinitionId()));
		}
		if (StringUtils.isNotBlank(filter.getIsbn())) {
			criteria.add(Restrictions.like("isbn", filter.getIsbn()).ignoreCase());
		}
		if(filter.getFilterEditedIsbn()) {
			criteria.add(Restrictions.not(Restrictions.like("isbn", EbookAuditDao.MOD_TEXT + "%")));
		}
		
		return criteria;
	}
	
	/**
	 * Map the sort column enumeration into the actual column identifier used in the HQL query.
	 * @param sortProperty enumerated value that reflects the database table sort column to sort on.
	 */
	private String getOrderByColumnName(SortProperty sortProperty) {
		switch (sortProperty) {
			case ACTION:
				return "auditType";
			case SUBMITTED_DATE:
				return "lastUpdated";
			case BOOK_NAME:
				return "proviewDisplayName";
			case TITLE_ID:
				return "titleId";
			case SUBMITTED_BY:
				return "updatedBy";
			case BOOK_DEFINITION_ID:
				return "ebookDefinitionId";
			default:
				throw new IllegalArgumentException("Unexpected sort property: " + sortProperty);
		}
	}
}
