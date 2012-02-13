/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.orchestrate.core.dao;

import org.easymock.EasyMock;
import org.hibernate.SessionFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.thomsonreuters.uscl.ereader.orchestrate.core.BookDefinition;
import com.thomsonreuters.uscl.ereader.orchestrate.core.BookDefinitionKey;

public class CoreDaoTest  {
	private static final BookDefinitionKey BOOK_KEY = new BookDefinitionKey("titleId");
	private static final BookDefinition BOOK_DEFINITION = new BookDefinition(BOOK_KEY);

	private SessionFactory mockSessionFactory;
	private org.hibernate.classic.Session mockSession;
	private CoreDaoImpl dao;
	
	@Before
	public void setUp() throws Exception {
		
		this.mockSessionFactory = EasyMock.createMock(SessionFactory.class);
		this.mockSession = EasyMock.createMock(org.hibernate.classic.Session.class);
		this.dao = new CoreDaoImpl(mockSessionFactory);
	}
	
	@Test
	public void testFindBookDefinition() {
		EasyMock.expect(mockSessionFactory.getCurrentSession()).andReturn(mockSession);
		EasyMock.expect(mockSession.get(BookDefinition.class, BOOK_KEY)).andReturn(BOOK_DEFINITION);
		EasyMock.replay(mockSessionFactory);
		EasyMock.replay(mockSession);
		
		BookDefinition actualBookDefinition = dao.findBookDefinition(BOOK_KEY);
		Assert.assertEquals(BOOK_DEFINITION, actualBookDefinition);
		
		EasyMock.verify(mockSessionFactory);
		EasyMock.verify(mockSession);
	}
}
