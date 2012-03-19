/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.core.job.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.DataAccessException;

import com.thomsonreuters.uscl.ereader.core.job.domain.JobRequest;

public class JobRequestDaoImpl implements JobRequestDao {
	
	//private static final Logger log = Logger.getLogger(JobRequestDaoImpl.class);
	private SessionFactory sessionFactory;
	
	public JobRequestDaoImpl(SessionFactory sessionFactory){
		this.sessionFactory = sessionFactory;
	}
	
	@SuppressWarnings("unchecked")
	public List<JobRequest> findAllJobRequests() {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(JobRequest.class);
		return criteria.list();
	}
	
	public JobRequest getJobRequestByBookDefinitionId(long ebookDefinitionId){
		@SuppressWarnings("unchecked")
		List<JobRequest> jobRequestList = sessionFactory.getCurrentSession().createCriteria(JobRequest.class)
		 .add( Restrictions.eq("ebookDefinitionId", ebookDefinitionId)).list();

		if (jobRequestList == null)
		{
			return null;
		}else{
			if(jobRequestList.size() > 0){
				return jobRequestList.get(0);	
			}else{
				return null;
			}
		}
	}
	
	@Override
	public JobRequest findByPrimaryKey(long jobRequestId) throws DataAccessException {
		Session session = sessionFactory.getCurrentSession();
		JobRequest jobRequest = (JobRequest) session.get(JobRequest.class, jobRequestId);
		
		return jobRequest;  
	}
	
	@Override
	public void deleteJobByJobId(long jobRequestId) {
		Session session = sessionFactory.getCurrentSession();
		session.delete(findByPrimaryKey(jobRequestId));
		session.flush();
		
	}
	
	@Override
	public void updateJobPriority(long jobRequestId, int jobPriority) {
		JobRequest jobRequest = findByPrimaryKey(jobRequestId);
		jobRequest.setPriority(jobPriority);
		Session session = sessionFactory.getCurrentSession();
		session.save(jobRequest);
	}
	
	@Override
	public Long saveJobRequest(JobRequest jobRequest) {		
		Session session = sessionFactory.getCurrentSession();
		return (Long) session.save(jobRequest);
	}
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
}

