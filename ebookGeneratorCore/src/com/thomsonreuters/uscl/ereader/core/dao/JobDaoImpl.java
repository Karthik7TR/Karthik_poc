package com.thomsonreuters.uscl.ereader.core.dao;

import java.sql.Types;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.JdbcTemplate;

import com.thomsonreuters.uscl.ereader.core.domain.JobExecutionEntity;
import com.thomsonreuters.uscl.ereader.core.domain.JobFilter;

public class JobDaoImpl implements JobDao {
	
	private static final Logger log = Logger.getLogger(JobDaoImpl.class);
//	private static final String PARAMETER_TABLE = "parameter";
//	private static final String EXECUTION_TABLE = "execution";
	private static final int MAX_RESULTS = 3000;
	private SessionFactory sessionFactory;
	private JdbcTemplate jdbcTemplate;

	
	@Override
	@SuppressWarnings("unchecked")
	public List<Long> findJobExecutionIds(JobFilter jobFilter) {
		StringBuffer hql = new StringBuffer("select execution.jobExecutionId from JobExecutionEntity execution where ");
		if (jobFilter.getFrom() != null) {
			hql.append("(execution.startTime > :fromDate) and ");
		}
		if (jobFilter.getTo() != null) {
			hql.append("(execution.startTime < :toDate) and ");
		}
		if (jobFilter.getStatus() != null) {
			hql.append(String.format("(execution.status = '%s') and ", jobFilter.getStatus().toString()));
		}
		
		hql.append("(1=1) "); // to ensure proper SQL syntax
log.debug(hql);

		// Create query and populate it with where clause values
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery(hql.toString());
		if (jobFilter.getFrom() != null) {
			query.setDate(":fromDate", jobFilter.getFrom());
		}
		if (jobFilter.getTo() != null) {
			query.setDate(":toDate", jobFilter.getTo());
		}
		
		query.setMaxResults(MAX_RESULTS);
		
		// Invoke the query to get a list of job execution ID's
		return query.list();
	}

//	@Override
//	@SuppressWarnings("unchecked")
//	public List<JobExecutionEntity> findJobExecutions(JobFilter jobFilter) {
//		Session session = sessionFactory.getCurrentSession();
//		Criteria criteria = session.createCriteria(JobExecutionEntity.class);
//		if (jobFilter.getStatus() != null) { 
//			criteria.add(Restrictions.eq("batchStatus", jobFilter.getStatus()));
//		}
//		if (jobFilter.getFrom() != null) {
//			criteria.add(Restrictions.ge("startTime", jobFilter.getFrom()));
//		}
//		if (jobFilter.getTo() != null) {
//			criteria.add(Restrictions.lt("endTime", jobFilter.getTo()));
//		}
//		List<JobExecutionEntity> executions = criteria.list();
//log.debug("critera executions.size="+executions.size());		
//
//		return executions;
//	}

	/**
	 * Populate the book definition properties that are associated with this give job run.
	 * @param executions a list of job executions to populate
	 */
//	private void populateBookProperties(List<JobExecutionEntity> executions) {
//		for (JobExecutionEntity execution : executions) {
//			Long jobInstanceId = execution.getJobInstanceId();
//			String titleId = findStringJobParameter(jobInstanceId, JobParameterKey.TITLE_ID_FULLY_QUALIFIED);
//			String bookName = findStringJobParameter(jobInstanceId, JobParameterKey.BOOK_NAME);
//log.debug("found: " + titleId + "/"+bookName);
//			execution.setTitleId(titleId);
//			execution.setBookname(bookName);
//		}
//	}
	
//	private String findStringJobParameter(Long jobInstanceId, String parameterKeyName) {
//		String sql = String.format("select STRING_VAL from BATCH_JOB_PARAMS where (JOB_INSTANCE_ID = %d) and (TYPE_CD = 'STRING') and (KEY_NAME = '%s')",
//									jobInstanceId, parameterKeyName);
////log.debug(sql);		
//		String value = null;
//		try {
//			value = jdbcTemplate.queryForObject(sql, String.class);
//		} catch (DataAccessException e) {
//			value = null;
//		}
//		return value;
//	}
	
	/**
	 * Strip out of the list all objects that do not match the specified filter.
	 */
//	private List<JobExecutionEntity> applyParameterFilter(List<JobExecutionEntity> executions, JobParameterFilter paramFilter) {
//		List<JobExecutionEntity> filteredExecutions = new ArrayList<JobExecutionEntity>();
//		String titleId = paramFilter.getTitleId();
//log.debug("filter titleId="+titleId);	
//		for (JobExecutionEntity execution : executions) {
//			if (StringUtils.isNotBlank(titleId)) {
//				// Statement to determines if the execution object matches the filter
//				if (titleId.equals(execution.getTitleId())) {
//					filteredExecutions.add(execution);
//				}
//			}
//		}
//		return filteredExecutions;
//	}


	
//	private String createStringParamFilterClause(String paramKey, String paramValue) {
//		StringBuffer clause = new StringBuffer();
//		clause.append(String.format("(%s.%s = '%s') and ", PARAMETER_TABLE, JobParameterEntity.KEY_NAME_PROPERTY_NAME, paramKey));
//		clause.append(String.format("(%s.%s = '%s') and ", PARAMETER_TABLE, JobParameterEntity.STRING_VAL_PROPERTY_NAME, paramValue));
//		return clause.toString();
//	}

	/**
	 * Delete records older than the specified date.
	 */
	@Override
	public void deleteJobsBefore(Date jobsBefore) {
		Object[] args = { jobsBefore };
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

