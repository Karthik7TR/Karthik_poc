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

import com.thomsonreuters.uscl.ereader.core.book.dao.EbookAuditDao;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;

public class EbookAuditServiceTest  {
	private static final long BOOK_KEY = 1L;

	private EBookAuditServiceImpl service;
	
	private EbookAuditDao mockDao;
	private EbookAudit expectedAudit = new EbookAudit();
	
	@Before
	public void setUp() throws Exception {
		this.mockDao = EasyMock.createMock(EbookAuditDao.class);
		
		this.service = new EBookAuditServiceImpl();
		service.seteBookAuditDAO(mockDao);
	}
	
	@Test
	public void testFindBookDefinition() {
		EasyMock.expect(mockDao.findEbookAuditByPrimaryKey(BOOK_KEY)).andReturn(expectedAudit);
		EasyMock.replay(mockDao);
		EbookAudit actualAudit = service.findEBookAuditByPrimaryKey(BOOK_KEY);
		Assert.assertEquals(expectedAudit, actualAudit);
		EasyMock.verify(mockDao);
	}
	
	@Test
	public void testSaveBookDefinition() {
		mockDao.saveAudit(expectedAudit);
		EasyMock.replay(mockDao);
		
		service.saveEBookAudit(expectedAudit);
		EasyMock.verify(mockDao);
	}
}
