package com.thomsonreuters.uscl.ereader.orchestrate.engine.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.thomsonreuters.uscl.ereader.orchestrate.engine.throttle.ScheduleCurvePoint;
import com.thomsonreuters.uscl.ereader.orchestrate.engine.throttle.ScheduleDayOfWeek;

@Component
public class EngineDaoImpl implements EngineDao {
	private static final Logger log = Logger.getLogger(EngineDaoImpl.class);
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Override
	public int getRunningJobExecutionCount() {
		String sql = String.format("select count(*) from BATCH_JOB_EXECUTION where (status = '%s') or (status = '%s')",
								BatchStatus.STARTING.toString(), BatchStatus.STARTED.toString());
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
	
	public JobParameters loadJobParameters(String jobName) {
// TODO: implement this
		return new JobParameters();		// TODO: implement this
	}
}

