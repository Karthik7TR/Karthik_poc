/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.core.outage.service;

import java.util.ArrayList;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.thomsonreuters.uscl.ereader.core.outage.dao.OutageDao;
import com.thomsonreuters.uscl.ereader.core.outage.domain.PlannedOutage;


public class OutageServiceTest  {
	private List<PlannedOutage> PLANNED_OUTAGE_LIST;


	private OutageDao mockDao;
	private OutageServiceImpl service;
	
	@Before
	public void setUp() throws Exception {
		this.mockDao = EasyMock.createMock(OutageDao.class);
		
		this.service = new OutageServiceImpl();
		this.service.setOutageDao(mockDao);
		
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
	
}
