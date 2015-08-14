/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.smoketest.domain;

import org.junit.Assert;

import org.junit.Before;
import org.junit.Test;

public class SmokeTestTest  {
	
	@Before
	public void setUp() throws Exception {
	}
	
	@Test
	public void smokeTestTest(){
		String name = "name";
		String address = "home";
		boolean isRunning = false;
		
		SmokeTest test = new SmokeTest();
		test.setAddress(address);
		test.setIsRunning(isRunning);
		test.setName(name);
		
		Assert.assertEquals(name, test.getName());
		Assert.assertEquals(isRunning, test.isRunning());
		Assert.assertEquals(address, test.getAddress());
	}
}
