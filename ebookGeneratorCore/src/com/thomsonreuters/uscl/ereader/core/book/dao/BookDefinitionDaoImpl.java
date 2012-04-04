/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.core.book.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateTemplate;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.PublisherCode;

public class BookDefinitionDaoImpl implements BookDefinitionDao {
	//private static final Logger log = Logger.getLogger(BookDefinitionDaoImpl.class);
	private SessionFactory sessionFactory;
	
	public BookDefinitionDaoImpl(SessionFactory sessFactory) {
		this.sessionFactory = sessFactory;
	}
	
	@Override
	public BookDefinition findBookDefinitionByTitle(String titleId) {
		BookDefinition bookDef = (BookDefinition) sessionFactory.getCurrentSession().createCriteria(BookDefinition.class)
		 .add( Restrictions.eq("fullyQualifiedTitleId", titleId)).uniqueResult();

		return bookDef;
	}
	
	@Override
	public BookDefinition findBookDefinitionByEbookDefId(Long ebookDefId) {
		return (BookDefinition) sessionFactory.getCurrentSession().get(BookDefinition.class, ebookDefId);
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
	@SuppressWarnings("unchecked")
	public List<BookDefinition> findBookDefinitions(String sortProperty, boolean isAscending, int pageNumber, int itemsPerPage, String proviewDisplayName, String fullyQualifiedTitleId, String isbn,
			String materialId, String to, String from, String status) {
		
		HibernateTemplate ht = new HibernateTemplate(sessionFactory);
		
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(BookDefinition.class);
		criteria.setMaxResults(10);
		
		
//		criteria.addOrder(isAscending?Order.asc(proviewDisplayName):Order.desc(proviewDisplayName));
//		
//		if (proviewDisplayName!=null && !proviewDisplayName.equals("")){
//			criteria.add(Restrictions.eq("proviewDisplayName", proviewDisplayName));
//		}
//		
//		if (fullyQualifiedTitleId!=null && !fullyQualifiedTitleId.equals("")){
//			criteria.add(Restrictions.eq("fullyQualifiedTitleId", fullyQualifiedTitleId));
//		}
//		
//		if (isbn!=null && !isbn.equals("")){
//			criteria.add(Restrictions.eq("isbn", isbn));
//		}
//		
//		if (materialId!=null && !materialId.equals("")){
//			criteria.add(Restrictions.eq("materialId", materialId));
//		}
//		
//		if (to!=null && !to.equals("")){
//			criteria.add(Restrictions.between("lastUpdated", from, to));
//		}
//		
//		if (status!=null && !status.equals("")){
//			criteria.add(Restrictions.eq("ebookDefinitionCompleteFlag", status));
//		}
		
		
		return criteria.list();
		
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
	public BookDefinition saveBookDefinition(BookDefinition eBook) {
		Session session = sessionFactory.getCurrentSession();
		
		eBook.setLastUpdated(new Date());
		
		// Attach Publisher Code
		eBook.setPublisherCodes( (PublisherCode) session.createCriteria(PublisherCode.class)
		 .add( Restrictions.eq("name", eBook.getPublisherCodes().getName())).uniqueResult());
		
		// Save if book is new
		if(eBook.getEbookDefinitionId() == null) {
			session.save(eBook);
		} 
		
		// attach child objects to book definition
		eBook = (BookDefinition) session.merge(eBook);
		session.flush();
		
		return eBook;
	}
}