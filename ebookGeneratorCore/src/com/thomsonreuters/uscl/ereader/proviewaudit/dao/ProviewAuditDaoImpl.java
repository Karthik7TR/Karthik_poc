/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.proviewaudit.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.thomsonreuters.uscl.ereader.proviewaudit.domain.ProviewAudit;
import com.thomsonreuters.uscl.ereader.proviewaudit.domain.ProviewAuditFilter;
import com.thomsonreuters.uscl.ereader.proviewaudit.domain.ProviewAuditSort;
import com.thomsonreuters.uscl.ereader.proviewaudit.domain.ProviewAuditSort.SortProperty;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;

/**
 * DAO to manage ProviewAudit entities.
 * 
 */

public class ProviewAuditDaoImpl implements ProviewAuditDao {

	private SessionFactory sessionFactory;

	public ProviewAuditDaoImpl(SessionFactory hibernateSessionFactory) {
		this.sessionFactory = hibernateSessionFactory;
	}

	@Override
	public String getBookStatus(String titleId, String version){
		StringBuffer hql = new StringBuffer(
				"select pa from ProviewAudit pa where (pa.titleId,pa.bookVersion,pa.requestDate) in ");
		hql.append("(select pa2.titleId,pa2.bookVersion,max(pa2.requestDate) from ProviewAudit pa2 where");
		hql.append(" pa2.titleId = :titleId");
		hql.append(" and pa2.bookVersion =  :version"); 
		hql.append(" and pa2.proviewRequest not in ('PROMOTE','REVIEW') group by pa2.titleId,pa2.bookVersion)");
		// Create query and populate it with where clause values
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery(hql.toString());
		query.setParameter("titleId", titleId);
		query.setParameter("version", version);

		ProviewAudit audit = (ProviewAudit) query.uniqueResult();
		if(audit == null){
			return null;
		}
		return audit.getProviewRequest();
		
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<ProviewAudit> findRemovedAndDeletedVersions(String titleId) {
		StringBuffer hql = new StringBuffer(
				"select pa from ProviewAudit pa where (pa.titleId,pa.bookVersion,pa.requestDate) in ");
		hql.append("(select pa2.titleId,pa2.bookVersion,max(pa2.requestDate) from ProviewAudit pa2 where");
		hql.append(" pa2.titleId = :titleId");
		hql.append(" and pa2.proviewRequest not in ('PROMOTE','REVIEW')  ");
		hql.append(" group by pa2.titleId,pa2.bookVersion) order by pa.id");
		// Create query and populate it with where clause values
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery(hql.toString());
		query.setParameter("titleId", titleId);

		return  query.list();
	}

	@Override
	public void save(ProviewAudit audit) {
		Session session = sessionFactory.getCurrentSession();
		
		if (audit.getId() != null) {
			audit = (ProviewAudit) session.merge(audit);
		} else {
			session.save(audit);
		}
		
		session.flush();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<ProviewAudit> findProviewAudits(ProviewAuditFilter filter, ProviewAuditSort sort) {

		Criteria criteria = addFilters(filter);

		String orderByColumn = getOrderByColumnName(sort.getSortProperty());
		if(sort.isAscending()) {
			criteria.addOrder(Order.asc(orderByColumn));
		} else {
			criteria.addOrder(Order.desc(orderByColumn));
		}
		
		int itemsPerPage = sort.getItemsPerPage();
		criteria.setFirstResult((sort.getPageNumber()-1)*(itemsPerPage));
		criteria.setMaxResults(itemsPerPage);
		
		return criteria.list();
	}
	
	
	@Override
	public int numberProviewAudits(ProviewAuditFilter filter) {
		Criteria criteria = addFilters(filter);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("id"), "id"));
		
		return criteria.list().size();
	}
	
	private Criteria addFilters(ProviewAuditFilter filter) {
		Session session = sessionFactory.getCurrentSession();

		Criteria criteria = session.createCriteria(ProviewAudit.class);

		if (filter.getFrom() != null) {
			criteria.add(Restrictions.ge("requestDate", filter.getFrom()));
		}
		if (filter.getTo() != null) {
			criteria.add(Restrictions.le("requestDate", filter.getTo()));
		}
		if (StringUtils.isNotBlank(filter.getAction())) {
			criteria.add(Restrictions.eq("proviewRequest", filter.getAction()));
		}
		if (StringUtils.isNotBlank(filter.getTitleId())) {
			criteria.add(Restrictions.like("titleId", filter.getTitleId()).ignoreCase());
		}
		if (StringUtils.isNotBlank(filter.getSubmittedBy())) {
			criteria.add(Restrictions.like("username", filter.getSubmittedBy()).ignoreCase());
		}
		
		return criteria;
	}
	
	/**
	 * Map the sort column enumeration into the actual column identifier used in the criteria.
	 * @param sortProperty enumerated value that reflects the database table sort column to sort on.
	 */
	private String getOrderByColumnName(SortProperty sortProperty) {
		switch (sortProperty) {
			case PROVIEW_REQUEST:
				return "proviewRequest";
			case REQUEST_DATE:
				return "requestDate";
			case BOOK_VERSION:
				return "bookVersion";
			case TITLE_ID:
				return "titleId";
			case USERNAME:
				return "username";
			case BOOK_LAST_UPDATED:
				return "bookLastUpdated";
			default:
				throw new IllegalArgumentException("Unexpected sort property: " + sortProperty);
		}
	}
}
