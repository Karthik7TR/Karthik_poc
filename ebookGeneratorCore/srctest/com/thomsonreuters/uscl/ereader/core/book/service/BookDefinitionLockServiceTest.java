/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.core.book.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.thomsonreuters.uscl.ereader.core.book.dao.BookDefinitionLockDao;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinitionLock;


public class BookDefinitionLockServiceTest  {
	private final static BookDefinitionLock BOOK_LOCK = new BookDefinitionLock();
	private  List<BookDefinitionLock> BOOK_LOCK_LIST;

	private BookDefinitionLockDao mockDao;
	private BookDefinitionLockServiceImpl service;
	
	@Before
	public void setUp() throws Exception {
		this.mockDao = EasyMock.createMock(BookDefinitionLockDao.class);
		
		this.service = new BookDefinitionLockServiceImpl();
		this.service.setBookDefinitionLockDao(mockDao);
		
		BOOK_LOCK_LIST = new ArrayList<BookDefinitionLock>();
	}
	
	@Test
	public void testFindBookLockByBookDefinitionNoLocksFound() {
		BookDefinition book = new BookDefinition();
		
		EasyMock.expect(mockDao.findLocksByBookDefinition(book)).andReturn(BOOK_LOCK_LIST);
		EasyMock.replay(mockDao);

		BookDefinitionLock lock = service.findBookLockByBookDefinition(book);
		Assert.assertEquals(null, lock);
		
		EasyMock.verify(mockDao);
	}
	
	@Test
	public void testFindBookLockByBookDefinitionLocksFound() {
		BookDefinition book = new BookDefinition();
		
		BookDefinitionLock expectedLock = new BookDefinitionLock();
		expectedLock.setCheckoutTimestamp(new Date());
		BOOK_LOCK_LIST.add(expectedLock);
		
		EasyMock.expect(mockDao.findLocksByBookDefinition(book)).andReturn(BOOK_LOCK_LIST);
		EasyMock.replay(mockDao);

		BookDefinitionLock actualLock = service.findBookLockByBookDefinition(book);
		Assert.assertEquals(expectedLock, actualLock);
		
		EasyMock.verify(mockDao);
	}
	
	@Test
	public void testFindAllActiveLocks() {
		EasyMock.expect(mockDao.findAllActiveLocks()).andReturn(BOOK_LOCK_LIST);
		EasyMock.replay(mockDao);

		List<BookDefinitionLock> locks = service.findAllActiveLocks();
		Assert.assertEquals(BOOK_LOCK_LIST, locks);
		
		EasyMock.verify(mockDao);
	}
	
	@Test
	public void testFindBookDefinitionLockByPrimaryKey() {
		EasyMock.expect(mockDao.findBookDefinitionLockByPrimaryKey(EasyMock.anyLong())).andReturn(BOOK_LOCK);
		EasyMock.replay(mockDao);

		BookDefinitionLock lock = service.findBookDefinitionLockByPrimaryKey(1L);
		Assert.assertEquals(BOOK_LOCK, lock);
		
		EasyMock.verify(mockDao);
	}
	
}
