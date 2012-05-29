/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.core.outage.domain;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PlannedOutageTest  {
	
	@Before
	public void setUp() throws Exception {
	}
	
	@Test
	public void booleanStateTest(){
		PlannedOutage po = new PlannedOutage();
		// Null is initial field values, verify that they return as false
		Assert.assertFalse(po.isAllClearEmailSent());
		Assert.assertFalse(po.isNotificationEmailSent());
		
		// Check true state
		po.setAllClearEmailSent(true);
		Assert.assertTrue(po.isAllClearEmailSent());
		po.setNotificationEmailSent(true);
		Assert.assertTrue(po.isNotificationEmailSent());
		
		// Check false state
		po.setAllClearEmailSent(false);
		Assert.assertFalse(po.isAllClearEmailSent());
		po.setNotificationEmailSent(false);
		Assert.assertFalse(po.isNotificationEmailSent());
	}
}
