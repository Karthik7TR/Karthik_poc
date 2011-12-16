/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.orchestrate.core;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class BookDefinitionTest  {
	
	@Before
	public void setUp() throws Exception {
	}
	
	@Test
	public void testBookDefinitionKey() {
		String titleId = "myTitleId";
		Long majorVer = new Long(1245);
		BookDefinitionKey key = new BookDefinitionKey(titleId, majorVer);
		Assert.assertEquals(titleId, key.getTitleId());
		Assert.assertEquals(majorVer, key.getMajorVersion());
		Assert.assertEquals(titleId+","+majorVer, key.toKeyString());  // string representation "<titleId>,<majorVersion>"
		// Check that null fields are rejected
		try { key.setTitleId(null);} catch (IllegalArgumentException e) {Assert.assertTrue(true); }
		try { key.setMajorVersion(null);} catch (IllegalArgumentException e) {Assert.assertTrue(true); }
	}
}
