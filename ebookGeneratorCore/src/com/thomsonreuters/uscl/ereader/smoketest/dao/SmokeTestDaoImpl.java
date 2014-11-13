/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.smoketest.dao;

import java.sql.Connection;
import java.sql.SQLException;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.jdbc.ReturningWork;

/**
 * DAO to test DB connection
 * 
 */

public class SmokeTestDaoImpl implements SmokeTestDao {

	private SessionFactory sessionFactory;

	public SmokeTestDaoImpl(SessionFactory hibernateSessionFactory) {
		this.sessionFactory = hibernateSessionFactory;
	}


	public boolean testConnection() {
		boolean status = false;
		try {
			status = sessionFactory.getCurrentSession().doReturningWork( new ReturningWork<Boolean>() {
	            @Override
	            public Boolean execute(Connection connection) throws SQLException 
	            { 
	                return connection.isValid(5000);
	            }
	        });
		} catch (HibernateException e) {
			status = false;
			e.printStackTrace();
		} 
		return status;
	}
}
