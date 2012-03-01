/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.orchestrate.engine.dao;

import java.util.Collections;

import org.easymock.EasyMock;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.thomsonreuters.uscl.ereader.orchestrate.engine.domain.JobStartupThrottle;


/**
* 
*  	
* @author Mahendra Survase (u0105927)
*
*/
public class JobStartupThrottleDaoTest  {
	
	private static String THROTTLE_STEP = "formatAddHTMLWrapper";
	private SessionFactory mockSessionFactory;
	private org.hibernate.classic.Session mockSession;
	private Criteria mockCriteria;
	private JobStartupThrottleDao dao;
	private JobStartupThrottle jobThrottle;	
	
	@Before
	public void setUp() throws Exception {
		
		this.mockSessionFactory = EasyMock.createMock(SessionFactory.class);
		this.mockSession = EasyMock.createMock(org.hibernate.classic.Session.class);
		this.mockCriteria = EasyMock.createMock(Criteria.class);
		this.dao = new JobStartupThrottleDaoImpl(mockSessionFactory);
		this.jobThrottle = EasyMock.createMock(JobStartupThrottle.class);
	}
	
	@Test
	public void testGetThrottleLimitForExecutionStep() {
		
		EasyMock.expect(mockSessionFactory.getCurrentSession()).andReturn(mockSession);
		EasyMock.expect(mockSession.createCriteria(JobStartupThrottle.class)).andReturn(mockCriteria);
		EasyMock.expect(mockCriteria.add((Criterion)EasyMock.anyObject())).andReturn(mockCriteria);
		EasyMock.expect(mockCriteria.uniqueResult()).andReturn(jobThrottle);		
		EasyMock.replay(mockSessionFactory);
		EasyMock.replay(mockSession);
		EasyMock.replay(mockCriteria);
		
		int throttleLimit = dao.getThrottleLimitForExecutionStep(THROTTLE_STEP);
		Assert.assertNotNull(throttleLimit);
		
		EasyMock.verify(mockSessionFactory);
		EasyMock.verify(mockSession);
		EasyMock.verify(mockCriteria);
	}
	
}

