/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.core.job.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Transactional;

import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobRequest;

public class JobRequestDaoImpl implements JobRequestDao {
	
	//private static final Logger log = Logger.getLogger(JobRequestDaoImpl.class);
	private SessionFactory sessionFactory;
	
	public JobRequestDaoImpl(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	@Override
	public void deleteJobRequest(long jobRequestId) {
		Session session = sessionFactory.getCurrentSession();
		session.delete(findByPrimaryKey(jobRequestId));
		session.flush();
	}
	
	@SuppressWarnings("unchecked")
	public List<JobRequest> findAllJobRequests() {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(JobRequest.class);
		return criteria.list();
	}
	
	@Override
	public JobRequest findByPrimaryKey(long jobRequestId) throws DataAccessException {
		Session session = sessionFactory.getCurrentSession();
		JobRequest jobRequest = (JobRequest) session.get(JobRequest.class, jobRequestId);
		
		return jobRequest;  
	}
	
	@Override
	public JobRequest findJobRequestByBookDefinitionId(long ebookDefinitionId){
		@SuppressWarnings("unchecked")
		List<JobRequest> jobRequestList = sessionFactory.getCurrentSession().createCriteria(JobRequest.class)
		 .add(Restrictions.eq("bookDefinition.ebookDefinitionId", ebookDefinitionId)).list();

		if (jobRequestList.size() > 0) {
			return jobRequestList.get(0);	
		}
		return null;
	}
	
	@Override
	public Long saveJobRequest(JobRequest jobRequest) {		
		Session session = sessionFactory.getCurrentSession();
		return (Long) session.save(jobRequest);
	}
	
	
	@Override
	public void updateJobPriority(long jobRequestId, int jobPriority) {
		JobRequest jobRequest = findByPrimaryKey(jobRequestId);
		jobRequest.setPriority(jobPriority);
		Session session = sessionFactory.getCurrentSession();
		session.save(jobRequest);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<JobRequest> findAllJobRequestsOrderByPriorityAndSubmitedtime() {
		StringBuffer hql = new StringBuffer(
				"select jr from JobRequest jr order by job_priority desc,job_submit_timestamp asc");

		
		// Create query and populate it with where clause values
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery(hql.toString());

		return (List<JobRequest>) query.list();
	}
}

