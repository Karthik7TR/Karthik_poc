/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.smoketest.dao;

import java.sql.SQLException;

import org.easymock.EasyMock;
import org.hibernate.SessionFactory;
import org.hibernate.jdbc.ReturningWork;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SmokeTestDaoTest  {
	
	private SessionFactory mockSessionFactory;
	private org.hibernate.Session mockSession;
	private SmokeTestDaoImpl dao;
	
	@Before
	public void setUp() throws Exception {
		this.mockSessionFactory = EasyMock.createMock(SessionFactory.class);
		this.mockSession = EasyMock.createMock(org.hibernate.Session.class);
		this.dao = new SmokeTestDaoImpl(mockSessionFactory);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testConnectionStatus() throws SQLException {
		boolean expectedStatus = true;
		
		EasyMock.expect(mockSessionFactory.getCurrentSession()).andReturn(mockSession);
		EasyMock.expect(mockSession.doReturningWork(EasyMock.anyObject(ReturningWork.class))).andReturn(expectedStatus);
		EasyMock.replay(mockSessionFactory);
		EasyMock.replay(mockSession);
		
		boolean status = dao.testConnection();
		Assert.assertEquals(expectedStatus, status);
		
		EasyMock.verify(mockSessionFactory);
		EasyMock.verify(mockSession);
	}
}
