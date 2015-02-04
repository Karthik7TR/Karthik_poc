/*
 * Copyright 2015: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.core.book.service;

import java.util.ArrayList;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.thomsonreuters.uscl.ereader.core.book.dao.EbookAuditDao;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAuditFilter;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAuditSort;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAuditSort.SortProperty;

public class EbookAuditServiceTest  {
	private static final long BOOK_KEY = 1L;
	private static final String TITLE_ID = "book/title";
	private static final String ISBN = "123456789";
	private List<EbookAudit> BOOK_AUDIT_LIST = null;
	private EbookAudit expectedAudit = null;
	
	private EBookAuditServiceImpl service;
	
	private EbookAuditDao mockDao;
	
	@Before
	public void setUp() throws Exception {
		this.mockDao = EasyMock.createMock(EbookAuditDao.class);
		
		this.service = new EBookAuditServiceImpl();
		service.seteBookAuditDAO(mockDao);
		
		BOOK_AUDIT_LIST = new ArrayList<EbookAudit>();
		expectedAudit = new EbookAudit();
		expectedAudit.setAuditId(BOOK_KEY);
		expectedAudit.setTitleId(TITLE_ID);
		expectedAudit.setIsbn(ISBN);
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
	
	@Test
	public void testFindEbookAudits() {
		EbookAuditSort sort = new EbookAuditSort(SortProperty.SUBMITTED_DATE, false, 1, 20);
		EbookAuditFilter filter = new EbookAuditFilter();
		
		EasyMock.expect(mockDao.findEbookAudits(filter, sort)).andReturn(BOOK_AUDIT_LIST);
		EasyMock.replay(mockDao);
		
		List<EbookAudit> actual = service.findEbookAudits(filter, sort);
		Assert.assertEquals(BOOK_AUDIT_LIST, actual);
		EasyMock.verify(mockDao);
	}
	
	@Test
	public void findNumberEbookAudits() {
		int number = 0;
		EbookAuditFilter filter = new EbookAuditFilter();
		
		EasyMock.expect(mockDao.numberEbookAudits(filter)).andReturn(number);
		EasyMock.replay(mockDao);
		
		int actual = service.numberEbookAudits(filter);
		Assert.assertEquals(number, actual);
		EasyMock.verify(mockDao);
	}
	
	@Test
	public void testEditIsbn() {
		
		BOOK_AUDIT_LIST.add(expectedAudit);
		EasyMock.expect(mockDao.findEbookAuditByTitleIdAndIsbn(TITLE_ID, ISBN)).andReturn(BOOK_AUDIT_LIST);
		mockDao.saveAudit(expectedAudit);
		EasyMock.replay(mockDao);
		
		EbookAudit actualAudit = service.editIsbn(TITLE_ID, ISBN);

		Assert.assertEquals(EbookAuditDao.MOD_TEXT + ISBN, actualAudit.getIsbn());
		EasyMock.verify(mockDao);
	}
}
