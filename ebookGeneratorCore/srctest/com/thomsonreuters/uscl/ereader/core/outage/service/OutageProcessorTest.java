/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.core.outage.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.mail.internet.InternetAddress;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.thomsonreuters.uscl.ereader.core.outage.domain.PlannedOutage;
import com.thomsonreuters.uscl.ereader.core.outage.domain.PlannedOutageContainer;
import com.thomsonreuters.uscl.ereader.userpreference.service.UserPreferenceService;


public class OutageProcessorTest  {
	private List<PlannedOutage> PLANNED_OUTAGE_LIST;
	private static InternetAddress STATIC_ADDR_1;
	private static InternetAddress STATIC_ADDR_2;
	private UserPreferenceService mockUserPreferenceService;
	private OutageProcessorImpl service;
	
	
	@Before
	public void setUp() throws Exception {
		STATIC_ADDR_1 = new InternetAddress("foo@tr.com");
		STATIC_ADDR_2 = new InternetAddress("bar@tr.com");
		this.mockUserPreferenceService = EasyMock.createMock(UserPreferenceService.class);
		
		this.service = new OutageProcessorImpl();
		this.service.setPlannedOutageContainer(new PlannedOutageContainer());
		this.service.setUserPreferenceService(mockUserPreferenceService);
		this.service.setStaticEmailRecipients(STATIC_ADDR_1.getAddress()+","+STATIC_ADDR_2.getAddress());
		
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
	
	@Test
	/**
	 * Test the creation of the recipient list for outage notification.
	 * This is a combination of a static csv addr list as specified by a Spring property, and the dynamic
	 * list as fetched from the unique set of user email addresses in the user preferences
	 * @throws Exception
	 */
	public void testGetOutageEmailRecipients() throws Exception {
		Set<InternetAddress> dynamicAddrs = new HashSet<InternetAddress>();
		InternetAddress dynamicAddr1 = new InternetAddress("aaa@tr.com");
		InternetAddress dynamicAddr2 = new InternetAddress("bbb@tr.com");
		InternetAddress dynamicAddr3 = new InternetAddress("bbb@tr.com");  // intentionally duplicate, should be excluded from unique set
		dynamicAddrs.add(dynamicAddr1);
		dynamicAddrs.add(dynamicAddr2);
		dynamicAddrs.add(dynamicAddr3);
		EasyMock.expect(mockUserPreferenceService.findAllUniqueEmailAddresses()).andReturn(dynamicAddrs);
		EasyMock.replay(mockUserPreferenceService);
		
		Set<InternetAddress> actualAddrs = service.getOutageEmailRecipients();
		Assert.assertNotNull(actualAddrs);
		Assert.assertEquals(4, actualAddrs.size());
		Assert.assertTrue(actualAddrs.contains(STATIC_ADDR_1));
		Assert.assertTrue(actualAddrs.contains(STATIC_ADDR_2));
		Assert.assertTrue(actualAddrs.contains(dynamicAddr1));
		Assert.assertTrue(actualAddrs.contains(dynamicAddr2));
		
		EasyMock.verify(mockUserPreferenceService);
		
	}
}
