/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.support.service;

import java.util.ArrayList;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.thomsonreuters.uscl.ereader.support.dao.SupportPageLinkDao;
import com.thomsonreuters.uscl.ereader.support.domain.SupportPageLink;

public class SupportPageLinkServiceTest  {
	private final SupportPageLink SUPPORT_PAGE_LINK = new SupportPageLink();
	private final List<SupportPageLink> ALL_SUPPORT_PAGE_LINK = new ArrayList<SupportPageLink>();
	private final Long SUPPORT_PAGE_LINK_ID = new Long("1");
	
	private SupportPageLinkServiceImpl service;
	private SupportPageLinkDao mockDao;
	
	
	@Before
	public void setUp() throws Exception {
		this.mockDao = EasyMock.createMock(SupportPageLinkDao.class);
		
		this.service = new SupportPageLinkServiceImpl();
		service.setSupportPageLinkDao(mockDao);
		
		SUPPORT_PAGE_LINK.setId(SUPPORT_PAGE_LINK_ID);
	}
	
	@Test
	public void testFindByPrimaryKey() {
		EasyMock.expect(mockDao.findByPrimaryKey(SUPPORT_PAGE_LINK_ID)).andReturn(SUPPORT_PAGE_LINK);
		EasyMock.replay(mockDao);
		SupportPageLink actual = service.findByPrimaryKey(SUPPORT_PAGE_LINK_ID);
		Assert.assertEquals(SUPPORT_PAGE_LINK, actual);
		EasyMock.verify(mockDao);
	}
	
	@Test
	public void testFindAllSupportPageLinks() {
		ALL_SUPPORT_PAGE_LINK.add(SUPPORT_PAGE_LINK);
		EasyMock.expect(mockDao.findAllSupportPageLink()).andReturn(ALL_SUPPORT_PAGE_LINK);
		EasyMock.replay(mockDao);
		List<SupportPageLink> actual = service.findAllSupportPageLink();
		List<SupportPageLink> expected = new ArrayList<SupportPageLink>();
		expected.add(SUPPORT_PAGE_LINK);
		
		Assert.assertEquals(expected, actual);
		EasyMock.verify(mockDao);
	}
	
}
