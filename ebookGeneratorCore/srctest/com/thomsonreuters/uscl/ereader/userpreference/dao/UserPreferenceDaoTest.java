/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.userpreference.dao;

import org.easymock.EasyMock;
import org.hibernate.SessionFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.thomsonreuters.uscl.ereader.userpreference.domain.UserPreference;


public class UserPreferenceDaoTest  {

	private SessionFactory mockSessionFactory;
	private org.hibernate.Session mockSession;
	private UserPreferenceDaoImpl dao;
	
	@Before
	public void setUp() throws Exception {
		this.mockSessionFactory = EasyMock.createMock(SessionFactory.class);
		this.mockSession = EasyMock.createMock(org.hibernate.Session.class);
		this.dao = new UserPreferenceDaoImpl(mockSessionFactory);
	}
	
	@Test
	public void testFindByUsername() {
		String username = "name";
		
		UserPreference expected = new UserPreference();
		expected.setUserName(username);

		EasyMock.expect(mockSessionFactory.getCurrentSession()).andReturn(mockSession);
		EasyMock.expect(mockSession.get(UserPreference.class, username)).andReturn(expected);
		EasyMock.replay(mockSessionFactory);
		EasyMock.replay(mockSession);
		UserPreference actual = dao.findByUsername(username);
		Assert.assertEquals(actual, expected);
		EasyMock.verify(mockSessionFactory);
		EasyMock.verify(mockSession);
	}
}
