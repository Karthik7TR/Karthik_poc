package com.thomsonreuters.uscl.ereader.orchestrate.dashboard.dao;

import java.sql.Types;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.batch.core.BatchStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component("dashboardDao")
public class DashboardDaoImpl implements DashboardDao {
	private static final Logger log = Logger.getLogger(DashboardDaoImpl.class);
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Override
	public List<Long> findJobExecutionIds(String jobName, Date startTime, BatchStatus batchStatus) {
		Object[] args;
		int[] argTypes;
		
		StringBuffer sql = new StringBuffer();
		sql.append("select bje.JOB_EXECUTION_ID from BATCH_JOB_EXECUTION bje ");
		if (StringUtils.isNotBlank(jobName)) {
			sql.append(", BATCH_JOB_INSTANCE bji ");
		}
		
		sql.append("where (START_TIME > ?) ");
		
		if (batchStatus != null) {
			sql.append("and (STATUS = ");
			sql.append("'" + batchStatus.toString() + "') ");
		}
		
		if (StringUtils.isNotBlank(jobName)) {
			sql.append("and (bji.JOB_NAME = ?) and (bje.JOB_INSTANCE_ID = bji.JOB_INSTANCE_ID) ");
			args = new Object[2]; args[0] = startTime; args[1] = jobName;
			argTypes = new int[2]; argTypes[0] = Types.TIMESTAMP; argTypes[1] = Types.VARCHAR;
		} else {
			args = new Object[1]; args[0] = startTime;
			argTypes = new int[1]; argTypes[0] = Types.TIMESTAMP;
		}
	
		sql.append("order by START_TIME desc");
		
log.debug(sql);		
		List<Long> list = jdbcTemplate.queryForList(sql.toString(), args, argTypes, Long.class);
		return list;
	}

	/**
	 * Delete records older than the specifed date.
	 */
	@Override
	public void deleteJobsBefore(Date jobsBefore) {
		Object[] args = { jobsBefore };
		int[] argTypes = { Types.TIMESTAMP };
		
		List<Long> jobExecutionIds = jdbcTemplate.queryForList(
				"select JOB_EXECUTION_ID from BATCH_JOB_EXECUTION where START_TIME < ?", args, argTypes, Long.class);
		
		if (jobExecutionIds.size() > 0) {
			String csvJobExecutionIds = createCsvString(jobExecutionIds);
			List<Long> jobInstanceIds = jdbcTemplate.queryForList(
					"select unique JOB_INSTANCE_ID from BATCH_JOB_EXECUTION where START_TIME < ?", args, argTypes, Long.class);
			String csvJobInstanceIds = createCsvString(jobInstanceIds);
			List<Long> stepExecutionIds = jdbcTemplate.queryForList(
					"select STEP_EXECUTION_ID from BATCH_STEP_EXECUTION where JOB_EXECUTION_ID in (" + csvJobExecutionIds + ")", Long.class);
			String csvStepExecutionIds = createCsvString(stepExecutionIds);
	
			jdbcTemplate.update("delete from BATCH_JOB_PARAMS where JOB_INSTANCE_ID in (" + csvJobInstanceIds + ")");
			jdbcTemplate.update("delete from BATCH_JOB_EXECUTION_CONTEXT where JOB_EXECUTION_ID in (" + csvJobExecutionIds + ")");
			jdbcTemplate.update("delete from BATCH_STEP_EXECUTION_CONTEXT where STEP_EXECUTION_ID in (" + csvStepExecutionIds + ")");
			jdbcTemplate.update("delete from BATCH_STEP_EXECUTION where JOB_EXECUTION_ID in (" + csvJobExecutionIds + ")");
			jdbcTemplate.update("delete from BATCH_JOB_EXECUTION where JOB_EXECUTION_ID in (" + csvJobExecutionIds + ")");
			jdbcTemplate.update("delete from BATCH_JOB_INSTANCE where JOB_INSTANCE_ID in (" + csvJobInstanceIds + ")");
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
}

