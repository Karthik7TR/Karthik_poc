/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.core.service;

import java.util.Collection;

import javax.mail.internet.InternetAddress;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.thomsonreuters.uscl.ereader.userpreference.domain.UserPreference;
import com.thomsonreuters.uscl.ereader.userpreference.service.UserPreferenceService;


public class CoreServiceTest  {
	private static InternetAddress GROUP_ADDR;
	private CoreServiceImpl service;
	private UserPreferenceService mockUserPreferenceService;
	
	@Before
	public void setUp() throws Exception {
		GROUP_ADDR = new InternetAddress("someGroupAddr@bogustr.com");
		
		mockUserPreferenceService = EasyMock.createMock(UserPreferenceService.class);
		
		service = new CoreServiceImpl();
		service.setGroupEmailAddress(GROUP_ADDR);
		service.setUserPreferenceService(mockUserPreferenceService);
	}
	
	@Test
	public void testGetEmailRecipientsByUsername() {
		String username = "C1234567";
		String recipients = "foo@tr.com,bar@tr.com";
		UserPreference bogusUserPreference = new UserPreference();
		bogusUserPreference.setUserName(username);
		bogusUserPreference.setEmails(recipients);
		
		EasyMock.expect(mockUserPreferenceService.findByUsername(username)).andReturn(bogusUserPreference);
		EasyMock.replay(mockUserPreferenceService);

		Collection<InternetAddress> actual = service.getEmailRecipientsByUsername(username);
		Assert.assertNotNull(actual);
		Assert.assertEquals(3, actual.size());
		
		EasyMock.verify(mockUserPreferenceService);
	}
	
	
}
