/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.orchestrate.core.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;

import com.thomsonreuters.uscl.ereader.orchestrate.core.BookDefinition;
import com.thomsonreuters.uscl.ereader.orchestrate.core.BookDefinitionKey;

public class CoreDaoImpl implements CoreDao {
	//private static final Logger log = Logger.getLogger(CoreDaoImpl.class);
	private SessionFactory sessionFactory;
	
	public CoreDaoImpl(SessionFactory sessFactory) {
		this.sessionFactory = sessFactory;
	}
	
	@Override
	public BookDefinition findBookDefinition(BookDefinitionKey key) {
		return (BookDefinition) sessionFactory.getCurrentSession().get(BookDefinition.class, key);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<BookDefinition> findAllBookDefinitions() {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(BookDefinition.class);
		return criteria.list();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<BookDefinition> findBookDefinitions(String sortProperty, boolean isAscending, int pageNumber, int itemsPerPage) {
		
		String namedQuery = "findBookDefnBySearchCriterion";
		if (isAscending) {
			namedQuery = namedQuery + " asc";
		}
		Query query = sessionFactory.getCurrentSession().getNamedQuery(namedQuery);
		query.setString("sort_by", sortProperty);
		query.setFirstResult((pageNumber-1)*(itemsPerPage) + 1);
		query.setMaxResults(itemsPerPage);
		return query.list();
	}

	@Override
	public int countNumberOfBookDefinitions() {
		Query query = sessionFactory.getCurrentSession().getNamedQuery("countBookDefinitions");
		return query.list().size();
	}
}