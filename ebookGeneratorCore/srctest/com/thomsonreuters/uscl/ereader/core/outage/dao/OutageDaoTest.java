/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.core.outage.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.easymock.EasyMock;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.thomsonreuters.uscl.ereader.core.outage.domain.PlannedOutage;

public class OutageDaoTest  {
	private List<PlannedOutage> PLANNED_OUTAGE_LIST;

	private SessionFactory mockSessionFactory;
	private org.hibernate.Session mockSession;
	private Criteria mockCriteria;
	private OutageDaoImpl dao;
	
	@Before
	public void setUp() throws Exception {
		this.mockSessionFactory = EasyMock.createMock(SessionFactory.class);
		this.mockSession = EasyMock.createMock(org.hibernate.Session.class);
		this.mockCriteria = EasyMock.createMock(Criteria.class);
		this.dao = new OutageDaoImpl(mockSessionFactory);
		
		PlannedOutage outage = new PlannedOutage();
		PLANNED_OUTAGE_LIST = new ArrayList<PlannedOutage>();
		PLANNED_OUTAGE_LIST.add(outage);
	}
	
	@Test
	public void testGetAllActiveAndScheduledPlannedOutages() {
		EasyMock.expect(mockSessionFactory.getCurrentSession()).andReturn(mockSession);
		EasyMock.expect(mockSession.createCriteria(PlannedOutage.class)).andReturn(mockCriteria);
		EasyMock.expect(mockCriteria.add(Restrictions.ge("endTime", EasyMock.anyObject(Date.class)))).andReturn(mockCriteria);
		EasyMock.expect(mockCriteria.addOrder(EasyMock.anyObject(Order.class))).andReturn(mockCriteria);
		EasyMock.expect(mockCriteria.list()).andReturn(PLANNED_OUTAGE_LIST);
		EasyMock.replay(mockSessionFactory);
		EasyMock.replay(mockSession);
		EasyMock.replay(mockCriteria);
		
		List<PlannedOutage> actual = dao.getAllActiveAndScheduledPlannedOutages();
		Assert.assertEquals(PLANNED_OUTAGE_LIST, actual);
		
		EasyMock.verify(mockSessionFactory);
		EasyMock.verify(mockSession);
		EasyMock.verify(mockCriteria);
	}
	
	@Test
	public void testGetAllPlannedOutages() {
		EasyMock.expect(mockSessionFactory.getCurrentSession()).andReturn(mockSession);
		EasyMock.expect(mockSession.createCriteria(PlannedOutage.class)).andReturn(mockCriteria);
		EasyMock.expect(mockCriteria.addOrder(EasyMock.anyObject(Order.class))).andReturn(mockCriteria);
		EasyMock.expect(mockCriteria.list()).andReturn(PLANNED_OUTAGE_LIST);
		EasyMock.replay(mockSessionFactory);
		EasyMock.replay(mockSession);
		EasyMock.replay(mockCriteria);
		
		List<PlannedOutage> actual = dao.getAllPlannedOutages();
		Assert.assertEquals(PLANNED_OUTAGE_LIST, actual);
		
		EasyMock.verify(mockSessionFactory);
		EasyMock.verify(mockSession);
		EasyMock.verify(mockCriteria);
	}
	
	@Test
	public void TestFindPlannedOutageByPrimaryKey() {
		Long id = 99L;
		PlannedOutage outage = new PlannedOutage();
		outage.setId(id);
		
		EasyMock.expect(mockSessionFactory.getCurrentSession()).andReturn(mockSession);
		EasyMock.expect(mockSession.get(PlannedOutage.class, id)).andReturn(outage);
		EasyMock.replay(mockSessionFactory);
		EasyMock.replay(mockSession);
		
		PlannedOutage actual = dao.findPlannedOutageByPrimaryKey(id);
		Assert.assertEquals(outage, actual);
		
		EasyMock.verify(mockSessionFactory);
		EasyMock.verify(mockSession);
	}
	
}
