/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.userpreference.domain;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class UserPreferenceTest  {
	
	@Before
	public void setUp() throws Exception {
	}
	
	@Test
	public void testGetEmailList() {
		List<String> emails = Arrays.asList(new String[] {"a@a.com", "b@b.com", "c@c.com"});
		
		UserPreference preference = new UserPreference();
		preference.setEmails("a@a.com,b@b.com,c@c.com");
		
		List<String> actual = preference.getEmailList();
		Assert.assertEquals(emails, actual);
	}

}
