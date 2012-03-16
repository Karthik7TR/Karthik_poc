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
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Transactional;

import com.thomsonreuters.uscl.ereader.core.job.domain.JobRequest;

/**
 * 
 * @author Mahendra Survase U0105927
 * 
 */
public class JobRequestDaoImpl implements JobRequestDao {
	
	//private static final Logger log = Logger.getLogger(JobRequestDaoImpl.class);
	private SessionFactory sessionFactory;
	
	public JobRequestDaoImpl(SessionFactory sessionFactory){
		this.sessionFactory = sessionFactory;
		
	}
	
	@Transactional
	@SuppressWarnings("unchecked")
	public List<JobRequest> findAllJobRequests() {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(JobRequest.class);
		return criteria.list();
	}
	
	@Transactional
	@Override
	public JobRequest getNextJobToExecute() {
		String namedQuery = "findNextJobRequestToRun";	
		String nextJobRequests = sessionFactory.getCurrentSession().getNamedQuery(namedQuery).getQueryString();
		Query query = sessionFactory.getCurrentSession().createQuery(nextJobRequests);
		
		JobRequest jobRequestToRun = null;
		if (query == null)
		{
			jobRequestToRun= null;
		}else{
			if(query.list().size() > 0){
				jobRequestToRun= (JobRequest)query.list().get(0);	
			}else{
				jobRequestToRun= null;
			}
				
		}
		
		return jobRequestToRun;
	
	}
	
//	@Override
//	public List<JobRequest> getAllJobRequestsBy(String jobStatus,
//			int jobPriority, Date jobScheduledTime, String jobSubmittersName) {
//		
//		String namedQuery = "findJobRequestByGivenCriteria";
//		String allJobsWithCriteria = sessionFactory.getCurrentSession().getNamedQuery(namedQuery).getQueryString();
//
//		boolean andFlag = false;
//		
//		if(checkIfStringIsValid(jobStatus)){
//			allJobsWithCriteria += " where myJobRequest.jobStatus = " + jobStatus;
//			andFlag = true;
//		}
//		
//		if(jobPriority != 0){
//			if (andFlag){
//				allJobsWithCriteria += " and myJobRequest.jobPriority = "+jobPriority;
//			}else{
//				allJobsWithCriteria += " where myJobRequest.jobPriority = "+jobPriority;
//				andFlag = true;
//			}
//		}
//		
//		if(jobScheduledTime != null){
//			if (andFlag){
//				allJobsWithCriteria += " and myJobRequest.jobScheduleTimeStamp = "+jobScheduledTime;
//			}else{
//				allJobsWithCriteria += " where myJobRequest.jobScheduleTimeStamp = "+jobScheduledTime;
//				andFlag = true;
//			}
//		}
//
//		if(checkIfStringIsValid(jobSubmittersName)){
//			if (andFlag){
//				allJobsWithCriteria += " and myJobRequest.jobSubmittersName = "+ "'"+jobSubmittersName+"'";
//			}else{
//				allJobsWithCriteria += " where myJobRequest.jobSubmittersName = "+"'"+jobSubmittersName+"'";
//				andFlag = true;
//			}
//		}
//
//		log.debug("findJobRequestByGivenCriteria = "+allJobsWithCriteria);
//		
//		Query query = sessionFactory.getCurrentSession().createQuery(allJobsWithCriteria);
//		return query.list();
//	}
	
//	private boolean checkIfStringIsValid(String strObject){
//		boolean flag = false;
//		if(strObject != null && !strObject.equals("")){
//			
//			flag = true;
//		}
//		return flag;
//	}
	
	@Transactional
	public JobRequest getJobRequestByBookDefinationId(long ebookDefinitionId){
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
	
	@Transactional
	@Override
	public JobRequest findByPrimaryKey(long jobRequestId) throws DataAccessException {
		Session session = sessionFactory.getCurrentSession();
		JobRequest jobRequest = (JobRequest) session.get(JobRequest.class, jobRequestId);
		
		return jobRequest;  
	}
	
	@Transactional
	@Override
	public void deleteJobByJobId(long jobRequestId) {
		Session session = sessionFactory.getCurrentSession();
		session.delete(findByPrimaryKey(jobRequestId));
		session.flush();
		
	}
	
	@Transactional
	@Override
	public void updateJobPriority(long jobRequestId, int jobPriority) {
		JobRequest jobRequest = findByPrimaryKey(jobRequestId);
		jobRequest.setPriority(jobPriority);
		Session session = sessionFactory.getCurrentSession();
		session.save(jobRequest);
	}
	
	@Transactional
	@Override
	public Long saveJobRequest(JobRequest jobRequest) {		
		Session session = sessionFactory.getCurrentSession();
		return (Long) session.save(jobRequest);
	}
	
	public SessionFactory getSessionFactory() {
		
		return sessionFactory;
	}
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		
		this.sessionFactory = sessionFactory;
	}
}

