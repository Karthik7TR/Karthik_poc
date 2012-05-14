package com.thomsonreuters.uscl.ereader.mgr.dao;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.JdbcTemplate;

public class ManagerDaoImpl implements ManagerDao {
	private static final Logger log = Logger.getLogger(ManagerDaoImpl.class);
	private JdbcTemplate jdbcTemplate;
	
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
				jdbcTemplate.update(sql);
			} catch (Exception e) {
				log.error(String.format("Error deleting old Spring Batch job record using SQL: %s - %s", sql, e.getMessage()));
			}
		}
	}

	@Required
	public void setJdbcTemplate(JdbcTemplate template) {
		this.jdbcTemplate = template;
	}
}

