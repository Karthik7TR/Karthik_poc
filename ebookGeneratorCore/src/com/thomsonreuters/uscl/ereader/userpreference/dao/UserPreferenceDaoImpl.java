/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.userpreference.dao;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.mail.internet.InternetAddress;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.thomsonreuters.uscl.ereader.userpreference.domain.UserPreference;

/**
 * DAO to manage UserPreference entities.
 * 
 */

public class UserPreferenceDaoImpl implements UserPreferenceDao {
	//private static Logger log = Logger.getLogger(UserPreferenceDaoImpl.class);
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
	
	@Override
	@SuppressWarnings("unchecked")
	public Set<InternetAddress> findAllUniqueEmailAddresses() {
		Set<InternetAddress> uniqueAddresses = new HashSet<InternetAddress>();
		Session session = sessionFactory.getCurrentSession();
		String hql = "select emails from UserPreference";
		Query query = session.createQuery(hql);
		// get the list of csv email addrs { "a@foo.com,b@foo.com,c@foo.com", "d@foo.com,e@foo.com,f@foo.com" }
		List<String> csvEmailAddrs = query.list();  // a list of CSV email addresses

		// Parse all the csv strings into a unique list of address objects
		for (String csvAddr : csvEmailAddrs) {
			List<String> addrStrings = UserPreference.toStringAddressAddressList(csvAddr);
			List<InternetAddress> addrs = UserPreference.toInternetAddressList(addrStrings);
			uniqueAddresses.addAll(addrs);
		}
		return uniqueAddresses;
	}
}
