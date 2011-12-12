/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.orchestrate.engine.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.JdbcTemplate;

import com.thomsonreuters.uscl.ereader.orchestrate.core.engine.EngineConstants;
import com.thomsonreuters.uscl.ereader.orchestrate.engine.throttle.ScheduleCurvePoint;
import com.thomsonreuters.uscl.ereader.orchestrate.engine.throttle.ScheduleDayOfWeek;

public class EngineDaoImpl implements EngineDao {
	private static final Logger log = Logger.getLogger(EngineDaoImpl.class);
	
	private JdbcTemplate jdbcTemplate;
	private String tablePrefix;
	
	@Override
	public int getRunningJobExecutionCount() {
		String sql = String.format("select count(*) from %sJOB_EXECUTION where (status = '%s') or (status = '%s')",
								tablePrefix, BatchStatus.STARTING.toString(), BatchStatus.STARTED.toString());
		int count = jdbcTemplate.queryForInt(sql);
		return count;
	}
	
	@Override
	public List<ScheduleCurvePoint> findAllThrottleScheduleCurvePoints() {
// TODO: implement this		
		return new ArrayList<ScheduleCurvePoint>(0);	// TODO: implement this
	}
	
	public List<ScheduleDayOfWeek> findDayOfWeekSchedule() {
// TODO: implement this		
		return new ArrayList<ScheduleDayOfWeek>(0);		// TODO: implement this
	}
	
public static final String STUB_BOOK_TITLE = "TODO: DAO Stub book title - " + System.currentTimeMillis();
	public JobParameters loadJobParameters(String jobName) {
// TODO: implement this
		HashMap<String,JobParameter> map = new HashMap<String,JobParameter>();
		map.put(EngineConstants.JOB_PARAM_BOOK_TITLE, new JobParameter(STUB_BOOK_TITLE));
		JobParameters jobParameters = new JobParameters(map);
		return jobParameters;		// TODO: implement this
	}
	
	@Required
	public void setJdbcTemplate(JdbcTemplate template) {
		this.jdbcTemplate = template;
	}
	@Required
	public void setTablePrefix(String prefix) {
		this.tablePrefix = prefix;
	}
}

