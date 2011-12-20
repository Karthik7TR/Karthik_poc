/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.orchestrate.core.dao;

import java.util.Collections;
import java.util.List;

import org.easymock.EasyMock;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.thomsonreuters.uscl.ereader.orchestrate.core.BookDefinition;
import com.thomsonreuters.uscl.ereader.orchestrate.core.BookDefinitionKey;

public class CoreDaoTest  {
	private static final BookDefinitionKey BOOK_KEY = new BookDefinitionKey("titleId", 1234l);
	private static final BookDefinition BOOK_DEFINITION = new BookDefinition(BOOK_KEY);

	private SessionFactory mockSessionFactory;
	private org.hibernate.classic.Session mockSession;
	private Criteria mockCriteria;
	private CoreDaoImpl dao;
	
	@Before
	public void setUp() throws Exception {
		
		this.mockSessionFactory = EasyMock.createMock(SessionFactory.class);
		this.mockSession = EasyMock.createMock(org.hibernate.classic.Session.class);
		this.mockCriteria = EasyMock.createMock(Criteria.class);
		this.dao = new CoreDaoImpl();
		dao.setSessionFactory(mockSessionFactory);
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
	
	@Test
	public void testFindAllBookDefinitions() {
		EasyMock.expect(mockSessionFactory.getCurrentSession()).andReturn(mockSession);
		EasyMock.expect(mockSession.createCriteria(BookDefinition.class)).andReturn(mockCriteria);
		EasyMock.expect(mockCriteria.list()).andReturn(Collections.EMPTY_LIST);
		EasyMock.replay(mockSessionFactory);
		EasyMock.replay(mockSession);
		EasyMock.replay(mockCriteria);
		
		List<BookDefinition> actualBookDefinitions = dao.findAllBookDefinitions();
		Assert.assertEquals(Collections.EMPTY_LIST, actualBookDefinitions);
		
		EasyMock.verify(mockSessionFactory);
		EasyMock.verify(mockSession);
		EasyMock.verify(mockCriteria);
	}
}
