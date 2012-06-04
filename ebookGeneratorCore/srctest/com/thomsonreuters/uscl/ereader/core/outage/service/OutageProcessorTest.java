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

import com.thomsonreuters.uscl.ereader.core.outage.domain.PlannedOutage;
import com.thomsonreuters.uscl.ereader.core.service.CoreService;
import com.thomsonreuters.uscl.ereader.userpreference.service.UserPreferenceService;


public class OutageProcessorTest  {
	private List<PlannedOutage> PLANNED_OUTAGE_LIST;
	private UserPreferenceService mockUserPreferenceService;
	private CoreService mockCoreService;
	private OutageProcessorImpl service;
	
	
	@Before
	public void setUp() throws Exception {
		this.mockUserPreferenceService = EasyMock.createMock(UserPreferenceService.class);
		this.mockCoreService = EasyMock.createMock(CoreService.class);
		
		this.service = new OutageProcessorImpl();
		this.service.setUserPreferenceService(mockUserPreferenceService);
		this.service.setCoreService(mockCoreService);
		
		PlannedOutage outage = new PlannedOutage();
		PLANNED_OUTAGE_LIST = new ArrayList<PlannedOutage>();
		PLANNED_OUTAGE_LIST.add(outage);
	}
	
	@Test
	public void testPlannedOutageContainer() {
		PlannedOutage outage = new PlannedOutage();
		long id = 1965;
		outage.setId(id);
		Date startTime = new Date(0);
		outage.setStartTime(startTime);
		Date endTime = new Date(1000);
		outage.setEndTime(endTime);
		service.addPlannedOutageToContainer(outage);
		Date midTime = new Date(500);
		
		// Check the finding of an outage
		PlannedOutage foundOutage = service.findPlannedOutageInContainer(midTime);
		Assert.assertNotNull(foundOutage);
		Assert.assertEquals(startTime, foundOutage.getStartTime());
		Assert.assertEquals(endTime, foundOutage.getEndTime());
		
		// Check expired
		Date expiredTime = new Date(1200);
		PlannedOutage expiredOutage = service.findExpiredOutageInContainer(expiredTime);
		Assert.assertNotNull(expiredOutage);
		Assert.assertEquals(endTime, expiredOutage.getEndTime());
	}
	

}
