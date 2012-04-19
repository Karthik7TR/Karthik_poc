/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.orchestrate.engine.dao;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import com.thomsonreuters.uscl.ereader.orchestrate.engine.domain.JobStartupThrottle;

/**
 *  	
 * @author Mahendra Survase (u0105927)
 *
 */
public class JobStartupThrottleDaoImpl implements JobStartupThrottleDao {
	//private static final Logger log = Logger.getLogger(JobStartupThrottleDaoImpl.class);
	
	public SessionFactory sessionFactory;
	
	public JobStartupThrottleDaoImpl(SessionFactory hibernateSessionFactory) {
		this.sessionFactory = hibernateSessionFactory;
	}
	
	@Override
	public int getThrottleLimitForExecutionStep(String throttleStep) {
		final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(JobStartupThrottle.class);
        criteria.add(Restrictions.eq("throttleStepName", throttleStep));
        JobStartupThrottle jobStartupThrottle = (JobStartupThrottle) criteria.uniqueResult();
        return jobStartupThrottle.getThrottleLimit();
	}

	@Override
	public int getThrottleLimitForCurrentTimeAndExecutionStep(int militaryTime,
			String throttleStep) {
		// TODO this method needs to be implemented once we decide to implement throttling around bell curve . 
		return 0;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
}

