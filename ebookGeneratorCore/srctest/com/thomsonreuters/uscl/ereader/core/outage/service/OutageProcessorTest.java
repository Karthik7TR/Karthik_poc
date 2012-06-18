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
	private static PlannedOutage OUTAGE_1;
	private static PlannedOutage OUTAGE_2;
	
	
	@Before
	public void setUp() throws Exception {
		this.mockUserPreferenceService = EasyMock.createMock(UserPreferenceService.class);
		this.mockCoreService = EasyMock.createMock(CoreService.class);
		
		this.service = new OutageProcessorImpl();
		this.service.setUserPreferenceService(mockUserPreferenceService);
		this.service.setCoreService(mockCoreService);
		
		OUTAGE_1 = new PlannedOutage();
		OUTAGE_1.setId(new Long(1965));
		OUTAGE_1.setStartTime(new Date(0));
		OUTAGE_1.setEndTime(new Date(1000));
		service.addPlannedOutageToContainer(OUTAGE_1);
		
		OUTAGE_2 = new PlannedOutage();
		OUTAGE_2.setId(new Long(1966));
		OUTAGE_2.setStartTime(new Date(5000));
		OUTAGE_2.setEndTime(new Date(6000));
		service.addPlannedOutageToContainer(OUTAGE_2);

		PlannedOutage outage = new PlannedOutage();
		PLANNED_OUTAGE_LIST = new ArrayList<PlannedOutage>();
		PLANNED_OUTAGE_LIST.add(outage);
	}
	
	@Test
	public void testPlannedOutageContainer() {
		
		// Check the finding of an outage
		Date midOutage1 = new Date(500);
		PlannedOutage foundOutage = service.findPlannedOutageInContainer(midOutage1);
		Assert.assertNotNull(foundOutage);
		Assert.assertEquals(OUTAGE_1, foundOutage);
		Assert.assertEquals(OUTAGE_1.getStartTime(), foundOutage.getStartTime());
		Assert.assertEquals(OUTAGE_1.getEndTime(), foundOutage.getEndTime());
		
		// Check expired
		Date expiredTime = new Date(1200);
		PlannedOutage expiredOutage = service.findExpiredOutageInContainer(expiredTime);
		Assert.assertNotNull(expiredOutage);
		Assert.assertEquals(OUTAGE_1, expiredOutage);
		Assert.assertEquals(OUTAGE_1.getEndTime(), expiredOutage.getEndTime());
		
		// Remove and verify removal
		service.deletePlannedOutageFromContainer(expiredOutage);
		foundOutage = service.findPlannedOutageInContainer(midOutage1);
		Assert.assertNull(foundOutage);
		
		// Find the later outage after the first one has been removed
		Date midOutage2 = new Date(5500);
		foundOutage = service.findPlannedOutageInContainer(midOutage2);
		Assert.assertNotNull(foundOutage);
		Assert.assertEquals(OUTAGE_2, foundOutage);
		Assert.assertEquals(OUTAGE_2.getStartTime(), foundOutage.getStartTime());
		Assert.assertEquals(OUTAGE_2.getEndTime(), foundOutage.getEndTime());
		
		// Verify that that later outage is now the only one that remains after it has expired
		expiredTime = new Date(6500);
		expiredOutage = service.findExpiredOutageInContainer(expiredTime);
		Assert.assertNotNull(expiredOutage);
		Assert.assertEquals(OUTAGE_2, expiredOutage);

	}
}
