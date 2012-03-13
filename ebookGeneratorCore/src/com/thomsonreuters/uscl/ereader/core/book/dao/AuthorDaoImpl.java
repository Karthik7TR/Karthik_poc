/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.core.book.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Transactional;

import com.thomsonreuters.uscl.ereader.core.book.domain.Author;

/**
 * DAO to manage DocMetadata entities.
 * 
 */

public class AuthorDaoImpl implements AuthorDao {

	private SessionFactory sessionFactory;

	public AuthorDaoImpl(SessionFactory hibernateSessionFactory) {
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
	public boolean canBeMerged(Author entity) {
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
	public Author persist(Author toPersist) {
		sessionFactory.getCurrentSession().save(toPersist);
		flush();
		return toPersist;
	}

	/*
	 * (non-Javadoc)
	 */
	@Transactional
	public void remove(Author toRemove) {
		toRemove = (Author) sessionFactory.getCurrentSession().merge(
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

	@SuppressWarnings("unchecked")
	public List<Author> findAuthorsByEBookDefnId(Long eBookDefnId) {
		Query query = createNamedQuery("findAuthorByEbookDefinitionId");
		query.setLong("eBookDefId", eBookDefnId);
		return query.list();
		
	}
	@Override
	@Transactional
	public void saveAuthor(Author author) {
		Session session = sessionFactory.getCurrentSession();
		session.save(author);
		session.flush();
	}

	@Override
	public Author findAuthorById(Long authorId)
			throws DataAccessException {
		Session session = sessionFactory.getCurrentSession();
		return (Author) session.get(Author.class, authorId);
	}
}
