/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.orchestrate.core.service;

import java.util.ArrayList;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.thomsonreuters.uscl.ereader.orchestrate.core.BookDefinition;
import com.thomsonreuters.uscl.ereader.orchestrate.core.BookDefinitionKey;
import com.thomsonreuters.uscl.ereader.orchestrate.core.dao.CoreDao;

public class CoreServiceTest  {
	private static final BookDefinitionKey BOOK_KEY = new BookDefinitionKey("titleId");

	private CoreServiceImpl service;
	
	private CoreDao mockCoreDao;
	private BookDefinition expectedBookDefinition = new BookDefinition();
	
	@Before
	public void setUp() throws Exception {
		this.mockCoreDao = EasyMock.createMock(CoreDao.class);
		
		this.service = new CoreServiceImpl();
		service.setCoreDao(mockCoreDao);
	}
	
	@Test
	public void testFindBookDefinition() {
		EasyMock.expect(mockCoreDao.findBookDefinition(BOOK_KEY)).andReturn(expectedBookDefinition);
		EasyMock.replay(mockCoreDao);
		BookDefinition actualBookDefinition = service.findBookDefinition(BOOK_KEY);
		Assert.assertEquals(expectedBookDefinition, actualBookDefinition);
		EasyMock.verify(mockCoreDao);
	}

	@Test
	public void testFindBookAllDefinitions() {
		List<BookDefinition> expectedBookDefinitions = new ArrayList<BookDefinition>(0);
		EasyMock.expect(mockCoreDao.findAllBookDefinitions()).andReturn(expectedBookDefinitions);
		EasyMock.replay(mockCoreDao);
		List<BookDefinition> actualBookDefinitions = service.findAllBookDefinitions();
		Assert.assertEquals(expectedBookDefinitions, actualBookDefinitions);
		EasyMock.verify(mockCoreDao);
	}
}
