/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.proviewaudit.service;

import java.util.ArrayList;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.thomsonreuters.uscl.ereader.proviewaudit.dao.ProviewAuditDao;
import com.thomsonreuters.uscl.ereader.proviewaudit.domain.ProviewAudit;
import com.thomsonreuters.uscl.ereader.proviewaudit.domain.ProviewAuditFilter;
import com.thomsonreuters.uscl.ereader.proviewaudit.domain.ProviewAuditSort;
import com.thomsonreuters.uscl.ereader.proviewaudit.domain.ProviewAuditSort.SortProperty;

public class ProviewAuditServiceTest  {
	private static final List<ProviewAudit> PROVIEW_AUDIT_LIST = new ArrayList<ProviewAudit>();

	private ProviewAuditServiceImpl service;
	
	private ProviewAuditDao mockDao;
	private ProviewAudit expectedAudit = new ProviewAudit();
	
	@Before
	public void setUp() throws Exception {
		this.mockDao = EasyMock.createMock(ProviewAuditDao.class);
		
		this.service = new ProviewAuditServiceImpl();
		service.setProviewAuditDao(mockDao);
	}

	@Test
	public void testSave() {
		mockDao.save(expectedAudit);
		EasyMock.replay(mockDao);
		
		service.save(expectedAudit);
		EasyMock.verify(mockDao);
	}
	
	@Test
	public void testFindProviewAudits() {
		ProviewAuditSort sort = new ProviewAuditSort(SortProperty.REQUEST_DATE, false, 1, 20);
		ProviewAuditFilter filter = new ProviewAuditFilter();
		
		EasyMock.expect(mockDao.findProviewAudits(filter, sort)).andReturn(PROVIEW_AUDIT_LIST);
		EasyMock.replay(mockDao);
		
		List<ProviewAudit> actual = service.findProviewAudits(filter, sort);
		Assert.assertEquals(PROVIEW_AUDIT_LIST, actual);
		EasyMock.verify(mockDao);
	}
	
	@Test
	public void findNumberProviewAudits() {
		int number = 0;
		ProviewAuditFilter filter = new ProviewAuditFilter();
		
		EasyMock.expect(mockDao.numberProviewAudits(filter)).andReturn(number);
		EasyMock.replay(mockDao);
		
		int actual = service.numberProviewAudits(filter);
		Assert.assertEquals(number, actual);
		EasyMock.verify(mockDao);
	}
}
