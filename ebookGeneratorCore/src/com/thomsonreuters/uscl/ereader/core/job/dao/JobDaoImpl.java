package com.thomsonreuters.uscl.ereader.core.job.dao;

import java.sql.Types;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.thomsonreuters.uscl.ereader.core.job.domain.JobFilter;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobSort;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobSort.SortProperty;

public class JobDaoImpl implements JobDao {
	
	private static final Logger log = Logger.getLogger(JobDaoImpl.class);
	private SessionFactory sessionFactory;
	private JdbcTemplate jdbcTemplate;
	
	@Override
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Long> findJobExecutions(JobFilter filter, JobSort sort) {
		
		StringBuffer hql = new StringBuffer("select execution.jobExecutionId from ");
		hql.append("JobExecutionEntity execution ");
		if (filter.hasAnyBookProperties() || sort.isSortingOnBookProperty()) {
			hql.append(", PublishingStats stats, EbookAudit audit ");
		}
		hql.append("where ");
		if (filter.getFrom() != null) {
			hql.append("(execution.startTime > :fromDate) and ");
		}
		if (filter.getTo() != null) {
			hql.append("(execution.startTime <= :toDate) and ");
		}
		if (filter.getBatchStatus() != null) {
			hql.append(String.format("(execution.batchStatus = '%s') and ", filter.getBatchStatus().toString()));
		}
		if (filter.hasAnyBookProperties() || sort.isSortingOnBookProperty()) {
			hql.append("(execution.jobInstanceId = stats.jobInstanceId) and ");
			hql.append("(stats.auditId = audit.auditId) and ");
			
			if (StringUtils.isNotBlank(filter.getBookName())) {
				hql.append(String.format("(audit.bookNamesConcat like '%%%s%%') and ", filter.getBookName()));
			}
			if (StringUtils.isNotBlank(filter.getTitleId())) {
				hql.append(String.format("(audit.titleId like '%%%s%%') and ", filter.getTitleId()));
			}
		}
		hql.append("(1=1) "); // end of WHERE clause, ensure proper SQL syntax
		
		String orderByColumn = getOrderByColumnName(sort.getSortProperty());
		hql.append(String.format("order by %s %s", orderByColumn, sort.getSortDirection()));
		
		// Create query and populate it with where clause values
		log.debug("HQL: " + hql.toString());
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery(hql.toString());
		
		// Plug in the missing date values in the query
		if (filter.getFrom() != null) {
			query.setTimestamp("fromDate", filter.getFrom());
		}
		if (filter.getTo() != null) {
			query.setTimestamp("toDate", filter.getTo());
		}
		// Invoke the query
		return query.list();
	}

	/**
	 * Map the sort column enumeration into the actual column identifier used in the HQL query.
	 * @param sortProperty enumerated value that reflects the database table sort column to sort on.
	 */
	private String getOrderByColumnName(SortProperty sortProperty) {
		switch (sortProperty) {
			case JOB_EXECUTION_ID:
				return "execution.jobExecutionId";
			case JOB_INSTANCE_ID:
				return "execution.jobInstanceId";
			case BATCH_STATUS:
				return "execution.batchStatus";
			case START_TIME:
				return "execution.startTime";
			case BOOK_NAME:
				return "audit.bookNamesConcat";
			case TITLE_ID:
				return "audit.titleId";
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
	public void setSessionFactory(SessionFactory sessionfactory) {
		this.sessionFactory = sessionfactory;
	}
	@Required
	public void setJdbcTemplate(JdbcTemplate template) {
		this.jdbcTemplate = template;
	}
}

