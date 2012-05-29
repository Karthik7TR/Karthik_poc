package com.thomsonreuters.uscl.ereader.core.outage.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.thomsonreuters.uscl.ereader.core.outage.domain.OutageType;
import com.thomsonreuters.uscl.ereader.core.outage.domain.PlannedOutage;



@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@Transactional
public class PlannedOutageIntegrationTest {
	//private static Logger log = Logger.getLogger(PlannedOutageIntegrationTest.class);
	private static final Long OUTAGE_TYPE_ID = 99999L;
	private static final Date DATE = new Date();
	private OutageType OUTAGE_TYPE;
	
	@Autowired
	protected OutageService outageService;

	@Before
	public void setUp() throws Exception {
		OUTAGE_TYPE = new OutageType();
		OUTAGE_TYPE.setId(OUTAGE_TYPE_ID);
		OUTAGE_TYPE.setLastUpdated(DATE);
		OUTAGE_TYPE.setSubSystem("sub system");
		OUTAGE_TYPE.setSystem("system");
		outageService.saveOutageType(OUTAGE_TYPE);
	}
	
	@Test
	public void createPlannedOutage() {
		OutageType type = outageService.findOutageTypeByPrimaryKey(OUTAGE_TYPE_ID);
		
		PlannedOutage outage = new PlannedOutage();
		outage.setStartTime(DATE);
		outage.setEndTime(DATE);
		outage.setLastUpdated(DATE);
		outage.setOperation(PlannedOutage.Operation.SAVE);
		outage.setOutageType(type);
		outage.setReason("Test");
		outage.setUpdatedBy("Me");
		
		outageService.savePlannedOutage(outage);
		
		PlannedOutage actual = outageService.findPlannedOutageByPrimaryKey(outage.getId());
		
		Assert.assertEquals(outage, actual);
	}
	
	@Test
	public void testList() {
		OutageType type = outageService.findOutageTypeByPrimaryKey(OUTAGE_TYPE_ID);
		
		for(int i = -2; i < 4; i++){
			Calendar cal = Calendar.getInstance();
			cal.setTime(DATE);
			cal.add(Calendar.HOUR, i);
			
			PlannedOutage outage = new PlannedOutage();
			outage.setStartTime(cal.getTime());
			outage.setEndTime(cal.getTime());
			outage.setLastUpdated(DATE);
			outage.setOperation(PlannedOutage.Operation.SAVE);
			outage.setOutageType(type);
			outage.setReason("Test");
			outage.setUpdatedBy("Me");
			outageService.savePlannedOutage(outage);
		}
		
		List<PlannedOutage> activeOutages = outageService.getAllActiveAndScheduledPlannedOutages();
		Assert.assertEquals(3, activeOutages.size());
		
		List<PlannedOutage> allOutages = outageService.getAllPlannedOutages();
		Assert.assertEquals(6, allOutages.size());
	}
}
