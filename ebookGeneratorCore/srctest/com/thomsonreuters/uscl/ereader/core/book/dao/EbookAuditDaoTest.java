/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.core.book.dao;

import org.easymock.EasyMock;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;

public class EbookAuditDaoTest  {
	private static final long AUDIT_KEY = 1L;
	private static final EbookAudit BOOK_AUDIT = new EbookAudit();

	private SessionFactory mockSessionFactory;
	private org.hibernate.classic.Session mockSession;
	private Query mockQuery;
	private EBookAuditDaoImpl dao;
	
	@Before
	public void setUp() throws Exception {
		this.mockSessionFactory = EasyMock.createMock(SessionFactory.class);
		this.mockSession = EasyMock.createMock(org.hibernate.classic.Session.class);
		this.mockQuery = EasyMock.createMock(Query.class);
		this.dao = new EBookAuditDaoImpl(mockSessionFactory);
	}
	
	@Test
	public void testFindAuditById() {
		EasyMock.expect(mockSessionFactory.getCurrentSession()).andReturn(mockSession);
		EasyMock.expect(mockSession.getNamedQuery("findEbookAuditByPrimaryKey")).andReturn(mockQuery);
		EasyMock.expect(mockQuery.setLong("auditId", AUDIT_KEY)).andReturn(mockQuery);
		EasyMock.expect(mockQuery.uniqueResult()).andReturn(BOOK_AUDIT);
		EasyMock.replay(mockSessionFactory);
		EasyMock.replay(mockSession);
		EasyMock.replay(mockQuery);
		EbookAudit actualBookDefinition = dao.findEbookAuditByPrimaryKey(AUDIT_KEY);
		Assert.assertEquals(BOOK_AUDIT, actualBookDefinition);
		EasyMock.verify(mockSessionFactory);
		EasyMock.verify(mockSession);
	}
}
