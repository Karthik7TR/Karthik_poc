package com.thomsonreuters.uscl.ereader.mgr.dao;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.JdbcTemplate;

import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobRequest;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.util.EBookServerException;

public class ManagerDaoImpl implements ManagerDao {
	private static final Logger log = Logger.getLogger(ManagerDaoImpl.class);
	private SessionFactory sessionFactory;
	private JdbcTemplate jdbcTemplate;
	private JobExplorer jobExplorer;
	
	@Override
	public JobExecution findRunningJobExecution(Long bookDefinitionId, String bookVersion) {
		Set<JobExecution> runningJobs = jobExplorer.findRunningJobExecutions(JobRequest.JOB_NAME_CREATE_EBOOK);
		for (JobExecution jobExec : runningJobs) {
			JobParameters params = jobExec.getJobInstance().getJobParameters();
			Long bookDefIdParamValue = params.getLong(JobParameterKey.BOOK_DEFINITION_ID);
			String bookVersionParamValue = params.getString(JobParameterKey.BOOK_VERSION_SUBMITTED);
			if (bookDefinitionId.equals(bookDefIdParamValue) && bookVersion.equals(bookVersionParamValue)) {
				return jobExec;
			}
		}
		return null;
	}
	
	/**
	 * Archive and delete Spring Batch records older than the specified date.
	 * @return the number of job step executions that were archived/deleted
	 */
	@Override
	public int archiveAndDeleteSpringBatchJobRecordsBefore(Date deleteBefore) {
		Object[] args = { deleteBefore };
		int[] argTypes = { Types.TIMESTAMP };
		List<Long> stepExecutionIds = new ArrayList<Long>();
		
		String TIME_COMPARISON_COLUMN_NAME = "CREATE_TIME";
		
		// EXECUTIONS to be cleaned up.  Using create_time for time comparison because it is non-null and
		// because jobs that are currently starting will not have a start time yet.
		List<Long> jobExecutionIds = jdbcTemplate.queryForList(
					"select job_execution_id from batch_job_execution where (" + TIME_COMPARISON_COLUMN_NAME + " < ?)",
									args, argTypes, Long.class);

		if (jobExecutionIds.size() > 0) {  // if no executions, there will be no instance or step data
			// INSTANCES to be cleaned up
			List<Long> jobInstanceIds = jdbcTemplate.queryForList(
					"select unique job_instance_id from batch_job_execution where (" + TIME_COMPARISON_COLUMN_NAME + " < ?)",
									args, argTypes, Long.class);

			// STEPS to be cleaned up, fetching them exec id, by exec id
			for (Long jobExecutionId : jobExecutionIds) {
				List<Long> stepExecutionIdsForJobExecutionId = jdbcTemplate.queryForList(
					String.format("select step_execution_id from batch_step_execution where (job_execution_id = %d)", jobExecutionId), 
					Long.class);
				stepExecutionIds.addAll(stepExecutionIdsForJobExecutionId);
			}
			
			if (log.isDebugEnabled()) {
				Collections.sort(jobInstanceIds);
				Collections.sort(jobExecutionIds);
				Collections.sort(stepExecutionIds);	
				log.debug("jobInstanceIds: " + jobInstanceIds);
				log.debug("jobExecutionIds: " + jobExecutionIds);
				log.debug("stepExecutionIds: " + stepExecutionIds);
			}

			// Save off the old step data to the JOB_HISTORY table
			archiveJobStepDataToJobHistoryTable(stepExecutionIds);
	
			// Delete the old Spring Batch job instance/execution/step rows
			deleteSpringBatchRecords("delete from BATCH_JOB_PARAMS where (JOB_INSTANCE_ID = %d)", jobInstanceIds);
			deleteSpringBatchRecords("delete from BATCH_JOB_EXECUTION_CONTEXT where (JOB_EXECUTION_ID = %d)", jobExecutionIds);
			deleteSpringBatchRecords("delete from BATCH_STEP_EXECUTION_CONTEXT where (STEP_EXECUTION_ID = %d)", stepExecutionIds);
			deleteSpringBatchRecords("delete from BATCH_STEP_EXECUTION where (JOB_EXECUTION_ID = %d)", jobExecutionIds);
			deleteSpringBatchRecords("delete from BATCH_JOB_EXECUTION where (JOB_EXECUTION_ID = %d)", jobExecutionIds);
			deleteSpringBatchRecords("delete from BATCH_JOB_INSTANCE where (JOB_INSTANCE_ID = %d)", jobInstanceIds);
		}
		return stepExecutionIds.size();
	}
	
