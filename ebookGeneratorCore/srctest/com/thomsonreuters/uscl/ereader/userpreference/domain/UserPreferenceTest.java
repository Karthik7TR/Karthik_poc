/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.userpreference.domain;

import java.util.Arrays;
import java.util.List;

import javax.mail.internet.InternetAddress;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class UserPreferenceTest  {
	private static final String CSV_RECIPIENTS = "a@a.com,b@b.com,c@c.com";
	
	@Before
	public void setUp() throws Exception {
	}
	
	@Test
	public void testGetEmailList() throws Exception {
		List<String> emails = Arrays.asList(new String[] {"a@a.com", "b@b.com", "c@c.com"});
		
		UserPreference preference = new UserPreference();
		preference.setEmails(CSV_RECIPIENTS);
		
		// Test the creation of the List<String>
		List<String> actual = preference.getEmailAddressList();
		Assert.assertEquals(emails, actual);
		
		// Test the creation of the List<InternetAddress>
		List<InternetAddress> addrs = preference.getInternetEmailAddressList();
		Assert.assertNotNull(addrs);
		Assert.assertEquals(3, addrs.size());
		Assert.assertTrue(addrs.contains(new InternetAddress("a@a.com")));
		Assert.assertTrue(addrs.contains(new InternetAddress("b@b.com")));
		Assert.assertTrue(addrs.contains(new InternetAddress("c@c.com")));
	}
	
	@Test
	public void testToStringEmailAddressList() {
		// Test a null csv address list
		String addressCsv = null;
		List<String> list = UserPreference.toStringEmailAddressList(addressCsv);
		Assert.assertNotNull(list);
		Assert.assertEquals(0, list.size());
		
		// Test a valid csv address
		list = UserPreference.toStringEmailAddressList(CSV_RECIPIENTS);
		Assert.assertNotNull(list);
		Assert.assertEquals(3, list.size());

	}
}
