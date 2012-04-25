/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.core.book.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.DataAccessException;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinitionLock;


/**
 * DAO to manage BookDefinitionLock entities.
 * 
 */

public class BookDefinitionLockDaoImpl implements BookDefinitionLockDao {
	//private static final Logger log = Logger.getLogger(BookDefinitionLockDaoImpl.class);

	private SessionFactory sessionFactory;

	public BookDefinitionLockDaoImpl(SessionFactory hibernateSessionFactory) {
		this.sessionFactory = hibernateSessionFactory;
	}
	
	@SuppressWarnings("unchecked")
	public List<BookDefinitionLock> findAllActiveLocks() {
		
		// Set session timeout 
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.SECOND, -(BookDefinitionLock.LOCK_TIMEOUT_SEC));
		
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(BookDefinitionLock.class)
				.createAlias("ebookDefinition", "book")
				.addOrder(Order.desc("checkoutTimestamp"))
				.add(Restrictions.ge("checkoutTimestamp", cal.getTime()))
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		// Create HashMap to find the newest timestamp for each book definition id
		Map<Long, BookDefinitionLock> bookDefinitionMap = new HashMap<Long, BookDefinitionLock>();
		for(BookDefinitionLock lock : (List<BookDefinitionLock>) criteria.list()) {
			
			BookDefinitionLock previousLock = bookDefinitionMap.get(lock.getEbookDefinition().getEbookDefinitionId());
			if(previousLock != null) {
				if(lock.getCheckoutTimestamp().after(previousLock.getCheckoutTimestamp())) {
					bookDefinitionMap.put(lock.getEbookDefinition().getEbookDefinitionId(), lock);
				}
			} else {
				bookDefinitionMap.put(lock.getEbookDefinition().getEbookDefinitionId(), lock);
			}
		}	
		
		return new ArrayList<BookDefinitionLock>(bookDefinitionMap.values());
	}
	
	@SuppressWarnings("unchecked")
	public List<BookDefinitionLock> findLocksByBookDefinition(BookDefinition book)
			throws DataAccessException {
		
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(BookDefinitionLock.class)
				.add(Restrictions.eq("ebookDefinition", book))
				.addOrder(Order.desc("checkoutTimestamp"));
		return criteria.list();
	}
	
	public BookDefinitionLock findBookDefinitionLockByPrimaryKey(Long primaryKey) {
		return (BookDefinitionLock) sessionFactory.getCurrentSession().createCriteria(BookDefinitionLock.class)
				.add(Restrictions.eq("ebookDefinitionLockId", primaryKey))
				.setFetchMode("ebookDefinition", FetchMode.JOIN)
				.uniqueResult();
	}


	public void removeLock(BookDefinitionLock bookDefinitionLock) throws DataAccessException {
		Session session = sessionFactory.getCurrentSession();
		bookDefinitionLock = (BookDefinitionLock) session.merge(
				bookDefinitionLock);
		session.delete(bookDefinitionLock);
		session.flush();
	}
	
	/**
	 * Removes all locks that has expired
	 */
	@SuppressWarnings("unchecked")
	public void cleanExpiredLocks() {
		Session session = sessionFactory.getCurrentSession();
		// Set session timeout 
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.SECOND, -(BookDefinitionLock.LOCK_TIMEOUT_SEC));
		
		Criteria criteria = session.createCriteria(BookDefinitionLock.class)
				.add(Restrictions.le("checkoutTimestamp", cal.getTime()));
		
		List<BookDefinitionLock> expiredLocks = criteria.list();
		
		for(BookDefinitionLock lock: expiredLocks) {
			session.delete(lock);
		}
		
		session.flush();
	}

	public void saveLock(BookDefinitionLock bookDefinitionLock) {
		Session session = sessionFactory.getCurrentSession();
		session.save(bookDefinitionLock);
		session.flush();
	}
}
