/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.userpreference.service;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.thomsonreuters.uscl.ereader.userpreference.dao.UserPreferenceDao;
import com.thomsonreuters.uscl.ereader.userpreference.domain.UserPreference;

public class UserPreferenceServiceTest  {

	private UserPreferenceServiceImpl service;
	
	private UserPreferenceDao mockDao;
	
	@Before
	public void setUp() throws Exception {
		this.mockDao = EasyMock.createMock(UserPreferenceDao.class);
		
		this.service = new UserPreferenceServiceImpl();
		service.setUserPreferenceDao(mockDao);
	}
	
	@Test
	public void testFindBookDefinition() {
		String username = "name";
		
		UserPreference expected = new UserPreference();
		expected.setUserName(username);
		
		EasyMock.expect(mockDao.findByUsername(username)).andReturn(expected);
		EasyMock.replay(mockDao);
		UserPreference actual = service.findByUsername(username);
		Assert.assertEquals(expected, actual);
		EasyMock.verify(mockDao);
	}
	
	@Test
	public void testSaveBookDefinition() {
		UserPreference expected = new UserPreference();
		
		mockDao.save(expected);
		EasyMock.replay(mockDao);
		
		service.save(expected);
		EasyMock.verify(mockDao);
	}
}