	@Override
	public void deletePlannedOutagesBefore(Date deleteBefore) {
		Session session = sessionFactory.getCurrentSession();
		String hql = "delete from PlannedOutage where endTime < :deleteBefore";
		Query query = session.createQuery(hql);
		query.setDate("deleteBefore", deleteBefore);
		query.executeUpdate();
	}

	/**
	 * Delete document_metadata and image_metadata records older than the specified date.
	 * @param numberLastMajorVersionKept the number of versions to keep
	 * @param daysBeforeDocMetadataDelete the number of days to subtract from current date before deleting data
	 */	
	@Override
	public void deleteTransientMetadata(int numberLastMajorVersionKept, int daysBeforeDocMetadataDelete) {


		/* If we have both 1.1 and 1.0 as successful runs then just keep 1.1. 
		 * However, if we have 1.1 and 2.0 then we need to keep both if numberLastMajorVersionKept is set to 2.
		 * If numberLastMajorVersionKept is set to 1 then for 1.1 and 2.0 keep only 2.0 
		 * In any case, also keep the last version run. 
		 * Use the daysBeforeDocMetadataDelete date restriction to avoid books in progress. 
		 * Note that the image deletes are driven off the document_metadata table so there
		 * is the potential for orphan rows in image if the document_metadata delete is successful
		 * but the image delete is not. */

		
		// JobIds of versions to be cleaned up
		List<Long> jobInstanceIds = new ArrayList<Long>();

		
		 // Get VERSIONS and JOB INSTANCES to be cleaned up. (Sort order is CRITICAL)  
		StringBuffer hql = new StringBuffer("select distinct dm.job_instance_id, ps.ebook_definition_id, book_version_submitted, publish_status ");
		hql.append(" from publishing_stats ps, document_metadata dm " );
		hql.append(" where DM.JOB_INSTANCE_ID = ps.job_instance_id " );
		hql.append(" and ps.last_updated < sysdate - " );
		hql.append(daysBeforeDocMetadataDelete);
		hql.append(" order by ebook_definition_id, book_version_submitted desc, job_instance_id desc "  );
   	
		try
		{
			Session session = sessionFactory.getCurrentSession();

			Query query = session.createSQLQuery(hql.toString());
			@SuppressWarnings("unchecked")
			List<Object[]> objectList = query.list();

			long prevBookId = (long) 0;
			long prevMajorVersion = (long) 0;
			int maxPublishedCntr = 0;			
			int maxVersionKept = numberLastMajorVersionKept;

			for(Object[] arr : objectList)
			{
				long jobId;
				long bookId;
				long majorVersion; 
				try 
				{
					jobId = Long.parseLong(arr[0].toString());
					bookId = Long.parseLong(arr[1].toString());
					String fullVersion = arr[2].toString();
					majorVersion = (long) 0;
					if (fullVersion.contains(".")) // after the version # change
					{
						String[] version = fullVersion.split("\\.", -1);
						majorVersion = Long.parseLong(version[0]);
					} 
					else 
					{
						majorVersion = Long.parseLong(fullVersion);
					}
				} 
				catch (NumberFormatException e) 
				{
					log.error("Encountered a version which is not a valid number in document metadata for book " + arr[1].toString() + " job " + arr[0].toString() + " version " + arr[2].toString());
					continue;
				}
				String status = "NO_STATUS";

				if (arr[3] != null)
				{
					status = arr[3].toString();
				}
				if (prevBookId == bookId && 
						prevMajorVersion == majorVersion
						&& (!status.equals("Publish Step Completed")
								||	 maxPublishedCntr >= maxVersionKept)
				)
				{
					// Add to list to be deleted
					jobInstanceIds.add(jobId); 
					log.debug("Delete book " + bookId + " jobId " + jobId + " major version " + majorVersion + " status " + status);
				}
				else
				{
//					log.debug("Keep book " + bookId + " jobId " + jobId + " major version " + majorVersion + " status " + status);
					if (prevBookId != bookId)
					{
						maxPublishedCntr = 0;
					}
					// only increment a successful publish 
					// so if we have a version 1.0 success, 1.1 success but a version 1.2 fail and then a version 2.0
					// we would want to keep all version 1.1, 1.2 and 2.0.
					if( status.equals("Publish Step Completed"))
					{
						maxPublishedCntr++;
					}
					prevBookId = bookId;
					prevMajorVersion = majorVersion;
				}
			}
		}
		catch(Exception e)
		{
			log.error("Failed to get list of docmetadata to delete", e);
		}	
		
		// Delete the old document_metadata rows by job instance
		if(jobInstanceIds.size() > 0)
		{
			deleteSpringBatchRecords("delete from IMAGE_METADATA where (JOB_INSTANCE_ID = %d)", jobInstanceIds);
			deleteSpringBatchRecords("delete from DOCUMENT_METADATA where (JOB_INSTANCE_ID = %d)", jobInstanceIds);
		}
		log.debug("Deleted records for " +  jobInstanceIds.size() + " job ids from document_metadata and image_metadata");

	}


