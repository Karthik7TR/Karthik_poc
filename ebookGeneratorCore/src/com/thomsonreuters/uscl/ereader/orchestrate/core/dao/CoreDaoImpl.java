/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.orchestrate.core.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import com.thomsonreuters.uscl.ereader.orchestrate.core.BookDefinition;

public class CoreDaoImpl implements CoreDao {
	//private static final Logger log = Logger.getLogger(CoreDaoImpl.class);
	private SessionFactory sessionFactory;
	
	public CoreDaoImpl(SessionFactory sessFactory) {
		this.sessionFactory = sessFactory;
	}
	
	@Override
	public BookDefinition findBookDefinitionByTitle(String titleId) {
		@SuppressWarnings("unchecked")
		List<BookDefinition> bookDef = sessionFactory.getCurrentSession().createCriteria(BookDefinition.class)
		 .add( Restrictions.eq("fullyQualifiedTitleId", titleId)).list();

		if (bookDef == null)
		{
			return null;
		}
		return bookDef.get(0);
	}
	
	@Override
	public BookDefinition findBookDefinitionByEbookDefId(Long ebookDefId) {
		@SuppressWarnings("unchecked")
		List<BookDefinition> bookDef = sessionFactory.getCurrentSession().createCriteria(BookDefinition.class)
		 .add( Restrictions.eq("eBookDefinitionId", ebookDefId)).list();

		if (bookDef == null)
		{
			return null;
		}
		return bookDef.get(0);

	}
	
	@Override
	@Deprecated
	@SuppressWarnings("unchecked")
	public List<BookDefinition> findAllBookDefinitions() {
		String namedQuery = "findBookDefnBySearchCriterion";	
		String bookDefnQuery = sessionFactory.getCurrentSession().getNamedQuery(namedQuery).getQueryString();
		Query query = sessionFactory.getCurrentSession().createQuery(bookDefnQuery);				
		return query.list();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<BookDefinition> findBookDefinitions(String sortProperty, boolean isAscending, int pageNumber, int itemsPerPage) {
		String namedQuery = "findBookDefnBySearchCriterion";	
		String bookDefnQuery = sessionFactory.getCurrentSession().getNamedQuery(namedQuery).getQueryString() + "order by " + sortProperty;
		bookDefnQuery += (isAscending) ? " asc" : " desc";	
		Query query = sessionFactory.getCurrentSession().createQuery(bookDefnQuery);
		query.setFirstResult((pageNumber-1)*(itemsPerPage));
		query.setMaxResults(itemsPerPage);
		return query.list();
	}

	@Override
	public long countNumberOfBookDefinitions() {
		Query query = sessionFactory.getCurrentSession().getNamedQuery("countBookDefinitions");
		return (Long)query.uniqueResult();
	}

	@Override
	public void removeBookDefinition(String titleId) {
		
		Session session = sessionFactory.getCurrentSession();
		session.delete(findBookDefinitionByTitle(titleId));
		session.flush();
	}

	@Override
	public void saveBookDefinition(BookDefinition eBook) {
		Session session = sessionFactory.getCurrentSession();
		session.save(eBook);
		session.flush();
	}


}