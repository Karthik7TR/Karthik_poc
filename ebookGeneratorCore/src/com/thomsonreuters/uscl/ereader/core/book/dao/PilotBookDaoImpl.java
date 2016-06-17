package com.thomsonreuters.uscl.ereader.core.book.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Transactional;

import com.thomsonreuters.uscl.ereader.core.book.domain.PilotBook;

public class PilotBookDaoImpl implements PilotBookDao{
	
	private SessionFactory sessionFactory;
	
	public PilotBookDaoImpl(SessionFactory hibernateSessionFactory) {
		this.sessionFactory = hibernateSessionFactory;
	}
	
	/**
	 * Query - findAuthorByPrimaryKey
	 * 
	 */
	@Transactional
	public PilotBook findPilotBookByTitleId(String pilotBookTitleId)
			throws DataAccessException{
		Session session = sessionFactory.getCurrentSession();
		return (PilotBook) session.get(PilotBook.class, pilotBookTitleId);
	}

	/**
	 * Query - findAuthorsByEBookDefnId
	 * 
	 */	
	@SuppressWarnings("unchecked")
	public List<PilotBook> findPilotBooksByEBookDefnId(Long eBookDefnId)
			throws DataAccessException{
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(PilotBook.class);
		criteria.add(Restrictions.eq("ebookDefinition.ebookDefinitionId", eBookDefnId));
		return criteria.list();
	}


	@Transactional
	public void remove(PilotBook toRemove) throws DataAccessException{
		toRemove = (PilotBook) sessionFactory.getCurrentSession().merge(
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

	@Transactional
	public void savePilotBook(PilotBook pilotBook){
		Session session = sessionFactory.getCurrentSession();
		session.save(pilotBook);
		session.flush();
	}

}
