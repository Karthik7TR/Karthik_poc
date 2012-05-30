/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.core.outage.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.thomsonreuters.uscl.ereader.core.outage.dao.OutageDao;
import com.thomsonreuters.uscl.ereader.core.outage.domain.PlannedOutage;
import com.thomsonreuters.uscl.ereader.core.outage.domain.PlannedOutageContainer;


public class OutageServiceTest  {
	private List<PlannedOutage> PLANNED_OUTAGE_LIST;

	private OutageDao mockDao;
	private OutageServiceImpl service;
	
	@Before
	public void setUp() throws Exception {
		this.mockDao = EasyMock.createMock(OutageDao.class);
		
		this.service = new OutageServiceImpl();
		this.service.setOutageDao(mockDao);
		this.service.setPlannedOutageContainer(new PlannedOutageContainer());
		
		PlannedOutage outage = new PlannedOutage();
		PLANNED_OUTAGE_LIST = new ArrayList<PlannedOutage>();
		PLANNED_OUTAGE_LIST.add(outage);
	}
	
	@Test
	public void testGetAllActiveAndScheduledPlannedOutages() {

		EasyMock.expect(mockDao.getAllActiveAndScheduledPlannedOutages()).andReturn(PLANNED_OUTAGE_LIST);
		EasyMock.replay(mockDao);

		List<PlannedOutage> actual = service.getAllActiveAndScheduledPlannedOutages();
		Assert.assertEquals(PLANNED_OUTAGE_LIST, actual);
		
		EasyMock.verify(mockDao);
	}
	
	@Test
	public void testGetAllPlannedOutages() {

		EasyMock.expect(mockDao.getAllPlannedOutages()).andReturn(PLANNED_OUTAGE_LIST);
		EasyMock.replay(mockDao);

		List<PlannedOutage> actual = service.getAllPlannedOutages();
		Assert.assertEquals(PLANNED_OUTAGE_LIST, actual);
		
		EasyMock.verify(mockDao);
	}
	
	@Test
	public void testFindPlannedOutageByPrimaryKey() {
		Long id = 99L;
		PlannedOutage outage = new PlannedOutage();
		outage.setId(id);
		
		EasyMock.expect(mockDao.findPlannedOutageByPrimaryKey(id)).andReturn(outage);
		EasyMock.replay(mockDao);

		PlannedOutage actual = service.findPlannedOutageByPrimaryKey(id);
		Assert.assertEquals(outage, actual);
		
		EasyMock.verify(mockDao);
	}
	
	@Test
	public void testPlannedOutageContainer() {
		PlannedOutageContainer container = service.getPlannedOutageContainer();
		Assert.assertNotNull(container);
		PlannedOutage outage = new PlannedOutage();
		long id = 1965;
		outage.setId(id);
		Date startTime = new Date(0);
		outage.setStartTime(startTime);
		Date endTime = new Date(1000);
		outage.setEndTime(endTime);
		container.save(outage);
		Date midTime = new Date(500);
		
		// Check the finding of an outage
		PlannedOutage foundOutage = container.findOutage(midTime);
		Assert.assertNotNull(foundOutage);
		Assert.assertEquals(startTime, foundOutage.getStartTime());
		Assert.assertEquals(endTime, foundOutage.getEndTime());
		
		// Check expired
		Date expiredTime = new Date(1200);
		PlannedOutage expiredOutage = container.findExpiredOutage(expiredTime);
		Assert.assertNotNull(expiredOutage);
		Assert.assertEquals(endTime, expiredOutage.getEndTime());
	}	
}
