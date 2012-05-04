/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.core.job.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author <a href="mailto:Mahendra.Survase@thomsonreuters.com">Mahendra Survase</a> u0105927
 */
public class JobCleanupDaoImpl implements JobCleanupDao {
	
	private static final Logger log = Logger.getLogger(JobCleanupDaoImpl.class);
	private SessionFactory sessionFactory;
	
	
	public JobCleanupDaoImpl(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	/**
	 * Gets list of dead jobs , so that the job owners could be notified to resubmit these jobs.   
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public ArrayList<String> findListOfDeadJobs() {
		StringBuffer hql = new StringBuffer(
		  "Select ps.job_submitter_name,ed.title_id ,ed.proview_display_name");   
			hql.append(" from batch_job_execution  bje");
			hql.append(" inner join publishing_stats ps on ps.job_instance_id =  bje.job_instance_id");
			hql.append(" inner join ebook_definition ed on ed.ebook_definition_id = ps.ebook_definition_id");
			hql.append(" where bje.exit_code = 'UNKNOWN'");
		
		Session session = sessionFactory.getCurrentSession();
		
		Query query = session.createSQLQuery(hql.toString());
		List<Object[]> objectList = query.list();
		Map<String, String> docMap = new HashMap<String, String>();
		
		ArrayList arrayList = new ArrayList<String>();
		for(Object[] arr : objectList)
		{
			if (arr[1] != null) 
			{
				String temp = arr[0].toString()+","+arr[1].toString() +","+arr[2].toString();
				arrayList.add(temp);
			}
		}
		return arrayList;
		
	}
	
	
	/**
	 * Update dead job exit status to "failed".  
	 * @return
	 */
	@Override
	//	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public int updateBatchJobExecution() {

		StringBuffer hql= new StringBuffer("update batch_job_execution set ");
			hql.append(" exit_code = 'FAILED',");
			hql.append(" STATUS = 'FAILED',");
			hql.append(" END_TIME = sysdate,");
			hql.append(" EXIT_MESSAGE = 'APPEARED TO BE A DEAD JOB. SET EXIT_CODE AND STATUS TO FAILED'");
			hql.append(" where exit_code = 'UNKNOWN'");
				
		Session session = sessionFactory.getCurrentSession();

		Query query = session.createSQLQuery(hql.toString());

		int result = 0;
		try {
			result = query.executeUpdate();
			session.flush();
		} catch (HibernateException e) {
			e.printStackTrace();
		}

		return result;
	}
	
	
	/**
	 * Update dead steps exit status to "failed".  
	 * @return
	 */
	@Override
//	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public int updateBatchStepExecution() {
				
		StringBuffer hql= new StringBuffer("update batch_step_execution set");
		hql.append(" EXIT_CODE = 'FAILED',");
		hql.append(" STATUS = 'FAILED',");
		hql.append(" END_TIME = sysdate,");
		hql.append(" EXIT_MESSAGE = 'APPEARED TO BE A DEAD JOB. SET EXIT_CODE AND STATUS TO FAILED',");
		hql.append(" LAST_UPDATED = sysdate");
		hql.append(" where exit_code = 'EXECUTING'");
		hql.append(" and job_execution_id in (select  job_instance_id  from batch_job_execution where exit_code ='UNKNOWN')" );

		Session session = sessionFactory.getCurrentSession();
		Query query = session.createSQLQuery(hql.toString());


		int result = 0;
		try {
			result = query.executeUpdate();
			session.flush();
		} catch (HibernateException e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 	 
	 * Update dead job exit status to 'failed' for given server name
	 * @param serverName
	 * @return
	 */
	@Override
	public int updateBatchJobExecutionForGivenServer(String serverName){
		
		StringBuffer hql = new StringBuffer();
		hql.append("update batch_job_execution set "); 
		hql.append("exit_code = 'FAILED', ");
		hql.append("STATUS = 'FAILED', ");
		hql.append("END_TIME = sysdate, "); 
		hql.append("EXIT_MESSAGE = 'APPEARED TO BE A DEAD JOB. SET EXIT_CODE AND STATUS TO FAILED' "); 
		hql.append("where job_instance_id in ");
		hql.append("( Select bje.job_instance_id from batch_job_execution bje "); 
		hql.append("inner join publishing_stats ps on ps.job_instance_id = bje.job_instance_id ");
		hql.append("where bje.exit_code ='UNKNOWN' and ps.job_host_name ='"+serverName+"')");
		
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createSQLQuery(hql.toString());
		int result = 0;
		try{
			result = query.executeUpdate();
			session.flush();
			
		}catch (HibernateException e) {
			e.printStackTrace();
		}
		
//		log.debug("On server start up number of Batch jobs updated ="+result);
	return result;	
	}
	
	/**
	 * Update dead step exit status to 'failed' for given server name
	 * @param serverName
	 * @return
	 */
	@Override
	public int updateBatchStepExecutionForGivenServer(String serverName) {
		StringBuffer hql = new StringBuffer();
		hql.append("update batch_step_execution set "); 
		hql.append("EXIT_CODE = 'FAILED', STATUS = 'FAILED', ");
		hql.append("END_TIME = sysdate, EXIT_MESSAGE = 'APPEARED TO BE A DEAD JOB. SET EXIT_CODE AND STATUS TO FAILED', ");
		hql.append("LAST_UPDATED = sysdate ");
		hql.append("where end_time is null and job_execution_id in "); 
		hql.append("(Select bje.job_execution_id from batch_job_execution bje "); 
		hql.append("inner join publishing_stats ps on ps.job_instance_id = bje.job_instance_id ");
		hql.append("where bje.exit_code ='UNKNOWN' and ps.job_host_name ='"+serverName+"')");
		
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createSQLQuery(hql.toString());
		int result = 0;
		try {
			result = query.executeUpdate();
			session.flush();
		}catch(HibernateException e){
			e.printStackTrace();
		}
//		log.debug("On server start up number of Batch job-step updated ="+result);
		return result;
	}

	/**
	 * Gets list of dead jobs for given serverName, so that the job owners could be notified to resubmit those jobs.   
	 * @return
	 */
	@Override
	public ArrayList<String> findListOfDeadJobsByServerName(String serverName) {
		StringBuffer hql = new StringBuffer(
				  "Select ps.job_submitter_name,ed.title_id ,ed.proview_display_name");   
					hql.append(" from batch_job_execution  bje");
					hql.append(" inner join publishing_stats ps on ps.job_instance_id =  bje.job_instance_id");
					hql.append(" inner join ebook_definition ed on ed.ebook_definition_id = ps.ebook_definition_id");
					hql.append(" where bje.exit_code = 'UNKNOWN'");
					hql.append(" and ps.job_host_name='"+serverName+"'");
				
				Session session = sessionFactory.getCurrentSession();
				
				Query query = session.createSQLQuery(hql.toString());
				List<Object[]> objectList = query.list();
				Map<String, String> docMap = new HashMap<String, String>();
				
				ArrayList arrayList = new ArrayList<String>();
				for(Object[] arr : objectList)
				{
					if (arr[1] != null) 
					{
						String temp = arr[0].toString()+","+arr[1].toString() +","+arr[2].toString();
						arrayList.add(temp);
					}
				}
				return arrayList;
				
			}
	
	
}