	/**
	 * Archive data from the BATCH_STEP_EXECUTION table into the JOB_HISTORY table.
	 * This is data that is to be preserved following the deletion of the BATCH_* table data.
	 * No exceptions are thrown and all errors will only be logged.
	 * @param stepExecutionIds steps whose data is to be archived.
	 */
	private void archiveJobStepDataToJobHistoryTable(List<Long> stepExecutionIds) {
		StringBuffer sqlTemplate = new StringBuffer();
		sqlTemplate.append("INSERT INTO job_history ");
		sqlTemplate.append("(step_execution_id, step_name, job_instance_id, job_execution_id, step_start_time, step_end_time, step_exit_code, step_exit_message) ");
		sqlTemplate.append("SELECT se.step_execution_id, se.step_name, je.job_instance_id, se.job_execution_id, se.start_time, se.end_time, se.exit_code, se.exit_message ");
		sqlTemplate.append("FROM batch_step_execution se, batch_job_execution je ");
		sqlTemplate.append("WHERE (se.step_execution_id = %d) and (se.job_execution_id = je.job_execution_id)");
		for (Long stepId : stepExecutionIds) {
			try {
				String sql = String.format(sqlTemplate.toString(), stepId);
				jdbcTemplate.update(sql);
			} catch (Exception e)  {
				log.error(String.format("Error archiving job step ID %d to the JOB_HISTORY table - %s", stepId, e.getMessage()));
			}
		}
	}

	/**
	 * Delete records from varying tables using an sql template.
	 * @param sqlTemplate a delete statement what will accept a primary key
	 * @param ids the list of primary key's suitable for the provided delete statement
	 */
	private void deleteSpringBatchRecords(String sqlTemplate, List<Long> ids) {
		for (Long id : ids) {
			String sql = null;
			try {
				sql = String.format(sqlTemplate, id);
//				log.debug("sql: " + sql);
				jdbcTemplate.update(sql);
			} catch (Exception e) {
				log.error(String.format("Error deleting old Spring Batch job record using SQL: %s - %s", sql, e.getMessage()));
			}
		}
	}

	@Required
	public void setSessionFactory(SessionFactory factory){
		this.sessionFactory = factory;
	}
	@Required
	public void setJdbcTemplate(JdbcTemplate template) {
		this.jdbcTemplate = template;
	}
	@Required
	public void setJobExplorer(JobExplorer explorer) {
		this.jobExplorer = explorer;
	}
}
