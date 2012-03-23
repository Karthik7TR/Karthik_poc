/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.core.book.service;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.thomsonreuters.uscl.ereader.core.book.dao.BookDefinitionDao;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;

public class BookDefinitionServiceTest  {
	private static final String BOOK_KEY = "titleId";

	private BookDefinitionServiceImpl service;
	
	private BookDefinitionDao bookDefinitionDao;
	private BookDefinition expectedBookDefinition = new BookDefinition();
	
	@Before
	public void setUp() throws Exception {
		this.bookDefinitionDao = EasyMock.createMock(BookDefinitionDao.class);
		
		this.service = new BookDefinitionServiceImpl();
		service.setBookDefinitionDao(bookDefinitionDao);
		
		expectedBookDefinition.setFullyQualifiedTitleId(BOOK_KEY);
	}
	
	@Test
	public void testFindBookDefinition() {
		EasyMock.expect(bookDefinitionDao.findBookDefinitionByTitle(BOOK_KEY)).andReturn(expectedBookDefinition);
		EasyMock.replay(bookDefinitionDao);
		BookDefinition actualBookDefinition = service.findBookDefinitionByTitle(BOOK_KEY);
		Assert.assertEquals(expectedBookDefinition, actualBookDefinition);
		EasyMock.verify(bookDefinitionDao);
	}
	
	@Test
	public void testSaveBookDefinition() {
		EasyMock.expect(bookDefinitionDao.findBookDefinitionByTitle(BOOK_KEY)).andReturn(expectedBookDefinition);
		EasyMock.expect(bookDefinitionDao.saveBookDefinition(expectedBookDefinition)).andReturn(expectedBookDefinition);
		EasyMock.replay(bookDefinitionDao);
		
		BookDefinition book = service.saveBookDefinition(expectedBookDefinition);
		Assert.assertEquals(expectedBookDefinition, book);
		EasyMock.verify(bookDefinitionDao);
	}
}
