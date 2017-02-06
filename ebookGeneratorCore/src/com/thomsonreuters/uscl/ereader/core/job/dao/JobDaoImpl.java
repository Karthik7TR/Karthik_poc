/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.core.job.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.batch.core.BatchStatus;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Transactional;

import com.thomsonreuters.uscl.ereader.core.job.domain.JobFilter;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobSort;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobSort.SortProperty;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobSummary;

public class JobDaoImpl implements JobDao {
	
	private static final Logger log = LogManager.getLogger(JobDaoImpl.class);
	private static final JobSummaryRowMapper JOB_SUMMARY_ROW_MAPPER = new JobSummaryRowMapper();
	private static final JobExecutionIdRowMapper JOB_EXECUTION_ID_ROW_MAPPER = new JobExecutionIdRowMapper();
	private JdbcTemplate jdbcTemplate;
	
	@Override
	public int getStartedJobCount() {
		String sql = String.format("select count(*) from BATCH_JOB_EXECUTION where (status = '%s') or (status = '%s')",
								   BatchStatus.STARTING.toString(), BatchStatus.STARTED.toString());
		int count = jdbcTemplate.queryForObject(sql, Integer.class);
		return count;
	}

	@Override
	public List<JobSummary> findJobSummary(List<Long> jobExecutionIds) {
		List<JobSummary> list = new ArrayList<>(jobExecutionIds.size());
		for (long jobExecutionId : jobExecutionIds) {
			StringBuilder sql = new StringBuilder("select auditTable.EBOOK_DEFINITION_ID, auditTable.PROVIEW_DISPLAY_NAME, auditTable.SOURCE_TYPE, auditTable.TITLE_ID, execution.JOB_INSTANCE_ID, ");
			sql.append("execution.JOB_EXECUTION_ID, execution.STATUS, execution.START_TIME, execution.END_TIME, stats.JOB_SUBMITTER_NAME from \n ");
			sql.append("BATCH_JOB_EXECUTION execution, PUBLISHING_STATS stats, EBOOK_AUDIT auditTable ");
			sql.append("where ");
			sql.append(String.format("(execution.JOB_EXECUTION_ID = %d) and ", jobExecutionId));
			sql.append("(execution.JOB_INSTANCE_ID = stats.JOB_INSTANCE_ID(+)) and ");
			sql.append("(stats.AUDIT_ID = auditTable.AUDIT_ID(+))");
//			sql.append("(auditTable.PROVIEW_DISPLAY_NAME is not null)");  // Do not fetch rows that appear to be garbage
			List<JobSummary> rows = jdbcTemplate.query(sql.toString(), JOB_SUMMARY_ROW_MAPPER);
			if (rows.isEmpty()) {
				log.debug(String.format("Job Execution ID %d was not found", jobExecutionId));
			} else if (rows.size() == 1) {
				JobSummary summary = rows.get(0);
				list.add(summary);
			} else {
				log.error(String.format("%d rows were unexpectedly returned!", rows.size()));
			}
		}
		return list;
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<Long> findJobExecutions(JobFilter filter, JobSort sort) {
		List<Long> jobExecutionIds = null;
		StringBuffer sql = new StringBuffer("select execution.JOB_EXECUTION_ID from ");
		sql.append("BATCH_JOB_EXECUTION execution ");
		if (filter.hasAnyBookProperties() || sort.isSortingOnBookProperty()) {
			sql.append(", PUBLISHING_STATS stats, EBOOK_AUDIT auditTable ");
		}
		sql.append("where ");
		
		sql.append(addFiltersToQuery(filter, sort));
		
		String orderByColumn = getOrderByColumnName(sort.getSortProperty());
		sql.append(String.format("order by %s %s", orderByColumn, sort.getSortDirection()));
		
//log.debug("SQL: " + sql.toString());
		Object[] args = argumentsAddToFilter(filter, sort);
		
		jobExecutionIds = jdbcTemplate.query(sql.toString(), JOB_EXECUTION_ID_ROW_MAPPER, args);
		
		return jobExecutionIds;
	}
	
	/**
	 * Generate sql query that matches the filters user inputs
	 * @param filter
	 * @return
	 */
	private String addFiltersToQuery(JobFilter filter, JobSort sort) {
		StringBuffer sql = new StringBuffer();
		
		if (filter.getFrom() != null) {
		/* 	We want to show jobs that are in the STARTING status even if they have no start time because
			these are restarted jobs that are waiting for a thread (from the pool) to run and have not yet
			began to actually execute.  They will remain in the STARTING status until another job completes
			so the task can come off the ThreadPoolTaskExecutor blocking queue and be assigned a thread 
			from the pool when a thread frees up. */
			sql.append(String.format("((execution.STATUS = '%s') or (execution.START_TIME >= ?)) and ",
						BatchStatus.STARTING.toString()));
		}
		if (filter.getTo() != null) {
			sql.append("(execution.START_TIME < ?) and ");
		}
		if (filter.getBatchStatus() != null && filter.getBatchStatus().length > 0) {
			StringBuffer csvStatus = new StringBuffer();
			boolean firstTime = true;
			for (BatchStatus status : filter.getBatchStatus()) {
				if (!firstTime) {
					csvStatus.append(",");
				}
				firstTime = false;
				csvStatus.append(String.format("'%s'", status.toString()));
			}
			sql.append(String.format("(execution.STATUS in (%s)) and ", csvStatus));
		}
		if (filter.hasAnyBookProperties() || sort.isSortingOnBookProperty()) {
			sql.append("(execution.JOB_INSTANCE_ID = stats.JOB_INSTANCE_ID(+)) and ");
			sql.append("(stats.AUDIT_ID = auditTable.AUDIT_ID(+)) and ");
			
			if (StringUtils.isNotBlank(filter.getBookName())) {
				sql.append("(UPPER(auditTable.PROVIEW_DISPLAY_NAME) like UPPER(?)) and ");
			}
			if (StringUtils.isNotBlank(filter.getTitleId())) {
				sql.append("(UPPER(auditTable.TITLE_ID) like UPPER(?)) and ");
			}
			if (StringUtils.isNotBlank(filter.getSubmittedBy())) {
				sql.append("(UPPER(stats.JOB_SUBMITTER_NAME) like UPPER(?)) and ");
			}
		}
		sql.append("(1=1) "); // end of WHERE clause, ensure proper SQL syntax
		
		return sql.toString();
	}
	
	/**
	 * Creates the arguments list to use in jdbcTemplate.query.
	 * The order of the arguements being added needs to match the filter order in 
	 * @param filter
	 * @return
	 */
	private Object[] argumentsAddToFilter(JobFilter filter, JobSort sort) {
		List<Object> args = new ArrayList<Object>();

		if (filter.getFrom() != null) {
			args.add(filter.getFrom());
		}
		if (filter.getTo() != null) {
			args.add(filter.getTo());
		}
		if (filter.hasAnyBookProperties() || sort.isSortingOnBookProperty()) {
			if (StringUtils.isNotBlank(filter.getBookName())) {
				args.add(filter.getBookName());
			}
			if (StringUtils.isNotBlank(filter.getTitleId())) {
				args.add(filter.getTitleId());
			}
			if (StringUtils.isNotBlank(filter.getSubmittedBy())) {
				args.add(filter.getSubmittedBy());
			}
		}
		return args.toArray();
	}

	/**
	 * Map the sort column enumeration into the actual column identifier used in the HQL query.
	 * @param sortProperty enumerated value that reflects the database table sort column to sort on.
	 */
	private String getOrderByColumnName(SortProperty sortProperty) {
		switch (sortProperty) {
			case JOB_EXECUTION_ID:
				return "execution.JOB_EXECUTION_ID";
			case JOB_INSTANCE_ID:
				return "execution.JOB_INSTANCE_ID";
			case BATCH_STATUS:
				return "execution.STATUS";
			case START_TIME:
				return "execution.START_TIME";
			case BOOK_NAME:
				return "auditTable.PROVIEW_DISPLAY_NAME";
			case TITLE_ID:
				return "auditTable.TITLE_ID";
			case SOURCE_TYPE:
				return "auditTable.SOURCE_TYPE";
			case SUBMITTED_BY:
				return "stats.JOB_SUBMITTER_NAME";
			default:
				throw new IllegalArgumentException("Unexpected sort property: " + sortProperty);
		}
	}
	
	@Required
	public void setJdbcTemplate(JdbcTemplate template) {
		this.jdbcTemplate = template;
	}
}

class JobSummaryRowMapper implements RowMapper<JobSummary> {
	public JobSummary mapRow(ResultSet resultSet, int rowNum) throws SQLException {
		Long bookDefinitionId = resultSet.getLong("EBOOK_DEFINITION_ID");
		String bookName = resultSet.getString("PROVIEW_DISPLAY_NAME");
		String titleId = resultSet.getString("TITLE_ID");
		String sourceType = resultSet.getString("SOURCE_TYPE");
		Long jobExecutionId = resultSet.getLong("JOB_EXECUTION_ID");
		Long jobInstanceId = resultSet.getLong("JOB_INSTANCE_ID");
		BatchStatus batchStatus = BatchStatus.valueOf(resultSet.getString("STATUS"));
		String submittedBy = resultSet.getString("JOB_SUBMITTER_NAME");	// Username of user who started the job
		Date startTime = resultSet.getTimestamp("START_TIME");
		Date endTime = resultSet.getTimestamp("END_TIME");
		JobSummary js = new JobSummary(bookDefinitionId, bookName, titleId, sourceType, jobInstanceId, jobExecutionId, batchStatus,
										submittedBy, startTime, endTime);
		return js;
	}
}

class JobExecutionIdRowMapper implements RowMapper<Long> {
	public Long mapRow(ResultSet resultSet, int rowNum) throws SQLException {
		Long id = resultSet.getLong("JOB_EXECUTION_ID");
		return id;
	}
}
