/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.core.job.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.transaction.annotation.Transactional;

import com.thomsonreuters.uscl.ereader.core.job.domain.JobUserInfo;
import com.thomsonreuters.uscl.ereader.util.EBookServerException;

/**
 * 
 * @author <a href="mailto:Mahendra.Survase@thomsonreuters.com">Mahendra Survase</a> u0105927
 */
public class JobCleanupDaoImpl implements JobCleanupDao {
	private static final String DEAD_JOB_MESSAGE = "Dead Job detected - updated status and exit codes";
	private static final String BATCH_STATUS_FAILED = BatchStatus.FAILED.toString();
	private static final String BATCH_STATUS_ABANDONED = BatchStatus.ABANDONED.toString();
	private static final String EXIT_STATUS_FAILED = ExitStatus.FAILED.getExitCode();
	private static final String EXIT_STATUS_UNKNOWN = ExitStatus.UNKNOWN.getExitCode();
	private static final String EXIT_STATUS_EXECUTING = ExitStatus.EXECUTING.getExitCode();
	
	private static final Logger log = LogManager.getLogger(JobCleanupDaoImpl.class);
	private SessionFactory sessionFactory;
	
	
	public JobCleanupDaoImpl(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	/**
	 * Gets list of dead jobs , so that the job owners could be notified to resubmit these jobs.   
	 * @return
	 * @throws EBookServerException 
	 */
	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public List<JobUserInfo> findListOfDeadJobs() throws EBookServerException {
		StringBuffer hql = new StringBuffer(
		  "Select ps.job_submitter_name,ed.title_id ,ed.proview_display_name");   
			hql.append(" from batch_job_execution  bje");
			hql.append(" inner join publishing_stats ps on ps.job_instance_id =  bje.job_instance_id");
			hql.append(" inner join ebook_definition ed on ed.ebook_definition_id = ps.ebook_definition_id");
			hql.append(String.format(" where bje.exit_code = '%s'", EXIT_STATUS_UNKNOWN));

		try{			
			Session session = sessionFactory.getCurrentSession();
			
			Query query = session.createSQLQuery(hql.toString());
			List<Object[]> objectList = query.list();
			
			List<JobUserInfo> arrayList = new ArrayList<JobUserInfo>();
			for(Object[] arr : objectList)
			{
				if (arr[1] != null) 
				{
					JobUserInfo jobUserInfo = new JobUserInfo(arr[0].toString(),arr[1].toString(),arr[2].toString());
					arrayList.add(jobUserInfo);
				}
			}
			return arrayList;
		}catch(Exception e){
			log.error(e);
			throw new EBookServerException("Failed to get list of dead job(s).");
		}
		
	}
	
	
	/**
	 * Update dead job exit status.
	 * @return
	 * @throws EBookServerException 
	 */
	@Override
	public int updateBatchJobExecution() throws EBookServerException {

		StringBuffer hql= new StringBuffer("update batch_job_execution set ");
			hql.append(String.format(" exit_code = '%s',", EXIT_STATUS_FAILED));
			hql.append(String.format(" STATUS = '%s',", BATCH_STATUS_ABANDONED));
			hql.append(" END_TIME = sysdate,");
			hql.append(String.format(" EXIT_MESSAGE = '%s' ", DEAD_JOB_MESSAGE));
			hql.append(String.format(" where exit_code = '%s'", EXIT_STATUS_UNKNOWN));
				
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createSQLQuery(hql.toString());
		int result = 0;
		try {
			result = query.executeUpdate();
			session.flush();
		} catch (HibernateException e) {
			log.error(e);
			throw new EBookServerException("Failed to update dead batch job(s).");

		}
		return result;
	}
	
	/**
	 * Update dead steps exit status.
	 * @return
	 * @throws EBookServerException 
	 */
	@Override
	public int updateBatchStepExecution() throws EBookServerException {
				
		StringBuffer hql= new StringBuffer("update batch_step_execution bse set");
		hql.append(String.format(" bse.EXIT_CODE = '%s',", EXIT_STATUS_FAILED));
		hql.append(String.format(" bse.STATUS = '%s',", BATCH_STATUS_FAILED));
		hql.append(" bse.END_TIME = sysdate,");
		hql.append(String.format(" bse.EXIT_MESSAGE = '%s',", DEAD_JOB_MESSAGE));
		hql.append(" bse.LAST_UPDATED = sysdate");
		hql.append(String.format(" where bse.exit_code = '%s'", EXIT_STATUS_EXECUTING));
		hql.append(String.format(" and bse.job_execution_id in (select  bje.job_execution_id  from batch_job_execution bje where bje.exit_code = '%s')", EXIT_STATUS_UNKNOWN ));

		Session session = sessionFactory.getCurrentSession();
		Query query = session.createSQLQuery(hql.toString());
		int result = 0;
		try {
			result = query.executeUpdate();
			session.flush();
		} catch (HibernateException e) {
			log.error(e);
			throw new EBookServerException("Failed to update job steps in batch_step_execution table.");

		}
		return result;
	}

	/**
	 * 	 
	 * Update dead job exit status for given server name.
	 * @param serverName
	 * @return
	 * @throws EBookServerException 
	 */
	@Override
	public int updateBatchJobExecutionForGivenServer(String serverName) throws EBookServerException{
		StringBuffer hql = new StringBuffer();
		hql.append("update batch_job_execution set "); 
		hql.append(String.format("exit_code = '%s', ", EXIT_STATUS_FAILED));
		hql.append(String.format("STATUS = '%s', ", BATCH_STATUS_ABANDONED));
		hql.append("END_TIME = sysdate, "); 
		hql.append(String.format("EXIT_MESSAGE = '%s' ", DEAD_JOB_MESSAGE)); 
		hql.append("where job_instance_id in ");
		hql.append("( Select bje.job_instance_id from batch_job_execution bje "); 
		hql.append("inner join publishing_stats ps on ps.job_instance_id = bje.job_instance_id ");
		hql.append(String.format("where bje.exit_code = '%s' and ps.job_host_name = '%s')", EXIT_STATUS_UNKNOWN, serverName));

		Session session = sessionFactory.getCurrentSession();
		Query query = session.createSQLQuery(hql.toString());
		int result = 0;
		try {
			result = query.executeUpdate();
			session.flush();
		} catch (HibernateException e) {
			log.error(e);
			throw new EBookServerException("Failed to update job(s) in batch_job_execution table. For given serverName ="+serverName);

		}
		return result;	
	}
	
	/**
	 * Update dead step exit status for given server name.
	 * @param serverName
	 * @return
	 * @throws EBookServerException 
	 */
	@Override
	public int updateBatchStepExecutionForGivenServer(String serverName) throws EBookServerException {
		StringBuffer hql = new StringBuffer();
		hql.append("update batch_step_execution set "); 
		hql.append(String.format("EXIT_CODE = '%s', STATUS = '%s', ", EXIT_STATUS_FAILED, BATCH_STATUS_FAILED));
		hql.append(String.format("END_TIME = sysdate, EXIT_MESSAGE = '%s', ", DEAD_JOB_MESSAGE));
		hql.append("LAST_UPDATED = sysdate ");
		hql.append("where end_time is null and job_execution_id in "); 
		hql.append("(Select bje.job_execution_id from batch_job_execution bje "); 
		hql.append("inner join publishing_stats ps on ps.job_instance_id = bje.job_instance_id ");
		hql.append(String.format("where bje.exit_code = '%s' and ps.job_host_name = '%s')", EXIT_STATUS_UNKNOWN, serverName));
		
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createSQLQuery(hql.toString());
		int result = 0;
		try {
			result = query.executeUpdate();
			session.flush();
		} catch (HibernateException e) {
			log.error(e);
			throw new EBookServerException("Failed to update job step(s) in batch_step_execution table. For given serverName ="+serverName);

		}
		return result;
	}

	/**
	 * Gets list of dead jobs for given serverName, so that the job owners could be notified to resubmit those jobs.   
	 * @return
	 * @throws EBookServerException 
	 */
	@Override
	public List<JobUserInfo> findListOfDeadJobsByServerName(String serverName) throws EBookServerException {
		StringBuffer hql = new StringBuffer(
				  "Select ps.job_submitter_name,ed.title_id ,ed.proview_display_name");   
					hql.append(" from batch_job_execution  bje");
					hql.append(" inner join publishing_stats ps on ps.job_instance_id =  bje.job_instance_id");
					hql.append(" inner join ebook_definition ed on ed.ebook_definition_id = ps.ebook_definition_id");
					hql.append(String.format(" where bje.exit_code = '%s'", EXIT_STATUS_UNKNOWN));
					hql.append(String.format(" and ps.job_host_name = '%s'", serverName));
				
		try{
				Session session = sessionFactory.getCurrentSession();
				
				Query query = session.createSQLQuery(hql.toString());
				@SuppressWarnings("unchecked")
				List<Object[]> objectList = query.list();
				
				List<JobUserInfo> arrayList = new ArrayList<JobUserInfo>();
				for(Object[] arr : objectList)
				{
					if (arr[1] != null) 
					{
						JobUserInfo jobUserInfo = new JobUserInfo(arr[0].toString(),arr[1].toString(),arr[2].toString());
						arrayList.add(jobUserInfo);
					}
				}
				return arrayList;

		}catch(Exception e){
			log.error(e);
			throw new EBookServerException("Failed to get list of dead job(s) for given serverName ="+serverName);
			
		}

	}
	
}
