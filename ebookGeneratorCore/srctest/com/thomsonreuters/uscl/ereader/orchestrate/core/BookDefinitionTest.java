/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.orchestrate.core;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class BookDefinitionTest  {
	
	@Before
	public void setUp() throws Exception {
	}
	
/*	@Test
	public void testBookDefinitionKey() {
		String path = "uscl/cr/";
		String titleId = "ak_2013_state";
		String fullyQualifiedTitleId = path + titleId;
		BookDefinitionKey key = new BookDefinitionKey(fullyQualifiedTitleId);
		Assert.assertEquals(fullyQualifiedTitleId, key.getFullyQualifiedTitleId());
		Assert.assertEquals(titleId, key.getTitleId());
		Assert.assertEquals(fullyQualifiedTitleId, key.toKeyString());  // string representation "<titleId>,<majorVersion>"
		// Check that null fields are rejected
		try { key.setFullyQualifiedTitleId(null);} catch (IllegalArgumentException e) {Assert.assertTrue(true); }
	}*/
	/*	@Test
	public void testTitleIdWithBackslashDelimiters() {
		String path = "uscl\\cr\\";
		String rightComponent = "ak_2013_state";
		String fullyQualifiedTitleId = path + rightComponent;
		BookDefinitionKey key = new BookDefinitionKey(fullyQualifiedTitleId);
		Assert.assertEquals(fullyQualifiedTitleId, key.getFullyQualifiedTitleId());
		Assert.assertEquals(fullyQualifiedTitleId, key.getTitleId());
		Assert.assertEquals(fullyQualifiedTitleId, key.toKeyString());  // string representation "<titleId>,<majorVersion>"
	}*/

/*	@Test
	public void testParseAuthorNames() {
		String obama = "Spends Toomuch";
		String bush = "George Bush";
		String clinton = "Bill Clinton";
		
		// Check null
		List<String> authors = BookDefinition.parseAuthorNames(null);
		Assert.assertEquals(0, authors.size());

		// Check empty list
		authors = BookDefinition.parseAuthorNames("");
		Assert.assertEquals(0, authors.size());
		
		// Check populated list
		String pipedNameString = String.format(" %s | %s | %s ", obama, bush, clinton);
		authors = BookDefinition.parseAuthorNames(pipedNameString);
		Assert.assertEquals(obama, authors.get(0));
		Assert.assertEquals(bush, authors.get(1));
		Assert.assertEquals(clinton, authors.get(2));
	}*/
}
