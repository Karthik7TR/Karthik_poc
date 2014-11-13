/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.proviewaudit.dao;

import java.util.ArrayList;
import java.util.List;

import org.easymock.EasyMock;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.thomsonreuters.uscl.ereader.proviewaudit.domain.ProviewAudit;
import com.thomsonreuters.uscl.ereader.proviewaudit.domain.ProviewAuditFilter;
import com.thomsonreuters.uscl.ereader.proviewaudit.domain.ProviewAuditSort;
import com.thomsonreuters.uscl.ereader.proviewaudit.domain.ProviewAuditSort.SortProperty;

public class ProviewAuditDaoTest  {
	private static final List<ProviewAudit> PROVIEW_AUDIT_LIST = new ArrayList<ProviewAudit>();

	private SessionFactory mockSessionFactory;
	private org.hibernate.Session mockSession;
	private Criteria mockCriteria;
	private ProviewAuditDaoImpl dao;
	
	@Before
	public void setUp() throws Exception {
		this.mockSessionFactory = EasyMock.createMock(SessionFactory.class);
		this.mockSession = EasyMock.createMock(org.hibernate.Session.class);
		this.mockCriteria = EasyMock.createMock(Criteria.class);
		this.dao = new ProviewAuditDaoImpl(mockSessionFactory);
	}
	
	@Test
	public void testFindProviewAudits() {
		ProviewAuditSort sort = new ProviewAuditSort(SortProperty.REQUEST_DATE, false, 1, 20);
		ProviewAuditFilter filter = new ProviewAuditFilter();
		
		EasyMock.expect(mockSessionFactory.getCurrentSession()).andReturn(mockSession);
		EasyMock.expect(mockSession.createCriteria(ProviewAudit.class)).andReturn(mockCriteria);
		EasyMock.expect(mockCriteria.addOrder(Order.asc(EasyMock.anyObject(String.class)))).andReturn(mockCriteria);
		
		int itemsPerPage = sort.getItemsPerPage();
		EasyMock.expect(mockCriteria.setFirstResult((sort.getPageNumber()-1)*(itemsPerPage))).andReturn(mockCriteria);
		EasyMock.expect(mockCriteria.setMaxResults(itemsPerPage)).andReturn(mockCriteria);
		EasyMock.expect(mockCriteria.list()).andReturn(PROVIEW_AUDIT_LIST);
		EasyMock.replay(mockSessionFactory);
		EasyMock.replay(mockSession);
		EasyMock.replay(mockCriteria);
		
		List<ProviewAudit> actualAudits = dao.findProviewAudits(filter, sort);
		Assert.assertEquals(PROVIEW_AUDIT_LIST, actualAudits);
		EasyMock.verify(mockSessionFactory);
		EasyMock.verify(mockSession);
		EasyMock.verify(mockCriteria);
	}
	
	@Test
	public void testNumberProviewAudits() {
		ProviewAuditFilter filter = new ProviewAuditFilter();
		
		EasyMock.expect(mockSessionFactory.getCurrentSession()).andReturn(mockSession);
		EasyMock.expect(mockSession.createCriteria(ProviewAudit.class)).andReturn(mockCriteria);
		EasyMock.expect(mockCriteria.setProjection(EasyMock.anyObject(Projection.class))).andReturn(mockCriteria);
		EasyMock.expect(mockCriteria.list()).andReturn(PROVIEW_AUDIT_LIST);
		EasyMock.replay(mockSessionFactory);
		EasyMock.replay(mockSession);
		EasyMock.replay(mockCriteria);
		
		int actual = dao.numberProviewAudits(filter);
		Assert.assertEquals(PROVIEW_AUDIT_LIST.size(), actual);
		EasyMock.verify(mockSessionFactory);
		EasyMock.verify(mockSession);
		EasyMock.verify(mockCriteria);
	}
}
