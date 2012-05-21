/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.userpreference.dao;

import java.util.Date;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.thomsonreuters.uscl.ereader.userpreference.domain.UserPreference;

/**
 * DAO to manage UserPreference entities.
 * 
 */

public class UserPreferenceDaoImpl implements UserPreferenceDao {

	private SessionFactory sessionFactory;

	public UserPreferenceDaoImpl(SessionFactory hibernateSessionFactory) {
		this.sessionFactory = hibernateSessionFactory;
	}


	@Override
	public void save(UserPreference preference) {
		Session session = sessionFactory.getCurrentSession();
		
		preference.setLastUpdated(new Date());
		session.saveOrUpdate(preference);
		session.flush();
	}

	@Override
	public UserPreference findByUsername(String username) {
		Session session = sessionFactory.getCurrentSession();
		return (UserPreference) session.get(UserPreference.class, username);
	}
}
