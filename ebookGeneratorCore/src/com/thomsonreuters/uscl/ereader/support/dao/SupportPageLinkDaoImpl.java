/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.support.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;

import com.thomsonreuters.uscl.ereader.support.domain.SupportPageLink;


/**
 * DAO to manage SupportPageLink entities.
 * 
 */

public class SupportPageLinkDaoImpl implements SupportPageLinkDao {
	//private static Logger log = LogManager.getLogger(UserPreferenceDaoImpl.class);
	private SessionFactory sessionFactory;

	public SupportPageLinkDaoImpl(SessionFactory hibernateSessionFactory) {
		this.sessionFactory = hibernateSessionFactory;
	}


	@Override
	public void save(SupportPageLink spl) {
		Session session = sessionFactory.getCurrentSession();
		
		spl.setLastUpdated(new Date());
		session.saveOrUpdate(spl);
		session.flush();
	}
	
	@Override
	public void delete(SupportPageLink spl) {
		Session session = sessionFactory.getCurrentSession();
		session.delete(spl);
		session.flush();
	}

	@Override
	public SupportPageLink findByPrimaryKey(Long id) {
		Session session = sessionFactory.getCurrentSession();
		return (SupportPageLink) session.get(SupportPageLink.class, id);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<SupportPageLink> findAllSupportPageLink() {
		Session session = sessionFactory.getCurrentSession();
		return session.createCriteria(SupportPageLink.class).addOrder(Order.desc("linkDescription")).list();
	}
}
