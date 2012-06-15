/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.support.dao;

import java.util.ArrayList;
import java.util.List;

import org.easymock.EasyMock;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.hibernate.criterion.Order;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.thomsonreuters.uscl.ereader.support.domain.SupportPageLink;

public class SupportPageLinkDaoTest  {
	private final SupportPageLink SUPPORT_PAGE_LINK = new SupportPageLink();
	private final Long SUPPORT_PAGE_LINK_ID = new Long("1");
	private final List<SupportPageLink> ALL_SUPPORT_PAGE_LINK = new ArrayList<SupportPageLink>();


	private SessionFactory mockSessionFactory;
	private Session mockSession;
	private Criteria mockCriteria;
	private SupportPageLinkDaoImpl dao;
	
	@Before
	public void setUp() throws Exception {
		
		this.mockSessionFactory = EasyMock.createMock(SessionFactory.class);
		this.mockSession = EasyMock.createMock(Session.class);
		this.mockCriteria = EasyMock.createMock(Criteria.class);
		this.dao = new SupportPageLinkDaoImpl(mockSessionFactory);
		
		SUPPORT_PAGE_LINK.setId(SUPPORT_PAGE_LINK_ID);
	}
	
	@Test
	public void testFindByPrimaryKey() {
		EasyMock.expect(mockSessionFactory.getCurrentSession()).andReturn(mockSession);
		EasyMock.expect(mockSession.get(SupportPageLink.class, SUPPORT_PAGE_LINK_ID)).andReturn(SUPPORT_PAGE_LINK);
		EasyMock.replay(mockSessionFactory);
		EasyMock.replay(mockSession);
		
		SupportPageLink actualSupportPageLink = dao.findByPrimaryKey(SUPPORT_PAGE_LINK_ID);
		SupportPageLink expected = new SupportPageLink();
		expected.setId(SUPPORT_PAGE_LINK_ID);
		
		Assert.assertEquals(expected, actualSupportPageLink);
		
		EasyMock.verify(mockSessionFactory);
		EasyMock.verify(mockSession);
	}
	
	@Test
	public void testGetAllSupportPageLinks() {
		ALL_SUPPORT_PAGE_LINK.add(SUPPORT_PAGE_LINK);
		EasyMock.expect(mockSessionFactory.getCurrentSession()).andReturn(mockSession);
		EasyMock.expect(mockSession.createCriteria(SupportPageLink.class)).andReturn(mockCriteria);
		EasyMock.expect(mockCriteria.addOrder( EasyMock.anyObject(Order.class))).andReturn(mockCriteria);
		EasyMock.expect(mockCriteria.list()).andReturn(ALL_SUPPORT_PAGE_LINK);
		EasyMock.replay(mockSessionFactory);
		EasyMock.replay(mockSession);
		EasyMock.replay(mockCriteria);
		
		List<SupportPageLink> actualSupportPageLink = dao.findAllSupportPageLink();
		List<SupportPageLink> expectedSupportPageLinks = new ArrayList<SupportPageLink>();
		expectedSupportPageLinks.add(SUPPORT_PAGE_LINK);
		Assert.assertEquals(expectedSupportPageLinks, actualSupportPageLink);
		
		EasyMock.verify(mockSessionFactory);
		EasyMock.verify(mockSession);
		EasyMock.verify(mockCriteria);
	}
}
