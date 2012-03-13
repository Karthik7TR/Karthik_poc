package com.thomsonreuters.uscl.ereader.core.job.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
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
	
	private static final Logger log = Logger.getLogger(JobDaoImpl.class);
	private static final JobSummaryRowMapper JOB_SUMMARY_ROW_MAPPER = new JobSummaryRowMapper();
	private static final JobExecutionIdRowMapper JOB_EXECUTION_ID_ROW_MAPPER = new JobExecutionIdRowMapper();
	private JdbcTemplate jdbcTemplate;
	
	@Override
	public List<JobSummary> findJobSummary(List<Long> jobExecutionIds) {

		List<JobSummary> list = new ArrayList<JobSummary>(jobExecutionIds.size());
		for (long jobExecutionId : jobExecutionIds) {
			StringBuffer sql = new StringBuffer("select auditTable.EBOOK_DEFINITION_ID, auditTable.BOOK_NAMES_CONCAT, auditTable.TITLE_ID, execution.JOB_INSTANCE_ID, ");
			sql.append("execution.JOB_EXECUTION_ID, execution.STATUS, execution.START_TIME, execution.END_TIME from \n ");
			sql.append("BATCH_JOB_EXECUTION execution, PUBLISHING_STATS stats, EBOOK_AUDIT auditTable ");
			sql.append("where ");
			sql.append(String.format("(execution.JOB_EXECUTION_ID = %d) and ", jobExecutionId));
			sql.append("(execution.JOB_INSTANCE_ID = stats.JOB_INSTANCE_ID(+)) and ");
			sql.append("(stats.AUDIT_ID = auditTable.AUDIT_ID(+))");
//log.debug("SQL: " + sql.toString());
			List<JobSummary> rows = jdbcTemplate.query(sql.toString(), JOB_SUMMARY_ROW_MAPPER);
			if (rows.size() == 0) {
				log.debug(String.format("Job Execution ID %d was not found", jobExecutionId));
			} else if (rows.size() == 1) {
				JobSummary summary = rows.get(0);
//log.debug("SUMMARY: " + summary);
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
		if (filter.getFrom() != null) {
			sql.append("(execution.START_TIME > ?) and ");
		}
		if (filter.getTo() != null) {
			sql.append("(execution.START_TIME <= ?) and ");
		}
		if (filter.getBatchStatus() != null) {
			sql.append(String.format("(execution.STATUS = '%s') and ", filter.getBatchStatus().toString()));
		}
		if (filter.hasAnyBookProperties() || sort.isSortingOnBookProperty()) {
			sql.append("(execution.JOB_INSTANCE_ID = stats.JOB_INSTANCE_ID(+)) and ");
			sql.append("(stats.AUDIT_ID = auditTable.AUDIT_ID(+)) and ");
			
			if (StringUtils.isNotBlank(filter.getBookName())) {
				sql.append(String.format("(auditTable.BOOK_NAMES_CONCAT like '%%%s%%') and ", filter.getBookName()));
			}
			if (StringUtils.isNotBlank(filter.getTitleId())) {
				sql.append(String.format("(auditTable.TITLE_ID like '%%%s%%') and ", filter.getTitleId()));
			}
		}
		sql.append("(1=1) "); // end of WHERE clause, ensure proper SQL syntax
		
		String orderByColumn = getOrderByColumnName(sort.getSortProperty());
		sql.append(String.format("order by %s %s", orderByColumn, sort.getSortDirection()));
		
log.debug("SQL: " + sql.toString());

		Object[] args = null;
		if ((filter.getFrom() != null) && (filter.getTo() != null)) {  // two args
			args = new Object[2];
			args[0] = filter.getFrom();
			args[1] = filter.getTo();
		}
		else if (filter.getFrom() != null) {
			args = new Object[1];
			args[0] = filter.getFrom();
		} else if (filter.getTo() != null) {
			args = new Object[1];
			args[0] = filter.getTo();
		}
		if (args != null) {
			jobExecutionIds = jdbcTemplate.query(sql.toString(), JOB_EXECUTION_ID_ROW_MAPPER, args);
		} else {
			jobExecutionIds = jdbcTemplate.query(sql.toString(), JOB_EXECUTION_ID_ROW_MAPPER);
		}
		return jobExecutionIds;
	}	
	
//	@Override
//	@SuppressWarnings("unchecked")
//	@Transactional(readOnly = true)
//	public List<Long> OLD_findJobExecutions(JobFilter filter, JobSort sort) {
//		
//		StringBuffer hql = new StringBuffer("select execution.jobExecutionId from ");
//		hql.append("JobExecutionEntity execution ");
//		if (filter.hasAnyBookProperties() || sort.isSortingOnBookProperty()) {
//			hql.append(", PublishingStats stats, EbookAudit audit ");
//		}
//		hql.append("where ");
//		if (filter.getFrom() != null) {
//			hql.append("(execution.startTime > :fromDate) and ");
//		}
//		if (filter.getTo() != null) {
//			hql.append("(execution.startTime <= :toDate) and ");
//		}
//		if (filter.getBatchStatus() != null) {
//			hql.append(String.format("(execution.batchStatus = '%s') and ", filter.getBatchStatus().toString()));
//		}
//		if (filter.hasAnyBookProperties() || sort.isSortingOnBookProperty()) {
//			hql.append("(execution.jobInstanceId = stats.jobInstanceId) and ");
//			hql.append("(stats.auditId = audit.auditId) and ");
//			
//			if (StringUtils.isNotBlank(filter.getBookName())) {
//				hql.append(String.format("(audit.bookNamesConcat like '%%%s%%') and ", filter.getBookName()));
//			}
//			if (StringUtils.isNotBlank(filter.getTitleId())) {
//				hql.append(String.format("(audit.titleId like '%%%s%%') and ", filter.getTitleId()));
//			}
//		}
//		hql.append("(1=1) "); // end of WHERE clause, ensure proper SQL syntax
//		
//		String orderByColumn = getOrderByColumnName(sort.getSortProperty());
//		hql.append(String.format("order by %s %s", orderByColumn, sort.getSortDirection()));
//		
//		// Create query and populate it with where clause values
//		log.debug("HQL: " + hql.toString());
//		Session session = sessionFactory.getCurrentSession();
//		Query query = session.createQuery(hql.toString());
//		
//		// Plug in the missing date values in the query
//		if (filter.getFrom() != null) {
//			query.setTimestamp("fromDate", filter.getFrom());
//		}
//		if (filter.getTo() != null) {
//			query.setTimestamp("toDate", filter.getTo());
//		}
//		// Invoke the query
//		return query.list();
//	}

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
				return "auditTable.BOOK_NAMES_CONCAT";
			case TITLE_ID:
				return "auditTable.TITLE_ID";
			default:
				throw new IllegalArgumentException("Unexpected sort property: " + sortProperty);
		}
	}

	/**
	 * Delete Spring Batch job records older than the specified date.
	 */
	@Override
	public void deleteJobsBefore(Date deleteJobsDataBefore) {
		Object[] args = { deleteJobsDataBefore };
		int[] argTypes = { Types.TIMESTAMP };
		
		List<Long> jobExecutionIds = jdbcTemplate.queryForList(
				String.format("select JOB_EXECUTION_ID from JOB_EXECUTION where START_TIME < ?"), args, argTypes, Long.class);
		
		if (jobExecutionIds.size() > 0) {
			String csvJobExecutionIds = createCsvString(jobExecutionIds);
			List<Long> jobInstanceIds = jdbcTemplate.queryForList(
					String.format("select unique JOB_INSTANCE_ID from JOB_EXECUTION where START_TIME < ?"), args, argTypes, Long.class);
			String csvJobInstanceIds = createCsvString(jobInstanceIds);
			List<Long> stepExecutionIds = jdbcTemplate.queryForList(
					String.format("select STEP_EXECUTION_ID from STEP_EXECUTION where JOB_EXECUTION_ID in (%s)", csvJobExecutionIds), Long.class);
			String csvStepExecutionIds = createCsvString(stepExecutionIds);
	
			jdbcTemplate.update(String.format("delete from JOB_PARAMS where JOB_INSTANCE_ID in (%s)", csvJobInstanceIds));
			jdbcTemplate.update(String.format("delete from JOB_EXECUTION_CONTEXT where JOB_EXECUTION_ID in (%s)", csvJobExecutionIds));
			jdbcTemplate.update(String.format("delete from STEP_EXECUTION_CONTEXT where STEP_EXECUTION_ID in (%s)", csvStepExecutionIds));
			jdbcTemplate.update(String.format("delete from STEP_EXECUTION where JOB_EXECUTION_ID in (%s)", csvJobExecutionIds));
			jdbcTemplate.update(String.format("delete from JOB_EXECUTION where JOB_EXECUTION_ID in (%s)", csvJobExecutionIds));
			jdbcTemplate.update(String.format("delete from JOB_INSTANCE where JOB_INSTANCE_ID in (%s)", csvJobInstanceIds));
		}
	}
	
	private String createCsvString(List<Long> ids) {
		int index = 0;
		int listSize = ids.size();
		StringBuffer csv = new StringBuffer();
		for (Long id : ids) {
			csv.append(id);
			if (++index < listSize) {
				csv.append(",");
			}
		}
		return csv.toString();
	}
	
	@Required
	public void setJdbcTemplate(JdbcTemplate template) {
		this.jdbcTemplate = template;
	}
}

class JobSummaryRowMapper implements RowMapper<JobSummary> {
	public JobSummary mapRow(ResultSet resultSet, int rowNum) throws SQLException {
		Long bookDefinitionId = resultSet.getLong("EBOOK_DEFINITION_ID");
		String bookName = resultSet.getString("BOOK_NAMES_CONCAT");
		String titleId = resultSet.getString("TITLE_ID");
		Long jobExecutionId = resultSet.getLong("JOB_EXECUTION_ID");
		Long jobInstanceId = resultSet.getLong("JOB_INSTANCE_ID");
		BatchStatus batchStatus = BatchStatus.valueOf(resultSet.getString("STATUS"));
		Date startTime = resultSet.getDate("START_TIME");
		Date endTime = resultSet.getDate("END_TIME");
		JobSummary js = new JobSummary(bookDefinitionId, bookName, titleId, jobInstanceId, jobExecutionId, batchStatus, startTime, endTime);
		return js;
	}
}

class JobExecutionIdRowMapper implements RowMapper<Long> {
	public Long mapRow(ResultSet resultSet, int rowNum) throws SQLException {
		Long id = resultSet.getLong("JOB_EXECUTION_ID");
		return id;
	}
}
