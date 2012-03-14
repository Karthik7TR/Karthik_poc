package com.thomsonreuters.uscl.ereader.core.job.dao;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.DataAccessException;

import com.thomsonreuters.uscl.ereader.core.job.domain.JobRequest;

public class JobRequestDaoImpl implements JobRequestDao {
	
	private static final Logger log = Logger.getLogger(JobRequestDaoImpl.class);
	private SessionFactory sessionFactory;
	
	public JobRequestDaoImpl(SessionFactory sessionFactory){
		this.sessionFactory = sessionFactory;
		
	}
	
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
	
	
	@Override
	public List<JobRequest> getAllJobRequestsBy(String jobStatus,
			int jobPriority, Date jobScheduledTime, String jobSubmittersName) {
		
		String namedQuery = "findJobRequestByGivenCriteria";
		String allJobsWithCriteria = sessionFactory.getCurrentSession().getNamedQuery(namedQuery).getQueryString();

		boolean andFlag = false;
		
		if(checkIfStringIsValid(jobStatus)){
			allJobsWithCriteria += " where myJobRequest.jobStatus = " + jobStatus;
			andFlag = true;
		}
		
		if(jobPriority != 0){
			if (andFlag){
				allJobsWithCriteria += " and myJobRequest.jobPriority = "+jobPriority;
			}else{
				allJobsWithCriteria += " where myJobRequest.jobPriority = "+jobPriority;
				andFlag = true;
			}
		}
		
		if(jobScheduledTime != null){
			if (andFlag){
				allJobsWithCriteria += " and myJobRequest.jobScheduleTimeStamp = "+jobScheduledTime;
			}else{
				allJobsWithCriteria += " where myJobRequest.jobScheduleTimeStamp = "+jobScheduledTime;
				andFlag = true;
			}
		}

		if(checkIfStringIsValid(jobSubmittersName)){
			if (andFlag){
				allJobsWithCriteria += " and myJobRequest.jobSubmittersName = "+ "'"+jobSubmittersName+"'";
			}else{
				allJobsWithCriteria += " where myJobRequest.jobSubmittersName = "+"'"+jobSubmittersName+"'";
				andFlag = true;
			}
		}

		log.debug("findJobRequestByGivenCriteria = "+allJobsWithCriteria);
		
//		String allJobsWithCriteria = sessionFactory.getCurrentSession().getNamedQuery(namedQuery).getQueryString();
		Query query = sessionFactory.getCurrentSession().createQuery(allJobsWithCriteria);
		return query.list();
	}
	
	private boolean checkIfStringIsValid(String strObject){
		boolean flag = false;
		if(strObject != null && !strObject.equals("")){
			
			flag = true;
		}
		return flag;
	}

	@SuppressWarnings("unchecked")
	public List<JobRequest> getAllJobRequests() {
		String namedQuery = "findAllJobRequests";	
		String allJobRequests = sessionFactory.getCurrentSession().getNamedQuery(namedQuery).getQueryString();
		Query query = sessionFactory.getCurrentSession().createQuery(allJobRequests);				
		return query.list();
	}

	
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
	

	@Override
	public JobRequest findJobRequestByRequestId(long jobRequestId) throws DataAccessException {
		Session session = sessionFactory.getCurrentSession();
		JobRequest jobRequest = (JobRequest) session.get(JobRequest.class, jobRequestId);
		
		return jobRequest;  
	}
	
	@Override
	public void deleteJobByJobId(long jobRequestId) {
		Session session = sessionFactory.getCurrentSession();
		session.delete(findJobRequestByRequestId(jobRequestId));
		session.flush();
		
	}
	
	@Override
	public void updateJobPriority(long jobRequestId ,int jobPriority) {
		
		JobRequest jobRequest = findJobRequestByRequestId(jobRequestId);
		jobRequest.setJobPriority(jobPriority);
		Session session = sessionFactory.getCurrentSession();
		session.save(jobRequest);
		
	}

	@Override
	public void saveJobRequest(JobRequest jobRequest) {		
		
		Session session = sessionFactory.getCurrentSession();
		session.save(jobRequest);
		session.flush();
		
	}
	
	public SessionFactory getSessionFactory() {
		
		return sessionFactory;
	}
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		
		this.sessionFactory = sessionFactory;
	}
	

}

